# Device Testing Session - Fantasy Draft Picker

## Session Information
**Date:** January 30, 2026
**APK Location:** `app/build/outputs/apk/debug/app-debug.apk`
**Package Name:** `com.fantasydraft.picker`
**Main Activity:** `com.fantasydraft.picker.ui.MainActivity`

## Connected Devices
- Surface Duo (Android 12) - Device ID: 001111312267

## Installation Status
✅ App successfully installed on Surface Duo
✅ App successfully launched

---

## Useful ADB Commands

### Device Management
```bash
# List connected devices
C:\Android\Sdk\platform-tools\adb.exe devices

# Get device info
C:\Android\Sdk\platform-tools\adb.exe shell getprop ro.product.model
C:\Android\Sdk\platform-tools\adb.exe shell getprop ro.build.version.release
```

### App Installation & Management
```bash
# Install APK
C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk

# Uninstall app
C:\Android\Sdk\platform-tools\adb.exe uninstall com.fantasydraft.picker

# Reinstall (uninstall + install)
./gradlew uninstallDebug installDebug
```

### App Launch & Control
```bash
# Start the app
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity

# Start ConfigActivity directly
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.ConfigActivity

# Force stop the app
C:\Android\Sdk\platform-tools\adb.exe shell am force-stop com.fantasydraft.picker

# Clear app data (reset to fresh state)
C:\Android\Sdk\platform-tools\adb.exe shell pm clear com.fantasydraft.picker
```

### Debugging & Logs
```bash
# View app logs (real-time)
C:\Android\Sdk\platform-tools\adb.exe logcat | findstr "FantasyDraft"

# View app logs (filtered by package)
C:\Android\Sdk\platform-tools\adb.exe logcat | findstr "com.fantasydraft.picker"

# Clear logcat
C:\Android\Sdk\platform-tools\adb.exe logcat -c

# View crash logs
C:\Android\Sdk\platform-tools\adb.exe logcat *:E

# Save logs to file
C:\Android\Sdk\platform-tools\adb.exe logcat > app_logs.txt
```

### Database Inspection
```bash
# Access app's database directory
C:\Android\Sdk\platform-tools\adb.exe shell run-as com.fantasydraft.picker

# List databases
C:\Android\Sdk\platform-tools\adb.exe shell run-as com.fantasydraft.picker ls /data/data/com.fantasydraft.picker/databases/

# Pull database to local machine for inspection
C:\Android\Sdk\platform-tools\adb.exe shell run-as com.fantasydraft.picker cat /data/data/com.fantasydraft.picker/databases/fantasy_draft.db > fantasy_draft.db

# View SharedPreferences
C:\Android\Sdk\platform-tools\adb.exe shell run-as com.fantasydraft.picker cat /data/data/com.fantasydraft.picker/shared_prefs/draft_prefs.xml
```

### Screen Capture
```bash
# Take screenshot
C:\Android\Sdk\platform-tools\adb.exe shell screencap -p /sdcard/screenshot.png
C:\Android\Sdk\platform-tools\adb.exe pull /sdcard/screenshot.png

# Record screen video
C:\Android\Sdk\platform-tools\adb.exe shell screenrecord /sdcard/test_session.mp4
# Press Ctrl+C to stop recording
C:\Android\Sdk\platform-tools\adb.exe pull /sdcard/test_session.mp4
```

### Performance Monitoring
```bash
# Monitor memory usage
C:\Android\Sdk\platform-tools\adb.exe shell dumpsys meminfo com.fantasydraft.picker

# Monitor CPU usage
C:\Android\Sdk\platform-tools\adb.exe shell top -n 1 | findstr "fantasydraft"

# Check battery usage
C:\Android\Sdk\platform-tools\adb.exe shell dumpsys batterystats com.fantasydraft.picker
```

---

## Testing Workflow

### Quick Test Cycle
1. **Make code changes**
2. **Build and install:**
   ```bash
   ./gradlew installDebug
   ```
3. **Launch app:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
   ```
4. **Test functionality**
5. **Check logs if needed:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe logcat | findstr "FantasyDraft"
   ```

### Fresh Start Test
1. **Clear app data:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe shell pm clear com.fantasydraft.picker
   ```
2. **Launch app:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
   ```
3. **Test from initial state**

### Persistence Test
1. **Configure draft and make picks**
2. **Force stop app:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe shell am force-stop com.fantasydraft.picker
   ```
3. **Restart app:**
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
   ```
4. **Verify state restored**

---

## Manual Testing Checklist

Use this checklist while testing on the device. Refer to MANUAL_TESTING_GUIDE.md for detailed scenarios.

### Basic Functionality
- [ ] App launches successfully
- [ ] No crashes on startup
- [ ] UI displays correctly on Surface Duo screen
- [ ] Can navigate to Config Activity
- [ ] Can configure teams (2-20)
- [ ] Can select draft flow (Serpentine/Linear)
- [ ] Can enter team names
- [ ] Can save configuration
- [ ] Can make picks
- [ ] Draft history updates
- [ ] Best available player updates
- [ ] Can reset draft

### Error Handling
- [ ] Duplicate team names rejected
- [ ] Empty team names rejected
- [ ] Invalid team count rejected
- [ ] Already-drafted players rejected
- [ ] Error messages display correctly

### Persistence
- [ ] Configuration persists after app restart
- [ ] Draft state persists after app restart
- [ ] Pick history persists after app restart
- [ ] Can continue draft after restart

### UI/UX
- [ ] All text is readable
- [ ] Buttons are easily tappable
- [ ] ScrollViews work smoothly
- [ ] Cards display properly
- [ ] Colors match theme (green/gold)
- [ ] Layout works in portrait
- [ ] Layout works in landscape

### Performance
- [ ] App is responsive
- [ ] No lag when making picks
- [ ] Scrolling is smooth
- [ ] No memory warnings

---

## Issues Found

Document any issues discovered during testing:

### Issue Template
```
**Issue #:** [Number]
**Severity:** [Critical/High/Medium/Low]
**Description:** [What went wrong]
**Steps to Reproduce:**
1. [Step 1]
2. [Step 2]
3. [Step 3]
**Expected:** [What should happen]
**Actual:** [What actually happened]
**Logs:** [Relevant log output if available]
**Screenshot:** [If applicable]
```

---

## Test Results Summary

### Session 1 - Initial Launch
**Date:** January 30, 2026
**Device:** Surface Duo (Android 12)
**Status:** ✅ App installed and launched successfully

**Next Steps:**
1. Execute manual test scenarios from MANUAL_TESTING_GUIDE.md
2. Document any issues found
3. Test on additional devices/screen sizes if available
4. Verify all acceptance criteria from requirements.md

---

## Notes

- The app is now running on the Surface Duo device
- Use the ADB commands above to control the app during testing
- Monitor logcat for any errors or warnings
- Take screenshots of any issues for documentation
- Test both portrait and landscape orientations on the Surface Duo
- The Surface Duo has a unique dual-screen form factor - test both single and dual screen modes

## Gradle Commands (Alternative)

If you prefer using Gradle instead of ADB directly:

```bash
# Build and install
./gradlew installDebug

# Uninstall
./gradlew uninstallDebug

# Run instrumented tests on device
./gradlew connectedAndroidTest

# Build release APK
./gradlew assembleRelease
```
