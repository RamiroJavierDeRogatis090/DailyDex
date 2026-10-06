package com.example.dailydex.data.supabase

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText

class SupabaseTaskRepository {

    private val client = HttpClient(CIO)

    suspend fun getTasks(): String {

        println("URL:")
        println("${SupabaseConfig.SUPABASE_URL}/rest/v1/tasks")

        return client.get(
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
    }

    }
