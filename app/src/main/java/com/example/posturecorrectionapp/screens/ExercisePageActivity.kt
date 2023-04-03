package com.example.posturecorrectionapp.screens

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.posturecorrectionapp.R

class ExercisePageActivity : AppCompatActivity() {
    lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_page)

        videoView = findViewById(R.id.videoView);

        val uri = Uri.parse(
            ("android.resource://"
                    + packageName + "/" + R.raw.push_up)
        )
        videoView.setMediaController(MediaController(this))
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}