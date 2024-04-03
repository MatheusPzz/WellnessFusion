package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LogDetails(log: TrainingLog, navController: NavController, viewModel: MainViewModel) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Define a more dynamic background color based on the app theme
    val backgroundColor = Color.Black.copy(alpha = 0.1f)
    val accentColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val dividerColor = accentColor.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(56.dp)
                    .background(accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                "Details",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            log.exercises.forEach { exerciseDetail ->
                ExerciseDetailCard(exerciseDetail, dateFormat, log, textColor, dividerColor)
            }
        }
    }
}

@Composable
fun ExerciseDetailCard(exerciseDetail: ExerciseDetail, dateFormat: SimpleDateFormat, log: TrainingLog, textColor: Color, dividerColor: Color) {

    val textFont = FontFamily(
        Font(R.font.zendots_regular)

    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            dateFormat.format(log.logDate),
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            fontFamily = textFont
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                Color.White
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Exercise: ${exerciseDetail.exerciseName}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                    fontFamily = textFont
                )
            }

            Text(
                "Sets: ${exerciseDetail.sets}",
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                fontFamily = textFont
            )
            Text(
                "Reps: ${exerciseDetail.reps}",
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                fontFamily = textFont
            )
            Text(
                "Weight: ${exerciseDetail.weight}kg",
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                fontFamily = textFont
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(
        color = dividerColor,
        thickness = 2.dp,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}