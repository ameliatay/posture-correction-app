package com.example.posturecorrectionapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.posturecorrectionapp.models.CameraViewModel
import com.example.posturecorrectionapp.screens.Camera
import com.example.posturecorrectionapp.screens.NavigationActivity
import com.example.posturecorrectionapp.utils.ExerciseLogicUtils
import com.example.posturecorrectionapp.utils.TextToSpeechUtils
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
    private lateinit var timerView: EditText
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var startPauseButton : Button

    //Data Related
    private var workoutRoutine = ArrayList<Map<String,String>>()
    private var currentIndex = 0

    private lateinit var ttsUtil: TextToSpeechUtils
    private lateinit var fragment: Camera

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Workout", "onCreate: ")

        //Read the workout routine from the intent
        workoutRoutine = intent.getSerializableExtra("workoutRoutine") as ArrayList<Map<String, String>>

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
        prevButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        startPauseButton = findViewById(R.id.startButton)

        // Invoke Function to set the update the exercise
        updateExercise(currentIndex)

        // Use the ExerciseLogicUtils to get the exercise logic
        val exerciseUtil = ExerciseLogicUtils()
        viewModel.setExerciseLogic(exerciseUtil.readAllExercise(assets.open("exercise_rule.csv"),exerciseList))

        viewModel.getCurrentExercise().observe(this) { data -> // Update the UI
            workoutTextView.text = data
            if (currentIndex !== 0 && !isTimerRunning) {
                ttsUtil.speak(data)
                ttsUtil.speak("Start")
//                startPauseButton.performClick()
                Handler(Looper.getMainLooper()).postDelayed({
                    startPauseButton.performClick()
                }, 3000)
            }

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
                ttsUtil.speak("$data")

            }
            Log.d("Score Changed", "$data")
        }

        viewModel.getCurrentRepetition().observe(this) { t -> Log.d("Repetition Changed", "$t") }

        viewModel.getCurrentTimeLeft().observe(this) { timeLeft ->
            if (timeLeft != null) {
                //Format the time to display from milliseconds to MM:SS
                val seconds = timeLeft / 1000
                val displayMinute = seconds / 60
                val displaySecond = seconds % 60
                timerView.setText("%02d:%02d".format(displayMinute, displaySecond))
                Log.d("Time Left Changed", "${"%02d:%02d".format(displayMinute, displaySecond)}")

                if (timeLeft == 0) {
                    if (currentIndex == workoutRoutine.size - 1) {
                        ttsUtil.speak("Workout is completed")
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 3000)
                    }

                    if (currentIndex < workoutRoutine.size -1 && viewModel.getCurExerciseIndex().value!=currentIndex) {
                        ttsUtil.speak("Time is up. Next")
                        Handler(Looper.getMainLooper()).postDelayed({
                            nextButton.performClick()
                        }, 3000)
                    }

                    if (currentIndex > 0) {
                        prevButton.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Workout", "onSaveInstanceState: ")
        // Check configuration landscape or portrait
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_workout)
            Log.d("Workout", "onSaveInstanceState: landscape" )
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_workout)
            Log.d("Workout", "onSaveInstanceState: potrait" )
        }
    }

    fun updateExercise(index : Int){

        val workoutTime = workoutRoutine[index]["duration"]!!.toInt() *1000

        //Format the time to display from milliseconds to MM:SS
        val seconds = workoutTime / 1000
        val displayMinute = seconds / 60
        val displaySecond = seconds % 60
        timerView.setText("%02d:%02d".format(displayMinute, displaySecond))

        if (currentIndex == 0) {
            prevButton.visibility = View.INVISIBLE
        }else{
            prevButton.visibility = View.VISIBLE
        }

        if (currentIndex == workoutRoutine.size - 1) {
            nextButton.visibility = View.INVISIBLE
        }else{
            nextButton.visibility = View.VISIBLE
        }
        // Update the current exercise
        viewModel.setCurrentExercise(workoutRoutine[index]["name"]!!)
        viewModel.setCurrentRepetition(0)
        viewModel.setCurrentScore(0)
        viewModel.setCurrentFeedback("")
        viewModel.setIsTimerRunning(false)
        isTimerRunning = false
        viewModel.setCurrentTimeLeft(workoutTime)
    }

    fun updateFeedBack(feedback : String){
        // Update the current feedback after 2 seconds, run on a separate thread
        feedbackTextView.text = feedback
        ttsUtil.speak(feedback)
//        Handler(Looper.getMainLooper()).postDelayed({
//            feedbackTextView.text = feedback
//        }, 2000)
    }
    fun goToPrevious(view: View){
        // Update the previous exercise
        if (currentIndex > 0){
            currentIndex -= 1
            updateExercise(currentIndex)
        }
        startPauseButton.text = "Start"
    }

    fun goToNext(view: View){
        // Update the next exercise
        if (currentIndex < workoutRoutine.size - 1){
            currentIndex += 1
            updateExercise(currentIndex)
        }
        startPauseButton.text = "Start"
    }

    fun startPause(view: View){
        Log.d("Timer", "startPause:Clicked")
        // Check if timer is running
        if (!isTimerRunning){
            // Start timer
            Log.d("Timer", "startPause: start timer")
            startTimer()
            startPauseButton.text = "Pause"
            fragment.updateView()

        } else {
            // Pause timer
            pauseTimer()
            startPauseButton.text = "Start"
        }
        isTimerRunning = !isTimerRunning
    }

    fun startTimer(){
        // Get the time from the timerView
        if (viewModel.getCurrentTimeLeft().value!! >0){
            duration = viewModel.getCurrentTimeLeft().value!!
        } else {
            duration = workoutRoutine[currentIndex]["duration"]!!.toInt()*1000
        }
        Log.d("Timer", "startTimer: $duration")
        viewModel.setIsTimerRunning(true)
        isTimerRunning = true
        timer = object : CountDownTimer(duration.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                duration -= 100
                // Update the timer
                viewModel.setCurrentTimeLeft(duration)
            }

            override fun onFinish() {
                startPauseButton.text = "Restart"
                isTimerRunning = false
                viewModel.setCurrentTimeLeft(0)
                viewModel.setIsTimerRunning(false)
            }
        }
        timer.start()

    }

    fun pauseTimer(){
        timer.cancel()
        viewModel.setIsTimerRunning(false)
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