# Fantasy Draft Picker - Signing Information

## Signed Release Files Created

### 1. FantasyDraftPicker-v1.0-signed.aab (Signed App Bundle)
- **Location**: `C:\Workspaces\Workspace_DraftTracker\FantasyDraftPicker-v1.0-signed.aab`
- **Format**: Android App Bundle (AAB)
- **Signing**: Release signed with production keystore
- **Use**: Google Play Store distribution or enterprise deployment
- **Status**: ✅ Ready for distribution

### 2. FantasyDraftPicker-v1.0-signed.apk (Signed APK)
- **Location**: `C:\Workspaces\Workspace_DraftTracker\FantasyDraftPicker-v1.0-signed.apk`
- **Format**: Android Package (APK)
- **Signing**: Release signed with production keystore
- **Use**: Direct installation on devices
- **Status**: ✅ Ready for distribution

## Keystore Information

### Keystore File
- **Location**: `C:\Workspaces\Workspace_DraftTracker\release-keystore.jks`
- **Type**: JKS (Java KeyStore)
- **Algorithm**: RSA 2048-bit
- **Validity**: 10,000 days (~27 years)

### Keystore Credentials
- **Store Password**: `fantasy2026`
- **Key Alias**: `fantasy-draft-picker`
- **Key Password**: `fantasy2026`

### Certificate Details
- **CN**: Fantasy Draft Picker
- **OU**: Development
- **O**: Personal
- **C**: US

## ⚠️ IMPORTANT SECURITY NOTES

### Keystore Protection
1. **BACKUP THE KEYSTORE**: Store `release-keystore.jks` in a secure location
2. **NEVER LOSE IT**: You cannot update your app on Play Store without the original keystore
3. **KEEP PASSWORDS SECURE**: Store credentials in a password manager
4. **DO NOT COMMIT**: The keystore is already in `.gitignore`

### For Production Use
If distributing publicly, consider:
1. Using a stronger password
2. Storing keystore in a secure vault
3. Using Google Play App Signing (recommended)

## Installation

### Signed APK Installation
```bash
# Via ADB
C:\Android\Sdk\platform-tools\adb.exe install -r FantasyDraftPicker-v1.0-signed.apk

# Or transfer to device and tap to install
```

### Signed AAB Deployment
```bash
# Upload to Google Play Console
# Or use bundletool for local testing:
java -jar bundletool.jar build-apks --bundle=FantasyDraftPicker-v1.0-signed.aab --output=app.apks --ks=release-keystore.jks --ks-key-alias=fantasy-draft-picker
java -jar bundletool.jar install-apks --apks=app.apks
```

## Verification

### Verify APK Signature
```bash
# Using apksigner (from Android SDK)
apksigner verify --verbose FantasyDraftPicker-v1.0-signed.apk

# Using jarsigner
jarsigner -verify -verbose -certs FantasyDraftPicker-v1.0-signed.apk
```

### View Certificate Info
```bash
keytool -list -v -keystore release-keystore.jks -alias fantasy-draft-picker -storepass fantasy2026
```

## Build Configuration

The signing configuration is in `app/build.gradle`:

```gradle
signingConfigs {
    release {
        storeFile file('../release-keystore.jks')
        storePassword 'fantasy2026'
        keyAlias 'fantasy-draft-picker'
        keyPassword 'fantasy2026'
    }
}

buildTypes {
    release {
        minifyEnabled false
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        signingConfig signingConfigs.release
    }
}
```

## Rebuilding Signed Releases

### Build Signed AAB
```bash
.\gradlew bundleRelease --console=plain
```
Output: `app\build\outputs\bundle\release\app-release.aab`

### Build Signed APK
```bash
.\gradlew assembleRelease --console=plain
```
Output: `app\build\outputs\apk\release\app-release.apk`

## Version Management

Current version:
- **Version Code**: 2
- **Version Name**: 1.1
- **API Level**: 35 (Android 15)

To update version for new releases, edit `app/build.gradle`:
```gradle
defaultConfig {
    versionCode 3        // Increment for each release
    versionName "1.2"    // Update version string
}
```

## Google Play Store Submission

### Prerequisites
1. ✅ Signed AAB created
2. ✅ App tested on device
3. ⬜ Create Google Play Developer account ($25 one-time fee)
4. ⬜ Prepare store listing (screenshots, description, icon)
5. ⬜ Set up app pricing and distribution

### Upload Process
1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app
3. Upload `FantasyDraftPicker-v1.0-signed.aab`
4. Complete store listing
5. Submit for review

### Google Play App Signing (Recommended)
- Let Google manage your signing key
- Provides additional security
- Enables Play Feature Delivery
- Set up during first upload

## Troubleshooting

### "App not installed" Error
- Uninstall any previous debug versions first
- Ensure device allows installation from unknown sources (for APK)

### Signature Verification Failed
- Keystore password incorrect
- Wrong key alias
- Keystore file corrupted (restore from backup)

### Cannot Update App
- Version code must be higher than previous version
- Must use same signing key as original upload

## File Checklist

✅ Files created:
- `FantasyDraftPicker-v1.0-signed.aab` (Signed App Bundle)
- `FantasyDraftPicker-v1.0-signed.apk` (Signed APK)
- `release-keystore.jks` (Keystore - BACKUP THIS!)
- `SIGNING_INFO.md` (This file)

## Next Steps

1. **Test the signed APK**: Install on your device
2. **Backup keystore**: Copy `release-keystore.jks` to secure location
3. **Distribute**: Share APK or upload AAB to Play Store
4. **Update regularly**: Run `scripts\fetch_adp.bat` before each season

## Version History

| Version | API Level | Date | Key Changes |
|---------|-----------|------|-------------|
| 1.1 | 35 | Feb 3, 2026 | Updated to Android 15 (API 35) |
| 1.0 | 34 | Feb 3, 2026 | Initial signed release with ADP rankings |

---

**Created**: February 3, 2026
**Current Version**: 1.1 (API 35)
**Keystore Validity**: Until ~2053
