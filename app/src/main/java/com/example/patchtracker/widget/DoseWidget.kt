package com.example.patchtracker.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.patchtracker.MainActivity
import kotlinx.coroutines.flow.first
import kotlin.math.roundToLong

/**
 * Glance widget for dose tracking.
 */
class DoseWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batchStateStore = BatchStateStore(context)
        val currentState = batchStateStore.batchStateFlow.first()

        android.util.Log.d("DoseWidget", "provideGlance called: clicks=${currentState.clicks}, units=${currentState.totalUnits}")

        provideContent {
            GlanceTheme {
                WidgetContent(currentState)
            }
        }
    }
}

@Composable
fun WidgetContent(state: SharedBatchState) {
    val now = System.currentTimeMillis()
    val remainingSeconds = if (state.expiresAtMillis > now) {
        ((state.expiresAtMillis - now) / 1000.0).roundToLong()
    } else {
        0L
    }

    val clickIntent = Intent(WidgetActionReceiver.ACTION_CLICK).apply {
        component = ComponentName(
            "com.example.patchtracker",
            "com.example.patchtracker.widget.WidgetActionReceiver"
        )
    }

    val undoIntent = Intent(WidgetActionReceiver.ACTION_UNDO).apply {
        component = ComponentName(
            "com.example.patchtracker",
            "com.example.patchtracker.widget.WidgetActionReceiver"
        )
    }
    
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Cequr Patch Logger",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground
            ),
            modifier = GlanceModifier.padding(bottom = 12.dp)
        )
        
        // Status info
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Batch: ${state.clicks} clicks",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onBackground
                )
            )
            
            Spacer(modifier = GlanceModifier.height(4.dp))
            
            Text(
                text = "Units: ${String.format("%.1f", state.totalUnits)}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onBackground
                )
            )
            
            if (state.clicks > 0) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                
                Text(
                    text = "Closes in: ${remainingSeconds}s",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.secondary
                    )
                )
            }
        }
        
        // Click button
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(80.dp)
                .background(GlanceTheme.colors.primary)
                .clickable(actionSendBroadcast(clickIntent))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CLICK",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onPrimary
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Undo button
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(50.dp)
                .background(GlanceTheme.colors.error)
                .clickable(actionSendBroadcast(undoIntent))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "UNDO",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onError
                )
            )
        }
    }
}

