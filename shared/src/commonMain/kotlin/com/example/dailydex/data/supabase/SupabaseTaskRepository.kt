package com.example.dailydex.data.supabase

import com.example.dailydex.data.auth.AuthManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SupabaseTaskRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient {

        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }
    }

    private fun authHeader(): String =
        "Bearer ${AuthManager.accessToken ?: SupabaseConfig.SUPABASE_KEY}"

    suspend fun getTasks(): List<TaskDto> {

        val response = client.get(
            "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks"
        ) {

            header("apikey", SupabaseConfig.SUPABASE_KEY)
            header("Authorization", authHeader())

        }.bodyAsText()

        return json.decodeFromString(
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

        try {

            client.post(
                "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks"
            ) {

                header("apikey", SupabaseConfig.SUPABASE_KEY)
                header("Authorization", authHeader())
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(task)
            }

        } catch (e: ResponseException) {

            throw IllegalStateException(e.readError(), e)
        }
    }

    suspend fun deleteTask(
        id: String
    ) {

        try {

            client.delete(
                "${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks?id=eq.$id"
            ) {

                header("apikey", SupabaseConfig.SUPABASE_KEY)
                header("Authorization", authHeader())
            }

        } catch (e: ResponseException) {

            throw IllegalStateException(e.readError(), e)
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

                header("apikey", SupabaseConfig.SUPABASE_KEY)
                header("Authorization", authHeader())
                header("Prefer", "return=representation")
                contentType(ContentType.Application.Json)
                setBody(task)
            }

        } catch (e: ResponseException) {

            throw IllegalStateException(e.readError(), e)
        }

        val body = response.bodyAsText()

        if (body.isBlank() || body.trim() == "[]") {

            throw IllegalStateException(
                "Supabase no actualizó ninguna fila (id=$id)."
            )
        }
    }

    private suspend fun ResponseException.readError(): String {

        val body = try {
            response.bodyAsText()
        } catch (_: Exception) {
            ""
        }

        val message = try {
            json.parseToJsonElement(body)
                .jsonObject["message"]
                ?.jsonPrimitive
                ?.content
        } catch (_: Exception) {
            null
        }

        return message
            ?.takeIf { it.isNotBlank() }
            ?: "Supabase respondió ${response.status.value}"
    }
}
