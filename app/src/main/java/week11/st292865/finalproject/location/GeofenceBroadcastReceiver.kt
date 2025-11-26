package week11.st292865.finalproject.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GEOFENCE_RX", "onReceive called")

        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            Log.e("GEOFENCE_RX", "GeofencingEvent is NULL")
            return
        }

        if (event.hasError()) {
            Log.e("GEOFENCE_RX", "GeofencingEvent errorCode=${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()

        Log.d("GEOFENCE_RX", "transition=$transition ids=$ids")

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            val canNotify =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

            Log.d("GEOFENCE_RX", "ENTER detected. canNotify=$canNotify")

            if (!canNotify) {
                Log.w("GEOFENCE_RX", "POST_NOTIFICATIONS not granted, skipping notify")
                return
            }

            val helper = NotificationHelper(context)
            ids.forEach { id ->
                Log.d("GEOFENCE_RX", "Showing notification for id=$id")
                helper.showTriggered(
                    title = "Location Reminder",
                    body = "You're near a saved task (id=$id)"
                )
            }

        } else {
            Log.d("GEOFENCE_RX", "Not ENTER, ignoring.")
        }
    }
}
