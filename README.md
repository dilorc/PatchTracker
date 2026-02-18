# PatchTracker

An Android application built with Kotlin, Jetpack Compose, and modern Android development practices.

## Project Configuration

- **Language**: Kotlin
- **Build System**: Gradle (Kotlin DSL)
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

## Features & Dependencies

### UI Framework
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design components
- **Navigation Compose**: Type-safe navigation between screens

### Architecture & Data
- **DataStore Preferences**: Type-safe data storage for app settings
- **Room**: SQLite database with compile-time verification
- **WorkManager**: Background task scheduling

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with logging interceptor
- **Kotlinx Serialization**: JSON serialization/deserialization

### Widgets
- **Jetpack Glance**: Modern app widget framework with Material 3 support

## App Features

### Dose Tracking
- **DoseBatcher**: Pure Kotlin batching engine for tracking insulin doses
- Large tap-optimized CLICK button for fast dose entry
- Undo functionality
- 5-second inactivity window for automatic dose finalization
- Real-time display of clicks and total units

### Settings Management
- **Nightscout Integration**: Configure Nightscout URL and API secret
- **Insulin Configuration**: Set insulin name and concentration (U100/U200)
- **Dynamic Units Calculation**: Automatically adjusts units-per-click based on concentration
  - U100: 2.0 units per click
  - U200: 4.0 units per click
- **Persistent Storage**: All settings saved using DataStore Preferences

See [SETTINGS_FEATURE.md](SETTINGS_FEATURE.md) for detailed settings documentation.

## Building the Project

### Prerequisites
- JDK 17 or higher
- Android SDK with API level 35
- Android device or emulator with API level 26+

**Important**: Set the `JAVA_HOME` environment variable before running Gradle commands:
```bash
export JAVA_HOME=/path/to/your/jdk
# Example: export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
```

Or add it to your `~/.bashrc` or `~/.zshrc` for persistence.

### Build Commands

#### Build the app
```bash
./gradlew build
```

#### Build debug APK
```bash
./gradlew assembleDebug
```

#### Build release APK
```bash
./gradlew assembleRelease
```

## Testing

### Run unit tests
Execute all local unit tests on the JVM:
```bash
./gradlew test
```

### Run instrumented tests (optional)
Execute instrumented tests on a connected device or emulator:
```bash
./gradlew connectedCheck
```

**Note**: For `connectedCheck`, ensure you have:
- A physical device connected via USB with USB debugging enabled, OR
- An Android emulator running

### Test Reports
After running tests, reports are available at:
- Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented tests: `app/build/reports/androidTests/connected/index.html`

## Running the App

### First Time Setup: Configure Android SDK PATH

If you get `adb: command not found`, run this once:

```bash
./setup-android-path.sh
source ~/.bashrc
```

See [EMULATOR_SETUP.md](EMULATOR_SETUP.md) for detailed emulator setup instructions for Arch Linux.

### Start the Emulator

```bash
./run-emulator.sh
```

Wait 30-60 seconds for it to boot, then check:

```bash
adb devices
```

You should see `emulator-5554   device` (not `offline`).

### Install the App

#### Option 1: Use the new install script (recommended)

```bash
./install-app.sh
```

This script will:
1. Check if a device/emulator is connected
2. Set JAVA_HOME automatically
3. Build the debug APK
4. Install it on the device/emulator

#### Option 2: Use the original build-and-install script

```bash
./build-and-install.sh
```

This script will:
1. Set JAVA_HOME automatically
2. Build the debug APK
3. Install it on a connected device/emulator
4. Launch the app

### From Command Line (Manual)

1. Build and install the debug APK:
```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr  # Adjust path as needed
./gradlew installDebug
```

2. Or build and install in one step:
```bash
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr  # Adjust path as needed
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
```

3. Launch the app:
```bash
adb shell am start -n com.example.patchtracker/.MainActivity
```

### From VS Code

1. Install the "Android for VS Code" extension
2. Open the project in VS Code
3. Use the Android extension to build and deploy to device/emulator

Alternatively, use the integrated terminal:
```bash
./gradlew installDebug
```

## CI/CD Tasks

### Continuous Integration Commands

```bash
# Run all checks (lint, test, build)
./gradlew check

# Run unit tests only
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedCheck

# Clean build artifacts
./gradlew clean

# Full clean build with tests
./gradlew clean build test
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/patchtracker/
│   │   │   ├── MainActivity.kt
│   │   │   └── ui/theme/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   ├── test/                    # Unit tests
│   └── androidTest/             # Instrumented tests
└── build.gradle.kts
```

## License

This project is a template for Android development.

