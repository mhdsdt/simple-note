package com.example.simplenote.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.simplenote.data.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteRepository: NoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check if there are local changes BEFORE pushing.
            val hadUnsyncedChanges = noteRepository.hasUnsyncedChanges()
            Log.d("SyncWorker", "hasUnsyncedChanges=${hadUnsyncedChanges}")

            // First, push any local changes to the server.
            noteRepository.syncPush()

            // Then, only fetch the latest data if this worker was NOT triggered by a local change.
            // This prevents the race condition where we pull data before our push is reflected.
            if (!hadUnsyncedChanges) {
                noteRepository.syncPull()
            }

            Result.success()
        } catch (e: Exception) {
            // If there's an error, retry the work later.
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}