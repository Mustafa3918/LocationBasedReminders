package week11.st292865.finalproject.location

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

data class PermissionState(
    val allGranted: Boolean,
    val requestPermissions: () -> Unit
)

@Composable
fun rememberLocationAndNotificationPermissionState(
    onAllGranted: () -> Unit = {}
): PermissionState {
    val context = LocalContext.current

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val notificationPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        else emptyArray()

    val required = locationPermissions + notificationPermissions

    var allGranted by remember { mutableStateOf(false) }

    fun checkAllGranted(): Boolean =
        required.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        allGranted = result.values.all { it }
        if (allGranted) onAllGranted()
    }

    LaunchedEffect(Unit) {
        allGranted = checkAllGranted()
        if (allGranted) onAllGranted()
    }

    return PermissionState(
        allGranted = allGranted,
        requestPermissions = { launcher.launch(required) }
    )
}
