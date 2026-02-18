package com.example.patchtracker

import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.Concentration
import org.junit.Assert.*
import org.junit.Test

class PatchConfigurationTest {

    @Test
    fun `U100 max loaded units is 200`() {
        val maxUnits = AppSettings.calculateMaxLoadedUnits(Concentration.U100)
        assertEquals(200.0, maxUnits, 0.001)
    }

    @Test
    fun `U200 max loaded units is 400`() {
        val maxUnits = AppSettings.calculateMaxLoadedUnits(Concentration.U200)
        assertEquals(400.0, maxUnits, 0.001)
    }

    @Test
    fun `U100 with 200 units loaded has initial remaining of 180`() {
        // U100: 2.0 units per click
        // Initial deduction: 10 clicks * 2.0 = 20 units
        // Remaining: 200 - 20 = 180
        val remaining = AppSettings.calculateInitialRemainingUnits(200.0, Concentration.U100)
        assertEquals(180.0, remaining, 0.001)
    }

    @Test
    fun `U200 with 400 units loaded has initial remaining of 360`() {
        // U200: 4.0 units per click
        // Initial deduction: 10 clicks * 4.0 = 40 units
        // Remaining: 400 - 40 = 360
        val remaining = AppSettings.calculateInitialRemainingUnits(400.0, Concentration.U200)
        assertEquals(360.0, remaining, 0.001)
    }

    @Test
    fun `U100 with 100 units loaded has initial remaining of 80`() {
        // U100: 2.0 units per click
        // Initial deduction: 10 clicks * 2.0 = 20 units
        // Remaining: 100 - 20 = 80
        val remaining = AppSettings.calculateInitialRemainingUnits(100.0, Concentration.U100)
        assertEquals(80.0, remaining, 0.001)
    }

    @Test
    fun `U200 with 200 units loaded has initial remaining of 160`() {
        // U200: 4.0 units per click
        // Initial deduction: 10 clicks * 4.0 = 40 units
        // Remaining: 200 - 40 = 160
        val remaining = AppSettings.calculateInitialRemainingUnits(200.0, Concentration.U200)
        assertEquals(160.0, remaining, 0.001)
    }

    @Test
    fun `AppSettings with U100 and 200 units has correct properties`() {
        val settings = AppSettings(
            concentration = Concentration.U100,
            loadedUnits = 200.0
        )
        
        assertEquals(2.0, settings.effectiveUnitsPerClick, 0.001)
        assertEquals(200.0, settings.maxLoadedUnits, 0.001)
        assertEquals(180.0, settings.initialRemainingUnits, 0.001)
    }

    @Test
    fun `AppSettings with U200 and 400 units has correct properties`() {
        val settings = AppSettings(
            concentration = Concentration.U200,
            loadedUnits = 400.0
        )
        
        assertEquals(4.0, settings.effectiveUnitsPerClick, 0.001)
        assertEquals(400.0, settings.maxLoadedUnits, 0.001)
        assertEquals(360.0, settings.initialRemainingUnits, 0.001)
    }

    @Test
    fun `initial deduction constant is 10 clicks`() {
        assertEquals(10, AppSettings.INITIAL_DEDUCTION_CLICKS)
    }

    @Test
    fun `U100 formula verification`() {
        // Max: 2 * 100 = 200
        // Units per click: 2.0 * (100 / 100) = 2.0
        // Initial deduction: 10 * 2.0 = 20
        // Remaining: 200 - 20 = 180
        
        val concentration = Concentration.U100
        val maxUnits = 2.0 * concentration.value
        val unitsPerClick = 2.0 * (concentration.value / 100.0)
        val initialDeduction = 10 * unitsPerClick
        val remaining = maxUnits - initialDeduction
        
        assertEquals(200.0, maxUnits, 0.001)
        assertEquals(2.0, unitsPerClick, 0.001)
        assertEquals(20.0, initialDeduction, 0.001)
        assertEquals(180.0, remaining, 0.001)
    }

    @Test
    fun `U200 formula verification`() {
        // Max: 2 * 200 = 400
        // Units per click: 2.0 * (200 / 100) = 4.0
        // Initial deduction: 10 * 4.0 = 40
        // Remaining: 400 - 40 = 360
        
        val concentration = Concentration.U200
        val maxUnits = 2.0 * concentration.value
        val unitsPerClick = 2.0 * (concentration.value / 100.0)
        val initialDeduction = 10 * unitsPerClick
        val remaining = maxUnits - initialDeduction
        
        assertEquals(400.0, maxUnits, 0.001)
        assertEquals(4.0, unitsPerClick, 0.001)
        assertEquals(40.0, initialDeduction, 0.001)
        assertEquals(360.0, remaining, 0.001)
    }
}

