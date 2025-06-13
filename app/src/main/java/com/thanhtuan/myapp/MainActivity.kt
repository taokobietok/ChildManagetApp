package com.thanhtuan.myapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActivitySelectRoleBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnParent.setOnClickListener {
            val intent = Intent(this, ActivityParentLogin::class.java)
            startActivity(intent)
        }
        binding.btnChild.setOnClickListener {
            val intent = Intent(this, ActivityChildLogin::class.java)
            startActivity(intent)
        }
        

    }
}