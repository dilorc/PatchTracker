#!/bin/bash

# Android SDK paths
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Java home for Gradle
export JAVA_HOME=/home/cdilorenzo/android-studio/jbr

echo "========================================"
echo "PatchTracker - Build and Install"
echo "========================================"
echo ""

# Check if device is connected
echo "Checking for connected devices..."
DEVICES=$($ANDROID_HOME/platform-tools/adb devices | grep -w "device" | wc -l)

if [ $DEVICES -eq 0 ]; then
    echo "❌ No device/emulator found!"
    echo ""
    echo "Please start the emulator first:"
    echo "  ./run-emulator.sh"
    echo ""
    echo "Or check device status:"
    echo "  adb devices"
    exit 1
fi

echo "✅ Device found!"
echo ""

# Build the app
echo "Building app..."
./gradlew assembleDebug --no-daemon

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""

# Install the app
echo "Installing on device..."
$ANDROID_HOME/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Installation successful!"
    echo ""
    echo "To launch the app:"
    echo "  adb shell am start -n com.example.patchtracker/.MainActivity"
    echo ""
    echo "To view logs:"
    echo "  adb logcat -s DoseBatcher"
else
    echo ""
    echo "❌ Installation failed!"
    exit 1
fi

