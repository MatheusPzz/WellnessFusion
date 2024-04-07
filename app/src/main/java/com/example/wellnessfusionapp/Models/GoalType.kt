package com.example.wellnessfusionapp.Models

enum class GoalCategory {
    PHYSICAL,
    MENTAL
}

data class GoalType(
    val id: String,
    val name: String,
    val category: GoalCategory,
    val goalIcon: Int
)
