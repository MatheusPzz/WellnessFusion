package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(0.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    IconButton(
                        onClick = { navController.navigate("login") }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Text(
                        "Sign Up",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(7.dp),
                        fontSize = 25.sp
                    )
                }



                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { })
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onNext = {})
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                            // Check for empty fields first
                            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                snackbarHostState.showSnackbar("Please fill in all fields")
                            } else if (!email.matches(emailPattern.toRegex())) {
                                snackbarHostState.showSnackbar("Invalid email format")
                            } else if (password != confirmPassword) {
                                snackbarHostState.showSnackbar("Passwords do not match")
                            } else {
                                // Proceed with registering the user
                                registerUser(
                                    email,
                                    password,
                                    User(
                                        name = name,
                                        email = email,
                                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    ),
                                    coroutineScope,
                                    snackbarHostState,
                                    navController
                                )
                            }
                            Toast.makeText(context,
                                "Profile created successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("login")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Sign Up")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){


                    TextButton(onClick = {
                    }) {
                        Text(
                            "By signing up you agree to our Privacy Policy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }
                }
            }
        }
    }
}

fun createUserProfile(
    user: User,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Users").document(user.userId)
        .set(user)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e.message ?: "An unknown error occurred") }
}

private fun registerUser(
    email: String,
    password: String,
    user: User, // Assuming this is filled with the necessary user profile data
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // User is successfully registered and authenticated
            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
            user.userId = userId // Ensure the userProfile has the correct userId
            createUserProfile(user, {
                // Success callback
                scope.launch {
                    snackBarHostState.showSnackbar("Profile created successfully")
                }
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }, { errMsg ->
                // Error callback
                scope.launch {
                    snackBarHostState.showSnackbar("Profile creation failed: $errMsg")
                }
            })
        } else {
            // Registration failed
            task.exception?.let { exception ->
                scope.launch {
                    snackBarHostState.showSnackbar("Registration failed: ${exception.localizedMessage}")
                }
            }
        }
    }
}