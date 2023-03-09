package com.example.posturecorrectionapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Workout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Workout", "onCreate: ")
        setContentView(R.layout.activity_workout)
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        Log.d("Workout", "onConfigurationChanged: " + newConfig.orientation)
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.activity_workout_landscape)
//            Log.d("Workout", "onConfigurationChanged: landscape" )
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            setContentView(R.layout.activity_workout)
//            Log.d("Workout", "onConfigurationChanged: potrait" )
//        }
//    }

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


}