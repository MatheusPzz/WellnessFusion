package com.example.wellnessfusionapp.Models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val categoryId: String,
    val name: String,
    var isSelected: Boolean = false,
    @DrawableRes val image: Int
)
