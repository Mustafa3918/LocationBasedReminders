package week11.st292865.finalproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st292865.finalproject.navigation.Screen
import week11.st292865.finalproject.ui.theme.*
import week11.st292865.finalproject.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val errorMessage by viewModel.errorMessage.collectAsState()
    val success by viewModel.success.collectAsState()

    // Navigate when login successful
    LaunchedEffect(success) {
        if (success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.clearState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "Welcome Back",
            style = AppTypography.headlineLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Sign in to continue",
            style = AppTypography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = ErrorRed,
                style = AppTypography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = TextGray) },
            placeholder = { Text("you@example.com", color = TextGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = TextGray) },
            placeholder = { Text("••••••••") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        Text(
            text = "Forgot Password?",
            color = PrimaryBlue,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController.navigate(Screen.ForgotPassword.route)
                },
            style = AppTypography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    // Prevent blank submissions
                    viewModel.clearState()
                    viewModel.setError("Email and password cannot be empty.")

                } else {
                    viewModel.login(email, password)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = AppShapes.large
        ) {
            Text("Login", style = AppTypography.labelLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Link
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Don’t have an account?", style = AppTypography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Register",
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
    }
}
