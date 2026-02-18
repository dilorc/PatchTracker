# Patch Configuration Feature

## Overview
Added "New Patch" functionality to configure insulin patches with concentration type and loaded units, with real-time remaining units tracking.

## Features Implemented

### 1. Patch Configuration Data Model

**Updated AppSettings** (`data/AppSettings.kt`)
- Added `loadedUnits: Double` field (default: 0.0)
- New constants and calculations:
  - `INITIAL_DEDUCTION_CLICKS = 10` - Fixed deduction when patch is loaded
  - `calculateMaxLoadedUnits(concentration)` - Max units for concentration
  - `calculateInitialRemainingUnits(loadedUnits, concentration)` - Initial remaining after deduction

**Maximum Units Formula:**
```kotlin
Max units = 2 * concentration value
U100: 2 * 100 = 200 units
U200: 2 * 200 = 400 units
```

**Initial Remaining Units Formula:**
```kotlin
Initial deduction = 10 clicks * units-per-click
Remaining = loaded units - initial deduction

U100 (200 units loaded):
  Initial deduction = 10 * 2.0 = 20 units
  Remaining = 200 - 20 = 180 units

U200 (400 units loaded):
  Initial deduction = 10 * 4.0 = 40 units
  Remaining = 400 - 40 = 360 units
```

### 2. New Patch Dialog

**NewPatchDialog** (`ui/NewPatchDialog.kt`)

**Components:**
- Concentration selector (U100/U200 radio buttons)
- Units loaded input field (numeric keyboard)
- Dynamic maximum units display
- Validation:
  - Must be a valid number
  - Must be greater than 0
  - Cannot exceed maximum for selected concentration
- Confirm/Cancel buttons

**Validation Examples:**
```
U100 selected:
  ✓ 200 units - Valid (at max)
  ✓ 150 units - Valid
  ✗ 250 units - Error: "Maximum 200 units for U100"
  ✗ 0 units - Error: "Units must be greater than 0"
  ✗ "abc" - Error: "Please enter a valid number"

U200 selected:
  ✓ 400 units - Valid (at max)
  ✓ 300 units - Valid
  ✗ 500 units - Error: "Maximum 400 units for U200"
```

### 3. Updated Settings Screen

**New Elements:**
1. **"New Patch" Button** - Opens configuration dialog
2. **Current Patch Info Card** - Shows:
   - Concentration type (U100/U200)
   - Loaded units
   - Initial remaining units
   - Only visible when patch is configured

**Updated SettingsRepository:**
- `updateLoadedUnits(units)` - Update loaded units
- `configureNewPatch(concentration, units)` - Atomic update of both

**Updated SettingsViewModel:**
- `configureNewPatch(concentration, units)` - Trigger patch configuration

### 4. Remaining Units Tracking

**DoseBatcherViewModel Updates:**
- New `remainingUnits: StateFlow<Double>` exposed to UI
- Calculation: `initialRemainingUnits - totalUnits`
- Updates in real-time as clicks are registered
- Updates when undo is pressed
- Resets when settings change (new patch configured)

**Formula:**
```kotlin
Remaining = (loadedUnits - initialDeduction) - totalUnits

Example U100 with 200 units loaded:
  Initial: 180 units (200 - 20)
  After 5 clicks (10 units): 170 units (180 - 10)
  After undo (8 units): 172 units (180 - 8)
```

### 5. Updated Main Screen

**New Display:**
- **Remaining Units Card** - Shows current remaining units
  - Large, prominent display (32sp)
  - Only visible when patch is configured (remainingUnits > 0)
  - Updates in real-time with clicks and undo
  - Color: Primary container (Material 3)

**Layout Order:**
1. Units per Click card (top)
2. Clicks counter
3. Total Units
4. Remaining Units card (if patch configured)
5. CLICK button
6. Undo button

## User Flow

### Configure New Patch

1. **Open Settings** → Tap ⚙️ icon
2. **Tap "New Patch" button**
3. **Select concentration** → U100 or U200
4. **Enter loaded units** → e.g., 200 for U100
5. **See max units** → "Maximum: 200 units"
6. **Tap Confirm**
7. **See current patch info** → "U100: 200 units loaded, Initial remaining: 180 units"
8. **Return to main screen**

### Track Usage

1. **Main screen shows:**
   - Units per Click: 2.0
   - Clicks: 0
   - Total Units: 0.0
   - Remaining Units: 180.0

2. **Tap CLICK 5 times:**
   - Clicks: 5
   - Total Units: 10.0
   - Remaining Units: 170.0 (180 - 10)

3. **Tap Undo:**
   - Clicks: 4
   - Total Units: 8.0
   - Remaining Units: 172.0 (180 - 8)

4. **Wait 5 seconds:**
   - Dose finalizes
   - Counters reset
   - Remaining Units: 172.0 (persists)

### Change Patch Configuration

1. **Open Settings**
2. **Tap "New Patch"**
3. **Select U200**
4. **Enter 400 units**
5. **Confirm**
6. **Main screen updates:**
   - Units per Click: 4.0
   - Remaining Units: 360.0 (400 - 40)
   - Previous batch cleared

## Data Persistence

**Stored in DataStore:**
- `concentration` - Current patch type
- `loadedUnits` - Total units loaded
- Survives app restarts
- Atomic updates when configuring new patch

**Not Stored:**
- Current batch state (clicks, total units)
- Resets when app restarts or settings change

## Testing

### Unit Tests

**PatchConfigurationTest** (11 tests)
- Max units calculation (U100=200, U200=400)
- Initial remaining calculation
- Formula verification
- AppSettings property validation

**All 35 tests passing** ✅
- 10 DoseBatcher tests
- 7 AppSettings tests
- 7 Concentration tests
- 11 PatchConfiguration tests

### Manual Testing Checklist

**U100 Configuration:**
- [ ] Open Settings → Tap "New Patch"
- [ ] U100 selected by default
- [ ] Enter 200 units → No error
- [ ] Enter 201 units → Error: "Maximum 200 units for U100"
- [ ] Enter 200 → Confirm
- [ ] See "Current Patch: U100: 200 units loaded"
- [ ] See "Initial remaining: 180 units"
- [ ] Main screen shows "Remaining Units: 180.0"

**U200 Configuration:**
- [ ] Tap "New Patch"
- [ ] Select U200
- [ ] Max shows "Maximum: 400 units"
- [ ] Enter 400 → Confirm
- [ ] See "Current Patch: U200: 400 units loaded"
- [ ] See "Initial remaining: 360 units"
- [ ] Main screen shows "Remaining Units: 360.0"
- [ ] Units per Click: 4.0

**Real-time Updates:**
- [ ] Configure U100 with 200 units
- [ ] Remaining: 180.0
- [ ] Tap CLICK → Remaining: 178.0
- [ ] Tap CLICK 4 more times → Remaining: 170.0
- [ ] Tap Undo → Remaining: 172.0
- [ ] Wait 5 seconds → Dose finalizes
- [ ] Remaining still: 172.0 (persists)

**Validation:**
- [ ] Enter "abc" → Error
- [ ] Enter 0 → Error
- [ ] Enter negative → Error
- [ ] Enter valid number → No error

## Files Created

```
app/src/main/java/com/example/patchtracker/ui/
└── NewPatchDialog.kt

app/src/test/java/com/example/patchtracker/
└── PatchConfigurationTest.kt
```

## Files Modified

```
app/src/main/java/com/example/patchtracker/
├── data/
│   ├── AppSettings.kt - Added loadedUnits, max units, initial remaining
│   └── SettingsRepository.kt - Added configureNewPatch method
├── ui/
│   ├── SettingsViewModel.kt - Added configureNewPatch
│   └── SettingsScreen.kt - Added New Patch button and dialog
├── DoseBatcherViewModel.kt - Added remaining units tracking
└── MainScreen.kt - Added remaining units display
```

## Formulas Summary

```kotlin
// Maximum units
maxUnits = 2 * concentration
  U100: 2 * 100 = 200
  U200: 2 * 200 = 400

// Units per click
unitsPerClick = 2.0 * (concentration / 100.0)
  U100: 2.0 * 1.0 = 2.0
  U200: 2.0 * 2.0 = 4.0

// Initial deduction
initialDeduction = 10 * unitsPerClick
  U100: 10 * 2.0 = 20
  U200: 10 * 4.0 = 40

// Initial remaining
initialRemaining = loadedUnits - initialDeduction
  U100 (200): 200 - 20 = 180
  U200 (400): 400 - 40 = 360

// Current remaining
currentRemaining = initialRemaining - totalUnits
  After 5 clicks (U100): 180 - 10 = 170
  After undo to 4 (U100): 180 - 8 = 172
```

