package com.example.patchtracker.data

/**
 * Insulin concentration types.
 * The numeric value represents the concentration factor (100 or 200).
 */
enum class Concentration(val value: Int) {
    U100(100),
    U200(200);

    companion object {
        fun fromValue(value: Int): Concentration {
            return entries.find { it.value == value } ?: U100
        }
    }
}

