package com.example.wellnessfusionapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.wellnessfusionapp.Navigation.MainNavHost
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.ExerciseSelectionViewModel
import com.example.wellnessfusionapp.ViewModels.GeneratedWorkoutViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.example.wellnessfusionapp.ui.theme.WellnessFusionAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WellnessFusionAppTheme {
                val navController = rememberNavController()
                // Use authState to observe the authentication state.

                // Access the current FirebaseUser? directly.
                val authState = authState(FirebaseAuth.getInstance()).value


                // Determine the start destination based on whether a user is logged in.

                Log.d("Main", "authState: $authState")

                val startDestination = if (authState != null) "home" else "login"

                MainNavHost(navController, CategoryViewModel(), startDestination, ExerciseSelectionViewModel(), GeneratedWorkoutViewModel(), MainViewModel(), "", "", emptyList(), "")

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


//fun saveFCMTokenForUser(token: String) {
//    val userId = FirebaseAuth.getInstance().currentUser?.uid
//    if (userId != null) {
//        val userRef = Firebase.firestore.collection("Users").document(userId)
//
//        userRef.update("fcmToken", token)
//            .addOnSuccessListener { Log.d("FCM", "FCM Token updated for user $userId") }
//            .addOnFailureListener { e -> Log.w("FCM", "Error updating FCM Token for user $userId", e) }
//    }
//}
//FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//    if (!task.isSuccessful) {
//        Log.w("FCM", "Fetching FCM registration token failed", task.exception)
//        return@addOnCompleteListener
//    }
//    // Get new FCM registration token
//    val token = task.result
//    Log.d("FCM", "FCM Token: $token")
//    // Armazene o token do dispositivo
//    saveFCMTokenForUser(token)
//}
