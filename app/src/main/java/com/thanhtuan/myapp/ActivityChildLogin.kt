package com.thanhtuan.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActitivityParentLoginBinding // Giả định dùng chung layout
import com.thanhtuan.myapp.utils.AuthUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityChildLogin : AppCompatActivity() {
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
                showLoading(false)
                Log.e("DEBUG Login", "Google sign in failed", e)
                Toast.makeText(this, "Đăng nhập thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            showLoading(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActitivityParentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInClient = AuthUtils.getGoogleSignInClient(this)

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnEmail.setOnClickListener {
            Toast.makeText(this, "Đăng nhập email sắp ra mắt", Toast.LENGTH_SHORT).show()
        }

        if (AuthUtils.getCurrentUser() != null) {
            Toast.makeText(this, "Đã đăng nhập: ${AuthUtils.getUid()}", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        }
    }

    private fun signInWithGoogle() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = AuthUtils.signInWithGoogle(account.idToken!!, isParent = false)
                showLoading(false)
                if (success) {
                    Log.d("DEBUG Login", "Google sign in successful")
                    navigateToMainActivity()
                } else {
                    Log.e("DEBUG Login", "Error: Failed to sign in with Google")
                    Toast.makeText(this@ActivityChildLogin, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Log.e("DEBUG Login", "Error: ${e.message}", e)
                Toast.makeText(this@ActivityChildLogin, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnGoogle.isEnabled = !show
        binding.btnEmail.isEnabled = !show
    }
}