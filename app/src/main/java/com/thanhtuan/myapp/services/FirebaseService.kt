package com.thanhtuan.myapp.services

import android.net.Uri
import android.util.Log
import com.thanhtuan.myapp.models.Child
import com.thanhtuan.myapp.models.Note
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.User
import com.thanhtuan.myapp.models.Reward
import com.thanhtuan.myapp.models.TaskStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Random

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tasksCollection = db.collection(Task.COLLECTION_NAME)
    private val usersCollection = db.collection("users")
    private val linkCodesCollection = db.collection("link_codes")

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onResult(document.toObject(User::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun createTask(task: Task, onResult: (Boolean) -> Unit) {
        tasksCollection.document(task.taskId)
            .set(task)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun uploadPhoto(taskId: String, photoUri: Uri, onResult: (String?) -> Unit) {
        val photoRef = storage.reference.child("task_photos/$taskId.jpg")
        
        photoRef.putFile(photoUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                photoRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                // Update task with photo URL
                tasksCollection.document(taskId)
                    .update("photoUrl", downloadUri.toString())
                    .addOnSuccessListener {
                        onResult(downloadUri.toString())
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun evaluateTask(taskId: String, evaluation: String, childUid: String, onResult: (Boolean) -> Unit) {
        val batch = db.batch()
        val taskRef = tasksCollection.document(taskId)
        val childRef = usersCollection.document(childUid)

        // Update task status and evaluation
        batch.update(taskRef, mapOf(
            "status" to TaskStatus.EVALUATED,
            "evaluation" to evaluation
        ))

        // Add coins to child if task is completed
        if (evaluation == TaskStatus.COMPLETED.name) {
            batch.update(childRef, mapOf(
                "coins" to com.google.firebase.firestore.FieldValue.increment(10)
            ))
        }

        batch.commit()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getTask(taskId: String, onResult: (Task?) -> Unit) {
        tasksCollection.document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onResult(document.toObject(Task::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getChildTasks(childUid: String, onResult: (List<Task>) -> Unit) {
        tasksCollection
            .whereEqualTo("assignedTo", childUid)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = documents.mapNotNull { it.toObject(Task::class.java) }
                onResult(tasks)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getParentTasks(parentUid: String, onResult: (List<Task>) -> Unit) {
        tasksCollection
            .whereEqualTo("createdBy", parentUid)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = documents.mapNotNull { it.toObject(Task::class.java) }
                onResult(tasks)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun redeemReward(childUid: String, reward: Reward, onResult: (Boolean) -> Unit) {
        // First check if user has enough coins
        usersCollection.document(childUid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null && user.coins >= reward.coinCost) {
                        // User has enough coins, proceed with redemption
                        val batch = db.batch()
                        val userRef = usersCollection.document(childUid)

                        // Deduct coins
                        batch.update(userRef, mapOf(
                            "coins" to (user.coins - reward.coinCost)
                        ))

                        // Commit the transaction
                        batch.commit()
                            .addOnSuccessListener {
                                onResult(true)
                            }
                            .addOnFailureListener {
                                onResult(false)
                            }
                    } else {
                        // Not enough coins
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateTask(task: Task, onResult: (Boolean) -> Unit) {
        tasksCollection.document(task.taskId)
            .set(task)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun deleteTask(taskId: String, onResult: (Boolean) -> Unit) {
        tasksCollection.document(taskId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus, onResult: (Boolean) -> Unit) {
        tasksCollection.document(taskId)
            .update("status", newStatus)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getNotes(userId: String, onResult: (List<Note>) -> Unit) {
        db.collection(Note.COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val notes = documents.mapNotNull { it.toObject(Note::class.java) }
                onResult(notes)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addNote(note: Note, onResult: (Boolean) -> Unit) {
        db.collection(Note.COLLECTION_NAME)
            .document(note.noteId)
            .set(note)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun addChild(child: Child, onResult: (Boolean) -> Unit) {
        db.collection(Child.COLLECTION_NAME)
            .document(child.childId)
            .set(child)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getChildren(parentId: String, onResult: (List<Child>) -> Unit) {
        db.collection(Child.COLLECTION_NAME)
            .whereEqualTo("parentId", parentId)
            .get()
            .addOnSuccessListener { documents ->
                val children = documents.mapNotNull { it.toObject(Child::class.java) }
                onResult(children)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    suspend fun createLinkCode(parentUid: String): String? {
        return try {
            Log.d("FirebaseService", "Creating link code for parentUid: $parentUid")
            // Check if parent exists and is actually a parent
            val parentDoc = usersCollection.document(parentUid).get().await()
            val parent = parentDoc.toObject(User::class.java)
            
            if (parent == null || !parent.isParent) {
                Log.e("FirebaseService", "Parent not found or not a parent: $parentUid")
                return null
            }

            // Generate 6-digit code
            val code = Random().nextInt(900000) + 100000 // 100000 to 999999
            val codeString = code.toString()

            // Calculate expiration time (1 hour from now)
            val expiresAt = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour in milliseconds

            // Save to Firestore
            val linkCodeData = hashMapOf(
                "code" to codeString,
                "parentUid" to parentUid,
                "expiresAt" to expiresAt
            )

            linkCodesCollection.document(codeString)
                .set(linkCodeData)
                .await()

            Log.d("FirebaseService", "Code created: $codeString")
            codeString
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error creating link code: ${e.message}", e)
            null
        }
    }

    suspend fun linkChildToParent(
        childUid: String,
        code: String,
        childName: String?,
        childAge: Int?
    ): String {
        return try {
            Log.d("FirebaseService", "Starting link process - Child: $childUid, Code: $code")
            
            // Check if child exists and is not a parent
            val childDoc = usersCollection.document(childUid).get().await()
            val child = childDoc.toObject(User::class.java)
            
            Log.d("FirebaseService", "Child data: $child")
            
            if (child == null || child.isParent) {
                Log.e("FirebaseService", "Child not found or is parent")
                return "Tài khoản không phải trẻ"
            }

            // Check if code exists and is valid
            val codeDoc = linkCodesCollection.document(code).get().await()
            Log.d("FirebaseService", "Code document exists: ${codeDoc.exists()}")
            
            if (!codeDoc.exists()) {
                return "Mã không hợp lệ"
            }

            val codeData = codeDoc.data
            val expiresAt = codeData?.get("expiresAt") as? Long
            val parentUid = codeData?.get("parentUid") as? String

            Log.d("FirebaseService", "Code data - expiresAt: $expiresAt, parentUid: $parentUid")

            if (expiresAt == null || parentUid == null) {
                return "Mã không hợp lệ"
            }

            if (expiresAt < System.currentTimeMillis()) {
                return "Mã hết hạn"
            }

            // Check if parent exists and is actually a parent
            val parentDoc = usersCollection.document(parentUid).get().await()
            val parent = parentDoc.toObject(User::class.java)
            
            Log.d("FirebaseService", "Parent data: $parent")
            
            if (parent == null || !parent.isParent) {
                return "Không phải phụ huynh"
            }

            // Perform transaction
            Log.d("FirebaseService", "Starting transaction")
            db.runTransaction { transaction ->
                // Update child document
                val childUpdates = hashMapOf<String, Any>(
                    "linkedParent" to parentUid
                )
                childName?.let { childUpdates["name"] = it }
                childAge?.let { childUpdates["age"] = it }
                
                Log.d("FirebaseService", "Updating child document with: $childUpdates")
                transaction.update(usersCollection.document(childUid), childUpdates)

                // Update parent document
                Log.d("FirebaseService", "Updating parent document to add child")
                transaction.update(
                    usersCollection.document(parentUid),
                    "linkedChildren",
                    FieldValue.arrayUnion(childUid)
                )

                // Delete link code
                Log.d("FirebaseService", "Deleting used code")
                transaction.delete(linkCodesCollection.document(code))
            }.await()

            Log.d("FirebaseService", "Transaction completed successfully")
            "Liên kết thành công"
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error linking child to parent", e)
            "Lỗi: ${e.message}"
        }
    }
} 