//package com.thanhtuan.myapp
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.thanhtuan.myapp.databinding.ActivityPairingBinding
//import com.thanhtuan.myapp.models.Child
//import com.thanhtuan.myapp.services.FirebaseService
//import java.util.UUID
//
//class ActivityPairing : AppCompatActivity() {
//    private lateinit var binding: ActivityPairingBinding
//    private val firebaseService = FirebaseService()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPairingBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val childName = intent.getStringExtra("child_name") ?: ""
//        val childAge = intent.getIntExtra("child_age", 0)
//
//        setupClickListeners(childName, childAge)
//    }
//
//    private fun setupClickListeners(childName: String, childAge: Int) {
////        binding.backButton.setOnClickListener {
////            finish()
////        }
//
////        binding.pairButton.setOnClickListener {
////            val pairingCode = binding.pairingCodeInput.text.toString()
////            if (pairingCode.isBlank()) {
////                binding.pairingCodeInput.error = "Vui lòng nhập mã kết nối"
////                return@setOnClickListener
////            }
//
//            showLoading(true)
//            // Create child in Firebase
//            val child = Child(
//                childId = UUID.randomUUID().toString(),
//                name = childName,
//                age = childAge,
//                pairingCode = pairingCode
//            )
//
//            firebaseService.addChild(child) { success ->
//                showLoading(false)
//                if (success) {
//                    Toast.makeText(this, "Kết nối thành công", Toast.LENGTH_SHORT).show()
//                    setResult(RESULT_OK)
//                    finish()
//                } else {
//                    Toast.makeText(this, "Kết nối thất bại", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
////    private fun showLoading(show: Boolean) {
////        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
////        binding.pairButton.isEnabled = !show
////    }
//}