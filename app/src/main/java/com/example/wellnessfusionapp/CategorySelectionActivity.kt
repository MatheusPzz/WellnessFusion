@file:Suppress("UNUSED_EXPRESSION")

package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.DataTypes.WorkoutType
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhysicalCategoryScreen(navController: NavController, viewModel: CategoryViewModel) {
    // ... your code
    val categories = viewModel.physicalCategory.collectAsState().value
    CategoryScreenLayout(
        navController = navController,
        viewModel = viewModel,  // Pass viewModel here
        categories = categories,
        title = "Physical Categories",
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ZenCategoryScreen(navController: NavController, viewModel: CategoryViewModel) {
    // ... your code
    val categories = viewModel.zenCategory.collectAsState().value
    CategoryScreenLayout(
        navController = navController,
        viewModel = viewModel,  // Pass viewModel here
        categories = categories,
        title = "Zen Categories",
    )
}


@Composable
fun CategoryScreenLayout(
    navController: NavController,
    viewModel: CategoryViewModel,
    categories: List<Category>,
    title: String,
)

{
    Scaffold(
        topBar = {
            CustomTopBar(
                title = title,
                navController = navController,
                actions = {
                    IconButton(onClick = { /* TODO: Implement help action */ }) {
                        Icon(Icons.Filled.Info, contentDescription = "Help")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val selectedCategories = viewModel.getSelectedCategories()
                    val selectedCategoriesString = selectedCategories.joinToString(",")
                    Log.d("Navigation", "Navigating to exerciseSelection with categories: $selectedCategoriesString")
                    navController.navigate("exerciseSelection/$selectedCategoriesString")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Submit")
            }
        }
    )
    { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Introductory text above the categories
            Text(
                text = "Pick up to 3 categories to improve your wellness.",
                style = MaterialTheme.typography.titleMedium,
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
                                categoryId = categories[index].id,
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
        Column(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        navigationIcon = {
            if (showBackButton) {
                run {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            } else null
        },
        title = { Text(text = title) },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

