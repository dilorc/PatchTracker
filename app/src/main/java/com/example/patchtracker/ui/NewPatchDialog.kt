package com.example.patchtracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.ui.theme.PatchTrackerTheme

@Composable
fun NewPatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (Concentration, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedConcentration by remember { mutableStateOf(Concentration.U100) }
    var loadedUnitsText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val maxUnits = AppSettings.calculateMaxLoadedUnits(selectedConcentration)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Configure New Patch")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Concentration selector
                Text(
                    text = "Insulin Concentration",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Concentration.entries.forEach { concentration ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedConcentration == concentration,
                                onClick = { 
                                    selectedConcentration = concentration
                                    // Clear error when concentration changes
                                    errorMessage = null
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedConcentration == concentration,
                            onClick = null
                        )
                        Text(
                            text = concentration.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Loaded units input
                Text(
                    text = "Units Loaded",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = loadedUnitsText,
                    onValueChange = { 
                        loadedUnitsText = it
                        errorMessage = null
                    },
                    label = { Text("Units") },
                    supportingText = {
                        Text("Maximum: ${maxUnits.toInt()} units")
                    },
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val units = loadedUnitsText.toDoubleOrNull()
                    when {
                        units == null -> {
                            errorMessage = "Please enter a valid number"
                        }
                        units <= 0 -> {
                            errorMessage = "Units must be greater than 0"
                        }
                        units > maxUnits -> {
                            errorMessage = "Maximum ${maxUnits.toInt()} units for ${selectedConcentration.name}"
                        }
                        else -> {
                            onConfirm(selectedConcentration, units)
                        }
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun NewPatchDialogPreview() {
    PatchTrackerTheme {
        NewPatchDialog(
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

