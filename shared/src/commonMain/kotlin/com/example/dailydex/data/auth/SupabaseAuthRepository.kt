package com.example.dailydex.data.auth

import com.example.dailydex.data.supabase.SupabaseConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SupabaseAuthRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient {

        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ): AuthSession {

        val response = client.post(
            "${SupabaseConfig.SUPABASE_URL}/auth/v1/token?grant_type=password"
        ) {

            header("apikey", SupabaseConfig.SUPABASE_KEY)

            contentType(ContentType.Application.Json)

            setBody(AuthCredentialsDto(email = email, password = password))
        }

        return handleSession(response)
    }

    suspend fun signUp(
        email: String,
        password: String
    ): AuthSession {

        val response = client.post(
            "${SupabaseConfig.SUPABASE_URL}/auth/v1/signup"
        ) {

            header("apikey", SupabaseConfig.SUPABASE_KEY)

            contentType(ContentType.Application.Json)

            setBody(AuthCredentialsDto(email = email, password = password))
        }

        return handleSession(response)
    }

    suspend fun refresh(
        refreshToken: String
    ): AuthSession {

        val response = client.post(
            "${SupabaseConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token"
        ) {

            header("apikey", SupabaseConfig.SUPABASE_KEY)

            contentType(ContentType.Application.Json)

            setBody(RefreshTokenDto(refresh_token = refreshToken))
        }

        return handleSession(response)
    }

    suspend fun signOut(
        accessToken: String?
    ) {

        try {

            client.post(
                "${SupabaseConfig.SUPABASE_URL}/auth/v1/logout"
            ) {

                header("apikey", SupabaseConfig.SUPABASE_KEY)

                if (!accessToken.isNullOrBlank()) {
                    header("Authorization", "Bearer $accessToken")
                }
            }

        } catch (_: Exception) {

        }
    }

    private suspend fun handleSession(
        response: HttpResponse
    ): AuthSession {

        val body = response.bodyAsText()

        if (response.status.value !in 200..299) {
            throw IllegalStateException(parseError(body))
        }

        val dto = json.decodeFromString(
            AuthSessionDto.serializer(),
            body
        )

        return AuthSession(
            accessToken = dto.access_token ?: "",
            refreshToken = dto.refresh_token,
            userId = dto.user?.id,
            email = dto.user?.email
        )
    }

    private fun parseError(
        body: String
    ): String {

        val dto = try {

            json.decodeFromString(
                AuthErrorDto.serializer(),
                body
            )

        } catch (_: Exception) {

            null
        }

        val code = dto?.error_code
            ?: dto?.error
            ?: dto?.code?.toString()

        return when {

            code?.contains("invalid_credentials") == true ->
                "Email o contraseña incorrectos."

            code?.contains("user_already_exists") == true ->
                "Ya existe una cuenta con ese email."

            code?.contains("email_not_confirmed") == true ->
                "Confirmá tu email antes de iniciar sesión."

            code?.contains("weak_password") == true ->
                "La contraseña es muy débil (mínimo 6 caracteres)."

            code?.contains("over_email_send_rate_limit") == true ||
                code?.contains("over_request_rate_limit") == true ->
                "Demasiados intentos. Esperá un momento e intentá de nuevo."

            else ->
                dto?.msg
                    ?: dto?.message
                    ?: dto?.error_description
                    ?: "Ocurrió un error. Intentá de nuevo."
        }
    }
}
