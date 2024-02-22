package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel

@Composable
fun ExerciseSelectionScreen(navController: NavController, viewModel: CategoryViewModel, selectedCategoryIds: List<String>) {
    val selectedExercises = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize()) {
        selectedCategoryIds.forEach { categoryId ->
            val exercises by viewModel.getExercisesForCategory(categoryId).collectAsState(initial = emptyList())
            CategoryCard(
                categoryId = categoryId,
                exercises = exercises,
                selectedExercises = selectedExercises,
                onExerciseSelectionChanged = { exercise, isSelected ->
                    if (isSelected) {
                        selectedExercises.add(exercise.categoryId)
                    } else {
                        selectedExercises.remove(exercise.categoryId)
                    }
                }
            )
        }
        Button(
            onClick = {
                // Handle generate workout plan logic
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Generate Workout Plan")
        }
    }
}



@Composable
fun ExerciseItem(exercise: Exercise, onExerciseSelected: (Exercise, Boolean) -> Unit, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onExerciseSelected(exercise, !isSelected) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onExerciseSelected(exercise, it) }
            )
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


@Composable
fun CategoryCard(categoryId: String, exercises: List<Exercise>, selectedExercises: List<String>, onExerciseSelectionChanged: (Exercise, Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = categoryId, // You might want to pass the category name instead of the ID
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            exercises.forEach { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onExerciseSelected = onExerciseSelectionChanged,
                    isSelected = selectedExercises.contains(exercise.categoryId)
                )
            }
        }
    }
}