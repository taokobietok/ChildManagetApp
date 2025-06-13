package com.thanhtuan.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.thanhtuan.myapp.databinding.ActivityManageParentBinding

class ActivityManageParent : AppCompatActivity() {
    private lateinit var binding: ActivityManageParentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageParentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_parent)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
