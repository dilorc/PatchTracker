package com.example.patchtracker.widget

import android.content.Context
import android.util.Log
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WidgetUtils {
    private const val TAG = "WidgetUtils"

    suspend fun updateCompact(context: Context) {
        try {
            withContext(Dispatchers.Main) {
                CompactDoseWidgetReceiver.updateAllWidgets(context)
                Log.d(TAG, "Triggered compact widget update")
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to update compact widget", t)
        }
    }

    suspend fun updateFull(context: Context) {
        try {
            withContext(Dispatchers.Main) {
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(DoseWidget::class.java)
                Log.d(TAG, "Updating full widget instances: count=${ids.size}")
                val widget = DoseWidget()
                for (id in ids) {
                    widget.update(context, id)
                }
                // Also broadcast update for robustness
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val component = ComponentName(context, DoseWidgetReceiver::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(component)
                Log.d(TAG, "Broadcasting ACTION_APPWIDGET_UPDATE to full: ids=${appWidgetIds.contentToString()}")
                val intent = android.content.Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                    this.component = component
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to update full widget", t)
        }
    }

    suspend fun updateBoth(context: Context) {
        updateFull(context)
        updateCompact(context)
    }
}

