package com.example.wellnessfusionapp.Models

data class ExerciseLog(
    val logName: String,
    val logDate: java.util.Date,
    val exerciseId: String,
    val exerciseName: String,
    var sets: Int = 0,
    var reps: Int = 0,
    var weight: Float = 0f,
    val isDetailsVisible: Boolean = false
)
