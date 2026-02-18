package com.example.patchtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room entity representing a finalized dose record.
 */
@Entity(tableName = "dose_records")
data class DoseRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val createdAtMillis: Long,
    
    val clicks: Int,
    
    val concentration: Concentration,
    
    val totalUnits: Double,
    
    val insulinName: String,
    
    val uploadStatus: UploadStatus = UploadStatus.PENDING,
    
    val lastError: String? = null
)

