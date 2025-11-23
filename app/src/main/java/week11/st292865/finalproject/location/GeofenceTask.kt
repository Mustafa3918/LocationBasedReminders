package week11.st292865.finalproject.location

import com.google.android.gms.maps.model.LatLng

// min request for location model
// need task part
data class GeofenceTask(
    val id: String,
    val title: String,
    val latLng: LatLng,
    val radiusMeters: Int
)
