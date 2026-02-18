package com.example.patchtracker

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun string_concatenation_works() {
        val result = "Patch" + "Tracker"
        assertEquals("PatchTracker", result)
    }

    @Test
    fun list_operations_work() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(5, list.size)
        assertEquals(15, list.sum())
    }
}

