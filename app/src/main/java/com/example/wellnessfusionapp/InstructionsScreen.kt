package com.example.wellnessfusionapp

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(exerciseId: String, viewModel: MainViewModel, navController: NavController) {
    val pagerState = rememberPagerState()
    val instructions = viewModel.instructions.observeAsState()

    LaunchedEffect(exerciseId) {
        viewModel.fetchInstructionsForExercise(exerciseId)
        viewModel.fetchNotesForUser(exerciseId)
    }

    instructions.value?.let { instruction ->

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = instruction.exerciseName)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }, bottomBar = {

            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = instruction.imageUrl,
                    contentDescription = "Exercise Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.FillWidth
                )


                // HorizontalPager para detalhes adicionais
                HorizontalPager(
                    count = 3, // Total de páginas
                    state = pagerState,
                    modifier = Modifier
                        .height(250.dp)
                ) { page ->
                    when (page) {
                        0 ->
                            Column(
                                modifier = Modifier
                                    .padding(15.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("Muscles worked:")
                                Text(instruction.musclesWorked)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    AsyncImage(
                                        model = instruction.imageUrl,
                                        contentDescription = "Exercise Image",
                                        modifier = Modifier
                                            .width(150.dp)
                                            .height(250.dp),
                                    )
                                    AsyncImage(
                                        model = instruction.imageUrl,
                                        contentDescription = "Exercise Image",
                                        modifier = Modifier
                                            .width(150.dp)
                                            .height(250.dp),
                                    )
                                }


                            }

                        1 ->

                            Column(
                                modifier = Modifier
                                    .padding(0.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("Instructions:")
                                Text(instruction.instructions)
                            }

                        2 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceAround)
                                {
                                    Text("Sets: ${instruction.sets}")
                                    Text("Reps: ${instruction.reps}")
                                }
                                Column(modifier = Modifier
                                    .padding(0.dp)
                                    .fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally)
                                {
                                    Text("Extra Content: ${instruction.videoUrl}")
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start
                ) {
                    Text("Notes:")
                    NotesSection(viewModel, exerciseId)
                }
            }
        }
    }
}

// function to write personalized notes for user in each exercise

@Composable
fun NotesSection(viewModel: MainViewModel, exerciseId: String) {
    val notes by viewModel.notes.observeAsState(initial = emptyList())
    var noteText by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Add your notes here") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            viewModel.saveNotesForUser(exerciseId, noteText)
            noteText = "" // Limpar o campo de texto
        }, modifier = Modifier
            .width(120.dp)
            .height(50.dp)) {
            Text("Save Note")
        }

        Spacer(modifier = Modifier.height(15.dp))
        // Exibir notas existentes
        LazyColumn {
            this.items(notes) { note ->
                Text(note.noteText)
                // Adicione mais detalhes conforme necessário
            }
        }
    }
}