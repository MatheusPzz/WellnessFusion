package com.example.wellnessfusionapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone



/*
 Instructions screen, here the user can see detailed information about
 each exercise, how to execute them and all.
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(exerciseId: String, viewModel: MainViewModel, navController: NavController) {

    // Fetching the instructions for each exercise
    val instructions = viewModel.instructions.observeAsState()


    LaunchedEffect(exerciseId) {
        viewModel.fetchInstructionsForExercise(exerciseId)
    }

    // Displaying the instructions for each exercise in a scaffold with a top app bar
    instructions.value?.let { instruction ->
        Scaffold(topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                title = { Text(text = instruction.exerciseName) }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }, bottomBar = {
            BottomNavBar(navController = navController)
        }
        ) { paddingValues ->

            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
            ) {
                // Displaying the instructions in a lazy column
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
                            // i have split the into 4 sections of composable for better edition and readability
                            AnimatedGif(imageUrl = instruction.imageUrl)
                            Spacer(modifier = Modifier.size(10.dp))
                            MuscleWorkedSection(instruction)
                            Spacer(modifier = Modifier.size(10.dp))
                            InstructionsSection(instruction)
                            ExtraContentSection(instruction)
                        }
                    }
                }
            }
        }

    }
}

/*
 Here we are loading the animated gif for each exercise
 */
@Composable
fun AnimatedGif (imageUrl: String) {

    // Loading the image with coil
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


/*
 Muscle worked section, here we display the muscles worked by each exercise
 */
@Composable
fun MuscleWorkedSection(instruction: Instructions) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .shadow(5.dp),
        colors = CardColors(
            Color.Gray.copy(alpha = 0.2f),
            Color.White,
            Color(0xff5c7a92),
            Color(0xffFE7316),
        )
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if(instruction.exerciseId == "12" || instruction.exerciseId == "13" || instruction.exerciseId == "14") {
                Text("Mental Target: \n${instruction.targetMental}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = instruction.musclesWorkedImage,
                        contentDescription = "Muscles Worked",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            } else {

                // Calling the information fetched from the API to our UI
                Text("Target Muscles:", fontWeight = FontWeight.Bold)
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
}

/*
 Instructions section is displaying the incstructions for each exercise
 that were also fetched from the API, firestore in this case
 */
@Composable
fun InstructionsSection(instruction: Instructions) {
    val steps = instruction.instructions.split(".")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .shadow(5.dp),
        colors = CardColors(
            Color.Gray.copy(alpha = 0.2f),
            Color.White,
            Color(0xff5c7a92),
            Color(0xffFE7316),
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Instructions:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(10.dp))
            // Formatting the lines and putting it into steps which are numbered
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


/*
 Basic recommended information for the user mainly for beginners and
 link to a page that talks about each of the exercises that are displayed
 professional content
 */
@Composable
fun ExtraContentSection(instruction: Instructions) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp),
            colors = CardColors(
                Color.Gray.copy(alpha = 0.2f),
                Color.White,
                Color(0xff5c7a92),
                Color(0xffFE7316),
            )
        ){
            if(instruction.workoutType == "Mental") {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // section that shows recommended sets, reps and weight for each exercise, this information is also fetched from firestore
                    Text(text = "Recommended", color = Color.White)
                    Text(text = "Sets: ${instruction.sets} daily", color = Color.White)
                    Text(text = "Duration: ${instruction.duration} minutes (min)", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // section that shows recommended sets, reps and weight for each exercise, this information is also fetched from firestore
                    Text(text = "Recommended", color = Color.White)
                    Text(text = "Sets: ${instruction.sets}", color = Color.White)
                    Text(text = "Reps: ${instruction.reps}", color = Color.White)
                    Text(text = "Weight: ${instruction.weight} KG", color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp),
            colors = CardColors(
                Color.Gray.copy(alpha = 0.2f),
                Color.White,
                Color(0xff5c7a92),
                Color(0xffFE7316),
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Professional Content:")


                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xffFE7316)),
                    onClick = {
                        // Use Uri.parse() to transform the String URL into a Uri
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instruction.videoUrl))
                        context.startActivity(intent)
                        Log.d("URL Check", "URL to open: ${instruction.videoUrl}")
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Link to professional content")
                }
            }
        }
    }
}
