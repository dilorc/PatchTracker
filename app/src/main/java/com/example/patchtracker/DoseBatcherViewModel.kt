package com.example.patchtracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.DoseRecord
import com.example.patchtracker.data.DoseRepository
import com.example.patchtracker.data.SettingsRepository
import com.example.patchtracker.widget.BatchStateStore
import com.example.patchtracker.widget.SharedBatchState
import com.example.patchtracker.widget.WidgetUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing DoseBatcher lifecycle and exposing state to UI.
 */
class DoseBatcherViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val doseRepository = DoseRepository(application)
    private val batchStateStore = BatchStateStore(application)

    // Expose units per click from settings
    val unitsPerClick: StateFlow<Double> = settingsRepository.settingsFlow
        .map { it.effectiveUnitsPerClick }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2.0
        )

    // Expose remaining units
    private val _remainingUnits = MutableStateFlow(0.0)
    val remainingUnits: StateFlow<Double> = _remainingUnits.asStateFlow()

    // Expose dose history
    val doseHistory: StateFlow<List<DoseRecord>> = doseRepository.listRecent()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _batchState = MutableStateFlow(BatchState())
    val batchState: StateFlow<BatchState> = _batchState.asStateFlow()

    // Keep track of current settings for dose recording
    private var currentSettings: AppSettings = AppSettings()

    init {
        // Read batch state from BatchStateStore (single source of truth)
        viewModelScope.launch {
            batchStateStore.batchStateFlow.collect { widgetState ->
                _batchState.value = BatchState(
                    clicks = widgetState.clicks,
                    totalUnits = widgetState.totalUnits,
                    expiresAtMillis = widgetState.expiresAtMillis
                )
            }
        }

        // Keep track of current settings
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                currentSettings = settings
            }
        }

        // Update remaining units based on settings, dose history, and current batch
        viewModelScope.launch {
            combine(
                settingsRepository.settingsFlow,
                doseHistory,
                batchState
            ) { settings, history, batch ->
                Triple(settings, history, batch)
            }.collect { (settings, history, batch) ->
                updateRemainingUnits(settings, history, batch.totalUnits)
            }
        }
    }

    /**
     * Calculate remaining units based on loaded units, finalized doses, and current batch.
     * Formula: initialRemainingUnits - totalFinalizedUnits - currentBatchUnits
     */
    private fun updateRemainingUnits(
        settings: AppSettings,
        history: List<DoseRecord>,
        currentBatchUnits: Double
    ) {
        if (settings.loadedUnits > 0) {
            // Sum all finalized doses
            val totalFinalizedUnits = history.sumOf { it.totalUnits }

            // Calculate remaining: initial - finalized - current batch
            val remaining = settings.initialRemainingUnits - totalFinalizedUnits - currentBatchUnits
            _remainingUnits.value = remaining.coerceAtLeast(0.0)

            Log.d("DoseBatcherViewModel", "Remaining units: initial=${settings.initialRemainingUnits}, finalized=$totalFinalizedUnits, batch=$currentBatchUnits, remaining=$remaining")
        } else {
            _remainingUnits.value = 0.0
        }
    }

    fun registerClick() {
        viewModelScope.launch {
            val current = batchStateStore.batchStateFlow.first()
            val unitsPerClick = currentSettings.effectiveUnitsPerClick

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
            scheduleFinalizationWorker()

            // Update widgets
            WidgetUtils.updateBoth(getApplication())
        }
    }

    fun undoClick() {
        viewModelScope.launch {
            val current = batchStateStore.batchStateFlow.first()

            if (current.clicks > 0) {
                val newClicks = current.clicks - 1
                val newTotalUnits = newClicks * current.unitsPerClick

                val newState = if (newClicks == 0) {
                    SharedBatchState(unitsPerClick = current.unitsPerClick)
                } else {
                    val newExpiresAt = System.currentTimeMillis() + 5000L
                    SharedBatchState(
                        clicks = newClicks,
                        totalUnits = newTotalUnits,
                        expiresAtMillis = newExpiresAt,
                        unitsPerClick = current.unitsPerClick,
                        uploadStatus = current.uploadStatus
                    )
                }

                batchStateStore.updateBatchState(newState)

                if (newClicks > 0) {
                    scheduleFinalizationWorker()
                }

                // Update widgets
                WidgetUtils.updateBoth(getApplication())
            }
        }
    }

    private fun scheduleFinalizationWorker() {
        val work = androidx.work.OneTimeWorkRequestBuilder<com.example.patchtracker.widget.DoseFinalizationWorker>()
            .setInitialDelay(5, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        androidx.work.WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            "dose_finalization",
            androidx.work.ExistingWorkPolicy.REPLACE,
            work
        )
    }
}

