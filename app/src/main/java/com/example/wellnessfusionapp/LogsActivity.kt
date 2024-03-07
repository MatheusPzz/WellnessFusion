package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseLog
import com.example.wellnessfusionapp.Models.WorkoutPlan
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

    LaunchedEffect(key1 = true) {
        viewModel.fetchUserWorkoutPlans()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Logs, Keep Track Of Your Progression") },
                actions = {

                }
            )
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedWorkoutPlan != null) {
                        // Quando um plano de treino é selecionado, exibe o formulário de detalhes do log
                        LogDetailsForm(
                            workoutPlan = selectedWorkoutPlan!!,
                            viewModel = viewModel,
                            onLogSaved = {
                                selectedWorkoutPlan = null
                            })
                    } else {
                        // Se nenhum plano estiver selecionado, exibe um texto informativo
                        Text(
                            "Here you Select a Workout Plan to Log Your Progression.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    SavedLogsList(viewModel = viewModel)
                }
            }
        }


        // A verificação showDialog.value agora é feita aqui, fora do Scaffold
        if (showDialog.value) {
            SelectWorkoutPlanDialog(
                mainViewModel = viewModel,
                onPlanSelected = { workoutPlan ->
                    selectedWorkoutPlan = workoutPlan
                    showDialog.value = false // Fecha o diálogo após a seleção
                },
                onDismiss = { showDialog.value = false } // Fecha o diálogo se o usuário cancelar
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
                        Text(
                            text = workout.planName,
                            modifier = Modifier.clickable { onPlanSelected(workout) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun LogDetailsForm(workoutPlan: WorkoutPlan, viewModel: MainViewModel, onLogSaved: () -> Unit) {
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var exerciseLogs = remember { mutableStateListOf<ExerciseLog>() }

    var isNameDialogVisible by remember { mutableStateOf(false) }
    var logName by remember { mutableStateOf("") }

    // Busca os detalhes dos exercícios quando o composable é montado
    LaunchedEffect(workoutPlan.exercises) {
        viewModel.fetchExercisesDetailsByIds(workoutPlan.exercises) { fetchedExercises ->
            exercises = fetchedExercises
            // Inicializa os logs com valores padrão para cada exercício
            exerciseLogs.clear()
            fetchedExercises.forEach { exercise ->
                exerciseLogs.add(ExerciseLog("", logDate = java.util.Date(), exercise.id, exercise.name, 0, 0, 0f, false))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            "Log Details For: ${workoutPlan.planName}",
            style = MaterialTheme.typography.titleLarge
        )

        exercises.forEachIndexed { index, exercise ->
            ExerciseLogEntry(exercise = exercise, exerciseLog = exerciseLogs[index]) { updatedLog ->
                // Atualiza o log específico baseado em mudanças no UI
                exerciseLogs[index] = updatedLog
            }
        }

        Button(onClick = {
            isNameDialogVisible = true
        }) {
            Text("Save Log")
        }
    }
    if (isNameDialogVisible) {
        AlertDialog(
            onDismissRequest = { isNameDialogVisible = false },
            title = { Text("Name Your Log") },
            text = {
                TextField(
                    value = logName,
                    onValueChange = { logName = it },
                    label = { Text("Log Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    // Agora, salva o log com o nome fornecido quando o botão dentro do diálogo é pressionado
                    if (logName.isNotBlank()) {
                        viewModel.saveExerciseLog(logName, workoutPlan.id, exerciseLogs.toList())
                        isNameDialogVisible = false
                        logName = "" // Limpar o nome do log para o próximo uso
                        onLogSaved()
                    } else {
                        // Adicionar algum feedback para o usuário se o nome estiver vazio
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { isNameDialogVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExerciseLogEntry(
    exercise: Exercise,
    exerciseLog: ExerciseLog,
    onLogUpdate: (ExerciseLog) -> Unit
) {
    var reps by remember { mutableStateOf(exerciseLog.reps.toFloat()) }
    var sets by remember { mutableStateOf(exerciseLog.sets.toFloat()) }
    var weightText by remember { mutableStateOf(exerciseLog.weight.toString()) }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(exercise.name, style = MaterialTheme.typography.headlineLarge)
            TextField(
                value = weightText,
                onValueChange = { newValue ->
                    weightText = newValue.filter { it.isDigit() || it == '.' }
                    weightText.toFloatOrNull()?.let {
                        onLogUpdate(exerciseLog.copy(weight = it))
                    }
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
            Text("Sets ${sets.toInt()}")
            Slider(value = sets, onValueChange = { newValue ->
                sets = newValue
                onLogUpdate(exerciseLog.copy(sets = sets.toInt()))
            }, valueRange = 1f..10f, modifier = Modifier.padding(vertical = 8.dp))

            Text("Reps ${reps.toInt()}")
            Slider(value = reps, onValueChange = { newValue ->
                reps = newValue
                onLogUpdate(exerciseLog.copy(reps = reps.toInt()))
            }, valueRange = 1f..20f, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun SavedLogsList(viewModel: MainViewModel) {
    val savedLogs by viewModel.savedLogs.observeAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(savedLogs) { log ->
            LogItem(log, onLogClicked = {
                viewModel.toggleLogDetails(log.logName) // Certifique-se de que você tem um ID único para cada log
            })
        }
    }
}


@Composable
fun LogItem(log: ExerciseLog, onLogClicked: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Row(modifier = Modifier
        .clickable { onLogClicked() }
        .padding(all = 8.dp) // Adiciona padding à Row para um design mais limpo
        .fillMaxWidth(), // Certifica que a Row ocupa a largura máxima disponível
        horizontalArrangement = Arrangement.SpaceBetween // Espaça os elementos uniformemente
    ) {
        Text(log.logName, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.width(16.dp)) // Espaço entre o nome do log e a data

        Text(dateFormat.format(log.logDate), style = MaterialTheme.typography.bodyMedium)
    }
    if (log.isDetailsVisible) {
        // Mostra os detalhes deste exercício
        Text("Exercise: ${log.exerciseName}", style = MaterialTheme.typography.bodySmall)
        Text("Sets: ${log.sets}", style = MaterialTheme.typography.bodySmall)
        Text("Reps: ${log.reps}", style = MaterialTheme.typography.bodySmall)
        Text("Weight: ${log.weight}kg", style = MaterialTheme.typography.bodySmall)
    }
}
