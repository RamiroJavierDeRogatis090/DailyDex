package com.example.dailydex.presentation.viewmodel

import com.example.dailydex.data.auth.AuthManager
import com.example.dailydex.data.auth.SupabaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel {

    private val repository = SupabaseAuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())

    val uiState = _uiState.asStateFlow()

    fun clearMessages() {

        _uiState.value = _uiState.value.copy(
            error = null,
            info = null
        )
    }

    suspend fun restoreSession() {

        val stored = AuthManager.restore()

        if (stored == null || !stored.isUsable) {

            _uiState.value = AuthUiState(
                isCheckingSession = false
            )

            return
        }

        val refreshed = try {

            AuthManager.set(
                repository.refresh(stored.refreshToken ?: "")
            )

            AuthManager.restore()

        } catch (_: Exception) {

            stored
        }

        _uiState.value = AuthUiState(
            isCheckingSession = false,
            isAuthenticated = true,
            email = refreshed?.email
        )
    }

    suspend fun signIn(
        email: String,
        password: String
    ) {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            info = null
        )

        try {

            val session = repository.signIn(
                email = email,
                password = password
            )

            AuthManager.set(session)

            _uiState.value = AuthUiState(
                isCheckingSession = false,
                isAuthenticated = true,
                email = session.email
            )

        } catch (e: Exception) {

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "No se pudo iniciar sesión."
            )
        }
    }

    suspend fun signUp(
        email: String,
        password: String
    ) {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            info = null
        )

        try {

            val session = repository.signUp(
                email = email,
                password = password
            )

            if (!session.isUsable) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    info = "Te enviamos un correo. Confirmá tu cuenta para iniciar sesión."
                )

                return
            }

            AuthManager.set(session)

            _uiState.value = AuthUiState(
                isCheckingSession = false,
                isAuthenticated = true,
                email = session.email
            )

        } catch (e: Exception) {

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "No se pudo crear la cuenta."
            )
        }
    }

    suspend fun signOut() {

        repository.signOut(AuthManager.accessToken)

        AuthManager.clear()

        _uiState.value = AuthUiState(
            isCheckingSession = false
        )
    }
}
