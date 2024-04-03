package com.example.wellnessfusionapp.Navigation

import ExerciseSelection
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.wellnessfusionapp.AchievedGoals
import com.example.wellnessfusionapp.HomeScreen
import com.example.wellnessfusionapp.InstructionScreen
import com.example.wellnessfusionapp.LogScreen
import com.example.wellnessfusionapp.LoginScreen
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.PhysicalCategoryScreen
import com.example.wellnessfusionapp.SavedWorkoutsScreen
import com.example.wellnessfusionapp.SignUpScreen
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.CreatedPlan
import com.example.wellnessfusionapp.GoalProgressRecordScreen
import com.example.wellnessfusionapp.GoalScreen
import com.example.wellnessfusionapp.LogDetails
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.ProfileScreen
import com.example.wellnessfusionapp.R
import com.example.wellnessfusionapp.R.drawable.icon_home_filled
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.example.wellnessfusionapp.ZenCategoryScreen
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.BallAnimInfo
import com.exyte.animatednavbar.animation.balltrajectory.BallAnimation
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.IndentAnimation
import com.exyte.animatednavbar.animation.indendshape.ShapeCornerRadius
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.layout.animatedNavBarMeasurePolicy
import com.exyte.animatednavbar.utils.ballTransform
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainNavHost(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    startDestination: String,
    exerciseSelectionViewModel: ExerciseSelectionViewModel,
    generatedWorkoutViewModel: GeneratedWorkoutViewModel,
    mainViewModel: MainViewModel,
    exerciseId: String,
    workoutPlanId: String,
    goals: List<Goal>,
    userId: String
) {
    NavHost(navController = navController as NavHostController, startDestination = startDestination)
    {
        // Login Routes
        composable("login") {
            LoginScreen(navController = navController) {
                navController.navigate("home") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            }
        }
        composable("signUp") { SignUpScreen(navController = navController) }

        // Main Routes
        composable("home") {
            HomeScreen(
                navController,
                categoryViewModel,
                mainViewModel,
                exerciseId = exerciseId,
                userId = userId
            )
        }
        composable("logs") { LogScreen(navController, mainViewModel, goals) }
        composable("savedWorkoutPlans") {
            SavedWorkoutsScreen(
                viewModel = generatedWorkoutViewModel,
                navController = navController
            )
        }
//        composable("settings") { SettingsScreen(navController) }

        // Functionality Routes
        composable("physicalCategory") {
            PhysicalCategoryScreen(
                navController,
                categoryViewModel,
            )
        }
        composable("zenCategory") {
            ZenCategoryScreen(
                navController,
                categoryViewModel,
            )
        }
        composable(
            route = "exerciseSelection/{selectedCategories}",
            arguments = listOf(navArgument("selectedCategories") { type = NavType.StringType })
        )
        { backStackEntry ->
            val selectedCategoriesString =
                backStackEntry.arguments?.getString("selectedCategories") ?: ""
            val selectedCategoriesList =
                selectedCategoriesString.split(",").filter { it.isNotBlank() }
            ExerciseSelection(
                exerciseSelectionViewModel = exerciseSelectionViewModel,
                navController = navController,
                viewModel = categoryViewModel
            )
        }
        composable(
            "instructions/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            InstructionScreen(
                exerciseId = exerciseId,
                viewModel = mainViewModel,
                navController = navController
            )
        }
        composable(
            route = "createdPlans/{workoutPlanId}",
            arguments = listOf(navArgument("workoutPlanId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Fetch the workoutPlanId from the backStackEntry's arguments
            val workoutPlanId =
                backStackEntry.arguments?.getString("workoutPlanId") ?: return@composable
            CreatedPlan(
                workoutPlanId = workoutPlanId,
                exerciseId = exerciseId,
                navController = navController,
                viewModel = generatedWorkoutViewModel,
                mainViewModel = mainViewModel
            )
        }
        composable("goalScreen"){
            GoalScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(
            route = "goalProgressRecord/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Aqui você recupera o goalId passado como argumento
            val goalId = backStackEntry.arguments?.getString("goalId") ?: return@composable
            GoalProgressRecordScreen(goalId = goalId, viewModel = mainViewModel, navController = navController, exerciseId = exerciseId)
        }
        composable("UserProfile"){
            ProfileScreen(navController = navController, viewModel = mainViewModel)
        }
        composable("AchievedGoals"){
            AchievedGoals(navController = navController, viewModel = mainViewModel)
        }
        composable(
            "logDetails/{logName}",
            arguments = listOf(navArgument("logName") { type = NavType.StringType })
        )
        { backStackEntry ->
            val logName = backStackEntry.arguments?.getString("logName") ?: ""
            val log = mainViewModel.getTrainingLog(logName)
            if (log != null) {
                LogDetails(log, navController, mainViewModel)
            }
        }
    }
}

// Bottom Navigation System
data class NavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val route: String,
)

@Composable
fun BottomNavBar(navController: NavController) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFFE7316))
    ) {
        val items = listOf(
            NavigationItem(
                title = "Workouts",
                selectedIcon = R.drawable.icon_favorite_filled,
                unselectedIcon = R.drawable.icon_favorite,
                route = "savedWorkoutPlans"
            ),
            NavigationItem(
                title = "Home",
                selectedIcon = R.drawable.icon_home_filled, // Make sure this is correctly defined
                unselectedIcon = R.drawable.home_icon,
                route = "home"
            ),
            NavigationItem(
                title = "Logs For User",
                selectedIcon = R.drawable.icon_notes_filled,
                unselectedIcon = R.drawable.icon_notes,
                route = "logs"
            )
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        var selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

        AnimatedNavigationBar(
            selectedIndex = selectedIndex,
            barColor = Color.Black,
            ballColor = Color.Black,
            content = {
                items.forEachIndexed { index, item ->
                    IconButton(onClick = {
                        selectedIndex = index
                        navController.navigate(item.route) {
                            // This ensures that the navigation pop up to the start destination
                            // and saves the state at each navigation graph level
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }) {
                        // Correctly use the icon for each NavigationItem
                        val iconRes = if (index == selectedIndex) item.selectedIcon else item.unselectedIcon
                        Icon(
                            modifier = Modifier
                                .width(22.dp)
                                .height(22.dp)
                                ,
                            painter = painterResource(id = iconRes),
                            contentDescription = item.title,
                            tint = Color.Unspecified // Preserve the original icon color
                        )
                    }
                }
            }
        )
    }
}


fun navigateToTopLevelDestination(navController: NavController, route: String) {
    val currentRoute = navController.currentDestination?.route
    if (route != currentRoute) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}


@Composable
fun AnimatedNavigationBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    barColor: Color = Color.White,
    ballColor: Color = Color.Black,
    cornerRadius: ShapeCornerRadius = shapeCornerRadius(0f),
    ballAnimation: BallAnimation = Parabolic(tween(300)),
    indentAnimation: IndentAnimation = Height(tween(300)),
    content: @Composable () -> Unit,
) {

    var itemPositions by remember { mutableStateOf(listOf<Offset>()) }
    val measurePolicy = animatedNavBarMeasurePolicy {
        itemPositions = it.map { xCord ->
            Offset(xCord, 0f)
        }
    }

    val selectedItemOffset by remember(selectedIndex, itemPositions) {
        derivedStateOf {
            if (itemPositions.isNotEmpty()) itemPositions[selectedIndex] else Offset.Unspecified
        }
    }

    val indentShape = indentAnimation.animateIndentShapeAsState(
        shapeCornerRadius = cornerRadius,
        targetOffset = selectedItemOffset
    )

    val ballAnimInfoState = ballAnimation.animateAsState(
        targetOffset = selectedItemOffset,
    )

    Box(
        modifier = modifier
    ) {
        Layout(
            modifier = Modifier
                .graphicsLayer {
                    clip = true
                    shape = indentShape.value
                }
                .background(barColor),
            content = content,
            measurePolicy = measurePolicy
        )

        if (ballAnimInfoState.value.offset.isSpecified) {
            ColorBall(
                ballAnimInfo = ballAnimInfoState.value,
                ballColor = ballColor,
                sizeDp = ballSize
            )
        }
    }
}

val ballSize = 10.dp

@Composable
private fun ColorBall(
    modifier: Modifier = Modifier,
    ballColor: Color,
    ballAnimInfo: BallAnimInfo,
    sizeDp: Dp,
) {
    Box(
        modifier = modifier
            .ballTransform(ballAnimInfo)
            .size(sizeDp)
            .clip(shape = CircleShape)
            .background(ballColor)
    )
}

// Main Top Bar System
