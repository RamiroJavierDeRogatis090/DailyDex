package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun EditTaskScreen(
    taskId: String,
    currentTitle: String,
    currentDescription: String,
    onBack: () -> Unit
) {

    val viewModel = ViewModelProvider.homeViewModel

    val scope = rememberCoroutineScope()

    var title by remember {
        mutableStateOf(currentTitle)
    }

    var description by remember {
        mutableStateOf(currentDescription)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Editar tarea",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Título")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("Descripción")
            },
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {

                    scope.launch {

                        val errorMessage = viewModel.updateTaskInSupabase(
                            id = taskId,
                            title = title,
                            description = description
                        )

                        if (errorMessage == null) {
                            onBack()
                        } else {
                            error = "No se pudo guardar: $errorMessage"
                        }
                    }
                }
            ) {
                Text("💾 Guardar")
            }

            Button(
                onClick = {
                    onBack()
                }
            ) {
                Text("⬅ Cancelar")
            }
        }
    }
}