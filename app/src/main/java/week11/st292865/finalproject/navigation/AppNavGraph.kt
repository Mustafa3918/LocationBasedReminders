package week11.st292865.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st292865.finalproject.ui.screens.LoginScreen
import week11.st292865.finalproject.ui.screens.RegisterScreen
import week11.st292865.finalproject.ui.screens.ForgotPasswordScreen
import week11.st292865.finalproject.ui.screens.SettingsScreen
import week11.st292865.finalproject.ui.screens.HomeScreen
import week11.st292865.finalproject.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
