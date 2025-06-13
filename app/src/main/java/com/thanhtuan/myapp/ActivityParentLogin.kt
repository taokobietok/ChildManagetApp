package com.thanhtuan.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActitivityParentLoginBinding
import com.thanhtuan.myapp.utils.AuthUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityParentLogin : AppCompatActivity() {
    private lateinit var binding: ActitivityParentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                //showLoading(false)
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            //showLoading(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActitivityParentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Google Sign In
        googleSignInClient = AuthUtils.getGoogleSignInClient(this)

        // Set up click listeners
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnEmail.setOnClickListener {
            // TODO: Implement email login
            Toast.makeText(this, "Email login coming soon", Toast.LENGTH_SHORT).show()
        }

        // Check if user is already signed in
        if (AuthUtils.getCurrentUser() != null) {
            Toast.makeText(this, "User already signed in ${AuthUtils.getUid()}", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        }
    }

    private fun signInWithGoogle() {
        //showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = AuthUtils.signInWithGoogle(account.idToken!!, isParent = true)
                if (success) {
                    Log.d("DEBUG Login", "Google sign in successful")
                    navigateToMainActivity()
                } else {
                    val errorMessage = "Failed to sign in with Google"
                    Log.e("DEBUG Login", "Error: $errorMessage")
                    Toast.makeText(this@ActivityParentLogin, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error occurred"
                Log.e("DEBUG Login", "Error: $errorMessage", e)
                Toast.makeText(this@ActivityParentLogin, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun navigateToMainActivity() {
      val intent = Intent(this, ActivityHomeParent::class.java)
        startActivity(intent)
    }
}


//    private fun showLoading(show: Boolean) {
//        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
//        binding.btnGoogle.isEnabled = !show
//        binding.btnEmail.isEnabled = !show
//    }

