package com.example.patchtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(logEntry: LogEntry)
    
    @Query("SELECT * FROM log_entries ORDER BY timestampMillis DESC LIMIT :limit")
    fun listRecent(limit: Int = 100): Flow<List<LogEntry>>
    
    @Query("DELETE FROM log_entries WHERE timestampMillis < :beforeMillis")
    suspend fun deleteOlderThan(beforeMillis: Long)
    
    @Query("DELETE FROM log_entries")
    suspend fun deleteAll()
}

