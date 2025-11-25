package week11.st292865.finalproject.repository

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st292865.finalproject.data.TaskModel

class TaskRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun userTasksCollection() : Result<com.google.firebase.firestore.CollectionReference> {
        val user = auth.currentUser
        return if (user == null) {
            Result.failure(Exception("User not authenticated"))
        } else {
            Result.success(
                firestore.collection("users")
                    .document(user.uid)
                    .collection("tasks")
            )
        }
    }

    //Active tasks - Read
    fun getActiveTasks(): Flow<Result<List<TaskModel>>> = callbackFlow {
        val colResult = userTasksCollection()
        if (colResult.isFailure) {
            trySend(Result.failure(colResult.exceptionOrNull()!!))
            close(colResult.exceptionOrNull())
            return@callbackFlow
        }

        val reg = colResult.getOrThrow()
            .whereEqualTo("isComplete", false)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val tasks = snapshot?.toObjects(TaskModel::class.java).orEmpty()
                trySend(Result.success(tasks))
            }
        awaitClose { reg.remove() }
    }

    //Completed Tasks / History - Read
    fun getCompletedTasks(): Flow<Result<List<TaskModel>>> = callbackFlow {
        val colResult = userTasksCollection()
        if (colResult.isFailure) {
            trySend(Result.failure(colResult.exceptionOrNull()!!))
            close(colResult.exceptionOrNull())
            return@callbackFlow
        }

        val reg = colResult.getOrThrow()
            .whereEqualTo("isComplete", true)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val tasks = snapshot?.toObjects(TaskModel::class.java).orEmpty()
                trySend(Result.success(tasks))
            }

        awaitClose { reg.remove() }
    }

    //Create
    suspend fun addTask(task: TaskModel): Result<Unit> {
        return try {
            val colRef = userTasksCollection().getOrElse { return Result.failure(it) }
            val docRef = colRef.document()
            val data = task.copy(
                id = docRef.id,
                isComplete = false,
                createdAt = Timestamp.now(),
                completedAt = null
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Update
    suspend fun updateTask(task: TaskModel): Result<Unit> {
        val id = task.id ?: return Result.failure(Exception("Task id is null"))
        return try {
            val colRef = userTasksCollection().getOrElse { return Result.failure(it) }
            colRef.document(id).set(
                task.copy(
                    id = id,
                    completedAt = task.completedAt,
                    createdAt = task.createdAt
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Delete
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val colRef = userTasksCollection().getOrElse { return Result.failure(it) }
            colRef.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Mark complete
    suspend fun markTaskComplete(taskId: String): Result<Unit> {
        return try {
            val colRef = userTasksCollection().getOrElse { return Result.failure(it) }
            colRef.document(taskId).update(
                mapOf(
                    "isComplete" to true,
                    "completedAt" to com.google.firebase.Timestamp.now()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}