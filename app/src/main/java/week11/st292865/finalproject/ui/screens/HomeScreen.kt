package week11.st292865.finalproject.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.location.GeofenceManager
import week11.st292865.finalproject.location.GeofenceTask
import week11.st292865.finalproject.location.LocationService
import week11.st292865.finalproject.location.LocationUpdates
import week11.st292865.finalproject.location.rememberLocationAndNotificationPermissionState
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.BackgroundWhite
import week11.st292865.finalproject.ui.theme.ErrorRed
import week11.st292865.finalproject.ui.theme.PrimaryBlue
import week11.st292865.finalproject.ui.theme.TextBlack
import week11.st292865.finalproject.ui.theme.TextGray
import week11.st292865.finalproject.viewmodel.SettingsViewModel
import week11.st292865.finalproject.viewmodel.TaskViewModel



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel
) {
    // Start observing tasks once user reaches Home (after auth)
    LaunchedEffect(Unit) {
        taskViewModel.startObservingTasks()
    }

    // ---- Settings VM (default radius source) ----
    val settingsViewModel: SettingsViewModel = viewModel()
    val userState by settingsViewModel.user.collectAsState()


    LaunchedEffect(Unit) {
        settingsViewModel.loadUser()
    }

    // Future use for tasks / geofences if needed
    val defaultRadius = userState.defaultRadius

    // ---- Permissions (location + notifications) ----
    val permissionState = rememberLocationAndNotificationPermissionState()

    LaunchedEffect(Unit) {
        if (!permissionState.allGranted) {
            permissionState.requestPermissions()
        }
    }


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
                                "Linked Lists",
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
                        TextButton(
                            onClick = {navController.navigate(Screen.History.route)}
                        ) {
                            Text(
                                text = "History",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = AppTypography.bodyLarge
                            )
                        }
                    }
                )
                Divider(color = TextBlack, thickness = 1.dp)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.TaskEditor.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { padding ->

        // -------- Permission Gate (UC7) --------
        if (!permissionState.allGranted) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Location and Notification permissions are required.",
                    style = AppTypography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Please grant permissions to continue.",
                    style = AppTypography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = permissionState.requestPermissions) {
                    Text("Grant Permissions")
                }
            }
            return@Scaffold
        }

        // -------- Main Layout --------
        val context = LocalContext.current
        val locationService = remember { LocationService(context) }
        val updates = remember { LocationUpdates(context) }

        var myLocation by remember { mutableStateOf<LatLng?>(null) }
        var pickedLocation by remember { mutableStateOf<LatLng?>(null) }
        var firstFix by remember { mutableStateOf(true) }

        val geofenceManager = remember { GeofenceManager(context) }


        // 1) define cameraPositionState (default Toronto)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(43.6532, -79.3832),
                13f
            )
        }

        // 2) pull fresh location (init)
        LaunchedEffect(Unit) {
            myLocation = locationService.getFreshLatLng()
        }

        // 3) keep receiving location updates
        LaunchedEffect(Unit) {
            updates.locationFlow().collect { latLng ->
                myLocation = latLng
            }
        }

        // 4) move camera when first location fix arrives
        LaunchedEffect(myLocation) {
            if (firstFix && myLocation != null) {
                firstFix = false
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(myLocation!!, 14f)
                )
            }
        }

        fun openAppSettings(context: Context) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        val hasBackgroundLocation =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else true

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (!hasBackgroundLocation) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "Background Location Required",
                            style = AppTypography.bodyLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "To receive location reminders reliably, please set Location permission to 'Allow all the time'.",
                            style = AppTypography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { openAppSettings(context) }) {
                            Text("Open Settings")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Welcome, ${userState.displayName}",
                style = AppTypography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            // --- Collect state from TaskViewModel (UC3/UC4/UC6) ---
            val tasksState = taskViewModel.activeTasks.collectAsState()
            val isLoadingState = taskViewModel.isLoading.collectAsState()
            val errorState = taskViewModel.error.collectAsState()

            val activeTasksWithLocation = tasksState.value.filter { t ->
                t.latitude != null && t.longitude != null && t.id != null && !t.isComplete
            }

            val geofenceTasks = activeTasksWithLocation.map { t ->
                val radius = if (t.radiusMeters > 0) t.radiusMeters else defaultRadius

                GeofenceTask(
                    id = t.id!!,
                    title = t.title,
                    latLng = LatLng(t.latitude!!, t.longitude!!),
                    radiusMeters = radius
                )
            }

            // UC5: register/update geofences whenever active tasks change
            LaunchedEffect(geofenceTasks, hasBackgroundLocation) {
                if (hasBackgroundLocation) {
                    geofenceManager.register(geofenceTasks)
                } else {
                    // optional: remove any fences to avoid stale registration
                    geofenceManager.remove(geofenceTasks.map { it.id })
                }
            }


            // --- Map Section (UC4 Map + UC5/UC7 support) ---
            Text(
                text = "Google Map Interface",
                style = AppTypography.bodyLarge
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    ),
                    onMapLongClick = { latLng ->
                        pickedLocation = latLng
                    }
                ) {
                    pickedLocation?.let { pos ->
                        Marker(state = MarkerState(pos), title = "Picked Location")
                    }

                    // UC4: show task markers + geofence circles
                    geofenceTasks.forEach { gt ->
                        Marker(state = MarkerState(gt.latLng), title = gt.title)
                        Circle(center = gt.latLng, radius = gt.radiusMeters.toDouble())
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buffering indicator
            if (isLoadingState.value) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Error message
            errorState.value?.let { err ->
                Text(
                    text = err,
                    color = ErrorRed,
                    style = AppTypography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- Task List Section ---
            Text(
                text = "Task List",
                style = AppTypography.bodyLarge
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {navController.navigate(Screen.History.route)}) {
                    Text("View History")
                }
            }


            if (tasksState.value.isEmpty()) {
                Text(
                    text = "No active tasks currently.\nMake sure to add one!",
                    style = AppTypography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(tasksState.value) { task ->
                        TaskListItem(
                            task = task,
                            onMarkComplete = {
                                task.id?.let { id -> taskViewModel.markTaskComplete(id) }
                            },
                            onDelete = {
                                task.id?.let { id -> taskViewModel.deleteTask(id) }
                            },
                            onClick = {
                                task.id?.let {id -> navController.navigate("${Screen.TaskEditor.route}?taskId=$id")}
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Use the + button to add a reminder for any location.",
                style = AppTypography.bodyMedium
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
            .clickable { onClick() },
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
                    text = task.title.ifBlank { "(Untitled Task)" },
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

            // Mark complete
            IconButton(onClick = onMarkComplete) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mark complete",
                    tint = PrimaryBlue
                )
            }

            // Delete
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
