package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWorkoutsScreen(viewModel: GeneratedWorkoutViewModel, navController: NavController) {
    val savedWorkouts by viewModel.savedWorkouts.observeAsState(listOf())

    LaunchedEffect(Unit) {
        viewModel.fetchSavedWorkouts()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Row(
                        modifier = Modifier.width(200.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text("Workout Plans")
                    }
                    ButtonPhysical(navController)
                    MentalButton(navController)
                }
            })
        },
        bottomBar = { BottomNavBar(navController) }

    ) { paddingValues ->
        if (savedWorkouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No saved workout plans", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues)

            )
            {
                items(savedWorkouts) { workout ->
                    WorkoutCard(workout, viewModel, navController)
                }
            }
        }
    }
}


@Composable
fun EditButton(workoutPlanId: String, currentName: String, viewModel: GeneratedWorkoutViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(currentName) }
    val exercises by remember { mutableStateOf(emptyList<Exercise>()) }


    IconButton(onClick = { showDialog = true }) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Plan")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Workout Plan Name") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) {
                        viewModel.updateWorkoutPlanName(workoutPlanId, newName)
                        showDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DeleteButton(workoutPlanId: String, viewModel: GeneratedWorkoutViewModel) {
    IconButton(onClick = { viewModel.deleteWorkoutPlan(workoutPlanId) }) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Workout Plan")
    }
}

@Composable
fun PlayButton(navController: NavController, workout: WorkoutPlan) {
    IconButton(onClick = { navController.navigate("createdPlans/${workout.workoutPlanId}") }) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Workout Plan")
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun WorkoutCard(
    workout: WorkoutPlan,
    viewModel: GeneratedWorkoutViewModel,
    navController: NavController
) {
    var exercisesDetails by remember { mutableStateOf<List<Exercise>>(listOf()) }

    LaunchedEffect(workout.exercises) {
        viewModel.fetchExercisesDetails(workout.exercises) { exercises ->
            exercisesDetails = exercises
        }
    }
    //
    Card(modifier = Modifier
        .padding(16.dp)
        .clickable { }) {
        Column(modifier = Modifier.padding(0.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = workout.planName, style = MaterialTheme.typography.titleLarge)
                }
                PlayButton(navController, workout)
                EditButton(workout.workoutPlanId, workout.planName, viewModel)
                DeleteButton(workout.workoutPlanId, viewModel)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            val pagerState = rememberPagerState()
            HorizontalPager(
                count = exercisesDetails.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) { page ->
                val exercise = exercisesDetails[page]
                ExercisePage(exercise, navController)
            }
        }
    }
}

@Composable
fun ExercisePage(exercise: Exercise, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        AsyncImage(
            model = exercise.imageUrl,
            contentDescription = "Exercise Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {

            Column(

            ) {
                Text(text = "Nome: ${exercise.name}", style = MaterialTheme.typography.bodyLarge)
            }
            Row(
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Descrição: ${exercise.description}",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
            Column(

            ) {
                Text("Workout Counter: 0")
            }
        }
    }
}
// column out and row out

@Composable
fun ButtonPhysical(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.7f
                    }
                    .background(Color(0xffFE7316))
            )
            Image(
                painter = painterResource(id = R.drawable.physical),
                contentDescription = "Physical",
                modifier = Modifier
                    .height(120.dp) // Adjust size as needed
                    .clickable(onClick = { navController.navigate("physicalCategory") }
                    ))

        }
    }
}

@Composable
fun MentalButton(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.9f
                    }
                    .background(Color(0xff1666ba))
            )
            Image(
                painter = painterResource(id = R.drawable.mental),
                contentDescription = "Mental",
                modifier = Modifier
                    .height(120.dp) // Adjust size as needed
                    .clickable(onClick = { navController.navigate("zenCategory") }
                    ))
        }
    }
}



