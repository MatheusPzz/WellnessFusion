package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.GoalType
import com.google.firebase.Timestamp
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavController, viewModel: MainViewModel) {
    val predefinedGoalTypes = listOf("Exercising Weight Progress", "Meditation Time Progress")
    var selectedGoalType by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { GoalScreenTopAppBar(navController) }) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {

            GoalScreenContent(
                predefinedGoalTypes = predefinedGoalTypes,
                selectedGoalType = selectedGoalType,
                onGoalTypeSelected = { selectedGoalType = it },
                viewModel = viewModel,
                onGoalAdded = { selectedGoalType = null },
                navController = navController
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreenTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("Set Your Goals") },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun GoalScreenContent(
    predefinedGoalTypes: List<String>,
    selectedGoalType: String?,
    onGoalTypeSelected: (String) -> Unit,
    viewModel: MainViewModel,
    onGoalAdded: () -> Unit,
    navController: NavController
) {
    if (selectedGoalType != null) {
        GoalDetailScreen(
            viewModel = viewModel,
            goalType = selectedGoalType,
            onGoalAdded = onGoalAdded,
            navController = navController
        )
    } else {
        GoalSelectionList(predefinedGoalTypes, onGoalTypeSelected)
    }
}

@Composable
fun GoalSelectionList(predefinedGoalTypes: List<String>, onGoalTypeSelected: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        items(predefinedGoalTypes) { goalType ->
            GoalTypeItem(goalType = goalType, onGoalTypeSelected = onGoalTypeSelected)
        }
    }
}

@Composable
fun GoalTypeItem(goalType: String, onGoalTypeSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onGoalTypeSelected(goalType) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray, MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(text = goalType, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: MainViewModel,
    goalType: String,
    onGoalAdded: () -> Unit,
    navController: NavController
) {
    LaunchedEffect(key1 = true) {
        viewModel.fetchAllExercises()
    }

    val exercises by viewModel.exercisesForDropdown.observeAsState(emptyList())
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    val currentValue by remember { mutableStateOf("") }
    var initialValue by remember { mutableStateOf("") }
    var desiredValue by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val startDate by remember { mutableStateOf(Timestamp.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(goalType, style = MaterialTheme.typography.titleLarge, fontSize = 21.sp)
        Spacer(Modifier.height(10.dp))

        if (goalType == "Meditation Time Progress") {
            // UI for setting meditation goal`
            Column(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
            ) {
                Text(
                    "Meditation Time Goal:",
                    style = MaterialTheme.typography.bodyLarge
                )
                // Desired meditation time
                OutlinedTextField(
                    value = initialValue,
                    onValueChange = { initialValue = it },
                    label = { Text("Current Meditation Time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = desiredValue,
                    onValueChange = { desiredValue = it },
                    label = { Text("Desired Meditation Time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

        } else {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Set your goal for the following exercise",
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End

                ) {

                    OutlinedTextField(
                        value = selectedExercise?.name ?: "Select Exercise",
                        onValueChange = { expanded = !expanded },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        label = { Text("Exercise") },
                        modifier = Modifier.fillMaxWidth().clickable {
                            expanded = !expanded
                        },
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    )
                    {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(350.dp)
                        ) {
                            exercises.forEach { exercise ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedExercise = exercise
                                        expanded = false
                                    },
                                    text = { Text(exercise.name) }
                                )
                            }
                        }
                    }
                }

                if (goalType != "Exercising Days Goal") {
                    // Render text fields specific to other goal types
                    OutlinedTextField(
                        value = initialValue,
                        onValueChange = { initialValue = it },
                        label = { Text("Current Weight: KG") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = desiredValue,
                        onValueChange = { desiredValue = it },
                        label = { Text("Goal: KG") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(onClick = {
            if (goalType == "Meditation Time Progress" && desiredValue.isNotEmpty()) {
                println(": $desiredValue days")
                val newGoal = Goal(
                    id = UUID.randomUUID().toString(),
                    type = GoalType(goalType, ""), // Create GoalType instance
                    typeId = "",
                    description = selectedExercise?.name ?: "",
                    desiredValue = desiredValue.toInt(),
                    initialValue = initialValue.toInt(),
                    exerciseId = null,
                    startDate = Timestamp.now(),
                    endDate = null,
                    status = "active",
                    workoutDays = null
                )
                viewModel.addGoal(newGoal)
                onGoalAdded()
                navController.popBackStack()
            } else if (goalType == "Exercising Weight Progress" && selectedExercise != null && initialValue.isNotEmpty() && desiredValue.isNotEmpty()) {
                // Handle other goal types
                val newGoal = Goal(
                    id = UUID.randomUUID().toString(),
                    type = GoalType(goalType, ""), // Create GoalType instance
                    typeId = "",
                    description = selectedExercise!!.name,
                    desiredValue = desiredValue.toInt(),
                    initialValue = initialValue.toInt(),
                    exerciseId = selectedExercise!!.id ?: "",
                    startDate = Timestamp.now(),
                    endDate = null,
                    status = "active",
                    workoutDays = null
                )
                Log.d("GoalScreen", "New goal: $newGoal")
                viewModel.addGoal(newGoal)
                Log.d("GoalScreen", "Goal added")
                onGoalAdded()
                Log.d("GoalScreen", "onGoalAdded called")
                navController.navigate("home")
            }
        }) {
            Text("Add Goal")
        }
    }
}
