package com.example.wellnessfusionapp.Models





data class Instructions(
    val musclesWorked: String = "",
    val instructions: String = "",
    val videoUrl: String = "",
    val imageUrl: String = "",
    val weight: String = "",
    val reps: String = "",
    val sets: String = "",
    val exerciseName: String = "",
    val exerciseId: String = ""
)


data class WeightRecommendation(
    val minWeight: Int = 0,
    val maxWeight: Int = 0,
    val recommendedReps : Int = 0,
    val recommendedSets : Int = 0
)