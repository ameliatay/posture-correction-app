package com.example.posturecorrectionapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ProgramCard(
    @StringRes val categoryStringId: Int,
    @StringRes val programStringId: Int,
    @StringRes val rating: Int,
    @StringRes val duration: Int,
    @DrawableRes val imageResourceId: Int
)