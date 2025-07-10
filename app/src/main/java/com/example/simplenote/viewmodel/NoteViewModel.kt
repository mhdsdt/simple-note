package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.api.models.NotesListResponse
import com.example.simplenote.data.repository.NoteRepository
import com.example.simplenote.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notesState = MutableStateFlow<Resource<NotesListResponse>>(Resource.Idle())
    val notesState: StateFlow<Resource<NotesListResponse>> = _notesState

    private val _noteState = MutableStateFlow<Resource<NoteResponse>>(Resource.Idle())
    val noteState: StateFlow<Resource<NoteResponse>> = _noteState

    private val _createNoteState = MutableStateFlow<Resource<NoteResponse>>(Resource.Idle())
    val createNoteState: StateFlow<Resource<NoteResponse>> = _createNoteState

    private val _updateNoteState = MutableStateFlow<Resource<NoteResponse>>(Resource.Idle())
    val updateNoteState: StateFlow<Resource<NoteResponse>> = _updateNoteState

    private val _deleteNoteState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val deleteNoteState: StateFlow<Resource<Boolean>> = _deleteNoteState

    private val _searchState = MutableStateFlow<Resource<NotesListResponse>>(Resource.Idle())
    val searchState: StateFlow<Resource<NotesListResponse>> = _searchState

    fun getNotes(page: Int? = null, pageSize: Int? = null) {
        viewModelScope.launch {
            noteRepository.getNotes(page, pageSize).onEach { result ->
                _notesState.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            noteRepository.getNoteById(id).onEach { result ->
                _noteState.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun createNote(title: String, description: String) {
        viewModelScope.launch {
            noteRepository.createNote(title, description).onEach { result ->
                _createNoteState.value = result
                if (result is Resource.Success) {
                    getNotes()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateNote(id: Int, title: String, description: String) {
        viewModelScope.launch {
            noteRepository.updateNote(id, title, description).onEach { result ->
                _updateNoteState.value = result
                if (result is Resource.Success) {
                    getNotes()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            noteRepository.deleteNote(id).onEach { result ->
                _deleteNoteState.value = result
                if (result is Resource.Success) {
                    getNotes()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            noteRepository.searchNotes(title = query, description = query).onEach { result ->
                _searchState.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun resetNoteState() {
        _noteState.value = Resource.Idle()
    }

    fun resetCreateNoteState() {
        _createNoteState.value = Resource.Idle()
    }

    fun resetUpdateNoteState() {
        _updateNoteState.value = Resource.Idle()
    }

    fun resetDeleteNoteState() {
        _deleteNoteState.value = Resource.Idle()
    }
}