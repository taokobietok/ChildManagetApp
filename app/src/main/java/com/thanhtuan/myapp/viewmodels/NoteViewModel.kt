package com.thanhtuan.myapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhtuan.myapp.models.Note
import com.thanhtuan.myapp.repositories.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _noteStatus = MutableLiveData<NoteStatus>()
    val noteStatus: LiveData<NoteStatus> = _noteStatus

    fun loadNotes(userId: String) {
        viewModelScope.launch {
            _noteStatus.value = NoteStatus.Loading
            repository.getNotes(userId).fold(
                onSuccess = { notes ->
                    _notes.value = notes
                    _noteStatus.value = NoteStatus.Success
                },
                onFailure = { error ->
                    _noteStatus.value = NoteStatus.Error(error.message ?: "Failed to load notes")
                }
            )
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            _noteStatus.value = NoteStatus.Loading
            repository.addNote(note).fold(
                onSuccess = {
                    loadNotes(note.userId)
                },
                onFailure = { error ->
                    _noteStatus.value = NoteStatus.Error(error.message ?: "Failed to add note")
                }
            )
        }
    }

    sealed class NoteStatus {
        object Loading : NoteStatus()
        object Success : NoteStatus()
        data class Error(val message: String) : NoteStatus()
    }
} 