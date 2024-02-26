package com.example.wellnessfusionapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanScreen(navController: NavController, viewModel: ExerciseSelectionViewModel) {

    val selectedExercises by viewModel.selectedExercises.collectAsState()

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Here is your Generated Plan:") },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp),
                    )
                    Text("Refresh")
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = paddingValues
        ) {
            items(selectedExercises) { exercise ->
                Card(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(0.dp)
                                .width(220.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = exercise.imageUrl,
                                contentDescription = "Exercise Image",
                                modifier = Modifier.size(220.dp)
                            )
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(top =5.dp)
                                    .padding(start = 10.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth()
                                .height(250.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Reps: ${exercise.reps}")
                            Text(text = "Sets: ${exercise.sets}")
                            Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom){
                                TextButton(onClick = { }, modifier = Modifier.size(100.dp)) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Exercise Info",
                                        modifier = Modifier

                                            .size(80.dp),
                                    )
                                }
                            }
                        }
                        // Add Image, Video, or any other components here
                    }
                    Row {
                        Text(
                            text = "Description: ${exercise.description}", modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                }
            }

        }
    }
}
