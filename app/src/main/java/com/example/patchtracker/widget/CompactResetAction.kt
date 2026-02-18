package com.example.patchtracker.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll

/**
 * Handles the small top-right '×' reset in the compact widget.
 */
class CompactResetAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        try {
            Log.d("CompactResetAction", "Reset clicked on glanceId: $glanceId")

            val store = BatchStateStore(context)
            store.clearBatchState()

            Log.d("CompactResetAction", "State cleared, forcing widget update")

            // Force update ALL compact widget instances
            CompactDoseWidget().updateAll(context)

            // Also update this specific instance as a fallback
            CompactDoseWidget().update(context, glanceId)
        } catch (t: Throwable) {
            Log.e("CompactResetAction", "Failed to reset compact widget state", t)
        }
    }
}
