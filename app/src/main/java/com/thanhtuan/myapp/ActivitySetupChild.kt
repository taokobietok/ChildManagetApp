package com.thanhtuan.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.thanhtuan.myapp.databinding.ActivitySetupChildBinding
import com.thanhtuan.myapp.viewmodels.UserViewModel
import com.thanhtuan.myapp.viewmodels.UserViewModelFactory
import kotlinx.coroutines.launch

class ActivitySetupChild : AppCompatActivity() {
    private lateinit var binding: ActivitySetupChildBinding
    private val viewModel: UserViewModel by viewModels{UserViewModelFactory(this)}
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupChildBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.nextButton.setOnClickListener {
            currentUser?.let { it1 -> handleNextButtonClick(it1.uid) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PAIRING && resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val REQUEST_PAIRING = 1002
    }
    private fun handleNextButtonClick(parentUid: String) {
        val childName = binding.childNameInput.text.toString().trim()
        val childAgeStr = binding.childAgeInput.text.toString().trim()
        val childAge = childAgeStr.toIntOrNull()


        // Validate inputs
        if (childName.isEmpty() || childAge == null) {
             var user = viewModel.user.value;
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()

                Log.d("ActivitySetupChild", "User loaded: isParent=${user?.isParent}")

            return
        }

        // Create link code
        lifecycleScope.launch {
            Log.d("ActivitySetupChild", "Creating link code for parentUid: $parentUid")
            val  code = viewModel.createLinkCodeAndReturn(parentUid)
            Log.d("ActivitySetupChild", "Received code: $code")
            if (code != null) {
                // Navigate to HomeActivity with data
                val intent = Intent(this@ActivitySetupChild, HomeActivity::class.java).apply {
                    putExtra("pairingCode", code)
                    putExtra("childName", childName)
                    putExtra("childAge", childAge)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@ActivitySetupChild, "Không tạo được mã", Toast.LENGTH_SHORT).show()
            }
        }
    }
}