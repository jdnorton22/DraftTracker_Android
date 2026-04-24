# Fantasy Draft Picker - Screenshot Guide

## Emulator Status

✅ **Emulator Running**: Medium_Phone_API_36.1 (emulator-5554)
✅ **App Installed**: Fantasy Draft Picker v1.1 (API 35)
✅ **App Launched**: Ready for screenshots

## Taking Screenshots

### Method 1: Using ADB (Command Line)

Take a screenshot and save it to your computer:

```bash
# Take screenshot and save to workspace
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > screenshot.png

# Take screenshot with custom name
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > splash_screen.png

# Take screenshot to specific location
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > C:\Screenshots\app_main.png
```

### Method 2: Using Emulator Controls

1. Look for the emulator window toolbar on the right side
2. Click the **camera icon** (📷) to take a screenshot
3. Screenshots are saved to: `C:\Users\YourName\Pictures\Screenshots\`

### Method 3: Using Android Studio

If you have Android Studio open:
1. Go to **View** → **Tool Windows** → **Logcat**
2. Click the **camera icon** in the device toolbar
3. Choose save location

## Recommended Screenshots for App Store

### 1. Splash Screen
- Shows app branding and icon
- First impression for users

### 2. Draft Configuration
- Shows team setup and draft settings
- Demonstrates customization options

### 3. Player Selection (Main Screen)
- Shows player list with rankings
- Demonstrates core functionality
- Include filters and search

### 4. Draft History
- Shows drafted players
- Demonstrates tracking features
- Include undo functionality

### 5. Player Details
- Shows detailed player stats
- Demonstrates data richness

## Screenshot Naming Convention

Suggested naming for organization:

```
01_splash_screen.png
02_draft_config.png
03_player_selection_main.png
04_player_selection_filtered.png
05_draft_history.png
06_player_details.png
07_custom_player.png
08_export_draft.png
```

## Quick Screenshot Script

Create a batch file to take multiple screenshots:

```batch
@echo off
echo Taking Fantasy Draft Picker Screenshots...
echo.

echo 1. Splash Screen (wait for it to appear)
pause
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > 01_splash_screen.png

echo 2. Draft Configuration
pause
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > 02_draft_config.png

echo 3. Player Selection Main
pause
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > 03_player_selection.png

echo 4. Draft History
pause
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 exec-out screencap -p > 04_draft_history.png

echo.
echo ✓ Screenshots saved!
pause
```

## Screenshot Specifications

### Google Play Store Requirements
- **Minimum**: 320px
- **Maximum**: 3840px
- **Recommended**: 1080 x 1920 (portrait) or 1920 x 1080 (landscape)
- **Format**: PNG or JPEG
- **Aspect Ratio**: 16:9 or 9:16

### Current Emulator Resolution
- **Resolution**: 1080 x 2400 pixels
- **DPI**: 420
- **Aspect Ratio**: 9:20 (perfect for app stores)

## Editing Screenshots

### Add Device Frame
Use Google's Device Art Generator:
- https://developer.android.com/distribute/marketing-tools/device-art-generator

### Add Captions
Recommended tools:
- **Figma** (free, web-based)
- **Canva** (free templates)
- **Photoshop** (professional)

### Screenshot Captions Ideas
1. "Draft with Confidence - Real-time ADP Rankings"
2. "Track Your Picks - Complete Draft History"
3. "Customize Your Draft - Flexible Team Setup"
4. "Player Stats at Your Fingertips"
5. "Export and Share Your Draft Results"

## Navigating the App for Screenshots

### To Get to Configuration Screen:
```bash
# From main screen, tap the settings/config button
# Or restart the app if it's first launch
```

### To Get to Draft History:
```bash
# Draft a few players first
# Then tap "Draft History" button
```

### To Show Player Details:
```bash
# Tap on any player in the list
# Shows detailed stats and rankings
```

## Stopping the Emulator

When done taking screenshots:

```bash
# Stop the emulator gracefully
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 emu kill
```

Or close the emulator window.

## Troubleshooting

### Screenshot is Black
- Wait a few seconds after navigating to a screen
- Ensure the app is in the foreground

### Screenshot Command Fails
- Check emulator is still running: `adb devices`
- Ensure correct device ID: `emulator-5554`

### App Crashed
- Restart the app:
```bash
C:\Android\Sdk\platform-tools\adb.exe -s emulator-5554 shell am start -n com.fantasydraft.picker/.ui.SplashActivity
```

---

**Emulator**: Medium_Phone_API_36.1
**Device ID**: emulator-5554
**App Version**: 1.1 (API 35)
**Resolution**: 1080 x 2400 pixels
**Status**: ✅ Ready for screenshots
