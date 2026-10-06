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
fun CreateTaskScreen(
    onBack: () -> Unit
) {

    val viewModel = ViewModelProvider.homeViewModel

    val scope = rememberCoroutineScope()

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Nueva tarea",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Añade una nueva actividad a tu lista",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            singleLine = true,
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
            minLines = 2,
            maxLines = 3,
            label = {
                Text("Descripción")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {

                    if (
                        title.isNotBlank() &&
                        description.isNotBlank()
                    ) {

                        scope.launch {

                            viewModel.addTaskToSupabase(
                                title = title,
                                description = description
                            )

                            onBack()
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
                Text("⬅ Volver")
            }
        }
    }
}