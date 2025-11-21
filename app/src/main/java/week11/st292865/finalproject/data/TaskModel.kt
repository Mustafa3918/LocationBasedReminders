package week11.st292865.finalproject.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class TaskModel (
    @DocumentId
    val id: String? = null,

    val title: String = "",
    val note: String = "",

    //Location info that will be set once user selects one
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Int = 200,

    val isComplete: Boolean = false,

    //Timestamps
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("completedAt")
    @set:PropertyName("completedAt")
    var completedAt: Timestamp? = null
)