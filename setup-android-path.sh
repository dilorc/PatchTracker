#!/bin/bash

# This script adds Android SDK tools to your PATH permanently

BASHRC="$HOME/.bashrc"
ZSHRC="$HOME/.zshrc"

ANDROID_EXPORTS='
# Android SDK
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
'

echo "Adding Android SDK to PATH..."

# Detect which shell is being used
CURRENT_SHELL=$(basename "$SHELL")
CONFIG_FILE=""

if [ "$CURRENT_SHELL" = "zsh" ]; then
    CONFIG_FILE="$ZSHRC"
else
    CONFIG_FILE="$BASHRC"
fi

# Create config file if it doesn't exist
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Creating $CONFIG_FILE..."
    touch "$CONFIG_FILE"
    echo "✅ Created $CONFIG_FILE"
fi

# Check if already added
if grep -q "ANDROID_HOME" "$CONFIG_FILE"; then
    echo "Android SDK already in $CONFIG_FILE"
else
    echo "$ANDROID_EXPORTS" >> "$CONFIG_FILE"
    echo "✅ Added to $CONFIG_FILE"
fi

# Also add to the other shell config if it exists
if [ "$CURRENT_SHELL" = "bash" ] && [ -f "$ZSHRC" ]; then
    if ! grep -q "ANDROID_HOME" "$ZSHRC"; then
        echo "$ANDROID_EXPORTS" >> "$ZSHRC"
        echo "✅ Also added to ~/.zshrc"
    fi
elif [ "$CURRENT_SHELL" = "zsh" ] && [ -f "$BASHRC" ]; then
    if ! grep -q "ANDROID_HOME" "$BASHRC"; then
        echo "$ANDROID_EXPORTS" >> "$BASHRC"
        echo "✅ Also added to ~/.bashrc"
    fi
fi

echo ""
echo "To apply changes, run:"
if [ "$CURRENT_SHELL" = "zsh" ]; then
    echo "  source ~/.zshrc"
else
    echo "  source ~/.bashrc"
fi
echo ""
echo "Or open a new terminal."

