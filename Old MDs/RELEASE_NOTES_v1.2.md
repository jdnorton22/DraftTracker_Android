# Fantasy Draft Picker - Version 1.2 Release Notes

## Release Date
February 4, 2026

## Version Information
- Version Code: 3
- Version Name: 1.2
- Target SDK: 35 (Android 12+)
- Minimum SDK: 24 (Android 7.0+)

## What's New in Version 1.2

### UI Improvements
1. **Dark Theme Text Visibility**
   - Fixed text color in draft history items to be visible on colored backgrounds
   - Draft history on main screen now uses dark grey (#333333) for better readability
   - "Draft History" title follows system theme (white in dark mode, black in light mode)

2. **Draft History Sorting**
   - Added sort functionality to draft history page
   - Default sort: Descending (most recent picks first)
   - Toggle button to switch between ascending/descending order
   - Sorting persists when filtering by team

3. **Best Available Player Button**
   - Simplified button text from "Recommended Tap to Draft" to just "Tap to Draft"
   - Cleaner, more concise interface

4. **Draft Pick Confirmation**
   - Added confirmation dialog when selecting a player to draft
   - Prevents accidental picks
   - Shows player name and position before confirming

5. **Layout Refinements**
   - Removed extra spacing above League Configuration section
   - Improved ActionBar alignment on all screens
   - Increased button sizes to 48dp (Material Design standard)
   - Better visual hierarchy throughout the app

### Bug Fixes
- Fixed persistence error on app startup with corrupted database
- Fixed ActionBar overlapping content on MainActivity, DraftHistoryActivity, and ConfigActivity
- Improved database migration error handling

## Files Included
- `FantasyDraftPicker-v1.2-signed.aab` - Android App Bundle for Google Play Store
- `FantasyDraftPicker-v1.2-signed.apk` - Signed APK for direct installation

## Installation Instructions

### For Google Play Store Upload:
1. Go to Google Play Console
2. Navigate to your app's Release section
3. Create a new release
4. Upload `FantasyDraftPicker-v1.2-signed.aab`
5. Add release notes and submit for review

### For Direct Installation (Testing):
1. Enable "Install from Unknown Sources" on your device
2. Transfer `FantasyDraftPicker-v1.2-signed.apk` to your device
3. Open the APK file and install

## Technical Details
- Built with Android Gradle Plugin
- Signed with release keystore
- ProGuard: Disabled (for easier debugging)
- Compiled SDK: 35
- Target SDK: 35

## Previous Versions
- v1.1 - Initial release with core draft functionality
- v1.0 - Beta release

## Support
For issues or questions, please contact the development team.
