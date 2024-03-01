package com.example.wellnessfusionapp.ViewModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {
    private val _physicalCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("0", "Chest", false, Icons.Default.AccountBox),
            Category("1", "Arms", false, Icons.Default.AccountBox),
            Category("2", "Back", false, Icons.Default.AccountBox),
            Category("3", "Legs", false, Icons.Default.AccountBox),
            Category("4", "Shoulders", false, Icons.Default.AccountBox),
            Category("5", "Abs", false, Icons.Default.AccountBox),
        )
    )
    private val _zenCategory = MutableStateFlow<List<Category>>(
        listOf(
            Category("6", "Meditation", false, Icons.Default.AccountBox),
            Category("7", "Breathing", false, Icons.Default.AccountBox),
            Category("8", "Mindfulness", false, Icons.Default.AccountBox),
            Category("9", "Yoga", false, Icons.Default.AccountBox),
            Category("10", "Stretching", false, Icons.Default.AccountBox),
            Category("11", "Gaming", false, Icons.Default.AccountBox),
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
}