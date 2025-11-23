package week11.st292865.finalproject.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import week11.st292865.finalproject.location.LocationService
import week11.st292865.finalproject.location.rememberLocationAndNotificationPermissionState
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(navController: NavController) {

    val permissionState = rememberLocationAndNotificationPermissionState()

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
                                "Linked Lists - Location Based Reminder App",
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
                Divider(color = TextBlack, thickness = 1.dp)
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO (UC3 later): navigate to AddTask screen
                    // navController.navigate(Screen.AddTask.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
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

        // -------- Main Layout (Scheme A) --------
        val context = androidx.compose.ui.platform.LocalContext.current
        val locationService = remember { LocationService(context) }

        var myLocation by remember { mutableStateOf<LatLng?>(null) }
        var pickedLocation by remember { mutableStateOf<LatLng?>(null) }

        // get last known location once
        LaunchedEffect(Unit) {
            myLocation = locationService.getLastKnownLatLng()
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                myLocation ?: LatLng(43.6532, -79.3832), // default Toronto
                13f
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            // --- Section Title ---
            Text(
                text = "Google Map Interface",
                style = AppTypography.bodyLarge
            )
            Spacer(modifier = Modifier.height(6.dp))

            // --- Small Map Area ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),   // 👈 small map like your figma
                shape = MaterialTheme.shapes.medium
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    ),
                    onMapLongClick = { latLng ->
                        pickedLocation = latLng
                        // TODO (UC3 later): go to AddTask with coords
                        // navController.navigate(Screen.AddTask.route + "?lat=${latLng.latitude}&lng=${latLng.longitude}")
                    }
                ) {
                    // temporary marker when user long-presses
                    pickedLocation?.let { pos ->
                        Marker(
                            state = MarkerState(pos),
                            title = "Picked Location"
                        )
                    }

                    // TODO (UC4 later):
                    // activeTasks.forEach { task ->
                    //   Marker(...)
                    //   Circle(...)
                    // }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Task List Section ---
            Text(
                text = "Task List",
                style = AppTypography.bodyLarge
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),   // list takes remaining space
                shape = MaterialTheme.shapes.medium
            ) {
                // Placeholder list for now
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "(Tasks near your current location will appear here.)",
                        style = AppTypography.bodyMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    // TODO (UC4 later):
                    // LazyColumn {
                    //   items(activeTasks) { task -> TaskRow(task) }
                    // }
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
