package com.example.dailydex

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.dailydex.presentation.navigation.Routes
import com.example.dailydex.presentation.screens.CreateTaskScreen
import com.example.dailydex.presentation.screens.EditTaskScreen
import com.example.dailydex.presentation.screens.HomeScreen
import androidx.compose.runtime.LaunchedEffect
import com.example.dailydex.presentation.viewmodel.ViewModelProvider

@Composable
fun App() {
    val viewModel = ViewModelProvider.homeViewModel

    LaunchedEffect(Unit) {
        viewModel.testSupabase()
    }

    var currentScreen by remember {
        mutableStateOf<Routes>(Routes.Home)
    }

    var taskId by remember {
        mutableStateOf("")
    }

    var taskTitle by remember {
        mutableStateOf("")
    }

    var taskDescription by remember {
        mutableStateOf("")
    }

    MaterialTheme {

        when (currentScreen) {

            Routes.Home -> {

                HomeScreen(
                    onAddTaskClick = {
                        currentScreen = Routes.CreateTask
                    },

                    onEditTaskClick = { id, title, description ->

                        taskId = id
                        taskTitle = title
                        taskDescription = description

                        currentScreen = Routes.EditTask
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

            Routes.EditTask -> {

                EditTaskScreen(
                    taskId = taskId,
                    currentTitle = taskTitle,
                    currentDescription = taskDescription,
                    onBack = {
                        currentScreen = Routes.Home
                    }
                )
            }
        }
    }
}