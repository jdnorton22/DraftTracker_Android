# Release 7 - Version 1.6

**Release Date:** February 16, 2026  
**Version Code:** 7  
**Version Name:** 1.6

## What's New

### Player Data Management
- **GitHub-based Player Refresh**: Player data now fetches from GitHub Pages (https://jdnorton22.github.io/fantasy-draft-data/players.json)
- **Config Screen Refresh Button**: Added "Refresh Player Data" button in Config section with warning dialog
- **Proper Draft Reset**: Player refresh now correctly resets current pick information to Round 1, Pick 1

### Injury Status Display
- **Best Available Player**: Injury status now displayed with color-coded badges (OUT/IR: Red, DOUBTFUL: Dark Orange, QUESTIONABLE: Orange/Yellow)
- **Recent Picks Section**: Injury status shown inline next to player names in the 3 most recent picks
- **Color-Coded Indicators**: Consistent injury status colors across all screens

### UI Improvements
- **Compact League Header**: Reduced whitespace and font sizes in the current pick information section
- **"On the Clock" Label**: Changed "Team:" to "On the Clock:" for better clarity
- **Circular Position Badges**: Position labels (WR, RB, QB, TE, DST, K) now displayed in colored circles matching position colors with dark grey text
- **Enhanced Football Icon**: Smaller football with prominent blue outline in the header

### Bug Fixes
- Fixed player refresh to properly reset draft state and current pick information
- Improved layout spacing and alignment throughout the app

## Technical Details

### Build Information
- **APK Size:** 4.9 MB
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Signed:** Yes (release keystore)

### File Location
- Release APK: `app/build/outputs/apk/release/app-release.apk`

## Installation
1. Download the APK from the release folder
2. Enable "Install from Unknown Sources" on your Android device
3. Install the APK
4. Grant necessary permissions when prompted

## Git Tag
This release is tagged as `v1.6` in the repository.

To push the tag to remote:
```bash
git push origin v1.6
```

## Previous Versions
- v1.5 - Release 6
- v1.4 - Release 5
- v1.3 - Release 4
- v1.2 - Release 3
- v1.1 - Release 2
- v1.0 - Release 1
