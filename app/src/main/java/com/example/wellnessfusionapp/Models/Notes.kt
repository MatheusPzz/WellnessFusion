package com.example.wellnessfusionapp.Models

data class Notes(
    val userId: String = "",
    val noteId: String = "",
    val exerciseId: String = "",
    val noteText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
