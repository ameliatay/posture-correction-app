package com.example.posturecorrectionapp.data

data class Exercise(
    val name: String,
    val description: String,
    var exerciseRule: Map<String,List<PoseRule>>
)
