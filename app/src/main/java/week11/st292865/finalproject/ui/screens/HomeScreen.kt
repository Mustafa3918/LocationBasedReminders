package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.BackgroundWhite
import week11.st292865.finalproject.ui.theme.ErrorRed
import week11.st292865.finalproject.ui.theme.PrimaryBlue
import week11.st292865.finalproject.ui.theme.TextBlack
import week11.st292865.finalproject.ui.theme.TextGray
import week11.st292865.finalproject.viewmodel.TaskViewModel
import androidx.compose.material3.FloatingActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {

    Scaffold(
        topBar = {
            Column {

                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Linked Lists - Location-Based Reminders App",
                                style = AppTypography.headlineMedium
                            )
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .clickable { navController.navigate(Screen.Settings.route) }
                        )
                    }
                )

                HorizontalDivider(
                    color = TextBlack,
                    thickness = 1.dp
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            //Collect state from TaskViewModel
            val tasksState = taskViewModel.activeTasks.collectAsState()
            val isLoadingState = taskViewModel.isLoading.collectAsState()
            val errorState = taskViewModel.error.collectAsState()

            //Buffering indicator
            if (isLoadingState.value) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            //Error message
            errorState.value?.let {err->
                Text(
                    text = err,
                    color = ErrorRed,
                    style = AppTypography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            //Task list
            if (tasksState.value.isEmpty()) {
                Text(
                    text = "No active tasks currently.\nMake sure to add one!",
                    style = AppTypography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tasksState.value) {task ->
                        TaskListItem(
                            task = task,
                            onMarkComplete = {
                                task.id?.let {id -> taskViewModel.markTaskComplete(id)}
                            },
                            onDelete = {
                                task.id?.let{id -> taskViewModel.deleteTask(id)}
                            },
                            onClick = {
                                //will have to come back
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    floatingActionButton = {
        FloatingActionButton(
            onClick = {navController.navigate(Screen.TaskEditor.route)}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }
    }
}

@Composable
fun TaskListItem(
    task: TaskModel,
    onMarkComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable{onClick()},
        color = BackgroundWhite,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title.ifBlank {"(Untitled Task)"},
                    style = AppTypography.bodyLarge
                )
                if (task.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.note,
                        style = AppTypography.bodySmall,
                        color = TextGray
                    )
                }
            }

            //Mark complete
            IconButton(onClick = onMarkComplete) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mark complete",
                    tint = PrimaryBlue
                )
            }

            //Delete
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = ErrorRed
                )
            }
        }
    }
}
