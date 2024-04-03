package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.FontRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel


/*
 * Home Screen composable function
 * This is the main screen of the app where the user can see his goals, create new ones, create new plans,
 * see his progress and look his profile page clicking on the image in the top app bar
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: CategoryViewModel,
    viewModel2: MainViewModel,
    exerciseId: String,
    userId: String
) {

    // Text and font color and style
    val textColor = contentColorFor(Color(0xffFF8D0F))
    val fontTest = FontFamily(
        Font(R.font.zendots_regular)
    )

    // extracting the userName and profile picture from firebase
    val userName by viewModel.userName.collectAsState()
    val userProfilePictureUrl by viewModel2.profilePictureUrl.collectAsState()

    //
    val ProfilePainter = if (userProfilePictureUrl != null) {
        rememberAsyncImagePainter(model = userProfilePictureUrl)
    } else {
        painterResource(id = R.drawable.ic_launcher_foreground)
    }


    /*
     * Fetching the user name from the database
     */
    LaunchedEffect(userId) {
        viewModel.fetchUserName()
    }

    /*
     * Scaffold setup start
     */
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                title = {
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /*
                         * Custom Profile picture placed in the top app bar with a box around making the effect of a circle shape
                         */
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .clickable { navController.navigate("UserProfile") }
                            ) {
                                Image(
                                    painter = ProfilePainter,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(3.dp, Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        /*
                         * User name and welcome message placed in the top app bar
                         */
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Hi, $userName",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = fontTest,
                            )
                            Text(
                                text = "Welcome Back",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = fontTest

                            )
                        }

                    }

                },
            )
        },
        /*
         * Custom NavBar used across the app
         */
        bottomBar = { BottomNavBar(navController) }
        /*
     * End of scaffold setup for the record
     */
    )

    { paddingValues ->

        /*
         * Setting an image as the background of the home screen
         */
        Box(
            modifier = Modifier
                .alpha(0.87f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.backgroundhome),
                contentDescription = "Physical",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .scale(1.5f)
                    .blur(1.dp)
            )

            /*
             * Main Column for the home screen
             */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /*
                 * Buttons Section for the home screen
                 */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Get Started, Create Your Plan",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xffFE7316),
                        fontFamily = fontTest,
                        fontSize = 19.sp
                    )
                    HorizontalDivider()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        ButtonPhysicalHome(navController = navController)
                        Spacer(modifier = Modifier.height(20.dp))
                        ButtonMental(navController = navController)
                        Spacer(modifier = Modifier.height(20.dp))
                }
                /*
                 * Goals Section for the home screen
                 */
                GoalsSection(navController = navController)
            }
        }
    }
}


/*
 * Here are creating our goal Section where the user can see their active goals and create new goals also
 * The user can also see goals progression from this page with navigation to the goal progress record
 */
@Composable
fun GoalsSection(navController: NavController) {

    val fontText = FontFamily(
        Font(R.font.zendots_regular)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Active Goals",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xffFE7316),
                fontFamily = fontText
            )
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "New Goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
                IconButton(
                    onClick = { navController.navigate("goalScreen") }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add a goal",
                        tint = Color(0xffFE7316),
                    )
                }
            }
        }
        HorizontalDivider()

        Spacer(modifier = Modifier.height(15.dp))
        GoalsDashboard(viewModel = MainViewModel(), navController = navController)
        Spacer(modifier = Modifier.height(15.dp))

    }
}

/*
 * Custom Button For mental workout type with a custom image and text
 */
@Composable
fun ButtonMental(navController: NavController) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(top = 10.dp)
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
                    .height(120.dp)
                    // On click it goes to workout saved plans
                    .clickable(onClick = { navController.navigate("savedWorkoutPlans") }
                    ))
        }
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.7f
                    }
                    .background(Color.Black)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(shape = CircleShape),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Mental", color = Color(0xffFE7316))
                Text(
                    textAlign = TextAlign.Center,
                    text = "Mind Focused Exercises",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        }
    }
}
/*
 * Custom Button For physical workout type with a custom image and text
 */
@Composable
fun ButtonPhysicalHome(navController: NavController) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(top = 10.dp)
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
                    // On click it goes to workout saved plans
                    .clickable(onClick = { navController.navigate("savedWorkoutPlans") }
                    ))
        }
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        alpha = 0.7f
                    }
                    .background(Color.Black)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(shape = CircleShape),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Physical", color = Color(0xffFE7316))
                Text(
                    textAlign = TextAlign.Center,
                    text = "Body Focused Exercises",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        }

    }
}

/*
 * Here is our custom goals dashboard, user can see his goals and interact with it
 * We are using a lazy row to display the goals horizontally
 * We are also using a custom goal item to display the goal with a progress bar
 * Card setup for the goals dashboard
 */
@Composable
fun GoalsDashboard(viewModel: MainViewModel, navController: NavController) {
    val goals by viewModel.goals.observeAsState(initial = emptyList())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.9f),
        colors = CardColors(
            Color.Black,
            Color.Black,
            Color.Black,
            Color.Black
        )
    ) {
        LazyRow {
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    navController = navController,
                )
            }
        }
    }
}


/*
 * We are using our modal class goal to get the goals from the database
 * So, each item goals parameters and dimensions are defined here,
 * We are using a circular progress bar to show the progress of each goal,
 * We have also defined a function to calculate the progress of each goal each time it is updated based on a user log
 * We are also saying that when the user clicks on a goal, he will be redirected to a goal progress page
 */
@Composable
fun GoalItem(
    goal: Goal,
    navController: NavController,
) {
    // Parameters to calculate the progress
    val progress = calculateProgress(
        goal.currentValue.toFloat(),
        goal.initialValue.toFloat(),
        goal.desiredValue.toFloat()
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(130.dp)
                .padding(8.dp)
                .clickable { navController.navigate("goalProgressRecord/${goal.id}") }
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp),
                color = Color(0xffFE7316),
                strokeWidth = 9.dp,
                trackColor = Color(0xff5c7a92),
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xffFE7316)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/*
 * Function to calculate the progress of each goal every time is updated
 */
fun calculateProgress(current: Float, initial: Float, target: Float): Float {
    // If the target is less than the initial value, the progress is 100%
    if (target <= initial) return 1f
    // Then we calculate the progress
    val adjustedCurrent = current - initial
    val range = target - initial
    // the progress is the adjusted current value divided by the range
    val progress = adjustedCurrent / range
    // returning the value of the progress, ensuring it is between 0 and 1 (0% and 100%)
    return progress.coerceIn(0f, 1f)
}
