@file:Suppress("DEPRECATION")

package com.thanhtuan.myapp.utils

import android.content.Context
import android.util.Log
import com.thanhtuan.myapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.thanhtuan.myapp.models.User
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AuthUtils {
    private const val RC_SIGN_IN = 9001
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithGoogle(idToken: String, isParent: Boolean): Boolean {
        return try {
            // Authenticate with Firebase
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e("AuthUtils", "Firebase user is null")
                return false
            }

            // Create User object
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "",
                isParent = isParent,
                age = null,
                linkedParent = null,
                linkedChildren = emptyList(),
                coins = 0
            )

            // Save to Firestore
            usersCollection.document(user.uid)
                .set(user)
                .await()

            Log.d("AuthUtils", "User saved: ${user.uid}, isParent: $isParent")
            true
        } catch (e: Exception) {
            Log.e("AuthUtils", "Error", e)
            false
        }
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        auth.signOut()
        
        // Create new GoogleSignInClient for sign out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
            
        GoogleSignIn.getClient(context, gso)
            .signOut()
            .addOnCompleteListener {
                onComplete()
            }
    }

    fun getCurrentUser() = auth.currentUser
    fun getUid() = auth.currentUser?.uid
    suspend fun getUser(uid: String): User? {
        return try {
            val doc = usersCollection.document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("AuthUtils", "Lỗi lấy thông tin người dùng: ${e.message}")
            null
        }
    }

} 