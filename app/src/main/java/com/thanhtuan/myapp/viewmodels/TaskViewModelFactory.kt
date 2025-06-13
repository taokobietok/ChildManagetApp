package com.thanhtuan.myapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thanhtuan.myapp.repositories.TaskRepository
import com.thanhtuan.myapp.repositories.TaskRepositoryImpl
import com.thanhtuan.myapp.services.FirebaseService

class TaskViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            val firebaseService = FirebaseService()
            val repository: TaskRepository = TaskRepositoryImpl(firebaseService)
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 