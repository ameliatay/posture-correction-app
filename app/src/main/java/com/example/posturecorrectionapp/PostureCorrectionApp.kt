package com.example.posturecorrectionapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class PostureCorrectionApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}