# Player Data Refresh - Troubleshooting Guide

## Overview

This guide provides detailed troubleshooting steps for common issues encountered when using the Player Data Refresh feature in the Fantasy Draft Picker app.

## Quick Diagnostic Checklist

Before diving into specific errors, verify these basics:

- [ ] Device has active internet connection
- [ ] App has network permissions enabled
- [ ] Sufficient storage space available (at least 10MB free)
- [ ] App is up to date (latest version installed)
- [ ] Not in airplane mode
- [ ] No VPN or firewall blocking ESPN domains

## Common Error Messages

### 1. "No internet connection"

#### Symptoms
- Error appears immediately when tapping refresh
- No progress dialog shown
- Existing data remains unchanged

#### Root Causes
- Device has no active network connection
- Airplane mode is enabled
- Wi-Fi or mobile data is disabled
- Network adapter is malfunctioning

#### Solutions

**Step 1: Verify Network Connection**
```
1. Open device Settings
2. Go to Network & Internet
3. Verify Wi-Fi or Mobile Data is ON
4. Check connection status shows "Connected"
```

**Step 2: Test Internet Access**
```
1. Open web browser
2. Navigate to any website (e.g., google.com)
3. Verify page loads successfully
4. If page doesn't load, troubleshoot device network
```

**Step 3: Disable Airplane Mode**
```
1. Swipe down from top of screen
2. Check if airplane icon is highlighted
3. Tap to disable airplane mode
4. Wait for network to reconnect
```

**Step 4: Restart Network Connection**
```
1. Turn Wi-Fi OFF, wait 5 seconds, turn ON
2. Or toggle Mobile Data OFF/ON
3. Wait for connection to stabilize
4. Try refresh again
```

**Step 5: Restart Device**
```
1. Power off device completely
2. Wait 10 seconds
3. Power on device
4. Wait for network to connect
5. Try refresh again
```

#### Prevention
- Check network status before starting refresh
- Use Wi-Fi for more reliable connection
- Avoid refreshing in areas with poor signal

---

### 2. "Unable to reach ESPN servers"

#### Symptoms
- Progress dialog appears briefly
- Error shows after 5-10 seconds
- Network connection is active

#### Root Causes
- ESPN API servers are down or under maintenance
- Network is blocking ESPN domains
- DNS resolution failure
- Firewall or VPN interference

#### Solutions

**Step 1: Verify ESPN Accessibility**
```
1. Open web browser on device
2. Navigate to https://espn.com
3. Verify ESPN website loads
4. If ESPN doesn't load, issue is network-level
```

**Step 2: Check ESPN Status**
```
1. Visit https://downdetector.com/status/espn/
2. Check if other users reporting ESPN outages
3. If widespread outage, wait and try later
```

**Step 3: Disable VPN**
```
1. If using VPN, temporarily disable it
2. Try refresh again
3. Some VPNs may block ESPN domains
```

**Step 4: Try Different Network**
```
1. Switch from Wi-Fi to Mobile Data (or vice versa)
2. Try refresh on different network
3. If works on one network, original network has restrictions
```

**Step 5: Clear DNS Cache**
```
1. Go to device Settings
2. Apps → Fantasy Draft Picker
3. Storage → Clear Cache
4. Restart app and try again
```

**Step 6: Wait and Retry**
```
1. ESPN servers may be temporarily unavailable
2. Wait 15-30 minutes
3. Try refresh again during off-peak hours
```

#### Prevention
- Refresh during off-peak hours (early morning)
- Avoid refreshing during major sporting events
- Use reliable network connections

---

### 3. "Request timed out"

#### Symptoms
- Progress dialog shows for 30+ seconds
- Eventually shows timeout error
- Network connection is active but slow

#### Root Causes
- Slow internet connection
- Network congestion
- Poor signal strength
- ESPN servers responding slowly

#### Solutions

**Step 1: Check Connection Speed**
```
1. Open web browser
2. Visit https://fast.com or https://speedtest.net
3. Run speed test
4. Minimum recommended: 1 Mbps download
```

**Step 2: Improve Signal Strength**
```
For Wi-Fi:
1. Move closer to Wi-Fi router
2. Reduce obstacles between device and router
3. Restart router if possible

For Mobile Data:
1. Move to area with better signal
2. Check signal bars in status bar
3. Go outdoors if indoors has poor signal
```

**Step 3: Switch Networks**
```
1. If on Wi-Fi, try Mobile Data
2. If on Mobile Data, try Wi-Fi
3. Use fastest available network
```

**Step 4: Close Background Apps**
```
1. Open Recent Apps view
2. Close apps using network (streaming, downloads)
3. Free up network bandwidth
4. Try refresh again
```

**Step 5: Retry During Off-Peak**
```
1. Network may be congested
2. Try early morning (6-8 AM)
3. Or late evening (10 PM - midnight)
4. Avoid peak usage times
```

#### Prevention
- Use Wi-Fi when available
- Refresh during off-peak hours
- Ensure strong signal before starting
- Close bandwidth-heavy apps first

---

### 4. "Invalid data format"

#### Symptoms
- Progress dialog completes
- Error appears after data fetch
- Network request succeeded but parsing failed

#### Root Causes
- ESPN API response format changed
- Corrupted data received
- Incomplete response from server
- App version incompatible with current API

#### Solutions

**Step 1: Retry Immediately**
```
1. Tap refresh button again
2. May have been temporary glitch
3. Second attempt often succeeds
```

**Step 2: Clear App Cache**
```
1. Go to device Settings
2. Apps → Fantasy Draft Picker
3. Storage → Clear Cache (NOT Clear Data)
4. Restart app
5. Try refresh again
```

**Step 3: Check for App Updates**
```
1. Open Google Play Store
2. Search for "Fantasy Draft Picker"
3. If update available, install it
4. ESPN API may have changed, requiring app update
```

**Step 4: Wait and Retry**
```
1. ESPN may be deploying API changes
2. Wait 1-2 hours
3. Try refresh again
4. Changes may stabilize
```

**Step 5: Contact Support**
```
If error persists after 24 hours:
1. Note exact error message
2. Note date and time of attempts
3. Contact app support
4. May require app update to fix
```

#### Prevention
- Keep app updated to latest version
- Don't refresh during ESPN maintenance windows
- Report persistent issues to support

---

### 5. "Unable to save player data"

#### Symptoms
- Data fetches successfully
- Error occurs during save operation
- Existing data remains unchanged

#### Root Causes
- Insufficient storage space
- Storage permissions denied
- File system error
- Storage media corrupted

#### Solutions

**Step 1: Check Storage Space**
```
1. Go to device Settings
2. Storage
3. Check available space
4. Need at least 10MB free
```

**Step 2: Free Up Space**
```
1. Delete unused apps
2. Clear app caches (Settings → Storage → Cached data)
3. Delete old photos/videos
4. Move files to SD card or cloud
```

**Step 3: Verify App Permissions**
```
1. Go to device Settings
2. Apps → Fantasy Draft Picker
3. Permissions
4. Ensure Storage permission is granted
5. If denied, enable it
```

**Step 4: Clear App Data (Last Resort)**
```
⚠️ WARNING: This will reset all app data including drafts

1. Go to device Settings
2. Apps → Fantasy Draft Picker
3. Storage → Clear Data
4. Restart app
5. Reconfigure settings
6. Try refresh again
```

**Step 5: Reinstall App**
```
⚠️ WARNING: This will delete all app data

1. Uninstall Fantasy Draft Picker
2. Restart device
3. Reinstall from Google Play Store
4. Try refresh again
```

#### Prevention
- Maintain at least 100MB free storage
- Regularly clear app caches
- Monitor storage usage
- Backup important data

---

### 6. Refresh Button is Disabled/Grayed Out

#### Symptoms
- Refresh button appears but cannot be tapped
- Button is grayed out or unresponsive
- No error message shown

#### Root Causes
- Refresh operation already in progress
- App is in invalid state
- UI not properly initialized

#### Solutions

**Step 1: Wait for Completion**
```
1. Check if progress dialog is showing
2. Wait for current operation to complete
3. Button re-enables automatically
```

**Step 2: Dismiss Dialogs**
```
1. Check for any open dialogs
2. Tap outside dialog or press Back
3. Dismiss all dialogs
4. Try refresh button again
```

**Step 3: Return to Main Screen**
```
1. Press Back to exit configuration
2. Return to main draft screen
3. Re-enter configuration screen
4. Try refresh button again
```

**Step 4: Restart App**
```
1. Press Home button
2. Open Recent Apps
3. Swipe away Fantasy Draft Picker
4. Reopen app from launcher
5. Navigate to configuration
6. Try refresh button again
```

**Step 5: Force Stop App**
```
1. Go to device Settings
2. Apps → Fantasy Draft Picker
3. Force Stop
4. Reopen app
5. Try refresh again
```

#### Prevention
- Wait for operations to complete before navigating
- Don't tap refresh multiple times rapidly
- Close dialogs properly

---

### 7. Data Doesn't Appear Updated After Refresh

#### Symptoms
- Refresh completes successfully
- Success message shows
- But player data looks unchanged

#### Root Causes
- ESPN data hasn't actually changed
- UI didn't reload properly
- Cached data being displayed
- Refresh completed but data not reloaded

#### Solutions

**Step 1: Verify Success Message**
```
1. Check the success toast message
2. Note the number of players updated
3. If 0 players, ESPN data unchanged
```

**Step 2: Check Specific Players**
```
1. Look up a player you know changed (injury, trade)
2. Verify their status in app
3. If updated, refresh worked
4. If not updated, issue with refresh
```

**Step 3: Restart App**
```
1. Close app completely
2. Reopen from launcher
3. Check player data again
4. UI should reload with new data
```

**Step 4: Force Reload**
```
1. Go to configuration screen
2. Change any setting (e.g., team count)
3. Save changes
4. Return to main screen
5. Data should reload
```

**Step 5: Clear Cache and Retry**
```
1. Settings → Apps → Fantasy Draft Picker
2. Storage → Clear Cache
3. Restart app
4. Try refresh again
```

#### Prevention
- Always restart app after refresh
- Verify specific player changes
- Check ESPN.com to confirm data changed

---

### 8. App Crashes During Refresh

#### Symptoms
- Tap refresh button
- Progress dialog appears
- App suddenly closes/crashes
- Returns to home screen

#### Root Causes
- Out of memory
- Corrupted app data
- Device resource constraints
- App bug

#### Solutions

**Step 1: Free Up Memory**
```
1. Open Recent Apps
2. Close all other apps
3. Restart device
4. Open only Fantasy Draft Picker
5. Try refresh again
```

**Step 2: Clear App Cache**
```
1. Settings → Apps → Fantasy Draft Picker
2. Storage → Clear Cache
3. Restart app
4. Try refresh again
```

**Step 3: Update App**
```
1. Open Google Play Store
2. Check for app updates
3. Install any available updates
4. Crash may be fixed in newer version
```

**Step 4: Clear App Data**
```
⚠️ WARNING: Deletes all app data

1. Settings → Apps → Fantasy Draft Picker
2. Storage → Clear Data
3. Restart app
4. Reconfigure settings
5. Try refresh again
```

**Step 5: Reinstall App**
```
⚠️ WARNING: Deletes all app data

1. Uninstall Fantasy Draft Picker
2. Restart device
3. Reinstall from Play Store
4. Try refresh again
```

**Step 6: Report Bug**
```
1. Note exact steps to reproduce crash
2. Note device model and Android version
3. Contact app support with details
4. May require bug fix update
```

#### Prevention
- Keep app updated
- Maintain sufficient free memory
- Close other apps before refresh
- Report crashes to help fix bugs

---

## Advanced Troubleshooting

### Network Diagnostics

#### Check Network Connectivity
```bash
# On device with terminal access
ping espn.com
nslookup fantasy.espn.com
```

#### Test ESPN API Directly
```bash
# Using curl (if available)
curl -v "https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3"
```

#### Check DNS Resolution
```
1. Install DNS Lookup app from Play Store
2. Look up fantasy.espn.com
3. Verify IP address resolves
4. If fails, DNS issue
```

### App Diagnostics

#### Enable Developer Options
```
1. Settings → About Phone
2. Tap Build Number 7 times
3. Developer Options enabled
4. Settings → Developer Options
5. Enable USB Debugging
```

#### View App Logs
```
1. Connect device to computer
2. Install Android SDK Platform Tools
3. Run: adb logcat | grep FantasyDraft
4. Reproduce issue
5. Review logs for errors
```

#### Check App Permissions
```
1. Settings → Apps → Fantasy Draft Picker
2. Permissions
3. Verify all required permissions granted:
   - Internet (should be auto-granted)
   - Network State (should be auto-granted)
   - Storage (if needed)
```

### Storage Diagnostics

#### Check Available Space
```
1. Settings → Storage
2. Note available space
3. Need minimum 10MB free
4. Recommended 100MB+ free
```

#### Check App Storage Usage
```
1. Settings → Apps → Fantasy Draft Picker
2. Storage
3. Note:
   - App size
   - Data size
   - Cache size
4. If excessive, clear cache
```

---

## Error Code Reference

The app may display internal error codes for debugging:

| Error Code | Meaning | Solution |
|------------|---------|----------|
| NET_001 | No network connection | Check internet connection |
| NET_002 | DNS resolution failed | Check DNS settings |
| NET_003 | Connection timeout | Improve network speed |
| NET_004 | SSL certificate error | Check device date/time |
| API_001 | ESPN server unreachable | Wait and retry |
| API_002 | HTTP 4xx error | ESPN API issue, retry later |
| API_003 | HTTP 5xx error | ESPN server error, retry later |
| API_004 | Invalid response format | Update app or retry |
| PARSE_001 | JSON parsing failed | Retry or update app |
| PARSE_002 | Missing required fields | ESPN data incomplete |
| PARSE_003 | Invalid data types | ESPN API changed |
| STORAGE_001 | Insufficient space | Free up storage |
| STORAGE_002 | Permission denied | Grant storage permission |
| STORAGE_003 | File write failed | Check storage health |
| STORAGE_004 | File corruption | Clear app data |

---

## When to Contact Support

Contact app support if:

1. **Persistent Errors**: Same error occurs repeatedly over 24+ hours
2. **Data Corruption**: Player data appears corrupted or nonsensical
3. **App Crashes**: App crashes consistently during refresh
4. **Missing Features**: Refresh button doesn't appear
5. **Unexpected Behavior**: Refresh completes but causes other issues

### Information to Provide

When contacting support, include:

1. **Device Information**:
   - Device model (e.g., Samsung Galaxy S21)
   - Android version (e.g., Android 12)
   - App version (Settings → About)

2. **Error Details**:
   - Exact error message
   - Error code (if shown)
   - When error occurs (step-by-step)

3. **Network Information**:
   - Connection type (Wi-Fi or Mobile Data)
   - Network provider
   - Can you access ESPN.com in browser?

4. **Troubleshooting Attempted**:
   - Steps already tried
   - Results of each step
   - Any patterns noticed

5. **Screenshots**:
   - Error message screenshot
   - Configuration screen screenshot
   - Any relevant UI issues

---

## Prevention Best Practices

### Before Refreshing

1. ✅ Verify strong internet connection
2. ✅ Close unnecessary apps
3. ✅ Ensure 100MB+ free storage
4. ✅ Check ESPN.com is accessible
5. ✅ Backup current draft if in progress

### During Refresh

1. ✅ Don't close the app
2. ✅ Don't press Home or Back
3. ✅ Don't lock the screen
4. ✅ Keep device awake
5. ✅ Wait for completion message

### After Refresh

1. ✅ Verify success message
2. ✅ Check a few player updates
3. ✅ Restart app to ensure data loaded
4. ✅ Test draft functionality
5. ✅ Report any issues immediately

---

## FAQ

**Q: How long should refresh take?**
A: Typically 3-10 seconds on good connection. If exceeds 30 seconds, will timeout.

**Q: Can I use the app during refresh?**
A: No, the progress dialog blocks interaction. Wait for completion.

**Q: What if I accidentally close the app during refresh?**
A: Refresh will be aborted. Data remains unchanged. Try again.

**Q: Will failed refresh corrupt my data?**
A: No, the app maintains data integrity. Failed refresh leaves existing data unchanged.

**Q: Can I refresh without internet?**
A: No, internet connection is required to fetch data from ESPN.

**Q: Why does refresh sometimes fail even with good internet?**
A: ESPN servers may be down, under maintenance, or experiencing high load.

**Q: How do I know if ESPN data actually changed?**
A: Check specific players you know changed (injuries, trades) or compare with ESPN.com.

**Q: Can I undo a refresh?**
A: No, refresh is permanent. Previous data is overwritten.

---

## Additional Resources

- **User Guide**: See PLAYER_DATA_REFRESH_USER_GUIDE.md
- **API Documentation**: See ESPN_API_DOCUMENTATION.md
- **Testing Guide**: See PLAYER_DATA_REFRESH_TESTING_GUIDE.md
- **App Support**: Contact through app settings or Play Store

---

## Document Version

- **Version**: 1.0
- **Last Updated**: 2025-01-15
- **Applies to App Version**: 1.0+
