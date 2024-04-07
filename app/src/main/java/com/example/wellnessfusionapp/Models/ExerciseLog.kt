package com.example.wellnessfusionapp.Models

import java.util.Date

data class TrainingLog(
    val logName: String,
    val logDate: Date,
    val workoutPlanId: String,
    val exercises: List<ExerciseDetail>,
    val isDetailsVisible: Boolean = false
){
    constructor() : this("", Date(), "", emptyList())
}

data class ExerciseDetail(
    val exerciseId: String,
    val exerciseName: String,
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Int = 0,
)
{
    constructor() : this("", "", 0, 0, 0)
}
