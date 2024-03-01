package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp
import java.util.UUID

data class WorkoutPlan(
    val id : String = "",
    val exerciseId: String = "",
    val workoutPlanId: String = "",
    val planName: String = "",
    val description: String = "",
    val exercises: List<String> = emptyList(),
    var creationDate: Timestamp? = null
){
    constructor() : this("", "", "", "", "", emptyList(), null)
}
