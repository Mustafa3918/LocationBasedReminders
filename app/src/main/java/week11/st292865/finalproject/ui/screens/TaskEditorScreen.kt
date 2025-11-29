package week11.st292865.finalproject.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import week11.st292865.finalproject.ui.theme.BackgroundWhite
import week11.st292865.finalproject.ui.theme.PrimaryBlue
import week11.st292865.finalproject.ui.theme.TextBlack
import week11.st292865.finalproject.viewmodel.SettingsViewModel
import week11.st292865.finalproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TaskEditorScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    existingTask: TaskModel? = null,
    settingsViewModel: SettingsViewModel,
    presetLat: Double? = null,
    presetLng: Double? = null
) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()

    val userState by settingsViewModel.user.collectAsState()

    // Load user preferences (including defaultRadius)
    LaunchedEffect(Unit) {
        settingsViewModel.loadUser()
    }

    var radius by remember { mutableStateOf(200f) }

    LaunchedEffect(userState, existingTask) {
        radius = when {
            existingTask != null ->
                existingTask.radiusMeters?.toFloat() ?: 200f

            userState.defaultRadius > 0 ->
                userState.defaultRadius.toFloat()

            else -> 200f
        }
    }

    // LOCATION SELECTION
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            pickedLocation ?: LatLng(43.6532, -79.3832),
            13f
        )
    }

    LaunchedEffect(pickedLocation) {
        pickedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
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
                                text = if (existingTask == null) "New Task" else "Edit Task",
                                style = AppTypography.headlineMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextBlack
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundWhite
                    )
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = TextBlack
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            var title by remember { mutableStateOf(existingTask?.title ?: "") }
            var note by remember { mutableStateOf(existingTask?.note ?: "") }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Text("Radius: ${radius.toInt()} meters", style = AppTypography.bodyMedium)
            Slider(
                value = radius,
                onValueChange = { radius = it },
                valueRange = 100f..500f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

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
                    onMapLongClick = { latLng ->
                        pickedLocation = latLng
                    }
                ) {
                    pickedLocation?.let { pos ->
                        Marker(state = MarkerState(pos), title = "Task Location")
                        Circle(center = pos, radius = radius.toDouble())
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

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

            Button(
                onClick = {
                    val loc = pickedLocation

                    if (existingTask == null) {
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
