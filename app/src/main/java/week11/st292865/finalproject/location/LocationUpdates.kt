package week11.st292865.finalproject.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

class LocationUpdates(context: Context) {
    private val fused = LocationServices.getFusedLocationProviderClient(context)

    private val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5000L // 5s
    ).setMinUpdateIntervalMillis(3000L).build()

    @SuppressLint("MissingPermission")
    fun locationFlow(): Flow<LatLng> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                trySend(LatLng(loc.latitude, loc.longitude))
            }
        }
        fused.requestLocationUpdates(request, callback, android.os.Looper.getMainLooper())
        awaitClose { fused.removeLocationUpdates(callback) }
    }
}
