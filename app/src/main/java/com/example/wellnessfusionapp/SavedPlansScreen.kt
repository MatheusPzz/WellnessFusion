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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWorkoutsScreen(viewModel: GeneratedWorkoutViewModel, navController: NavController) {

    val textFont = FontFamily(
        Font(R.font.zendots_regular)
    )
    val savedWorkouts by viewModel.savedWorkouts.observeAsState(listOf())

    LaunchedEffect(Unit) {
        viewModel.fetchSavedWorkouts()
    }

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
        if (savedWorkouts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No saved workout plans, Create one!", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontFamily = textFont)
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
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ButtonPhysical(navController)
                            Text(
                                "Physical",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = textFont,
                                fontSize = 20.sp
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
                                fontSize = 20.sp
                            )
                        }
                    }
                }
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
                        WorkoutCard(workout, viewModel, navController)
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


@Composable
fun EditButton(workoutPlanId: String, currentName: String, viewModel: GeneratedWorkoutViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(currentName) }
    val exercises by remember { mutableStateOf(emptyList<Exercise>()) }

    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {

        IconButton(onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Plan",
                tint = Color.White
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Edit Workout Plan Name") },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("New Name") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.updateWorkoutPlanName(workoutPlanId, newName)
                            showDialog = false
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DeleteButton(workoutPlanId: String, viewModel: GeneratedWorkoutViewModel) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {
        IconButton(onClick = { viewModel.deleteWorkoutPlan(workoutPlanId) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Workout Plan",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PlayButton(navController: NavController, workout: WorkoutPlan) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.DarkGray.copy(alpha = 0.9f))
    ) {
        IconButton(onClick = { navController.navigate("createdPlans/${workout.workoutPlanId}") }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Workout Plan",
                tint = Color.White
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun WorkoutCard(
    workout: WorkoutPlan,
    viewModel: GeneratedWorkoutViewModel,
    navController: NavController
) {
    var exercisesDetails by remember { mutableStateOf<List<Exercise>>(listOf()) }

    var isExpanded by remember { mutableStateOf(false) }
    val clickModifier = Modifier.clickable { isExpanded = !isExpanded }
    val pagerState = rememberPagerState()
    // ... your existing code ...

    LaunchedEffect(workout.exercises) {
        viewModel.fetchExercisesDetails(workout.exercises) { exercises ->
            exercisesDetails = exercises
        }
    }

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

            // This is where we create a horizontal pager for exercises.
            HorizontalPager(
                count = exercisesDetails.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Fixed height for each pager item
            ) { page ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.DarkGray) // Temporary background color for visibility
                ) {
                    // Load exercise image here
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
                                Text(
                                    text = exercisesDetails.size.toString(),
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    // Exercise information overlay
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
                                    text = workout.planName,
                                    modifier = clickModifier
                                        .animateContentSize()
                                        .padding(0.dp), // Apply padding for better touch area, if needed
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    maxLines = if (isExpanded) Int.MAX_VALUE else 1, // Use Int.MAX_VALUE for no limit when expanded
                                    overflow = TextOverflow.Ellipsis // Add ellipsis when text is truncated
                                )
                            }
                            PlayButton(navController, workout)
                            Spacer(modifier = Modifier.width(9.dp))
                            EditButton(workout.workoutPlanId, workout.planName, viewModel)
                            Spacer(modifier = Modifier.width(9.dp))
                            DeleteButton(workout.workoutPlanId, viewModel)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
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


//@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
//@Composable
//fun WorkoutCard(
//    workout: WorkoutPlan,
//    viewModel: GeneratedWorkoutViewModel,
//    navController: NavController
//) {
//    var exercisesDetails by remember { mutableStateOf<List<Exercise>>(listOf()) }
//
//    var isExpanded by remember { mutableStateOf(false) }
//    val clickModifier = Modifier.clickable { isExpanded = !isExpanded }
//
//    LaunchedEffect(workout.exercises) {
//        viewModel.fetchExercisesDetails(workout.exercises) { exercises ->
//            exercisesDetails = exercises
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .padding(16.dp)
//    ) {
//        Column() {
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
//                Column(
//                    modifier = Modifier
//                        .width(180.dp)
//                        .padding(8.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = workout.planName,
//                        modifier = clickModifier
//                            .animateContentSize()
//                            .padding(0.dp), // Apply padding for better touch area, if needed
//                        style = MaterialTheme.typography.titleLarge,
//                        maxLines = if (isExpanded) Int.MAX_VALUE else 1, // Use Int.MAX_VALUE for no limit when expanded
//                        overflow = TextOverflow.Ellipsis // Add ellipsis when text is truncated
//                    )
//                }
//                PlayButton(navController, workout)
//                EditButton(workout.workoutPlanId, workout.planName, viewModel)
//                DeleteButton(workout.workoutPlanId, viewModel)
//            }
//            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
//
//
//            val pagerState = rememberPagerState()
//            HorizontalPager(
//                count = exercisesDetails.size,
//                state = pagerState,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) { page ->
//                val exercise = exercisesDetails[page]
//                ExercisePage(exercise, navController)
//            }
//        }
//    }
//}

@Composable
fun ExercisePage(exercise: Exercise, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        AsyncImage(
            model = exercise.imageUrl,
            contentDescription = "Exercise Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        )

    }
}


@Composable
fun ButtonPhysical(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
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
                    .height(120.dp) // Adjust size as needed
                    .clickable(onClick = { navController.navigate("physicalCategory") }
                    ))

        }
    }
}

@Composable
fun MentalButton(navController: NavController) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
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
                    .clickable(onClick = { navController.navigate("zenCategory") }
                    ))
        }
    }
}



