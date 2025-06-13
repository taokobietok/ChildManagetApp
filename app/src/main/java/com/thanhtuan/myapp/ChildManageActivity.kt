package com.thanhtuan.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActivityChildManageBinding

class ChildManageActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityChildManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}