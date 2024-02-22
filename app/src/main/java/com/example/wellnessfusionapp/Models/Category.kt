package com.example.wellnessfusionapp.Models

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    var isSelected: Boolean = false,
    val icon: ImageVector
)
