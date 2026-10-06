package com.example.dailydex.data.supabase

import kotlinx.serialization.Serializable

@Serializable
data class InsertTaskDto(
    val title: String,
    val description: String,
    val completed: Boolean = false
)