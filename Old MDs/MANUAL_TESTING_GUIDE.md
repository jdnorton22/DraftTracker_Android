# Manual Testing Guide - Fantasy Draft Picker

## Overview
This guide provides comprehensive manual testing scenarios to verify all functionality of the Fantasy Draft Picker app before release.

## Pre-Testing Setup
1. Install the app on a test device or emulator
2. Clear app data to start fresh: Settings > Apps > Fantasy Draft Picker > Storage > Clear Data
3. Have a notepad ready to document any issues

---

## Test Suite 1: Complete Draft Scenarios

### Scenario 1.1: Basic 4-Team Serpentine Draft
**Objective:** Test a complete draft with minimal teams

**Steps:**
1. Launch the app
2. Tap "Edit Config"
3. Set team count to 4
4. Select "Serpentine" flow
5. Enter team names:
   - Team 1: "The Champions"
   - Team 2: "Draft Masters"
   - Team 3: "Fantasy Pros"
   - Team 4: "Grid Iron"
6. Verify draft positions are 1, 2, 3, 4
7. Tap "Save Configuration"
8. Verify you return to main screen
9. Verify configuration shows "Teams: 4" and "Flow: Serpentine"
10. Verify current pick shows "Round 1, Pick 1" and "Team: The Champions"
11. Verify best available player is displayed
12. Tap "Make Pick"
13. Select the best available player from the dialog
14. Verify pick appears in draft history
15. Verify current pick advances to "Round 1, Pick 2" and "Team: Draft Masters"
16. Continue making picks through Round 1 (4 picks total)
17. Verify Round 2 starts with "Round 2, Pick 1" and "Team: Grid Iron" (serpentine reversal)
18. Continue through Round 2
19. Verify draft history shows all picks in order

**Expected Results:**
- ✅ Configuration saves successfully
- ✅ Pick order follows serpentine pattern: 1-2-3-4, 4-3-2-1
- ✅ Best available updates after each pick
- ✅ Draft history displays all picks correctly
- ✅ No crashes or errors

### Scenario 1.2: 10-Team Linear Draft
**Objective:** Test larger draft with linear flow

**Steps:**
1. Clear app data and restart
2. Configure 10 teams with linear flow
3. Name teams: Team 1, Team 2, ..., Team 10
4. Make picks through 3 complete rounds (30 picks)
5. Verify pick order remains 1-2-3-4-5-6-7-8-9-10 for all rounds

**Expected Results:**
- ✅ Linear flow maintains same order each round
- ✅ App handles 10 teams without performance issues
- ✅ Draft history scrolls smoothly with 30+ picks

### Scenario 1.3: Maximum Teams (20 Teams)
**Objective:** Test upper boundary of team count

**Steps:**
1. Clear app data and restart
2. Configure 20 teams with serpentine flow
3. Make picks through at least 2 rounds (40 picks)
4. Verify performance remains smooth

**Expected Results:**
- ✅ App accepts 20 teams
- ✅ UI remains responsive with 20 teams
- ✅ Draft history handles 40+ picks

---

## Test Suite 2: Error Conditions

### Scenario 2.1: Invalid Team Count
**Objective:** Verify team count validation

**Steps:**
1. Open Config Activity
2. Try to set team count to 1 (below minimum)
3. Try to set team count to 21 (above maximum)

**Expected Results:**
- ✅ App prevents invalid team counts
- ✅ Error message displayed or picker constrained to valid range

### Scenario 2.2: Duplicate Team Names
**Objective:** Verify team name uniqueness

**Steps:**
1. Configure 3 teams
2. Name first team "Champions"
3. Try to name second team "Champions"
4. Attempt to save configuration

**Expected Results:**
- ✅ Error message: "Team name must be unique"
- ✅ Configuration not saved until names are unique

### Scenario 2.3: Empty Team Names
**Objective:** Verify team names are required

**Steps:**
1. Configure 3 teams
2. Leave one team name empty
3. Attempt to save configuration

**Expected Results:**
- ✅ Error message: "Team name cannot be empty"
- ✅ Configuration not saved until all names provided

### Scenario 2.4: Draft Already-Drafted Player
**Objective:** Verify drafted player rejection

**Steps:**
1. Start a draft with 4 teams
2. Make first pick (e.g., Christian McCaffrey)
3. On second pick, try to select the same player

**Expected Results:**
- ✅ Player appears disabled/grayed out in selection dialog
- ✅ If somehow selected, error message: "This player has already been drafted"
- ✅ Pick not recorded

### Scenario 2.5: Incomplete Draft Order
**Objective:** Verify draft order validation

**Steps:**
1. Configure 4 teams
2. Manually set positions to 1, 2, 2, 4 (duplicate position 2, missing position 3)
3. Attempt to save

**Expected Results:**
- ✅ Error message: "All draft positions must be assigned"
- ✅ Configuration not saved

---

## Test Suite 3: Persistence Across App Restarts

### Scenario 3.1: Save and Restore Draft State
**Objective:** Verify draft state persists

**Steps:**
1. Configure 4 teams with serpentine flow
2. Make 5 picks
3. Note current pick (should be Round 2, Pick 2)
4. Note draft history
5. Force close the app (swipe away from recent apps)
6. Relaunch the app

**Expected Results:**
- ✅ Configuration restored (4 teams, serpentine)
- ✅ Current pick restored (Round 2, Pick 2)
- ✅ Draft history restored (all 5 picks visible)
- ✅ Best available player updated correctly
- ✅ Can continue drafting from where left off

### Scenario 3.2: Save Configuration Only
**Objective:** Verify configuration persists without picks

**Steps:**
1. Configure 6 teams with linear flow
2. Save configuration but don't make any picks
3. Force close the app
4. Relaunch the app

**Expected Results:**
- ✅ Configuration restored (6 teams, linear)
- ✅ Draft at initial state (Round 1, Pick 1)
- ✅ No picks in history

### Scenario 3.3: Multiple Save/Load Cycles
**Objective:** Verify persistence reliability

**Steps:**
1. Configure draft and make 3 picks
2. Close and reopen app (verify state)
3. Make 3 more picks
4. Close and reopen app (verify state)
5. Make 3 more picks
6. Close and reopen app (verify state)

**Expected Results:**
- ✅ State correctly restored after each restart
- ✅ No data loss or corruption
- ✅ Pick count accumulates correctly

---

## Test Suite 4: Draft Reset Functionality

### Scenario 4.1: Reset Mid-Draft
**Objective:** Verify reset clears picks but preserves config

**Steps:**
1. Configure 4 teams with serpentine flow
2. Make 10 picks
3. Tap "Reset Draft"
4. Confirm reset in dialog

**Expected Results:**
- ✅ Confirmation dialog appears
- ✅ After confirmation, all picks cleared from history
- ✅ Current pick returns to Round 1, Pick 1
- ✅ All players marked as available again
- ✅ Team configuration preserved (4 teams, serpentine)
- ✅ Best available player resets to top-ranked player

### Scenario 4.2: Cancel Reset
**Objective:** Verify reset can be cancelled

**Steps:**
1. Configure draft and make 5 picks
2. Tap "Reset Draft"
3. Tap "Cancel" in confirmation dialog

**Expected Results:**
- ✅ Dialog closes
- ✅ Draft state unchanged (5 picks still in history)
- ✅ Current pick unchanged

### Scenario 4.3: Reset After Complete Draft
**Objective:** Verify reset works after draft completion

**Steps:**
1. Configure 4 teams, 5 rounds (20 total picks)
2. Complete entire draft
3. Reset draft
4. Start new draft

**Expected Results:**
- ✅ Reset successful
- ✅ Can start fresh draft with same configuration

---

## Test Suite 5: Player Selection and Best Available

### Scenario 5.1: Best Available Updates
**Objective:** Verify best available calculation

**Steps:**
1. Start fresh draft
2. Note the best available player (should be rank #1)
3. Tap "Make Pick" and select the best available player
4. Verify best available updates to next highest-ranked undrafted player
5. Repeat for 5 picks

**Expected Results:**
- ✅ Best available always shows highest-ranked undrafted player
- ✅ Updates immediately after each pick
- ✅ Never shows a drafted player

### Scenario 5.2: Manual Player Selection
**Objective:** Verify manual selection works

**Steps:**
1. Start draft
2. Tap "Make Pick"
3. Use search to find a specific player (e.g., "Kelce")
4. Select that player (not the best available)
5. Verify pick recorded correctly

**Expected Results:**
- ✅ Search filters player list
- ✅ Can select any available player
- ✅ Pick recorded with correct player
- ✅ Best available updates correctly

### Scenario 5.3: View All Players
**Objective:** Verify player list dialog

**Steps:**
1. Start draft
2. Tap "View All Players" button
3. Scroll through player list
4. Verify drafted players are marked/disabled
5. Close dialog without selecting

**Expected Results:**
- ✅ Dialog shows all players
- ✅ Drafted players clearly indicated
- ✅ Can close without making selection

---

## Test Suite 6: UI and Navigation

### Scenario 6.1: Configuration Changes
**Objective:** Verify configuration can be modified

**Steps:**
1. Configure 4 teams with serpentine
2. Make 3 picks
3. Tap "Edit Config"
4. Change team count to 6
5. Add 2 more teams
6. Change flow to linear
7. Save configuration

**Expected Results:**
- ✅ Configuration updates successfully
- ✅ Pick sequence recalculated
- ✅ Existing picks preserved (if valid)
- ✅ Current pick updated based on new configuration

### Scenario 6.2: Screen Rotation
**Objective:** Verify app handles rotation

**Steps:**
1. Start draft with 4 teams
2. Make 5 picks
3. Rotate device to landscape
4. Verify UI displays correctly
5. Make another pick
6. Rotate back to portrait
7. Verify UI displays correctly

**Expected Results:**
- ✅ Layout adjusts to orientation
- ✅ No data loss on rotation
- ✅ All UI elements accessible in both orientations

### Scenario 6.3: Back Navigation
**Objective:** Verify back button behavior

**Steps:**
1. From main screen, tap "Edit Config"
2. Press back button (should return to main screen)
3. Tap "Make Pick"
4. Press back button (should close dialog)
5. Press back button on main screen (should exit app)

**Expected Results:**
- ✅ Back button navigates correctly
- ✅ No unexpected exits or crashes

---

## Test Suite 7: Edge Cases

### Scenario 7.1: All Players Drafted
**Objective:** Verify behavior when player pool exhausted

**Steps:**
1. Configure 2 teams
2. Draft all 300 players (150 picks each)
3. Verify best available behavior

**Expected Results:**
- ✅ App handles exhausted player pool gracefully
- ✅ Best available shows "No players available" or similar
- ✅ No crashes

### Scenario 7.2: Rapid Picks
**Objective:** Verify app handles quick succession picks

**Steps:**
1. Start draft
2. Make 10 picks as quickly as possible
3. Verify all picks recorded correctly

**Expected Results:**
- ✅ All picks recorded
- ✅ No duplicate picks
- ✅ Draft history accurate
- ✅ No UI glitches

### Scenario 7.3: Long Team Names
**Objective:** Verify UI handles long text

**Steps:**
1. Configure teams with very long names (50+ characters)
2. Make picks
3. Verify UI displays correctly

**Expected Results:**
- ✅ Long names don't break layout
- ✅ Text truncates or wraps appropriately
- ✅ All UI elements remain accessible

---

## Test Suite 8: Performance and Stability

### Scenario 8.1: Extended Session
**Objective:** Verify app stability over time

**Steps:**
1. Configure 12 teams
2. Complete 10 rounds (120 picks)
3. Monitor for memory leaks or slowdowns

**Expected Results:**
- ✅ App remains responsive throughout
- ✅ No memory warnings
- ✅ No crashes

### Scenario 8.2: Multiple Configuration Changes
**Objective:** Verify stability with frequent config changes

**Steps:**
1. Configure draft
2. Make 5 picks
3. Change configuration
4. Make 5 more picks
5. Change configuration again
6. Repeat 5 times

**Expected Results:**
- ✅ App handles configuration changes gracefully
- ✅ No data corruption
- ✅ No crashes

---

## Defect Reporting Template

If you find any issues during testing, document them using this template:

```
**Issue ID:** [Unique identifier]
**Severity:** [Critical / High / Medium / Low]
**Test Scenario:** [Which scenario from above]
**Steps to Reproduce:**
1. [Step 1]
2. [Step 2]
3. [Step 3]

**Expected Result:** [What should happen]
**Actual Result:** [What actually happened]
**Screenshots:** [If applicable]
**Device/Emulator:** [Device model and Android version]
**Reproducibility:** [Always / Sometimes / Once]
```

---

## Sign-Off Checklist

Before considering the app ready for release, verify:

- [ ] All Test Suite 1 scenarios pass (Complete Draft Scenarios)
- [ ] All Test Suite 2 scenarios pass (Error Conditions)
- [ ] All Test Suite 3 scenarios pass (Persistence)
- [ ] All Test Suite 4 scenarios pass (Draft Reset)
- [ ] All Test Suite 5 scenarios pass (Player Selection)
- [ ] All Test Suite 6 scenarios pass (UI and Navigation)
- [ ] All Test Suite 7 scenarios pass (Edge Cases)
- [ ] All Test Suite 8 scenarios pass (Performance)
- [ ] No critical or high severity defects remain
- [ ] App tested on at least 2 different screen sizes
- [ ] App tested on at least 2 different Android versions
- [ ] All automated tests pass (unit, property, integration)

---

## Notes for Testers

1. **Take your time:** Manual testing is thorough work. Don't rush.
2. **Document everything:** Even minor issues should be noted.
3. **Think like a user:** Try unexpected actions and edge cases.
4. **Test on real devices:** Emulators are good, but real devices reveal more issues.
5. **Fresh eyes:** If possible, have someone unfamiliar with the app test it.

## Automated Test Verification

Before manual testing, ensure all automated tests pass:

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

All tests should pass before beginning manual testing.
