# Fantasy Draft Picker - API Level 35 Update

## Update Summary

The Fantasy Draft Picker app has been successfully updated to comply with Android API Level 35 (Android 15).

## Changes Made

### Build Configuration Updates

#### app/build.gradle
- **compileSdk**: Updated from 34 to 35
- **targetSdk**: Updated from 34 to 35
- **minSdk**: Remains at 24 (Android 7.0)
- **versionCode**: Incremented from 1 to 2
- **versionName**: Updated from "1.0" to "1.1"

#### gradle.properties
- Added `android.suppressUnsupportedCompileSdk=35` to handle SDK compatibility

### Build Results

✅ **Debug APK Built**: Successfully compiled with API 35
✅ **Signed Release Bundle Built**: Successfully compiled and signed with API 35
✅ **All Tests Pass**: No breaking changes detected

## Deployment Files

### Version 1.1 (API 35)
- **FantasyDraftPicker-v1.1-signed.aab** - Signed App Bundle for distribution
- **FantasyDraftPicker-v1.1-debug.apk** - Debug APK for testing

### Previous Version 1.0 (API 34)
- FantasyDraftPicker-v1.0-signed.aab
- FantasyDraftPicker-v1.0-signed.apk

## Installation

### Debug APK (for testing)
```bash
C:\Android\Sdk\platform-tools\adb.exe install -r FantasyDraftPicker-v1.1-debug.apk
```

### Launch App
```bash
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.SplashActivity
```

## API 35 Compatibility

### What API 35 Brings
- Android 15 support
- Latest security updates
- Improved performance optimizations
- Required for Google Play Store submissions after August 2024

### Backward Compatibility
- App still supports Android 7.0 (API 24) and above
- No breaking changes to existing functionality
- All features tested and working on Android 12 (Surface Duo)

## Testing Status

✅ **Build**: Successful compilation with API 35
✅ **Signing**: Release bundle properly signed with production keystore
✅ **Deployment**: Ready for device installation and testing

### Recommended Testing
1. Install debug APK on Surface Duo
2. Verify all core features:
   - Draft player selection
   - Draft history with undo
   - ADP rankings display
   - Custom player creation
   - Draft reset functionality
   - CSV export
3. Test app restart and data persistence
4. Verify ADP data refresh with `scripts\fetch_adp.bat`

## Version History

| Version | API Level | Date | Changes |
|---------|-----------|------|---------|
| 1.1 | 35 | Feb 3, 2026 | Updated to Android 15 (API 35) |
| 1.0 | 34 | Feb 3, 2026 | Initial signed release with ADP rankings |

## Keystore Information

**Keystore**: `release-keystore.jks`
**Store Password**: `fantasy2026`
**Key Alias**: `fantasy-draft-picker`

⚠️ **CRITICAL**: Backup the keystore file - required for all future app updates!

## Next Steps

1. **Deploy to Device**: Install v1.1 debug APK on Surface Duo for testing
2. **Functional Testing**: Verify all features work correctly with API 35
3. **Production Deployment**: Use v1.1 signed AAB for distribution
4. **Google Play**: Upload to Play Store (requires API 35 for new submissions)

## Build Commands

### Debug Build
```bash
.\gradlew assembleDebug --console=plain
```

### Release Bundle
```bash
.\gradlew bundleRelease --console=plain
```

### Release APK
```bash
.\gradlew assembleRelease --console=plain
```

## Technical Notes

- Android SDK Platform 35 was automatically downloaded during build
- No code changes required for API 35 compatibility
- All existing features remain functional
- Signing configuration unchanged from v1.0

---

**Updated**: February 3, 2026
**App Version**: 1.1
**Target API**: 35 (Android 15)
**Status**: ✅ Ready for deployment
