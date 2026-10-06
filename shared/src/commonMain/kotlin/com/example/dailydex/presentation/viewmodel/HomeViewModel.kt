package com.example.dailydex.presentation.viewmodel

import com.example.dailydex.data.supabase.SupabaseTaskRepository
import com.example.dailydex.data.supabase.toTask
import com.example.dailydex.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.dailydex.data.supabase.toTask


class HomeViewModel {

    private val repository = SupabaseTaskRepository()

    private val _uiState = MutableStateFlow(
        HomeUiState(
            tasks = emptyList()
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

    suspend fun loadTasksFromSupabase() {

        try {

            val tasks = repository
                .getTasks()
                .map {
                    it.toTask()
                }

            _uiState.value = _uiState.value.copy(
                tasks = tasks
            )

            println("===================================")
            println("TAREAS CARGADAS DESDE SUPABASE")
            println(tasks.size)
            println("===================================")

        } catch (e: Exception) {

            println("===================================")
            println("LOAD TASKS ERROR")
            println(e.toString())
            println("===================================")
        }
    }

    suspend fun addTaskToSupabase(
        title: String,
        description: String
    ) {

        repository.insertTask(
            title = title,
            description = description
        )

        loadTasksFromSupabase()
    }

}
