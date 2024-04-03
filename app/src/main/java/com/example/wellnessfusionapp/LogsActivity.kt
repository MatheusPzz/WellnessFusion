@file:Suppress("NAME_SHADOWING")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import android.widget.NumberPicker
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val fontText = FontFamily(
        Font(R.font.zendots_regular),
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                ),
                title = {
                    Text(
                        "Training Record",
                        color = Color.White,
                        fontFamily = fontText,
                        fontSize = 14.sp
                    )
                },
                actions = {
                    if (!isAddingNewLog) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Add a Log",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = fontText
                            )
                            IconButton(onClick = { showDialog.value = true }) {
                                Icon(
                                    imageVector = Filled.Add,
                                    contentDescription = "Add Log",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
        )
        {
            if (
                isAddingNewLog
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )
                LogDetailsFormLogs(
                    workoutPlans = selectedWorkoutPlans,
                    viewModel = viewModel,
                    onLogSaved = {
                        selectedWorkoutPlans = emptyList()
                        viewModel.finishAddingNewLog()
                    },
                    navController = navController
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Current Goals",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xffFE7316),
                                fontFamily = fontText,
                                fontSize = 16.sp
                            )
                            VerticalDivider()
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Text(
                                    "Add a Goal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xffFE7316),
                                    fontFamily = fontText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xffFE7316), RoundedCornerShape(6.dp))
                                ) {
                                    IconButton(onClick = { navController.navigate("goalScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Goal",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Goals(viewModel, navController)
                    }

                    // This part is for the saved logs list

                    Column(
                        modifier = Modifier
                            .padding(13.dp)
                            .weight(2f) // Larger portion for logs
                            .fillMaxWidth()
                    ) {
                        SavedLogsList(viewModel = viewModel, navController = navController)
                    }
                        Text(
                            "Click on the log to see details",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
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
}


@Composable
fun Goals(viewModel: MainViewModel, navController: NavController) {
    val goals by viewModel.goals.observeAsState(initial = emptyList())
    var goalToUpdate by remember { mutableStateOf<Goal?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .alpha(0.8f),
        colors = CardColors(
            Color.Black,
            Color.Black,
            Color.Black,
            Color.Black
        )


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(end = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            TextButton(onClick = { navController.navigate("AchievedGoals") }) {
                Text("See All Achieved Goals", color = Color(0xffFE7316))
            }
            HorizontalDivider()
        }
        LazyRow {
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    navController = navController,
                )
            }
        }
    }
}


//@Composable
//fun GoalCompletionNotification(
//    viewModel: MainViewModel,
//    snackbarHostState: SnackbarHostState,
//    scope: CoroutineScope
//) {
//    val goalCompletionEvent by viewModel.goalCompletionEvent.observeAsState()
//
//    LaunchedEffect(goalCompletionEvent) {
//        goalCompletionEvent?.let { goal ->
//            scope.launch {
//                snackbarHostState.showSnackbar(
//                    message = "Parabéns, sua meta '${goal.description}' foi alcançada com sucesso!"
//                )
//                viewModel.clearGoalCompletionEvent()
//            }
//        }
//    }
//}


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
fun SavedLogsList(viewModel: MainViewModel, navController: NavController) {
    val savedLogs by viewModel.savedLogs.observeAsState(initial = emptyList())
    // Represents the index in savedLogs to start showing logs from
    var startIndex by remember { mutableStateOf(0) }
    val sortedLogs = savedLogs.sortedByDescending { it.logDate }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        colors = CardColors(
            Color.White.copy(alpha = 0.8f),
            Color.Black,
            Color.Black,
            Color.Black
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Records List",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.zendots_regular)),
                    fontSize = 20.sp,
                )
                Row {
                    IconButton(
                        onClick = {
                            startIndex =
                                maxOf(0, startIndex - 3) // Move back by 3 logs, not going below 0
                        },
                        enabled = startIndex > 0 // Enable when not at the start
                    ) {
                        Icon(Icons.Default.ArrowBack, "Show Previous Logs")
                    }
                    VerticalDivider(modifier = Modifier
                        .padding(8.dp)
                        .height(35.dp))

                    IconButton(
                        onClick = {
                            startIndex =
                                minOf(sortedLogs.size - 3, startIndex + 3) // Move forward by 3 logs
                        },
                        enabled = startIndex + 3 < savedLogs.size // Enable if more logs are available ahead
                    ) {
                        Icon(Icons.Default.ArrowForward, "Show Next Logs")
                    }
                }
            }
            HorizontalDivider(thickness = 3.dp, color =Color.Black)

            // Calculate the subset of logs to show based on current startIndex
            val displayLogs = sortedLogs.drop(startIndex).take(3)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(displayLogs) { log ->
                    LogItem(log = log, viewModel = viewModel, navController = navController)
                    HorizontalDivider(thickness = 3.dp, color = Color.Black)
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun LogItem(log: TrainingLog, viewModel: MainViewModel, navController: NavController) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var isExpandedLine by remember { mutableStateOf(false) }
    val clickModifier = Modifier.clickable { isExpandedLine = !isExpandedLine }
    val textFont = FontFamily(
        Font(R.font.zendots_regular),
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("logDetails/${log.logName}") // Assuming each log has a unique `id`.
            },
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .width(200.dp)
            ) {
                Text(
                    log.logName,
                    modifier = clickModifier
                        .animateContentSize(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = textFont,
                    maxLines = if (isExpandedLine) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis // Add ellipsis when text is truncated

                )
            }
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    dateFormat.format(log.logDate),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = textFont,
                    fontWeight = FontWeight(400),
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Black, RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = { viewModel.deleteLog(log.logName) }) {
                        Icon(imageVector = Filled.Close, contentDescription = "Delete", tint = Color.White)
                    }
                }
            }
        }
    }


//    // Toggle visibility of log details
//    if (log.isDetailsVisible) {
//        log.exercises.forEach { exerciseDetail ->
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp),
//                horizontalAlignment = Alignment.Start,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    "Exercise: ${exerciseDetail.exerciseName}",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(2.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        "Sets: ${exerciseDetail.sets}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        "Reps: ${exerciseDetail.reps}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//                Text(
//                    "Weight: ${exerciseDetail.weight}kg",
//                    style = MaterialTheme.typography.bodySmall
//                )
//                HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp)) // Optional: add a divider for clarity
//            }
//        }
//    }
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


@Composable
fun LogDetailsFormLogs(
    workoutPlans: List<WorkoutPlan>,
    viewModel: MainViewModel,
    navController: NavController,
    onLogSaved: () -> Unit,
    goalToUpdate: Goal? = null
) {
    // This will hold all unique exercises across the selected workout plans
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var exerciseDetails = remember { mutableStateListOf<ExerciseDetail>() }
    var isNameDialogVisible by remember { mutableStateOf(false) }
    var logName by remember { mutableStateOf("") }
//    var isAddingNewLog by viewModel.isAddingNewLog.observeAsState(false)

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
    ) {

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
                    "Record for Plan:",
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
                    ExerciseDetailEntryForLogsActivity(detail = detail) { updatedDetail ->
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
                    onClick = {
                        isNameDialogVisible = true
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
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Save"
                    )
                }
                IconButton(
                    onClick = {
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
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Save"
                    )
                }
            }
        }

        var goalToUpdate by remember { mutableStateOf<Goal?>(null) }

        if (isNameDialogVisible) {
            NameLogDialogLogs(
                logName = logName,
                onLogNameChange = { logName = it },
                goalToUpdate = goalToUpdate, // Use a meta selecionada
                onUpdate = { updatedGoal ->
                    // Atualiza a meta
                    viewModel.updateGoal(updatedGoal)
                    viewModel.saveProgressUpdate(updatedGoal.id, updatedGoal.currentValue)
                    Log.d("GoalScreen", "Goal updated: $updatedGoal")
                },
                onConfirm = {
                    // Salva o log
                    val workoutPlanIds =
                        workoutPlans.joinToString(separator = ",") { it.id.toString() }
                    viewModel.saveExerciseLog(logName, workoutPlanIds, exerciseDetails)
                    isNameDialogVisible = false
                    logName = "" // Limpa o nome do log
                    onLogSaved()
                },
                onDismiss = { isNameDialogVisible = false },
            )
        }
    }
}


@Composable
fun NameLogDialogLogs(
    logName: String,
    onLogNameChange: (String) -> Unit,
    goalToUpdate: Goal?,
    onUpdate: (Goal) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val newValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Training Record Name") },
        text = {
            Column {
                TextField(
                    value = logName,
                    onValueChange = onLogNameChange,
                    label = { Text("e.g., Arms Progression") },
                    singleLine = true // Encourages compact input without line breaks
                )
                // Consider adding additional UI elements here if you plan to let users update goals directly from this dialog
            }
        },
        confirmButton = {
            // Ensures the button is only clickable when the log name is not blank.
            // This prevents creating a log without a meaningful name.
            Button(
                onClick = {
                    // Proceed only if logName is filled. Check for goalToUpdate and newValue only if relevant.
                    if (logName.isNotBlank()) {
                        goalToUpdate?.let {
                            // Ensure newValue is valid before proceeding to update the goal.
                            if (newValue.isNotBlank()) {
                                // Assuming newValue is used to update the goal
                                val updatedGoal = it.copy(currentValue = newValue.toInt())
                                onUpdate(updatedGoal) // Update the goal
                            }
                        }
                        onConfirm() // Save the log
                    }
                },
                enabled = logName.isNotBlank() // Button is enabled only if logName is not blank
            ) {
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
fun ExerciseDetailEntryForLogsActivity(
    detail: ExerciseDetail,
    onDetailUpdate: (ExerciseDetail) -> Unit // Callback to handle updates
) {
    var reps by remember { mutableIntStateOf(detail.reps) }
    var sets by remember { mutableIntStateOf(detail.sets) }
    var weightInPicker by remember { mutableIntStateOf(detail.weight) }

    Column(modifier = Modifier.padding(15.dp)) {
        Text(
            text = detail.exerciseName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Compact layout for weight picker and its label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Weight:", style = MaterialTheme.typography.bodyLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${weightInPicker}kg", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(6.dp))
                AndroidView(
                    { context ->
                        NumberPicker(context).apply {
                            minValue = 1
                            maxValue = 200
                            value = weightInPicker
                            wrapSelectorWheel = true
                            setOnValueChangedListener { _, _, newVal ->
                                weightInPicker = newVal
                                onDetailUpdate(detail.copy(weight = newVal.toInt()))
                            }
                        }
                    },
                    modifier = Modifier.height(48.dp) // Adjust the height to make the picker more compact
                )
            }
        }
        DetailSliderForLogs(
            label = "Sets",
            value = sets.toFloat(),
            range = 1f..10f,
            onValueChange = { newValue ->
                val newSets = newValue.roundToInt()
                sets = newSets
                onDetailUpdate(detail.copy(sets = newSets))
            }
        )

        DetailSliderForLogs(
            label = "Reps",
            value = reps.toFloat(),
            range = 1f..20f,
            onValueChange = { newValue ->
                val newReps = newValue.roundToInt()
                reps = newReps
                onDetailUpdate(detail.copy(reps = newReps))
            }
        )
    }
}

@Composable
fun DetailSliderForLogs(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("$label: ${value.roundToInt()}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = range.endInclusive.toInt() - range.start.toInt() - 1, // Define steps to match the integer range
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
    }
}
