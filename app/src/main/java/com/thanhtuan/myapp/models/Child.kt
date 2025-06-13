package com.thanhtuan.myapp.models

import com.google.firebase.firestore.DocumentId

data class Child(
    @DocumentId
    val childId: String = "",
    val name: String = "",
    val age: Int = 0,
    val pairingCode: String = "",
    val parentId: String = ""
) {
    companion object {
        const val COLLECTION_NAME = "children"
    }
} 