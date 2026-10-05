package com.example.dailydex.presentation.viewmodel

import com.example.dailydex.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            tasks = listOf(
                Task(
                    id = "1",
                    title = "Estudiar Kotlin Multiplatform",
                    description = "Avanzar con DailyDex"
                ),
                Task(
                    id = "2",
                    title = "Crear arquitectura",
                    description = "Implementar MVVM"
                ),
                Task(
                    id = "3",
                    title = "Entregar challenge",
                    description = "Enviar APK y repositorio"
                )
            )
        )
    )

    val uiState = _uiState.asStateFlow()

    fun addTask(
        title: String,
        description: String
    ) {

        val newTask = Task(
            id = (_uiState.value.tasks.size + 1).toString(),
            title = title,
            description = description
        )

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks + newTask
        )
    }
}