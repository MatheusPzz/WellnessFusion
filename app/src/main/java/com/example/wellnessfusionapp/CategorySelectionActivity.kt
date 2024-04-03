@file:Suppress("UNUSED_EXPRESSION")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel


/*
 * This composable contains the UI for the category selection screen.
 * In the category selection screen the user can select the categories they want to workout in.
 * The user can select multiple categories.
 * The user can navigate to the exercise selection screen by clicking the "Submit Choices" button.
 * But also needs to select at least one category
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhysicalCategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel,
) {
    /*
     * Get the context of the current screen for toast
     */
    val context = LocalContext.current
    /*
     * Collect the physical categories from the view model
     */
    val categories = viewModel.physicalCategory.collectAsState().value

    /*
     * Font for the text in the screen
     */
    val textFont = FontFamily(
        Font(R.font.zendots_regular, FontWeight.Normal),
    )
    /*
     * Scaffold setup for the screen
     */
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                title = {
                    Text(text = "Physical Categories", fontFamily = textFont)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        /*
                         * Navigates back to the previous screen
                         * and clears the category selections
                         */
                        navController.navigateUp()
                        viewModel.clearCategorySelections()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    )

    /*
     * Main content of the screen
     */
    { padding ->
        Box(
            modifier = Modifier
                .alpha(0.9F)
                .background(Color.Black)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f))
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Pick Your Workout Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = textFont,
                            color = Color(0xFFFE7316),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    HorizontalDivider(color = Color(0xFFFE7316))
                }
                /*
                 * Lazy grid to display the categories
                 */
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(500.dp)

                ) {
                    /*
                     * Taking the size of the categories list and displaying the categories
                     */
                    items(categories.size) { index ->
                        CategoryItem(
                            category = categories[index],
                            onCategorySelected = { isSelected ->
                                /*
                                 * Toggle the selection state of the category
                                 * calls the Type of the workout
                                 * calls the Category id of the category
                                 * Selection state of the category is changed
                                 */
                                viewModel.updateCategorySelection(
                                    type = WorkoutType.PHYSICAL,
                                    categoryId = categories[index].categoryId,
                                    isSelected = isSelected
                                )
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFFFE7316),
                        ),
                        onClick = {
                            /*
                             * Get the selected categories from the view model
                             */
                            val selectedCategories = viewModel.getSelectedCategoryIds()

                            /*
                             * Checks if it is not empty
                             * Then navigates to the exercise selection screen with the selected categories passed as argument
                             */
                            if (selectedCategories.isNotEmpty()) {
                                val selectedCategoriesString = selectedCategories.joinToString(",")
                                Log.d("Navigation", "Navigating to exerciseSelection with categories: $selectedCategoriesString")
                                /*
                                 * navigates with the categories to exercise selection screen
                                 */
                                navController.navigate("exerciseSelection/$selectedCategoriesString") {
                                    /*
                                     * Pops the back stack till the home screen
                                     */
                                    popUpTo("home") {
                                        saveState = false
                                    } // Adjust the "home" route as needed
                                    launchSingleTop = true
                                }
                                /*
                                 * Else tells the user to select at least a category
                                 */
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select at least one category",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(
                            "Submit Choices",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontFamily = textFont,
                        )
                    }
                }
            }
        }
    }
}



/*
 * That's exactly the same as the PhysicalCategoryScreen, but with the Zen categories
 * Briefly explaining the code:
 * - Get the context of the current screen for toast
 * - Collect the zen categories from the view model
 * - Navigate with the selected categories to the exercise selection screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ZenCategoryScreen(navController: NavController, viewModel: CategoryViewModel) {
    val context = LocalContext.current
    // ... your code
    val categories = viewModel.zenCategory.collectAsState().value

    val textFont = FontFamily(
        Font(R.font.zendots_regular, FontWeight.Normal),
    )
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff1666ba),
                ),
                title = {
                    Text(text = "Mental Categories", fontFamily = textFont)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                        viewModel.clearCategorySelections()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .alpha(0.90F)
        )
        {
            Image(
                painter = painterResource(id = R.drawable.mental_background),
                contentDescription = "Mental",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .blur(1.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f))
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Pick Your Workout Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = textFont,
                            color = Color(0xff1666ba),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    HorizontalDivider(color = Color(0xff1666ba))
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(500.dp)
                ) {
                    items(categories.size) { index ->
                        CategoryItem(
                            category = categories[index],
                            onCategorySelected = { isSelected ->
                                viewModel.updateCategorySelection(
                                    type = WorkoutType.ZEN,
                                    categoryId = categories[index].categoryId,
                                    isSelected = isSelected
                                )
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(40.dp),
                        onClick = {
                            val selectedCategories = viewModel.getSelectedCategoryIds()

                            if (selectedCategories.isNotEmpty()) {
                                val selectedCategoriesString = selectedCategories.joinToString(",")
                                Log.d(
                                    "Navigation",
                                    "Navigating to exerciseSelection with categories: $selectedCategoriesString"
                                )

                                navController.navigate("exerciseSelection/$selectedCategoriesString") {
                                    popUpTo("home") {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                }
                            } else {

                                Toast.makeText(
                                    context,
                                    "Please select at least one category",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    ) {
                        Text("Submit", fontFamily = textFont, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}


/*
 * This composable contains the UI for the category item.
 * Each category item has its style,
 * The image of the category,
 * The name of the category,
 * The color of the category,
 * The selection state of the category is the same
 */
@Composable
fun CategoryItem(category: Category, onCategorySelected: (Boolean) -> Unit) {

    val zenCategories = listOf("Meditation", "Yoga", "Mindfulness", "Breathing", "Stretching", "Gaming")
    val physicalCategories = listOf("Chest", "Shoulders", "Abs", "Legs", "Arms", "Back")

    val text = FontFamily(
        Font(R.font.zendots_regular, FontWeight.Normal),
    )
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .padding(10.dp)
            .clickable { onCategorySelected(!category.isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (category.isSelected) Color.Gray
            else Color.Black
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.4f)),

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
        ) {
            Image(
                painter = painterResource(id = category.image),
                contentDescription = category.name,
                modifier = Modifier
                    .fillMaxSize() // Change this to fillMaxSize to cover the background
                    .align(Alignment.Center)
                    .scale(scale = 1f)
                    .graphicsLayer {
                        alpha =
                            if (category.isSelected) 0.25f else 1f // Adjust opacity based on selection
                    },
                contentScale = ContentScale.Crop

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center) // Align the column in the center of the Box
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
//                Spacer(modifier = Modifier.height(100.dp)) // Spacer used to push the text down if needed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(shape = CircleShape)
                        .graphicsLayer {
                            alpha = 0.9f
                        }
                        .background(Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        /*
                         * Here are just performing a check to see if the category is a zen category or a physical category,
                         * if contains the category name in the zenCategories list, then the color of the category is blue
                         * else if contains the category name in the physicalCategories list, then the color of the category is orange
                         */
                        if (zenCategories.contains(category.name))
                            Text(
                                text = category.name,
                                fontFamily = text,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xff1666ba),
                            )
                        else if (physicalCategories.contains(category.name)){
                            Text(
                                text = category.name,
                                fontFamily = text,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFE7316),
                            )
                        }
                    }
                }
            }
        }
    }
}





