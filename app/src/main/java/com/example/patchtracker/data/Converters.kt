package com.example.patchtracker.data

import androidx.room.TypeConverter

/**
 * Type converters for Room database.
 */
class Converters {
    
    @TypeConverter
    fun fromConcentration(value: Concentration): Int {
        return value.value
    }
    
    @TypeConverter
    fun toConcentration(value: Int): Concentration {
        return Concentration.fromValue(value)
    }
    
    @TypeConverter
    fun fromUploadStatus(value: UploadStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toUploadStatus(value: String): UploadStatus {
        return UploadStatus.valueOf(value)
    }
}

