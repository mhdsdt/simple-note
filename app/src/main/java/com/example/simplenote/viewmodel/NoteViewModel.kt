package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    // MODIFIED: initialValue is now null
    val allNotes: StateFlow<List<NoteResponse>?> = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _selectedNote = MutableStateFlow<NoteResponse?>(null)
    val selectedNote: StateFlow<NoteResponse?> = _selectedNote

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        noteRepository.schedulePeriodicSync()
        noteRepository.enqueueSync()
    }

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            noteRepository.getNoteById(id).collect { note ->
                _selectedNote.value = note
            }
        }
    }

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
        // Prevent multiple syncs from being triggered while one is already running
        if (_isSyncing.value) return

        val syncWorkId = noteRepository.enqueueSync()
        viewModelScope.launch {
            noteRepository.getSyncWorkInfoFlow(syncWorkId)
                // When the flow completes (work is finished), set loading to false
                .onCompletion { _isSyncing.value = false }
                .collect { workInfo ->
                    // Set loading to true as soon as the job is enqueued or running
                    if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                        _isSyncing.value = true
                    } else if (workInfo.state.isFinished) {
                        _isSyncing.value = false
                    }
                }
        }
    }
}