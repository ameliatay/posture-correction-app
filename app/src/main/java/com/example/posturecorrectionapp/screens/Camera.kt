package com.example.posturecorrectionapp.screens

import android.Manifest
import android.graphics.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.data.Device
import com.example.posturecorrectionapp.data.Exercise
import com.example.posturecorrectionapp.data.Person
import com.example.posturecorrectionapp.data.PoseRule
import com.example.posturecorrectionapp.ml.ModelType
import com.example.posturecorrectionapp.ml.MoveNet
import com.example.posturecorrectionapp.ml.PoseClassifier
import com.example.posturecorrectionapp.models.CameraViewModel
import com.example.posturecorrectionapp.models.ExerciseRule
import com.example.posturecorrectionapp.utils.*
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.HashMap

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
    private var classifier: PoseClassifier? = null
    private val lock = Any()
    private var cameraFacing = false
    private val exerciseLogicUtils = ExerciseLogicUtils()

    private lateinit var cameraToggle: Button
    private lateinit var safeContext: Context
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private lateinit var v: View
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var exercise: ExerciseRule

    // Use ViewModel by Workout Activity
    private lateinit var cameraViewModel: CameraViewModel

    private lateinit var ttsUtil: TextToSpeechUtils

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

        // Use ViewModel by Workout Activity
        cameraViewModel = ViewModelProvider(requireActivity())[CameraViewModel::class.java]

        cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)
        cameraToggle = v.findViewById<Button>(R.id.switchCameraButton)
        preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (!cameraFacing!!) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera(safeContext)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

//        v.findViewById<Button>(R.id.image_capture_button)?.setOnClickListener { takePhoto() }
        v.findViewById<Button>(R.id.switchCameraButton)?.setOnClickListener { switchCamera() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        //find resource id based on exercise name from ViewModel

        //Set up a listener to observe for changes in the ViewModel

        //and update the GIF accordingly
        cameraViewModel.getCurrentExercise().observe(viewLifecycleOwner) {
            Log.d("Camera", "Exercise: $it")
            Log.d("Camera", "Package Name: ${resources.getIdentifier(it, "drawable", safeContext.applicationContext.packageName).toInt()}")
            val resourceId = resources.getIdentifier(it, "drawable", safeContext.applicationContext.packageName).toInt()
            if (resourceId == 0) {
                Log.d("Camera", "Resource ID not found")
            }else{
                // get the resource path from the resource id
                //Set up GIF ImageView
                Glide.with(this)
                    .asGif()
                    .load(resourceId)
                    .into(v.findViewById<ImageView>(R.id.imageView))

            }
            //Retrieve the exerciseMap from the ViewModel
            val exerciseMap = cameraViewModel.getExerciseLogic()
            Log.d("Camera", "Exercise Map: $exerciseMap")
            //Retrieve the current exercise from the ViewModel
            val currentExercise = cameraViewModel.getCurrentExercise().value

            exercise = if (exerciseMap != null && currentExercise != null && exerciseMap.containsKey(currentExercise)) {
                //Retrieve the exercise from the exerciseMap
                exerciseMap[currentExercise]!!
            }else{
                //create empty map
                val map = HashMap<String, List<PoseRule>>().toMap()
                ExerciseRule("","",map)
            }
            Log.d("Camera", "Exercise: ${exercise.toString()}")
        }



        //Observe for changes in the ViewModel
        cameraViewModel.getIsTimerRunning().observe(viewLifecycleOwner) {
            if (it) {
                // Start the inference
                v.findViewById<ImageView>(R.id.surfaceView).visibility = View.VISIBLE
                v.findViewById<PreviewView>(R.id.previewView).visibility = View.INVISIBLE
                v.findViewById<Button>(R.id.switchCameraButton).visibility = View.INVISIBLE
                startInference()
            } else {
                v.findViewById<ImageView>(R.id.surfaceView).visibility = View.INVISIBLE
                v.findViewById<PreviewView>(R.id.previewView).visibility = View.VISIBLE
                v.findViewById<Button>(R.id.switchCameraButton).visibility = View.VISIBLE
                startCamera(safeContext)
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            safeContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(safeContext)
            } else {
                Toast.makeText(safeContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera(context: Context) {
        startTTS(context)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageCapture)

                Log.d(TAG, "TRYYY ${v.findViewById<PreviewView>(R.id.previewView).surfaceProvider}")
                preview!!.setSurfaceProvider(v.findViewById<PreviewView>(R.id.previewView).surfaceProvider)

            } catch(exc: Exception) {
                exc.printStackTrace()
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this.requireContext()))
    }


    private fun startInference() {

        val surfaceView = v.findViewById<ImageView>(R.id.surfaceView)
        //Based on landscape mode or portrait mode, set the size of image based on width or height of surfaceView
        val size = if (surfaceView.width > surfaceView.height) {
            Size(surfaceView.width, surfaceView.height)
        } else {
            Size(surfaceView.height, surfaceView.width)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(size)
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
        ContextCompat.getMainExecutor(this.requireContext())
        createPoseEstimator()
        createPoseClassifier()
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
        startCamera(safeContext)

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

    // Create Pose Classifier
    private fun createPoseClassifier() {
        //REad labels from file
        classifier = PoseClassifier.create(safeContext)
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

    //Process Image
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processImage(imageProxy:ImageProxy, bitmap: Bitmap) {

        //Convert to RGB
        val yuvToRgbConverter = YuvToRgbConverter(safeContext)
        yuvToRgbConverter.yuvToRgb(imageProxy.image!!, bitmap)

        val rotateMatrix = Matrix()
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        rotateMatrix.postRotate(rotationDegrees.toFloat())

        // Flip the image if front camera
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            rotateMatrix.postScale(1f, 1f)
        } else {
            rotateMatrix.postScale(-1f, 1f)
        }

        // Scale the rotatedMatrix to fit the bitmap
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
        var classificationResult: List<Pair<String,Float>>? = null

        synchronized(lock) {
            detector?.estimatePoses(rotatedBitmap)?.let { it ->
                for (person in it) {
                    if (person.score > 0.01) {
                        persons.add(person)
                    }
                }
                if (persons.size > 0) {
                    Log.d(TAG, "persons: ${persons[0].score}")
                    Log.d(TAG, "persons: ${persons[0].keyPoints}")
                    classifier?.run {
                        var temp = classify(persons[0])
                        //get relevant pose
                        var pose = exerciseLogicUtils.getRelevantPoseFromClassificationModel(temp, exercise)
                        var feedback = ""
                        if (pose != "None") {
                            feedback = exerciseLogicUtils.getFeedbackFromPose(pose, exercise,persons[0])
                        }
                        if (feedback == "Good Job!") {
                            //Update ViewModel with count +1
                            addCount(feedback)
                        }
                        updateClassify(feedback,pose)
                    }
                }
            }
        }
        Log.d("Visualise","${persons.size} persons found ${rotatedBitmap.width} x ${rotatedBitmap.height}")
        // run on background thread
        activity?.runOnUiThread(Runnable {
            visualize(persons, rotatedBitmap)
        })
    }

    private fun addCount(feedback:String) {
        activity?.runOnUiThread(Runnable {
            cameraViewModel.addCount(feedback)
        })
    }

    private fun updateClassify(it1: String, pose: String) {
        Handler(Looper.getMainLooper()).post {
            cameraViewModel.setCurrentPose(pose)
            cameraViewModel.setCurrentFeedback(it1)
        }
    }

    private fun startTTS(context: Context) {
        ttsUtil = TextToSpeechUtils()
        ttsUtil.init(context)
    }

    // Close all resources when fragment is destroyed
    override fun onDestroy() {
        ttsUtil.destroy()
        super.onDestroy()
        detector?.close()
        classifier?.close()
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

            processImage(imageProxy, bitmap)
            imageProxy.close()

        }

    }

}