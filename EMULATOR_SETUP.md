# Android Emulator Setup for Arch Linux

## Quick Start (You're Already Set Up!)

You already have:
- ✅ Android SDK installed at `~/Android/Sdk`
- ✅ Android Studio with JDK at `~/android-studio/jbr`
- ✅ Emulator created: `Medium_Phone_API_36` (Android 15)

You just need to add the SDK tools to your PATH.

## Step 1: Add Android SDK to PATH (One-time setup)

Run this script to permanently add Android tools to your PATH:

```bash
./setup-android-path.sh
source ~/.bashrc
```

Or manually add to `~/.bashrc`:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

## Step 2: Start the Emulator

### Option A: Use the helper script (Recommended)

```bash
./run-emulator.sh
```

This starts the emulator in the background.

### Option B: Manual command

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools
$ANDROID_HOME/emulator/emulator -avd Medium_Phone_API_36 &
```

### Wait for Boot

The emulator takes 30-60 seconds to fully boot. Check status:

```bash
adb devices
```

You should see:
```
List of devices attached
emulator-5554   device
```

If it says `offline`, wait a bit longer.

## Step 3: Install the App

Once the emulator shows `device` status:

```bash
./install-app.sh
```

This will:
1. Build the app (`./gradlew assembleDebug`)
2. Install it on the emulator
3. Show you how to launch it

## Step 4: Launch and Test

### Launch the app:

```bash
adb shell am start -n com.example.patchtracker/.MainActivity
```

### Watch logs for finalized doses:

```bash
adb logcat -s DoseBatcher
```

### Test the app:

1. Tap the large **CLICK** button multiple times
2. Watch the counter increment
3. Tap **Undo** to decrement
4. Wait 5 seconds without tapping
5. Check Logcat - you should see: `Dose finalized: X clicks, Y units at Z`

## Troubleshooting

### "adb: command not found"

Your PATH isn't set. Run:
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Emulator won't start

Check if KVM is enabled (for hardware acceleration):
```bash
ls -l /dev/kvm
```

If it doesn't exist, you may need to enable virtualization in BIOS.

### Emulator is slow

Make sure you have KVM support:
```bash
sudo pacman -S qemu-desktop libvirt
sudo usermod -aG kvm $USER
```

Then log out and back in.

### "No space left on device"

The emulator needs disk space. Check:
```bash
df -h
```

## Alternative: Create a New Emulator

If you want to create a different emulator:

```bash
# List available system images
sdkmanager --list | grep system-images

# Install a system image (if needed)
sdkmanager "system-images;android-35;google_apis;x86_64"

# Create AVD
avdmanager create avd -n MyEmulator -k "system-images;android-35;google_apis;x86_64" -d pixel_6

# List all AVDs
emulator -list-avds

# Start your new emulator
emulator -avd MyEmulator
```

## Using Android Studio GUI (Easiest for Creating Emulators)

1. Open Android Studio
2. Go to **Tools → Device Manager**
3. Click **Create Device**
4. Choose a device (e.g., Pixel 6)
5. Choose a system image (API 35 recommended)
6. Click **Finish**

Then use the scripts above to run it from the command line.

## Summary of Helper Scripts

- `./setup-android-path.sh` - Add Android SDK to PATH permanently
- `./run-emulator.sh` - Start the emulator in background
- `./install-app.sh` - Build and install the app
- `./build-and-install.sh` - Original script (now works if PATH is set)

## Quick Reference

```bash
# Start emulator
./run-emulator.sh

# Check if ready
adb devices

# Install app
./install-app.sh

# Launch app
adb shell am start -n com.example.patchtracker/.MainActivity

# View logs
adb logcat -s DoseBatcher

# Stop emulator
adb emu kill
```

