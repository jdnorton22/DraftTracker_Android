# Fantasy Draft Picker - Deployment Guide

## App Files Created

Two deployment files have been created in the root directory:

### 1. FantasyDraftPicker-v1.0.aab (App Bundle)
- **Location**: `C:\Workspaces\Workspace_DraftTracker\FantasyDraftPicker-v1.0.aab`
- **Format**: Android App Bundle (AAB)
- **Use Case**: Google Play Store distribution or internal testing
- **Size**: Optimized for distribution
- **Signing**: Debug signed (for testing only)

### 2. FantasyDraftPicker-debug.apk (Debug APK)
- **Location**: `C:\Workspaces\Workspace_DraftTracker\FantasyDraftPicker-debug.apk`
- **Format**: Android Package (APK)
- **Use Case**: Direct installation on devices (sideloading)
- **Size**: ~5-10 MB
- **Signing**: Debug signed

## Installation Methods

### Method 1: Direct Installation (APK)

**On Device:**
1. Transfer `FantasyDraftPicker-debug.apk` to your device
2. Enable "Install from Unknown Sources" in Settings
3. Tap the APK file to install

**Via ADB:**
```bash
C:\Android\Sdk\platform-tools\adb.exe install -r FantasyDraftPicker-debug.apk
```

### Method 2: App Bundle (AAB)

**For Google Play Store:**
1. Upload `FantasyDraftPicker-v1.0.aab` to Google Play Console
2. Google Play will generate optimized APKs for each device

**For Local Testing (requires bundletool):**
```bash
# Download bundletool from: https://github.com/google/bundletool/releases

# Generate APKs from bundle
java -jar bundletool.jar build-apks --bundle=FantasyDraftPicker-v1.0.aab --output=app.apks

# Install on connected device
java -jar bundletool.jar install-apks --apks=app.apks
```

## Current App Version

**Version**: 1.0
**Build Date**: February 3, 2026
**Features**:
- ✅ Serpentine and Linear draft flows
- ✅ 369 players with ESPN rankings
- ✅ ADP (Average Draft Position) from FantasyPros
- ✅ Position-based color coding
- ✅ Draft history with undo
- ✅ Custom player creation
- ✅ CSV export
- ✅ Persistent draft state
- ✅ Player data refresh from ESPN
- ✅ Injury status tracking

## File Locations

### Source Files
- **Original AAB**: `app\build\outputs\bundle\release\app-release.aab`
- **Original Debug APK**: `app\build\outputs\apk\debug\app-debug.apk`

### Deployment Files (Root Directory)
- **App Bundle**: `FantasyDraftPicker-v1.0.aab`
- **Debug APK**: `FantasyDraftPicker-debug.apk`

## Signing Information

⚠️ **Important**: These files are signed with a debug certificate and should NOT be used for production distribution.

For production release:
1. Generate a release keystore
2. Configure signing in `app/build.gradle`
3. Build with: `.\gradlew bundleRelease`

## Device Requirements

- **Minimum Android Version**: Android 7.0 (API 24)
- **Target Android Version**: Android 12 (API 31)
- **Optimized For**: Foldable devices (Surface Duo)
- **Permissions**: Storage (for CSV export)

## Updating the App

To create a new version:

1. **Update player data** (optional):
   ```bash
   python scripts\fetch_fantasypros_adp.py
   ```

2. **Build new bundle**:
   ```bash
   .\gradlew bundleRelease --console=plain
   ```

3. **Copy to deployment location**:
   ```bash
   Copy-Item "app\build\outputs\bundle\release\app-release.aab" -Destination "FantasyDraftPicker-v1.1.aab"
   ```

## Troubleshooting

### Installation Failed
- Ensure "Install from Unknown Sources" is enabled
- Check device has sufficient storage
- Try uninstalling previous version first

### App Won't Launch
- Check Android version compatibility (7.0+)
- Clear app data and cache
- Reinstall the app

### ADB Not Found
- Ensure Android SDK is installed
- Add platform-tools to PATH
- Use full path: `C:\Android\Sdk\platform-tools\adb.exe`

## Support

For issues or questions:
1. Check `CURRENT_STATUS.md` for known issues
2. Review `README.md` for feature documentation
3. Check `ADP_FEATURE_SUMMARY.md` for ADP data information

## Distribution Checklist

Before distributing:
- [ ] Test on target device
- [ ] Verify all features work
- [ ] Check player data is current
- [ ] Update version number
- [ ] Create release notes
- [ ] Sign with production certificate (if needed)
- [ ] Test installation process

## Next Steps

1. **Test the APK**: Install `FantasyDraftPicker-debug.apk` on your device
2. **Verify Features**: Test draft flow, player selection, CSV export
3. **Update Data**: Run `scripts\fetch_adp.bat` before each draft season
4. **Share**: Distribute APK to other users or upload AAB to Play Store

---

**Note**: The debug APK is ready for immediate use. The AAB requires additional steps for installation but is the preferred format for Play Store distribution.
