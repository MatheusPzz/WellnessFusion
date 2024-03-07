@file:OptIn(ExperimentalFoundationApi::class)

package com.example.wellnessfusionapp

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(navController: NavController, onSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isPasswordRecoveryDialogOpen by remember { mutableStateOf(false) }
    var showRecoverySuccessSnackbar by remember { mutableStateOf(false) }
    var recoveryErrorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Default.Person,
                contentDescription = "Person Icon"
            )

        }

        Column(
            modifier = Modifier
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            //Image Slider for LoginPage
            val pagerState = rememberPagerState(pageCount = { 3 }) // Define the page count
            val loginSlider = listOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(state = pagerState) { page ->
                    // Instead of displaying text, we display the image corresponding to the current page
                    Image(
                        painter = painterResource(id = loginSlider[page]),
                        contentDescription = "Login Image $page",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), // Adjust the modifier as needed
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill in all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        signInWithEmail(
                            auth,
                            email,
                            password,
                            onSuccess = {
                                onSuccess()
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            // Error message display


            // Recover Password Function call

            // If the password recovery dialog is open, show the dialog
            if (isPasswordRecoveryDialogOpen) {
                PasswordRecoveryDialog(                                             // Function call
                    isOpen = isPasswordRecoveryDialogOpen,                             // the dialog is open
                    onDismissRequest = {
                        isPasswordRecoveryDialogOpen = false
                    },        // here we are dismissing the dialog
                    onRecoveryEmailSent = { isSuccess, errorMessage ->                      // here we call the function to send the recovery email
                        if (isSuccess) {                                                // if the email is sent successfully
                            showRecoverySuccessSnackbar =
                                true                              // show the snackbar
                            isPasswordRecoveryDialogOpen =
                                false                        // on success close the dialog
                        } else {                                                        // if not successful throw our error message
                            recoveryErrorMessage =
                                errorMessage                            // show the error message
                        }
                    }
                )
            }

            // function to show the snackbar
            if (showRecoverySuccessSnackbar) {                                              // if the email was sent  with success aligning with the code above..
                Snackbar(                                                                      // snack bar
                    action = {                                                              // action
                        Button(onClick = {
                            showRecoverySuccessSnackbar = false
                        }) {         // on the click of the button set the snackbar to false (making it disappear)
                            Text("OK")                                                  // text that on snackbars button
                        }
                    }
                ) {
                    Text("Password recovery email sent. Please check your inbox.")          // snack bar message
                }
            }

            // Show error message if the password recovery failed
            if (recoveryErrorMessage != null) {             // if recovery message is not null
                Text(
                    text = recoveryErrorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )        // Text and color for the error message
                Spacer(modifier = Modifier.height(8.dp))                            // Space between the error message and the button
            }

        }
        Spacer(modifier = Modifier.height(10.dp))                                // another spacer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = "Wrong Credentials! Please try again",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(5.dp)
                )
            }


            TextButton(
                // Button that navigates to the sign up page
                onClick = { navController.navigate("signUp") },
            ) {
                Text("Don`t have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
            }

            TextButton(onClick = {
                isPasswordRecoveryDialogOpen = true
            }) {                     // now we have a highlighted text link that triggers our password recovery dialog
                Text("Recover Password", color = MaterialTheme.colorScheme.primary)
            }

        }
    }
}


// Function to handle email and password sign-in
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


// Function for password recovery
@Composable
fun PasswordRecoveryDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onRecoveryEmailSent: (Boolean, String?) -> Unit,
) {
    if (isOpen) {                                                                                                                       // if the dialog is open
        var email by remember { mutableStateOf("") }
        var isSending by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(                                                    //dialog setup
            onDismissRequest = { onDismissRequest() },
            title = { Text("Password Recovery") },
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
                            errorMessage = null // Reset error message when user starts to edit
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        enabled = !isSending
                    )
                }
            },
            confirmButton = {                       // confirm button
                Button(
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
                    Text("Cancel")
                }
            }
        )
    }
}
