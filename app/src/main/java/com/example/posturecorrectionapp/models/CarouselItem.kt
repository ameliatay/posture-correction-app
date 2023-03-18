package com.example.posturecorrectionapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CarouselItem(
    @DrawableRes val imageResourceId: Int,
    @StringRes val titleStringId: Int,
    @StringRes val subtitleStringId: Int,
    val position: Int,
)