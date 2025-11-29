package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.*
import week11.st292865.finalproject.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {

    val userState by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var displayName by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf(200f) }

    // Load user on first entry
    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    // Updates UI when userState changes
    LaunchedEffect(userState) {
        displayName = userState.displayName
        radius = userState.defaultRadius.toFloat()
    }

    LaunchedEffect(success) {
        if (success) {
            viewModel.loadUser()
            viewModel.clearSuccess()
        }
    }

    Column {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings", style = AppTypography.headlineMedium)
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

            }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = TextBlack
        )

        Scaffold(
            containerColor = BackgroundWhite
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {

                // -------- PROFILE INFO ----------
                Text("Profile Info", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Text("Current: ${userState.displayName}", style = AppTypography.bodyMedium)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        if (it.isNotBlank()) displayNameError = null
                    },
                    label = { Text("Display Name", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = displayNameError != null
                )

                if (displayNameError != null) {
                    Text(
                        text = displayNameError!!,
                        color = ErrorRed,
                        style = AppTypography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Email — Read-only field
                OutlinedTextField(
                    value = viewModel.getCurrentUserEmail(),
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email", color = TextGray) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = TextGray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = TextGray,
                        disabledIndicatorColor = TextGray.copy(alpha = 0.4f),
                        disabledLabelColor = TextGray,
                        disabledContainerColor = BackgroundWhite,
                        disabledLeadingIconColor = TextGray,
                        disabledTrailingIconColor = TextGray
                    )
                )

                Spacer(Modifier.height(32.dp))

                // -------- LOCATION SETTINGS ----------
                Text("Location Settings", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Default Radius", style = AppTypography.bodyMedium)
                    Text("${radius.toInt()}m", fontWeight = FontWeight.SemiBold)
                }

                Slider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 100f..500f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        if (displayName.isBlank()) {
                            displayNameError = "Display name cannot be empty."
                            return@Button
                        }

                        viewModel.saveSettings(displayName, radius.toInt())
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Settings.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = AppShapes.large
                ) {
                    Text("Save Changes", style = AppTypography.labelLarge)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Loading bar appears AFTER clicking Save
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Error message
                if (error != null) {
                    Text(
                        text = error!!,
                        color = ErrorRed,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // LOGOUT
                Text(
                    "Logout",
                    color = ErrorRed,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                    style = AppTypography.bodyMedium
                )
            }
        }
    }
}
