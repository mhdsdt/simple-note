package com.example.simplenote.data.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.simplenote.api.ApiService
import com.example.simplenote.api.models.NoteRequest
import com.example.simplenote.api.models.NoteResponse
import com.example.simplenote.data.local.db.NoteDao
import com.example.simplenote.data.local.db.NoteEntity
import com.example.simplenote.data.local.db.SyncStatus
import com.example.simplenote.sync.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val noteDao: NoteDao,
    private val workManager: WorkManager
) {

    fun getAllNotes(): Flow<List<NoteResponse>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toNoteResponse() }
        }
    }

    fun getNoteById(id: Int): Flow<NoteResponse?> {
        return noteDao.getNoteById(id).map { it?.toNoteResponse() }
    }


    suspend fun createNote(title: String, description: String) {
        val tempId = -(UUID.randomUUID().mostSignificantBits.toInt())
        val now = LocalDateTime.now()

        val newNote = NoteEntity(
            id = tempId,
            title = title,
            description = description,
            createdAt = now,
            updatedAt = now,
            creatorName = "Me",
            creatorUsername = "me",
            syncStatus = SyncStatus.NEW
        )
        noteDao.upsertNote(newNote)
        enqueueSync()
    }

    suspend fun updateNote(id: Int, title: String, description: String) {
        val existingNote = noteDao.getNoteById(id).firstOrNull()
        if (existingNote != null) {
            val updatedNote = existingNote.copy(
                title = title,
                description = description,
                updatedAt = LocalDateTime.now(),
                syncStatus = if (existingNote.syncStatus == SyncStatus.SYNCED) SyncStatus.UPDATED else existingNote.syncStatus
            )
            noteDao.upsertNote(updatedNote)
            enqueueSync()
        }
    }

    suspend fun deleteNote(id: Int) {
        val existingNote = noteDao.getNoteById(id).firstOrNull()
        if (existingNote != null) {
            if (existingNote.syncStatus == SyncStatus.NEW) {
                noteDao.deleteNoteById(id)
            } else {
                val noteToDelete = existingNote.copy(
                    syncStatus = SyncStatus.DELETED,
                    updatedAt = LocalDateTime.now()
                )
                noteDao.upsertNote(noteToDelete)
                enqueueSync()
            }
        }
    }

    // --- SYNCHRONIZATION LOGIC ---

    suspend fun syncPush() {
        val unsyncedNotes = noteDao.getUnsyncedNotes()
        for (note in unsyncedNotes) {
            try {
                when (note.syncStatus) {
                    SyncStatus.NEW -> {
                        val request = NoteRequest(note.title, note.description)
                        val response = apiService.createNote(request)
                        if (response.isSuccessful && response.body() != null) {
                            // Replace temporary local note with the one from the server
                            noteDao.deleteNoteById(note.id)
                            noteDao.upsertNote(response.body()!!.toNoteEntity(SyncStatus.SYNCED))
                        }
                    }

                    SyncStatus.UPDATED -> {
                        val request = NoteRequest(note.title, note.description)
                        val response = apiService.updateNote(note.id, request)
                        if (response.isSuccessful && response.body() != null) {
                            noteDao.upsertNote(response.body()!!.toNoteEntity(SyncStatus.SYNCED))
                        }
                    }

                    SyncStatus.DELETED -> {
                        val response = apiService.deleteNote(note.id)
                        if (response.isSuccessful) {
                            // Permanently delete from local DB after successful server deletion
                            noteDao.deleteNoteById(note.id)
                        }
                    }

                    SyncStatus.SYNCED -> { /* Do nothing */
                    }
                }
            } catch (e: Exception) {
                // Log error and continue with the next note
                e.printStackTrace()
            }
        }
    }

    suspend fun syncPull() {
        try {
            val response = apiService.getNotes(pageSize = 1000) // Get all notes
            if (response.isSuccessful && response.body() != null) {
                val remoteNotes =
                    response.body()!!.results.map { it.toNoteEntity(SyncStatus.SYNCED) }
                noteDao.upsertAll(remoteNotes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Re-throw to let WorkManager know the job failed
        }
    }

    fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
        workManager.enqueue(syncRequest)
    }

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    // --- MAPPERS ---
    private fun NoteEntity.toNoteResponse() = NoteResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        creatorName = this.creatorName,
        creatorUsername = this.creatorUsername
    )

    private fun NoteResponse.toNoteEntity(status: SyncStatus) = NoteEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        creatorName = this.creatorName,
        creatorUsername = this.creatorUsername,
        syncStatus = status
    )
}