package com.example.wellnessfusionapp

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Navigation.BottomNavBar
import kotlinx.coroutines.launch

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SettingsScreen(navController: NavController) {
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    Scaffold(
//        topBar = { MainTopBar(
//
//            title = "Wellness Fusion",
//            navController = navController,
//            userName = "User",
//        )
//        },
//        bottomBar = { BottomNavBar(navController) }
//    ){
//        AppModalNavigationDrawer(
//            drawerState = drawerState,
//            scope = scope,
//            navController = navController
//        ) { innerPadding ->
//            // Your screen content here, considering innerPadding to avoid overlap
//            Column(modifier = Modifier.padding(innerPadding)) {
//                // Your screen content
//            }
//        }
//    }
//}