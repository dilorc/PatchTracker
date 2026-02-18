package com.example.patchtracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing application settings using DataStore.
 * 
 * Note: API secret is stored as plain text for MVP. Do not log it.
 */
class SettingsRepository(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        private val NIGHTSCOUT_URL = stringPreferencesKey("nightscout_url")
        private val API_SECRET = stringPreferencesKey("api_secret")
        private val INSULIN_NAME = stringPreferencesKey("insulin_name")
        private val CONCENTRATION = intPreferencesKey("concentration")
        private val LOADED_UNITS = doublePreferencesKey("loaded_units")
    }

    /**
     * Flow of current settings.
     */
    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            nightscoutUrl = preferences[NIGHTSCOUT_URL] ?: "",
            apiSecret = preferences[API_SECRET] ?: "",
            insulinName = preferences[INSULIN_NAME] ?: "Rapid-acting",
            concentration = Concentration.fromValue(
                preferences[CONCENTRATION] ?: Concentration.U100.value
            ),
            loadedUnits = preferences[LOADED_UNITS] ?: 0.0
        )
    }

    /**
     * Update Nightscout URL.
     */
    suspend fun updateNightscoutUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[NIGHTSCOUT_URL] = url
        }
    }

    /**
     * Update API secret.
     * Note: Stored as plain text for MVP. Do not log this value.
     */
    suspend fun updateApiSecret(secret: String) {
        context.dataStore.edit { preferences ->
            preferences[API_SECRET] = secret
        }
    }

    /**
     * Update insulin name.
     */
    suspend fun updateInsulinName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[INSULIN_NAME] = name
        }
    }

    /**
     * Update concentration.
     */
    suspend fun updateConcentration(concentration: Concentration) {
        context.dataStore.edit { preferences ->
            preferences[CONCENTRATION] = concentration.value
        }
    }

    /**
     * Update loaded units (when a new patch is configured).
     */
    suspend fun updateLoadedUnits(units: Double) {
        context.dataStore.edit { preferences ->
            preferences[LOADED_UNITS] = units
        }
    }

    /**
     * Configure a new patch with concentration and loaded units.
     */
    suspend fun configureNewPatch(concentration: Concentration, loadedUnits: Double) {
        context.dataStore.edit { preferences ->
            preferences[CONCENTRATION] = concentration.value
            preferences[LOADED_UNITS] = loadedUnits
        }
    }

    /**
     * Update all settings at once.
     */
    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[NIGHTSCOUT_URL] = settings.nightscoutUrl
            preferences[API_SECRET] = settings.apiSecret
            preferences[INSULIN_NAME] = settings.insulinName
            preferences[CONCENTRATION] = settings.concentration.value
            preferences[LOADED_UNITS] = settings.loadedUnits
        }
    }
}

