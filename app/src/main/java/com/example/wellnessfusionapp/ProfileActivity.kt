package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// Profile Screen content and functions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userProfilePicture: Painter, // You can pass a Painter object for the profile image
    userName: String,
    userEmail: String,
    onChangePictureClick: () -> Unit, // Lambda to handle changing the profile picture
    onNameChange: (String) -> Unit // Lambda to handle name changes
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login")
                        FirebaseAuth.getInstance().signOut()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                }
            )
        },
        bottomBar = { }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileCard(
                userProfilePicture = userProfilePicture,
                userName = userName,
                onChangePictureClick = onChangePictureClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoFields(
                userName = userName,
                userEmail = userEmail,
                onNameChange = onNameChange
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Usage")
            }
        }
    }
}

@Composable
fun ProfileCard(
    userProfilePicture: Painter,
    userName: String,
    onChangePictureClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(200.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Profile Picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(96.dp)
                    .height(96.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onChangePictureClick() }
            ) {
                Icon(
                    painter = userProfilePicture,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .width(72.dp)
                        .height(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // User Name
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge
            )
            // Placeholder for the location, you can replace this with a dynamic value
            Text(
                text = "User Location",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileInfoFields(
    userName: String,
    userEmail: String,
    onNameChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email Address (read-only)
        OutlinedTextField(
            value = userEmail,
            onValueChange = {},
            label = { Text("Email") },
            readOnly = true, // Set to false if it should be editable
            singleLine = true
        )
        // Editable Name Field
        OutlinedTextField(
            value = userName,
            onValueChange = onNameChange,
            label = { Text("Name") },
            singleLine = true
        )
        OutlinedTextField(
            value = "Password",
            onValueChange = {},
            label = { Text("Password") },
            readOnly = true, // Set to false if it should be editable
            singleLine = true
        )
    }
}