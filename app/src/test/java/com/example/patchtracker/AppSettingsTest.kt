package com.example.patchtracker

import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.Concentration
import org.junit.Assert.*
import org.junit.Test

class AppSettingsTest {

    @Test
    fun `default settings have correct values`() {
        val settings = AppSettings()
        
        assertEquals("", settings.nightscoutUrl)
        assertEquals("", settings.apiSecret)
        assertEquals("Rapid-acting", settings.insulinName)
        assertEquals(Concentration.U100, settings.concentration)
    }

    @Test
    fun `U100 concentration calculates correct units per click`() {
        val unitsPerClick = AppSettings.calculateUnitsPerClick(Concentration.U100)
        
        assertEquals(2.0, unitsPerClick, 0.001)
    }

    @Test
    fun `U200 concentration calculates correct units per click`() {
        val unitsPerClick = AppSettings.calculateUnitsPerClick(Concentration.U200)
        
        assertEquals(4.0, unitsPerClick, 0.001)
    }

    @Test
    fun `effectiveUnitsPerClick property works for U100`() {
        val settings = AppSettings(concentration = Concentration.U100)
        
        assertEquals(2.0, settings.effectiveUnitsPerClick, 0.001)
    }

    @Test
    fun `effectiveUnitsPerClick property works for U200`() {
        val settings = AppSettings(concentration = Concentration.U200)
        
        assertEquals(4.0, settings.effectiveUnitsPerClick, 0.001)
    }

    @Test
    fun `base units per click constant is correct`() {
        assertEquals(2.0, AppSettings.BASE_UNITS_PER_CLICK_U100, 0.001)
    }

    @Test
    fun `concentration formula is correct`() {
        // Formula: 2.0 * (concentration / 100.0)
        // U100: 2.0 * (100 / 100.0) = 2.0
        // U200: 2.0 * (200 / 100.0) = 4.0
        
        val u100Result = 2.0 * (100 / 100.0)
        val u200Result = 2.0 * (200 / 100.0)
        
        assertEquals(u100Result, AppSettings.calculateUnitsPerClick(Concentration.U100), 0.001)
        assertEquals(u200Result, AppSettings.calculateUnitsPerClick(Concentration.U200), 0.001)
    }
}

