package com.example.wellnessfusionapp.ViewModels


import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Models.WorkoutType
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ExerciseSelectionViewModel @Inject constructor() : ViewModel() {

    init {
        // Initializes the viewModel setting up a listener for changes in the category
        listenToCategorySelectionChanges()
    }

    // Returning the ID of the logged user using firebase AUTH
    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    // State flow that holds a map of exercises grouped by their category Name.
    private val _groupedExercisesState = MutableStateFlow<Map<String, List<Exercise>>>(mapOf())
    val groupedExercisesState: StateFlow<Map<String, List<Exercise>>> =
        _groupedExercisesState.asStateFlow()



    // Collecting the flow that listens to the changes in the category selection
    // It also calls the variable updateExercises with new selected category Ids
    private fun listenToCategorySelectionChanges() {
        viewModelScope.launch {
            CategoryViewModel.selectedCategoryIds.collect { selectedCategoryIds ->
                updateExercises(selectedCategoryIds)
            }
        }
    }


    // Fetches the exercises from firestore based on their selected category Ids, grouped by categoryName
    private suspend fun updateExercises(categoryIds: List<String>) {
        val exercises = getExercisesFromFireStore(categoryIds)
        _groupedExercisesState.value = exercises.groupBy { it.categoryName }
    }


    /*
    * Realiza uma consulta ao firestore para buscar exercicios que correspondem aos ids de categorias selecionadas
    * retorna uma lista de objetos exercicios
    * */

    // Queries firebase firestore for exercises matching the selected category Ids and return the list of the matched category exercises
    private suspend fun getExercisesFromFireStore(categoryIds: List<String>): List<Exercise> {
        val db = FirebaseFirestore.getInstance()
        val exercises = mutableListOf<Exercise>()

        if (categoryIds.isNotEmpty()) {
            try {
                db.collection("Exercises")
                    .whereIn("categoryId", categoryIds)
                    .get()
                    .await()
                    .documents
                    .forEach { document ->
                        document.toObject(Exercise::class.java)?.let { exercise ->
                            exercises.add(exercise)
                        }
                    }
            } catch (e: Exception) {
                Log.e("ExerciseSelectionVM", "Error fetching exercises: ${e.message}", e)
            }
        }
        return exercises
    }



    private val _selectedExercises = MutableStateFlow<List<Exercise>>(emptyList())
//    val selectedExercises: StateFlow<List<Exercise>> = _selectedExercises.asStateFlow()

//    private val _selectedCategories = MutableStateFlow<List<Exercise>>(emptyList())
//    val selectedCategories: StateFlow<List<Exercise>> = _selectedCategories.asStateFlow()

//    val currentSelection = _selectedExercises.value



    // Clearing the state of exercise selection whenever the user navigates to another screen or saved a plan
    fun clearExerciseSelections() {
        viewModelScope.launch {
            _selectedExercises.value = emptyList()
        }
    }


    // Add or remove exercises from the selected exercises list based if the exercise is or not in the list
    fun toggleExerciseSelection(exercise: Exercise) {

        val currentSelection = _selectedExercises.value.toMutableList()
        if (currentSelection.any { it.id == exercise.id }) {
            currentSelection.removeAll { it.id == exercise.id }
        } else {
            currentSelection.add(exercise)
        }
        _selectedExercises.value = currentSelection
    }


    // Checking if a specific exercise is selected or not
    fun isExerciseSelected(exercise: Exercise): Boolean {
        return _selectedExercises.value.any { it.id == exercise.id }
    }
    // Checking to see if the user selected any exercise, in this case is checking if our variable in not empty (the one that contains the exercises list)
    val hasSelectedExercises: Boolean
        get() = _selectedExercises.value.isNotEmpty()




    // Function responsible for saving a workout plan to firestore, it creates a document with the necessary fields and passes data collected across our app to it
    // Using os success and failure listener to set the prepared data to firestore.
    fun saveWorkoutPlan(navController: NavController, planName: String, workoutType: WorkoutType) {
        val userId = getCurrentUserId()
        val db = Firebase.firestore
        val planDocument =
            db.collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("WorkoutPlans").document()

        // Collecting the exercises
        val exerciseData = _selectedExercises.value.map { exercise ->
            exercise.id
        }

        // Preparing the data
        val workoutPlan = hashMapOf(
            "planName" to planName,
            "exercises" to exerciseData,
            "creationDate" to Timestamp.now(),
            "workoutPlanId" to planDocument.id,
            "category" to workoutType.name, // "mental" or "physical"
            "isStarted" to false, // Initially, the plan is not started
            "completedDate" to null, // Initially, there's no completion date
            "timesFinished" to 0 // Add this line: Initially, the plan has not been completed
        )

        // Setting the document with the fields and workout plan ID
        planDocument.set(workoutPlan)
            .addOnSuccessListener {
                Log.d("SaveWorkoutPlan", "Successfully saved workout plan: $planName")
                navigateToCreatedPlans(navController, planDocument.id)
            }
            .addOnFailureListener { e ->
                Log.e("SaveWorkoutPlan", "Error saving workout plan: ${e.message}", e)
            }
    }

    // Navigating to the workout session screen with the planId as an argument
    private fun navigateToCreatedPlans(navController: NavController, planId: String) {
        navController.navigate("createdPlans/$planId") {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }


}

