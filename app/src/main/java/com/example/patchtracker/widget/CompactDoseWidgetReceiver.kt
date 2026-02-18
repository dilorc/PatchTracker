package com.example.patchtracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.patchtracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Traditional AppWidgetProvider for CompactDoseWidget (1x1).
 * Uses RemoteViews instead of Glance for better update control.
 */
class CompactDoseWidgetReceiver : AppWidgetProvider() {

    companion object {
        private const val TAG = "CompactDoseWidget"
        const val ACTION_CLICK = "com.example.patchtracker.widget.COMPACT_CLICK"
        const val ACTION_RESET = "com.example.patchtracker.widget.COMPACT_RESET"

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, CompactDoseWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, CompactDoseWidgetReceiver::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_CLICK -> {
                Log.d(TAG, "Click action received")
                handleClick(context)
            }
            ACTION_RESET -> {
                Log.d(TAG, "Reset action received")
                handleReset(context)
            }
        }
    }

    private fun handleClick(context: Context) {
        scope.launch {
            val batchStateStore = BatchStateStore(context)
            val settingsRepo = com.example.patchtracker.data.SettingsRepository(context)

            val current = batchStateStore.batchStateFlow.first()
            val settings = settingsRepo.settingsFlow.first()
            val unitsPerClick = settings.effectiveUnitsPerClick

            val newClicks = current.clicks + 1
            val newTotalUnits = newClicks * unitsPerClick
            val newExpiresAt = System.currentTimeMillis() + 5000L

            val newState = SharedBatchState(
                clicks = newClicks,
                totalUnits = newTotalUnits,
                expiresAtMillis = newExpiresAt,
                unitsPerClick = unitsPerClick,
                uploadStatus = current.uploadStatus
            )

            batchStateStore.updateBatchState(newState)

            // Schedule finalization worker
            scheduleFinalizationWorker(context)

            Log.d(TAG, "Click registered: $newClicks clicks, $newTotalUnits units")

            // Update all widget instances
            updateAllWidgets(context)
        }
    }

    private fun handleReset(context: Context) {
        scope.launch {
            val batchStateStore = BatchStateStore(context)
            batchStateStore.clearBatchState()

            Log.d(TAG, "Reset successful")

            // Update all widget instances
            updateAllWidgets(context)
        }
    }

    private fun scheduleFinalizationWorker(context: Context) {
        val work = androidx.work.OneTimeWorkRequestBuilder<DoseFinalizationWorker>()
            .setInitialDelay(5, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            "dose_finalization",
            androidx.work.ExistingWorkPolicy.REPLACE,
            work
        )
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        scope.launch {
            val batchStateStore = BatchStateStore(context)
            val state = batchStateStore.batchStateFlow.first()

            Log.d(TAG, "Updating widget $appWidgetId: clicks=${state.clicks}, units=${state.totalUnits}")

            val views = RemoteViews(context.packageName, R.layout.compact_dose_widget)

            // Set the display text
            val displayText = if (state.clicks > 0) {
                state.clicks.toString()
            } else {
                "+"
            }
            views.setTextViewText(R.id.compact_dose_text, displayText)

            // Set click action
            val clickIntent = Intent(context, CompactDoseWidgetReceiver::class.java).apply {
                action = ACTION_CLICK
            }
            val clickPendingIntent = PendingIntent.getBroadcast(
                context, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.compact_dose_button, clickPendingIntent)

            // Set reset action (long press or small button)
            val resetIntent = Intent(context, CompactDoseWidgetReceiver::class.java).apply {
                action = ACTION_RESET
            }
            val resetPendingIntent = PendingIntent.getBroadcast(
                context, 1, resetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.compact_reset_button, resetPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

