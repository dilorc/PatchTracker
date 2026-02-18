#!/bin/bash

# Android SDK paths
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

echo "Starting Android Emulator..."
echo "Emulator: Medium_Phone_API_36"
echo ""
echo "The emulator will run in the background."
echo "To install the app after the emulator starts, run:"
echo "  ./install-app.sh"
echo ""

# Start emulator in background
$ANDROID_HOME/emulator/emulator -avd Medium_Phone_API_36 -accel on &

echo "Emulator starting... (PID: $!)"
echo "Wait 30-60 seconds for it to fully boot."
echo ""
echo "To check if it's ready, run:"
echo "  adb devices"
echo ""
echo "When you see 'device' (not 'offline'), run:"
echo "  ./install-app.sh"

