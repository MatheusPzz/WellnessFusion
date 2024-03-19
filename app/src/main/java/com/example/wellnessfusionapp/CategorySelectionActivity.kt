@file:Suppress("UNUSED_EXPRESSION")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhysicalCategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel,
    title: String
) {
    val context = LocalContext.current
    val categories = viewModel.physicalCategory.collectAsState().value
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Physical Categories",
                navController = navController,
                viewModel = viewModel,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
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
                                popUpTo("home") { saveState = false } // Adjust the "home" route as needed
                                launchSingleTop = true
                            }                        } else {
                            Toast.makeText(
                                context,
                                "Please select at least one category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Submit Choices")
                }
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Introductory text above the categories
            Text(
                text = "Target Muscles",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(30.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp)
            ) {
                items(categories.size) { index ->
                    CategoryItem(
                        category = categories[index],
                        onCategorySelected = { isSelected ->
                            // Toggle the selection state
                            viewModel.updateCategorySelection(
                                type = if (title == "Zen Categories") WorkoutType.ZEN else WorkoutType.PHYSICAL,
                                categoryId = categories[index].categoryId,
                                isSelected = isSelected
                            )
                        }
                    )
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ZenCategoryScreen(navController: NavController, viewModel: CategoryViewModel, title: String) {
    val context = LocalContext.current
    // ... your code
    val categories = viewModel.zenCategory.collectAsState().value
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Zen Categories",
                navController = navController,
                viewModel = viewModel,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
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
                                popUpTo("home") { saveState = false } // Adjust the "home" route as needed
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
                    Text("Submit Choices")
                }
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Introductory text above the categories
            Text(
                text = "Target Mental Health",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(30.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.padding(16.dp)
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
        }
    }
}


@Composable
fun CategoryItem(category: Category, onCategorySelected: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .padding(10.dp)
            .clickable { onCategorySelected(!category.isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (category.isSelected) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surface
        )

    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                imageVector = category.icon,
                contentDescription = category.name
            )
            Text(text = category.name)
        }
    }
}

@JvmOverloads
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    viewModel: CategoryViewModel,
    title: String,
    navController: NavController,
    showBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = {
                    Log.d("Navigation", "Back button pressed")
                    viewModel.clearCategorySelections()// Call the callback when back button is pressed
                    navController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = { Text(text = title) },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}


