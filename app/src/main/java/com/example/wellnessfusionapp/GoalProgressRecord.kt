package com.example.wellnessfusionapp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.ProgressRecord
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalProgressRecordScreen(viewModel: MainViewModel, goalId: String, exerciseId: String, navController: NavController) {
    val progressHistory by viewModel.progressRecords.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = goalId) {
        coroutineScope.launch {
            viewModel.fetchProgressHistory(goalId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text("Progression Chart", modifier = Modifier.padding(10.dp))
            if (progressHistory.isNotEmpty()) {
                GoalProgressChart(progressData = progressHistory, viewModel = viewModel)
            } else {
                Text("No progress data available.", modifier = Modifier.padding(10.dp))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            Text("Exercise Specific Logs", modifier = Modifier.padding(10.dp))
            ExerciseSpecificLogsList(viewModel, exerciseId)
        }
    }
}

@Composable
fun GoalProgressChart(progressData: List<ProgressRecord>, viewModel: MainViewModel) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        factory = { context ->
            LineChart(context).apply {
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry, h: Highlight?) {
                        val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(e.x.toLong() * 1000))
                        Toast.makeText(context, "Data: $selectedDate, Carga: ${e.y}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected() {}
                })

                description.text = "Carga ao Longo do Tempo"
                description.isEnabled = true
                legend.isEnabled = true

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(progressData.map { record ->
                        SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(record.date.seconds * 1000))
                    })
                }

                axisLeft.apply {
                    isEnabled = true
                    setDrawGridLines(true)
                    granularity = 1f
                }

                axisRight.isEnabled = false

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
            }
        },
        update = { chart ->
            val entries = progressData.map { progressRecord ->
                Entry(progressRecord.date.seconds.toFloat(), progressRecord.value.toFloat())
            }

            val lineDataSet = LineDataSet(entries, "Progresso de Carga").apply {
                color = androidx.compose.ui.graphics.Color.Blue.toArgb()
                valueTextColor = androidx.compose.ui.graphics.Color.Black.toArgb()
                lineWidth = 2.5f
                setCircleColor(androidx.compose.ui.graphics.Color.Blue.toArgb())
                circleRadius = 5f
                setDrawValues(true)
                setDrawFilled(true)
                fillColor = androidx.compose.ui.graphics.Color.Blue.toArgb()
            }

            chart.data = LineData(lineDataSet)
            chart.animateX(1000)
            chart.invalidate()
        }
    )
}

@Composable
fun ExerciseSpecificLogsList(viewModel: MainViewModel, exerciseId: String) {
    val exerciseSpecificLogs by viewModel.exerciseSpecificLogs.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxWidth()) {
        exerciseSpecificLogs.forEach { log ->
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

