package com.example.patchtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for dose record operations.
 */
@Dao
interface DoseDao {
    
    /**
     * Insert a new dose record.
     */
    @Insert
    suspend fun insert(doseRecord: DoseRecord)
    
    /**
     * Get all dose records ordered by creation time (most recent first).
     */
    @Query("SELECT * FROM dose_records ORDER BY createdAtMillis DESC")
    fun listRecent(): Flow<List<DoseRecord>>
    
    /**
     * Mark a dose record as uploaded.
     */
    @Query("UPDATE dose_records SET uploadStatus = 'UPLOADED', lastError = NULL WHERE id = :id")
    suspend fun markUploaded(id: String)
    
    /**
     * Mark a dose record as failed with an error message.
     */
    @Query("UPDATE dose_records SET uploadStatus = 'FAILED', lastError = :error WHERE id = :id")
    suspend fun markFailed(id: String, error: String)
    
    /**
     * Get pending dose records (not yet uploaded) with a limit.
     */
    @Query("SELECT * FROM dose_records WHERE uploadStatus = 'PENDING' ORDER BY createdAtMillis ASC LIMIT :limit")
    suspend fun getPending(limit: Int): List<DoseRecord>

    /**
     * Delete all dose records (used when configuring a new patch).
     */
    @Query("DELETE FROM dose_records")
    suspend fun deleteAll()
}

