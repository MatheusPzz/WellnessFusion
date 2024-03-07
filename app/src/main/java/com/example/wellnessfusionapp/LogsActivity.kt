package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.test.services.events.TimeStamp
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
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
) {
    val showDialog = remember { mutableStateOf(false) }
    var selectedWorkoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    val isAddingNewLog by viewModel.isAddingNewLog.observeAsState(false)

    LaunchedEffect(key1 = true) {
        viewModel.fetchUserWorkoutPlans()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Progression Logs") },
                actions = {
                    if (!isAddingNewLog) {
                        IconButton(onClick = { showDialog.value = true }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isAddingNewLog) {
                    // Exibir a lista de logs salvos apenas se não estiver adicionando um novo log
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        SavedLogsList(viewModel = viewModel)
                    }
                }

                // Controle para iniciar a adição de um novo log ou mostrar o formulário de detalhes do log selecionado
                if (selectedWorkoutPlan != null && isAddingNewLog) {
                    // Quando um plano de treino é selecionado e está adicionando um novo log, exibe o formulário de detalhes do log
                    LogDetailsForm(
                        workoutPlan = selectedWorkoutPlan!!,
                        viewModel = viewModel,
                        onLogSaved = {
                            selectedWorkoutPlan = null
                            viewModel.finishAddingNewLog()
                        })
                } else if (!isAddingNewLog) {
                    // Se nenhum plano estiver selecionado e não estiver adicionando um novo log, exibe um texto informativo
                    Text(
                        "Here you Select a Workout Plan to Log Your Progression.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Dialog para selecionar o plano de treino, visível apenas se não estiver no processo de adicionar um novo log
        if (showDialog.value && !isAddingNewLog) {
            SelectWorkoutPlanDialog(
                mainViewModel = viewModel,
                onPlanSelected = { workoutPlan ->
                    selectedWorkoutPlan = workoutPlan
                    viewModel.startAddingNewLog()
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }
    }
}

@Composable
fun SelectWorkoutPlanDialog(
    mainViewModel: MainViewModel,
    onPlanSelected: (WorkoutPlan) -> Unit,
    onDismiss: () -> Unit
) {
    // Suponha que mainViewModel.savedWorkouts seja um LiveData ou StateFlow que contém os planos de treino salvos
    val savedWorkouts by mainViewModel.workoutPlans.observeAsState(initial = listOf())
    Log.d("WorkoutPlans", "Saved workouts: $savedWorkouts")

    if (savedWorkouts.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Selecione um Plano de Treino") },
            text = {
                LazyColumn {
                    items(savedWorkouts) { workout ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(10.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .clickable { onPlanSelected(workout) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(workout.planName, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Exercises: ${workout.exercises.size}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LogDetailsForm(
    workoutPlan: WorkoutPlan,
    viewModel: MainViewModel,
    onLogSaved: () -> Unit
) {
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var exerciseDetails = remember { mutableStateListOf<ExerciseDetail>() }
    var isNameDialogVisible by remember { mutableStateOf(false) }
    var logName by remember { mutableStateOf("") }

    LaunchedEffect(workoutPlan.exercises) {
        viewModel.fetchExercisesDetailsByIds(workoutPlan.exercises) { fetchedExercises ->
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Log Details For",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                workoutPlan.planName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        Divider() // Adiciona um divisor entre os itens para melhor visualização

        // Use LazyColumn for the list of exercises, agora com o modificador weight
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { isNameDialogVisible = true },
                modifier = Modifier
                    .padding(0.dp)
                    .width(40.dp)
                    .height(40.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp), imageVector = Icons.Filled.Done, contentDescription = "Save"
                )
            }
            IconButton(
                onClick = { onLogSaved() },
                modifier = Modifier
                    .padding(0.dp)
                    .width(40.dp)
                    .height(40.dp)
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

    if (isNameDialogVisible) {
        NameLogDialog(
            logName = logName,
            onLogNameChange = { logName = it },
            onConfirm = {
                if (logName.isNotBlank()) {
                    viewModel.saveExerciseLog(logName, workoutPlan.id, exerciseDetails)
                    isNameDialogVisible = false
                    logName = "" // Limpar o nome do log para o próximo uso
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
                .padding(0.dp)
                .fillMaxWidth()
        ) {
            Text("Sets: ${sets.toInt()}")
            Slider(value = sets, onValueChange = { newValue ->
                sets = newValue
                onDetailUpdate(detail.copy(sets = sets.toInt()))
            }, valueRange = 1f..10f, modifier = Modifier.padding(vertical = 8.dp))

            Text("Reps: ${reps.toInt()}")
            Slider(value = reps, onValueChange = { newValue ->
                reps = newValue
                onDetailUpdate(detail.copy(reps = reps.toInt()))
            }, valueRange = 1f..20f, modifier = Modifier.padding(vertical = 8.dp))
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
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(log.logName, style = MaterialTheme.typography.headlineLarge)
            Text(dateFormat.format(log.logDate), style = MaterialTheme.typography.bodyMedium)
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
