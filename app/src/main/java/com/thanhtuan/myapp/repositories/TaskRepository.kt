package com.thanhtuan.myapp.repositories

import android.net.Uri
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.TaskStatus

interface TaskRepository {
    suspend fun getTasks(userId: String): List<Task>
    suspend fun createTask(task: Task): Unit
    suspend fun updateTask(task: Task): Unit
    suspend fun deleteTask(taskId: String): Unit
    suspend fun evaluateTask(taskId: String, evaluation: String, childUid: String): Unit
    suspend fun getChildTasks(childUid: String): List<Task>
    suspend fun getParentTasks(parentUid: String): List<Task>
    suspend fun uploadPhoto(taskId: String, photoUri: Uri): String
    suspend fun updateTaskStatus(taskId: String, newStatus: TaskStatus): Unit
} 