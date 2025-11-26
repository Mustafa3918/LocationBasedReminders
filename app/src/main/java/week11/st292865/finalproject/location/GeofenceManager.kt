package week11.st292865.finalproject.location

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.*

class GeofenceManager(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE
                else 0)

        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }


    @SuppressLint("MissingPermission")
    fun register(tasks: List<GeofenceTask>) {
        if (tasks.isEmpty()) return

        val geofences = tasks.map { t ->
            Geofence.Builder()
                .setRequestId(t.id)
                .setCircularRegion(
                    t.latLng.latitude,
                    t.latLng.longitude,
                    t.radiusMeters.toFloat()
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        client.addGeofences(request, geofencePendingIntent)
    }

    fun remove(taskIds: List<String>) {
        if (taskIds.isEmpty()) return
        client.removeGeofences(taskIds)
    }
}
