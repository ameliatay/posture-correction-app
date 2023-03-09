package com.example.posturecorrectionapp

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraDevice
import android.media.ImageReader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.bumptech.glide.Glide
import com.example.posturecorrectionapp.data.Device
import com.example.posturecorrectionapp.data.Person
import com.example.posturecorrectionapp.databinding.ActivityCameraPreviewBinding
import com.example.posturecorrectionapp.ml.ModelType
import com.example.posturecorrectionapp.ml.MoveNet
import com.example.posturecorrectionapp.utils.AngleCheckingUtils
import com.example.posturecorrectionapp.utils.VisualizationUtils
import com.example.posturecorrectionapp.utils.YuvToRgbConverter
import com.google.common.util.concurrent.ListenableFuture
import org.checkerframework.checker.guieffect.qual.UI
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraPreviewActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraPreviewBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector

    private var detector: MoveNet? = null
    private val lock = Any()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        val cameraToggle = findViewById<Button>(R.id.switchCameraButton)
        var cameraFacing = cameraToggle.text == "Front Camera"

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (cameraFacing!!) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()

        //Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        //Set up listeners
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }
        findViewById<Button>(R.id.switchCameraButton).setOnClickListener { switchCamera() }


        cameraExecutor = Executors.newSingleThreadExecutor()

        //Set up GIF ImageView
        Glide.with(this)
            .asGif()
            .load(R.raw.tree_pose)
            .into(findViewById<ImageView>(R.id.imageView))


    }

    private fun takePhoto() {
        Log.d("CameraPreviewActivity", "takePhoto: ")

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {
        viewBinding.videoCaptureButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture?.output!!
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraPreviewActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera() {

        cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also{it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)}

                // ImageCapture
                imageCapture = ImageCapture.Builder().build()

                // VideoCapture
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HD))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)


                // Get ImageView dimensions
                val display = windowManager.currentWindowMetrics.bounds
                val width = display.width()+300
                val height = display.height()

                // ImageAnalysis (Pose Estimation)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(width, height))
                    .setTargetRotation(viewBinding.viewFinder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, PoseAnalyzer())
                    }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageCapture,imageAnalysis)

            } catch(exc: Exception) {
                exc.printStackTrace()
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
        createPoseEstimator()

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun switchCamera() {
        val cameraToggle = findViewById<Button>(R.id.switchCameraButton)

        //check if state is the same as current camera
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()

        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraToggle.text = "Back Camera"
        } else {
            cameraToggle.text = "Front Camera"
        }
    }

    //Create Pose Estimator Movenet Lightning
    private fun createPoseEstimator(){
        detector = MoveNet.create(this, Device.CPU, ModelType.Lightning)
    }

    // Visualisation
    private fun visualize(persons: List<Person>, bitmap: Bitmap) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            persons.filter { it.score > 0.01 }
        )

        val surfaceView = findViewById<ImageView>(R.id.surfaceView)

        val canvas = Canvas(outputBitmap)

        val screenWidth: Int
        val screenHeight: Int
        val left: Int
        val top: Int

        if (canvas.height > canvas.width) {
            val ratio = outputBitmap.height.toFloat() / outputBitmap.width
            screenWidth = canvas.width
            left = 0
            screenHeight = (canvas.width * ratio).toInt()
            top = (canvas.height - screenHeight) / 2
        } else {
            val ratio = outputBitmap.width.toFloat() / outputBitmap.height
            screenHeight = canvas.height
            top = 0
            screenWidth = (canvas.height * ratio).toInt()
            left = (canvas.width - screenWidth) / 2
        }
        val right: Int = left + screenWidth
        val bottom: Int = top + screenHeight

        canvas.drawBitmap(
            outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
            Rect(left, top, right, bottom), null
        )

        runOnUiThread(Runnable {
            surfaceView.setImageBitmap(outputBitmap)
        })
    }

    // Angle Checking Feedback
    private fun feedback(person: List<Person>){
        // get people with score > 0.01
        val persons = person.filter { it.score > 0.01 }

        // get the first person
        val person = persons[0]

        //Perform Angle Check
        val angleCheck = AngleCheckingUtils.checkAngle(person)

        //Get Feedback
        if (angleCheck){
            Toast.makeText(this, "Good Posture", Toast.LENGTH_SHORT).show()
        }
    }

    //Process Image
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processImage(imageProxy:ImageProxy, bitmap: Bitmap) {

        //Convert to RGB
        val yuvToRgbConverter = YuvToRgbConverter(this)
        yuvToRgbConverter.yuvToRgb(imageProxy.image!!, bitmap)

        val rotateMatrix = Matrix()
        rotateMatrix.postRotate(-90f)
        rotateMatrix.postScale(-1.5f, 1.5f)

        val rotatedBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            rotateMatrix,
            true
        )

        val persons = mutableListOf<Person>()

        synchronized(lock) {
            detector?.estimatePoses(rotatedBitmap)?.let {
                for (person in it) {
                    if (person.score > 0.01) {
                        persons.add(person)
                    }
                }
                if (persons.size > 0) {
                    Log.d(TAG, "persons: ${persons.get(0).score}")
                    Log.d(TAG, "persons: ${persons.get(0).keyPoints}")
                }
            }
        }
        Log.d("Visualise","${persons.size} persons found ${rotatedBitmap.width} x ${rotatedBitmap.height}")
        visualize(persons, rotatedBitmap)
        // check angle of pose -AngleCheckingUtils.checkAngle(persons)
        //Run on UI Thread

        runOnUiThread(Runnable {
            //Update UI
            feedback(persons)
        })
    }

    // Class for Pose Estimation
    private inner class PoseAnalyzer() : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {

            //Run Pose Estimation
            val bitmap = Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
//            Log.d(TAG, "imageProxy: ${imageProxy.width} x ${imageProxy.height}")

            processImage(imageProxy, bitmap)
            imageProxy.close()

        }

    }

}