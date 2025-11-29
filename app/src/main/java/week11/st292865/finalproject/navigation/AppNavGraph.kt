package week11.st292865.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import week11.st292865.finalproject.ui.screens.LoginScreen
import week11.st292865.finalproject.ui.screens.RegisterScreen
import week11.st292865.finalproject.ui.screens.ForgotPasswordScreen
import week11.st292865.finalproject.ui.screens.SettingsScreen
import week11.st292865.finalproject.ui.screens.HomeScreen
import week11.st292865.finalproject.ui.screens.TaskEditorScreen
import week11.st292865.finalproject.viewmodel.AuthViewModel
import week11.st292865.finalproject.viewmodel.TaskViewModel
import week11.st292865.finalproject.ui.screens.HistoryScreen
import week11.st292865.finalproject.viewmodel.SettingsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel
) {

    // Observe login state for login persistance
    val isLoggedIn = authViewModel.isUserLoggedIn.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
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
            HomeScreen(
                navController = navController,
                taskViewModel = taskViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                settingsViewModel = viewModel(),
                authViewModel = authViewModel
            )
        }

        composable(
            route = "${Screen.TaskEditor.route}?${Screen.TaskEditor.ARG_TASK_ID}={${Screen.TaskEditor.ARG_TASK_ID}}",
            arguments = listOf(
                navArgument(Screen.TaskEditor.ARG_TASK_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(Screen.TaskEditor.ARG_TASK_ID)
            val existingTask = taskId?.let { id -> taskViewModel.getTaskById(id) }
            val settingsViewModel: SettingsViewModel = viewModel()

            TaskEditorScreen(
                navController = navController,
                taskViewModel = taskViewModel,
                existingTask = existingTask,
                settingsViewModel = settingsViewModel
                )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                navController = navController,
                taskViewModel = taskViewModel
            )
        }
    }
}
