package week11.st292865.finalproject.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

class LocationService(context: Context) {
    private val fused = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Get a reasonably fresh location once.
     * 1) Try last known location (fast)
     * 2) If null, request current high-accuracy location once
     */
    @SuppressLint("MissingPermission")
    suspend fun getFreshLatLng(): LatLng? {
        val last = fused.lastLocation.await()
        if (last != null) {
            return LatLng(last.latitude, last.longitude)
        }

        val current = fused.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).await()

        return current?.let { LatLng(it.latitude, it.longitude) }
    }
}
