package com.example.patchtracker.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.data.DoseRecord
import com.example.patchtracker.data.DoseRepository
import com.example.patchtracker.data.LogRepository
import com.example.patchtracker.data.SettingsRepository
import kotlinx.coroutines.flow.first

/**
 * Worker to finalize a dose after the inactivity window expires.
 */
class DoseFinalizationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "DoseFinalizationWorker"
    }
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "DoseFinalizationWorker started")
        
        val batchStateStore = BatchStateStore(applicationContext)
        val currentState = batchStateStore.batchStateFlow.first()
        
        // Check if the batch has expired
        val now = System.currentTimeMillis()
        if (currentState.clicks > 0 && currentState.expiresAtMillis <= now) {
            Log.d(TAG, "Finalizing dose: ${currentState.clicks} clicks, ${currentState.totalUnits} units")
            
            // Get settings for insulin name and concentration
            val settingsRepository = SettingsRepository(applicationContext)
            val settings = settingsRepository.settingsFlow.first()
            
            // Create dose record
            val doseRecord = DoseRecord(
                createdAtMillis = now,
                clicks = currentState.clicks,
                concentration = settings.concentration,
                totalUnits = currentState.totalUnits,
                insulinName = settings.insulinName
            )
            
            // Save to database (this will trigger upload)
            val doseRepository = DoseRepository(applicationContext)
            doseRepository.insert(doseRecord)

            // Log the dose
            val logRepository = LogRepository(applicationContext)
            logRepository.logInfo(
                "Dose recorded: ${doseRecord.totalUnits}u",
                "${doseRecord.clicks} clicks"
            )

            Log.d(TAG, "Dose finalized and saved: ${doseRecord.id}")
            
            // Clear batch state
            batchStateStore.clearBatchState()
            
            // Update widgets (both)
            WidgetUtils.updateBoth(applicationContext)
            
            return Result.success()
        } else {
            Log.d(TAG, "Batch not ready for finalization or already finalized")
            return Result.success()
        }
    }
}

