package com.example.dailydex.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentialsDto(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenDto(
    val refresh_token: String
)

@Serializable
data class AuthUserDto(
    val id: String? = null,
    val email: String? = null
)

@Serializable
data class AuthSessionDto(
    val access_token: String? = null,
    val refresh_token: String? = null,
    val token_type: String? = null,
    val expires_in: Long? = null,
    val user: AuthUserDto? = null
)

@Serializable
data class AuthErrorDto(
    val error: String? = null,
    val error_description: String? = null,
    val error_code: String? = null,
    val msg: String? = null,
    val message: String? = null,
    val code: Int? = null
)
