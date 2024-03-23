@file:Suppress("UNUSED_EXPRESSION")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.util.Log
import android.widget.Toast
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


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhysicalCategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel,
    title: String
) {
    val context = LocalContext.current
    val categories = viewModel.physicalCategory.collectAsState().value

    val textFont = FontFamily(
        Font(R.font.zendots_regular, FontWeight.Normal),
    )
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
                        // Navigate back
                        navController.navigateUp()
                        viewModel.clearCategorySelections()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Navigate to the info screen
                        navController.navigate("info")
                    }) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = "Info")
                    }
                }
            )
        },
        bottomBar = {
        }
    )
    { padding ->
        Box(
            modifier = Modifier
                .alpha(0.9F)
        )
        {
            Image(
                painter = painterResource(id = R.drawable.background_physical),
                contentDescription = "Physical",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .scale(1.5F)
                    .blur(1.dp)
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
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Pick Your Workout Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = textFont,
                        color = Color(0xFFFE7316),
                        fontWeight = FontWeight.ExtraBold
                    )
                    HorizontalDivider()
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(500.dp)

                ) {
                    items(categories.size) { index ->
                        CategoryItem(
                            category = categories[index],
                            onCategorySelected = { isSelected ->
                                // Toggle the selection state
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
                            Color.Black,
                        ),
                        onClick = {
                            val selectedCategories = viewModel.getSelectedCategoryIds()

                            if (selectedCategories.isNotEmpty()) {
                                val selectedCategoriesString = selectedCategories.joinToString(",")
                                Log.d(
                                    "Navigation",
                                    "Navigating to exerciseSelection with categories: $selectedCategoriesString"
                                )

                                navController.navigate("exerciseSelection/$selectedCategoriesString") {
                                    // Adjust these parameters based on your navigation structure and needs
                                    popUpTo("home") {
                                        saveState = false
                                    } // Adjust the "home" route as needed
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
                        Text(
                            "Submit Choices",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFE7316),
                            fontFamily = textFont,

                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ZenCategoryScreen(navController: NavController, viewModel: CategoryViewModel, title: String) {
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
                        // Navigate back
                        navController.navigateUp()
                        viewModel.clearCategorySelections()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Navigate to the info screen
                        navController.navigate("info")
                    }) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = "Info")
                    }
                }
            )
        },
        bottomBar = {
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .alpha(0.95F)
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
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Introductory text above the categories
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    Text(
                        text = "Pick Your Workout Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = textFont,
                        color = Color(0xff1666ba),
                        fontWeight = FontWeight.ExtraBold
                    )
                    HorizontalDivider()
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(500.dp)
                ) {
                    items(categories.size) { index ->
                        CategoryItem(
                            category = categories[index],
                            onCategorySelected = { isSelected ->
                                // Toggle the selection state
                                viewModel.updateCategorySelection(
                                    type = if (title == "Physical Categories") WorkoutType.PHYSICAL else WorkoutType.ZEN,
//                                type = if (title == "Zen Categories") WorkoutType.ZEN else WorkoutType.PHYSICAL,
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
                                    // Adjust these parameters based on your navigation structure and needs
                                    popUpTo("home") {
                                        saveState = false
                                    } // Adjust the "home" route as needed
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
                        Text("Submit", fontFamily = textFont, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItem(category: Category, onCategorySelected: (Boolean) -> Unit) {

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
        )

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
                        Text(
                            text = category.name,
                            fontFamily = text,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFE7316),
                            // Center text horizontally in the column
                        )
                    }
                }
            }
        }
    }
}


