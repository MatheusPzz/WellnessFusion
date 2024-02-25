package com.example.wellnessfusionapp.Models

data class Exercise(
    val categoryName: String = "",
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val reps: String = "",
    val sets: String = "",
    val videoUrl: String? = null,
    val imageUrl: String? = null,
    val isSelected : Boolean = false,
    val id : String = ""
    )
