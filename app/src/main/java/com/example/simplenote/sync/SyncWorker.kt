package com.example.simplenote.sync

import android.content.Context
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
            // First, push local changes to the server.
            noteRepository.syncPush()

            // Then, fetch the latest data from the server.
            noteRepository.syncPull()

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