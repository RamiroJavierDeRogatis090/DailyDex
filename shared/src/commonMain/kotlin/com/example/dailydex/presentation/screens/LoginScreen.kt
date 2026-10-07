package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dailydex.presentation.components.DailyDexLogo
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen() {

    val viewModel = ViewModelProvider.authViewModel

    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    var isSignUp by remember {
        mutableStateOf(false)
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            DailyDexLogo()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text =
                    if (isSignUp)
                        "Creá tu cuenta para empezar"
                    else
                        "Iniciá sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                label = {
                    Text("Email")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                singleLine = true,
                label = {
                    Text("Contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            uiState.info?.let {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    scope.launch {

                        if (isSignUp) {

                            viewModel.signUp(
                                email = email.trim(),
                                password = password
                            )

                        } else {

                            viewModel.signIn(
                                email = email.trim(),
                                password = password
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    email.isNotBlank() &&
                        password.isNotBlank() &&
                        !uiState.isLoading
            ) {

                if (uiState.isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {

                    Text(
                        if (isSignUp)
                            "Crear cuenta"
                        else
                            "Iniciar sesión"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {

                    isSignUp = !isSignUp

                    viewModel.clearMessages()
                },
                enabled = !uiState.isLoading
            ) {

                Text(
                    if (isSignUp)
                        "¿Ya tenés cuenta? Iniciá sesión"
                    else
                        "¿No tenés cuenta? Registrate"
                )
            }
        }
    }
}
