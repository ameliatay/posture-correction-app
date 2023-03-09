package com.example.posturecorrectionapp.screens

import android.Manifest
import android.graphics.*
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.data.Device
import com.example.posturecorrectionapp.data.Person
import com.example.posturecorrectionapp.ml.ModelType
import com.example.posturecorrectionapp.ml.MoveNet
import com.example.posturecorrectionapp.utils.AngleCheckingUtils
import com.example.posturecorrectionapp.utils.VisualizationUtils
import com.example.posturecorrectionapp.utils.YuvToRgbConverter
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.*
import java.util.concurrent.Executors

class Camera : Fragment() {

    companion object {
        val TAG = "CameraXFragment"
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

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var detector: MoveNet? = null
    private val lock = Any()

    private lateinit var safeContext: Context
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private lateinit var v: View
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_camera, container, false)
        return v
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)
        val cameraToggle = v.findViewById<Button>(R.id.switchCameraButton)
        var cameraFacing = cameraToggle!!.text == "Front Camera"
        preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (cameraFacing!!) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        v.findViewById<Button>(R.id.image_capture_button)?.setOnClickListener { takePhoto() }
        v.findViewById<Button>(R.id.switchCameraButton)?.setOnClickListener { switchCamera() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        //Set up GIF ImageView
        Glide.with(this)
            .asGif()
            .load(R.raw.tree_pose)
            .into(v.findViewById<ImageView>(R.id.imageView))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            safeContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(safeContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera() {

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // ImageAnalysis (Pose Estimation)
            val imageAnalysis = ImageAnalysis.Builder()
//                .setTargetResolution(Size(width, height)
                .setTargetResolution(Size(640, 480))
                .setTargetRotation(v.display.rotation)
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

                Log.d(TAG, "TRYYY ${v.findViewById<PreviewView>(R.id.previewView).surfaceProvider}")
                preview!!.setSurfaceProvider(v.findViewById<PreviewView>(R.id.previewView).surfaceProvider)

            } catch(exc: Exception) {
                exc.printStackTrace()
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this.requireContext()))
        createPoseEstimator()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(safeContext), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    }

    fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun switchCamera() {
        val cameraToggle = v.findViewById<Button>(R.id.switchCameraButton)

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
        detector = MoveNet.create(safeContext, Device.CPU, ModelType.Lightning)
    }

    // Visualisation
    private fun visualize(persons: List<Person>, bitmap: Bitmap) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            persons.filter { it.score > 0.01 }
        )

        val surfaceView = v.findViewById<ImageView>(R.id.surfaceView)

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

        activity?.runOnUiThread(Runnable {
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
            Toast.makeText(safeContext, "Good Posture", Toast.LENGTH_SHORT).show()
        }
    }

    //Process Image
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processImage(imageProxy:ImageProxy, bitmap: Bitmap) {

        //Convert to RGB
        val yuvToRgbConverter = YuvToRgbConverter(safeContext)
        yuvToRgbConverter.yuvToRgb(imageProxy.image!!, bitmap)

        val rotateMatrix = Matrix()
        // Get Orientation of the camera
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        // Rotate the image
        rotateMatrix.postRotate(rotationDegrees.toFloat())
//        rotateMatrix.postRotate(-90f)


        // if front camera
        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            rotateMatrix.postScale(-1f, 1f)
            // increase the size of the image
            rotateMatrix.postTranslate(bitmap.width.toFloat(), 0f)
        } else {
            rotateMatrix.postScale(1f, 1f)
        }

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
        //Run on UI Thread

        activity?.runOnUiThread(Runnable {
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