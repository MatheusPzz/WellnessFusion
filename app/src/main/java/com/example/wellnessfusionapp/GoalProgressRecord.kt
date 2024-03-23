package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.ProgressRecord
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalProgressRecordScreen(viewModel: MainViewModel, goalId: String, exerciseId: String, navController: NavController) {
    val progressHistory by viewModel.progressRecords.collectAsState()
    val goal by viewModel.goalDetails.observeAsState()

    LaunchedEffect(key1 = goalId) {
        viewModel.fetchProgressHistory(goalId)
    }

    LaunchedEffect(key1 = goalId) {
        viewModel.fetchGoalDetails(goalId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { paddingValues ->
        goal?.let { goal ->
            Column(
                modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = androidx.compose.ui.Alignment.Start,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(goal.typeId, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Exercise Name: ${goal.description}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Current: ${goal.currentValue}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Goal: ${goal.desiredValue}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                HorizontalDivider()

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Progress Chart")
                    GoalProgressChart(progressHistory, goal.startDate)
                }

                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                ) {
                    item {
                        Text("Exercise Specific Logs", style = MaterialTheme.typography.bodyMedium)
                    }
                    item {
                        ExerciseSpecificLogsList(viewModel, exerciseId)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalProgressChart(progressData: List<ProgressRecord>, goalStartDate: Timestamp) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                val startDateMillis = goalStartDate.seconds * 1000
                // Calculate the end date as start date + 30 days in milliseconds
                val endDateMillis = startDateMillis + TimeUnit.DAYS.toMillis(30)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(true)
                xAxis.granularity = 1f
                // Set the XAxis range to cover 30 days from the goal start date
                xAxis.axisMinimum = startDateMillis.toFloat()
                xAxis.axisMaximum = endDateMillis.toFloat()
                xAxis.valueFormatter = CustomDateFormatter()

                axisLeft.isEnabled = true
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            // Convert progress data into chart entries
            val entries = progressData.map { Entry(it.date.seconds.toFloat() * 1000, it.value.toFloat()) }

            // Ensure the entries list starts from the goal start date with value 0
            val startEntry = Entry(goalStartDate.seconds.toFloat() * 1000, 0f)
            val allEntries = mutableListOf(startEntry).apply {
                addAll(entries)
            }

            val lineDataSet = LineDataSet(entries, "Progress").apply {
                color = Color(0xFF6200EE).toArgb() // Replace with your primary color value
                valueTextColor = Color(0xFF000000).toArgb() // Replace with your onSurface color value
                lineWidth = 2.5f
                setCircleColor(Color(0xFF03DAC5).toArgb()) // Replace with your secondary color value
                circleRadius = 5f
                setDrawCircleHole(true)
                circleHoleColor = Color(0xFFFFFFFF).toArgb() // Replace with your background color value
                setDrawValues(false)
                mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(context, R.color.purple_500) // Custom gradient
            }

            // Apply dataset to chart
            chart.data = LineData(lineDataSet)
            chart.animateX(1500)
            chart.setVisibleXRangeMaximum(TimeUnit.DAYS.toMillis(30).toFloat()) // Set maximum visible range to 30 days
            chart.moveViewToX(goalStartDate.seconds.toFloat() * 1000) // Move the view to the goal start date
            chart.invalidate()
        }
    )
}

class CustomDateFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        // Convert the Float value back to milliseconds for formatting
        return dateFormat.format(Date(value.toLong()))
    }
}
@Composable
fun ExerciseSpecificLogsList(viewModel: MainViewModel, exerciseId: String) {
    val exerciseSpecificLogs by viewModel.exerciseSpecificLogs.observeAsState(initial = emptyList())


    LaunchedEffect(key1 = exerciseId) {
        viewModel.fetchLogsForExerciseGoals(exerciseId)
        Log.d("ExerciseSpecificLogs", "Fetching logs for exercise $exerciseId")
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)) {
        exerciseSpecificLogs.forEach { log ->
            Log.d("ExerciseSpecificLogs", log.toString())
            LogItem(log = log, exerciseId = exerciseId)
            Divider()
        }
    }
}



@Composable
fun LogItem(log: TrainingLog, exerciseId: String, padding: PaddingValues = PaddingValues(8.dp)) {
    val exerciseDetail = log.exercises.find { it.exerciseId == exerciseId }
    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    exerciseDetail?.let { detail ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "Data: ${dateFormat.format(log.logDate)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Exercício: ${detail.exerciseName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Carga: ${detail.weight} kg", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Repetições: ${detail.reps}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sets: ${detail.sets}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

