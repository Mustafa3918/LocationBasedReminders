package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.AppTypography
import week11.st292865.finalproject.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

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

                Divider(
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

            Text(
                text = "Home Screen Placeholder",
                style = AppTypography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "[ Map + Task List finna go here ]",
                style = AppTypography.bodyMedium
            )
        }
    }
}
