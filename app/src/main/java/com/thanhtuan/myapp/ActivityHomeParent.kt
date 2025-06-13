package com.thanhtuan.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.adapters.ChildAdapter
import com.thanhtuan.myapp.databinding.ActivityMainBinding
import com.thanhtuan.myapp.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth

class ActivityHomeParent : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseService = FirebaseService()
    private val childAdapter = ChildAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        loadChildren()
    }

    private fun setupRecyclerView() {
        binding.childrenRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ActivityHomeParent)
            adapter = childAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addGuardianButton.setOnClickListener {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm trẻ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mở ActivitySetupChild để nhập thông tin trẻ
            val intent = Intent(this, ActivitySetupChild::class.java)
            startActivityForResult(intent, REQUEST_SETUP_CHILD)
        }

        binding.profileButton.setOnClickListener {
            // TODO: Xử lý khi click vào nút profile
        }

        binding.helpButton.setOnClickListener {
            // TODO: Xử lý khi click vào nút help
        }
    }

    private fun loadChildren() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.progressBar.visibility = View.VISIBLE
            firebaseService.getChildren(currentUser.uid) { children ->
                binding.progressBar.visibility = View.GONE
                if (children.isEmpty()) {
                    binding.emptyStateText.visibility = View.VISIBLE
                    binding.childrenRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyStateText.visibility = View.GONE
                    binding.childrenRecyclerView.visibility = View.VISIBLE
                    childAdapter.submitList(children)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SETUP_CHILD && resultCode == Activity.RESULT_OK) {
            // Tải lại danh sách trẻ khi thêm thành công
            loadChildren()
            Toast.makeText(this, "Thêm trẻ thành công", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_SETUP_CHILD = 1001
    }
} 