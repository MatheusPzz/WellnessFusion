@file:Suppress("NAME_SHADOWING")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.play.integrity.internal.m
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

/*
 Defining a composable function to display out user logs / record screen
 here user will be able to log for a workout plan, set a new goal
 see the state of each goal set (progress) and see a list of logs as a history tab,
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    navController: NavController, // Navigation controller, this handles the screen transition or better, compose transition
    viewModel: MainViewModel, // Provides teh back end of our composable ( the logic )
    goals: List<Goal>, // We are importing the goals from the main view model
) {
    val showDialog =
        remember { mutableStateOf(false) }  // State the visibility of the dialog for adding new record for a workout plan or plans
    var selectedWorkoutPlans by remember { mutableStateOf<List<WorkoutPlan>>(emptyList()) }  // State holding the selected workout plans for the new entry
    val isAddingNewLog by viewModel.isAddingNewLog.observeAsState(false)  // Observers the view model state changes for adding new log
    val fontText = FontFamily(
        // Font for app theme
        Font(R.font.zendots_regular),
    )

    // Scaffold setup with top, bottom bar and content area
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                ),
                title = {
                    Text(
                        "Training Record",
                        color = White,
                        fontFamily = fontText,
                        fontSize = 14.sp
                    )
                },
                actions = {
                    if (!isAddingNewLog) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Add Entry",
                                style = MaterialTheme.typography.bodyMedium,
                                color = White,
                                fontFamily = fontText
                            )
                            IconButton(onClick = { showDialog.value = true }) {
                                Icon(
                                    imageVector = Filled.Add,
                                    contentDescription = "Add Entry",
                                    tint = White
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        // Main Content Area
        Surface(
            modifier = Modifier
                .padding(paddingValues)
        )
        {
            // Conditional check for the dialog visibility and the adding new Entry state
            if (isAddingNewLog) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )
                // Showing the form for adding details about an Entry
                LogDetailsFormLogs(
                    workoutPlans = selectedWorkoutPlans, // Selected workout plans that were selected
                    viewModel = viewModel,
                    onLogSaved = {
                        // Functions to take after saving new log
                        selectedWorkoutPlans = emptyList() // Clear the selected workout plans
                        viewModel.finishAddingNewLog() // Finish adding new log
                    },
                    navController = navController,
                    onDismiss = { viewModel.finishAddingNewLog() } // Closes the form
                )

                // Setting a condition
            } else if (showDialog.value && !isAddingNewLog) {
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )
                SelectWorkoutPlanDialog(
                    viewModel = viewModel,
                    onPlansSelected = { workoutPlans ->
                        selectedWorkoutPlans = workoutPlans
                        viewModel.startAddingNewLog()
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )

            } else {
                // If not adding a nre Entry show the main content
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )

                // UI components and layout configurations for display of the main content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { navController.navigate("AchievedGoals") }) { // Text button that leads the user to the achieved goals screen, we are using a navigation function to navigate to a route passed as parameter
                                Text("Achieved Goals", color = Color(0xffFE7316), fontFamily = fontText)
                            }
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(0xffFE7316), CircleShape)
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

                            VerticalDivider()
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Text(
                                    "Add a Goal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xffFE7316),
                                    fontFamily = fontText
                                )
                                Spacer(modifier = Modifier.width(9.dp))
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(Color(0xffFE7316), CircleShape)
                                ) {
                                    IconButton(onClick = { navController.navigate("goalScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Goal",
                                            tint = White
                                        )
                                    }
                                }
                            }
                        }
                    }


                    // Calling the goals dashboard into a column
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                            Goals(viewModel, navController)
                    }

                    // Calling all the entries history section into a column

                    Column(
                        modifier = Modifier
                            .padding(13.dp)
                            .weight(2f) // Larger portion for logs
                            .fillMaxWidth()
                    ) {
                        SavedLogsList(viewModel = viewModel, navController = navController)
                    }

                    // Little text to let the user know how to expand the logs to see their details
                    Text(
                        "Click on the log to see details",
                        color = White,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}


/*
 Goals Dashboard section of the app,
 Here we are showing the goals user set,
 displaying the progress of each goal,
 and providing a way to the user to see his achieved goals in a list
 */
@Composable
fun Goals(
    viewModel: MainViewModel, // View Model on which the logic is being taken
    navController: NavController  // Nav Controller
) {
    val goals by viewModel.goals.observeAsState(initial = emptyList())  // Live data to observe the goals from the view model collecting it as state
    val fontText = FontFamily(
        Font(R.font.zendots_regular),
    )

    // Card composable that visually groups the goals related to the user
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .alpha(0.8f),
        colors = CardColors(
            Color.Black,
            Color.Black,
            Color.Black,
            Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Current Goals",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xffFE7316),
                fontFamily = fontText,
                fontSize = 16.sp
            )
            HorizontalDivider()
        }

        if(goals.isEmpty()) {
            Text(
                "No goals set yet, set one to follow your progress!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontFamily = fontText,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        // Lazy Row that display the goals and stacks them horizontally and also makes the row scrollable as we are going to have more than one goal
        LazyRow {
            // Maps each goal to a goal Item composable
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    navController = navController,
                )
            }
        }
    }
}


@Composable
fun SelectWorkoutPlanDialog(
    viewModel: MainViewModel, // ViewModel instance used to access the functions
    onPlansSelected: (List<WorkoutPlan>) -> Unit, // Changed to handle multiple plans
    onDismiss: () -> Unit // Callback for dialog dismissal
) {
    val savedWorkouts by viewModel.workoutPlans.observeAsState(initial = listOf())  // Observes the workout plans live data from the viewmodel, collecting it as state
    val selectedWorkouts =
        remember { mutableStateListOf<WorkoutPlan>() } // Tracks which workout plan is select by the user

    // Shows the dialog in a condition of if there is any saved workout plans
    if (savedWorkouts.isNotEmpty()) {

        AlertDialog(
            onDismissRequest = { onDismiss() },  // Calling os dismiss
            title = { Text("Select Workout Plans", color = Color.Black) },
            text = {

                // Lazy Column that displays a list of items in a scrollable column, the content is the workout plans
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(savedWorkouts) { workout ->

                        // Changing the state whenever the plan is selected or deselected
                        val isSelected = selectedWorkouts.contains(workout)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(16.dp)
                                .background(
                                    if (isSelected) Color.Gray.copy(alpha = 0.4f) else Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { // Whenever clicked, toggles the selection function in our view model it either removes or adds a workout
                                    if (isSelected) {
                                        selectedWorkouts.remove(workout)
                                    } else {
                                        selectedWorkouts.add(workout)
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var isExpandedLine by remember { mutableStateOf(false) } // Flag for the expanded line
                            val clickModifier = Modifier.clickable {
                                isExpandedLine = !isExpandedLine
                            } // Click modifier to trigger the line expansion animation

                            Text(
                                workout.planName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = clickModifier
                                    .animateContentSize()
                                    .width(150.dp)
                                    .padding(16.dp)
                                    .align(Alignment.Top), // animating content size upon expanding the line
                                maxLines = if (isExpandedLine) Int.MAX_VALUE else 1, // Expand the line if the flag is set
                                overflow = TextOverflow.Ellipsis, // If the text goes over the line, it will be truncated
                            )
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null, // We handle the change in the Row's clickable
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(8.dp)
                            )
                        }

                    }
                }

            },
            // Confirm button to select the workout plans
            confirmButton = {
                Button(onClick = { onPlansSelected(selectedWorkouts.toList()); onDismiss() }) {
                    Text("Select")
                }
            },
            // Dismiss button to cancel selection and go back to last screen state
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// In this composable i was trying to implement a user weekly activity, showing the days that the user most engaged in his fitness journey
// but i was not able to finish it, i was trying to use the MPAndroidChart library to implement the bar chart, but i was not able to finish it
// i will leave the code here for future implementation

//@Composable
//fun WeeklyWorkoutBarChart(
//    weeklyWorkoutCounts: List<MainViewModel.DailyWorkoutCount>,
//    modifier: Modifier = Modifier
//) {
//    val barWidth = 0.3f // Example bar width
//    val barSpace = 0.03f // Space between two bars in a group
//    val groupSpace = 0.2f // Space between groups of bars
//    val physicalColor = Color(0xffFE7316) // Use a color representative of physical workouts
//    val mentalColor = Color.Blue // Use a color representative of mental workouts
//
//    AndroidView(
//        modifier = modifier.height(150.dp).fillMaxWidth(),
//        factory = { context ->
//            BarChart(context).apply {
//                description.isEnabled = false
//                axisLeft.axisMinimum = 0f
//                axisRight.isEnabled = false
//                xAxis.position = XAxis.XAxisPosition.BOTTOM
//                xAxis.setDrawGridLines(false)
//                xAxis.granularity = 1f // Only allow whole numbers on the X-axis
//                xAxis.labelCount = weeklyWorkoutCounts.size
//                xAxis.valueFormatter = IndexAxisValueFormatter(weeklyWorkoutCounts.map {
//                    it.dayOfWeek.take(3) // Abbreviate the days to 3 letters
//                })
//
//                axisLeft.valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return value.toInt().toString()
//                    }
//                }
//
//                val entriesPhysical = weeklyWorkoutCounts.mapIndexed { index, dailyWorkoutCount ->
//                    BarEntry(index.toFloat(), dailyWorkoutCount.physicalCount.toFloat())
//                }
//
//                val entriesMental = weeklyWorkoutCounts.mapIndexed { index, dailyWorkoutCount ->
//                    BarEntry(index.toFloat() + barWidth + barSpace, dailyWorkoutCount.mentalCount.toFloat())
//                }
//
//                val setPhysical = BarDataSet(entriesPhysical, "Physical").apply {
//                    color = physicalColor.toArgb()
//                }
//
//                val setMental = BarDataSet(entriesMental, "Mental").apply {
//                    color = mentalColor.toArgb()
//                }
//
//                val data = BarData(setPhysical, setMental).apply {
//                    this.barWidth = barWidth
//                    setValueFormatter(object : ValueFormatter() {
//                        override fun getFormattedValue(value: Float): String {
//                            return if (value % 1.0f == 0f) value.toInt().toString() else value.toString()
//                        }
//                    })
//                }
//
//                setData(data)
//                // Calculate the correct start position for groupBars based on the bar width and spaces
//                val fromX = barWidth / 2f
//                groupBars(fromX, groupSpace, barSpace)
//                invalidate()
//            }
//        },
//        update = { barChart ->
//            barChart.notifyDataSetChanged()
//            barChart.invalidate()
//        }
//    )
//}


//@Composable
//fun WeeklyWorkoutBarChart(weeklyWorkoutCounts: List<MainViewModel.DailyWorkoutCount>) {
//    // Define the space between pairs and individual bars
//    val spaceBetweenPairs = 12.dp
//    val spaceBetweenBars = 4.dp
//
//    // Create the bars for physical and mental workout counts
//    val bars = weeklyWorkoutCounts.flatMap { workoutCount ->
//        listOf(
//            BarChartData.Bar(
//                label = workoutCount.dayOfWeek.substring(0, 3), // Use day abbreviation
//                value = workoutCount.physicalCount.toFloat(),
//                color = Color.Blue,
//                // Optionally set individual bar width here
//            ),
//            BarChartData.Bar(
//                label = workoutCount.dayOfWeek.substring(0, 3),
//                value = workoutCount.mentalCount.toFloat(),
//                color = Color.Green,
//                // Optionally set individual bar width here
//            )
//        )
//    }
//
//    // Render the bar chart with the bars
//    BarChart(
//        barChartData = BarChartData(bars = bars),
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//            .height(200.dp),
//        animation = simpleChartAnimation(),
//        barDrawer = SimpleBarDrawer(
//            // Apply the space between individual bars
//        ),
//        xAxisDrawer = SimpleXAxisDrawer(),
//        yAxisDrawer = SimpleYAxisDrawer(),
//        labelDrawer = SimpleValueDrawer()
//    )
//}


/*
 In this composable we are displaying a list of logs entries recorded by the user at the end of each section, or
 in our case here when he manually clicks in the button at the top of the page to add a log,
 This composable will show also the date that the entry was made
 and a navigation history system to show the next 3 logs on the list.
 */
@Composable
fun SavedLogsList(viewModel: MainViewModel, navController: NavController) {
    val savedLogs by viewModel.savedLogs.observeAsState(initial = emptyList()) // Observers and retrieves the list of saved logs from our function in view model
    var startIndex by remember { mutableStateOf(0) } // State to track the start index for displaying logs in the list as page
    val sortedLogs =
        savedLogs.sortedByDescending { it.logDate } // Sorts the logs by the date they were recorded

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardColors(
            White.copy(alpha = 0.8f),
            Color.Black,
            Color.Black,
            Color.Black
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Displays the title of the section
                Text(
                    "Records List",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.zendots_regular)),
                    fontSize = 20.sp,
                )

                // Row for pagination controls, it involves to show the last 3 logs in the list and the next 3 as well
                Row {
                    IconButton(
                        onClick = {
                            startIndex =
                                maxOf(0, startIndex - 3) // Move back by 3 logs, not going below 0
                        },
                        enabled = startIndex > 0 // Enable when not at the start, otherwise is disabled
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Show Previous Logs")
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .padding(8.dp)
                            .height(35.dp)
                    )
                    IconButton(
                        onClick = {
                            startIndex =
                                minOf(
                                    sortedLogs.size - 3,
                                    startIndex + 3
                                ) // Moving to next 3 logs on the list
                        },
                        enabled = startIndex + 3 < savedLogs.size // Enable if more logs are available ahead
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Show Next Logs")
                    }
                }
            }
            HorizontalDivider(thickness = 3.dp, color = Color.Black)

            // Displaying the logs in the list, based on the current index
            val displayLogs = sortedLogs.drop(startIndex).take(3)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(displayLogs) { log ->
                    // For each log, display a LogItem composable
                    LogItem(log = log, viewModel = viewModel, navController = navController)
                    HorizontalDivider(thickness = 3.dp, color = Color.Black)
                }
            }
        }
    }
}


/*
 This log Item represents how each log item is displayed in the list of logs,
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun LogItem(log: TrainingLog, viewModel: MainViewModel, navController: NavController) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var isExpandedLine by remember { mutableStateOf(false) }

    // Define the textFont outside Row to prevent recomposition issues
    val textFont = FontFamily(Font(R.font.zendots_regular))

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(IntrinsicSize.Min), // Adjust height based on content
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
                .clickable { isExpandedLine = !isExpandedLine } // Toggle expansion when the column is clicked
                .animateContentSize(), // Animate the size change
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                log.logName,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = textFont,
                maxLines = if (isExpandedLine) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )
        }

            Text(
                dateFormat.format(log.logDate),
                modifier = Modifier.padding(start = 8.dp), // Provide separation from the name
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = textFont
            )
//        IconButton(
//            onClick = { viewModel.deleteLog(log.logName) },
//            modifier = Modifier
//                .size(10.dp)
//                .background(Color.Black, RoundedCornerShape(8.dp))
//        ) {
//            Icon(
//                modifier = Modifier.size(15.dp),
//                imageVector = Icons.Default.Close,
//                contentDescription = "Delete",
//                tint = Color.White
//            )
//        }
        Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { navController.navigate("logDetails/${log.logName}") },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, RoundedCornerShape(60.dp))
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }

    }
}

@Composable
fun LogDetailsFormLogs(
    workoutPlans: List<WorkoutPlan>,  // List of selected workout plans
    viewModel: MainViewModel,  // View model instance for MainViewModel
    navController: NavController,  // Nav Controller for navigation between the screens
    onLogSaved: () -> Unit, // Callback for log saved
    goalToUpdate: Goal? = null, // Callback for goal update
    onDismiss: () -> Unit  // Callback for on dismiss
) {
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) } // This will hold all unique exercises across the selected workout plans
    val exerciseDetails =
        remember { mutableStateListOf<ExerciseDetail>() } // This will hold the details for each exercise
    var isNameDialogVisible by remember { mutableStateOf(false) } // Flag for the dialog visibility
    var logName by remember { mutableStateOf("") } // The log name placeholder
    val fontText = FontFamily(
        // Text font
        Font(R.font.zendots_regular),
    )

    var isExpandedLine by remember { mutableStateOf(false) } // Flag for the expanded line
    val clickModifier = Modifier.clickable { isExpandedLine = !isExpandedLine } // Click modifier to trigger the line expansion animation

    // Below code snippet is adapted from guidance provided by ChatGPT.
    // It has been modified to fit the specific requirements of this application.
    // Original ChatGPT suggestions were used as a foundation for further customization and optimization.
    // Fetching and combining exercises from the current plan, so exercises related to it, will be shown in the form, or when there is any change in the list of workout plans
    LaunchedEffect(workoutPlans) {
        val combinedExerciseIds = workoutPlans.flatMap { it.exercises }.distinct()
        viewModel.fetchExercisesDetailsByIds(combinedExerciseIds) { fetchedExercises ->
            exercises = fetchedExercises
            exerciseDetails.clear()
            fetchedExercises.forEach { exercise ->
                exerciseDetails.add(ExerciseDetail(exercise.id, exercise.name))
            }
        }
    }

    // If our name dialog state changes, the dialog pops up in the screen, and the user can input the name of the log
    if (isNameDialogVisible) {
        NameLogDialogLogs(
            logName = logName,
            onLogNameChange = { logName = it },
            goalToUpdate = goalToUpdate, // if there is any goal to be updated it will do after this
            onUpdate = { updatedGoal ->
                // Updates the goal calling our view model functions to update the goal and save the progress update
                viewModel.updateGoal(updatedGoal)
                viewModel.saveProgressUpdate(updatedGoal.id, updatedGoal.currentValue)
                Log.d("GoalScreen", "Goal updated: $updatedGoal")
            },
            onConfirm = {
                // Then on confirm it saved the log with the name and details, calling the functions from view model
                val workoutPlanIds =
                    workoutPlans.joinToString(separator = ",") { it.id.toString() }
                viewModel.saveExerciseLog(logName, workoutPlanIds, exerciseDetails)
                isNameDialogVisible = false  // Closes the dialog
                logName = "" // Clears the log name for next use
                onLogSaved() // Callback for log saved
            },
            onDismiss = { isNameDialogVisible = false }, // Dismisses the dialog
        )
    }
    // Form content setup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Record for plan:",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = fontText
                )
                workoutPlans.forEach { plan ->
                    Text(
                        plan.planName, // Display the plan name
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = fontText,
                        modifier = clickModifier.animateContentSize(), // animating content size upon expanding the line
                        maxLines = if (isExpandedLine) Int.MAX_VALUE else 1, // Expand the line if the flag is set
                        overflow = TextOverflow.Ellipsis, // If the text goes over the line, it will be truncated
                    )
                }
            }
            Text(
                "Set your personal record",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
                fontFamily = fontText
            )
            HorizontalDivider(thickness = 4.dp)

            // Display the exercises as before, but now consolidated from the current plan
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exerciseDetails) { detail ->
                    // For each exercise allow the user to input its entries
                    ExerciseDetailEntry(
                        exercise = exercises.first { it.id == detail.exerciseId },
                        detail = detail,
                        onDetailUpdate = { updatedDetail ->
                            // Update the detail in the list
                            val index = exerciseDetails.indexOfFirst { it.exerciseId == updatedDetail.exerciseId }
                            exerciseDetails[index] = updatedDetail
                            if (index != -1) { // If the index is valid
                                exerciseDetails[index] = updatedDetail // Update the exercise detail
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }

            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFFFE7316),
                    ),
                    onClick = {
                        isNameDialogVisible = true // Show the dialog for the name o log
                    },
                    modifier = Modifier.width(100.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        "Save",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }


                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFFFE7316),
                    ),
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.width(100.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameLogDialogLogs(
    logName: String,  // LogName placeholder
    onLogNameChange: (String) -> Unit, // Callback for when the name is changed
    goalToUpdate: Goal?, // Goal to update
    onUpdate: (Goal) -> Unit, // On update
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val newValue by remember { mutableStateOf("") }  // new value for the goal

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.White, RoundedCornerShape(10.dp)),
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Any improvement today?",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    "Extra information for the record today:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = logName,
                    onValueChange = onLogNameChange,
                    label = { Text("e.g., Arms Progression") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFFFE7316),
                        ),
                        onClick = {
                            onDismiss() // Dismiss the dialog
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFFFE7316),
                        ),
                        onClick = {
                            // Will go on only if the log name is filled
                            if (logName.isNotBlank()) {
                                goalToUpdate?.let { // If there is a goal to update
                                    // Ensure newValue is valid before proceeding to update the goal.
                                    if (newValue.isNotBlank()) {
                                        // newValue is used to update the goal
                                        val updatedGoal =
                                            it.copy(currentValue = newValue.toInt())
                                        onUpdate(updatedGoal) // Goal updated
                                    }
                                }
                                onConfirm() // Save the log and confirm
                            }
                        },
                        enabled = logName.isNotBlank(), // Enable the button only if the log name is filled
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    )
}

/*
 This composable functions displays the details of each exercise in a form
 that will take the user input for personal records, what was done in that workout specifically
 */
@Composable
fun ExerciseDetailEntryForLogsActivity(
    exercise: Exercise,
    detail: ExerciseDetail,  // The details of the exercise to display and edit
    onDetailUpdate: (ExerciseDetail) -> Unit, // Callback to handle the function invoked with the updated exercise details
) {
    var reps by remember { mutableFloatStateOf(detail.reps.toFloat()) }  // Remember the reps number as mutable state on which can be changed or edited
    var sets by remember { mutableFloatStateOf(detail.sets.toFloat()) }  // Remember the sets number as mutable state on which can be changed or edited
    var weightInPicker by remember { mutableStateOf(detail.weight.toString()) }  // Remember the weight number as mutable state on which can be changed or edited

    Column(modifier = Modifier.padding(15.dp)) {

        // Displaying the name of the exercise from our modal which holds each exercise detail fetched
        Text(
            text = detail.exerciseName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Compact layout for weight picker and its label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if(exercise.workoutType == "Mental") "Duration:" else "Weight:", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(6.dp))
            NumberPicker(
                exercise = exercise,
                value = detail.weight,
                onValueChange = { newValue ->
                    weightInPicker = newValue.toString()
                    onDetailUpdate(detail.copy(weight = newValue))
                },
                range = 1..200, // Range for picker
                modifier = Modifier.width(200.dp) // Adjust the width as needed
            )
        }
        // Custom composable to set the sets and reps, it represents a slider that takes the value of sets and reps in an animated way
        DetailSliderForLogs(
            label = "Sets",
            value = sets,
            range = 1f..10f,
            onValueChange = { newValue ->
                val newSets = newValue.roundToInt()
                sets = newSets.toFloat()
                onDetailUpdate(detail.copy(sets = newSets))
            }
        )

        DetailSliderForLogs(
            label = "Reps",
            value = reps,
            range = 1f..20f,
            onValueChange = { newValue ->
                val newReps = newValue.roundToInt()
                reps = newReps.toFloat()
                onDetailUpdate(detail.copy(reps = newReps))
            }
        )
    }
}
/*
 Below code snippet is adapted from guidance provided by ChatGPT.
 It has been modified to fit the specific requirements of this application.
 Original ChatGPT suggestions were used as a foundation for further customization and optimization. it is used to display the details of each exercise in a form that will register the user input
 */
@Composable
fun NumberPicker2(
    value: Int,  // Current value
    onValueChange: (Int) -> Unit, // Callback, on change, which means when new value is input
    range: IntRange = 1..200,  // Setting the range 1 to 200KG
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Minus Button Setup
        Button(
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFE7316)),
            onClick = { if (value > range.first) onValueChange(value - 1) },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("-")
        }
        Text(
            text = "$value kg",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        // Plus button setup
        Button(
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFE7316)),
            onClick = { if (value < range.last) onValueChange(value + 1) },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+")
        }
    }
}


/*
 Below code snippet is adapted from guidance provided by ChatGPT.
 It has been modified to fit the specific requirements of this application.
 Original ChatGPT suggestions were used as a foundation for further customization and optimization.
 It display a custom slider to choose sets and reps in a nice way.
 */
@Composable
fun DetailSliderForLogs(
    label: String,  // Label
    value: Float, // Current value for the slider
    range: ClosedFloatingPointRange<Float>, // Range of the slider
    onValueChange: (Float) -> Unit  // Call back to listen when the new value takes place
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("$label: ${value.roundToInt()}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = range.endInclusive.toInt() - range.start.toInt() - 1, // Define steps to match the integer range
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
    }
}