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
    private val _physicalCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("0", "Chest", false, R.drawable.chest),
            Category("1", "Arms", false, R.drawable.arms),
            Category("2", "Back", false, R.drawable.backs),
            Category("3", "Legs", false, R.drawable.legs),
            Category("4", "Shoulders", false, R.drawable.shoulders),
            Category("5", "Abs", false, R.drawable.abs),
        )
    )
    private val _zenCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("6", "Meditation", false, R.drawable.meditation),
            Category("7", "Breathing", false, R.drawable.breathing),
            Category("8", "Mindfulness", false, R.drawable.mindfullness),
            Category("9", "Yoga", false, R.drawable.yoga),
            Category("10", "Stretching", false, R.drawable.stretching),
            Category("11", "Gaming", false, R.drawable.gaming),
        )
    )

    // Expose as read-only StateFlow
    val physicalCategory: StateFlow<List<Category>> = _physicalCategory.asStateFlow()
    val zenCategory: StateFlow<List<Category>> = _zenCategory.asStateFlow()


    // Function to update the selection of a category
    fun updateCategorySelection(type: WorkoutType, categoryId: String, isSelected: Boolean) {
        val targetFlow = if (type == WorkoutType.PHYSICAL) _physicalCategory else _zenCategory
        targetFlow.value = targetFlow.value.map {
            if (it.categoryId == categoryId) it.copy(isSelected = isSelected) else it
        }
        notifyCategorySelectionChanged()
    }

    fun clearCategorySelections() {
        _physicalCategory.value = _physicalCategory.value.map { it.copy(isSelected = false) }
        _zenCategory.value = _zenCategory.value.map { it.copy(isSelected = false) }
        viewModelScope.launch {
            SharedCategorySelection.updateSelectedCategoryIds(emptyList())
        }
    }

    private fun notifyCategorySelectionChanged() {
        viewModelScope.launch {
            val selectedIds = (_physicalCategory.value + _zenCategory.value)
                .filter { it.isSelected }
                .map { it.categoryId }
            SharedCategorySelection.updateSelectedCategoryIds(selectedIds)
        }
    }

    fun getSelectedCategoryIds(): List<String> {
        return (_physicalCategory.value + _zenCategory.value)
            .filter { it.isSelected }
            .map { it.categoryId }
    }

    companion object SharedCategorySelection {
        private val _selectedCategoryIds = MutableSharedFlow<List<String>>(replay = 1)
        val selectedCategoryIds: SharedFlow<List<String>> = _selectedCategoryIds

        suspend fun updateSelectedCategoryIds(ids: List<String>) {
            _selectedCategoryIds.emit(ids)
        }
    }


    private val _userName = MutableStateFlow("") // Default value
    val userName: StateFlow<String> = _userName.asStateFlow()


    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
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