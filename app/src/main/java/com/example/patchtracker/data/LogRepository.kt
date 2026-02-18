package com.example.patchtracker.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow

/**
 * Repository for log entries.
 */
class LogRepository(context: Context) {
    
    private val logDao = AppDatabase.getDatabase(context).logDao()
    
    companion object {
        private const val TAG = "LogRepository"
    }
    
    /**
     * Get recent log entries.
     */
    fun listRecent(limit: Int = 100): Flow<List<LogEntry>> {
        return logDao.listRecent(limit)
    }
    
    /**
     * Log an info message.
     */
    suspend fun logInfo(message: String, details: String? = null) {
        val entry = LogEntry(
            level = LogLevel.INFO,
            message = message,
            details = details
        )
        logDao.insert(entry)
        Log.i(TAG, message)
    }
    
    /**
     * Log a success message.
     */
    suspend fun logSuccess(message: String, details: String? = null) {
        val entry = LogEntry(
            level = LogLevel.SUCCESS,
            message = message,
            details = details
        )
        logDao.insert(entry)
        Log.i(TAG, "✓ $message")
    }
    
    /**
     * Log an error message.
     */
    suspend fun logError(message: String, details: String? = null) {
        val entry = LogEntry(
            level = LogLevel.ERROR,
            message = message,
            details = details
        )
        logDao.insert(entry)
        Log.e(TAG, "✗ $message${details?.let { ": $it" } ?: ""}")
    }
    
    /**
     * Delete old log entries (older than 7 days).
     */
    suspend fun deleteOldLogs() {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        logDao.deleteOlderThan(sevenDaysAgo)
    }
    
    /**
     * Clear all logs.
     */
    suspend fun clearAll() {
        logDao.deleteAll()
    }
}

