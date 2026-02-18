# Settings UI Guide

## Main Screen (Updated)

```
┌─────────────────────────────────────┐
│ PatchTracker              ⚙️        │  ← Settings icon
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Units per Click           │   │  ← Info card
│  │        2.0                  │   │  ← Current units/click
│  └─────────────────────────────┘   │
│                                     │
│          Clicks: 5                  │  ← 48sp, Bold
│                                     │
│      Total Units: 10.0              │  ← 36sp, Medium
│                                     │
│  ┌─────────────────────────────┐   │
│  │          CLICK              │   │  ← 120dp min height
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │          Undo               │   │  ← 72dp height
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

## Settings Screen

```
┌─────────────────────────────────────┐
│ ← Settings                          │  ← Back button
├─────────────────────────────────────┤
│                                     │
│  Nightscout URL                     │
│  ┌─────────────────────────────┐   │
│  │ https://example.com         │   │  ← URL input
│  └─────────────────────────────┘   │
│                                     │
│  API Secret                         │
│  ┌─────────────────────────────┐   │
│  │ ••••••••••••      [Show]    │   │  ← Password field
│  └─────────────────────────────┘   │
│                                     │
│  Insulin Name                       │
│  ┌─────────────────────────────┐   │
│  │ Rapid-acting                │   │  ← Text input
│  └─────────────────────────────┘   │
│                                     │
│  Concentration                      │
│  ○ U100                             │  ← Radio buttons
│  ● U200                             │  ← Selected
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Effective Units Per Click   │   │  ← Info card
│  │         4.0 units           │   │  ← Calculated
│  │ Base: 2.0 units at U100     │   │  ← Helper text
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

## Navigation Flow

```
Main Screen
    │
    │ Tap ⚙️ icon
    ▼
Settings Screen
    │
    │ Tap ← back
    ▼
Main Screen (with updated settings)
```

## Settings → Main Screen Data Flow

```
User changes concentration to U200
    │
    ▼
SettingsRepository updates DataStore
    │
    ▼
SettingsViewModel emits new settings
    │
    ▼
DoseBatcherViewModel receives update
    │
    ├─► Recreates DoseBatcher with new units (4.0)
    │
    └─► Updates unitsPerClick StateFlow
        │
        ▼
    MainScreen displays "Units per Click: 4.0"
        │
        ▼
    User taps CLICK
        │
        ▼
    Increments by 4.0 units (not 2.0)
```

## Example Scenarios

### Scenario 1: U100 Concentration
```
Settings:
- Concentration: U100
- Effective units: 2.0

Main Screen:
- Units per Click: 2.0
- Tap CLICK 5 times
- Total Units: 10.0
```

### Scenario 2: U200 Concentration
```
Settings:
- Concentration: U200
- Effective units: 4.0

Main Screen:
- Units per Click: 4.0
- Tap CLICK 5 times
- Total Units: 20.0
```

### Scenario 3: Switching Concentration
```
Initial State (U100):
- 3 clicks = 6.0 units

User changes to U200:
- DoseBatcher recreates
- Previous batch lost (by design)
- New clicks = 4.0 units each

After 2 more clicks:
- Total: 8.0 units (2 × 4.0)
```

## Field Validation

### Nightscout URL
- No validation in MVP
- Accepts any string
- Keyboard type: URI for convenience

### API Secret
- No validation in MVP
- Masked by default
- Show/Hide toggle available
- **Not logged anywhere**

### Insulin Name
- No validation in MVP
- Accepts any string
- Default: "Rapid-acting"

### Concentration
- Radio button selection
- Only U100 or U200 allowed
- Default: U100

## Accessibility Features

- Large touch targets (radio buttons)
- Clear labels for all fields
- Password masking with toggle
- High contrast Material 3 colors
- Readable font sizes (14sp - 24sp)

## Persistence

All settings are automatically saved to DataStore when changed:
- No "Save" button needed
- Changes apply immediately
- Survives app restarts
- Survives app updates (same storage key)

## Testing the Settings

### Manual Test Steps

1. **Launch app** → Should show U100, 2.0 units per click
2. **Tap Settings icon** → Settings screen opens
3. **Enter Nightscout URL** → Auto-saves
4. **Enter API secret** → Masked, auto-saves
5. **Change insulin name** → Auto-saves
6. **Select U200** → Units per click shows 4.0
7. **Tap back** → Main screen shows 4.0 units per click
8. **Tap CLICK** → Increments by 4.0
9. **Close app and reopen** → Settings persist

### Logcat Verification

```bash
adb logcat -s DoseBatcher
```

Expected output after 5 seconds:
```
D/DoseBatcher: Dose finalized: 1 clicks, 4.0 units at 1234567890
```

(Note: API secret is never logged)

