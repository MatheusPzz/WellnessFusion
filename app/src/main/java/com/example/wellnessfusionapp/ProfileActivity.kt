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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.font.FontWeight
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

/*
 This is the composable responsible for our profile screen
 where the user can see its email
 change its name,
 set a new goal
 and update its profile picture
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current  // Local context used for toasting
    val userName by viewModel.userName.collectAsState()  // Collecting the fetched user name from firestore
    var userEmail by remember { mutableStateOf("") } // Remember email to survive screen updates or recomposition
    val user = FirebaseAuth.getInstance().currentUser // Fetching current user from firebase AUTH method
    userEmail = user?.email ?: "" // updating the email with firebase auth email

    // fetching the new user name back after user updates the user name
    LaunchedEffect(userName) {
        viewModel.fetchUserName()
    }
    //Below code snippet is adapted from guidance provided by ChatGPT.
    //It has been modified to fit the specific requirements of this application.
    //Original ChatGPT suggestions were used as a foundation for further customization and optimization.
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

    // Scaffold of the page setup
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFE7316)),
                title = { Text(text = "Profile", fontFamily = FontFamily(Font(R.font.zendots_regular))) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {

                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
    ) { paddingValues ->
        Box(modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {
            // Background box for the entire screen look
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(
                        Color.Black,
                        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                    )
            ){
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.background_profile_card),
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
                )
            }

            // Main Content of the screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(120.dp))
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Custom composable with the profile fields such as user name and email
                    ProfileInfoFields(
                        userName = userName,
                        userEmail = userEmail,
                        context = context,
                        viewModel = viewModel,
                        navController = navController
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("Log out", color = Color.White)
                    IconButton(onClick = {
                        // Log out button, with firebase log out AUTH method
                        // Then navigating to login screen
                        navController.navigate("login")
                        FirebaseAuth.getInstance().signOut()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color.White)
                    }
                }

            }
        }
    }
}


/*
 This composable creates a button in the screen that,
 once clicked navigates to goal setup screen.
 */
@Composable
fun SetGoalButton(navController: NavController) {

        Text(
            "Add personal goals to keep track of your progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFE7316),
            fontSize = 13.sp
        )
        IconButton(
            modifier = Modifier
                .background(Color(0xFFFE7316), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFFE7316), RoundedCornerShape(10.dp))
                .size(50.dp),
            onClick = {
                // navigating to goalscreen route
            navController.navigate("goalScreen")
        }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal", Modifier.fillMaxSize(), tint = Color.White)
        }
    }


/*
 Composable that updates the profile picture and shows it in the UI
 */
@Composable
fun ProfileCard(
    viewModel: MainViewModel,
    userName: String,
    onChangePictureClick: () -> Unit, // lambda to handle profile picture pick update
) {
    val userProfilePictureUrl by viewModel.profilePictureUrl.collectAsState() // Observing profile pictureUrl and collecting as state

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        // Profile Picture box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .clip(CircleShape)
                .alpha(0.9f)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    onChangePictureClick()
                }
                .background(color = Color.Black, shape = CircleShape)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ){
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Picture", tint = Color.Black,
                )
            }
            // Loading the image into the container
            Image(
                painter = rememberAsyncImagePainter(userProfilePictureUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily(Font(R.font.zendots_regular)),
            color = Color(0xFFFE7316),
            fontWeight = FontWeight.Bold
        )

    }
}

/*
 Custom composable for the profile fields
 is displays User name and user email
 also updates the user name
 */
@Composable
fun ProfileInfoFields(
    viewModel: MainViewModel,
    userName: String,
    userEmail: String,
    context: Context, // context to show Toast messages
    navController: NavController
) {
    val fontTest = FontFamily(Font(R.font.zendots_regular))
    var editableUserName by remember { mutableStateOf(userName) }
    var isEditing by remember { mutableStateOf(false) }

    // Observing updateResult LiveData
    val updateResult by viewModel.updateResult.observeAsState()

    // Check for update result and show Toast accordingly
    LaunchedEffect(updateResult) {
        updateResult?.let { result ->
            Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
            if (result.first) {
                isEditing = false
                editableUserName = userName // Reset the editable name if needed
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("Personal Information", fontFamily = fontTest, color = Color(0xFFFE7316), fontSize = 20.sp)
        // Display user email read only
        OutlinedTextField(
            value = userEmail,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            onValueChange = { /* Do nothing */ },
            label = { Text("Email Address", color = Color(0xFFFE7316)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            )

        // display user name editable, and trailing icon execute the change
        OutlinedTextField(
            value = userName,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            onValueChange = { newName ->
                editableUserName = newName
                isEditing = true
            },
            label = { Text(text = "Change Your Name", color = Color(0xFFFE7316))},
            singleLine = true,
            trailingIcon = {
                // ife editing gets the instance of the user and updates the user name
                if (isEditing) {
                    IconButton(onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (userId.isNotEmpty()) {
                            viewModel.updateUserName(userId, editableUserName) // Call the ViewModel's update method directly
                        }
                    }) {
                        Icon(Icons.Filled.Check, "Save", tint = Color.White)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Calling setGoalButton at the end of the column
        SetGoalButton(navController = navController)


    }
}
