package com.example.posturecorrectionapp.data

data class PoseRule(
    val name: String,
    val bodyPart1: BodyPart,
    val bodyPart2: BodyPart,
    val bodyPart3: BodyPart?,
    val minAngle: Float,
    val maxAngle: Float,
    val leniency: Float,
)
