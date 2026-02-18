package com.example.patchtracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.data.DoseRepository
import com.example.patchtracker.data.SettingsRepository
import com.example.patchtracker.nightscout.NightscoutClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing application settings.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private val doseRepository = DoseRepository(application)

    /**
     * Current settings as StateFlow.
     */
    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    // Pending settings (not yet saved)
    private val _pendingSettings = MutableStateFlow<AppSettings?>(null)
    val pendingSettings: StateFlow<AppSettings?> = _pendingSettings.asStateFlow()

    /**
     * Update pending Nightscout URL (not saved until saveSettings is called).
     */
    fun updateNightscoutUrl(url: String) {
        _pendingSettings.value = (pendingSettings.value ?: settings.value).copy(nightscoutUrl = url)
    }

    /**
     * Update pending API secret (not saved until saveSettings is called).
     */
    fun updateApiSecret(secret: String) {
        _pendingSettings.value = (pendingSettings.value ?: settings.value).copy(apiSecret = secret)
    }

    /**
     * Update pending insulin name (not saved until saveSettings is called).
     */
    fun updateInsulinName(name: String) {
        _pendingSettings.value = (pendingSettings.value ?: settings.value).copy(insulinName = name)
    }

    /**
     * Update pending concentration (not saved until saveSettings is called).
     */
    fun updateConcentration(concentration: Concentration) {
        _pendingSettings.value = (pendingSettings.value ?: settings.value).copy(concentration = concentration)
    }

    /**
     * Save all pending settings to the repository.
     */
    fun saveSettings() {
        val pending = pendingSettings.value ?: return
        viewModelScope.launch {
            repository.updateSettings(pending)
            _pendingSettings.value = null // Clear pending after save
        }
    }

    /**
     * Configure a new patch with concentration and loaded units.
     * This clears all dose history to start fresh.
     */
    fun configureNewPatch(concentration: Concentration, loadedUnits: Double) {
        viewModelScope.launch {
            // Clear all dose history when starting a new patch
            doseRepository.deleteAll()

            // Update patch configuration
            repository.configureNewPatch(concentration, loadedUnits)
        }
    }

    /**
     * Validate Nightscout URL.
     * Returns null if valid, error message if invalid.
     */
    fun validateNightscoutUrl(url: String): String? {
        if (url.isBlank()) {
            return null // Empty is allowed
        }

        return if (NightscoutClient.validateUrl(url)) {
            null
        } else {
            "URL must start with https:// (or http:// for localhost only)"
        }
    }
}

