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

    private val _paginatedNotes = MutableStateFlow<List<NoteResponse>>(emptyList())
    val paginatedNotes: StateFlow<List<NoteResponse>> = _paginatedNotes

    private val _screenState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val screenState: StateFlow<Resource<Unit>> = _screenState

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var currentPage = 1
    private var canPaginate = true

    // Other states
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

    init {
        loadMoreNotes()
    }

    fun loadMoreNotes() {
        if (_isLoadingMore.value || !canPaginate) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            if (currentPage == 1) {
                _screenState.value = Resource.Loading()
            }

            // Fetches 20 items per page
            noteRepository.getNotes(page = currentPage, pageSize = 20).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            _paginatedNotes.value = _paginatedNotes.value + it.results
                            canPaginate = it.next != null
                            currentPage++
                            _screenState.value = Resource.Success(Unit)
                        } ?: run {
                            _screenState.value = Resource.Error("Empty response")
                        }
                    }
                    is Resource.Error -> {
                        _screenState.value = Resource.Error(result.message ?: "An error occurred")
                    }
                    else -> {}
                }
            }
            _isLoadingMore.value = false
        }
    }

    fun refreshNotes() {
        currentPage = 1
        canPaginate = true
        _paginatedNotes.value = emptyList()
        loadMoreNotes()
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            noteRepository.searchNotes(title = query, description = query).onEach { result ->
                _searchState.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun clearSearch() {
        _searchState.value = Resource.Idle()
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
                    refreshNotes()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateNote(id: Int, title: String, description: String) {
        viewModelScope.launch {
            noteRepository.updateNote(id, title, description).onEach { result ->
                _updateNoteState.value = result
                if (result is Resource.Success) {
                    refreshNotes()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            noteRepository.deleteNote(id).onEach { result ->
                _deleteNoteState.value = result
                if (result is Resource.Success) {
                    refreshNotes()
                }
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