package com.example.posturecorrectionapp.screens

import android.content.Intent
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
    private lateinit var numberOfExercisesCompleted : TextView
    private lateinit var duration : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_complete)

        // Change values accordingly @SHEN JIE
        numberOfExercisesCompleted = findViewById(R.id.exercisesValue)
        duration = findViewById(R.id.durationValue)

        nextWorkoutButton = findViewById(R.id.nextWorkoutBtn)
        homeButton = findViewById(R.id.homeBtn)

        // NOT SURE WHAT TO PUT FOR ONCLICK for both buttons @SHEN JIE (Finish activity or new intent)
        nextWorkoutButton.setOnClickListener {
            var it = Intent(this, NavigationActivity::class.java)
            startActivity(it)
        }

        homeButton.setOnClickListener {
            finish()
        }
    }
}