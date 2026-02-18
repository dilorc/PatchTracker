package com.example.patchtracker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Pure Kotlin batching engine for dose tracking.
 * No Android dependencies.
 */
data class BatchState(
    val clicks: Int = 0,
    val totalUnits: Double = 0.0,
    val expiresAtMillis: Long = 0L,
    val remainingMillis: Long = 0L
)

data class FinalDose(
    val timestampMillis: Long,
    val clicks: Int,
    val totalUnits: Double
)

class DoseBatcher(
    private val scope: CoroutineScope,
    private val unitsPerClick: Double = 0.5,
    private val inactivityWindowMs: Long = 5000L,
    private val onDoseFinalized: (FinalDose) -> Unit = {},
    private val timeProvider: () -> Long = { System.currentTimeMillis() }
) {
    private val _batchState = MutableStateFlow(BatchState())
    val batchState: StateFlow<BatchState> = _batchState.asStateFlow()

    private var currentClicks = 0
    private var expirationJob: Job? = null
    private var expiresAt = 0L

    /**
     * Register a click, increment count, and reset expiration timer.
     */
    fun registerClick() {
        currentClicks++
        resetExpirationTimer()
        updateState()
    }

    /**
     * Undo the last click (decrement count, not below zero).
     */
    fun undoClick() {
        if (currentClicks > 0) {
            currentClicks--
            if (currentClicks == 0) {
                // No clicks left, cancel timer
                cancelExpirationTimer()
                expiresAt = 0L
            } else {
                // Still have clicks, reset timer
                resetExpirationTimer()
            }
            updateState()
        }
    }

    /**
     * Reset the expiration timer to now + inactivity window.
     */
    private fun resetExpirationTimer() {
        cancelExpirationTimer()
        expiresAt = timeProvider() + inactivityWindowMs

        expirationJob = scope.launch {
            delay(inactivityWindowMs)
            finalizeDose()
        }
    }

    /**
     * Cancel the current expiration timer.
     */
    private fun cancelExpirationTimer() {
        expirationJob?.cancel()
        expirationJob = null
    }

    /**
     * Finalize the current dose if clicks > 0.
     */
    private fun finalizeDose() {
        if (currentClicks > 0) {
            val dose = FinalDose(
                timestampMillis = timeProvider(),
                clicks = currentClicks,
                totalUnits = currentClicks * unitsPerClick
            )
            onDoseFinalized(dose)

            // Reset state
            currentClicks = 0
            expiresAt = 0L
            updateState()
        }
    }

    /**
     * Update the batch state flow.
     */
    private fun updateState() {
        val now = timeProvider()
        val remaining = if (expiresAt > 0) maxOf(0L, expiresAt - now) else 0L

        _batchState.value = BatchState(
            clicks = currentClicks,
            totalUnits = currentClicks * unitsPerClick,
            expiresAtMillis = expiresAt,
            remainingMillis = remaining
        )
    }

    /**
     * Clean up resources.
     */
    fun dispose() {
        cancelExpirationTimer()
    }
}

