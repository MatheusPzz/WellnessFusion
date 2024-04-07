package com.example.wellnessfusionapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.wellnessfusionapp.Navigation.MainNavHost
import com.example.wellnessfusionapp.Services.authState
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.example.wellnessfusionapp.ui.theme.WellnessFusionAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi


/*
 Author: Matheus Perazzo
 Date: 2024-04-07
 Project: Wellness Fusion App

 Mock User for easy testing:

 - Email: example@gmail.com
 - Password: 123456

 Profile used to run the last tests in the app, you can login and see it for yourself when all the features are working. (mock date was used to see the results in the CHART)

 This project was an academic project developed for the course of Mobile Development at Dorset College Ireland.
 The project was developed by myself, Matheus Perazzo,
 In this project you will find sections where AI was used for implementation, where i used information provided by ChatGPT,
 Where i took the information and adapted to the projects needs.
 The time development of this project was around 3 months, where i developed the app from scratch, learning and using the best practices of kotlin jetpack compose
 The apps goal is to give the user a way to organise his fitness life and be aware of his progress and goals.
 */

class MainActivity : ComponentActivity() {
    /*
     Main Activity of the APP, principal entry point
     here is where the app is created, and the activity is started
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the UI content of this activity, defining the layout
        setContent {
            WellnessFusionAppTheme {

                // Remember the navController that will handle navigation between composables in the app
                val navController = rememberNavController()

                // Use authState to observe the authentication state.
                // Getting the instance of the firebase everytime the app is executed
                // Auth state is a firebase method to get the instance of it, listening the authentication state


                // Creating a value to determine the first screen of the app
                // if the user is authenticated the route will be home directly
                // else he will go to login screen

                // Nav Host composable
                // We are passing all the view models and parameters/arguments necessary for our
                // nav controller navigates smoothly and properly between screens
                MainNavHost(
                    navController,
                    CategoryViewModel(),
                    startDestination = "splash",
                    ExerciseSelectionViewModel(),
                    GeneratedWorkoutViewModel(),
                    MainViewModel(),
                    "",
                    "",
                    emptyList(),
                    ""
                )

            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun WellnessScreenPreview() {
    WellnessFusionAppTheme {

    }
}
