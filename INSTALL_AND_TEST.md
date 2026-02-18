# Installation and Testing Guide

## Quick Start

### 1. Start the Emulator

```bash
./run-emulator.sh
```

Wait 30-60 seconds for the emulator to boot. Verify it's ready:

```bash
adb devices
```

Expected output:
```
List of devices attached
emulator-5554   device
```

### 2. Build and Install

```bash
./install-app.sh
```

This will:
- Build the debug APK
- Install it on the emulator
- Launch the app automatically

### 3. Watch Logs

In a separate terminal, monitor dose finalization:

```bash
adb logcat -s DoseBatcher
```

## Manual Testing Checklist

### Main Screen - Default Behavior (U100)

- [ ] App launches to main screen
- [ ] Units per Click card shows "2.0"
- [ ] Clicks counter shows "0"
- [ ] Total Units shows "0.0"
- [ ] Tap CLICK button → Clicks: 1, Total: 2.0
- [ ] Tap CLICK 4 more times → Clicks: 5, Total: 10.0
- [ ] Tap Undo → Clicks: 4, Total: 8.0
- [ ] Wait 5 seconds → Logcat shows: "Dose finalized: 4 clicks, 8.0 units"
- [ ] Counters reset to 0

### Settings Screen - Navigation

- [ ] Tap Settings icon (⚙️) in top app bar
- [ ] Settings screen opens
- [ ] All fields visible:
  - Nightscout URL
  - API Secret (masked)
  - Insulin Name
  - Concentration (U100 selected by default)
  - Effective Units Per Click card shows "2.0 units"

### Settings Screen - Field Interactions

- [ ] Enter Nightscout URL: `https://test.herokuapp.com`
- [ ] Enter API Secret: `test-secret-123`
- [ ] Verify API secret is masked (shows ••••••••••••)
- [ ] Tap Show button → Secret becomes visible
- [ ] Tap Hide button → Secret masked again
- [ ] Change Insulin Name to: `Humalog`
- [ ] Select U200 radio button
- [ ] Effective Units Per Click card updates to "4.0 units"

### Settings Screen - Back Navigation

- [ ] Tap back arrow (←)
- [ ] Returns to main screen
- [ ] Units per Click card now shows "4.0"
- [ ] Counters still at 0 (previous batch was finalized)

### Main Screen - U200 Behavior

- [ ] Tap CLICK button → Clicks: 1, Total: 4.0
- [ ] Tap CLICK 2 more times → Clicks: 3, Total: 12.0
- [ ] Tap Undo → Clicks: 2, Total: 8.0
- [ ] Wait 5 seconds → Logcat shows: "Dose finalized: 2 clicks, 8.0 units"

### Settings Persistence

- [ ] Close app (swipe away from recent apps)
- [ ] Reopen app
- [ ] Units per Click still shows "4.0"
- [ ] Tap Settings icon
- [ ] Verify all settings persisted:
  - Nightscout URL: `https://test.herokuapp.com`
  - API Secret: `test-secret-123` (masked)
  - Insulin Name: `Humalog`
  - Concentration: U200 selected

### Undo Edge Cases

- [ ] Tap CLICK once → Clicks: 1
- [ ] Tap Undo → Clicks: 0
- [ ] Tap Undo again → Clicks: 0 (doesn't go negative)
- [ ] Wait 5 seconds → No dose finalized (0 clicks)

### Rapid Tapping

- [ ] Tap CLICK rapidly 10 times
- [ ] All clicks register
- [ ] Total Units = 40.0 (10 × 4.0)
- [ ] Wait 5 seconds → Dose finalizes with 10 clicks

## Unit Tests

Run all unit tests:

```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
./gradlew testDebugUnitTest --no-daemon
```

Expected: **24 tests passing**

Test breakdown:
- DoseBatcherTest: 10 tests
- AppSettingsTest: 7 tests
- ConcentrationTest: 7 tests

## Build Verification

Build the release APK:

```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
./gradlew assembleDebug --no-daemon
```

Expected: `BUILD SUCCESSFUL`

APK location: `app/build/outputs/apk/debug/app-debug.apk`

## Logcat Filtering

### View only DoseBatcher logs:
```bash
adb logcat -s DoseBatcher
```

### View all app logs:
```bash
adb logcat | grep com.example.patchtracker
```

### Clear logcat before testing:
```bash
adb logcat -c
```

## Troubleshooting

### Emulator won't start
```bash
# Check available emulators
$ANDROID_HOME/emulator/emulator -list-avds

# Start specific emulator
$ANDROID_HOME/emulator/emulator -avd Medium_Phone_API_36 &
```

### App won't install
```bash
# Uninstall old version
adb uninstall com.example.patchtracker

# Reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Settings not persisting
```bash
# Clear app data
adb shell pm clear com.example.patchtracker

# Reinstall app
./install-app.sh
```

### Build fails
```bash
# Clean build
./gradlew clean

# Rebuild
./gradlew assembleDebug --no-daemon
```

## Performance Notes

- **Emulator boot time**: 30-60 seconds
- **Build time (clean)**: ~20-30 seconds
- **Build time (incremental)**: ~5-10 seconds
- **Test execution**: ~5 seconds
- **App launch time**: < 2 seconds

## Next Steps

After verifying all functionality:

1. Configure real Nightscout URL in settings
2. Test with actual insulin tracking workflow
3. Monitor Logcat for finalized doses
4. Verify dose calculations match expected values

## Known Limitations (MVP)

- API secret stored as plain text (not encrypted)
- No Nightscout URL validation
- No network connectivity yet (Retrofit configured but not used)
- Changing concentration resets current batch
- No dose history UI (only Logcat)

