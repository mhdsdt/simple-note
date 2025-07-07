package com.example.simplenote.api.models

import com.google.gson.annotations.SerializedName

data class NoteRequest(
    val title: String,
    val description: String
)

data class NoteResponse(
    @SerializedName("id")               val id: Int,
    @SerializedName("title")            val title: String,
    @SerializedName("description")      val description: String,
    @SerializedName("created_at")       val createdAt: String,
    @SerializedName("updated_at")       val updatedAt: String,
    @SerializedName("creator_name")     val creatorName: String,
    @SerializedName("creator_username") val creatorUsername: String
)

data class NotesListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NoteResponse>
)