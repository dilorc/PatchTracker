# DoseBatcher Implementation

## Overview
A pure Kotlin batching engine for dose tracking with a minimal Compose UI demo.

## Part A: Core Batching Engine (No Android Dependencies)

### Implementation: `DoseBatcher.kt`

**Features:**
- ✅ Fixed 5-second inactivity window
- ✅ `registerClick()` - increments count and resets timer
- ✅ `undoClick()` - decrements count (not below zero)
- ✅ Automatic finalization after 5 seconds of inactivity
- ✅ Pure Kotlin - no Android framework dependencies

**State Emissions:**
- `BatchState(clicks, totalUnits, expiresAtMillis, remainingMillis)` - Current batch state
- `FinalDose(timestampMillis, clicks, totalUnits)` - Finalized dose callback

**Configuration:**
- `unitsPerClick`: Default 0.5 units per click
- `inactivityWindowMs`: Default 5000ms (5 seconds)
- `onDoseFinalized`: Callback when dose is finalized

### Unit Tests: `DoseBatcherTest.kt`

**Test Coverage:**
- ✅ Rapid clicks form one dose
- ✅ Separated clicks form multiple doses
- ✅ Undo decrements count correctly
- ✅ Undo does not go below zero
- ✅ Undo all clicks cancels timer
- ✅ Timer expiration works correctly
- ✅ Undo resets timer

**All 10 tests passing** ✅

## Part B: Minimal Compose UI Demo

### Implementation: `MainScreen.kt`

**UI Components:**
1. **Large Click Count Display**
   - 48sp font size
   - Bold weight
   - Primary color

2. **Total Units Display**
   - 36sp font size
   - Medium weight
   - Secondary color

3. **VERY LARGE "CLICK" Button**
   - Full width
   - Minimum height: 120dp
   - 32sp text size
   - Bold text
   - Optimized for fast thumb tapping

4. **"Undo" Button**
   - Full width
   - 72dp height
   - 24sp text size
   - Outlined style

**Layout:**
- Centered vertically and horizontally
- Material 3 design
- Optimized spacing for thumb interaction

### ViewModel: `DoseBatcherViewModel.kt`

**Features:**
- Manages DoseBatcher lifecycle
- Exposes `batchState` as StateFlow
- Logs finalized doses to Logcat
- Proper cleanup in `onCleared()`

### MainActivity Integration

The app is wired up and ready to run:
- `MainActivity.kt` uses the new `MainScreen`
- ViewModel is automatically created via `viewModel()`
- State is collected and displayed reactively

## Acceptance Criteria

✅ **Tapping the large button increments the counter**
- The CLICK button calls `viewModel.registerClick()`
- State updates immediately via StateFlow

✅ **Undo works**
- The Undo button calls `viewModel.undoClick()`
- Count decrements (not below zero)
- Timer resets if clicks remain

✅ **After 5 seconds of inactivity, a finalized dose is logged to Logcat**
- Timer automatically fires after 5 seconds
- Logs: `"Dose finalized: X clicks, Y units at Z"`
- State resets to zero

## Running the App

### Build and Install
```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or use the helper script:
```bash
./build-and-install.sh
```

### Run Tests
```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
./gradlew testDebugUnitTest
```

### View Logcat for Finalized Doses
```bash
adb logcat -s DoseBatcher
```

## Architecture

```
MainActivity
    └── MainScreen (Composable)
        └── DoseBatcherViewModel
            └── DoseBatcher (Pure Kotlin)
                ├── BatchState (StateFlow)
                └── FinalDose (Callback)
```

## Key Design Decisions

1. **Pure Kotlin Core**: DoseBatcher has zero Android dependencies, making it testable with standard JUnit
2. **Coroutines for Timing**: Uses `delay()` in a coroutine scope for the inactivity timer
3. **StateFlow for Reactivity**: UI automatically updates when state changes
4. **Test Time Provider**: Allows virtual time in tests for deterministic behavior
5. **Material 3**: Modern design system with large, tappable buttons

## Files Created

- `app/src/main/java/com/example/patchtracker/DoseBatcher.kt`
- `app/src/main/java/com/example/patchtracker/DoseBatcherViewModel.kt`
- `app/src/main/java/com/example/patchtracker/MainScreen.kt`
- `app/src/test/java/com/example/patchtracker/DoseBatcherTest.kt`

## Files Modified

- `app/build.gradle.kts` - Added ViewModel Compose dependency
- `app/src/main/java/com/example/patchtracker/MainActivity.kt` - Wired up MainScreen

