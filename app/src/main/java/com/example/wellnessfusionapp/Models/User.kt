package com.example.wellnessfusionapp.Models

data class User(
    var userId: String,
    val email: String,
    val password: String,
    val name: String? = null,
    val profile_picture: String? = null,
    // Add other fields as necessary
)
