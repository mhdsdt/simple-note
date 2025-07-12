package com.example.simplenote.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE syncStatus != 'DELETED' ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id AND syncStatus != 'DELETED'")
    fun getNoteById(id: Int): Flow<NoteEntity?>

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Upsert
    suspend fun upsertAll(notes: List<NoteEntity>)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("SELECT * FROM notes WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE syncStatus = 'SYNCED'")
    suspend fun getLocalSyncedNotes(): List<NoteEntity>

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotesByIds(ids: List<Int>)

    @Query("DELETE FROM notes")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceNote(noteId: Int, updatedNote: NoteEntity) {
        deleteNoteById(noteId)
        upsertNote(updatedNote)
    }
}