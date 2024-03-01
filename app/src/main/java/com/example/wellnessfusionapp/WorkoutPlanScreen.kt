package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
fun WorkoutsScreen(viewModel: GeneratedWorkoutViewModel, navController: NavController) {
    val savedWorkouts by viewModel.savedWorkouts.observeAsState(listOf())

    LaunchedEffect(Unit) {
        viewModel.fetchSavedWorkouts()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Workouts") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
        ) {
            items(savedWorkouts) { workout ->
                WorkoutCard(workout, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun WorkoutCard(workout: WorkoutPlan, viewModel: GeneratedWorkoutViewModel) {
    val exercisesDetails by viewModel.exercisesDetails.observeAsState(emptyList())

    LaunchedEffect(workout.exercises) {
        viewModel.fetchExercisesDetails(workout.exercises)
    }

    Card(modifier = Modifier.padding(10.dp)) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = workout.planName, style = MaterialTheme.typography.titleLarge)
            Divider(modifier = Modifier.padding(vertical = 15.dp))

            // Usando HorizontalPager para mostrar cada exercício em sua própria página
            val pagerState = rememberPagerState()
            HorizontalPager(
                count = exercisesDetails.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Altura do pager
            ) { page ->
                val exercise = exercisesDetails[page]
                ExercisePage(exercise)
            }
        }
    }
}

@Composable
fun ExercisePage(exercise: Exercise) {
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
                .padding(15.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text(text = "Nome: ${exercise.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Series: ${exercise.sets}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Reps: ${exercise.reps}", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
            {
                Column(modifier = Modifier.width(250.dp).fillMaxHeight()) {
                    Text(
                        text = "Descrição: ${exercise.description}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(modifier = Modifier.width(100.dp).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    IconButton(modifier = Modifier
                        .padding(start = 0.dp).size(100.dp),
                        onClick = { /*TODO*/ }) {
                        Icon(modifier = Modifier
                            .size(80.dp)    ,
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play Exercise"

                        )
                    }
                }
            }
        }


    }
}