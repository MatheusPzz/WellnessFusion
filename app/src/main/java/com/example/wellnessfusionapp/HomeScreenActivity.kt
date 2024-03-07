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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.Navigation.MainTopBar
import com.example.wellnessfusionapp.ViewModels.CategoryViewModel
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: CategoryViewModel,
    viewModel2: MainViewModel,
    exerciseId: String
) {

    val note = remember { mutableStateOf("") }
    val notes = viewModel2.notes.observeAsState(initial = emptyMap())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {

            MainTopBar(
                title = "Welcome to Wellness Fusion ",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                //button physical
                Row(

                ) {
                    Button(
                        modifier = Modifier
                            .width(160.dp)
                            .height(150.dp)
                            .padding(15.dp),
                        onClick = { navController.navigate("physicalCategory") },
                        shape = RoundedCornerShape(10.dp)
                    )
                    {
                        Text(
                            "Physical",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(15.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Focused on the body, physical exercises are designed to improve strength, endurance, and flexibility. They can be performed at home or in a gym.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }


                }
                Row(

                ) {
                    Button(
                        modifier = Modifier
                            .width(160.dp)
                            .height(150.dp)
                            .padding(15.dp),
                        onClick = { navController.navigate("physicalCategory") },
                        shape = RoundedCornerShape(10.dp)
                    )
                    {
                        Text(
                            "Physical",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(15.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Focused on the mind, zen exercises are designed to improve mental health, reduce stress, and promote relaxation. They can be performed at home or in a gym.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }


                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Log your activity here", style = MaterialTheme.typography.bodyMedium)
                NotesSection(viewModel2, exerciseId)

            }

        }
    }

}


@Composable
fun NotesSection(viewModel: MainViewModel, exerciseId: String) {
    val allNotes = viewModel.notes.observeAsState(initial = emptyMap())
    val notes = allNotes.value[exerciseId] ?: emptyList()
    var noteText by remember { mutableStateOf("") }


    fun formatTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm a", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(timestamp)
    }

    Column {
        OutlinedTextField(value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note any progression here") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    if (noteText.isNotBlank()) {
                        viewModel.saveNotesForUser(exerciseId, noteText)
                        noteText = ""
                    }
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save Note")
                }
            })
        LazyColumn {
            items(notes) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // Espaço vertical entre itens
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = note.noteText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = formatTimestamp(note.timestamp),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

















//@Composable
//private fun WorkoutOptionWithDescription(
//    title: String,
//    description: String,
//    icon: ImageVector,
//    onClick: () -> Unit,
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable(onClick = onClick)
//            .width(160.dp) // Set width to match the button and description box
//    ) {
//
//        Button(
//            onClick = onClick,
//            shape = RoundedCornerShape(0.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp) // Adjusted for a standard button height
//        ) {
//            Icon(
//                icon,
//                contentDescription = title
//            )
//            Text(text = title, style = MaterialTheme.typography.bodySmall)
//        }
//        DescriptionBox(
//            description = description,
//            modifier = Modifier.fillMaxWidth() // Ensure the description box uses the full width
//        )
//    }
//}
//
//@Composable
//fun DescriptionBox(description: String, modifier: Modifier = Modifier) {
//    Surface(
//        modifier = modifier
//            // Added padding for better visual separation
//            .heightIn(min = 80.dp), // Ensure a minimum height for the description box
//        shape = RoundedCornerShape(8.dp),
//        color = MaterialTheme.colorScheme.surfaceVariant,
//    ) {
//        Text(
//            text = description,
//            modifier = Modifier.padding(8.dp),
//            style = MaterialTheme.typography.bodySmall,
//            textAlign = TextAlign.Center
//        )
//    }
//}



