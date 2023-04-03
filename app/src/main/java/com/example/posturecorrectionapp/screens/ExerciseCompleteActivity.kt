package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.Workout

class ExerciseCompleteActivity : AppCompatActivity() {
    private lateinit var nextWorkoutButton : Button
    private lateinit var homeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_complete)
        //Require portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Get the number of exercises completed and duration from the previous activity

        val it = intent
        val numberOfExercisesCompleted = it.getStringExtra("numberOfExercisesCompleted")
        val duration = it.getStringExtra("duration")

        //Convert the duration to minutes and seconds
        val minutes = duration?.toInt()?.div(60)
        val seconds = duration?.toInt()?.rem(60)


        // Set the text of the text views
        findViewById<TextView>(R.id.exercisesValue).text = numberOfExercisesCompleted.toString()
        findViewById<TextView>(R.id.durationValue).text = "$minutes:$seconds"

        nextWorkoutButton = findViewById(R.id.nextWorkoutBtn)
        homeButton = findViewById(R.id.homeBtn)

    }

    fun again(view: View) {
        //send result code to intent
        val resultIntent = Intent()
        setResult(2, resultIntent)
        finish()
    }

    fun home(view: View) {
        //send result code to intent
        val resultIntent = Intent()
        setResult(1, resultIntent)
        finish()
    }
}