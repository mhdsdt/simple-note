package com.example.simplenote.data.repository

import com.example.simplenote.api.ApiService
import com.example.simplenote.api.models.NoteRequest
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.api.models.NotesListResponse
import com.example.simplenote.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getNotes(page: Int? = null, pageSize: Int? = null): Flow<Resource<NotesListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNotes(page, pageSize)

            if (response.isSuccessful) {
                response.body()?.let { notesResponse ->
                    emit(Resource.Success(notesResponse))
                } ?: emit(Resource.Error("Notes response is null"))
            } else {
                emit(Resource.Error("Failed to get notes: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun createNote(title: String, description: String): Flow<Resource<NoteResponse>> = flow {
        emit(Resource.Loading())
        try {
            val request = NoteRequest(title, description)
            val response = apiService.createNote(request)

            if (response.isSuccessful) {
                response.body()?.let { noteResponse ->
                    emit(Resource.Success(noteResponse))
                } ?: emit(Resource.Error("Create note response is null"))
            } else {
                emit(Resource.Error("Failed to create note: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun getNoteById(id: Int): Flow<Resource<NoteResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNoteById(id)

            if (response.isSuccessful) {
                response.body()?.let { noteResponse ->
                    emit(Resource.Success(noteResponse))
                } ?: emit(Resource.Error("Note response is null"))
            } else {
                emit(Resource.Error("Failed to get note: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun updateNote(id: Int, title: String, description: String): Flow<Resource<NoteResponse>> = flow {
        emit(Resource.Loading())
        try {
            val request = NoteRequest(title, description)
            val response = apiService.updateNote(id, request)

            if (response.isSuccessful) {
                response.body()?.let { noteResponse ->
                    emit(Resource.Success(noteResponse))
                } ?: emit(Resource.Error("Update note response is null"))
            } else {
                emit(Resource.Error("Failed to update note: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun deleteNote(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteNote(id)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to delete note: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun searchNotes(
        title: String? = null,
        description: String? = null,
        updatedAfter: String? = null,
        updatedBefore: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): Flow<Resource<NotesListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.filterNotes(
                title, description, updatedAfter, updatedBefore, page, pageSize
            )

            if (response.isSuccessful) {
                response.body()?.let { notesResponse ->
                    emit(Resource.Success(notesResponse))
                } ?: emit(Resource.Error("Search notes response is null"))
            } else {
                emit(Resource.Error("Failed to search notes: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}