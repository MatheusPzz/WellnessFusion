package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White,  // Lighter shade of blue
            Color.White
        )
    )
    val topBarGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xffe85d04), // Dark navy blue
            Color(0xffFF8D0F)  // Lighter shade of blue
        )

    )
    val textColor = contentColorFor(Color(0xffFF8D0F))

    val fontTest = FontFamily(
        Font(R.font.zendots_regular)
    )


    val userName by viewModel.userName.collectAsState()
    val userProfilePictureUrl by viewModel2.profilePictureUrl.collectAsState()


    LaunchedEffect(userId) {
        viewModel.fetchUserName()
    }

    Scaffold(

        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .height(65.dp),
                title = {
                },

                navigationIcon = {

                    Row(
                        modifier =
                        Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .clip(CircleShape)
                                .clickable { navController.navigate("UserProfile") }
                        ) {

                            val painter = if (userProfilePictureUrl != null) {
                                rememberAsyncImagePainter(model = userProfilePictureUrl)
                            } else {
                                painterResource(id = R.drawable.ic_launcher_foreground)
                            }

                            Image(
                                painter = painter,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .border(2.5.dp, Color.Black, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Text for user's name and welcome message integrated directly beside the profile icon
                        Column(
                            modifier = Modifier.padding(start = 40.dp)
                        ) {
                            Text(
                                text = "Hi, $userName",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = fontTest
                            )
                            Text(
                                text = "Welcome",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = fontTest

                            )
                        }

                    }

                },
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    //button physical
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
                                    .height(120.dp) // Adjust size as needed
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
                    Spacer(modifier = Modifier.height(20.dp))
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
                                    .height(120.dp) // Adjust size as needed
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
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Text(
                            "Active Goals",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xffFE7316),
                            fontFamily = fontTest
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
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(15.dp))
                    GoalsDashboard(viewModel = MainViewModel(), navController = navController)
                    Spacer(modifier = Modifier.height(15.dp))

                }

            }
        }
    }

}


@Composable
fun GoalsDashboard(viewModel: MainViewModel, navController: NavController) {
    val goals by viewModel.goals.observeAsState(initial = emptyList())
    var goalToUpdate by remember { mutableStateOf<Goal?>(null) }


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
                    onUpdateClick = { goalToUpdate = it }
                )
            }
        }
    }


    // Mostra o UpdateGoalDialog se goalToUpdate não for null
    goalToUpdate?.let { goal ->
        UpdateGoalDialog(goal = goal, onDismiss = { goalToUpdate = null }) { updatedGoal ->
            viewModel.updateGoal(updatedGoal)
            Log.d("GoalScreen", "Goal updated: $updatedGoal")
            viewModel.saveProgressUpdate(updatedGoal.id, updatedGoal.currentValue.toFloat())
            Log.d("GoalScreen", "Progress updated: ${updatedGoal.currentValue}")
            goalToUpdate = null
        }
    }
}

@Composable
fun UpdateGoalDialog(goal: Goal, onDismiss: () -> Unit, onUpdate: (Goal) -> Unit) {
    var newValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Update Goal") },
        text = {
            Column {
                Text("Current Progress: ${goal.currentValue}/${goal.desiredValue}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text("New Progress") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newValue.isNotEmpty()) {
                        val updatedGoal = goal.copy(currentValue = newValue.toInt())
                        onUpdate(updatedGoal)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun GoalItem(
    goal: Goal,
    navController: NavController,
    onUpdateClick: (Goal) -> Unit // Callback quando o botão de atualizar é clicado
) {
    val progress = calculateProgress(goal.currentValue.toFloat(), goal.initialValue.toFloat(), goal.desiredValue.toFloat())

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

fun calculateProgress(current: Float, initial: Float, target: Float): Float {
    // Ensure no division by zero and logical target > initial
    if (target <= initial) return 1f
    val adjustedCurrent = current - initial
    val range = target - initial
    val progress = adjustedCurrent / range
    return progress.coerceIn(0f, 1f) // Ensure the progress is between 0% and 100%
}


@Composable
fun GradientBackground() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0d1f2d), // Dark navy blue
            Color(0xFF5c7a92)  // Lighter shade of blue
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        // Your content goes here
    }
}


//@Composable
//fun NotesSection(viewModel2: MainViewModel, exerciseId: String) {
//    val allNotes = viewModel2.notes.observeAsState(initial = emptyMap())
//    val notes = allNotes.value[exerciseId] ?: emptyList()
//    var noteText by remember { mutableStateOf("") }
//
//
//    fun formatTimestamp(timestamp: Long): String {
//        val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm a", Locale.getDefault())
//        formatter.timeZone = TimeZone.getDefault()
//        return formatter.format(timestamp)
//    }
//
//    Column {
//        OutlinedTextField(value = noteText,
//            onValueChange = { noteText = it },
//            label = { Text("Note any progression here") },
//            modifier = Modifier.fillMaxWidth(),
//            trailingIcon = {
//                IconButton(onClick = {
//                    if (noteText.isNotBlank()) {
//                        viewModel2.saveNotesForUser(exerciseId, noteText)
//                        noteText = ""
//                    }
//                }) {
//                    Icon(Icons.Default.Check, contentDescription = "Save Note")
//                }
//            })
//        LazyColumn {
//            items(notes) { note ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp), // Espaço vertical entre itens
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Box(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = note.noteText,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis,
//                            modifier = Modifier.padding(end = 8.dp)
//                        )
//                    }
//                    Text(
//                        text = formatTimestamp(note.timestamp),
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}


//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//            ) {
//                Text("Log your activity here", style = MaterialTheme.typography.bodyMedium)
//                NotesSection(viewModel2, exerciseId)
//
//            }


//@Composable
//private fun WorkoutOptionWithDescription(
//    title: String,
//    description: String,
//    icon: ImageVector,
//    onClick: () -> Unit,
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable(onClick = onClick)
//            .width(160.dp) // Set width to match the button and description box
//    ) {
//
//        Button(
//            onClick = onClick,
//            shape = RoundedCornerShape(0.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp) // Adjusted for a standard button height
//        ) {
//            Icon(
//                icon,
//                contentDescription = title
//            )
//            Text(text = title, style = MaterialTheme.typography.bodySmall)
//        }
//        DescriptionBox(
//            description = description,
//            modifier = Modifier.fillMaxWidth() // Ensure the description box uses the full width
//        )
//    }
//}
//
//@Composable
//fun DescriptionBox(description: String, modifier: Modifier = Modifier) {
//    Surface(
//        modifier = modifier
//            // Added padding for better visual separation
//            .heightIn(min = 80.dp), // Ensure a minimum height for the description box
//        shape = RoundedCornerShape(8.dp),
//        color = MaterialTheme.colorScheme.surfaceVariant,
//    ) {
//        Text(
//            text = description,
//            modifier = Modifier.padding(8.dp),
//            style = MaterialTheme.typography.bodySmall,
//            textAlign = TextAlign.Center
//        )
//    }
//}



