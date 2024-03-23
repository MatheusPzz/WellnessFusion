package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// Profile Screen content and functions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    var userEmail by remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser

    userEmail = user?.email ?: ""

    LaunchedEffect(userName) {
        viewModel.fetchUserName()
    }

    val changePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                if (user != null) {
                    viewModel.updateUserProfilePicture(it, user.uid) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to update profile picture: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFE7316)),
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login")
                        FirebaseAuth.getInstance().signOut()
                    }) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        },
        bottomBar = { }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background box for the entire screen
            Box(
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .height(190.dp)// Use matchParentSize to cover the entire parent
                    .background(
                        Color.Black,
                        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                    )
            )

            // Content of the screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Mental Workouts:", fontFamily = FontFamily(Font(R.font.zendots_regular)), color = Color.White)
                    Text("Physical Workouts:", fontFamily = FontFamily(Font(R.font.zendots_regular)), color = Color.White)
                }
                Spacer(modifier = Modifier.height(15.dp))
                ProfileCard(
                    viewModel = viewModel,
                    userName = userName,
                    onChangePictureClick = { changePictureLauncher.launch("image/*") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ProfileInfoFields(
                        userName = userName,
                        userEmail = userEmail,
                        context = context,
                        viewModel = viewModel
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    )
                {
                    SetGoalButton(navController = navController)
                }
            }
        }
    }
}



@Composable
fun SetGoalButton(navController: NavController) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .padding(6.dp)
            .clickable { navController.navigate("goalScreen") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Add personal goals to keep track of your progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontSize = 13.sp
        )
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal", Modifier.fillMaxSize())

        }
    }
}

@Composable
fun ProfileCard(
    viewModel: MainViewModel, // Assume ViewModel is passed here
    userName: String,
    onChangePictureClick: () -> Unit,
) {
    val userProfilePictureUrl by viewModel.profilePictureUrl.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        // Profile Picture
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { onChangePictureClick() }

        ) {
            // Use Coil to load the image from URL
            Image(
                painter = rememberAsyncImagePainter(userProfilePictureUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // User Name
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge
        )

    }
}


@Composable
fun ProfileInfoFields(
    viewModel: MainViewModel,
    userName: String,
    userEmail: String,
    context: Context // Pass the context to show Toast messages
) {
    var editableUserName by remember { mutableStateOf(userName) }
    var isEditing by remember { mutableStateOf(false) }

    // This function updates the userName and shows a Toast based on the result
    fun saveUserName() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.updateUserName(userId, editableUserName) { success, message ->
                if (success) {
                    Toast.makeText(context, "User name updated", Toast.LENGTH_SHORT).show()
                    isEditing = false // Reset editing state
                    editableUserName = "" // Reset the editable name
                } else {
                    Toast.makeText(
                        context,
                        "Failed to update user name: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val fontTest = FontFamily(Font(R.font.zendots_regular))
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("Personal Information", fontFamily = fontTest)
        OutlinedTextField(
            value = userEmail,
            onValueChange = { /* Do nothing */ },
            label = { Text("Email Address", fontFamily = fontTest) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),

            )
        OutlinedTextField(
            value = editableUserName,
            onValueChange = { newName ->
                editableUserName = newName
                isEditing = true
            },
            label = { Text(text = "UserName", fontFamily = fontTest) },
            singleLine = true,
            trailingIcon = {
                if (isEditing) {
                    IconButton(onClick = { saveUserName() }) {
                        Icon(Icons.Filled.Check, "Save")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
