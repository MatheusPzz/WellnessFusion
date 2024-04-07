package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneratedWorkoutViewModel @Inject constructor() : ViewModel() {



    // Variables to keep the lists of plans saved by the user
    val _savedWorkouts = MutableLiveData<List<WorkoutPlan>>()
    val savedWorkouts: LiveData<List<WorkoutPlan>> = _savedWorkouts


    // Creating an instance for the current user
    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    // Here we are fetching the current workout plans saved by the user using a firestore query and storing it in the _savedWorkouts variable
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


    // Holding the fetched details into the _exercisesDetails variable live data
    private val _exercisesDetails = MutableLiveData<List<Exercise>>()
    val exercisesDetails: LiveData<List<Exercise>> = _exercisesDetails


    // Fetching the details of each exercise based on their ids saved in the workout plan creation then placing the results of the query to the _exercisesDetails variable
    fun fetchExercisesDetails(exerciseIds: List<String>, onComplete: (List<Exercise>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                val querySnapshot = db.collection("Exercises")
                    .whereIn("id", exerciseIds)
                    .get()
                    .await()

                val exercises = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Exercise::class.java)
                }

                // Call back with the list of exercises fetched
                withContext(Dispatchers.Main) {
                    onComplete(exercises)
                }
            } catch (e: Exception) {
                Log.e("Fetch Exercise Details", "Error fetching exercises details", e)
            }
        }
    }


    // Variable that holds the selected workout plan to be displayed in the workout plan details screen
    private val _workoutPlan = MutableLiveData<WorkoutPlan?>()
    val workoutPlan: MutableLiveData<WorkoutPlan?> = _workoutPlan


    // Fetching the workout plans by ID, specific for the workout plan details screen and storing the result in the _workoutPlan variable
    fun fetchWorkoutPlanById(workoutPlanId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val documentSnapshot = db.collection("Users").document(getCurrentUserId())
                .collection("UserProfile").document(getCurrentUserId())
                .collection("WorkoutPlans").document(workoutPlanId)
                .get()
                .await()
            val workoutPlan = documentSnapshot.toObject(WorkoutPlan::class.java)
            _workoutPlan.value = workoutPlan
        }
    }


    // Deleting the workout plan from the firestore database with firestore delete method
    fun deleteWorkoutPlan(workoutPlanId: String){
        val userId = getCurrentUserId()
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("WorkoutPlans").document(workoutPlanId)
            .delete()
            .addOnSuccessListener {
                Log.d("Workouts VM", "Workout plan deleted successfully")
                fetchSavedWorkouts()
            }
            .addOnFailureListener { e ->
                Log.e("Workouts VM", "Error trying to delete the workout plan", e)
            }
    }

    // Updating the workout plan name in the firestore database with firestore update method
    fun updateWorkoutPlanName(workoutPlanId: String, newPlanName: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(getCurrentUserId())
            .collection("UserProfile").document(getCurrentUserId())
            .collection("WorkoutPlans").document(workoutPlanId)
            .update("planName", newPlanName)
            .addOnSuccessListener {
                Log.d("Workouts VM", "Workout plan name updated successfully")
                fetchSavedWorkouts()
            }
            .addOnFailureListener { e ->
                Log.e("Workouts VM", "Error trying to update the workout plan name", e)
            }
    }

}
