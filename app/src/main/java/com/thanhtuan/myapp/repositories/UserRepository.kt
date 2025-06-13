package com.thanhtuan.myapp.repositories

import com.thanhtuan.myapp.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random
import com.thanhtuan.myapp.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {
    suspend fun createLinkCode(parentUid: String): Result<String>
    suspend fun linkChildToParent(childUid: String, code: String, childName: String?, childAge: Int?): Result<String>
    suspend fun getUser(uid: String): Result<User?>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun signOut(): Result<Unit>
}

class UserRepositoryImpl(private val firebaseService: FirebaseService) : UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val linkCodesCollection = firestore.collection("link_codes")

    override suspend fun createLinkCode(parentUid: String): Result<String> = suspendCoroutine { continuation ->
        // Generate a random 6-digit code
        val code = String.format("%06d", Random.nextInt(0, 1000000))
        
        // Set expiration time to 1 hour from now
        val expiresAt = Date(System.currentTimeMillis() + 3600000) // 1 hour in milliseconds
        
        val linkCodeData = hashMapOf(
            "code" to code,
            "parentUid" to parentUid,
            "expiresAt" to expiresAt
        )

        linkCodesCollection.document(code)
            .set(linkCodeData)
            .addOnSuccessListener {
                continuation.resume(Result.success(code))
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }

    override suspend fun linkChildToParent(
        childUid: String,
        code: String,
        childName: String?,
        childAge: Int?
    ): Result<String> = suspendCoroutine { continuation ->
        linkCodesCollection.document(code)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val expiresAt = document.getTimestamp("expiresAt")
                    val parentUid = document.getString("parentUid")

                    if (expiresAt != null && expiresAt.toDate().after(Date()) && parentUid != null) {
                        // Code is valid, proceed with linking
                        val batch = firestore.batch()

                        // Update child's document
                        val childRef = usersCollection.document(childUid)
                        batch.update(childRef, mapOf(
                            "linkedParent" to parentUid,
                            "childName" to childName,
                            "childAge" to childAge
                        ))

                        // Update parent's document to add child to linkedChildren
                        val parentRef = usersCollection.document(parentUid)
                        batch.update(parentRef, "linkedChildren", FieldValue.arrayUnion(childUid))

                        // Delete the used code
                        val codeRef = linkCodesCollection.document(code)
                        batch.delete(codeRef)

                        // Commit the batch
                        batch.commit()
                            .addOnSuccessListener {
                                continuation.resume(Result.success("success"))
                            }
                            .addOnFailureListener { e ->
                                continuation.resume(Result.failure(e))
                            }
                    } else {
                        continuation.resume(Result.failure(Exception("Link code has expired")))
                    }
                } else {
                    continuation.resume(Result.failure(Exception("Invalid link code")))
                }
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }

    override suspend fun getUser(uid: String): Result<User?> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.getUser(uid) { user ->
                if (user != null) {
                    continuation.resume(Result.success(user))
                } else {
                    continuation.resume(Result.failure(Exception("User not found")))
                }
            }
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> = suspendCoroutine { continuation ->
        usersCollection.document(user.uid)
            .set(user)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }

    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseService.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 