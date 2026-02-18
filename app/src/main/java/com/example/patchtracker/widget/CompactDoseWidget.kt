package com.example.patchtracker.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.first

/**
 * Compact 1x1 widget - just the CLICK button.
 */
class CompactDoseWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batchStateStore = BatchStateStore(context)
        val currentState = batchStateStore.batchStateFlow.first()
        val timestamp = System.currentTimeMillis()
        Log.d("CompactDoseWidget", "[$timestamp] provideGlance called for id=$id")
        Log.d("CompactDoseWidget", "[$timestamp] State: clicks=${currentState.clicks}, units=${currentState.totalUnits}, status='${currentState.uploadStatus}'")

        provideContent {
            GlanceTheme {
                CompactWidgetContent(currentState)
            }
        }

        Log.d("CompactDoseWidget", "[$timestamp] provideContent completed")
    }
}

@Composable
    fun CompactWidgetContent(state: SharedBatchState) {

    // Determine display state
    val displayText: String
    val displayColor: ColorProvider
    val fontSize: Int

    when {
        state.uploadStatus == "success" -> {
            displayText = "✓"
            displayColor = ColorProvider(Color(0xFF4CAF50)) // Green
            fontSize = 32
        }
        state.uploadStatus == "error" -> {
            displayText = "✗"
            displayColor = ColorProvider(Color(0xFFF44336)) // Red
            fontSize = 32
        }
        else -> {
            // Show current dose (units)
            displayText = "${state.totalUnits.toInt()}"
            displayColor = GlanceTheme.colors.primary
            fontSize = 28
        }
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(displayColor)
            .clickable(actionRunCallback(CompactClickAction::class.java)),
        contentAlignment = Alignment.Center
    ) {
        // Center dose/status text
        Text(
            text = displayText,
            style = TextStyle(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color.White)
            )
        )

        // Top-right small reset "×" overlay (closer to the corner)
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "×",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                ),
                modifier = GlanceModifier
                    .padding(top = 2.dp, end = 2.dp)
                    .clickable(actionRunCallback(CompactResetAction::class.java))
            )
        }
    }
}

