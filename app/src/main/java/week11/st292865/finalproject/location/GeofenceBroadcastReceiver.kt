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
        if (event.hasError()) {
            Log.e("GeofenceReceiver", "Error: ${event.errorCode}")
            return
        }

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()

            // 这里你可以用 id 去 Firestore 拉 title
            // 简化：直接通知 “you are near a task”
            ids.forEach { id ->
                NotificationHelper(context).showGeofenceTriggered(id)
            }
        }
    }
}
