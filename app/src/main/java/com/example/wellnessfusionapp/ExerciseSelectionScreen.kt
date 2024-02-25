import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Checkbox
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelection(
    exerciseSelectionViewModel: ExerciseSelectionViewModel,
    navController: NavController,
    viewModel: CategoryViewModel
) {
//    val exercises = exerciseSelectionViewModel.state.value
    val groupedExercises by exerciseSelectionViewModel.groupedExercisesState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Exercise Selection") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearCategorySelections()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val selectedExercises = exerciseSelectionViewModel.getSelectedExercises()
                        if(selectedExercises.isNotEmpty()){
                            val selectedExercisesString = selectedExercises.joinToString("")
                            Log.d(
                                "ExerciseSelection",
                                "Selected exercises: $selectedExercisesString"
                            )

                            navController.navigate("workoutPlan/$selectedExercisesString")
                        }else {
                            Log.d("ExerciseSelection", "No exercises selected")
                        }
                    }


                ) {
                        Text(text = "Generate Plan")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoriesWithExercises(groupedExercises = groupedExercises)
        }
    }
}
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ExerciseDetail(exercise: Exercise, viewModel: ExerciseSelectionViewModel){
    var isSelected: Boolean by remember { mutableStateOf(exercise.isSelected) }


    Row(modifier = Modifier.padding(2.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
            ,
            verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.Start
        ) {
            AsyncImage(model = exercise.imageUrl , contentDescription = "Exercise Image", modifier = Modifier.size(160.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)

        }
        Column (
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxHeight()
        ){
            Text(text = exercise.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
        }
    }
    Column (
        modifier = Modifier
            .padding(6.dp)
            .fillMaxHeight()
    ) {
        Checkbox(checked =isSelected , onCheckedChange = {
            isSelected = it
            viewModel.toggleExerciseSelection(exercise)
        } )
    }
}




@Composable
fun ExpandableCard(categoryName: String, exercises: List<Exercise>, viewModel: ExerciseSelectionViewModel) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleMedium
            )
            AnimatedVisibility(visible = isExpanded) {
                Column (
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                ){
                    exercises.forEach { exercise ->
                        Divider(Modifier.padding(vertical = 10.dp))
                        ExerciseDetail(exercise = exercise, viewModel = viewModel)
                    }
                    }
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }


// This is a list of categories with exercises inside

@Composable
fun ExercisesList(exercises: List<Exercise>) {
    val groupedExercises = exercises.groupBy { it.categoryName }

    LazyColumn{
        groupedExercises.forEach { (categoryName,exercisesForCategory) ->
            items(exercisesForCategory) { exercise ->
                ExerciseCard(exercise = exercise)
            }
        }
    }
}



// This padding for outside of the card
@Composable
fun CategoriesWithExercises(groupedExercises: Map<String, List<Exercise>>) {
    LazyColumn(modifier = Modifier.padding(horizontal = 25.dp)) {
        groupedExercises.forEach { (categoryName, exercises) ->
            item {
                ExpandableCard(categoryName = categoryName, exercises = exercises, viewModel = ExerciseSelectionViewModel())
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = exercise.categoryName, style = MaterialTheme.typography.bodyMedium)
            Divider(modifier = Modifier.padding(vertical = 5.dp))
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(text = exercise.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Reps: ${exercise.reps}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sets: ${exercise.sets}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun ExerciseSelectionPreview() {
    val navController = rememberNavController()
    val viewModel = remember { CategoryViewModel() }

    Scaffold(
        topBar = { },
        bottomBar = { },
        content = {
            ExerciseSelection(
                exerciseSelectionViewModel = ExerciseSelectionViewModel(),
                navController = navController,
                viewModel = viewModel
            )
        }
    )
}