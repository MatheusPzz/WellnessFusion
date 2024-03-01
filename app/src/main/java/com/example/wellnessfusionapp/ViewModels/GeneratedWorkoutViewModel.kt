package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class GeneratedWorkoutViewModel @Inject constructor() : ViewModel() {


    val _savedWorkouts = MutableLiveData<List<WorkoutPlan>>()
    val savedWorkouts: LiveData<List<WorkoutPlan>> = _savedWorkouts


    /*Buscando os treinos salvos no firebase*/

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    fun fetchSavedWorkouts() {
        viewModelScope.launch {

            val userId = getCurrentUserId()

            if (userId.isNotEmpty()) {
                try {
                    val db = Firebase.firestore
                    val workoutPlans = db
                        .collection("Users").document(userId)
                        .collection("UserProfile").document(userId)
                        .collection("WorkoutPlans")
                        .get()
                        .await()
                        .documents.mapNotNull { it.toObject(WorkoutPlan::class.java) }

                    _savedWorkouts.value = workoutPlans
                    Log.d("Workouts VM", "Workout plans fetched successfully")
                } catch (e: Exception) {
                    Log.e("Workouts VM", "Error trying to fetch the workout plans", e)
                }
            }
        }
    }

    /*Funcao para buscar os detalhes de cada exericio baseados nos seus ids salvos na criacao do treino*/

    val _exercisesDetails = MutableLiveData<List<Exercise>>()
    val exercisesDetails: LiveData<List<Exercise>> = _exercisesDetails

    suspend fun fetchExercisesDetails(exerciseIds: List<String>) {
        Log.d("Fetch Exercise Details", "Fetching details for exercise IDs: $exerciseIds")
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                // Utilize o método .whereIn para buscar documentos que tenham um campo 'id' correspondente aos IDs na lista exerciseIds
                val querySnapshot = db.collection("Exercises")
                    .whereIn("id", exerciseIds)
                    .get()
                    .await()

                val exercises = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Exercise::class.java)?.also { exercise ->
                        Log.d("Fetch Exercise Details", "Found exercise: ${exercise.name}")
                    }
                }

                withContext(Dispatchers.Main) {
                    _exercisesDetails.value = exercises
                    Log.d(
                        "Workouts VM",
                        "Exercises details fetched successfully: ${exercises.size}"
                    )
                }
            } catch (e: Exception) {
                Log.e("Workouts VM", "Error fetching exercises details", e)
            }
        }
    }
}
