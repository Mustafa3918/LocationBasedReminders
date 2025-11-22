package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.TextGray
import week11.st292865.finalproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {

    val completedTasksState = taskViewModel.completedTasks.collectAsState()
    val errorState = taskViewModel.error.collectAsState()

    // Start observing when screen appears
    LaunchedEffect(Unit) {
        taskViewModel.loadCompletedTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Task History", style = AppTypography.headlineMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            // Optional error
            errorState.value?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (completedTasksState.value.isEmpty()) {
                Text(
                    text = "No completed tasks yet.",
                    color = TextGray,
                    style = AppTypography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(completedTasksState.value) { task ->
                        CompletedTaskListItem(task)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedTaskListItem(task: TaskModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(task.title, style = AppTypography.bodyLarge)
            if (task.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(task.note, style = AppTypography.bodyMedium, color = TextGray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Completed",
                style = AppTypography.bodySmall,
                color = TextGray
            )
        }
    }
}
