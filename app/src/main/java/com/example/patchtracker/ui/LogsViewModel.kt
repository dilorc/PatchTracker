package com.example.patchtracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.patchtracker.data.LogEntry
import com.example.patchtracker.data.LogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val logRepository = LogRepository(application)
    
    val logs: StateFlow<List<LogEntry>> = logRepository.listRecent(100)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun clearLogs() {
        viewModelScope.launch {
            logRepository.clearAll()
        }
    }
}

