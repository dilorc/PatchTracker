package com.example.patchtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for PatchTracker.
 */
@Database(
    entities = [DoseRecord::class, LogEntry::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun doseDao(): DoseDao
    abstract fun logDao(): LogDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "patchtracker_database"
                )
                .fallbackToDestructiveMigration() // For development - recreate DB on schema change
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

