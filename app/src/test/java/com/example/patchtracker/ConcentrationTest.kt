package com.example.patchtracker

import com.example.patchtracker.data.Concentration
import org.junit.Assert.*
import org.junit.Test

class ConcentrationTest {

    @Test
    fun `U100 has correct value`() {
        assertEquals(100, Concentration.U100.value)
    }

    @Test
    fun `U200 has correct value`() {
        assertEquals(200, Concentration.U200.value)
    }

    @Test
    fun `fromValue returns U100 for 100`() {
        val concentration = Concentration.fromValue(100)
        assertEquals(Concentration.U100, concentration)
    }

    @Test
    fun `fromValue returns U200 for 200`() {
        val concentration = Concentration.fromValue(200)
        assertEquals(Concentration.U200, concentration)
    }

    @Test
    fun `fromValue returns U100 for invalid value`() {
        val concentration = Concentration.fromValue(999)
        assertEquals(Concentration.U100, concentration)
    }

    @Test
    fun `fromValue returns U100 for negative value`() {
        val concentration = Concentration.fromValue(-1)
        assertEquals(Concentration.U100, concentration)
    }

    @Test
    fun `all enum entries are present`() {
        val entries = Concentration.entries
        assertEquals(2, entries.size)
        assertTrue(entries.contains(Concentration.U100))
        assertTrue(entries.contains(Concentration.U200))
    }
}

