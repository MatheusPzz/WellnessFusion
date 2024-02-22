package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.lifecycle.ViewModel
import com.example.wellnessfusionapp.DataTypes.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Models.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CategoryViewModel : ViewModel() {
    private val _physicalCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("1", "Chest", false, Icons.Default.AccountBox),
            Category("2", "Arms", false, Icons.Default.AccountBox),
            Category("3", "Back", false, Icons.Default.AccountBox),
            Category("4", "Legs", false, Icons.Default.AccountBox),
            Category("5", "Shoulders", false, Icons.Default.AccountBox),
            Category("6", "Abs", false, Icons.Default.AccountBox),
        )
    )
    private val _zenCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("7", "Meditation", false, Icons.Default.AccountBox),
            Category("8", "Breathing", false, Icons.Default.AccountBox),
            Category("9", "Mindfulness", false, Icons.Default.AccountBox),
            Category("10", "Yoga", false, Icons.Default.AccountBox),
            Category("11", "Stretching", false, Icons.Default.AccountBox),
            Category("12", "Gaming", false, Icons.Default.AccountBox),
        )
    )

    // Expose as read-only StateFlow
    val zenCategory: StateFlow<List<Category>> = _zenCategory.asStateFlow()
    val physicalCategory: StateFlow<List<Category>> = _physicalCategory.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    // Function to update the selection of a category
     fun updateCategorySelection(type: WorkoutType, categoryId: String, isSelected: Boolean) {
        val currentList = if (type == WorkoutType.PHYSICAL) _physicalCategory.value else _zenCategory.value
        val selectedCount = currentList.count { it.isSelected }

        if (isSelected && selectedCount >= 3) {
            // Emit message to UI or handle the logic to prevent more than 3 selections
            return
        }

        val updatedCategories = currentList.map { category ->
            if (category.id == categoryId) category.copy(isSelected = isSelected) else category
        }

        if (type == WorkoutType.PHYSICAL) {
            _physicalCategory.value = updatedCategories
        } else {
            _zenCategory.value = updatedCategories
        }
    }

    fun getExercisesForCategory(categoryId: String): Flow<List<Exercise>> = flow {
        try {
            val exercises = db.collection("Exercises Collection")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(Exercise::class.java)
            emit(exercises)
        } catch (e: Exception) {
            Log.e("CategoryViewModel", "Error fetching exercises for category $categoryId", e)
            emit(emptyList<Exercise>()) // Emit an empty list or a specific error object
        }
    }

    fun getSelectedCategories(): List<String> {
        return (_physicalCategory.value + _zenCategory.value)
            .filter { it.isSelected }
            .map { it.id }
    }
}