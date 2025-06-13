package com.thanhtuan.myapp.repositories

import android.net.Uri
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import com.thanhtuan.myapp.models.TaskStatus
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TaskRepositoryImpl(private val firebaseService: FirebaseService) : TaskRepository {
    
    override suspend fun getTasks(userId: String): List<Task> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.getChildTasks(userId) { tasks ->
                continuation.resume(tasks)
            }
        }
    }

    override suspend fun createTask(task: Task): Unit = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.createTask(task) { success ->
                if (success) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(Exception("Failed to create task"))
                }
            }
        }
    }

    override suspend fun updateTask(task: Task): Unit = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.updateTask(task) { success ->
                if (success) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(Exception("Failed to update task"))
                }
            }
        }
    }

    override suspend fun deleteTask(taskId: String): Unit = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.deleteTask(taskId) { success ->
                if (success) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(Exception("Failed to delete task"))
                }
            }
        }
    }

    override suspend fun evaluateTask(taskId: String, evaluation: String, childUid: String): Unit = 
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseService.evaluateTask(taskId, evaluation, childUid) { success ->
                    if (success) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(Exception("Failed to evaluate task"))
                    }
                }
            }
        }

    override suspend fun getChildTasks(childUid: String): List<Task> = 
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseService.getChildTasks(childUid) { tasks ->
                    continuation.resume(tasks)
                }
            }
        }

    override suspend fun getParentTasks(parentUid: String): List<Task> = 
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseService.getParentTasks(parentUid) { tasks ->
                    continuation.resume(tasks)
                }
            }
        }

    override suspend fun uploadPhoto(taskId: String, photoUri: Uri): String = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.uploadPhoto(taskId, photoUri) { photoUrl ->
                if (photoUrl != null) {
                    continuation.resume(photoUrl)
                } else {
                    continuation.resumeWithException(Exception("Failed to upload photo"))
                }
            }
        }
    }

    override suspend fun updateTaskStatus(taskId: String, newStatus: TaskStatus): Unit = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.updateTaskStatus(taskId, newStatus) { success ->
                if (success) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(Exception("Failed to update task status"))
                }
            }
        }
    }
} 