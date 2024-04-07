package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wellnessfusionapp.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



/*
  * This is the signup screen where the user can create an account in the app
  * here you will see methods like register and create user profile using firebase authentication and firestore
  * you will also see that we confirming the profile creation through toasts and performing error handling based on user input
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(navController: NavController) {

    /*
     * Here we are using remember to store the state of the input fields through recomposition (all)
     */

    val coroutineScope = rememberCoroutineScope()
    val context =  LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    /*
     * Scaffold setup for the screen
     * Inside the column all the custom composables for each function of the screen
     */
    Scaffold {
        Box(
            modifier = Modifier.background(Color.Black),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(navController)
                Spacer(modifier = Modifier.height(30.dp))
                UserInputFields(
                    name,
                    email,
                    password,
                    confirmPassword,
                    onNameChange = { name = it },
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onConfirmPasswordChange = { confirmPassword = it })
                Spacer(modifier = Modifier.height(24.dp))
                SignUpButton(
                    coroutineScope,
                    name,
                    email,
                    password,
                    confirmPassword,
                    context,
                    navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/*
 * Top bar composable, i have put it separately due for better readability and edition
 * We are using navController to go back to login if wished or clicked by mistake as our app already leads you to home screen after sign up.
 */
@Composable
fun TopAppBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController.navigate("login") }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(30.dp), tint = Color.White)
        }
        Text("Sign Up", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(7.dp), fontSize = 25.sp, color = Color.White)
    }
}

/*
 * User input fields for all setup system of user profile,
 * all information here will be passed to fireBase authentication and firestore whenever profile is created
 * We are passing the values and the onValueChange functions to update the values
 */

@Composable
fun UserInputFields(name: String, email: String, password: String, confirmPassword: String, onNameChange: (String) -> Unit, onEmailChange: (String) -> Unit, onPasswordChange: (String) -> Unit, onConfirmPasswordChange: (String) -> Unit) {
    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Username",color = Color(0xffFE7316)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),textStyle = TextStyle(color = Color.White))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email Address",color = Color(0xffFE7316)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),textStyle = TextStyle(color = Color.White))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = password, onValueChange = onPasswordChange, label = { Text("Password",color = Color(0xffFE7316)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { ImeAction.Next }), visualTransformation = PasswordVisualTransformation(),textStyle = TextStyle(color = Color.White))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = confirmPassword, onValueChange = onConfirmPasswordChange, label = { Text("Confirm Password",color = Color(0xffFE7316)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onNext = { ImeAction.Done }), visualTransformation = PasswordVisualTransformation(),textStyle = TextStyle(color = Color.White))
    Spacer(modifier = Modifier.height(24.dp))
}


/*
 * The button which performs the sign up action and calls the registerUser function for the user to be created
 * We are passing the coroutineScope, name, email, password, confirmPassword, snack-barHostState and navController to the function
 * CoroutineScope is used here to perform a few operations in the background to check if the user is inputting the correct data
 * it is important to use it due to its stability that it gives to the program, it executes the operations asynchronously keeping the app responsive
 */
@Composable
fun SignUpButton(coroutineScope: CoroutineScope, name: String, email: String, password: String, confirmPassword: String, context: Context, navController: NavController) {
    Column {
        Button(
            colors = ButtonDefaults.buttonColors(
                Color(0xffFE7316),
            ),
            onClick = {
            coroutineScope.launch {
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                when {
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> context.showToast("Please fill in all fields")

                    !email.matches(emailPattern.toRegex()) -> context.showToast("Invalid email format")
                    password != confirmPassword -> context.showToast("Passwords do not match")


                    /*
                     * If patter is matched then user is registered with the information that's inside "User",
                     * which means you will see those fields in firestore when registered.
                     */
                    else -> registerUser(email, password, User(name = name, email = email, userId = FirebaseAuth.getInstance().currentUser?.uid ?: "", password = password), coroutineScope, context, navController)
                }
            }
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Sign Up")
        }
    }
}


/*
 * Registering the user in firestore with email and password
 * We are using the FirebaseAuth to create the user and then passing the user to the createUserProfile function
 * We are also handling any errors and success with snackBarHostState
 */
private fun registerUser(
    email: String,
    password: String,
    user: User, // Profile data is inside of it
    scope: CoroutineScope,
    context: Context,
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
                    context.showToast("Profile created successfully")
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }, { errMsg ->
                // Error callback
                scope.launch {
                    context.showToast("Profile creation failed: $errMsg")
                }
            })
        } else {
            // Registration failed
            task.exception?.let { exception ->
                scope.launch {
                    context.showToast("Registration failed: ${exception.localizedMessage}")
                }
            }
        }
    }
}
/*
 * Creating the user profile firestore function
 * Creating a user profile in firestore with the user data inserted in the text fields and passed to the register user function
 * On success user goes to home screen and on error user gets a toast message
 */
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


/*
 * Custom toast function to show the user a message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}





//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SignUpScreen(navController: NavController) {
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Spacer(modifier = Modifier.width(0.dp))
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Row (
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start
//                ){
//                    IconButton(
//                        onClick = { navController.navigate("login") }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(30.dp)
//                        )
//                    }
//
//                    Text(
//                        "Sign Up",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(7.dp),
//                        fontSize = 25.sp
//                    )
//                }
//
//
//
//                Spacer(modifier = Modifier.height(30.dp))
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Username") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = {
//                        email = it
//                    },
//                    label = { Text("Email Address") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text("Password") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
//                    keyboardActions = KeyboardActions(onNext = { })
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = confirmPassword,
//                    onValueChange = { confirmPassword = it },
//                    label = { Text("Confirm Password") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                    keyboardActions = KeyboardActions(onNext = {})
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = {
//                        coroutineScope.launch {
//                            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
//                            // Check for empty fields first
//                            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
//                                snackbarHostState.showSnackbar("Please fill in all fields")
//                            } else if (!email.matches(emailPattern.toRegex())) {
//                                snackbarHostState.showSnackbar("Invalid email format")
//                            } else if (password != confirmPassword) {
//                                snackbarHostState.showSnackbar("Passwords do not match")
//                            } else {
//                                // Proceed with registering the user
//                                registerUser(
//                                    email,
//                                    password,
//                                    User(
//                                        name = name,
//                                        email = email,
//                                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//                                    ),
//                                    coroutineScope,
//                                    snackbarHostState,
//                                    navController
//                                )
//                            }
//                            Toast.makeText(context,
//                                "Profile created successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            navController.navigate("home")
//                        }
//                    },
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Sign Up")
//                }
//                Spacer(modifier = Modifier.height(24.dp))
//                Column (
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ){
//
//
//                    TextButton(onClick = {
//                    }) {
//                        Text(
//                            "By signing up you agree to our Privacy Policy",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Blue
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun createUserProfile(
//    user: User,
//    onSuccess: () -> Unit,
//    onError: (String) -> Unit
//) {
//    val db = FirebaseFirestore.getInstance()
//    db.collection("Users").document(user.userId)
//        .set(user)
//        .addOnSuccessListener { onSuccess() }
//        .addOnFailureListener { e -> onError(e.message ?: "An unknown error occurred") }
//}
//
//private fun registerUser(
//    email: String,
//    password: String,
//    user: User, // Assuming this is filled with the necessary user profile data
//    scope: CoroutineScope,
//    snackBarHostState: SnackbarHostState,
//    navController: NavController
//) {
//    val auth = FirebaseAuth.getInstance()
//    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            // User is successfully registered and authenticated
//            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//            user.userId = userId // Ensure the userProfile has the correct userId
//            createUserProfile(user, {
//                // Success callback
//                scope.launch {
//                    snackBarHostState.showSnackbar("Profile created successfully")
//                }
//            }, { errMsg ->
//                // Error callback
//                scope.launch {
//                    snackBarHostState.showSnackbar("Profile creation failed: $errMsg")
//                }
//            })
//        } else {
//            // Registration failed
//            task.exception?.let { exception ->
//                scope.launch {
//                    snackBarHostState.showSnackbar("Registration failed: ${exception.localizedMessage}")
//                }
//            }
//        }
//    }
//}
