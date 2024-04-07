package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale


/*
 Defining a composable function to display details of a training record in the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetails(log: TrainingLog, navController: NavController, viewModel: MainViewModel) {

    // Defining a value to format the date of the training record
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Define a more dynamic background color based on the app theme
    val physicalColor = Color(0xffFE7316)
    val mentalColor = Color(0xff1666ba)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.Black),
                title = {
                    Text(
                        "Record Details",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
    ) { paddingValues ->
        // Main Container Box with a semi-transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            HorizontalDivider(color = Color.White, thickness = 3.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    log.exercises.forEach { exerciseDetail ->
                        ExerciseDetailCard(exerciseDetail, dateFormat, log)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                }

            }
        }
    }
}

@Composable
fun ExerciseDetailCard(
    exerciseDetail: ExerciseDetail,
    dateFormat: SimpleDateFormat,
    log: TrainingLog,
) {
    val textFont = FontFamily(
        Font(R.font.zendots_regular)
    )
    val physicalColor = Color(0xffFE7316)
    val mentalColor = Color(0xff1666ba)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, physicalColor, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Date: ",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                fontFamily = textFont
            )
            Text(
                dateFormat.format(log.logDate),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                fontFamily = textFont
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(thickness = 3.dp, color = physicalColor)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Exercise: ${exerciseDetail.exerciseName}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    fontFamily = textFont
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "Sets: ${exerciseDetail.sets}",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                fontFamily = textFont
            )
            Text(
                "Reps: ${exerciseDetail.reps}",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                fontFamily = textFont
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if(exerciseDetail.exerciseId.toInt() >= 12 ) "Duration(min)${exerciseDetail.weight}" else "Weight(KG) ${exerciseDetail.weight}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    fontFamily = textFont
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(physicalColor, CircleShape)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center),
                        imageVector = Icons.Default.Star,
                        contentDescription = "Back",
                        tint = Color.Yellow,
                    )
                }
            }
//            } else if(exerciseDetail.exerciseId <= "12"){
//                Spacer(modifier = Modifier.height(5.dp))
//                Text(
//                    "Sets: ${exerciseDetail.sets}",
//                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
//                    fontFamily = textFont
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Text(
//                    "Reps: ${exerciseDetail.reps}",
//                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
//                    fontFamily = textFont
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Weight: ${exerciseDetail.weight}kg",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
//                        fontFamily = textFont
//                    )
//                    Box(
//                        modifier = Modifier
//                            .size(30.dp)
//                            .background(physicalColor, CircleShape)
//                    ) {
//                        Icon(
//                            modifier = Modifier
//                                .size(20.dp)
//                                .align(Alignment.Center),
//                            imageVector = Icons.Default.Star,
//                            contentDescription = "Back",
//                            tint = Color.Yellow,
//                        )
//                    }
//                }
//            }
//        }

//    }
        }
    }
}
