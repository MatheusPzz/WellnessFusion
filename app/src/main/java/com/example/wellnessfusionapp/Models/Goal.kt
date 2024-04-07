package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp
import com.google.type.Date
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val type: GoalType,
    val typeId: String,
    val description: String = "",
    val desiredValue: Int = 0,
    val initialValue: Int = 0,
    val currentValue: Int = 0,
    val exerciseId: String? = null,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    var status: String = "active",
    val workoutDays: Int? = null
){
    constructor() : this(
        id = UUID.randomUUID().toString(),
        type = GoalType("", "", GoalCategory.PHYSICAL, 0),
        typeId = "",
        description = "",
        desiredValue = 0,
        initialValue = 0,
        currentValue = 0,
        exerciseId = "",
        startDate = Timestamp.now(),
        endDate = Timestamp.now(),
        status = "active",
        workoutDays = 0
    )
}
