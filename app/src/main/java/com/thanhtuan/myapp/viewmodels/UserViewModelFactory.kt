package com.thanhtuan.myapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thanhtuan.myapp.repositories.UserRepository
import com.thanhtuan.myapp.repositories.UserRepositoryImpl
import com.thanhtuan.myapp.services.FirebaseService

class UserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            val firebaseService = FirebaseService()
            val repository: UserRepository = UserRepositoryImpl(firebaseService)
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}