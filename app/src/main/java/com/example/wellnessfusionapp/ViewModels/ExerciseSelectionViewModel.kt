package com.example.wellnessfusionapp.ViewModels


import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Models.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ExerciseSelectionViewModel @Inject constructor() : ViewModel() {
    private val _exercisesState = MutableStateFlow<List<Exercise>>(emptyList())
    val exercisesState: StateFlow<List<Exercise>> = _exercisesState.asStateFlow()


    // Upon exercise selection, save it in a list
    private val _selectedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val selectedExercises: StateFlow<List<Exercise>> = _selectedExercises.asStateFlow()

    fun toggleExerciseSelection(exercise: Exercise) {
        viewModelScope.launch {
            val currentList = _selectedExercises.value.toMutableList()
            if (currentList.contains(exercise)){
                currentList.remove(exercise)
            } else {
                currentList.add(exercise)

            }
        }
    }

    fun getSelectedExercises(): List<Exercise> = exercisesState.value.filter { it.isSelected }




    private val _groupedExercisesState = MutableStateFlow<Map<String, List<Exercise>>>(mapOf())
    val groupedExercisesState: StateFlow<Map<String, List<Exercise>>> = _groupedExercisesState.asStateFlow()

    init {
        listenToCategorySelectionChanges()
    }

    private fun listenToCategorySelectionChanges() {
        viewModelScope.launch {
            CategoryViewModel.SharedCategorySelection.selectedCategoryIds.collect { selectedCategoryIds ->
                updateExercises(selectedCategoryIds)
            }
        }
    }

    private suspend fun updateExercises(categoryIds: List<String>) {
        val exercises = getExercisesFromFireStore(categoryIds)
        _groupedExercisesState.value = exercises.groupBy { it.categoryName }
    }

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
}



//    val response: MutableState<DataState<List<Exercise>>> = mutableStateOf(DataState.Loading)
//    init {
//        fetchDataFromDatabase()
//    }
//
//    private fun fetchDataFromDatabase() {
//        val tempList = mutableListOf<Exercise>()
//        val databaseReference = FirebaseDatabase.getInstance().getReference("Exercises")
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                tempList.clear()
//                for (dataSnapshot in snapshot.children) {
//                    val exerciseItem = dataSnapshot.getValue(Exercise::class.java)
//                    exerciseItem?.let {
//                        tempList.add(it)
//                    }
//                }
//                response.value = DataState.Success(tempList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                response.value = DataState.Failure(error.message)
//            }
//        })
//    }
//    private fun getData() {
//        viewModelScope.launch {
//            state.value = getExercisesFromFireStore()
//
//
//        }
//    }
