package com.example.patchtracker.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.patchtracker.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Broadcast receiver for widget actions (Click and Undo).
 */
class WidgetActionReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_CLICK = "com.example.patchtracker.widget.ACTION_CLICK"
        const val ACTION_UNDO = "com.example.patchtracker.widget.ACTION_UNDO"
        const val ACTION_RESET = "com.example.patchtracker.widget.ACTION_RESET"

        private const val TAG = "WidgetActionReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        
        Log.d(TAG, "Received action: $action")
        
        // Use goAsync to handle coroutines
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        
        scope.launch {
            try {
                when (action) {
                    ACTION_CLICK -> handleClick(context)
                    ACTION_UNDO -> handleUndo(context)
                    ACTION_RESET -> handleReset(context)
                }
                
                // Update widget UI (both widgets)
                WidgetUtils.updateBoth(context)

                // Schedule periodic updates for countdown
                WidgetUpdateWorker.scheduleUpdate(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling action: $action", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    private suspend fun handleClick(context: Context) {
        Log.d(TAG, "Handling click")

        val batchStateStore = BatchStateStore(context)
        val settingsRepository = SettingsRepository(context)

        // Get current state and settings
        val currentState = batchStateStore.batchStateFlow.first()
        val settings = settingsRepository.settingsFlow.first()
        val unitsPerClick = settings.effectiveUnitsPerClick

        // Increment clicks
        val newClicks = currentState.clicks + 1
        val newTotalUnits = newClicks * unitsPerClick
        val newExpiresAt = System.currentTimeMillis() + 5000L // 5 seconds

        val newState = SharedBatchState(
            clicks = newClicks,
            totalUnits = newTotalUnits,
            expiresAtMillis = newExpiresAt,
            unitsPerClick = unitsPerClick
        )

        batchStateStore.updateBatchState(newState)

        // Schedule finalization worker (REPLACE ensures only one worker runs)
        scheduleFinalizationWorker(context)

        Log.d(TAG, "Click registered: $newClicks clicks, $newTotalUnits units")
    }
    
    private suspend fun handleUndo(context: Context) {
        Log.d(TAG, "Handling undo")

        val batchStateStore = BatchStateStore(context)
        val currentState = batchStateStore.batchStateFlow.first()

        if (currentState.clicks > 0) {
            val newClicks = currentState.clicks - 1
            val newTotalUnits = newClicks * currentState.unitsPerClick

            val newState = if (newClicks == 0) {
                // No clicks left, clear state
                SharedBatchState(unitsPerClick = currentState.unitsPerClick)
            } else {
                // Reset timer
                val newExpiresAt = System.currentTimeMillis() + 5000L
                SharedBatchState(
                    clicks = newClicks,
                    totalUnits = newTotalUnits,
                    expiresAtMillis = newExpiresAt,
                    unitsPerClick = currentState.unitsPerClick
                )
            }

            batchStateStore.updateBatchState(newState)

            if (newClicks > 0) {
                // Reschedule finalization with new timer
                scheduleFinalizationWorker(context)
            }

            Log.d(TAG, "Undo successful: $newClicks clicks, $newTotalUnits units")
        }
    }

    private suspend fun handleReset(context: Context) {
        Log.d(TAG, "Handling reset")

        val batchStateStore = BatchStateStore(context)
        batchStateStore.clearBatchState()

        Log.d(TAG, "Reset successful: batch cleared")
    }

    private fun scheduleFinalizationWorker(context: Context) {
        val work = OneTimeWorkRequestBuilder<DoseFinalizationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        // Use REPLACE to ensure only one finalization worker runs at a time
        WorkManager.getInstance(context).enqueueUniqueWork(
            "dose_finalization",
            ExistingWorkPolicy.REPLACE,
            work
        )

        Log.d(TAG, "Scheduled finalization worker with 5s delay")
    }
}

