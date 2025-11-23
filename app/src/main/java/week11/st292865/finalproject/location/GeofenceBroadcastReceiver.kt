package week11.st292865.finalproject.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            Log.e("GeofenceReceiver", "GeofencingEvent is null")
            return
        }

        if (event.hasError()) {
            Log.e("GeofenceReceiver", "Error code: ${event.errorCode}")
            return
        }

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val ids = event.triggeringGeofences
                ?.map { it.requestId }
                .orEmpty()

            ids.forEach { id ->
                NotificationHelper(context).showTriggered(
                    title = "Location Reminder",
                    body = "You're near a saved task (id=$id)"
                )
            }
        } else {
            Log.d("GeofenceReceiver", "Transition ignored: ${event.geofenceTransition}")
        }

    }
}
