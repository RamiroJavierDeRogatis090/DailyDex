package com.example.dailydex.presentation.viewmodel

import com.example.dailydex.data.supabase.SupabaseTaskRepository
import com.example.dailydex.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel {

    private val repository = SupabaseTaskRepository()

    private val _uiState = MutableStateFlow(
        HomeUiState(
            tasks = listOf(
                Task(
                    id = "1",
                    title = "Estudiar Kotlin Multiplatform",
                    description = "Avanzar con DailyDex",
                    createdAt = "2026-10-05"
                ),
                Task(
                    id = "2",
                    title = "Crear arquitectura",
                    description = "Implementar MVVM",
                    createdAt = "2026-10-05"
                ),
                Task(
                    id = "3",
                    title = "Entregar challenge",
                    description = "Enviar APK y repositorio",
                    createdAt = "2026-10-05"
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
            id = (
                    (_uiState.value.tasks.maxOfOrNull {
                        it.id.toIntOrNull() ?: 0
                    } ?: 0) + 1
                    ).toString(),
            title = title,
            description = description,
            createdAt = "2026-10-05"
        )

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks + newTask
        )
    }

    fun updateTask(
        id: String,
        title: String,
        description: String
    ) {

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { task ->

                if (task.id == id) {
                    task.copy(
                        title = title,
                        description = description
                    )
                } else {
                    task
                }
            }
        )
    }

    fun deleteTask(id: String) {

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.filter {
                it.id != id
            }
        )
    }

    fun toggleTask(id: String) {

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { task ->

                if (task.id == id) {
                    task.copy(
                        completed = !task.completed
                    )
                } else {
                    task
                }
            }
        )
    }

    suspend fun testSupabase() {

        try {

            println("===================================")
            println("ANTES DEL GET")

            val result = repository.getTasks()

            println("SUPABASE RESPONSE:")
            println(result)
            println("LARGO RESPUESTA = ${result.length}")

            println("===================================")

        } catch (e: Exception) {

            println("===================================")
            println("SUPABASE ERROR:")
            println(e.toString())
            println("===================================")
        }

    }
    }
