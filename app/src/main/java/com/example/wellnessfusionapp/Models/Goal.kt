package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp
import com.google.type.Date
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val type: GoalType,
    val description: String = "",
    val desiredValue: Int = 0,
    val currentValue: Int = 0,
    val exerciseId: String? = null,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Date? = null,
    var status: String = "active",
    val workoutDays: Int? = null
){
    constructor() : this(
        id = UUID.randomUUID().toString(),
        type = GoalType("", ""),
        description = "",
        desiredValue = 0,
        currentValue = 0,
        exerciseId = "",
        startDate = Timestamp.now(),
        endDate = null,
        status = "active",
        workoutDays = 0
    )
}
