package week11.st292865.finalproject.location

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class GeofenceManager(private val context: Context) {

    private val geofencingClient = LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    fun registerGeofences(tasks: List<GeofenceTask>) {
        if (tasks.isEmpty()) return

        val geofences = tasks.map { t ->
            Geofence.Builder()
                .setRequestId(t.id)
                .setCircularRegion(t.latLng.latitude, t.latLng.longitude, t.radiusMeters.toFloat())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        geofencingClient.addGeofences(request, pendingIntent)
    }

    fun removeGeofences(taskIds: List<String>) {
        if (taskIds.isEmpty()) return
        geofencingClient.removeGeofences(taskIds)
    }
}

data class GeofenceTask(
    val id: String,
    val latLng: LatLng,
    val radiusMeters: Int,
    val title: String
)
