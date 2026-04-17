# Position Requirements in Team Roster Dialog

## Summary
Added position roster requirements display to the Team Roster Dialog, showing a compact summary of current position counts vs configured requirements.

## Implementation Details

### Display Format
The requirements are shown in a compact, single-line format below the team selector:
```
✓ QB: 2/1+  ✓ RB: 4/2+  ❌ WR: 1/2+  ✓ TE: 2/1+  ✓ K: 1/1+  ✓ DST: 1/1+
```

### Visual Indicators
- **✓** (Green check) - Position meets minimum requirements
- **❌** (Red X) - Position does not meet minimum requirements
- **⚠️** (Warning) - Position exceeds maximum limit (when max is set)

### Format Explanation
- `QB: 2/1+` means:
  - Current count: 2 players
  - Minimum required: 1
  - Maximum: No limit (indicated by +)
  
- `RB: 3/2-6` means:
  - Current count: 3 players
  - Minimum required: 2
  - Maximum allowed: 6

### Features
1. **Real-time Updates**: Requirements update when switching between teams
2. **Compact Display**: Uses monospace font and emoji indicators for space efficiency
3. **Smart Formatting**: Shows "+" for no limit, or specific max value
4. **Visual Feedback**: Color-coded indicators show compliance at a glance
5. **All Positions**: Displays QB, RB, WR, TE, K, DST in order

### Technical Changes

#### Files Modified
1. **TeamRosterDialog.java**
   - Added `draftConfig` parameter to constructor
   - Added `textPositionRequirements` TextView field
   - Added `updatePositionRequirements()` method to calculate and display counts
   - Integrated with `loadRosterForTeam()` to update on team change

2. **dialog_team_roster.xml**
   - Added position requirements TextView between spinner and roster list
   - Uses monospace font for alignment
   - Small text size (11sp) to minimize space usage
   - Light background to distinguish from other content

3. **DraftFragment.java**
   - Updated `showTeamRosterDialog()` to pass `currentConfig` parameter

### Space Efficiency
- Single line display (wraps on small screens if needed)
- Small font size (11sp)
- Compact notation (emojis + abbreviations)
- Only visible when roster has players
- Minimal vertical space (~24dp including margins)

## User Experience
When viewing a team's roster:
1. Select team from dropdown
2. See position requirements summary immediately below
3. Quickly identify which positions need attention
4. Visual indicators make it easy to spot issues

## Deployment
- Successfully built and deployed to ADB device
- No compilation errors
- Ready for testing
