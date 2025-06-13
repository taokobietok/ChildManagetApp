package com.thanhtuan.myapp.models

import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId
    val taskId: String = "",
    val title: String = "",
    val time: Long = 0,
    val assignedTo: String = "",
    val createdBy: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val evaluation: String? = null,
    val photoUrl: String? = null
) {
    companion object {
        const val COLLECTION_NAME = "tasks"
    }
}

enum class TaskStatus {
    PENDING,
    COMPLETED,
    FAILED,
    EVALUATED
} 