package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.PrimaryBlue
import week11.st292865.finalproject.viewmodel.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen (
    navController: NavController,
    taskViewModel: TaskViewModel,
    existingTask: TaskModel? = null
) {
    var title by remember {mutableStateOf(existingTask?.title ?: "")}
    var note by remember {mutableStateOf(existingTask?.note ?: "")}
    var radius by remember {mutableStateOf(existingTask?.radiusMeters?.toFloat() ?: 200f)}

    Scaffold(
       topBar = {
           TopAppBar(
               title = {
                       Text(
                           if (existingTask == null) "New Task"
                           else "Edit Task",
                           style = AppTypography.headlineMedium
                       )
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
            OutlinedTextField(
                value = title,
                onValueChange = {title = it},
                label = {Text("Task Title")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = {note=it},
                label = {Text ("Notes (optional)")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text("Radius: ${radius.toInt()} meters", style = AppTypography.bodyMedium)

            Slider(
                value = radius,
                onValueChange = {radius = it},
                valueRange = 100f..500f,
                steps = 3
            )

            Spacer(Modifier.height(32.dp))

            //Placeholder for location selection
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Select Location")
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (existingTask == null){
                        taskViewModel.addTask(
                            TaskModel(
                                title = title,
                                note = note,
                                radiusMeters = radius.toInt()
                            )
                        )
                    } else {
                        taskViewModel.updateTask(
                            existingTask.copy(
                                title = title,
                                note = note,
                                radiusMeters = radius.toInt()
                            )
                        )
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text(
                    if (existingTask == null) "Create Task" else "Save Changes",
                    style = AppTypography.labelLarge
                )
            }
        }
    }
}