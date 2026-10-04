package com.example.dailydex

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.dailydex.presentation.navigation.Routes
import com.example.dailydex.presentation.screens.CreateTaskScreen
import com.example.dailydex.presentation.screens.HomeScreen

@Composable
fun App() {

    var currentScreen by remember {
        mutableStateOf<Routes>(Routes.Home)
    }

    MaterialTheme {

        when (currentScreen) {

            Routes.Home -> {
                HomeScreen(
                    onAddTaskClick = {
                        currentScreen = Routes.CreateTask
                    }
                )
            }

            Routes.CreateTask -> {
                CreateTaskScreen(
                    onBack = {
                        currentScreen = Routes.Home
                    }
                )
            }
        }
    }
}