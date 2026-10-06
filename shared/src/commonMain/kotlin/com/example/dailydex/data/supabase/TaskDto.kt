package com.example.dailydex.data.supabase

import com.example.dailydex.domain.model.Task
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean,
    val created_at: String? = null
)

fun TaskDto.toTask(): Task {

    return Task(
        id = id?.toString() ?: "",
        title = title,
        description = description,
        completed = completed,
        createdAt = created_at ?: ""
    )
}