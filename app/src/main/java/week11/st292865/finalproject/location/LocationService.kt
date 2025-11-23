package week11.st292865.finalproject.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

class LocationService(context: Context) {
    private val fused = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLatLng(): LatLng? {
        val loc = fused.lastLocation.await()
        return loc?.let { LatLng(it.latitude, it.longitude) }
    }
}
