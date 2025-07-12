package com.example.simplenote.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class SyncStatus {
    SYNCED, // The note is synchronized with the server
    NEW,    // The note is new and needs to be created on the server
    UPDATED,// The note has been updated locally and needs to be updated on the server
    DELETED // The note has been deleted locally and needs to be deleted from the server
}

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val creatorName: String,
    val creatorUsername: String,
    val syncStatus: SyncStatus
)