package com.example.wellnessfusionapp

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievedGoals(navController: NavController, viewModel: MainViewModel) { // Removed the unnecessary `goal: Goal` parameter
    val completedGoals = viewModel.completedGoals.observeAsState(listOf()).value // Access the `.value`
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achieved Goals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

                // Removed incorrect CardColors usage

                LazyColumn {
                    items(completedGoals) { goal -> // Ensure you're using the correct list
                        GoalCard(goal = goal, navController = navController)
                    }
                }

        }
    }
}

@Composable
fun GoalCard(goal: Goal, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .alpha(0.9f),
    colors = CardColors(
        Color.Black,
        Color(0xffFE7316),
        Color(0xff5c7a92),
        Color(0xffFE7316),
    )
    )
    {
        GoalCardItem(goal = goal, navController = navController, onUpdateClick = { /* Do nothing */ })
    }
}

@Composable
fun GoalCardItem(
    goal: Goal,
    navController: NavController,
    onUpdateClick: (Goal) -> Unit // Callback quando o botão de atualizar é clicado
) {
    val progress = calculateProgress(goal.currentValue.toFloat(), goal.currentValue.toFloat(), goal.desiredValue.toFloat())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(130.dp)
                .padding(8.dp)
                .clickable { navController.navigate("goalProgressRecord/${goal.id}") }
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp),
                color = Color(0xffFE7316),
                strokeWidth = 9.dp,
                trackColor = Color(0xff5c7a92)

            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xffFE7316)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ){
                val dateFormater = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "Start Date: ${goal.startDate.toDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xffFE7316)
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
    }
}