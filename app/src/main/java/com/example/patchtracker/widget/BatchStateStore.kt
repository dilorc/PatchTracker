package com.example.patchtracker.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Shared batch state between app and widget.
 */
data class SharedBatchState(
    val clicks: Int = 0,
    val totalUnits: Double = 0.0,
    val expiresAtMillis: Long = 0L,
    val unitsPerClick: Double = 2.0,
    val uploadStatus: String = "" // "", "success", "error"
)

/**
 * DataStore-based state store for sharing batch state between app and widget.
 */
class BatchStateStore(private val context: Context) {
    
    companion object {
        private val Context.batchStateDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "batch_state"
        )
        
        private val CLICKS_KEY = intPreferencesKey("clicks")
        private val TOTAL_UNITS_KEY = doublePreferencesKey("total_units")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
        private val UNITS_PER_CLICK_KEY = doublePreferencesKey("units_per_click")
        private val UPLOAD_STATUS_KEY = stringPreferencesKey("upload_status")
    }
    
    /**
     * Flow of current batch state.
     */
    val batchStateFlow: Flow<SharedBatchState> = context.batchStateDataStore.data.map { prefs ->
        SharedBatchState(
            clicks = prefs[CLICKS_KEY] ?: 0,
            totalUnits = prefs[TOTAL_UNITS_KEY] ?: 0.0,
            expiresAtMillis = prefs[EXPIRES_AT_KEY] ?: 0L,
            unitsPerClick = prefs[UNITS_PER_CLICK_KEY] ?: 2.0,
            uploadStatus = prefs[UPLOAD_STATUS_KEY] ?: ""
        )
    }
    
    /**
     * Update the batch state.
     */
    suspend fun updateBatchState(state: SharedBatchState) {
        context.batchStateDataStore.edit { prefs ->
            prefs[CLICKS_KEY] = state.clicks
            prefs[TOTAL_UNITS_KEY] = state.totalUnits
            prefs[EXPIRES_AT_KEY] = state.expiresAtMillis
            prefs[UNITS_PER_CLICK_KEY] = state.unitsPerClick
            prefs[UPLOAD_STATUS_KEY] = state.uploadStatus
        }
    }

    /**
     * Set upload status (success/error) and clear after delay.
     */
    suspend fun setUploadStatus(status: String) {
        context.batchStateDataStore.edit { prefs ->
            prefs[UPLOAD_STATUS_KEY] = status
        }
    }
    
    /**
     * Clear the batch state (after finalization).
     */
    suspend fun clearBatchState() {
        context.batchStateDataStore.edit { prefs ->
            prefs[CLICKS_KEY] = 0
            prefs[TOTAL_UNITS_KEY] = 0.0
            prefs[EXPIRES_AT_KEY] = 0L
            prefs[UPLOAD_STATUS_KEY] = ""
        }
    }
}

