package com.example.dailydex.presentation.navigation

sealed class Routes {

    data object Home : Routes()

    data object CreateTask : Routes()
}