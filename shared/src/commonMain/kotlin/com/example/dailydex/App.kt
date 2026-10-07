package com.example.dailydex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dailydex.presentation.navigation.Routes
import com.example.dailydex.presentation.screens.CreateTaskScreen
import com.example.dailydex.presentation.screens.EditTaskScreen
import com.example.dailydex.presentation.screens.HomeScreen
import com.example.dailydex.presentation.screens.LoginScreen
import com.example.dailydex.presentation.theme.DailyDexTheme
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun App() {

    val viewModel = ViewModelProvider.homeViewModel

    val authViewModel = ViewModelProvider.authViewModel

    val authState by authViewModel.uiState.collectAsState()

    val appScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.restoreSession()
    }

    LaunchedEffect(authState.isAuthenticated) {

        if (authState.isAuthenticated) {
            viewModel.loadTasksFromSupabase()
        }
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

    DailyDexTheme {

        when {

            authState.isCheckingSession -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            !authState.isAuthenticated -> {

                LoginScreen()
            }

            else -> {

                AnimatedContent(
                    targetState = currentScreen,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {

                        val goingForward =
                            targetState.depth > initialState.depth

                        if (goingForward) {

                            (slideInHorizontally(animationSpec = tween(350)) { it } +
                                fadeIn(animationSpec = tween(350))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(350)) { -it / 3 } +
                                    fadeOut(animationSpec = tween(250)))

                        } else {

                            (slideInHorizontally(animationSpec = tween(350)) { -it } +
                                fadeIn(animationSpec = tween(350))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(350)) { it / 3 } +
                                    fadeOut(animationSpec = tween(250)))
                        }
                    },
                    label = "screenTransition"
                ) { screen ->

                    when (screen) {

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
                                },

                                onSignOut = {

                                    appScope.launch {
                                        authViewModel.signOut()
                                    }
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
        }
    }
}

private val Routes.depth: Int
    get() = when (this) {
        Routes.Home -> 0
        Routes.CreateTask, Routes.EditTask -> 1
    }
