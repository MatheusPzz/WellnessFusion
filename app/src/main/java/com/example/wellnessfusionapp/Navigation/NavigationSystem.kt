package com.example.wellnessfusionapp.Navigation

import ExerciseSelection
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.wellnessfusionapp.FavoritesScreen
import com.example.wellnessfusionapp.HomeScreen
import com.example.wellnessfusionapp.LoginScreen
import com.example.wellnessfusionapp.PhysicalCategoryScreen
import com.example.wellnessfusionapp.ProfileScreen
import com.example.wellnessfusionapp.SettingsScreen
import com.example.wellnessfusionapp.SignUpScreen
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import com.example.wellnessfusionapp.WorkoutPlanScreen
import com.example.wellnessfusionapp.ZenCategoryScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainNavHost(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    startDestination: String,
    exerciseSelectionViewModel: ExerciseSelectionViewModel,
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = startDestination
    ) {
        composable("login") {
            // LoginScreen implementation
            LoginScreen(navController = navController) {
                // Navigate to the main content upon login success
                navController.navigate("home") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        composable("signUp") { SignUpScreen(navController = navController) }
        composable("home") { HomeScreen(navController, categoryViewModel) }

        composable("profile") { ProfileScreen(navController) }

        composable("favorites") { FavoritesScreen(navController) }

        composable("settings") { SettingsScreen(navController) }

        composable("physicalCategory") { PhysicalCategoryScreen(navController, categoryViewModel) }
        composable("zenCategory") { ZenCategoryScreen(navController, categoryViewModel) }
        composable(
            route = "exerciseSelection/{selectedCategories}",
            arguments = listOf(navArgument("selectedCategories") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract the string of selected categories
            val selectedCategoriesString =
                backStackEntry.arguments?.getString("selectedCategories") ?: ""
            // Optionally, convert the string back to a list if needed for your logic
            val selectedCategoriesList =
                selectedCategoriesString.split(",").filter { it.isNotBlank() }
            ExerciseSelection(
                exerciseSelectionViewModel = exerciseSelectionViewModel,
                navController = navController,
                viewModel = categoryViewModel
            )
        }
        composable( "generatePlan") {
            WorkoutPlanScreen(navController, exerciseSelectionViewModel)

        }
    }
}

    // Bottom Navigation System
    data class NavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val route: String,
    )

    @Composable
    fun BottomNavBar(navController: NavController) {
        val items = listOf(
            NavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "home"),
            NavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, "profile"),
            NavigationItem(
                "Favorites",
                Icons.Filled.Favorite,
                Icons.Outlined.Favorite,
                "favorites"
            ),
            NavigationItem(
                "Settings",
                Icons.Filled.Settings,
                Icons.Outlined.Settings,
                "settings"
            )
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = { Text(item.title) },
                )
            }
        }
    }


// Main Top Bar System

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainTopBar(title: String, navController: NavController, onMenuClick: () -> Unit) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = "Wellness Fusion", style = MaterialTheme.typography.titleLarge)
            },

            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu Drawer"
                    )
                }
            },

            actions = {
                IconButton(onClick = {
                    navController.navigate("Login")
                    FirebaseAuth.getInstance().signOut()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Sign Out"
                    )
                }
            }
        )
    }