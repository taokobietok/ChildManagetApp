package com.thanhtuan.myapp.repositories

import com.thanhtuan.myapp.models.Note

interface NoteRepository {
    suspend fun getNotes(userId: String): Result<List<Note>>
    suspend fun addNote(note: Note): Result<Unit>
} 