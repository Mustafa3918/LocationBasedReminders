package week11.st292865.finalproject.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.location.LocationService
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.PrimaryBlue
import week11.st292865.finalproject.viewmodel.SettingsViewModel
import week11.st292865.finalproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TaskEditorScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    existingTask: TaskModel? = null,
    // Optional preset coordinates if we navigate here from Home map long-press
    presetLat: Double? = null,
    presetLng: Double? = null
) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()

    // Settings ViewModel provides the default radius for NEW tasks
    val settingsViewModel: SettingsViewModel = viewModel()
    val userState by settingsViewModel.user.collectAsState()

    // Load user settings on first entry
    LaunchedEffect(Unit) { settingsViewModel.loadUser() }

    // Fallback to 200m if Firestore user doc is missing or radius is invalid
    val defaultRadius = userState.defaultRadius.takeIf { it > 0 } ?: 200

    // Form states
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var note by remember { mutableStateOf(existingTask?.note ?: "") }

    // Radius: use task radius when editing, otherwise use user default radius
    var radius by remember {
        mutableStateOf(
            existingTask?.radiusMeters?.toFloat()
                ?: defaultRadius.toFloat()
        )
    }

    // Picked location:
    // 1) Use existing task location if editing
    // 2) Use preset location if passed from Home
    // 3) Otherwise start as null until user picks a spot
    var pickedLocation by remember {
        mutableStateOf(
            when {
                existingTask?.latitude != null && existingTask.longitude != null ->
                    LatLng(existingTask.latitude!!, existingTask.longitude!!)
                presetLat != null && presetLng != null ->
                    LatLng(presetLat, presetLng)
                else -> null
            }
        )
    }

    // Camera state for the embedded picker map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            pickedLocation ?: LatLng(43.6532, -79.3832), // Default Toronto
            13f
        )
    }

    // Whenever pickedLocation changes, zoom the camera to that point
    LaunchedEffect(pickedLocation) {
        pickedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (existingTask == null) "New Task" else "Edit Task",
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

            // Task title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Optional task note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Radius selector
            Text("Radius: ${radius.toInt()} meters", style = AppTypography.bodyMedium)
            Slider(
                value = radius,
                onValueChange = { radius = it },
                valueRange = 100f..500f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Location picker map
            Text("Pick a Location", style = AppTypography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    ),
                    // Long-press to select a location
                    onMapLongClick = { latLng ->
                        pickedLocation = latLng
                    }
                ) {
                    // Show marker + radius circle for the selected location
                    pickedLocation?.let { pos ->
                        Marker(state = MarkerState(pos), title = "Task Location")
                        Circle(center = pos, radius = radius.toDouble())
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Use device current location as the task location
            Button(
                onClick = {
                    scope.launch {
                        val loc = locationService.getFreshLatLng()
                        if (loc != null) {
                            pickedLocation = loc
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Use Current Location")
            }

            Spacer(Modifier.height(24.dp))

            // Save/Create task
            Button(
                onClick = {
                    val loc = pickedLocation

                    if (existingTask == null) {
                        // Create new task
                        taskViewModel.addTask(
                            TaskModel(
                                title = title,
                                note = note,
                                latitude = loc?.latitude,
                                longitude = loc?.longitude,
                                radiusMeters = radius.toInt()
                            )
                        )
                    } else {
                        // Update existing task
                        taskViewModel.updateTask(
                            existingTask.copy(
                                title = title,
                                note = note,
                                latitude = loc?.latitude,
                                longitude = loc?.longitude,
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
