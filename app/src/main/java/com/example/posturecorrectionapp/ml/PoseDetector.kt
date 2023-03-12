package com.example.posturecorrectionapp.ml

import android.graphics.Bitmap
import com.example.posturecorrectionapp.data.Person

interface PoseDetector : AutoCloseable {

    fun estimatePoses(bitmap: Bitmap): List<Person>

    fun lastInferenceTimeNanos(): Long
}
