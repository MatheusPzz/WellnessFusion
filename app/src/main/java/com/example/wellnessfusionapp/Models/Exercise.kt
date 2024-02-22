package com.example.wellnessfusionapp.Models

import android.media.Image
import android.widget.ImageButton

data class Exercise(
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val repetitions: Int,
    val sets: Int,
    val categoryId: String,
)
