package com.example.dailydex.presentation.viewmodel

data class AuthUiState(
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val email: String? = null,
    val error: String? = null,
    val info: String? = null
)
