package week11.st292865.finalproject.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import week11.st292865.finalproject.data.UserModel

class SettingsRepository(
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()
    private fun userDoc() : Result<com.google.firebase.firestore.DocumentReference> {
        val user = auth.currentUser
        return if (user == null) {
            Result.failure(Exception("User not authenticated"))
        } else {
            Result.success(
                firestore.collection("users").document(user.uid)
            )
        }
    }

    suspend fun loadUser(): Result<UserModel> {
        return try {
            val docRef = userDoc().getOrElse { return Result.failure(it) }
            val snapshot = docRef.get().await()
            val user = snapshot.toObject(UserModel::class.java) ?: UserModel()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(displayName: String, radius: Int): Result<Unit> {
        return try {
            val docRef = userDoc().getOrElse { return Result.failure(it) }

            val data = mapOf(
                "displayName" to displayName,
                "defaultRadius" to radius
            )

            docRef.update(data).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        firestore.clearPersistence()
    }
}