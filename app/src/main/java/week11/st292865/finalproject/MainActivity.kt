package week11.st292865.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import week11.st292865.finalproject.navigation.AppNavGraph
import week11.st292865.finalproject.ui.theme.AppTheme
import week11.st292865.finalproject.viewmodel.AuthViewModel
import week11.st292865.finalproject.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {

                val authViewModel: AuthViewModel = viewModel()
                val taskViewModel: TaskViewModel = viewModel()

                val navController = rememberNavController()

                // Pass shared ViewModel into the navigation graph
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}
