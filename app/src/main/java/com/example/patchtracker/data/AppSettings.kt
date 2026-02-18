package com.example.patchtracker.data

/**
 * Application settings data class.
 *
 * @property nightscoutUrl URL for Nightscout instance
 * @property apiSecret API secret for Nightscout (stored as plain text for MVP)
 * @property insulinName Name of the insulin type
 * @property concentration Insulin concentration (U100 or U200)
 * @property loadedUnits Total units loaded in the current patch
 */
data class AppSettings(
    val nightscoutUrl: String = "",
    val apiSecret: String = "",
    val insulinName: String = "Rapid-acting",
    val concentration: Concentration = Concentration.U100,
    val loadedUnits: Double = 0.0
) {
    companion object {
        /**
         * Base units-per-click at U100 concentration.
         * This is a fixed constant.
         */
        const val BASE_UNITS_PER_CLICK_U100 = 2.0

        /**
         * Initial units deducted when a new patch is loaded.
         * This is 10 * units-per-click.
         */
        const val INITIAL_DEDUCTION_CLICKS = 10

        /**
         * Calculate effective units-per-click based on concentration.
         * Formula: 2.0 * (concentration / 100.0)
         */
        fun calculateUnitsPerClick(concentration: Concentration): Double {
            return BASE_UNITS_PER_CLICK_U100 * (concentration.value / 100.0)
        }

        /**
         * Calculate maximum units that can be loaded for a concentration.
         * Formula: 2 * concentration value
         * U100: 2 * 100 = 200 units
         * U200: 2 * 200 = 400 units
         */
        fun calculateMaxLoadedUnits(concentration: Concentration): Double {
            return 2.0 * concentration.value
        }

        /**
         * Calculate initial remaining units after loading a patch.
         * Formula: loadedUnits - (10 * unitsPerClick)
         */
        fun calculateInitialRemainingUnits(loadedUnits: Double, concentration: Concentration): Double {
            val unitsPerClick = calculateUnitsPerClick(concentration)
            val initialDeduction = INITIAL_DEDUCTION_CLICKS * unitsPerClick
            return loadedUnits - initialDeduction
        }
    }

    /**
     * Get the effective units-per-click for the current concentration.
     */
    val effectiveUnitsPerClick: Double
        get() = calculateUnitsPerClick(concentration)

    /**
     * Get the maximum units that can be loaded for the current concentration.
     */
    val maxLoadedUnits: Double
        get() = calculateMaxLoadedUnits(concentration)

    /**
     * Get the initial remaining units (after 10-click deduction).
     */
    val initialRemainingUnits: Double
        get() = calculateInitialRemainingUnits(loadedUnits, concentration)
}

