import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Models.WorkoutType
import com.example.wellnessfusionapp.R


/*
 In this composable function,
    - A top app bar is displayed with the title "Exercise Selection".
    - A bottom bar is displayed with a button to create a workout plan.
    - The categories with their exercises are displayed in a LazyColumn.
    - The user is able to expand and colapse to see the exercises in each category.
    - USer can select exercises by clicking on the checkbox.
    - The user can create a workout plan by clicking on the "Create Plan" button.
    - A dialog is displayed to enter the workout plan name.
    - User saves his workout and goes to session workout screen to execute the workout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelection(
    exerciseSelectionViewModel: ExerciseSelectionViewModel,
    navController: NavController,
    viewModel: CategoryViewModel
) {
    // local context for toast messages
    val context = LocalContext.current

    // variable to store the plan name
    val planName by remember { mutableStateOf("") }

    // gradient colors for the background of the page
    val backgroundGradient = listOf(
        Color(0xffFF8D0F),
        Color.Black,
    )
    // Collects the grouped exercises state for display
    val groupedExercises by exerciseSelectionViewModel.groupedExercisesState.collectAsState()

    Scaffold(
        topBar = {
            ExerciseSelectionTopBar(navController, viewModel) },
        bottomBar = {
            BottomBarForCreatingPlan(
                planName,
                context,
                exerciseSelectionViewModel,
                viewModel,
                navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        backgroundGradient,
                        startY = 750f,
                        endY = 1500f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // A custom composable function to display categories with their exercises
                CategoriesWithExercises(groupedExercises, exerciseSelectionViewModel)

            }
        }
    }
}


/*
 Top app bar for the Exercise Selection screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionTopBar(navController: NavController, viewModel: CategoryViewModel) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
        ),
        title = { Text("Exercise Selection", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.clearCategorySelections()
                navController.navigateUp()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    )
}


/*
 This composable triggers the creation of a workout plan.
 It includes opening a dialog for creation and saving of this plan, with a proper name
 chosen by the user
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarForCreatingPlan(
    planName: String,
    context: android.content.Context,
    exerciseSelectionViewModel: ExerciseSelectionViewModel,
    viewModel: CategoryViewModel,
    navController: NavController
) {
    // Local state for dialog and variable to store the plan name
    var localPlanName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var textFont = FontFamily(
        Font(R.font.zendots_regular)
    )


    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp)),
            properties = DialogProperties()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = 16.dp)
            ) {
                Text("Workout Plan Name",fontFamily = textFont, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = localPlanName,
                    onValueChange = { localPlanName = it },
                    label = { Text("Workout Plan Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        // on click dismisses the dialog
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFF8D0F)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("Cancel") }
                    Button(
                        onClick = {
                            // checks if the plan name is not empty
                            if (localPlanName.isNotEmpty()) {

                                // gets the selected workout type
                                val workoutType = viewModel.getSelectedWorkoutType()

                                // if it is not null, saves the workout plan
                                if (workoutType != null) {
                                    exerciseSelectionViewModel.saveWorkoutPlan(
                                        navController,
                                        localPlanName,
                                        workoutType
                                    )
                                    Toast.makeText(
                                        context,
                                        "Workout Plan Created",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.clearCategorySelections()
                                    exerciseSelectionViewModel.clearExerciseSelections()
                                    showDialog = false
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please select a category",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a plan name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFF8D0F))
                    ) { Text("Save") }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFF8D0F)),
            onClick = {
                if (exerciseSelectionViewModel.hasSelectedExercises) {
                    showDialog = true
                } else {
                    Toast.makeText(
                        context,
                        "Please select at least one exercise",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            shape = ShapeDefaults.Medium,
        ) {
            Text("Create Plan")
        }
    }
}


/*
 This composable function displays the categories with their exercises together.
 */
@Composable
fun CategoriesWithExercises(
    groupedExercises: Map<String, List<Exercise>>,
    viewModel: ExerciseSelectionViewModel
) {

    // lazy column that displays the categories with their exercises
    LazyColumn {
        groupedExercises.forEach { (categoryName, exercises) ->
            item {
                ExpandableCard(categoryName, exercises, viewModel)
            }
        }
    }
}


/*
 This composable is the setup for the category card, once expanded exercises are shown.
 */
@Composable
fun ExpandableCard(
    categoryName: String,
    exercises: List<Exercise>,
    viewModel: ExerciseSelectionViewModel
) {

    // local state for the card expansion
    var isExpanded by remember { mutableStateOf(false) }
    val textColor = Color(0xffFF8D0F)
    val textFont = FontFamily(
        Font(R.font.zendots_regular)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .shadow(5.dp),
        colors = CardColors(
            Color.Black,
            Color.Black,
            Color.Black,
            Color.Black
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.exercise_selection_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Column {
                Box(
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                        .padding(3.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            categoryName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = textColor,
                            fontFamily = textFont,
                            modifier = Modifier.padding(8.dp),
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp)
                                .clickable { isExpanded = !isExpanded },
                            tint = textColor
                        )
                    }
                }
                // Animated visibility for the exercises, exercise detail list is shown once it is expanded
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxSize()
                    ) {
                        // list of exercises
                        exercises.forEach { exercise ->
                            ExerciseDetail(exercise, viewModel)
                        }
                    }
                }
            }
        }
    }
}


/*
 This composable function displays the details of each exercise
 as name, picture and description.
 */
@Composable
fun ExerciseDetail(exercise: Exercise, viewModel: ExerciseSelectionViewModel) {

    // local state for the checkbox selection
    val isSelected = remember { mutableStateOf(viewModel.isExerciseSelected(exercise)) }
    val textFont = FontFamily(
        Font(R.font.zendots_regular)
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // loading the exercise image
        AsyncImage(
            model = exercise.imageUrl,
            contentDescription = "Exercise Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )


        // displaying exercise name and description with a checkbox for selection
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = exercise.description ?: "No description",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {

        // Checkbox for exercise selection, it triggers a view model function (boolean value), for each exercise selected state change
        // Then passes the value of the selected ones to another viewModel function that holds the values of the selected exercises, later on passed to the workout plan
        Checkbox(
            modifier = Modifier.height(50.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue,
                uncheckedColor = Color.White
            ),
            checked = isSelected.value,
            onCheckedChange = {
                viewModel.toggleExerciseSelection(exercise)
                isSelected.value = viewModel.isExerciseSelected(exercise)
            },
        )
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        thickness = 5.dp
    )
}


//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Preview(showBackground = true)
//@Composable
//fun ExerciseSelectionPreview() {
//    val navController = rememberNavController()
//    val viewModel = remember { CategoryViewModel() }
//
//    Scaffold(
//        topBar = { },
//        bottomBar = { },
//        content = {
//            ExerciseSelection(
//                exerciseSelectionViewModel = ExerciseSelectionViewModel(),
//                navController = navController,
//                viewModel = viewModel
//            )
//        }
//    )
//}