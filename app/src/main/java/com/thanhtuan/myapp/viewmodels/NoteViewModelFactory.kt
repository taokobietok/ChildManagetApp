package com.thanhtuan.myapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thanhtuan.myapp.repositories.NoteRepository
import com.thanhtuan.myapp.repositories.NoteRepositoryImpl
import com.thanhtuan.myapp.services.FirebaseService

class NoteViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            val firebaseService = FirebaseService()
            val repository: NoteRepository = NoteRepositoryImpl(firebaseService)
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 