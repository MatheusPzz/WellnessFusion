package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {

    // Holds the list of physical workout categories, represented by their own attributes
    private val _physicalCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("0", "Chest", false, R.drawable.chest_category, WorkoutType.PHYSICAL),
            Category("1", "Arms", false, R.drawable.arms_category, WorkoutType.PHYSICAL),
            Category("2", "Back", false, R.drawable.back_category, WorkoutType.PHYSICAL),
            Category("3", "Legs", false, R.drawable.leg_category, WorkoutType.PHYSICAL),
            Category("4", "Shoulders", false, R.drawable.shoulder_category, WorkoutType.PHYSICAL),
            Category("5", "Abs", false, R.drawable.abs_category, WorkoutType.PHYSICAL),
        )
    )
    // Holds the list of mental workout categories, represented by their own attributes
    private val _mentalCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("6", "Meditation", false, R.drawable.category_meditation, WorkoutType.MENTAL),
            Category("7", "Breathing", false, R.drawable.category_breathing, WorkoutType.MENTAL),
            Category("8", "Yoga", false, R.drawable.category_yoga, WorkoutType.MENTAL),
        )
    )

    // Variables that hold the view of each category to be observed in the UI
    val physicalCategory: StateFlow<List<Category>> = _physicalCategory.asStateFlow()
    val zenCategory: StateFlow<List<Category>> = _mentalCategory.asStateFlow()


    // Function to update the selection of a category, it returns the selected one or null if none is selected
    fun getSelectedWorkoutType(): WorkoutType? {
        val isPhysicalSelected = _physicalCategory.value.any { it.isSelected }
        val isZenSelected = _mentalCategory.value.any { it.isSelected }

        return when {
            isPhysicalSelected -> WorkoutType.PHYSICAL
            isZenSelected -> WorkoutType.MENTAL
            else -> null // No category selected
        }
    }

    // Updates the selection state of a category based on the provided type, category ID and selection state
    fun updateCategorySelection(type: WorkoutType, categoryId: String, isSelected: Boolean) {
        val targetFlow = if (type == WorkoutType.PHYSICAL) _physicalCategory else _mentalCategory
        targetFlow.value = targetFlow.value.map {
            if (it.categoryId == categoryId) it.copy(isSelected = isSelected) else it
        }
        // notifies the observer variables states about the changes
        notifyCategorySelectionChanged()
    }

    // Clearing the selections if the user goes back to the previous screen
    fun clearCategorySelections() {
        _physicalCategory.value = _physicalCategory.value.map { it.copy(isSelected = false) }
        _mentalCategory.value = _mentalCategory.value.map { it.copy(isSelected = false) }
        viewModelScope.launch {
            SharedCategorySelection.updateSelectedCategoryIds(emptyList())
        }
    }

    // emmits an updated list of selected category IDs
    private fun notifyCategorySelectionChanged() {
        viewModelScope.launch {
            val selectedIds = (_physicalCategory.value + _mentalCategory.value)
                .filter { it.isSelected }
                .map { it.categoryId }
            SharedCategorySelection.updateSelectedCategoryIds(selectedIds)
        }
    }

    // Returns a list of the ids of the current selected categories
    fun getSelectedCategoryIds(): List<String> {
        return (_physicalCategory.value + _mentalCategory.value)
            .filter { it.isSelected }
            .map { it.categoryId }
    }

    // This piece of code was taken out from ChatGPT as a base form, then adapted to my application
    // It was need for sharing the ids to different parts of the app, in this case it will be shared to the exercise selection view model
    // It shares the selected category Ids and allowa updating and observe these Ids upon any changes
    companion object SharedCategorySelection {
        private val _selectedCategoryIds = MutableSharedFlow<List<String>>(replay = 1)
        val selectedCategoryIds: SharedFlow<List<String>> = _selectedCategoryIds

        suspend fun updateSelectedCategoryIds(ids: List<String>) {
            _selectedCategoryIds.emit(ids)
        }
    }

    // Hold the current users name as a state flow
    private val _userName = MutableStateFlow("") // Default value
    val userName: StateFlow<String> = _userName.asStateFlow()


    // retrieves the current user Ids from firebaseAuth
    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    // Here we are performing a few queries to fetch the current user name from firestore based in their user ID
    suspend fun fetchUserName() {
        val userId = getCurrentUserId()
        val db = FirebaseFirestore.getInstance()

        try {
            Log.d("Firestore", "Fetching user name for ID: $userId")
            val documentSnapshot = db.collection("Users").document(userId).get().await()
            val fetchedName = documentSnapshot.getString("name") // Use a different variable name here
            Log.d("Firestore", "Fetched name: $fetchedName")

            // Update the _userName StateFlow
            _userName.value = fetchedName ?: "User Not Found"
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user name", e)
            _userName.value = "Error Fetching User" // Update StateFlow on error
        }
    }
}