#!/bin/bash
# Helper script to build and install the app on a connected device/emulator

# Set JAVA_HOME if not already set
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME=/home/cdilorenzo/android-studio/jbr
fi

echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful! Installing on device..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "Installation successful!"
        echo "Launching app..."
        adb shell am start -n com.example.patchtracker/.MainActivity
    else
        echo "Installation failed. Make sure a device/emulator is connected."
        echo "Run 'adb devices' to check connected devices."
    fi
else
    echo "Build failed!"
    exit 1
fi

