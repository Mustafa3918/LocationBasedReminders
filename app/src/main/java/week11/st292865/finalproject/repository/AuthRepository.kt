package week11.st292865.finalproject.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    // Always return a fresh Firestore instance (fixes stale auth token issue)
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()

            // Force Firestore to drop old cached auth state
            firestore.clearPersistence()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("User creation failed."))

            val uid = user.uid

            val data = mapOf(
                "displayName" to displayName,
                "defaultRadius" to 200
            )

            firestore.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .await()

            // Refresh persistence after creating user
            firestore.clearPersistence()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        // Drop cached login session so next user isn't using old token
        firestore.clearPersistence()
    }

    fun currentUser() = auth.currentUser
    fun getCurrentUser() = auth.currentUser
}
