package com.example.patchtracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DoseBatcherTest {

    @Test
    fun `rapid clicks form one dose`() = runTest {
        val finalizedDoses = mutableListOf<FinalDose>()
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            onDoseFinalized = { finalizedDoses.add(it) },
            timeProvider = { testScheduler.currentTime }
        )

        // Rapid clicks within 5 seconds
        batcher.registerClick()
        advanceTimeBy(1000) // 1 second
        runCurrent()
        batcher.registerClick()
        advanceTimeBy(1000) // 1 second
        runCurrent()
        batcher.registerClick()
        runCurrent()

        // Verify state before finalization
        assertEquals(3, batcher.batchState.value.clicks)
        assertEquals(1.5, batcher.batchState.value.totalUnits, 0.001)

        // Wait for expiration (5 seconds from last click)
        advanceTimeBy(5000)
        runCurrent()

        // Should have one finalized dose
        assertEquals(1, finalizedDoses.size)
        assertEquals(3, finalizedDoses[0].clicks)
        assertEquals(1.5, finalizedDoses[0].totalUnits, 0.001)
        
        // State should be reset
        assertEquals(0, batcher.batchState.value.clicks)
        assertEquals(0.0, batcher.batchState.value.totalUnits, 0.001)
    }

    @Test
    fun `separated clicks form multiple doses`() = runTest {
        val finalizedDoses = mutableListOf<FinalDose>()
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            onDoseFinalized = { finalizedDoses.add(it) },
            timeProvider = { testScheduler.currentTime }
        )

        // First dose: 2 clicks
        batcher.registerClick()
        advanceTimeBy(1000)
        runCurrent()
        batcher.registerClick()
        advanceTimeBy(5000) // Expire first dose
        runCurrent()

        assertEquals(1, finalizedDoses.size)
        assertEquals(2, finalizedDoses[0].clicks)

        // Second dose: 3 clicks
        batcher.registerClick()
        advanceTimeBy(1000)
        runCurrent()
        batcher.registerClick()
        advanceTimeBy(1000)
        runCurrent()
        batcher.registerClick()
        advanceTimeBy(5000) // Expire second dose
        runCurrent()

        assertEquals(2, finalizedDoses.size)
        assertEquals(3, finalizedDoses[1].clicks)
        assertEquals(1.5, finalizedDoses[1].totalUnits, 0.001)
    }

    @Test
    fun `undo click decrements count`() = runTest {
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            timeProvider = { testScheduler.currentTime }
        )

        batcher.registerClick()
        batcher.registerClick()
        batcher.registerClick()

        assertEquals(3, batcher.batchState.value.clicks)

        batcher.undoClick()
        assertEquals(2, batcher.batchState.value.clicks)
        assertEquals(1.0, batcher.batchState.value.totalUnits, 0.001)

        batcher.undoClick()
        assertEquals(1, batcher.batchState.value.clicks)
    }

    @Test
    fun `undo does not go below zero`() = runTest {
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            timeProvider = { testScheduler.currentTime }
        )

        batcher.registerClick()
        assertEquals(1, batcher.batchState.value.clicks)

        batcher.undoClick()
        assertEquals(0, batcher.batchState.value.clicks)

        // Try to undo again
        batcher.undoClick()
        assertEquals(0, batcher.batchState.value.clicks)
    }

    @Test
    fun `undo all clicks cancels timer`() = runTest {
        val finalizedDoses = mutableListOf<FinalDose>()
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            onDoseFinalized = { finalizedDoses.add(it) },
            timeProvider = { testScheduler.currentTime }
        )

        batcher.registerClick()
        batcher.registerClick()

        // Undo all clicks
        batcher.undoClick()
        batcher.undoClick()

        assertEquals(0, batcher.batchState.value.clicks)

        // Wait past expiration time
        advanceTimeBy(6000)

        // Should not finalize a dose with 0 clicks
        assertEquals(0, finalizedDoses.size)
    }

    @Test
    fun `timer expiration works correctly`() = runTest {
        val finalizedDoses = mutableListOf<FinalDose>()
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            inactivityWindowMs = 5000,
            onDoseFinalized = { finalizedDoses.add(it) },
            timeProvider = { testScheduler.currentTime }
        )

        batcher.registerClick()
        runCurrent()

        // Wait 4 seconds (not expired yet)
        advanceTimeBy(4000)
        runCurrent()
        assertEquals(0, finalizedDoses.size)

        // Wait 1 more second (total 5 seconds, should expire)
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(1, finalizedDoses.size)
        assertEquals(1, finalizedDoses[0].clicks)
    }

    @Test
    fun `undo resets timer`() = runTest {
        val finalizedDoses = mutableListOf<FinalDose>()
        val batcher = DoseBatcher(
            scope = this,
            unitsPerClick = 0.5,
            onDoseFinalized = { finalizedDoses.add(it) },
            timeProvider = { testScheduler.currentTime }
        )

        batcher.registerClick()
        batcher.registerClick()
        runCurrent()

        // Wait 4 seconds
        advanceTimeBy(4000)
        runCurrent()

        // Undo (should reset timer)
        batcher.undoClick()
        runCurrent()

        // Wait 4 more seconds (total 8, but timer was reset)
        advanceTimeBy(4000)
        runCurrent()
        assertEquals(0, finalizedDoses.size) // Not expired yet

        // Wait 1 more second (5 seconds from undo)
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(1, finalizedDoses.size)
        assertEquals(1, finalizedDoses[0].clicks)
    }
}

