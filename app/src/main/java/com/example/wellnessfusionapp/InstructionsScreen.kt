package com.example.wellnessfusionapp

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(exerciseId: String, viewModel: MainViewModel, navController: NavController) {
    val pagerState = rememberPagerState()
    val instructions = viewModel.instructions.observeAsState()

    LaunchedEffect(exerciseId) {
        viewModel.fetchInstructionsForExercise(exerciseId)
    }

    instructions.value?.let { instruction ->
        Scaffold(topBar = {
            TopAppBar(title = { Text(text = instruction.exerciseName) }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }, bottomBar = {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
//                NotesSection(viewModel, exerciseId)
            }
        }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedGif(imageUrl = instruction.imageUrl)
                        Spacer(modifier = Modifier.size(10.dp))
                        MuscleWorkedSection(instruction)
                        Spacer(modifier = Modifier.size(10.dp))
                        InstructionsSection(instruction)
                        Spacer(modifier = Modifier.size(10.dp))
                        ExtraContentSection(instruction)


                    }
                }
            }
        }

    }
}
@Composable
fun AnimatedGif (imageUrl: String) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val painter = rememberImagePainter(
        data = imageUrl,
        imageLoader = imageLoader,
        builder = {
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
        })
    Image(
        painter = painter,
        contentDescription = "Animated Gif",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}


@Composable
fun MuscleWorkedSection(instruction: Instructions) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .shadow(5.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Target Muscles:")
            Spacer(modifier = Modifier.size(15.dp))
            Text(text = "Primary Muscles: ${instruction.musclesPrimary}")
            Text(text = "Secondary Muscles: ${instruction.musclesSecondary}")
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = instruction.musclesWorkedImage,
                    contentDescription = "Muscles Worked",
                    modifier = Modifier.size(250.dp)
                )
            }


        }
    }
}

@Composable
fun InstructionsSection(instruction: Instructions) {
    val steps = instruction.instructions.split(".")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .shadow(5.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Instructions:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(10.dp))
            steps.forEachIndexed { index, step ->
                Spacer(modifier = Modifier.size(10.dp))
                if (step.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("${index + 1} ", style = MaterialTheme.typography.titleMedium)
                        Text(step.trim(), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExtraContentSection(instruction: Instructions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp),
        ){
            Column(
                modifier = Modifier.fillMaxWidth().padding(10.dp),

                ) {
                Text("Sets: ${instruction.sets}")
                Text("Reps: ${instruction.reps}")
                Text("Weight: ${instruction.weight} Kg for beginners")
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp),
        ){
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Professional Content: ${instruction.videoUrl}")
        }
            }
    }
}

// function to write personalized notes for user in each exercise

//@Composable
//fun NotesSection(viewModel: MainViewModel, exerciseId: String) {
//    val allNotes = viewModel.notes.observeAsState(initial = emptyMap())
//    val notes = allNotes.value[exerciseId] ?: emptyList()
//    var noteText by remember { mutableStateOf("") }
//
//
//    fun formatTimestamp(timestamp: Long): String {
//        val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm a", Locale.getDefault())
//        formatter.timeZone = TimeZone.getDefault()
//        return formatter.format(timestamp)
//    }
//
//    Column {
//        OutlinedTextField(value = noteText,
//            onValueChange = { noteText = it },
//            label = { Text("Note any progression here") },
//            modifier = Modifier.fillMaxWidth(),
//            trailingIcon = {
//                IconButton(onClick = {
//                    if (noteText.isNotBlank()) {
//                        viewModel.saveNotesForUser(exerciseId, noteText)
//                        noteText = ""
//                    }
//                }) {
//                    Icon(Icons.Default.Check, contentDescription = "Save Note")
//                }
//            })
//    }
//}

//        LazyColumn {
//            items(notes) { note ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(vertical = 4.dp), // Adiciona um pequeno espaço vertical entre os itens
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    // Limita o espaço que o texto da nota pode ocupar
//                    Box(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = note.noteText, maxLines = 1, // Limita o texto a uma única linha
//                            overflow = TextOverflow.Ellipsis, // Adiciona "..." se o texto for muito longo
//                            modifier = Modifier.padding(end = 8.dp) // Garante espaço entre o texto da nota e o timestamp
//                        )
//                    }
//                    // Exibe o timestamp, garantindo que ele não será empurrado para fora
//                    Text(
//                        text = formatTimestamp(note.timestamp),
//                        modifier = Modifier.padding(start = 8.dp) // Adiciona um pequeno espaço antes do timestamp
//                    )
//                }
//            }
//        }


//HorizontalPager(
//count = 3, // Total de páginas
//state = pagerState, modifier = Modifier.height(250.dp)
//) { page ->
//    when (page) {
//        0 -> MuscleWorkedSection(instruction)
//
//        1 -> InstructionsSection(instruction)
//
//        2 -> ExtraContentSection(instruction)
//    }
//}

