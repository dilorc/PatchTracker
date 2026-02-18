# Settings Feature Documentation

## Overview
Added comprehensive settings management using DataStore Preferences with a dedicated Settings screen.

## Features Implemented

### 1. Settings Data Model

**Concentration Enum** (`data/Concentration.kt`)
- `U100` - Standard concentration (value: 100)
- `U200` - Double concentration (value: 200)
- `fromValue()` - Safe conversion from integer to enum

**AppSettings Data Class** (`data/AppSettings.kt`)
- `nightscoutUrl: String` - Nightscout instance URL (default: "")
- `apiSecret: String` - API secret for Nightscout (default: "", stored as plain text for MVP)
- `insulinName: String` - Name of insulin type (default: "Rapid-acting")
- `concentration: Concentration` - Insulin concentration (default: U100)
- `effectiveUnitsPerClick: Double` - Computed property based on concentration

**Units Per Click Calculation**
```kotlin
Base units at U100 = 2.0 (constant)
Effective units = 2.0 * (concentration / 100.0)

U100: 2.0 * (100 / 100.0) = 2.0 units per click
U200: 2.0 * (200 / 100.0) = 4.0 units per click
```

### 2. DataStore Repository

**SettingsRepository** (`data/SettingsRepository.kt`)
- Manages persistent storage using DataStore Preferences
- Exposes `settingsFlow: Flow<AppSettings>` for reactive updates
- Individual update methods:
  - `updateNightscoutUrl(url: String)`
  - `updateApiSecret(secret: String)` - **Note: Does not log secret**
  - `updateInsulinName(name: String)`
  - `updateConcentration(concentration: Concentration)`
- Batch update: `updateSettings(settings: AppSettings)`

### 3. Settings ViewModel

**SettingsViewModel** (`ui/SettingsViewModel.kt`)
- Extends `AndroidViewModel` for Context access
- Exposes `settings: StateFlow<AppSettings>`
- Provides update methods that delegate to repository
- Manages coroutine scope via `viewModelScope`

### 4. Settings UI Screen

**SettingsScreen** (`ui/SettingsScreen.kt`)

**Components:**
1. **Top App Bar** with back navigation
2. **Nightscout URL Field**
   - OutlinedTextField
   - Keyboard type: URI
   - Placeholder: "https://your-nightscout.herokuapp.com"

3. **API Secret Field**
   - OutlinedTextField with password masking
   - Show/Hide toggle button
   - Visual transformation for security

4. **Insulin Name Field**
   - OutlinedTextField
   - Default: "Rapid-acting"

5. **Concentration Selector**
   - Radio buttons for U100 and U200
   - Clear visual selection

6. **Effective Units Per Click Card**
   - Displays calculated units per click
   - Shows base value (2.0 units at U100)
   - Updates reactively when concentration changes

### 5. Updated Main Screen

**MainScreen** (`MainScreen.kt`)

**New Features:**
- Settings icon button in top app bar
- Units per click info card at top
- Shows current effective units per click
- All existing functionality preserved (CLICK button, Undo, counters)

### 6. Updated DoseBatcherViewModel

**DoseBatcherViewModel** (`DoseBatcherViewModel.kt`)

**Changes:**
- Now extends `AndroidViewModel` for Context access
- Integrates with `SettingsRepository`
- Exposes `unitsPerClick: StateFlow<Double>` from settings
- Recreates `DoseBatcher` when settings change
- Uses effective units per click from settings

### 7. Navigation

**MainActivity** (`MainActivity.kt`)

**Navigation Setup:**
- Uses Jetpack Compose Navigation
- Two routes:
  - `"main"` - Main screen with dose tracking
  - `"settings"` - Settings screen
- Settings button navigates to settings
- Back button returns to main screen

## Testing

### Unit Tests

**AppSettingsTest** (13 tests)
- Default values validation
- U100 calculation (2.0 units)
- U200 calculation (4.0 units)
- effectiveUnitsPerClick property
- Base constant verification
- Formula correctness

**ConcentrationTest** (7 tests)
- Enum values (U100=100, U200=200)
- fromValue() conversion
- Invalid value handling (defaults to U100)
- Enum entries completeness

**All 24 tests passing** ✅

## Usage Flow

### First Launch
1. App opens to Main screen
2. Default settings: U100, 2.0 units per click
3. Tap Settings icon to configure

### Configure Settings
1. Tap Settings icon (⚙️) in top app bar
2. Enter Nightscout URL
3. Enter API secret (masked)
4. Set insulin name
5. Select concentration (U100 or U200)
6. See effective units per click update
7. Tap back arrow to return to main screen

### Track Doses
1. Main screen shows current units per click
2. Tap CLICK button (increments by effective units)
3. Watch total units update
4. Tap Undo if needed
5. After 5 seconds, dose finalizes to Logcat

## Data Persistence

- All settings stored in DataStore Preferences
- Survives app restarts
- Reactive updates across app
- No manual save button needed (auto-saves on change)

## Security Note

⚠️ **API Secret Storage**: For MVP, the API secret is stored as plain text in DataStore. The code explicitly does not log this value. For production, consider:
- Encrypted DataStore
- Android Keystore
- Secure credential management

## Files Created

```
app/src/main/java/com/example/patchtracker/
├── data/
│   ├── Concentration.kt
│   ├── AppSettings.kt
│   └── SettingsRepository.kt
└── ui/
    ├── SettingsViewModel.kt
    └── SettingsScreen.kt

app/src/test/java/com/example/patchtracker/
├── AppSettingsTest.kt
└── ConcentrationTest.kt
```

## Files Modified

```
app/build.gradle.kts - Added navigation-compose dependency
app/src/main/java/com/example/patchtracker/
├── MainActivity.kt - Added navigation
├── MainScreen.kt - Added settings button and units display
└── DoseBatcherViewModel.kt - Integrated settings
```

## Dependencies Added

```kotlin
implementation("androidx.navigation:navigation-compose:2.8.5")
```

(DataStore was already included from initial setup)

