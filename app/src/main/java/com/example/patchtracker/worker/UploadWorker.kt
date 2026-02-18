package com.example.patchtracker.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.patchtracker.data.DoseRepository
import com.example.patchtracker.data.LogRepository
import com.example.patchtracker.data.SettingsRepository
import com.example.patchtracker.nightscout.NightscoutClient
import com.example.patchtracker.widget.BatchStateStore
import com.example.patchtracker.widget.CompactDoseWidget
import com.example.patchtracker.widget.WidgetUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker for uploading pending doses to Nightscout.
 */
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val doseRepository = DoseRepository(context)
    private val settingsRepository = SettingsRepository(context)
    private val logRepository = LogRepository(context)
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "UploadWorker started")
        
        try {
            // Get settings
            val settings = settingsRepository.settingsFlow.first()
            
            // Validate settings
            if (settings.nightscoutUrl.isBlank()) {
                Log.w(TAG, "Nightscout URL not configured, skipping upload")
                return Result.success()
            }
            
            if (settings.apiSecret.isBlank()) {
                Log.w(TAG, "API secret not configured, skipping upload")
                return Result.success()
            }
            
            if (!NightscoutClient.validateUrl(settings.nightscoutUrl)) {
                Log.e(TAG, "Invalid Nightscout URL")
                return Result.failure()
            }
            
            // Create Nightscout client
            // SECURITY: Never log the API secret
            val client = NightscoutClient(settings.nightscoutUrl, settings.apiSecret)
            
            // Get pending doses (limit to 10 per run)
            val pendingDoses = doseRepository.getPending(limit = 10)
            
            if (pendingDoses.isEmpty()) {
                Log.d(TAG, "No pending doses to upload")
                return Result.success()
            }
            
            Log.d(TAG, "Found ${pendingDoses.size} pending doses to upload")
            
            var successCount = 0
            var failureCount = 0
            val batchStateStore = BatchStateStore(applicationContext)

            // Upload each dose
            for (dose in pendingDoses) {
                val result = client.uploadDose(dose)

                if (result.isSuccess) {
                    doseRepository.markUploaded(dose.id)
                    successCount++
                    Log.d(TAG, "Successfully uploaded dose ${dose.id}")

                    // Log success
                    logRepository.logSuccess(
                        "Uploaded ${dose.totalUnits}u to Nightscout",
                        "${dose.clicks} clicks"
                    )

                    // Show success in widget
                    batchStateStore.setUploadStatus("success")
                    Log.d(TAG, "Updating compact widget with success status")
                    WidgetUtils.updateCompact(applicationContext)
                    delay(2000) // Show success for 2 seconds
                    batchStateStore.setUploadStatus("")
                    Log.d(TAG, "Clearing compact widget status")
                    WidgetUtils.updateCompact(applicationContext)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    doseRepository.markFailed(dose.id, error)
                    failureCount++
                    Log.e(TAG, "Failed to upload dose ${dose.id}: $error")

                    // Log error
                    logRepository.logError(
                        "Failed to upload ${dose.totalUnits}u",
                        error
                    )

                    // Show error in widget
                    batchStateStore.setUploadStatus("error")
                    Log.d(TAG, "Updating compact widget with error status")
                    WidgetUtils.updateCompact(applicationContext)
                    delay(2000) // Show error for 2 seconds
                    batchStateStore.setUploadStatus("")
                    Log.d(TAG, "Clearing compact widget status")
                    WidgetUtils.updateCompact(applicationContext)
                }
            }

            Log.d(TAG, "Upload complete: $successCount succeeded, $failureCount failed")
            
            // If any failed, retry with backoff
            return if (failureCount > 0) {
                Result.retry()
            } else {
                Result.success()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "UploadWorker error: ${e.message}", e)
            return Result.retry()
        }
    }
    
    companion object {
        private const val TAG = "UploadWorker"
        const val WORK_NAME = "nightscout_upload"
    }
}

