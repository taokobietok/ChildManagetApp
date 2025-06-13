package com.thanhtuan.myapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thanhtuan.myapp.repositories.RewardRepository
import com.thanhtuan.myapp.repositories.RewardRepositoryImpl
import com.thanhtuan.myapp.services.FirebaseService

class RewardViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardViewModel::class.java)) {
            val firebaseService = FirebaseService()
            val repository: RewardRepository = RewardRepositoryImpl(firebaseService)
            @Suppress("UNCHECKED_CAST")
            return RewardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 