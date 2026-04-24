# Player Data Refresh - Manual Testing Guide

## Overview
This guide provides step-by-step instructions for manually testing the Player Data Refresh feature on a physical device (Surface Duo).

## Prerequisites
- Debug APK built: `app/build/outputs/apk/debug/app-debug.apk`
- Surface Duo device connected via USB with USB debugging enabled
- ESPN API credentials configured in the app
- Internet connection available on the device

## Installation

### Install APK on Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or use Android Studio:
1. Open Android Studio
2. Select "Run" > "Run 'app'"
3. Choose Surface Duo from device list

## Test Scenarios

### Test 8.1: Successful Refresh Flow (No Draft in Progress)

**Objective:** Verify refresh works correctly when no draft is in progress.

**Steps:**
1. Launch the Fantasy Draft Picker app
2. If a draft is in progress, reset it:
   - Go to Draft History
   - Delete the current draft
3. From the main screen, tap the menu or settings icon
4. Navigate to Configuration screen
5. Scroll down to find "Refresh Player Data" button
6. Tap "Refresh Player Data"

**Expected Results:**
- ✓ Confirmation dialog appears
- ✓ Dialog title: "Refresh Player Data?"
- ✓ Dialog message mentions "ESPN" and "injury statuses"
- ✓ Dialog does NOT mention "reset your current draft" (since no draft in progress)
- ✓ Two buttons visible: "Cancel" and "Refresh"

7. Tap "Refresh" button

**Expected Results:**
- ✓ Progress dialog appears immediately
- ✓ Progress dialog title: "Refreshing Player Data"
- ✓ Progress dialog message: "Fetching latest data from ESPN..."
- ✓ Progress dialog shows spinning indicator
- ✓ Progress dialog is not cancelable (tapping outside doesn't dismiss)
- ✓ Refresh button becomes disabled during operation

8. Wait for operation to complete (5-30 seconds depending on network)

**Expected Results:**
- ✓ Progress dialog dismisses
- ✓ Success toast message appears: "Player data refreshed successfully! [N] players updated."
- ✓ Refresh button becomes enabled again
- ✓ No errors displayed

9. Return to main screen and verify player data

**Expected Results:**
- ✓ Player list shows updated data
- ✓ Injury statuses are current
- ✓ Rankings are updated
- ✓ No duplicate players
- ✓ All positions represented (QB, RB, WR, TE)

---

### Test 8.2: Refresh with Draft in Progress

**Objective:** Verify warning appears when draft is in progress and draft resets correctly.

**Steps:**
1. Launch the app
2. Start a new draft or continue existing draft
3. Make several draft picks (at least 5-10 picks)
4. Note the current draft state:
   - Current round and pick number
   - Players drafted
   - Team rosters
5. Navigate to Configuration screen
6. Tap "Refresh Player Data"

**Expected Results:**
- ✓ Confirmation dialog appears
- ✓ Dialog message includes: "reset your current draft"
- ✓ Dialog message includes: "All picks will be cleared"
- ✓ Warning is clear and prominent

7. Tap "Refresh" to confirm

**Expected Results:**
- ✓ Progress dialog appears
- ✓ Operation completes successfully

8. Return to main screen

**Expected Results:**
- ✓ Draft is reset to Round 1, Pick 1
- ✓ All pick history is cleared
- ✓ All team rosters are empty
- ✓ All players show as "undrafted"
- ✓ Player data is updated
- ✓ Draft configuration (teams, flow type) is preserved

---

### Test 8.3: Error Scenarios

#### Test 8.3.1: No Network Connection

**Steps:**
1. Enable Airplane Mode on device
2. Launch app and navigate to Configuration screen
3. Tap "Refresh Player Data"
4. Tap "Refresh" in confirmation dialog

**Expected Results:**
- ✓ Progress dialog appears briefly
- ✓ Error dialog appears
- ✓ Error title: "Refresh Failed"
- ✓ Error message: "No internet connection. Please check your network and try again."
- ✓ "OK" button to dismiss
- ✓ Original player data is intact (not corrupted)
- ✓ Draft state unchanged

#### Test 8.3.2: No ESPN API Credentials

**Steps:**
1. Clear ESPN API credentials:
   - Go to ESPN API Credentials screen
   - Clear League ID and SWID
   - Save
2. Navigate to Configuration screen
3. Tap "Refresh Player Data"
4. Tap "Refresh" in confirmation dialog

**Expected Results:**
- ✓ Error dialog appears
- ✓ Error message mentions "ESPN API credentials not configured"
- ✓ Error message directs user to credentials screen
- ✓ Original player data is intact

#### Test 8.3.3: Network Timeout

**Steps:**
1. Use a very slow network connection (if possible)
2. Tap "Refresh Player Data"
3. Tap "Refresh" in confirmation dialog
4. Wait for timeout (30 seconds)

**Expected Results:**
- ✓ Progress dialog shows for up to 30 seconds
- ✓ Error dialog appears after timeout
- ✓ Error message: "Request timed out. Please try again."
- ✓ Original player data is intact

#### Test 8.3.4: Invalid Server Response

**Steps:**
1. This scenario is difficult to test manually
2. Would require mock server or network interception
3. Skip for manual testing (covered by unit tests)

---

### Test 8.4: Cancellation

**Objective:** Verify user can cancel refresh operation.

**Steps:**
1. Launch app
2. Make a few draft picks (to trigger warning dialog)
3. Navigate to Configuration screen
4. Tap "Refresh Player Data"

**Expected Results:**
- ✓ Confirmation dialog appears

5. Tap "Cancel" button

**Expected Results:**
- ✓ Dialog dismisses immediately
- ✓ No progress dialog appears
- ✓ No refresh operation starts
- ✓ Refresh button remains enabled
- ✓ Draft state is completely unchanged
- ✓ Pick history is intact
- ✓ Player data is unchanged

6. Verify draft state by returning to main screen

**Expected Results:**
- ✓ All picks are still present
- ✓ Draft position is unchanged
- ✓ Team rosters are unchanged

---

### Test 8.5: Build and Deploy to Device

**Objective:** Verify APK builds correctly and installs on Surface Duo.

**Steps:**
1. Build debug APK:
```bash
.\gradlew :app:assembleDebug
```

**Expected Results:**
- ✓ Build completes successfully
- ✓ APK created at: `app/build/outputs/apk/debug/app-debug.apk`
- ✓ No build errors
- ✓ No warnings about missing resources

2. Install on Surface Duo:
```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Expected Results:**
- ✓ Device appears in `adb devices` list
- ✓ Installation succeeds
- ✓ App appears in device app drawer
- ✓ App icon displays correctly

3. Launch app on device

**Expected Results:**
- ✓ App launches without crashes
- ✓ Splash screen displays
- ✓ Main screen loads
- ✓ All UI elements render correctly on Surface Duo screen
- ✓ No layout issues or overlapping elements

4. Run all test scenarios (8.1 - 8.4) on actual device

---

## Additional Verification Tests

### Test: Multiple Consecutive Refreshes

**Steps:**
1. Perform a successful refresh
2. Immediately tap "Refresh Player Data" again
3. Confirm and complete second refresh
4. Repeat 2-3 more times

**Expected Results:**
- ✓ Each refresh completes successfully
- ✓ No memory leaks or performance degradation
- ✓ Player data remains consistent
- ✓ No duplicate players appear

### Test: Refresh During Different App States

**Steps:**
1. Test refresh with different team counts (2, 10, 20 teams)
2. Test refresh with different draft flows (Serpentine, Linear)
3. Test refresh with different round counts (1, 15, 20 rounds)
4. Test refresh with keeper league enabled/disabled

**Expected Results:**
- ✓ Refresh works correctly in all configurations
- ✓ Draft configuration is preserved after refresh
- ✓ Only draft state and player data are reset

### Test: App Restart After Refresh

**Steps:**
1. Perform successful refresh
2. Note the player count and some player names
3. Force close the app
4. Relaunch the app

**Expected Results:**
- ✓ Updated player data persists
- ✓ Player count matches previous refresh
- ✓ Draft state remains reset (Round 1, Pick 1)
- ✓ No data loss

### Test: Refresh Button State

**Steps:**
1. Tap refresh button
2. While progress dialog is showing, observe button state
3. After completion, observe button state

**Expected Results:**
- ✓ Button is disabled during refresh operation
- ✓ Button is re-enabled after success
- ✓ Button is re-enabled after error
- ✓ Button cannot be tapped multiple times rapidly

---

## Test Results Template

Use this template to record test results:

```
Test Date: _______________
Device: Surface Duo
Android Version: _______________
App Version: _______________

Test 8.1 - Successful Refresh (No Draft): [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.2 - Refresh with Draft in Progress: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.3.1 - No Network Connection: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.3.2 - No API Credentials: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.3.3 - Network Timeout: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.4 - Cancellation: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Test 8.5 - Build and Deploy: [ ] PASS [ ] FAIL
Notes: _________________________________________________

Additional Tests:
- Multiple Consecutive Refreshes: [ ] PASS [ ] FAIL
- Different App States: [ ] PASS [ ] FAIL
- App Restart After Refresh: [ ] PASS [ ] FAIL
- Refresh Button State: [ ] PASS [ ] FAIL

Overall Result: [ ] ALL TESTS PASSED [ ] SOME FAILURES

Issues Found:
1. _________________________________________________
2. _________________________________________________
3. _________________________________________________
```

---

## Troubleshooting

### Issue: "No internet connection" error when network is available
**Solution:** 
- Check if app has INTERNET permission in AndroidManifest.xml
- Verify device actually has working internet (open browser)
- Check if firewall or VPN is blocking connection

### Issue: "ESPN API credentials not configured" error
**Solution:**
- Navigate to ESPN API Credentials screen
- Enter valid League ID and SWID
- Save credentials
- Try refresh again

### Issue: Progress dialog never dismisses
**Solution:**
- Wait full 30 seconds for timeout
- Check logcat for errors: `adb logcat | grep -i espn`
- Verify ESPN API endpoint is accessible
- Check if API credentials are valid

### Issue: App crashes during refresh
**Solution:**
- Check logcat for stack trace: `adb logcat`
- Verify all required permissions are granted
- Check if device has sufficient storage space
- Try clearing app data and reinstalling

### Issue: Player data not updating after successful refresh
**Solution:**
- Check if players_updated.json file was created in internal storage
- Verify MainActivity is reloading data after refresh
- Check if broadcast/callback mechanism is working
- Try force closing and reopening the app

---

## Success Criteria

All tests pass when:
- ✓ Refresh works correctly with and without draft in progress
- ✓ Appropriate warnings are shown
- ✓ Progress feedback is clear and accurate
- ✓ Error messages are helpful and specific
- ✓ Cancellation works as expected
- ✓ Draft state resets completely
- ✓ Player data updates correctly
- ✓ No data corruption occurs
- ✓ App remains stable through multiple refreshes
- ✓ APK builds and installs successfully on Surface Duo

---

## Notes

- Network-dependent tests may have variable results based on connection quality
- ESPN API availability may affect test results
- Some error scenarios are difficult to reproduce manually
- Automated tests provide better coverage for edge cases
- Always test on actual device (Surface Duo) for final validation
