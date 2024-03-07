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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneratedWorkoutViewModel @Inject constructor() : ViewModel() {




    // variaveris para manter as listas de planos salvos pelo usuario
    val _savedWorkouts = MutableLiveData<List<WorkoutPlan>>()
    val savedWorkouts: LiveData<List<WorkoutPlan>> = _savedWorkouts


    // Criando uma estancia para o usuario atual

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
    /*Buscando os treinos salvos no firebase*/

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



    private val _exercisesDetails = MutableLiveData<List<Exercise>>()
    val exercisesDetails: LiveData<List<Exercise>> = _exercisesDetails

    //

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

                // Chame o callback com a lista de exercícios obtida
                withContext(Dispatchers.Main) {
                    onComplete(exercises)
                }
            } catch (e: Exception) {
                Log.e("Fetch Exercise Details", "Error fetching exercises details", e)
                // Pode ser uma boa ideia chamar o callback mesmo em caso de erro, possivelmente com uma lista vazia ou passando uma mensagem de erro.
                withContext(Dispatchers.Main) {
                    onComplete(emptyList())
                }
            }
        }
    }










    // Tornando a tela de planos de exrcicio editavel

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
