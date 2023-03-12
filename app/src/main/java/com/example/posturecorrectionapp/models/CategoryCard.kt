package com.example.posturecorrectionapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CategoryCard(
    @StringRes val categoryStringId: Int,
    @DrawableRes val imageResourceId: Int
)