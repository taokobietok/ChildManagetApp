package com.thanhtuan.myapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActivityPairingChildBinding
import com.thanhtuan.myapp.viewmodels.UserViewModel
import com.thanhtuan.myapp.viewmodels.UserViewModelFactory

class ActivityPairingChild : AppCompatActivity() {
    private lateinit var binding: ActivityPairingChildBinding
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(this) }

    // ... rest of the code remains the same ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairingChildBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}