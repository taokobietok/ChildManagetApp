package com.thanhtuan.myapp.models

import com.google.firebase.firestore.DocumentId

data class Note(
    @DocumentId
    val noteId: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val title: CharSequence,
    val date: CharSequence
) {
    companion object {
        const val COLLECTION_NAME = "notes"
    }
} 