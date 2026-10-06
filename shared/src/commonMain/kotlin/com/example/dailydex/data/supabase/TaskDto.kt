package com.example.dailydex.data.supabase

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean,
    val created_at: String? = null
)