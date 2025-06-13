package com.thanhtuan.myapp.repositories

import com.thanhtuan.myapp.models.Note
import com.thanhtuan.myapp.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NoteRepositoryImpl(private val firebaseService: FirebaseService) : NoteRepository {
    
    override suspend fun getNotes(userId: String): Result<List<Note>> = withContext(Dispatchers.IO) {
        try {
            suspendCoroutine { continuation ->
                firebaseService.getNotes(userId) { notes ->
                    continuation.resume(Result.success(notes))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addNote(note: Note): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            suspendCoroutine { continuation ->
                firebaseService.addNote(note) { success ->
                    if (success) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resumeWithException(Exception("Failed to add note"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 