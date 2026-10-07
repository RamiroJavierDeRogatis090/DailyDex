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

    suspend fun toggleTaskInSupabase(id: String): String? {

        val task = _uiState.value.tasks
            .firstOrNull { it.id == id }
            ?: return null

        val toggled = task.copy(completed = !task.completed)

        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map {
                if (it.id == id) toggled else it
            }
        )

        return try {

            repository.updateTask(
                id = id,
                title = toggled.title,
                description = toggled.description,
                completed = toggled.completed
            )

            loadTasksFromSupabase()

            null

        } catch (e: Exception) {

            _uiState.value = _uiState.value.copy(
                tasks = _uiState.value.tasks.map {
                    if (it.id == id) task else it
                }
            )

            println("TOGGLE TASK ERROR -> ${e.message}")

            e.message ?: "No se pudo actualizar la tarea."
        }
    }

    suspend fun loadTasksFromSupabase() {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        try {

            val tasks = repository
                .getTasks()
                .map {
                    it.toTask()
                }

            _uiState.value = _uiState.value.copy(
                tasks = tasks,
                isLoading = false
            )

        } catch (e: Exception) {

            println("LOAD TASKS ERROR -> ${e.message}")

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No se pudieron cargar las tareas. Revisá tu conexión."
            )
        }
    }

    suspend fun addTaskToSupabase(
        title: String,
        description: String
    ): String? {

        return try {

            repository.insertTask(
                title = title,
                description = description
            )

            loadTasksFromSupabase()

            null

        } catch (e: Exception) {

            println("ADD TASK ERROR -> ${e.message}")

            e.message ?: "No se pudo guardar la tarea."
        }
    }

    suspend fun deleteTaskFromSupabase(
        id: String
    ): String? {

        val previous = _uiState.value.tasks

        _uiState.value = _uiState.value.copy(
            tasks = previous.filterNot { it.id == id }
        )

        return try {

            repository.deleteTask(id)

            loadTasksFromSupabase()

            null

        } catch (e: Exception) {

            _uiState.value = _uiState.value.copy(
                tasks = previous
            )

            println("DELETE TASK ERROR -> ${e.message}")

            e.message ?: "No se pudo eliminar la tarea."
        }
    }

    suspend fun updateTaskInSupabase(
        id: String,
        title: String,
        description: String
    ): String? {

        return try {

            val completed = _uiState.value.tasks
                .firstOrNull { it.id == id }
                ?.completed
                ?: false

            repository.updateTask(
                id = id,
                title = title,
                description = description,
                completed = completed
            )

            loadTasksFromSupabase()

            null

        } catch (e: Exception) {

            println("UPDATE TASK ERROR -> ${e.message}")

            e.message ?: e.toString()
        }
    }

}
