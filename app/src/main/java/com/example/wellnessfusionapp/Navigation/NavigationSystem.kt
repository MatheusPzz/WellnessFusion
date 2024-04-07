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
import com.example.wellnessfusionapp.MentalCategoryScreen
import com.example.wellnessfusionapp.Models.NavigationItem
import com.example.wellnessfusionapp.ProfileScreen
import com.example.wellnessfusionapp.R
import com.example.wellnessfusionapp.Services.authState
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.example.wellnessfusionapp.ui.theme.SplashScreen
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
import com.google.firebase.auth.FirebaseAuth

/*
 MainNavHost defined the main navigation system of my application, coordinating how the screen transitions are going to be handled
 */
@Composable
fun MainNavHost(
    navController: NavController,  // Usual controller
    categoryViewModel: CategoryViewModel, // View model that interacts with the logic for category selection
    startDestination: String, // defined the initial path of the app
    exerciseSelectionViewModel: ExerciseSelectionViewModel,  // View Model that interacts with the logic for exercise selection
    generatedWorkoutViewModel: GeneratedWorkoutViewModel, // View model that interacts with the generated workout plans that were created by the user
    mainViewModel: MainViewModel, // This is the principal view model of the app, it has most of the logic behind the screens
    exerciseId: String, // Passing the ID of each exercise between screens
    workoutPlanId: String,  // Passing the Plan ID of each plan between screens
    goals: List<Goal>, // Goals modal class
    userId: String  // User ID string
) {
    val authState = authState(FirebaseAuth.getInstance()).value
    // Creating a nav host that manages the nav graph, where each composable is represented by a route destination
    NavHost(navController = navController as NavHostController, startDestination = startDestination)
    {
        // Splash
        composable("splash") {
            SplashScreen(navController)
        }

        // Authentication Routes //

        // Login route represents the login screen composable, on success login navigates to home screen
        composable("login") {
            LoginScreen(navController = navController) {
                navController.navigate("home") {
                    // Clears the back stack
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            }
        }
        composable("signUp") {
            SignUpScreen(
            navController = navController
        ) }

        composable("home") {
            HomeScreen(
                navController,
                categoryViewModel,
                mainViewModel,
                exerciseId = exerciseId,
                userId = userId
            )
        }


        // App features routes //
        composable("logs") { LogScreen(
            navController,
            mainViewModel,
            goals
        ) }
        composable("savedWorkoutPlans") {
            SavedWorkoutsScreen(
                viewModel = generatedWorkoutViewModel,
                navController = navController
            )
        }
        composable("physicalCategory") {
            PhysicalCategoryScreen(
                navController,
                categoryViewModel,
            )
        }
        composable("mentalCategory") {
            MentalCategoryScreen(
                navController,
                categoryViewModel,
            )
        }
        // This route has a few segments for exercise selection, it basically makes sure the selected categories are passed as argument to next screen in which this case is exercise selection screen
        composable(
            route = "exerciseSelection/{selectedCategories}",
            arguments = listOf(navArgument("selectedCategories") { type = NavType.StringType })
        )
        { backStackEntry ->
            // Extracting the categories strings selected by the user
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

        // This route visualizes the exercise instructions based on exercise ID
        // We are passing a few arguments between the workout session screen (exerciseId) to instructions route so we can visualize the instructions of the exercise selected
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

        // This route brings the newly created plan exercises to a list of workout session that displays the exercises of a plan
        // upon creation of a plan we pass the plan ID just generated as a string in nav argument, so we can have the just created plan with its current exercises in the next route directly
        composable(
            route = "createdPlans/{workoutPlanId}",
            arguments = listOf(navArgument("workoutPlanId") { type = NavType.StringType })
        ) { backStackEntry ->
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

/*
 This composable displays the bottom navigation bar of the app

 */
@Composable
fun BottomNavBar(navController: NavController) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFFE7316))
    ) {
        // Here we define and style the items that are shown in the navigation and its routes
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
        val navBackStackEntry by navController.currentBackStackEntryAsState() // Observes the current back stack entry to determine the current route for highlighting the active item.

        val currentRoute = navBackStackEntry?.destination?.route
        var selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)  // Determines the index of the currently selected navigation item based on the current route.

        // The custom animated navigation bar and its style setup
        AnimatedNavigationBar(
            selectedIndex = selectedIndex,
            barColor = Color.Black,
            ballColor = Color.Black,
            content = {
                items.forEachIndexed { index, item ->
                    IconButton(onClick = {
                        selectedIndex = index
                        navController.navigate(item.route) {
                            // This ensures that the navigation pop up to the start destination, depending on what level or route it is
                            // and also saves the state at each navigation graph level
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }) {
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

// https://github.com/exyte/AndroidAnimatedNavigationBar/tree/master
// This animated navigation bar was taken out from the author from this github link account
// Adapted and used in this project instead of the regular android navigation bottom bar
// i then, put some style to it, icons and readjusted to work the way my app requires it to run
// So here we have a bottom nav bar with an animated ball circle for index transition

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


// Custom for the color of the ball
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

