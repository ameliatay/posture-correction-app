package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.Workout

class ExercisePageActivity : AppCompatActivity() {
    lateinit var videoView: VideoView
    lateinit var uri: Uri
    lateinit var exercise: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_page)

        val resourceIdExercise = intent.getStringExtra("exercise")?.toInt()
        exercise = resourceIdExercise?.let { this.resources.getString(it) }.toString()
        findViewById<TextView>(R.id.tvTitle).text = exercise

        val resourceIdDifficulty = intent.getStringExtra("difficulty")?.toInt()
        val difficulty = resourceIdDifficulty?.let { this.resources.getString(it) }
        findViewById<TextView>(R.id.tvDifficulty).text = "Difficulty: $difficulty"

        val resourceIdCategory = intent.getStringExtra("category")?.toInt()
        val category = resourceIdCategory?.let { this.resources.getString(it) }
        findViewById<TextView>(R.id.tvCategory).text = "Category: $category"


        videoView = findViewById(R.id.videoView);

        if (exercise == "Sit Ups") {
            findViewById<TextView>(R.id.tvInstructions).text = R.string.sitUpInstructions?.let { this.resources.getString(it) }
            uri =Uri.parse(
                ("android.resource://"
                        + packageName + "/" + R.raw.sit_up)
            )
        } else {
            findViewById<TextView>(R.id.tvInstructions).text = R.string.pushUpInstructions?.let { this.resources.getString(it) }
            uri = Uri.parse(
                ("android.resource://"
                        + packageName + "/" + R.raw.push_up)
            )
        }

        videoView.setMediaController(MediaController(this))
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }

    fun tryItOut(view: View) {
        var workoutRoutine = ArrayList<Map<String,String>>()

        if (exercise == "Push Ups") {
            workoutRoutine.add(mapOf("name" to "pushup", "duration" to "5"))
        } else {
            workoutRoutine.add(mapOf("name" to "treepose", "duration" to "5"))
        }

        var it = Intent(this, Workout::class.java)
        it.putExtra("workoutRoutine", workoutRoutine)
        startActivity(it)
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}