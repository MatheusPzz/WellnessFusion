@file:OptIn(ExperimentalFoundationApi::class)

package com.example.wellnessfusionapp

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/* Login screen composable, which contains nested composables for each item that shows up in the screen
*  as app logo, image slider, credential fields login button and recovery / sign up text buttons, we are also setting
*  a condition up, where the user needs to meet the authentication criteria to proceed to the next screen upon log in click,
*  triggering success or not based if the user is registered as firebase user, home screen is the following screen after log in*/
@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {

    /*Variables necessary for log in screen:
    * Mutable ensures the state between recompositions
    * - Getting the firestore instance
    * - Two fields for email and password input are stored in mutableStateOf initialized as an empty string
    * - Boolean set to trigger Alert Dialog (in this case recover password dialog value to true) */

    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isPasswordRecoveryDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var backGroundGradient = listOf(
        Color.Black,
        Color.DarkGray
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    backGroundGradient,
                    startY = 1000f,
                    endY = 500f
                )
            )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageSlider()


            CredentialsInput(email, password) { newEmail, newPassword ->
                email = newEmail
                password = newPassword
            }
            LoginButton(
                email = email,
                password = password,
                onLoginSuccess = { navController.navigate("home") },
                onLoginError = { msg -> errorMessage = msg },
                auth = auth,
                context = context
            )
            if (errorMessage.isNotEmpty()) {
                ErrorMessage(errorMessage)
            }
            NavigationButtons(navController = navController, onRecoverPassword = {
                isPasswordRecoveryDialogOpen = true
            })


            /* Triggering the opening of the password dialog then toasting on password recovery email sent, or error in case on fail */

            if (isPasswordRecoveryDialogOpen) {
                PasswordRecoveryDialog(
                    isOpen = isPasswordRecoveryDialogOpen,
                    onDismissRequest = { isPasswordRecoveryDialogOpen = false },
                    onRecoveryEmailSent = { isSuccess, errorMessage ->
                        if (isSuccess) {
                            Toast.makeText(
                                context,
                                "Password recovery email sent. Please check your inbox.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                errorMessage ?: "An error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}



/* Logo composable create in order to place a custom app logotype */
@Composable
fun LogoIcon() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(id = R.drawable.logotype3),
            modifier = Modifier.size(80.dp),
            contentDescription = "Logo",
        )
    }
}


/* That's an image slider where the user will see relevant content about the app as features and interesting motivational content for sign up and start using the app */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider() {
    Box(
        modifier = Modifier
            .height(480.dp)
            .fillMaxWidth()
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val loginSlider = listOf(
            R.drawable.background_physical,
            R.drawable.background_slider2,
            R.drawable.background_slider3
        )

        // Define titles and texts for each page
        val pageContents = listOf(
            Pair("Create your own workout plans", "Set the plan and save your favorites."),
            Pair("Follow your records", "Set a goal and beat yourself!."),
            Pair("Learn more and join us", "Know how to start and how to improve, never get lost!.")
        )

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
        ) { page ->
            Box {
                // Displaying an image for each of the index
                Image(
                    painter = painterResource(id = loginSlider[page]),
                    contentDescription = "Login Image $page",
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(1.5f)
                        .alpha(0.5f)
                        .height(440.dp)
                )
                // Overlay Title and Text
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .matchParentSize() // Use matchParentSize to cover the parent
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = pageContents[page].first,
                            lineHeight = 40.sp,
                            color = Color(0xffFE7316),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Space between title and text
                        Text(
                            text = pageContents[page].second,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
        LogoIcon() // Assuming LogoIcon is another composable you've defined

        // Overlay Text (Optional, adjust based on your needs)

    }
}



/*This is where the user input his email and password, the composable has straight forward structure and fields*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsInput(
    email: String,
    password: String,
    onCredentialsChanged: (String, String) -> Unit
) {

    OutlinedTextField(
        value = email,
        textStyle = TextStyle(color = Color.White),
        onValueChange = { onCredentialsChanged(it, password) },
        label = { Text("Email", fontSize = 15.sp, color = Color(0xffFE7316)) },
        modifier = Modifier
            .width(350.dp)
            .padding(6.dp),
        singleLine = true
    )


    OutlinedTextField(
        value = password,
        onValueChange = { onCredentialsChanged(email, it) },
        textStyle = TextStyle(color = Color.White),
        label = { Text("Password",fontSize = 15.sp ,color = Color(0xffFE7316)) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .width(350.dp)
            .padding(6.dp),
        singleLine = true,
    )
}

/*
 *Login Button custom for login screen creation
 *Here we are passing as lambdas for each process of check the button has to perform before moving to the next screen
 *Email and password fields are checked if they are not blank, if they are, a toast message is shown to the user
 *If the fields are all filled, authentication is performed and the user navigates to the next screen ("Home")
*/
@Composable
fun LoginButton(
    email: String,
    password: String,
    onLoginSuccess: () -> Unit,
    onLoginError: (String) -> Unit,
    auth: FirebaseAuth,
    context: android.content.Context
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            Color(0xffFE7316),
        ),
        onClick = {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {

                /*Method for sign in with email and password*/

                signInWithEmail(
                    auth,
                    email,
                    password,
                    onSuccess = onLoginSuccess,
                    onError = onLoginError
                )
            }
        },
        modifier = Modifier
            .width(350.dp)
            .padding(6.dp)
    ) {
        Text("Login")
    }
}


/*
 *Composable created to trigger an error message whenever the user fails to log in or anything goes wrong
*/
@Composable
fun ErrorMessage(errorMessage: String) {
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(5.dp)
    )
}


/*
 * Navigation text buttons for the user to navigate either sign up screen or to trigger password recovery dialog
 * onRecoverPassword () -> Unit is a lambda that triggers the password recovery dialog on click
*/
@Composable
fun NavigationButtons(navController: NavController, onRecoverPassword: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            /*
            Navigating to the sign up screen with navController
             */
            onClick = { navController.navigate("signUp") },
        ) {
            Text(
                "Don't have an account? Sign Up",
                color = Color(0xffFE7316),
                fontSize = 12.sp
            )
        }


        /*
        Triggers the password recovery dialog
        */
        TextButton(onClick = onRecoverPassword) {
            Text("Recover Password", color = Color(0xffFE7316), fontSize = 12.sp)
        }
    }
}


/*
  * This function is responsible for signing in the user with email and password through firebase auth and also checks the existence of the User
  * On success retrieves the user profile from Firestore (exists or not)
  * On failure throws an error message
*/
private fun signInWithEmail(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Authentication successful, now check if user profile exists in Firestore
                val userId = auth.currentUser?.uid ?: ""
                val db = FirebaseFirestore.getInstance()
                db.collection("Users").document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            // User profile exists
                            onSuccess()

                        } else {
                            // User profile does not exist
                            onError("User profile does not exist")
                        }
                    }
                    .addOnFailureListener { e ->
                        onError(
                            e.message ?: "An unknown error occurred while fetching user profile"
                        )

                    }
            } else {
                // Authentication failed
                onError(task.exception?.message ?: "An unknown error occurred")
            }
        }
}

/*
  * Password Recovery Dialog Composable, whenever the is open lambda passed is triggered
  * and as it is a boolean, the state changes to true and dialog is shown
  * onDismissRequest () -> Unit is a lambda that triggers the closing of the dialog
  * onRecoveryEmailSent (Boolean, String?) -> Unit is a lambda that triggers the sending of the recovery email
*/
@Composable
fun PasswordRecoveryDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onRecoveryEmailSent: (Boolean, String?) -> Unit,
) {
    /*
      * Condition if the dialog is open then show the items
      * email field, send button, cancel button
      * mutably setting the email, isSending, errorMessage to empty string, false and null respectively
      * if the error message is not null, show the error message
     */
    if (isOpen) {                                                                                                                       // if the dialog is open
        var email by remember { mutableStateOf("") }
        var isSending by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        /*
            * Password dialog setup where,
            * we customize title and text and place and input text field for email insertion
            * We also run a few checks to see if it is not null,
            * then we change the state of our mutables upon completion of checks.
            * Whenever confirm button is triggered our code checks if the email is not blank
            * Then state of our isSending is set to true then we get the firestore instance,
            * calling its auth.sendPasswordResetEmail function with the persons email,
            * on complete listener, app closes the dialog and toast it for confirmation, else it throws and error message
         */

        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text("Password Recovery", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your email to recover your password.")
                    if (errorMessage != null) {                                                 // if the error message is not null
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(                                                          // text email field
                        value = email,
                        onValueChange = {
                            email = it
                            /*
                              *Reset error message when user starts to edit
                            */
                            errorMessage = null

                        },
                        label = { Text("Email", color = Color(0xffFE7316)) },
                        singleLine = true,
                        enabled = !isSending
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color(0xffFE7316),
                    ),
                    onClick = {
                        // Check if email is not blank
                        if (email.isNotBlank()) {               // checks if the email field is not blank
                            isSending =
                                true                        // if not blank, set isSending to true
                            val auth =
                                FirebaseAuth.getInstance()           // get the instance of the firebase auth to check user email and send the recovery email
                            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                                if (task.isSuccessful) {                                            // if the task is successful
                                    // Inform the user that the email was sent successfully
                                    onRecoveryEmailSent(
                                        true,
                                        null
                                    )                                 // send the recovery email
                                } else {                                                                    //if not throw the error message
                                    // Show error message
                                    errorMessage = task.exception?.localizedMessage
                                        ?: "An error occurred"          // if the email was not sent successfully, throw the error message
                                }
                                isSending =
                                    false                                                                   // set isSending to false
                                onDismissRequest()                                                                  // close the dialog
                            }
                        } else {                                                                                  // if user didn't fill the email field
                            errorMessage = "Please enter your email."
                        }
                    },
                    enabled = !isSending
                ) {
                    if (isSending) {                                                    // if manages to send the email
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Send")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                    enabled = !isSending
                ) {
                    Text("Cancel", color = Color(0xffFE7316))
                }
            }
        )
    }
}


// Old login screen implementation without refinement

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun LoginScreen(navController: NavController, onSuccess: () -> Unit) {
//    val auth = FirebaseAuth.getInstance()
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf("") }
//    var isPasswordRecoveryDialogOpen by remember { mutableStateOf(false) }
//    var showRecoverySuccessSnackbar by remember { mutableStateOf(false) }
//    var recoveryErrorMessage by remember { mutableStateOf<String?>(null) }
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(15.dp),
//        verticalArrangement = Arrangement.SpaceEvenly
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                modifier = Modifier.size(100.dp),
//                imageVector = Icons.Default.Person,
//                contentDescription = "Person Icon"
//            )
//
//        }
//
//        Column(
//            modifier = Modifier
//                .padding(10.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//
//
//            //Image Slider for LoginPage
//            val pagerState = rememberPagerState(pageCount = { 3 }) // Define the page count
//            val loginSlider = listOf(
//                R.drawable.ic_launcher_background,
//                R.drawable.ic_launcher_foreground,
//                R.drawable.ic_launcher_background
//            )
//            Box(modifier = Modifier.fillMaxWidth()) {
//                HorizontalPager(state = pagerState) { page ->
//                    // Instead of displaying text, we display the image corresponding to the current page
//                    Image(
//                        painter = painterResource(id = loginSlider[page]),
//                        contentDescription = "Login Image $page",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp), // Adjust the modifier as needed
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(30.dp))
//
//        }
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(15.dp))
//
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                visualTransformation = PasswordVisualTransformation(),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = {
//                    if (email.isBlank() || password.isBlank()) {
//                        Toast.makeText(
//                            context,
//                            "Please fill in all fields",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        signInWithEmail(
//                            auth,
//                            email,
//                            password,
//                            onSuccess = {
//                                onSuccess()
//                            },
//                            onError = { error ->
//                                errorMessage = error
//                            }
//                        )
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Login")
//            }
//
//            // Error message display
//
//
//            // Recover Password Function call
//
//            // If the password recovery dialog is open, show the dialog
//            if (isPasswordRecoveryDialogOpen) {
//                PasswordRecoveryDialog(                                             // Function call
//                    isOpen = isPasswordRecoveryDialogOpen,                             // the dialog is open
//                    onDismissRequest = {
//                        isPasswordRecoveryDialogOpen = false
//                    },        // here we are dismissing the dialog
//                    onRecoveryEmailSent = { isSuccess, errorMessage ->                      // here we call the function to send the recovery email
//                        if (isSuccess) {                                                // if the email is sent successfully
//                            showRecoverySuccessSnackbar =
//                                true                              // show the snackbar
//                            isPasswordRecoveryDialogOpen =
//                                false                        // on success close the dialog
//                        } else {                                                        // if not successful throw our error message
//                            recoveryErrorMessage =
//                                errorMessage                            // show the error message
//                        }
//                    }
//                )
//            }
//
//            // function to show the snackbar
//            if (showRecoverySuccessSnackbar) {                                              // if the email was sent  with success aligning with the code above..
//                Snackbar(                                                                      // snack bar
//                    action = {                                                              // action
//                        Button(onClick = {
//                            showRecoverySuccessSnackbar = false
//                        }) {         // on the click of the button set the snackbar to false (making it disappear)
//                            Text("OK")                                                  // text that on snackbars button
//                        }
//                    }
//                ) {
//                    Text("Password recovery email sent. Please check your inbox.")          // snack bar message
//                }
//            }
//
//            // Show error message if the password recovery failed
//            if (recoveryErrorMessage != null) {             // if recovery message is not null
//                Text(
//                    text = recoveryErrorMessage!!,
//                    color = MaterialTheme.colorScheme.error
//                )        // Text and color for the error message
//                Spacer(modifier = Modifier.height(8.dp))                            // Space between the error message and the button
//            }
//
//        }
//        Spacer(modifier = Modifier.height(10.dp))                                // another spacer
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            if (errorMessage.isNotEmpty()) {
//                Text(
//                    text = "Wrong Credentials! Please try again",
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(5.dp)
//                )
//            }
//
//
//            TextButton(
//                // Button that navigates to the sign up page
//                onClick = { navController.navigate("signUp") },
//            ) {
//                Text("Don`t have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
//            }
//
//            TextButton(onClick = {
//                isPasswordRecoveryDialogOpen = true
//            }) {                     // now we have a highlighted text link that triggers our password recovery dialog
//                Text("Recover Password", color = MaterialTheme.colorScheme.primary)
//            }
//
//        }
//    }
//}