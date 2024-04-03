package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.anychart.APIlib
import java.util.*
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.annotations.Line
import com.anychart.enums.Anchor
import com.anychart.enums.TooltipPositionMode
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalProgressRecordScreen(
    viewModel: MainViewModel,
    goalId: String,
    exerciseId: String,
    navController: NavController
) {
    val progressHistory by viewModel.progressRecords.collectAsState()
    val goal by viewModel.goalDetails.observeAsState()
    val textFont = FontFamily(Font(R.font.zendots_regular))
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = goalId) {
        viewModel.fetchProgressHistory(goalId)
        Log.d("GoalProgressRecordScreen", "Fetching progress history for goal $goalId")
        viewModel.fetchGoalDetails(goalId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            goal.typeId,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = textFont
                        )
                        Text(
                            "Exercise Name: ${goal.description}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = textFont
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Current: ${goal.currentValue} Kg",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = textFont
                            )
                            Text(
                                "Goal: ${goal.desiredValue} Kg",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = textFont
                            )
                        }
                    }
                }
                HorizontalDivider()

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Progress Chart")
                    GoalProgressChart(progressHistory, goal.startDate, snackbarHostState, scope)
                }
            }

            HorizontalDivider()
        }
    }
}


@Composable
fun GoalProgressChart(
    progressData: List<ProgressRecord>,
    goalStartDate: Timestamp,
    snackbarHostState: SnackbarHostState, // Correct parameter to SnackbarHostState
    scope: CoroutineScope // Pass CoroutineScope to manage Snackbars
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = CustomDateFormatter(goalStartDate)
                }

                axisLeft.apply {
                    isEnabled = true
                    axisMinimum = 0f
                }

                axisRight.isEnabled = false

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { entry ->
                            val progressValue = entry.y
                            val progressDate = CustomDateFormatter(goalStartDate).getFormattedValue(entry.x)
                            val snackbarText = "Progress on $progressDate: $progressValue kg"

                            scope.launch {
                                snackbarHostState.showSnackbar(snackbarText)
                            }
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },

        update = { chart ->
            // Assuming goalStartDate is the start point for your progress and should be considered as 'day zero'
            val referenceTimestamp = goalStartDate.seconds * 1000 // Convert to milliseconds

            // Transform progress data to chart entries
            val entries = progressData.map { progressRecord ->
                // Calculate the difference in days from the goal start date to the record date
                val daysFromStart = TimeUnit.MILLISECONDS.toDays(progressRecord.date.seconds * 1000 - referenceTimestamp).toFloat()
                Entry(daysFromStart, progressRecord.value.toFloat())
            }.toMutableList()

            // Add a starting point at 'day zero' with a value of 0
            entries.add(0, Entry(0f, 0f))

            val lineDataSet = LineDataSet(entries, "Progress").apply {
                color = Color(0xFF6200EE).toArgb()
                valueTextColor = Color(0xFF000000).toArgb()
                lineWidth = 2.5f
                setCircleColor(Color(0xFF03DAC5).toArgb())
                circleRadius = 5f
                setDrawCircleHole(false)
                setDrawValues(false)
                mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(context, R.color.purple_200)
            }

            chart.data = LineData(lineDataSet)

            // Optionally, adjust the X-axis maximum to ensure all data is visible, including the last entry with some padding
            val lastEntryDay = entries.maxOfOrNull { it.x } ?: 0f
            chart.xAxis.axisMaximum = lastEntryDay + 1 // Add a small padding

            chart.invalidate()
        }

    )
}

class CustomDateFormatter(private val goalStartDate: Timestamp) : ValueFormatter() {
    private val calendar = Calendar.getInstance()

    override fun getFormattedValue(value: Float): String {
        calendar.timeInMillis = goalStartDate.seconds * 1000
        calendar.add(Calendar.DAY_OF_YEAR, value.toInt()) // Add the number of days (value) to the start date
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
@Composable
fun ExerciseSpecificLogsList(viewModel: MainViewModel, exerciseId: String) {
    val exerciseSpecificLogs by viewModel.exerciseSpecificLogs.observeAsState(initial = emptyList())


    LaunchedEffect(key1 = exerciseId) {
        viewModel.fetchLogsForExerciseGoals(exerciseId)
        Log.d("ExerciseSpecificLogs", "Fetching logs for exercise $exerciseId")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
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
            Text(
                text = "Data: ${dateFormat.format(log.logDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Exercício: ${detail.exerciseName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Carga: ${detail.weight} kg", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Repetições: ${detail.reps}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sets: ${detail.sets}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
