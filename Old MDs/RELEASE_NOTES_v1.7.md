# Release 8 - Version 1.7

**Release Date:** February 16, 2026  
**Version Code:** 8  
**Version Name:** 1.7

## What's New in This Release

This release builds upon v1.6 with all previous features included and verified.

### All Features from v1.6
- GitHub-based player data refresh from https://jdnorton22.github.io/fantasy-draft-data/players.json
- Refresh Player Data button in Config section with warning dialog
- Injury status display throughout the app (Best Available, Recent Picks)
- Color-coded injury indicators (OUT/IR: Red, DOUBTFUL: Dark Orange, QUESTIONABLE: Orange/Yellow)
- Compact league header with reduced whitespace
- "On the Clock:" label for current team
- Circular position badges with position-specific colors
- Enhanced football icon with blue outline in header
- Proper draft reset on player refresh

### Verified Functionality
- ✅ Player data refresh correctly resets draft state
- ✅ Current pick information properly resets to Round 1, Pick 1
- ✅ All injury status displays working correctly
- ✅ Position badges displaying with correct colors
- ✅ UI improvements stable and consistent

## Technical Details

### Build Information
- **APK Size:** 4.9 MB
- **AAB Size:** 4.2 MB
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Signed:** Yes (release keystore)

### File Locations
- Release APK: `app/build/outputs/apk/release/app-release.apk`
- Release Bundle (AAB): `app/build/outputs/bundle/release/app-release.aab`

## Distribution

### Google Play Store
Use the Android App Bundle (AAB) for Play Store distribution:
- File: `app-release.aab`
- Size: 4.2 MB
- Optimized for Play Store's dynamic delivery

### Direct Distribution
Use the APK for direct distribution to users:
- File: `app-release.apk`
- Size: 4.9 MB
- Can be installed directly on Android devices

## Installation

### From APK (Direct Install)
1. Download the APK from the release folder
2. Enable "Install from Unknown Sources" on your Android device
3. Install the APK
4. Grant necessary permissions when prompted

### From Play Store (AAB)
1. Upload the AAB to Google Play Console
2. Complete the store listing
3. Submit for review
4. Users can install from Play Store once approved

## Git Tag
This release is tagged as `v1.7` in the repository.

To push the tag to remote:
```bash
git push origin v1.7
```

## Version History
- v1.7 - Release 8 (Current)
- v1.6 - Release 7
- v1.5 - Release 6
- v1.4 - Release 5
- v1.3 - Release 4
- v1.2 - Release 3
- v1.1 - Release 2
- v1.0 - Release 1
