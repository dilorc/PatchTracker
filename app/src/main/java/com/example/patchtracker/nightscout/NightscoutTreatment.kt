package com.example.patchtracker.nightscout

import com.google.gson.annotations.SerializedName

/**
 * Nightscout treatment payload for insulin doses.
 */
data class NightscoutTreatment(
    @SerializedName("eventType")
    val eventType: String = "Correction Bolus",
    
    @SerializedName("insulin")
    val insulin: Double,
    
    @SerializedName("created_at")
    val createdAt: String, // ISO8601 timestamp
    
    @SerializedName("notes")
    val notes: String
)

/**
 * Response from Nightscout API.
 */
data class NightscoutResponse(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("status")
    val status: String? = null
)

