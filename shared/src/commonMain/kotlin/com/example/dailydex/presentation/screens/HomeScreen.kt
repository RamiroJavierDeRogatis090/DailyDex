package com.example.dailydex.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailydex.presentation.viewmodel.ViewModelProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
//import com.example.dailydex.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onAddTaskClick: () -> Unit
) {

    val viewModel = ViewModelProvider.homeViewModel
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

                Button(
                    onClick = onAddTaskClick
                ) {
                    Text("➕ Nueva tarea")
                }
            }

            items(tasks) { task ->

                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}