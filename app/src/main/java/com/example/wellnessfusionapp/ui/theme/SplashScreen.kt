package com.example.wellnessfusionapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.wellnessfusionapp.R
import com.example.wellnessfusionapp.Services.authState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(
                painter = painterResource(id = R.drawable.logotype3),
                contentDescription = "Wellness Fusion Logo"
            )
            Text(
                text = "Wellness Fusion",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
        }
    }

    val authState = authState(FirebaseAuth.getInstance()).value

    // Optionally, navigate away from the splash screen after a delay
    LaunchedEffect(key1 = true) {
        delay(2000) // 2 seconds delay
        if (authState == null) {
            navController.navigate("login")
        } else {
            navController.navigate("home")
        }
    }
}