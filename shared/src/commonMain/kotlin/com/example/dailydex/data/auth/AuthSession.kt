package com.example.dailydex.data.auth

data class AuthSession(
    val accessToken: String,
    val refreshToken: String? = null,
    val userId: String? = null,
    val email: String? = null
) {
    val isUsable: Boolean
        get() = accessToken.isNotBlank()
}
