package com.thanhtuan.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.thanhtuan.myapp.databinding.ActivityPairingBinding
import com.thanhtuan.myapp.viewmodels.UserViewModel
import com.thanhtuan.myapp.viewmodels.UserViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPairingBinding
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(this) }
    private var pairingCode: String? = null
    private var childName: String? = null
    private var childAge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        pairingCode = intent.getStringExtra("pairingCode")
        childName = intent.getStringExtra("childName")
        childAge = intent.getIntExtra("childAge", -1).takeIf { it != -1 }

        Log.d("HomeActivity", "Intent data - pairingCode: $pairingCode, childName: $childName, childAge: $childAge")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("HomeActivity", "Current user: ${currentUser.uid}")

        // Xử lý giao diện theo vai trò người dùng
        viewModel.loadUser(currentUser.uid)

        // Observe trạng thái user
        viewModel.user.observe(this) { user ->
            if (user == null) {
                Log.e("HomeActivity", "User is null")
                Toast.makeText(this, "Lỗi tài khoản", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.d("HomeActivity", "User loaded: isParent=${user.isParent}")
                
                if (user.isParent) {
                    if (pairingCode != null) {
                        binding.pairingCodeText.apply {
                            text = "Mã ghép nối: $pairingCode"
                            visibility = View.VISIBLE
                        }
                        binding.pairingCodeInput.visibility = View.GONE
                        binding.connectButton.visibility = View.GONE
                    }
                } else {
                    // Trẻ em: nhập mã và liên kết
                    binding.pairingCodeText.visibility = View.GONE
                    
                    Log.d("HomeActivity", "Setting up connect button for child user")
                    
                    binding.connectButton.setOnClickListener {
                        Log.d("HomeActivity", "Connect button clicked")
                        handleConnectButtonClick(currentUser.uid)
                    }
                }
            }
        }

        // Observe trạng thái liên kết
        viewModel.linkStatus.observe(this) { status ->
            Log.d("HomeActivity", "Link status changed: $status")
            when (status) {
                is UserViewModel.LinkStatus.Loading -> {
                    binding.connectButton.isEnabled = false
                    Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show()
                }
                is UserViewModel.LinkStatus.Success -> {
                    binding.connectButton.isEnabled = true
                    Toast.makeText(this, "Liên kết thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@HomeActivity, ChildManageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is UserViewModel.LinkStatus.Error -> {
                    binding.connectButton.isEnabled = true
                    Toast.makeText(this, "Lỗi: ${status.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun handleConnectButtonClick(childUid: String) {
        val code = binding.pairingCodeInput.text.toString().trim()
        if (code.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("HomeActivity", "Connect button clicked with code: $code")
        Log.d("HomeActivity", "Child UID: $childUid")
        Log.d("HomeActivity", "Child name: $childName, Child age: $childAge")

        // Disable button immediately to prevent multiple clicks
        binding.connectButton.isEnabled = false
        
        viewModel.linkChildToParent(
            childUid = childUid,
            code = code,
            childName = childName,
            childAge = childAge
        )
    }
}
