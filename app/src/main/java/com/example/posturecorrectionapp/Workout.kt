package com.example.posturecorrectionapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.posturecorrectionapp.models.CameraViewModel
import com.example.posturecorrectionapp.screens.Camera
import com.example.posturecorrectionapp.screens.ExerciseCompleteActivity
import com.example.posturecorrectionapp.screens.NavigationActivity
import com.example.posturecorrectionapp.utils.ExerciseLogicUtils
import com.example.posturecorrectionapp.utils.TextToSpeechUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.lang.Thread.sleep
import kotlin.properties.Delegates

class Workout : AppCompatActivity() {
    //Page Related
    private val viewModel : CameraViewModel by lazy {
        ViewModelProvider(this)[CameraViewModel::class.java]
    }
    private lateinit var timer: CountDownTimer
    private var isTimerRunning = false
    private var duration by Delegates.notNull<Int>()

    //Exercise Related
    private lateinit var workoutTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var repetitionTextView: TextView
    private lateinit var timerView: TextView
    private lateinit var startPauseButton : Button
    private lateinit var progressIndicator: CircularProgressIndicator

    //Data Related
    private var workoutRoutine = ArrayList<Map<String,String>>()
    private var currentIndex = 0

    private lateinit var ttsUtil: TextToSpeechUtils
    private lateinit var fragment: Camera

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 1){
            finish()
        }
        else if (result.resultCode == 2){
            restart()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Workout", "onCreate: ")

        //Read the workout routine from the intent
        workoutRoutine = intent.getSerializableExtra("workoutRoutine") as ArrayList<Map<String, String>>

        // if the workout routine is empty, go back to the navigation page
        if (workoutRoutine.isEmpty()){
            finish()
        }

        //Get list of exercises names from the workout routine
        val exerciseList = workoutRoutine.map { it["name"] }.toList()

        //Hide status bar
        window.decorView.windowInsetsController?.hide(android.view.WindowInsets.Type.statusBars())
        //Hide title bar
        supportActionBar?.hide()
        //Keep screen on
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_workout)

        fragment = supportFragmentManager.findFragmentById(R.id.cameraPreview) as Camera

        //Start TTS
        startTTS(this)

        // Initialise the UI
        workoutTextView = findViewById(R.id.WorkoutText)
        feedbackTextView = findViewById(R.id.feedbackText)
        repetitionTextView = findViewById(R.id.repetitionText)
        timerView = findViewById(R.id.timer)
        startPauseButton = findViewById(R.id.startButton)
        progressIndicator = findViewById(R.id.progressBar)

        // Invoke Function to set the update the exercise
        updateExercise(currentIndex)

        duration = workoutRoutine[currentIndex]["duration"]!!.toInt()*1000
        timerView.setText("%02d:%02d".format(duration / 1000 / 60, duration / 1000 % 60))
        // Update the progress bar
        progressIndicator.max = duration
        progressIndicator.progress = 0

        // Use the ExerciseLogicUtils to get the exercise logic
        val exerciseUtil = ExerciseLogicUtils()
        viewModel.setExerciseLogic(exerciseUtil.readAllExercise(assets.open("exercise_rule.csv"),exerciseList))

        viewModel.getCurrentExercise().observe(this) { data -> // Update the UI
            workoutTextView.text = data
            Log.d("Exercise Change", "New workout $data")
        }

        viewModel.getCurrentFeedback().observe(this) { data ->
            if (data != null) {
                updateFeedBack(data)
            }
            Log.d("Feedback", "$data")
        }

        viewModel.getCurrentScore().observe(this) { data ->
            if (data != null) {
                repetitionTextView.text = "Count: $data"
                if (data > 1) {
                    ttsUtil.speak("$data")
                }
            }
            Log.d("Score Changed", "$data")
        }


        // Set the onClickListener for the startPauseButton
        startPauseButton.setOnClickListener {
            Log.d("Workout", "exercieState: ${viewModel.getExerciseState().value}")
            // Check view model state for workout
            when (viewModel.getExerciseState().value) {
                null -> {
                    //Start workout
                    startWorkout()
                }
                "pause" -> {
                    //Resume workout
                    resumeWorkout()
                }
                "start" -> {
                    //Pause workout
                    pauseWorkout()
                }
            }

        }
    }

    private fun goToComplete() {
        val intent = Intent(this, ExerciseCompleteActivity::class.java)
        intent.putExtra("numberOfExercisesCompleted", workoutRoutine.size.toString())
        val duration = workoutRoutine.map { it["duration"] }.reduce { acc, s -> (acc!!.toInt() + s!!.toInt()).toString() }
        intent.putExtra("duration", duration)
        Log.d("duration", duration?:"null")
        Log.d("workoutRoutine", workoutRoutine.size.toString())
        getResult.launch(intent)
    }

    private fun restart() {
        val intent = Intent(this, Workout::class.java)
        intent.putExtra("workoutRoutine", workoutRoutine)
        startActivity(intent)
        finish()
    }


    fun updateExercise(index : Int){
        // Update the current exercise
        viewModel.setCurrentExercise(workoutRoutine[index]["name"]!!)
        viewModel.setCurrentRepetition(0)
        viewModel.setCurrentScore(0)
        viewModel.setCurrentFeedback("")
    }

    fun updateFeedBack(feedback : String){
        feedbackTextView.text = feedback
        ttsUtil.speak(feedback)
    }

    fun startWorkout(){
        // update the index
        currentIndex = 0
        // Update the exercise
        updateExercise(currentIndex)
        // Get duration from the workoutRoutine
        duration = workoutRoutine[currentIndex]["duration"]!!.toInt()*1000
        timerView.setText("%02d:%02d".format(duration / 1000 / 60, duration / 1000 % 60))
        // Update the progress bar
        progressIndicator.max = duration
        progressIndicator.progress = 0


        //Clear the camera's surface view
        fragment.clearView()

        // Initialize the timer
        timer = object : CountDownTimer(duration.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                duration -= 100
                // Update the view
                val seconds = duration / 1000
                val displayMinute = seconds / 60
                val displaySecond = seconds % 60
                timerView.setText("%02d:%02d".format(displayMinute, displaySecond))
                // Update the progress bar
                progressIndicator.progress = duration
            }
            override fun onFinish() {
                triggerNextExercise()
            }
        }

        // Update the button
        startPauseButton.text = "Pause"
        // Update the flag
        isTimerRunning = true
        // Update the viewModel
        viewModel.startExercise()

        ttsUtil.speak("${workoutRoutine[currentIndex]["name"]}")
        sleep(1000)
        timer.start()
    }

    fun triggerNextExercise(){
        // Check if the current exercise is the last exercise
        //Clear the camera's surface view
        fragment.clearView()
        if (currentIndex == workoutRoutine.size - 1) {
            // Workout is completed
            ttsUtil.speakNow("Workout is completed")
            goToComplete()
        } else {
            // Workout is not completed
            // Update the index
            currentIndex += 1
            // Update the exercise
            updateExercise(currentIndex)
            // Get duration from the workoutRoutine
            duration = workoutRoutine[currentIndex]["duration"]!!.toInt()*1000
            timerView.setText("%02d:%02d".format(duration / 1000 / 60, duration / 1000 % 60))
            // Update the progress bar
            progressIndicator.max = duration
            progressIndicator.progress = 0

            // Initialize the timer
            timer = object : CountDownTimer(duration.toLong(), 100) {
                override fun onTick(millisUntilFinished: Long) {
                    duration -= 100
                    // Update the view
                    val seconds = duration / 1000
                    val displayMinute = seconds / 60
                    val displaySecond = seconds % 60
                    timerView.setText("%02d:%02d".format(displayMinute, displaySecond))
                    // Update the progress bar
                    progressIndicator.progress = duration
                }
                override fun onFinish() {
                    triggerNextExercise()
                }
            }

            // Update the button
            startPauseButton.text = "Pause"
            // Update the flag
            isTimerRunning = true
            // Update the viewModel
            viewModel.startExercise()

            ttsUtil.speakNow("${workoutRoutine[currentIndex]["name"]}")
            sleep(1000)
            timer.start()
        }
    }

    fun pauseWorkout(){
        // Pause the timer
        timer.cancel()
        //Clear the camera's surface view
        fragment.clearView()
        // Update the button
        startPauseButton.text = "Resume"
        // Update the flag
        isTimerRunning = false
        // Update the viewModel
        viewModel.pauseExercise()
//        sleep(500)
        ttsUtil.speakNow("Workout Paused")
    }

    fun resumeWorkout(){
        // Resume the timer with the remaining time
        timer = object : CountDownTimer(duration.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                duration -= 100
                // Update the view
                val seconds = duration / 1000
                val displayMinute = seconds / 60
                val displaySecond = seconds % 60
                timerView.setText("%02d:%02d".format(displayMinute, displaySecond))
                // Update the progress bar
                progressIndicator.progress = duration
            }
            override fun onFinish() {
                triggerNextExercise()
            }
        }
        // Update the button
        startPauseButton.text = "Pause"
        // Update the flag
        isTimerRunning = true
        // Update the viewModel
        viewModel.startExercise()
        // Delay timer for 2 seconds
        ttsUtil.speakNow("${workoutRoutine[currentIndex]["name"]}")
        sleep(1000)
        timer.start()
    }

    private fun startTTS(context: Context) {
        ttsUtil = TextToSpeechUtils()
        ttsUtil.init(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsUtil.destroy()
    }

}