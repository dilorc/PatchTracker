package com.example.patchtracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.patchtracker.data.AppSettings
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.ui.theme.PatchTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val pendingSettings by viewModel.pendingSettings.collectAsState()

    SettingsScreenContent(
        settings = settings,
        pendingSettings = pendingSettings,
        onNightscoutUrlChange = viewModel::updateNightscoutUrl,
        onApiSecretChange = viewModel::updateApiSecret,
        onInsulinNameChange = viewModel::updateInsulinName,
        onConcentrationChange = viewModel::updateConcentration,
        onSaveSettings = viewModel::saveSettings,
        onConfigureNewPatch = viewModel::configureNewPatch,
        validateNightscoutUrl = viewModel::validateNightscoutUrl,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    settings: AppSettings,
    pendingSettings: AppSettings?,
    onNightscoutUrlChange: (String) -> Unit,
    onApiSecretChange: (String) -> Unit,
    onInsulinNameChange: (String) -> Unit,
    onConcentrationChange: (Concentration) -> Unit,
    onSaveSettings: () -> Unit,
    onConfigureNewPatch: (Concentration, Double) -> Unit,
    validateNightscoutUrl: (String) -> String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use pending settings if available, otherwise use saved settings
    val displaySettings = pendingSettings ?: settings

    var nightscoutUrl by remember(displaySettings.nightscoutUrl) { mutableStateOf(displaySettings.nightscoutUrl) }
    var apiSecret by remember(displaySettings.apiSecret) { mutableStateOf(displaySettings.apiSecret) }
    var insulinName by remember(displaySettings.insulinName) { mutableStateOf(displaySettings.insulinName) }
    var showPassword by remember { mutableStateOf(false) }
    var showNewPatchDialog by remember { mutableStateOf(false) }
    var showSaveConfirmation by remember { mutableStateOf(false) }

    // URL validation error
    val urlError = remember(nightscoutUrl) {
        validateNightscoutUrl(nightscoutUrl)
    }

    // Check if there are unsaved changes
    val hasUnsavedChanges = pendingSettings != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showSaveConfirmation) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSaveConfirmation = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("Settings saved successfully")
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // New Patch Button
            Button(
                onClick = { showNewPatchDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Patch")
            }

            // Current patch info
            if (settings.loadedUnits > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Current Patch",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "${settings.concentration.name}: ${settings.loadedUnits.toInt()} units loaded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Initial remaining: ${settings.initialRemainingUnits.toInt()} units",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            HorizontalDivider()

            // Nightscout URL
            OutlinedTextField(
                value = nightscoutUrl,
                onValueChange = {
                    nightscoutUrl = it
                    onNightscoutUrlChange(it)
                },
                label = { Text("Nightscout URL") },
                placeholder = { Text("https://your-nightscout.herokuapp.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                isError = urlError != null,
                supportingText = urlError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // API Secret
            OutlinedTextField(
                value = apiSecret,
                onValueChange = {
                    apiSecret = it
                    onApiSecretChange(it)
                },
                label = { Text("API Secret") },
                placeholder = { Text("Enter your API secret") },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Hide" else "Show")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Insulin Name
            OutlinedTextField(
                value = insulinName,
                onValueChange = {
                    insulinName = it
                    onInsulinNameChange(it)
                },
                label = { Text("Insulin Name") },
                placeholder = { Text("Rapid-acting") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Concentration Selector
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Concentration",
                    style = MaterialTheme.typography.titleMedium
                )

                Concentration.entries.forEach { concentration ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = displaySettings.concentration == concentration,
                                onClick = { onConcentrationChange(concentration) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = displaySettings.concentration == concentration,
                            onClick = null // Click handled by Row
                        )
                        Text(
                            text = concentration.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Calculated Units Per Click
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Effective Units Per Click",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = String.format("%.1f units", displaySettings.effectiveUnitsPerClick),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Base: ${AppSettings.BASE_UNITS_PER_CLICK_U100} units at U100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            HorizontalDivider()

            // Save Button
            Button(
                onClick = {
                    onSaveSettings()
                    showSaveConfirmation = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasUnsavedChanges && urlError == null
            ) {
                Text(
                    text = if (hasUnsavedChanges) "Save Settings" else "No Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // New Patch Dialog
    if (showNewPatchDialog) {
        NewPatchDialog(
            onDismiss = { showNewPatchDialog = false },
            onConfirm = { concentration, units ->
                onConfigureNewPatch(concentration, units)
                showNewPatchDialog = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PatchTrackerTheme {
        SettingsScreenContent(
            settings = AppSettings(
                nightscoutUrl = "https://example.herokuapp.com",
                apiSecret = "secret123",
                insulinName = "Rapid-acting",
                concentration = Concentration.U100,
                loadedUnits = 200.0
            ),
            pendingSettings = null,
            onNightscoutUrlChange = {},
            onApiSecretChange = {},
            onInsulinNameChange = {},
            onConcentrationChange = {},
            onSaveSettings = {},
            onConfigureNewPatch = { _, _ -> },
            validateNightscoutUrl = { null },
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenU200Preview() {
    PatchTrackerTheme {
        SettingsScreenContent(
            settings = AppSettings(
                nightscoutUrl = "",
                apiSecret = "",
                insulinName = "Rapid-acting",
                concentration = Concentration.U200,
                loadedUnits = 400.0
            ),
            pendingSettings = AppSettings(
                nightscoutUrl = "https://new-url.com",
                apiSecret = "newsecret",
                insulinName = "Rapid-acting",
                concentration = Concentration.U200,
                loadedUnits = 400.0
            ),
            onNightscoutUrlChange = {},
            onApiSecretChange = {},
            onInsulinNameChange = {},
            onConcentrationChange = {},
            onSaveSettings = {},
            onConfigureNewPatch = { _, _ -> },
            validateNightscoutUrl = { null },
            onNavigateBack = {}
        )
    }
}

