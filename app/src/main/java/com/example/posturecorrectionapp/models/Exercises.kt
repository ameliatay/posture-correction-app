package com.example.posturecorrectionapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Exercises(
    @StringRes val exerciseStringId: Int,
    @StringRes val categoryStringId: Int,
    @StringRes val difficulty: Int,
    @DrawableRes val imageResourceId: Int
)