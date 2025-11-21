package week11.st292865.finalproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.*
import week11.st292865.finalproject.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel) {

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val success by viewModel.success.collectAsState()

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }

    // Show toast when success
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Reset link sent!", Toast.LENGTH_SHORT).show()
            viewModel.clearState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Forgot Password",
            fontStyle = FontStyle.Italic,
            style = AppTypography.headlineLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enter your email to reset your password",
            fontStyle = FontStyle.Italic,
            style = AppTypography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = TextGray) },
            placeholder = { Text("you@example.com", color = TextGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Loading Indicator
        if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryBlue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = ErrorRed,
                style = AppTypography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Send Reset Button
        Button(
            onClick = {
                if (email.isBlank()) {
                    viewModel.setError("Email cannot be empty.")
                } else {
                    viewModel.sendReset(email)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = AppShapes.large
        ) {
            Text(text = "Send Reset Link", style = AppTypography.labelLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Remember your password?", style = AppTypography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Login",
                color = PrimaryBlue,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Login.route)
                },
                style = AppTypography.bodyMedium
            )
        }
    }
}
