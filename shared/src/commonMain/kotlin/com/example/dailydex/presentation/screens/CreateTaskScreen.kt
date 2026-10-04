package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@Composable
fun CreateTaskScreen(
    onBack: () -> Unit
) {

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
            text = "Nueva Tarea",
            style = MaterialTheme.typography.headlineMedium
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

        Button(
            onClick = {
                onBack()
            }
        ) {
            Text("Guardar")
        }

        Button(
            onClick = {
                onBack()
            }
        ) {
            Text("Volver")
        }
    }
}