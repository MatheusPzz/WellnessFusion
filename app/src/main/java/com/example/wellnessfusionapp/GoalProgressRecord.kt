package com.example.wellnessfusionapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Goal

import com.example.wellnessfusionapp.Models.ProgressRecord
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Navigation.BottomNavBar
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalProgressRecordScreen(
    viewModel: MainViewModel, // The ViewModel that contains the logic and data.
    goalId: String, // The ID of the goal to display progress for.
    exerciseId: String, // The ID of the related exercise.
    navController: NavController // Navigation controller for navigation events.
) {
    // Observing progress records and goal details from the ViewModel.
    val progressHistory by viewModel.progressRecords.collectAsState()
    val goal by viewModel.goalDetails.observeAsState()
    val currentWeek by viewModel.currentWeek.collectAsState()
    val textFont = FontFamily(Font(R.font.zendots_regular)) // Custom font for text elements.
    val snackbarHostState = remember { SnackbarHostState() } // State for showing snackbars.
    val scope = rememberCoroutineScope() // Coroutine scope for launching side effects.


    // Side effect for fetching progress history and goal details once the goalId changes.
    LaunchedEffect(key1 = goalId) {
        viewModel.fetchProgressHistory(goalId)
        viewModel.fetchGoalDetails(goalId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Host for displaying snackbars.
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                title = { Text("Goal Progress", fontFamily = textFont) }, // Title with custom font.
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) { // Button to navigate back.
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) } // Bottom navigation bar.
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.backgroundhome),
                contentDescription = "Background Image",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(2.dp)
                    .alpha(0.95f),
                contentScale = ContentScale.FillBounds
            )

            // Only display content if goal details are not null.
            goal?.let { goal ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Card displaying the goal's type, description, current, and desired values.
                    GoalDetailCard(goal, textFont)
                    // Divider for visual separation.
                    HorizontalDivider()
                    // Column for the progress chart.
                    ProgressChartColumn(
                        progressHistory,
                        goal.startDate,
                        snackbarHostState,
                        scope,
                        textFont,
                        currentWeek = currentWeek,
                        onChangeWeek = { newWeek ->
                            viewModel.setWeek(newWeek)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun GoalDetailCard(
    goal: Goal, // The goal details to display.
    textFont: FontFamily // Custom font for text.
) {
    Card(
        modifier = Modifier.padding(10.dp),
        colors = CardDefaults.cardColors(
            Color.Black.copy(alpha = 0.5f),
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Displaying goal type and description.
            Row {
                Text(
                    "Goal Name:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = textFont,
                    color = Color(0xFFFE7316)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(goal.typeId, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
            Row {
                Text(
                    "Exercise Name:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = textFont,
                    color = Color(0xFFFE7316)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(goal.description, color = Color.White)
            }
            Spacer(modifier = Modifier.height(13.dp))
            // Displaying current and goal values.
            GoalValuesRow(goal, textFont)
        }
    }
}

@Composable
fun GoalValuesRow(
    goal: Goal, // Goal details for current and desired values.
    textFont: FontFamily // Custom font for text.
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Current:",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = textFont,
            color = Color(0xFFFE7316)
        )
        Text(
            "${goal.currentValue} Kg",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = textFont,
            color = Color.White
        )
        Text(
            "Goal:",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = textFont,
            color = Color(0xFFFE7316)
        )
        Text(
            "${goal.desiredValue} Kg",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = textFont,
            color = Color.White
        )
    }
}

@Composable
fun ProgressChartColumn(
    progressData: List<ProgressRecord>,
    goalStartDate: Timestamp,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    textFont: FontFamily,
    currentWeek: Int,
    onChangeWeek: (Int) -> Unit
) {
    Card(
        modifier = Modifier.padding(10.dp),
        colors = CardDefaults.cardColors(Color.Black.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text("Progress Chart", fontFamily = textFont, color = Color.White)

            // Week Selector
            WeekSelector(currentWeek, onChangeWeek)

            // Chart
            GoalProgressChart(progressData, goalStartDate, currentWeek, snackbarHostState = snackbarHostState, scope)
        }
    }
}

@Composable
fun WeekSelector(currentWeek: Int, onChangeWeek: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFFFE7316)),
            onClick = { onChangeWeek(currentWeek - 1) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Week")
        }
        Text(
            "Week $currentWeek",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFFFE7316)),
            onClick = { onChangeWeek(currentWeek + 1) }) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Week")
        }
    }
}



/*
 The chart was a little bit hard to be implemented so the use of ChatGPT was needed for this occasion,
 The code was taken out from it and adapted to be fed with my history data fetched from firestore, on which
 displays the history of each improvement of a user goal.
 This is also a custom library from MPAndroid by PhilJay - https://github.com/PhilJay/MPAndroidChart
 */
@Composable
fun GoalProgressChart(
    progressData: List<ProgressRecord>,  // Getting the model that hods the fetched information of progress
    goalStartDate: Timestamp,  // Start date in timeStamp
    currentWeek: Int,  // Current week
    snackbarHostState: SnackbarHostState, // Snack bar hosting
    scope: CoroutineScope  // Scope for snack bar
) {
    val context = LocalContext.current  // Current context for snack bar

    // Calculate the start and end dates for the selected week which will be showing 7 days per week
    val calendar = Calendar.getInstance().apply {
        time = Date(goalStartDate.seconds * 1000)
        add(Calendar.WEEK_OF_YEAR, currentWeek - 1)
    }
    val weekStartDate = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    val weekEndDate = calendar.time

    val weekProgressData = progressData.filter {
        val recordDate = Date(it.date.seconds * 1000)
        !recordDate.before(weekStartDate) && recordDate.before(weekEndDate)
    }

    // Check for empty data, if is empty show the message, if not continue with the chart
    if (weekProgressData.isEmpty()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .height(300.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "No data for this week",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        // Android Chart configuration setup
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


                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.textColor =
                        Color(0xFFFFFFFF).toArgb()
                    xAxis.granularity = 1f

                    xAxis.axisMinimum = weekStartDate.time.toFloat()
                    xAxis.axisMaximum = weekEndDate.time.toFloat()
                    xAxis.valueFormatter = CustomDateFormatter()

                    axisLeft.isEnabled = true
                    axisRight.isEnabled = false
                    axisLeft.textColor =
                        Color(0xFFFFFFFF).toArgb() // Replace with your onSurface color value
                }
            },
            update = { chart ->

                val entries = weekProgressData.map {
                    Entry(it.date.seconds.toFloat() * 1000, it.value.toFloat())
                }
                val startEntry = Entry(weekStartDate.time.toFloat(), 0f)
                val allEntries = mutableListOf(startEntry).also {
                    it.addAll(entries)
                }


                val lineDataSet = LineDataSet(allEntries, "Progress").apply {
                    color = Color(0xFFFE7316).toArgb() // Replace with your primary color value
                    valueTextColor = Color(0xFF000000).toArgb() // Replace with your onSurface color value
                    lineWidth = 2.5f
                    setCircleColor(Color(0xFF03DAC5).toArgb()) // Replace with your secondary color value
                    circleRadius = 5f
                    setDrawCircleHole(true)
                    circleHoleColor = Color(0xFFFFFFFF).toArgb() // Replace with your background color value
                    setDrawValues(false)
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    setDrawFilled(true)
                    fillDrawable = ContextCompat.getDrawable(context, R.color.teal_700) }

                chart.data = LineData(lineDataSet)
                chart.invalidate()

                chart.xAxis.axisMinimum = weekStartDate.time.toFloat()
                chart.xAxis.axisMaximum = weekEndDate.time.toFloat()
                chart.setVisibleXRangeMaximum(7 * 24 * 60 * 60 * 1000f) // Sets maximum visible range to 7 days
                chart.moveViewToX(weekStartDate.time.toFloat())


                chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let {
                            val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.x.toLong()))
                            val selectedValue = it.y
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Progress: $selectedValue KG on $selectedDate",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }

                    override fun onNothingSelected() {}
                })

            }

        )
    }
}


/*
 Custom date formatter so we take the regular timestamp date formate and put it in a more readable format
 as dd MMM
 */
class CustomDateFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        // Convert the Float value back to milliseconds for formatting
        return dateFormat.format(Date(value.toLong()))
    }
}

