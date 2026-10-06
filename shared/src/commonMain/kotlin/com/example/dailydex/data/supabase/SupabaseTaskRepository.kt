package com.example.dailydex.data.supabase

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SupabaseTaskRepository {

    private val client = HttpClient {

        install(ContentNegotiation) {

            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getTasks(): List<TaskDto> {

        val response = client.get(
            "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks"
        ) {

            header(
                "apikey",
                SupabaseConfig.SUPABASE_KEY
            )

            header(
                "Authorization",
                "Bearer ${SupabaseConfig.SUPABASE_KEY}"
            )
        }.bodyAsText()

        return Json.decodeFromString(
            ListSerializer(TaskDto.serializer()),
            response
        )
    }

    suspend fun insertTask(
        title: String,
        description: String
    ) {

        val task = InsertTaskDto(
            title = title,
            description = description,
            completed = false
        )

        client.post(
            "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks"
        ) {

            header(
                "apikey",
                SupabaseConfig.SUPABASE_KEY
            )

            header(
                "Authorization",
                "Bearer ${SupabaseConfig.SUPABASE_KEY}"
            )

            header(
                "Prefer",
                "return=minimal"
            )

            contentType(ContentType.Application.Json)

            setBody(task)
        }
    }
}