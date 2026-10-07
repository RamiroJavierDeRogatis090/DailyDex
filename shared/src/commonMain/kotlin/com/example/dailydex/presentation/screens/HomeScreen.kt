package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAddTaskClick: () -> Unit,
    onEditTaskClick: (String, String, String) -> Unit
) {

    val viewModel = ViewModelProvider.homeViewModel

    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    val tasks = uiState.tasks

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("DailyDex")
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text =
                            if (tasks.size == 1)
                                "1 tarea"
                            else
                                "${tasks.size} tareas",

                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = onAddTaskClick
                    ) {
                        Text("➕ Nueva tarea")
                    }
                }
            }

            if (tasks.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "📋 No tienes tareas"
                            )

                            Text(
                                text = "Presiona Nueva tarea para comenzar"
                            )
                        }
                    }
                }
            }

            items(
                items = tasks,
                key = { it.id }
            ) { task ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "📅 ${task.createdAt}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text =
                                if (task.completed)
                                    "✅ Completada"
                                else
                                    "⏳ Pendiente",

                            color =
                                if (task.completed)
                                    Color(0xFF2E7D32)
                                else
                                    Color(0xFFE65100)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {

                                    scope.launch {

                                        viewModel.toggleTaskInSupabase(
                                            task.id
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    if (task.completed)
                                        "Desmarcar"
                                    else
                                        "Completar"
                                )
                            }

                            Button(
                                onClick = {

                                    onEditTaskClick(
                                        task.id,
                                        task.title,
                                        task.description
                                    )
                                }
                            ) {
                                Text("Editar")
                            }

                            Button(
                                onClick = {

                                    scope.launch {

                                        viewModel.deleteTaskFromSupabase(
                                            task.id
                                        )
                                    }
                                }
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}