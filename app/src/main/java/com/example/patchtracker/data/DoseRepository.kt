package com.example.patchtracker.data

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.patchtracker.worker.UploadWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

/**
 * Repository for dose record operations.
 */
class DoseRepository(private val context: Context) {

    private val doseDao = AppDatabase.getDatabase(context).doseDao()
    private val workManager = WorkManager.getInstance(context)

    /**
     * Insert a new dose record and enqueue upload worker.
     */
    suspend fun insert(doseRecord: DoseRecord) {
        doseDao.insert(doseRecord)
        Log.d("DoseRepository", "Inserted dose ${doseRecord.id}, enqueueing upload worker")
        enqueueUploadWorker()
    }

    /**
     * Enqueue upload worker to process pending doses.
     */
    private fun enqueueUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15, // Initial backoff delay
                TimeUnit.SECONDS
            )
            .build()

        // Use unique work to avoid duplicate uploads
        workManager.enqueueUniqueWork(
            UploadWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )

        Log.d("DoseRepository", "Upload worker enqueued")
    }
    
    /**
     * Get all dose records ordered by creation time (most recent first).
     */
    fun listRecent(): Flow<List<DoseRecord>> {
        return doseDao.listRecent()
    }
    
    /**
     * Mark a dose record as uploaded.
     */
    suspend fun markUploaded(id: String) {
        doseDao.markUploaded(id)
    }
    
    /**
     * Mark a dose record as failed with an error message.
     */
    suspend fun markFailed(id: String, error: String) {
        doseDao.markFailed(id, error)
    }
    
    /**
     * Get pending dose records (not yet uploaded) with a limit.
     */
    suspend fun getPending(limit: Int): List<DoseRecord> {
        return doseDao.getPending(limit)
    }

    /**
     * Delete all dose records (used when configuring a new patch).
     */
    suspend fun deleteAll() {
        doseDao.deleteAll()
        Log.d("DoseRepository", "All dose records deleted")
    }
}

