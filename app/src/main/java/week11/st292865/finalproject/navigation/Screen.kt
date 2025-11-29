package week11.st292865.finalproject.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object TaskEditor : Screen("task_editor") {
        const val ARG_TASK_ID = "taskId"
        fun routeWithTaskId(taskId: String): String = "$route?$ARG_TASK_ID=$taskId"
    }
    object History : Screen("history")
}
