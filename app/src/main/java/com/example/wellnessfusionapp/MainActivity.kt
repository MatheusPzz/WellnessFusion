package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ui.theme.WellnessFusionAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WellnessFusionAppTheme {
                Main()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main() {
    val navController = rememberNavController()

    MainNavHost(navController, viewModel())

}


@Composable
fun MainNavHost(navController: NavController, categoryViewModel: CategoryViewModel) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = "authentication"
    ) {

        //Authentication destinations
        navigation(startDestination = "login", route = "authentication") {
            composable("login") {
                // Assume LoginScreen is correctly implemented
                LoginScreen(navController = navController) {
                    // This lambda is called on login success; navigate to the main content
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            composable("signUp") { SignUpScreen(navController = navController) }
        }


        // Main content destinations

        navigation(startDestination = "home", route = "mainContent") {
            composable("home") { HomeScreen(navController, categoryViewModel) }
        }
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
            // Now extract the argument
            val categoriesString = backStackEntry.arguments?.getString("selectedCategories") ?: ""
            ExerciseSelectionScreen(
                navController = navController,
                viewModel = viewModel(),
                selectedCategoryIds = categoriesString.split(",").filterNot { it.isBlank() }
            )
        }
    }
}


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(title: String, navController: NavController, onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Wellness Fusion", style = MaterialTheme.typography.titleLarge)
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
            }) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Sign Out"
                )
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun WellnessScreenPreview() {
    WellnessFusionAppTheme {
        Main()
    }
}

//@Composable
//fun authState(auth: FirebaseAuth): State<FirebaseUser?> {
//    val currentUser = remember { mutableStateOf(auth.currentUser) }
//
//    DisposableEffect(auth) {
//        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            currentUser.value = firebaseAuth.currentUser
//        }
//        auth.addAuthStateListener(listener)
//        onDispose {
//            auth.removeAuthStateListener(listener)
//        }
//    }
//
//    return currentUser
//}
