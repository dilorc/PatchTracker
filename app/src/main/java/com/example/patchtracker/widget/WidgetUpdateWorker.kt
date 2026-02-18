package com.example.patchtracker.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Worker to periodically update the widget countdown.
 */
class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "WidgetUpdateWorker"
        const val WORK_NAME = "widget_update"
        
        /**
         * Schedule periodic widget updates while batch is active.
         */
        fun scheduleUpdate(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(1, TimeUnit.SECONDS)
                .addTag(WORK_NAME)
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "WidgetUpdateWorker started")
        
        val batchStateStore = BatchStateStore(applicationContext)
        val currentState = batchStateStore.batchStateFlow.first()
        
        // Update widgets (both)
        WidgetUtils.updateBoth(applicationContext)
        
        // If batch is still active, schedule another update
        val now = System.currentTimeMillis()
        if (currentState.clicks > 0 && currentState.expiresAtMillis > now) {
            scheduleUpdate(applicationContext)
        }
        
        return Result.success()
    }
}

