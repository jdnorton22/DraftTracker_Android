# Quick Start Testing Guide

## 🚀 App is Running on Your Device!

The Fantasy Draft Picker app is now installed and running on your Surface Duo (Android 12).

---

## 📱 What You Should See

On your device, you should see the Fantasy Draft Picker app with:
- **Main Screen** showing:
  - Draft Configuration section (Teams: --, Flow: --)
  - "Edit Config" button
  - Current Pick section
  - "Make Pick" and "Reset Draft" buttons
  - Draft History (empty initially)
  - Best Available Player section (right side)

---

## ✅ First Test - Basic Draft Flow (5 minutes)

Let's do a quick smoke test to verify everything works:

### Step 1: Configure Teams
1. Tap **"Edit Config"** button
2. Set team count to **4** using the number picker
3. Select **"Serpentine"** from the draft flow dropdown
4. Enter team names:
   - Team 1: "Champions"
   - Team 2: "Pros"
   - Team 3: "Winners"
   - Team 4: "Stars"
5. Tap **"Save Configuration"**

**Expected:** You return to main screen showing "Teams: 4" and "Flow: Serpentine"

### Step 2: Make First Pick
1. Verify current pick shows "Round 1, Pick 1" and "Team: Champions"
2. Note the best available player on the right (should be Christian McCaffrey, RB, #1)
3. Tap **"Make Pick"**
4. In the dialog, select the top player (Christian McCaffrey)

**Expected:** 
- Pick appears in draft history
- Current pick advances to "Round 1, Pick 2" and "Team: Pros"
- Best available updates to next player

### Step 3: Make More Picks
1. Make 3 more picks to complete Round 1
2. Verify Round 2 starts with "Team: Stars" (serpentine reversal)
3. Make 2 more picks in Round 2

**Expected:**
- Draft history shows all 6 picks
- Pick order follows serpentine: 1-2-3-4, 4-3-...
- Best available updates after each pick

### Step 4: Test Persistence
1. Press the **Home button** on your device
2. Swipe up to see recent apps
3. **Swipe away** the Fantasy Draft Picker app to close it
4. Reopen the app from the app drawer

**Expected:**
- Configuration restored (4 teams, serpentine)
- All 6 picks still in history
- Current pick shows where you left off
- Can continue drafting

### Step 5: Test Reset
1. Tap **"Reset Draft"**
2. Confirm the reset

**Expected:**
- All picks cleared from history
- Current pick returns to Round 1, Pick 1
- Team configuration preserved
- Best available resets to top player

---

## ✅ If Everything Works

**Congratulations!** The core functionality is working. Now proceed to comprehensive testing:

1. Open **MANUAL_TESTING_GUIDE.md**
2. Work through Test Suite 1 (Complete Draft Scenarios)
3. Continue with remaining test suites
4. Document any issues in **DEVICE_TESTING_SESSION.md**

---

## ❌ If Something Doesn't Work

### App Won't Open Config Activity
**Try:**
```bash
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.ConfigActivity
```

### App Crashes
**Check logs:**
```bash
C:\Android\Sdk\platform-tools\adb.exe logcat *:E | findstr "fantasydraft"
```

### Need to Start Fresh
**Clear data and restart:**
```bash
C:\Android\Sdk\platform-tools\adb.exe shell pm clear com.fantasydraft.picker
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
```

### App Not Responding
**Force stop and restart:**
```bash
C:\Android\Sdk\platform-tools\adb.exe shell am force-stop com.fantasydraft.picker
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
```

---

## 🎯 Testing Priorities

### Must Test (Critical)
1. ✅ Basic draft flow (4 teams, 10 picks)
2. ✅ Persistence (close and reopen app)
3. ✅ Draft reset
4. ✅ Error handling (duplicate names, drafted players)

### Should Test (Important)
1. Different team counts (2, 10, 20)
2. Linear vs Serpentine flow
3. Manual player selection (not just best available)
4. Screen rotation
5. Long team names

### Nice to Test (Optional)
1. Dual screen mode (Surface Duo specific)
2. 100+ picks (performance)
3. Rapid picks
4. All 300 players drafted

---

## 📊 Quick Status Check

After your first test, update this checklist:

- [ ] App launches successfully
- [ ] Can configure teams
- [ ] Can make picks
- [ ] Draft history updates
- [ ] Best available updates
- [ ] Persistence works
- [ ] Reset works
- [ ] No crashes observed
- [ ] UI looks good on Surface Duo
- [ ] Performance is acceptable

---

## 🔧 Useful Commands During Testing

### Restart App
```bash
C:\Android\Sdk\platform-tools\adb.exe shell am force-stop com.fantasydraft.picker
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
```

### Fresh Start
```bash
C:\Android\Sdk\platform-tools\adb.exe shell pm clear com.fantasydraft.picker
C:\Android\Sdk\platform-tools\adb.exe shell am start -n com.fantasydraft.picker/.ui.MainActivity
```

### Watch Logs
```bash
C:\Android\Sdk\platform-tools\adb.exe logcat | findstr "FantasyDraft"
```

### Take Screenshot
```bash
C:\Android\Sdk\platform-tools\adb.exe shell screencap -p /sdcard/screenshot.png
C:\Android\Sdk\platform-tools\adb.exe pull /sdcard/screenshot.png
```

---

## 📝 Document Your Findings

As you test, note:
- ✅ What works well
- ❌ What doesn't work
- ⚠️ What could be improved
- 🐛 Any bugs found

Update **DEVICE_TESTING_SESSION.md** with your findings.

---

## 🎉 Ready to Test!

The app is running on your device. Start with the 5-minute smoke test above, then proceed to comprehensive testing using the MANUAL_TESTING_GUIDE.md.

**Good luck with testing!** 🏈📊
