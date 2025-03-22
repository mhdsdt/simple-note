package com.example.simplenote.api.models

data class NoteRequest(
    val title: String,
    val description: String
)

data class NoteResponse(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val creator_name: String,
    val creator_username: String
)

data class NotesListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NoteResponse>
)