package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    // This remains the same for the home screen
    val allNotes: StateFlow<List<NoteResponse>> = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // NEW: StateFlow to hold the note being edited
    private val _selectedNote = MutableStateFlow<NoteResponse?>(null)
    val selectedNote: StateFlow<NoteResponse?> = _selectedNote

    init {
        noteRepository.schedulePeriodicSync()
        noteRepository.enqueueSync()
    }

    // MODIFIED: This function now populates the _selectedNote StateFlow
    fun getNoteById(id: Int) {
        viewModelScope.launch {
            noteRepository.getNoteById(id).collect { note ->
                _selectedNote.value = note
            }
        }
    }

    // NEW: Function to clear the selected note when leaving the screen
    fun clearSelectedNote() {
        _selectedNote.value = null
    }

    fun createNote(title: String, description: String) {
        viewModelScope.launch {
            noteRepository.createNote(title, description)
        }
    }

    fun updateNote(id: Int, title: String, description: String) {
        viewModelScope.launch {
            noteRepository.updateNote(id, title, description)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
        }
    }

    fun triggerSync() {
        noteRepository.enqueueSync()
    }
}