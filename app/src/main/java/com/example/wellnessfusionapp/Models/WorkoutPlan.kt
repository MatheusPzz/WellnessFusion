package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp
import java.util.UUID

data class WorkoutPlan(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String = "",
    val workoutPlanId: String = "",
    val planName: String = "",
    val category: String = "",
    val timesFinished: Int = 0,
    var isStarted: Boolean = false,
    var completedDate: Timestamp? = null,
    val description: String = "",
    val exercises: List<String> = emptyList(),
    var creationDate: Timestamp = Timestamp.now()
){
    constructor() : this("", "", "", "","",0 , false, null, "", emptyList(), Timestamp.now())
}
