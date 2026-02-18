package com.example.patchtracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.data.DoseRecord
import com.example.patchtracker.data.UploadStatus
import com.example.patchtracker.ui.theme.PatchTrackerTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DoseBatcherViewModel = viewModel()
) {
    val batchState by viewModel.batchState.collectAsState()
    val unitsPerClick by viewModel.unitsPerClick.collectAsState()
    val remainingUnits by viewModel.remainingUnits.collectAsState()
    val doseHistory by viewModel.doseHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PatchTracker") },
                actions = {
                    IconButton(onClick = onNavigateToLogs) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Logs"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        MainScreenContent(
            modifier = Modifier.padding(paddingValues),
            clicks = batchState.clicks,
            totalUnits = batchState.totalUnits,
            unitsPerClick = unitsPerClick,
            remainingUnits = remainingUnits,
            doseHistory = doseHistory,
            onClickPressed = { viewModel.registerClick() },
            onUndoPressed = { viewModel.undoClick() }
        )
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    clicks: Int,
    totalUnits: Double,
    unitsPerClick: Double,
    remainingUnits: Double,
    doseHistory: List<DoseRecord>,
    onClickPressed: () -> Unit,
    onUndoPressed: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Main controls section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        // Units per click info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Units per Click",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = String.format("%.1f", unitsPerClick),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Click count display
        Text(
            text = "Clicks: $clicks",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total units display
        Text(
            text = "Total Units: ${"%.1f".format(totalUnits)}",
            fontSize = 36.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Remaining units display
        if (remainingUnits > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Remaining Units",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.1f", remainingUnits),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        // Large CLICK button
        Button(
            onClick = onClickPressed,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            Text(
                text = "CLICK",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
            // Undo button
            OutlinedButton(
                onClick = onUndoPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Undo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // History section
        if (doseHistory.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            DoseHistorySection(
                doseHistory = doseHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun DoseHistorySection(
    doseHistory: List<DoseRecord>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(doseHistory) { dose ->
                DoseHistoryItem(dose = dose)
            }
        }
    }
}

@Composable
fun DoseHistoryItem(
    dose: DoseRecord,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(dose.createdAtMillis))

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${dose.clicks} clicks • ${dose.concentration.name}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f units", dose.totalUnits),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                UploadStatusBadge(status = dose.uploadStatus)
            }
        }
    }
}

@Composable
fun UploadStatusBadge(
    status: UploadStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        UploadStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.tertiary
        UploadStatus.UPLOADED -> "Uploaded" to MaterialTheme.colorScheme.primary
        UploadStatus.FAILED -> "Failed" to MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PatchTrackerTheme {
        MainScreenContent(
            clicks = 5,
            totalUnits = 10.0,
            unitsPerClick = 2.0,
            remainingUnits = 170.0,
            doseHistory = listOf(
                DoseRecord(
                    createdAtMillis = System.currentTimeMillis() - 3600000,
                    clicks = 8,
                    concentration = Concentration.U100,
                    totalUnits = 16.0,
                    insulinName = "Rapid-acting",
                    uploadStatus = UploadStatus.UPLOADED
                ),
                DoseRecord(
                    createdAtMillis = System.currentTimeMillis() - 7200000,
                    clicks = 5,
                    concentration = Concentration.U100,
                    totalUnits = 10.0,
                    insulinName = "Rapid-acting",
                    uploadStatus = UploadStatus.PENDING
                )
            ),
            onClickPressed = {},
            onUndoPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreviewEmpty() {
    PatchTrackerTheme {
        MainScreenContent(
            clicks = 0,
            totalUnits = 0.0,
            unitsPerClick = 2.0,
            remainingUnits = 180.0,
            doseHistory = emptyList(),
            onClickPressed = {},
            onUndoPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreviewU200() {
    PatchTrackerTheme {
        MainScreenContent(
            clicks = 3,
            totalUnits = 12.0,
            unitsPerClick = 4.0,
            remainingUnits = 348.0,
            doseHistory = listOf(
                DoseRecord(
                    createdAtMillis = System.currentTimeMillis() - 1800000,
                    clicks = 10,
                    concentration = Concentration.U200,
                    totalUnits = 40.0,
                    insulinName = "Rapid-acting",
                    uploadStatus = UploadStatus.FAILED,
                    lastError = "Network error"
                )
            ),
            onClickPressed = {},
            onUndoPressed = {}
        )
    }
}

