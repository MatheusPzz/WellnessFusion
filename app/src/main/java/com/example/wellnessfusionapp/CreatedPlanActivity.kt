package com.example.wellnessfusionapp

import android.util.Log
import android.widget.NumberPicker
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import kotlin.math.roundToInt


/*
 This composable contains the content from the workout session, whenever the user creates a plan
 he goes to a page where he starts the workout, this page contains the exercises and instructions for it,
 recommendation for sets, reps and weight, and a button to finish the workout and save the progress.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatedPlan(
    workoutPlanId: String, // The ID of the workout plan
    exerciseId: String, // The Id of the exercise
    navController: NavController, // Nav Controller
    viewModel: GeneratedWorkoutViewModel, // View model
    mainViewModel: MainViewModel // View model
) {

    val workoutPlan by viewModel.workoutPlan.observeAsState() // The workout plan
    var exercisesDetails by remember { mutableStateOf<List<Exercise>>(listOf()) } // The exercises details from data class
    var showLogFormPhysical by remember { mutableStateOf(false) } //
    var showLogFormMental by remember { mutableStateOf(false) } // Flag for mental log form
    val fontText = FontFamily(
        // Font for the text
        Font(R.font.zendots_regular),
    )
    // This is a launched effect that will run once the workout plan ID changes and fetch the workout plan by ID
    LaunchedEffect(workoutPlanId) {
        viewModel.fetchWorkoutPlanById(workoutPlanId)
    }



    LaunchedEffect(workoutPlan?.exercises) { // Fetch the details of each exercise from firestore when the list of exercises changes
        workoutPlan?.exercises?.let { exerciseIds -> // Get the exercise IDs
            viewModel.fetchExercisesDetails(exerciseIds) { exercises ->
                exercisesDetails =
                    exercises // Store it into a list and updates the exercise details
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                ),
                title = {
                    Text(
                        text = "Workout Session",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontFamily = fontText,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .background(Color.DarkGray, CircleShape)
                    ) {
                        IconButton(onClick = {
                            navController.popBackStack()
                            navController.navigate("savedWorkoutPlans") // Navigate to the saved workout plans, where contains the list of user workouts
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back To Workout Plans",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController) // Bottom pre defined nav bar
        },
    ) { paddingValues ->
        if (showLogFormPhysical) { // Conditional, that is Showing the dialog for record the physical log
                Image(
                    painter = painterResource(id = R.drawable.background_logs),
                    contentDescription = "Physical",
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(1.5f)
                        .alpha(0.8f)
                        .blur(1.dp)
                )
                LogDetailsForm(
                    workoutPlans = listOf(workoutPlan!!),
                    viewModel = mainViewModel,
                    navController = navController,
                    paddingValues = paddingValues,
                    onLogSaved = {
                        showLogFormPhysical = false
                        navController.navigate("logs")
                        mainViewModel.completeWorkoutPlan(workoutPlanId)
                        Log.d("workoutplancompleted", "Workout Plan Completed $workoutPlanId")
                    },
                    goalToUpdate = null,
                    onDismiss = { showLogFormPhysical = false }
                )
        } else {
            // Main Content of the workout session page starts here
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            Color.DarkGray
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Workout Plan: ${workoutPlan?.planName}", // extracting the plan name
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                            )
                        }
                    }
                    LazyColumn(             // lazy column to show a list of exercises based on the plan selected or created
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(470.dp)
                    )
                    {
                        items(exercisesDetails) { exercise -> // Display the exercises
                            ExerciseItem(
                                exercise = exercise,
                                navController,
                                onFavoriteClick = { // On favorite click
                                    mainViewModel.toggleFavoriteExercise(exercise) // Toggle the favorite exercise
                                }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                Color(0xFFFE7316),
                            ),
                            onClick = {
                                showLogFormPhysical =
                                    true // opening the dialog for record the physical log
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                "Set your record and finish",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


/*
 This composable contains the content from the lazy column list,
 we are defining the items inside of the list, each exercise item will be displayed
 according to the data class, the image, the name, the recommended sets, reps and weight fetched
 */
@Composable
fun ExerciseItem(exercise: Exercise, navController: NavController, onFavoriteClick: (Exercise) -> Unit){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = exercise.imageUrl,  // loading the image into the box
            contentDescription = "Exercise Image",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .background(color = Color.DarkGray.copy(alpha = 0.4f))
                .matchParentSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text(
                                text = exercise.name,  // Display the exercise name
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                            )
                            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 3.dp)
                        }
//                        Row {
//                            Spacer(Modifier.width(5.dp))
//                            IconButton(
//                                onClick = {
//                                    onFavoriteClick(exercise)
//                                    Log.d("Favorite", "Favorite Clicked")
//                                }) {
//                                Box(
//                                    modifier = Modifier
//                                        .background(
//                                            color = Color.Black.copy(alpha = 0.6f),
//                                            shape = CircleShape
//                                        )
//                                        .size(40.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(
//                                        modifier = Modifier.size(20.dp),
//                                        imageVector = if (exercise.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
//                                        contentDescription = "Favorite Exercise",
//                                        tint = Color.White
//                                    )
//                                }
//                            }
//                        }

                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.DarkGray.copy(alpha = 0.6f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        ) {

                            if(exercise.workoutType == "Mental") {
                                Column(
                                    modifier = Modifier
                                ) {
                                    // section that shows recommended sets, reps and weight for each exercise, this information is also fetched from firestore
                                    Text(text = "Recommended", color = Color.White)
                                    Text(text = "Sets: ${exercise.sets} daily", color = Color.White)
                                    Text(text = "Duration: ${exercise.duration} minutes (min)", color = Color.White)
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .height(100.dp)
                                ) {
                                    // section that shows recommended sets, reps and weight for each exercise, this information is also fetched from firestore
                                    Text(text = "Recommended", color = Color.White)
                                    Text(text = "Sets: ${exercise.sets}", color = Color.White)
                                    Text(text = "Reps: ${exercise.reps}", color = Color.White)
                                    Text(text = "Weight: ${exercise.weight} KG", color = Color.White
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.align(Alignment.Bottom),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text("Instructions", color = Color.White, modifier = Modifier.padding(5.dp))

                                IconButton(modifier = Modifier
                                    .size(50.dp),
                                    onClick = { navController.navigate("instructions/${exercise.id}") }) { // Instructions icon button that leads to the exercise instructions page, it passes and argument as parameter to the page this way we can see information relevant to the current exercise based on its ID
                                    Icon(
                                        modifier = Modifier
                                            .size(35.dp),
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Instructions For Exercise",
                                        tint = Color.White
                                    )
                                }

                        }
                    }
                }
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth(),
        thickness = 3.dp,
    )

}

/*
 This section of the code handles the form that logs users personal record for each workout plan done, he can
 input the number of sets, reps and weight for each exercise, and save the progress he has performed during the plan execution.
 */
@Composable
fun LogDetailsForm(
    workoutPlans: List<WorkoutPlan>,
    viewModel: MainViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    onLogSaved: () -> Unit,
    goalToUpdate: Goal? = null,
    onDismiss: () -> Unit
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
    val clickModifier = Modifier.clickable {
        isExpandedLine = !isExpandedLine
    } // Click modifier to trigger the line expansion animation

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



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
            HorizontalDivider(thickness = 2.dp, color = Color.Black)

            // Display the exercises as before, but now consolidated from the current plan
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exerciseDetails) { detail ->
                    ExerciseDetailEntry(
                        detail = detail,
                        onDetailUpdate = { updatedDetail ->
                            val index = exerciseDetails.indexOfFirst { it.exerciseId == updatedDetail.exerciseId }
                            exerciseDetails[index] = updatedDetail
                            if (index != -1) { // If the index is valid
                                exerciseDetails[index] = updatedDetail // Update the exercise detail
                            }
                        },
                        exercise = exercises.first { it.id == detail.exerciseId }
                    )
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                }
            }

            HorizontalDivider(thickness = 2.dp, color = Color.Black)
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


    if (isNameDialogVisible) {
        // In this section of the code we are defining the dialog that will be shown to the user, it will ask for the name of the log
        // Then it will save the progress and update the goal if needed
        // Thats why we are passing a few lambdas inside the name dialog composable
        NameLogDialog(
            logName = logName, // Log name
            onLogNameChange = { logName = it }, // on name change become it
            goalToUpdate = goalToUpdate, // Goal to update
            onUpdate = { updatedGoal -> // Update the goal if there are any

                viewModel.updateGoal(updatedGoal) // calling a function from view model to update the goal, it will automatically update any exercise weight progression goal if there are any exercises on the log
                viewModel.saveProgressUpdate(
                    updatedGoal.id,
                    updatedGoal.currentValue
                )  // In case of a goal, it will save the progress history for the goal
                Log.d("GoalScreen", "Goal updated: $updatedGoal") // Log for the goal update
            },
            onConfirm = {
                val workoutPlanIds =
                    workoutPlans.joinToString(separator = ",") { it.id.toString() } // Join the workout plans, so we can save the log for each one
                viewModel.saveExerciseLog(
                    logName,
                    workoutPlanIds,
                    exerciseDetails
                ) // Save the exercise Log
                Log.d("workoutplancompleted", "Workout Plan Completed")
                isNameDialogVisible = false // Disable the dialog
                logName = "" // Clears the log name
                onLogSaved() // calls the on log saved function
            },
            onDismiss = {
                isNameDialogVisible = false // Dismiss the dialog
                logName = "" // Clear the log name on dismiss also
            },
        )
    }
}


/*
 In this section of the code is related to the name that the user will give to the log that he is saving
 The user will be able to give a name to the log, and then save the progress that he has done during the workout session
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameLogDialog(
    logName: String,  // Log name
    onLogNameChange: (String) -> Unit, // On log name change
    goalToUpdate: Goal?, // Goal to update
    onUpdate: (Goal) -> Unit, // On update
    onConfirm: () -> Unit, // On confirm
    onDismiss: () -> Unit // On dismiss
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
 This composable handles the fields that will be shown to the user,
 whenever plan is finished, all the input fields as sets, reps and weight,
 user is able to select his own values for each exercise then save it into the log
 */
@Composable
fun ExerciseDetailEntry(
    detail: ExerciseDetail,
    onDetailUpdate: (ExerciseDetail) -> Unit,
    exercise: Exercise
) {
    var reps by remember { mutableFloatStateOf(detail.reps.toFloat()) }
    var sets by remember { mutableFloatStateOf(detail.sets.toFloat()) }
    var weightInPicker by remember { mutableStateOf(detail.weight.toFloat()) }
    var isExpandedLine by remember { mutableStateOf(false) } // Flag for the expanded line
    val clickModifier = Modifier.clickable {
        isExpandedLine = !isExpandedLine
    } // Click modifier to trigger the line expansion animation

    Column(modifier = Modifier.padding(15.dp)) {
        Text(
            modifier = clickModifier.animateContentSize(),
            maxLines = if (isExpandedLine) Int.MAX_VALUE else 1, // Expand the line if the flag is set
            overflow = TextOverflow.Ellipsis, // If the text goes over the line, it will be truncated
            text = detail.exerciseName,
            style = MaterialTheme.typography.headlineMedium,
            )
        // Compact layout for weight picker and its label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if(exercise.workoutType == "Mental") "Duration (min):" else "Weight:", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(6.dp))
            NumberPicker(
                exercise = exercise,
                value = weightInPicker.toInt(),
                onValueChange = { newValue ->
                    weightInPicker = newValue.toFloat()
                    onDetailUpdate(detail.copy(weight = newValue))
                },
                range = 1..200,
                modifier = Modifier.width(100.dp)
            )
        }
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
            range = 1f..50f,
            onValueChange = { newValue ->
                val newReps = newValue.roundToInt()
                reps = newReps.toFloat()
                onDetailUpdate(detail.copy(reps = newReps))
            }
        )
    }
}

@Composable
fun NumberPicker(
    exercise: Exercise,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 1..200,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFE7316),
            ),
            onClick = { if (value > range.first) onValueChange(value - 1) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("-")
        }
        Text(
            text = if(exercise.workoutType == "Mental") "$value min" else "$value kg",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFE7316),
            ),
            onClick = { if (value < range.last) onValueChange(value + 1) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun DetailSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
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
                .background(Color.Gray.copy(alpha = 0.6f)),
        )
    }
}

//@Composable
//fun PagePreview() {
//    val navController = rememberNavController()
//    val viewModel = remember { GeneratedWorkoutViewModel() }
//    val mainViewModel = remember { MainViewModel() }
//
//    CreatedPlan("workoutPlanId","exerciseId" ,navController, viewModel, mainViewModel)
//}




