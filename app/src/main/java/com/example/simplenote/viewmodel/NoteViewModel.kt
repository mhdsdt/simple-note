package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    // The UI observes this state, which is backed by the local database.
    val allNotes: StateFlow<List<NoteResponse>> = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Schedule a periodic sync when the ViewModel is created.
        noteRepository.schedulePeriodicSync()
        // Trigger an initial sync.
        noteRepository.enqueueSync()
    }

    fun getNoteById(id: Int): StateFlow<NoteResponse?> = noteRepository.getNoteById(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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