package com.example.dailydex.data.supabase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.request.patch

class SupabaseTaskRepository {

    private val client = HttpClient {

        expectSuccess = true

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

    suspend fun deleteTask(
        id: String
    ) {

        client.delete(
            "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks?id=eq.$id"
        ) {

            header(
                "apikey",
                SupabaseConfig.SUPABASE_KEY
            )

            header(
                "Authorization",
                "Bearer ${SupabaseConfig.SUPABASE_KEY}"
            )
        }
    }

    suspend fun updateTask(
        id: String,
        title: String,
        description: String,
        completed: Boolean
    ) {

        val task = InsertTaskDto(
            title = title,
            description = description,
            completed = completed
        )

        val response = try {

            client.patch(
                "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks?id=eq.$id"
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
                    "return=representation"
                )

                contentType(ContentType.Application.Json)

                setBody(task)
            }

        } catch (e: ResponseException) {

            val errorBody = e.response.bodyAsText()

            throw IllegalStateException(
                "Supabase respondió ${e.response.status.value} al actualizar id=$id. $errorBody",
                e
            )
        }

        val body = response.bodyAsText()

        println("UPDATE RESPONSE status=${response.status.value} id=$id body=$body")

        if (body.isBlank() || body.trim() == "[]") {

            throw IllegalStateException(
                "Supabase no actualizó ninguna fila (id=$id). " +
                    "¿La tarea todavía existe? Body=$body"
            )
        }
    }

}