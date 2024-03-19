@file:Suppress("NAME_SHADOWING")

package com.example.wellnessfusionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    navController: NavController,
    viewModel: MainViewModel,
    goals: List<Goal>,
) {
    val showDialog = remember { mutableStateOf(false) }
    val showAddGoalDialog = remember { mutableStateOf(false) }
    var selectedWorkoutPlans by remember { mutableStateOf<List<WorkoutPlan>>(emptyList()) }
    val isAddingNewLog by viewModel.isAddingNewLog.observeAsState(false)



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Training Record") },
                actions = {
                    if (!isAddingNewLog) {
                        IconButton(onClick = { showDialog.value = true }) {
                            Icon(imageVector = Filled.Add, contentDescription = "Add Log")
                        }
                    }
                },
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "Add personal goals to keep track of your progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(goals) { goal ->
                        GoalMiniCard(goal = goal) // Assuming GoalMiniCard is a composable you've defined
                    }
                    item {
                        IconButton(onClick = { navController.navigate("goalScreen") }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
                            Text("Add Goal", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
                // This part is for the saved logs list
                if (!isAddingNewLog) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(2f) // Larger portion for logs
                            .fillMaxWidth()
                    ) {
                        SavedLogsList(viewModel = viewModel)
                    }
                }

                if (selectedWorkoutPlans.isNotEmpty() && isAddingNewLog) {
                    LogDetailsForm(
                        workoutPlans = selectedWorkoutPlans,
                        viewModel = viewModel,
                        onLogSaved = {
                            selectedWorkoutPlans = emptyList()
                            viewModel.finishAddingNewLog()
                        }
                    )
                } else if (!isAddingNewLog) {
                    Text(
                        "Here you can select workout plans to log your progression.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (showDialog.value && !isAddingNewLog) {
                SelectWorkoutPlanDialog(
                    mainViewModel = viewModel,
                    onPlansSelected = { workoutPlans ->
                        selectedWorkoutPlans = workoutPlans
                        viewModel.startAddingNewLog()
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun GoalMiniCard(goal: Goal) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target: ${goal.desiredValue}",
                    style = MaterialTheme.typography.bodySmall
                )
                // Optionally display current value if applicable
                goal.currentValue?.let {
                    Text(
                        text = "Current: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // You can adjust the visibility of this button or handle its click event based on your app's functionality
            Button(
                onClick = { /* Handle edit or view goal details */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("View", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}



@Composable
fun SelectWorkoutPlanDialog(
    mainViewModel: MainViewModel,
    onPlansSelected: (List<WorkoutPlan>) -> Unit, // Changed to handle multiple plans
    onDismiss: () -> Unit
) {
    val savedWorkouts by mainViewModel.workoutPlans.observeAsState(initial = listOf())
    val selectedWorkouts =
        remember { mutableStateListOf<WorkoutPlan>() } // Tracks selected workouts

    if (savedWorkouts.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Select Workout Plans") }, // Changed title for clarity
            text = {
                LazyColumn {
                    items(savedWorkouts) { workout ->
                        val isSelected = selectedWorkouts.contains(workout)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(10.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                .clickable {
                                    if (isSelected) {
                                        selectedWorkouts.remove(workout)
                                    } else {
                                        selectedWorkouts.add(workout)
                                    }
                                }
                        ) {
                            Text(workout.planName, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.weight(1f))
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // We handle the change in the Row's clickable
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onPlansSelected(selectedWorkouts.toList()); onDismiss() }) {
                    Text("Select")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LogDetailsForm(
    workoutPlans: List<WorkoutPlan>, // Now accepts a list of WorkoutPlan
    viewModel: MainViewModel,
    onLogSaved: () -> Unit
) {
    // This will hold all unique exercises across the selected workout plans
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var exerciseDetails = remember { mutableStateListOf<ExerciseDetail>() }
    var isNameDialogVisible by remember { mutableStateOf(false) }
    var logName by remember { mutableStateOf("") }

    // Fetch and combine exercises from all plans
    LaunchedEffect(workoutPlans) {
        val combinedExerciseIds = workoutPlans.flatMap { it.exercises }.distinct()
        viewModel.fetchExercisesDetailsByIds(combinedExerciseIds) { fetchedExercises ->
            exercises = fetchedExercises
            exerciseDetails.clear()
            fetchedExercises.forEach { exercise ->
                exerciseDetails.add(ExerciseDetail(exercise.id, exercise.name))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Logging for Plans:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            workoutPlans.forEach { plan ->
                Text(
                    plan.planName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
        }
        Divider() // Divider for visual separation

        // Display exercises as before, now consolidated from multiple plans
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(exerciseDetails) { detail ->
                ExerciseDetailEntry(detail = detail) { updatedDetail ->
                    val index =
                        exerciseDetails.indexOfFirst { it.exerciseId == updatedDetail.exerciseId }
                    if (index != -1) {
                        exerciseDetails[index] = updatedDetail
                    }
                }
            }
        }

        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { isNameDialogVisible = true },
                modifier = Modifier
                    .padding(0.dp)
                    .width(40.dp)
                    .height(35.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp), imageVector = Filled.Done, contentDescription = "Save"
                )
            }
            IconButton(
                onClick = {
                    // Adjust saving logic if necessary to account for logging multiple plans
                    viewModel.saveExerciseLog(
                        logName,
                        workoutPlans.map { it.id }.toString(),
                        exerciseDetails
                    )
                    isNameDialogVisible = false
                    logName = "" // Clear log name for next use


                    onLogSaved()
                },
                modifier = Modifier
                    .padding(0.dp)
                    .width(40.dp)
                    .height(35.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp),
                    imageVector = Filled.Close,
                    contentDescription = "Save"
                )
            }
        }
    }

    if (isNameDialogVisible) {
        NameLogDialog(
            logName = logName,
            onLogNameChange = { logName = it },
            onConfirm = {
                if (logName.isNotBlank()) {
                    val workoutPlanIds =
                        workoutPlans.joinToString(separator = ",") { it.id.toString() }
                    viewModel.saveExerciseLog(logName, workoutPlanIds, exerciseDetails)
                    isNameDialogVisible = false
                    logName = "" // Clear the log name for next use
                    onLogSaved()
                }
            },
            onDismiss = { isNameDialogVisible = false }
        )
    }
}

@Composable
fun NameLogDialog(
    logName: String,
    onLogNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Name Your Log") },
        text = {
            TextField(
                value = logName,
                onValueChange = onLogNameChange,
                label = { Text("Log Name") }
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExerciseDetailEntry(
    detail: ExerciseDetail,
    onDetailUpdate: (ExerciseDetail) -> Unit // Callback to handle updates
) {
    var reps by remember { mutableStateOf(detail.reps.toFloat()) }
    var sets by remember { mutableStateOf(detail.sets.toFloat()) }
    var weightText by remember { mutableStateOf(detail.weight.toString()) }

    Column(
        modifier = Modifier.padding(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(detail.exerciseName, style = MaterialTheme.typography.headlineLarge)
            TextField(
                value = weightText,
                onValueChange = { newValue ->
                    weightText = newValue.filter { it.isDigit() || it == '.' }
                    val newWeight = weightText.toFloatOrNull()
                        ?: detail.weight // Fallback to the current weight if conversion fails
                    onDetailUpdate(detail.copy(weight = newWeight))
                },
                label = { Text("Weight: kg") },
                singleLine = true,






                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Sets: ${sets.toInt()}")
            Slider(
                value = sets, onValueChange = { newValue ->
                    sets = newValue
                    onDetailUpdate(detail.copy(sets = sets.toInt()))
                }, valueRange = 1f..10f, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(200.dp)
            )

            Text("Reps: ${reps.toInt()}")
            Slider(
                value = reps, onValueChange = { newValue ->
                    reps = newValue
                    onDetailUpdate(detail.copy(reps = reps.toInt()))
                }, valueRange = 1f..20f, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(200.dp)
            )
        }
    }
    Divider()
}

@Composable
fun SavedLogsList(viewModel: MainViewModel) {
    val savedLogs by viewModel.savedLogs.observeAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(savedLogs) { log ->
            LogItem(log = log, viewModel = viewModel) // Passando o log e o ViewModel para LogItem
            Divider() // Adiciona um divisor entre os itens para melhor visualização
        }
    }
}


@Composable
fun LogItem(log: TrainingLog, viewModel: MainViewModel) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(modifier = Modifier.clickable { viewModel.toggleLogDetails(log.logName) }) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(log.logName, style = MaterialTheme.typography.headlineLarge)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(dateFormat.format(log.logDate), style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { viewModel.deleteLog(log.logName) }) {
                    Icon(imageVector = Filled.Close, contentDescription = "Delete")
                }
            }
        }


        // Toggle visibility of log details
        if (log.isDetailsVisible) {
            log.exercises.forEach { exerciseDetail ->
                Text(
                    "Exercise: ${exerciseDetail.exerciseName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Sets: ${exerciseDetail.sets}", style = MaterialTheme.typography.bodySmall)
                Text("Reps: ${exerciseDetail.reps}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Weight: ${exerciseDetail.weight}kg",
                    style = MaterialTheme.typography.bodySmall
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp)) // Optional: add a divider for clarity
            }
        }
    }
}
//            if (showGoalsDialog.value) {
//                GoalSelectionDialog(
//                    goalsList = userGoals,
//                    onGoalSelected = { selectedGoal ->
//                        viewModel.addUserGoal(selectedGoal)
//                        showGoalsDialog.value = false
//                    },
//                    onDismiss = { showGoalsDialog.value = false }
//                )
//            }

//@Composable
//fun GoalSettingDialog(onDismiss: () -> Unit, onSaveGoal: (Goal) -> Unit) {
//    var selectedGoalType by remember { mutableStateOf("") }
//    var targetValue by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Defina sua Meta") },
//        text = {
//            Column {
//                DropdownMenu(...) // Exemplo, adaptar conforme a necessidade
//                TextField(
//                    value = targetValue,
//                    onValueChange = { targetValue = it },
//                    label = { Text("Valor Alvo") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//                )
//            }
//        },
//        confirmButton = {
//            Button(onClick = {
//                val newGoal = Goal(
//                    type = selectedGoalType,
//                    desiredValue = targetValue.toInt(),
//                    ...
//                )
//                onSaveGoal(newGoal)
//                onDismiss()
//            }) {
//                Text("Salvar")
//            }
//        }
//    )
//}
//
//@Composable
//fun GoalSelectionDialog(
//    goalsList: List<Goal>,
//    onGoalSelected: (Goal) -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Select a Goal") },
//        text = {
//            LazyColumn {
//                items(goalsList) { goal ->
//                    Text(
//                        text = goal.type,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onGoalSelected(goal)
//                                onDismiss()
//                            }
//                            .padding(8.dp)
//                    )
//                }
//            }
//        },
//        confirmButton = {
//            Button(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
//}
//
