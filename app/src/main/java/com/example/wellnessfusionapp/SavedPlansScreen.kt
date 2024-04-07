package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

/*
 This composable function displays a list of workout plans created by the user
 The user can create new plans from this screen as well
 The user can also view the details of each workout plan by clicking on the plan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWorkoutsScreen(
    viewModel: GeneratedWorkoutViewModel,  // Using another viewModel for this screen
    navController: NavController  // NavController for screen navigation
) {

    val textFont = FontFamily(
        Font(R.font.zendots_regular)
    )

    val savedWorkouts by viewModel.savedWorkouts.observeAsState(listOf()) // Observe the list of saved workout as state

    // Fetching any new workouts when the screen is first launched
    LaunchedEffect(Unit) {
        viewModel.fetchSavedWorkouts()
    }

    // Main content scaffold setup
    Scaffold(
        containerColor = Color(0xff383838),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                title = {
                    Text(
                        text = "Your Workout Plans",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = textFont
                    )
                },
            )
        },
        bottomBar = { BottomNavBar(navController) }

    ) { paddingValues ->

        // If there are no saved workout plans, display a message to create a new plan with the two main buttons
        if (savedWorkouts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No saved workout plans, Create one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontFamily = textFont
                )
                Row {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ButtonPhysical(navController)
                        Text(
                            "Physical",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontFamily = textFont
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MentalButton(navController)
                        Text(
                            "Mental",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontFamily = textFont
                        )
                    }
                }
            }
        } else {
            // Display the saved workout and options to create new ones at the top of the page
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    text = "Create your plan now!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontFamily = textFont
                )
                HorizontalDivider(Modifier.padding(start = 16.dp, end = 16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ButtonPhysical(navController)
                            Text(
                                "Physical",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = textFont,
                                fontSize = 16.sp
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MentalButton(navController)
                            Text(
                                "Mental",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = textFont,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                // Display a list of all saved workouts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "List of Existing Plans",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontFamily = textFont
                    )
                    HorizontalDivider()
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    items(savedWorkouts) { workout ->
                        // for each item a workout custom card holding information about the workout plan
                        WorkoutCard(workout, viewModel, navController)
                        // additional user information for UX
                        Text(
                            text = "Swipe left or right to view more exercises",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = MaterialTheme.typography.bodySmall.fontFamily
                        )
                    }
                }
            }
        }
    }
}

/*
 Custom composable that edits the name of a workout plan if the wrong wrongly named
 */
@Composable
fun EditButton(
    workoutPlanId: String,  // Takes the plan ID to be edited
    currentName: String,  // Takes the current name of the plan
    viewModel: GeneratedWorkoutViewModel // Uses the view model to update the plan name
) {
    var showDialog by remember { mutableStateOf(false) } // State to show the dialog
    var newName by remember { mutableStateOf(currentName) } // State to hold the new name of the plan

    // Container for edit button
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {
        // Upon click, shows the dialog for edition
        IconButton(onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Plan",
                tint = Color.White
            )
        }
        // If dialog is true, pops the dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, // os dismiss, close it
                title = { Text("Edit Workout Plan Name") },
                text = {
                    OutlinedTextField(
                        value = newName,  // new name to be updated
                        onValueChange = { newName = it },  // on change, update the new name
                        label = { Text("New Name") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        // if new name is not blank calls the update function from view model and closes the dialog
                        if (newName.isNotBlank()) {
                            viewModel.updateWorkoutPlanName(workoutPlanId, newName)
                            showDialog = false
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) { // Closes the dialog on cancel button
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


/*
 If user wants to delete a workout plan,
 this composable displays a button for it
 */
@Composable
fun DeleteButton(
    workoutPlanId: String,  // Takes the plan ID
    viewModel: GeneratedWorkoutViewModel // Uses the view model to delete the plan
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {
        // Upon button click calls the function from our view model that deletes the plan
        IconButton(onClick = { viewModel.deleteWorkoutPlan(workoutPlanId) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Workout Plan",
                tint = Color.White
            )
        }
    }
}


/*
 this button takes place in a scenario that the user
 wants to redo his plan from a previous week or day
 he can click on it and go to a workout session page where he will see the exercises and instructions for it
 */
@Composable
fun PlayButton(
    navController: NavController, // Navigation controller
    workout: WorkoutPlan // workout plan to be initiated
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {
        // on click it navigates to a new page with the workout plan ID as an argument passing all the info to the other page
        IconButton(onClick = { navController.navigate("createdPlans/${workout.workoutPlanId}") }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Workout Plan",
                tint = Color.White
            )
        }
    }
}


/*
 This composable is the UI setup for a workout plan card
 It displays the information about a workout plan and place the buttons on it
 for editing, deletion and execution of a plan
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun WorkoutCard(
    workout: WorkoutPlan, // Calls the plan from the model
    viewModel: GeneratedWorkoutViewModel, // Uses this view model
    navController: NavController  // navigation controller
) {
    var exercisesDetails by remember { mutableStateOf<List<Exercise>>(listOf()) } // State for managing the list of exercises in the workout plan

    var isExpanded by remember { mutableStateOf(false) } // Expanding the line of the workout plan name
    val clickModifier = Modifier.clickable { isExpanded = !isExpanded } // on click it animates the expansion
    val pagerState = rememberPagerState() // State for managing the pager position

    // Fetches the exercise details when the workout exercises are available and store it into out list of exercisesDetails
    LaunchedEffect(workout.exercises) {
        viewModel.fetchExercisesDetails(workout.exercises) { exercises ->
            exercisesDetails = exercises
        }
    }

    // Card component setup
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
        border = BorderStroke(
            width = 3.dp,
            color = Color.Black.copy(alpha = 0.8f)
        ),
        colors = CardColors(
            Color.Black.copy(alpha = 0.8f),
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.3f),
        )
    ) {
        Column {

            // Horizontal pager for swiping through the exercises in the workout plan.
            HorizontalPager(
                count = exercisesDetails.size, // how many pages / how many exercises
                state = pagerState, // pager state that controls which page the pager ir
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.DarkGray)
                ) {
                    // Loads exercise image
                    AsyncImage(
                        model = exercisesDetails[page].imageUrl,
                        contentDescription = "Exercise Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.Absolute.Right,
                    )
                    {
                        Row(
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.4f),
                                    CircleShape
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Number of Exercises: ",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xffFE7316).copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Displaying the number of exercise in the plan
                                Text(
                                    text = exercisesDetails.size.toString(),
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    // Exercise information section within the card
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(180.dp)
                                    .padding(),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Text(
                                    text = workout.planName, // plan name
                                    modifier = clickModifier  // click modifier to animate expansion of line
                                        .animateContentSize(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    maxLines = if (isExpanded) Int.MAX_VALUE else 1, // Use Int.MAX_VALUE for no limit when expanded
                                    overflow = TextOverflow.Ellipsis // Add ellipsis when text occupies more than the available space
                                )
                            }
                            // Calling our three custom composables in a row
                            PlayButton(navController, workout)
                            Spacer(modifier = Modifier.width(7.dp))
                            EditButton(workout.workoutPlanId, workout.planName, viewModel)
                            Spacer(modifier = Modifier.width(7.dp))
                            DeleteButton(workout.workoutPlanId, viewModel)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Displays the name of each exercise bof each page, passing the pager state for each page
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exercisesDetails[page].name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                        }
                    }
                }
            }
        }
    }
}

/*
 Custom composable that displays the UI of the button for physical plan creation
 */
@Composable
fun ButtonPhysical(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.7f
                    }
                    .background(Color(0xffFE7316))
            )
            Image(
                painter = painterResource(id = R.drawable.physical),
                contentDescription = "Physical",
                modifier = Modifier
                    .height(120.dp)
                    .clickable(onClick = { navController.navigate("physicalCategory") } //On Click navigates to physical category selection
                    ))
        }
    }
}

/*
 Custom composable that displays the UI of the button for Mental plan creation
 */
@Composable
fun MentalButton(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.9f
                    }
                    .background(Color(0xff1666ba))
            )
            Image(
                painter = painterResource(id = R.drawable.mental),
                contentDescription = "Mental",
                modifier = Modifier
                    .height(120.dp) // Adjust size as needed
                    .clickable(onClick = { navController.navigate("zenCategory") } // on click navigates to mental category selection
            ))
        }
    }
}



