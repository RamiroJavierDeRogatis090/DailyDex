package com.example.dailydex.presentation.viewmodel

import com.example.dailydex.domain.model.Task

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
