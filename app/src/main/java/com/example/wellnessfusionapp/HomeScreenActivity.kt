package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.Navigation.MainTopBar
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: CategoryViewModel) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {

            MainTopBar(
                title = "Welcome to Wellness Fusion",
                navController = navController,
                onMenuClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) {
        paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant),

                    textAlign = TextAlign.Center,
                    text = "Quick Workout Planner",
                    style = MaterialTheme.typography.headlineSmall
                )



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    //button physical
                    Button(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(0.dp)
                            .height(260.dp),
                        onClick = { navController.navigate("physicalCategory") },
                        shape = RoundedCornerShape(0.dp)
                    )
                    {
                        Text(
                            "Physical",
                            style = MaterialTheme.typography.bodySmall
                        )

                    }
                    //button mental
                    Button(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(0.dp)
                            .height(260.dp),
                        onClick = { navController.navigate("zenCategory") },
                        shape = RoundedCornerShape(0.dp)
                    )
                    {
                        Text(
                            "Mental",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth().height(200.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    //Desc
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Select a category to get started,Select a category to get started,Select a category to get started,Select a category to get started",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Select a category to get started,Select a category to get started,Select a category to get started,Select a category to get started",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }



@Composable
private fun WorkoutOptionWithDescription(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(160.dp) // Set width to match the button and description box
    ) {

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Adjusted for a standard button height
        ) {
            Icon(
                icon,
                contentDescription = title
            )
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
        DescriptionBox(
            description = description,
            modifier = Modifier.fillMaxWidth() // Ensure the description box uses the full width
        )
    }
}

@Composable
fun DescriptionBox(description: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            // Added padding for better visual separation
            .heightIn(min = 80.dp), // Ensure a minimum height for the description box
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}