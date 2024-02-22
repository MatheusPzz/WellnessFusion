package com.example.wellnessfusionapp.Models

import com.example.wellnessfusionapp.Models.Exercise

data class WorkoutPlan(
    val categories: List<String>,
    val exercises: List<Exercise>,
)
