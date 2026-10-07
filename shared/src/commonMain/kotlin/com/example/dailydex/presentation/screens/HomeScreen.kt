package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dailydex.domain.model.Task
import com.example.dailydex.presentation.components.DailyDexLogo
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTaskClick: () -> Unit,
    onEditTaskClick: (String, String, String) -> Unit,
    onSignOut: () -> Unit
) {

    val viewModel = ViewModelProvider.homeViewModel

    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    val tasks = uiState.tasks

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(uiState.errorMessage) {

        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    var taskToDelete by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    DailyDexLogo(
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    TextButton(
                        onClick = onSignOut
                    ) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick
            ) {
                Text("＋  Nueva tarea")
            }
        }
    ) { padding ->

        when {

            uiState.isLoading && tasks.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            tasks.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "📋",
                            style = MaterialTheme.typography.displayMedium
                        )

                        Text(
                            text = "No tenés tareas todavía",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Tocá «Nueva tarea» para empezar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),

                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 96.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        SummaryCard(
                            total = tasks.size,
                            completed = tasks.count { it.completed }
                        )
                    }

                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->

                        TaskCard(
                            task = task,
                            modifier = Modifier.animateItem(),
                            onToggle = {
                                scope.launch {
                                    val error = viewModel.toggleTaskInSupabase(task.id)
                                    if (error != null) {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            },
                            onEdit = {
                                onEditTaskClick(
                                    task.id,
                                    task.title,
                                    task.description
                                )
                            },
                            onDelete = {
                                taskToDelete = task.id
                            }
                        )
                    }
                }
            }
        }
    }

    taskToDelete?.let { id ->

        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },
            title = {
                Text("Eliminar tarea")
            },
            text = {
                Text("¿Seguro que querés eliminar esta tarea?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                        scope.launch {
                            val error = viewModel.deleteTaskFromSupabase(id)
                            if (error != null) {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(
    total: Int,
    completed: Int
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "$completed de $total completadas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            LinearProgressIndicator(
                progress = {
                    if (total == 0) 1f
                    else completed.toFloat() / total
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    onToggle()
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration =
                        if (task.completed)
                            TextDecoration.LineThrough
                        else
                            null,
                    color =
                        if (task.completed)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurface
                )

                if (task.description.isNotBlank()) {

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (task.createdAt.isNotBlank()) {

                    Text(
                        text = "📅 ${task.createdAt.take(10)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onEdit
            ) {
                Text("✏️")
            }

            IconButton(
                onClick = onDelete
            ) {
                Text("🗑️")
            }
        }
    }
}
