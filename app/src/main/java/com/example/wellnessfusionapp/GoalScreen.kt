package com.example.wellnessfusionapp

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.GoalCategory
import com.example.wellnessfusionapp.Models.GoalType
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.google.firebase.Timestamp
import java.util.UUID



// Only two goals are working at the moment, the rest will be left for future development
// Exercise Weight and Time Spent, exercise weight makes the user able to set a goal for a specific exercise, input current weight he is doing in the exercise and desired value to be achieved
// Time Spent is a goal for the user to set a goal for a specific exercise, input current time spent in the exercise and desired time to be achieved

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavController, viewModel: MainViewModel) {
    val predefinedGoalTypes = listOf(
        GoalType("1", "Exercise Weight", GoalCategory.PHYSICAL, R.drawable.icon_weight_progress_goal),
        GoalType("2", "Time Spent", GoalCategory.MENTAL, R.drawable.icon_meditation_goal),
        GoalType("3", "Workout Days", GoalCategory.PHYSICAL, R.drawable.icon_workout_days_goal),
        GoalType("4", "Meditation Days", GoalCategory.MENTAL,R.drawable.icon_workout_days_goal),
        GoalType("5", "Cardio Time", GoalCategory.PHYSICAL, R.drawable.icon_mobility_goal),
    )
    var selectedGoalType by remember { mutableStateOf<GoalType?>(null) }

    Scaffold(
        topBar = { GoalScreenTopAppBar(navController) },

        bottomBar =
        {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.Black)
        ) {

            GoalScreenContent(
                predefinedGoalTypes = predefinedGoalTypes,
                selectedGoalType = selectedGoalType,
                onGoalTypeSelected = { selectedGoalType = it },
                viewModel = viewModel,
                onGoalAdded = { selectedGoalType = null },
                navController = navController
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreenTopAppBar(navController: NavController) {

    val textFont = (FontFamily(Font(R.font.zendots_regular)))

    TopAppBar(
        colors = topAppBarColors(
            containerColor = Color.Black,
        ),
        title = { Text("Set Your Goals", color = Color.White, fontFamily = textFont) },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Gray, CircleShape)
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun GoalScreenContent(
    predefinedGoalTypes: List<GoalType>,
    selectedGoalType: GoalType?,
    onGoalTypeSelected: (GoalType) -> Unit,
    viewModel: MainViewModel,
    onGoalAdded: () -> Unit,
    navController: NavController
) {
    if (selectedGoalType != null) {
        GoalDetailScreen(
            viewModel = viewModel,
            goalType = selectedGoalType,
            onGoalAdded = onGoalAdded,
            navController = navController
        )
    } else {
        GoalSelectionList(predefinedGoalTypes, onGoalTypeSelected)
    }
}

@Composable
fun GoalSelectionList(predefinedGoalTypes: List<GoalType>, onGoalTypeSelected: (GoalType) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        items(predefinedGoalTypes) { goalType ->
            GoalTypeItem(goalType = goalType, onGoalTypeSelected = onGoalTypeSelected)
        }
    }
}

@Composable
fun GoalTypeItem(goalType: GoalType, onGoalTypeSelected: (GoalType) -> Unit) {
    val backgroundColor = when (goalType.category) {
        GoalCategory.PHYSICAL -> Color(0xffFE7316) // Orange for physical
        GoalCategory.MENTAL -> Color(0xff1666ba) // Blue for mental
    }

    val textColor = when (goalType.category) {
        GoalCategory.PHYSICAL -> Color(0xffFE7316) // Black text for physical
        GoalCategory.MENTAL -> Color(0xff1666ba) // White text for mental
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onGoalTypeSelected(goalType) }
            .padding(8.dp)
            .border(6.dp, backgroundColor, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
            Row(modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(text = goalType.name, style = MaterialTheme.typography.bodyLarge, fontSize = 16.sp, color = textColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painterResource(goalType.goalIcon),
                        contentDescription = "Goal Icon",
                        modifier = Modifier.size(100.dp)
                    )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: MainViewModel,
    goalType: GoalType, // Update to use GoalType
    onGoalAdded: () -> Unit,
    navController: NavController,
    planType: WorkoutPlan? = null
) {
    LaunchedEffect(key1 = true) {
        viewModel.fetchAllExercises()
    }

    val buttonColorChange = when (goalType.category) {
        GoalCategory.PHYSICAL -> Color(0xffFE7316) // Orange for physical
        GoalCategory.MENTAL -> Color(0xff1666ba) // Blue for mental
    }
    val textColorChange = when (goalType.category) {
        GoalCategory.PHYSICAL -> Color(0xffFE7316) // Black text for physical
        GoalCategory.MENTAL -> Color(0xff1666ba) // White text for mental
    }

    val exercises by viewModel.exercisesForDropdown.observeAsState(emptyList())
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    val currentValue by remember { mutableStateOf("") }
    var initialValue by remember { mutableStateOf("") }
    var desiredValue by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val startDate by remember { mutableStateOf(Timestamp.now()) }

    val filteredExercises = exercises.filter { exercise ->
        if(goalType.category == GoalCategory.PHYSICAL && exercise.workoutType == "Physical") {
            true
        } else if(goalType.category == GoalCategory.MENTAL && exercise.workoutType == "Mental") {
            true
        } else {
            false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Use goalType.name for the title
        Text(
            goalType.name,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 30.sp,
            color = textColorChange,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))

        if (goalType.name == "Time Spent") {
            // UI for setting meditation goal`
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(10.dp),
            ) {
                Column {
                    OutlinedTextField(
                        value = selectedExercise?.name ?: "Select Exercise",
                        onValueChange = { expanded = !expanded },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        label = { Text("Exercise") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = !expanded
                            },
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(320.dp)
                            .height(300.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(14.dp),
                    ) {
                        filteredExercises.forEach { exercise ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedExercise = exercise
                                    expanded = false
                                },
                                text = { Text(exercise.name) }
                            )
                            HorizontalDivider(thickness = 3.dp, color = Color.LightGray)
                        }

                    }
                }
                // Desired meditation time
                OutlinedTextField(
                    value = initialValue,
                    onValueChange = { initialValue = it },
                    label = { Text("Current Time Spent (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = desiredValue,
                    onValueChange = { desiredValue = it },
                    label = { Text("Desired Time to Spend (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    buttonColorChange
                ),
                onClick = {
                    val now = Timestamp.now()
                    val UID = UUID.randomUUID().toString()

                    val newGoal =
                        if (goalType.name == "Time Spent" && desiredValue.isNotEmpty()) {
                            // Assuming Meditation Time Progress doesn't require an associated exercise
                            Goal(
                                id = UID,
                                type = goalType, // Directly use goalType, which is already a GoalType instance
                                typeId = goalType.name, // Use goalType's id for typeId if needed
                                description = selectedExercise!!.name,
                                desiredValue = desiredValue.toInt(),
                                initialValue = initialValue.toInt(),
                                exerciseId = "12", // No exerciseId for meditation goals
                                startDate = now,
                                endDate = now,
                                status = "active",
                                workoutDays = null
                            )
                        }else {
                            null
                        }

                    newGoal?.let {
                        viewModel.addGoal(it)
                        onGoalAdded()
                        navController.popBackStack()
                    }
                }) {
                Text("Add Goal")
            }

        } else if (
            goalType.name == "Exercise Weight"
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Set your goal for the following exercise",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column {
                        OutlinedTextField(
                            value = selectedExercise?.name ?: "Select Exercise",
                            onValueChange = { expanded = !expanded },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            },
                            label = { Text("Exercise") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = !expanded
                                },
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(320.dp)
                                .height(300.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(14.dp),
                        ) {
                            filteredExercises.forEach { exercise ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedExercise = exercise
                                        expanded = false
                                    },
                                    text = { Text(exercise.name) }
                                )
                                HorizontalDivider(thickness = 3.dp, color = Color.LightGray)
                            }
                        }
                    }

                    // Render text fields specific to other goal types
                    OutlinedTextField(
                        value = initialValue,
                        onValueChange = { initialValue = it },
                        label = { Text("Current Exercise Weight: KG") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = desiredValue,
                        onValueChange = { desiredValue = it },
                        label = { Text("Goal: KG") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )




                Spacer(Modifier.height(10.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        buttonColorChange
                    ),
                    onClick = {
                        val now = Timestamp.now()
                        val UID = UUID.randomUUID().toString()

                        val newGoal =
                            if (goalType.name == "Exercise Weight" && selectedExercise != null && initialValue.isNotEmpty() && desiredValue.isNotEmpty()) {
                                // For exercise-related goals that require a selected exercise
                                Goal(
                                    id = UID,
                                    type = goalType,
                                    typeId = goalType.name,
                                    description = selectedExercise!!.name,
                                    desiredValue = desiredValue.toInt(),
                                    initialValue = initialValue.toInt(),
                                    exerciseId = selectedExercise!!.id ?: "",
                                    startDate = now,
                                    endDate = now,
                                    status = "active",
                                    workoutDays = null
                                )
                            } else {
                                null
                            }

                        newGoal?.let {
                            viewModel.addGoal(it)
                            onGoalAdded()
                            navController.popBackStack()
                        }
                    }) {
                    Text("Add Goal")
                }
            }
            }
        }
    }
}
