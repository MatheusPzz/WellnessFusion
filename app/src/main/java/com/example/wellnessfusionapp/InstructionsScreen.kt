package com.example.wellnessfusionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InstructionScreen(exerciseId: String, viewModel: MainViewModel, navController: NavController) {
    val pagerState = rememberPagerState()
    val instructions = viewModel.instructions.observeAsState()

    LaunchedEffect(exerciseId) {
        viewModel.fetchInstructionsForExercise(exerciseId)
    }

    instructions.value?.let { instruction ->
        Column(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxSize()
        ) {
            // Nome do exercício e imagem
            Text(
                text = instruction.exerciseName,
                modifier = Modifier.padding(15.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 5.dp))
            AsyncImage(
                model = instruction.imageUrl,
                contentDescription = "Exercise Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier =  Modifier.height(50.dp) )

            // HorizontalPager para detalhes adicionais
            HorizontalPager(
                count = 3, // Total de páginas
                state = pagerState,
                modifier = Modifier
                    .padding(25.dp)
                    .height(300.dp)
            ) { page ->
                when (page) {
                    0 ->
                        Column(modifier = Modifier
                            .padding(15.dp)
                            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start  ) {
                            Text("Muscles worked:")
                            Text(instruction.musclesWorked)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
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

                        Column(modifier = Modifier
                            .padding(0.dp)
                            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start  ) {
                            Text("Instructions:")
                            Text(instruction.instructions)
                        }
                    2 -> {
                        Column( modifier = Modifier
                            .padding(0.dp)
                            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start){
                            Text("Sets: ${instruction.sets}")
                            Text("Reps: ${instruction.reps}")
                            Spacer(modifier = Modifier.height(30.dp))
                            Text("Extra Content: ${instruction.videoUrl}")
                        }
                    }
                }
            }
        }
    } ?: Text("Loading...", Modifier.padding(16.dp)) // Fallback para quando as instruções ainda estão sendo carregadas
}