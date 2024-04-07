package com.example.wellnessfusionapp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.GoalCategory
import com.example.wellnessfusionapp.ViewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievedGoals(navController: NavController, viewModel: MainViewModel) {
    val completedGoals = viewModel.completedGoals.observeAsState(listOf()).value
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFFFE7316),
                ),
                title = { Text("Achieved Goals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_logs),
                contentDescription = "Achieved Goals Background",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (
                completedGoals.isEmpty()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No goals achieved yet",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                        Text(
                            text = "Keep working on it!",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
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
    }
}

@Composable
fun GoalCard(goal: Goal, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardColors(
            Color.Black.copy(alpha = 0.8f),
            Color(0xffFE7316),
            Color(0xff5c7a92),
            Color(0xffFE7316),
        )
    )
    {
        GoalCardItem(
            goal = goal,
            navController = navController,
            onUpdateClick = { /* Do nothing */ })
    }
}

@Composable
fun GoalCardItem(
    goal: Goal,
    navController: NavController,
    onUpdateClick: (Goal) -> Unit // Callback quando o botão de atualizar é clicado
) {
    val progress = calculateProgress(
        goal.currentValue.toFloat(),
        goal.currentValue.toFloat(),
        goal.desiredValue.toFloat()
    )
    val colorForProgressIndicator = if (goal.type.category == GoalCategory.PHYSICAL) {
        Color(0xffFE7316)
    } else {
        Color.Blue
    }

    val (isExpandedLine, setExpandedLine) = remember { mutableStateOf(false) }
    val clickModifier = Modifier.clickable { setExpandedLine(!isExpandedLine) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .padding(6.dp)
                .clickable { navController.navigate("goalProgressRecord/${goal.id}") }
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .size(130.dp),
                color = colorForProgressIndicator,
                strokeWidth = 7.dp,
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
                        modifier = clickModifier.animateContentSize(),
                        maxLines = if (isExpandedLine) Int.MAX_VALUE else 1,
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xffFE7316),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val dateFormater = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Well Done!!",
                    color = Color.White,
                    fontSize = 30.sp,
                )

            }
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Back",
                tint = Color.Yellow,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Start Date: ${dateFormater.format(goal.startDate.toDate())}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "End Date: ${dateFormater.format(goal.endDate.toDate())}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
        }
    }
}