package com.example.patchtracker.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Handles taps on the 1x1 compact widget within Glance.
 * Updates BatchStateStore which is monitored by DoseBatcherViewModel.
 * The ViewModel's DoseBatcher handles finalization automatically.
 */
class CompactClickAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        try {
            Log.d("CompactClickAction", "Click received on glanceId: $glanceId")

            val batchStateStore = BatchStateStore(context)
            val current = batchStateStore.batchStateFlow.first()

            val settingsRepo = com.example.patchtracker.data.SettingsRepository(context)
            val settings = settingsRepo.settingsFlow.first()
            val unitsPerClick = settings.effectiveUnitsPerClick

            val newClicks = current.clicks + 1
            val newTotalUnits = newClicks * unitsPerClick
            val newExpiresAt = System.currentTimeMillis() + 5_000L

            val newState = SharedBatchState(
                clicks = newClicks,
                totalUnits = newTotalUnits,
                expiresAtMillis = newExpiresAt,
                unitsPerClick = unitsPerClick,
                uploadStatus = current.uploadStatus
            )

            Log.d("CompactClickAction", "Updating state: clicks=$newClicks, units=$newTotalUnits")
            batchStateStore.updateBatchState(newState)

            // Schedule finalization worker (REPLACE ensures only one worker runs)
            scheduleFinalizationWorker(context)

            // Force update this specific widget instance
            Log.d("CompactClickAction", "Forcing widget update for glanceId=$glanceId")
            CompactDoseWidget().update(context, glanceId)
        } catch (t: Throwable) {
            Log.e("CompactClickAction", "Failed handling compact click", t)
        }
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

        Log.d("CompactClickAction", "Scheduled finalization worker with 5s delay")
    }
}
