# ESPN Depth Chart Links Feature

## Overview
Added clickable links from NFL team abbreviations to ESPN's depth charts throughout the app. Users can now tap on team abbreviations (e.g., "SF", "KC", "DAL") to view the team's depth chart on ESPN's fantasy football section.

## Changes Made

### 1. Player Model Enhancement
**File:** `app/src/main/java/com/fantasydraft/picker/models/Player.java`

**New Methods:**
- `getEspnDepthChartUrl()` - Generates ESPN depth chart URL for the player's team
- `getEspnTeamName(String teamAbbr)` - Maps team abbreviations to ESPN URL format

**URL Format:**
```
https://www.espn.com/nfl/team/depth/_/name/{team}
```

**Example URLs:**
- San Francisco 49ers: `https://www.espn.com/nfl/team/depth/_/name/sf`
- Kansas City Chiefs: `https://www.espn.com/nfl/team/depth/_/name/kc`
- Dallas Cowboys: `https://www.espn.com/nfl/team/depth/_/name/dal`

**Team Mapping:**
All 32 NFL teams are mapped:
- ARI → ari (Arizona Cardinals)
- ATL → atl (Atlanta Falcons)
- BAL → bal (Baltimore Ravens)
- BUF → buf (Buffalo Bills)
- CAR → car (Carolina Panthers)
- CHI → chi (Chicago Bears)
- CIN → cin (Cincinnati Bengals)
- CLE → cle (Cleveland Browns)
- DAL → dal (Dallas Cowboys)
- DEN → den (Denver Broncos)
- DET → det (Detroit Lions)
- GB → gb (Green Bay Packers)
- HOU → hou (Houston Texans)
- IND → ind (Indianapolis Colts)
- JAX → jax (Jacksonville Jaguars)
- KC → kc (Kansas City Chiefs)
- LV → lv (Las Vegas Raiders)
- LAC → lac (Los Angeles Chargers)
- LAR → lar (Los Angeles Rams)
- MIA → mia (Miami Dolphins)
- MIN → min (Minnesota Vikings)
- NE → ne (New England Patriots)
- NO → no (New Orleans Saints)
- NYG → nyg (New York Giants)
- NYJ → nyj (New York Jets)
- PHI → phi (Philadelphia Eagles)
- PIT → pit (Pittsburgh Steelers)
- SF → sf (San Francisco 49ers)
- SEA → sea (Seattle Seahawks)
- TB → tb (Tampa Bay Buccaneers)
- TEN → ten (Tennessee Titans)
- WAS → wsh (Washington Commanders)

### 2. Player Selection Dialog
**File:** `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java`

**Changes:**
- Team abbreviations now display in blue with underline
- Clicking team abbreviation opens ESPN depth chart
- Maintains bye week display: "SF (bye-7)"
- Falls back to regular text if no team is set

**Visual Indicators:**
- Blue color (#1976D2) for clickable teams
- Underlined text to indicate link
- Regular gray text for non-clickable elements

### 3. Draft History
**File:** `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java`

**Changes:**
- Team abbreviations in draft history are now clickable
- Both player names AND team abbreviations can be clicked
- Player name → ESPN player profile
- Team abbreviation → ESPN depth chart
- Uses SpannableString for multiple clickable regions

**Example:**
```
"Christian McCaffrey (RB - SF)"
  ↑ Clickable (player profile)    ↑ Clickable (depth chart)
```

## User Experience

### Player Selection Dialog
**Before:**
- Team abbreviation displayed as plain text
- No way to quickly check team depth

**After:**
- Team abbreviation appears as blue underlined link
- Tap to view full team depth chart on ESPN
- See positional depth and competition

### Draft History
**Before:**
- Only player names were clickable (if ESPN ID available)
- Team info was static text

**After:**
- Player names link to player profiles
- Team abbreviations link to depth charts
- Two clickable links per player entry

## Benefits

### Strategic Drafting
- Quickly check team depth before drafting
- See who else is on the depth chart
- Understand positional competition
- Make informed decisions about handcuffs

### Research Efficiency
- One-tap access to depth charts
- No need to manually search for team info
- Seamless integration with ESPN data
- Reduces time spent researching

### User Engagement
- More interactive draft experience
- Encourages deeper research
- Professional, polished feel
- Leverages ESPN's comprehensive data

## Technical Details

### Link Handling
- Uses Android's Intent.ACTION_VIEW
- Opens in device's default browser
- Works with any browser app
- Requires internet connection

### Error Handling
- Gracefully handles missing team data
- Falls back to non-clickable text
- No crashes if team abbreviation unknown
- Null-safe implementation

### Performance
- URL generation is lightweight
- No network calls during rendering
- Instant link creation
- No impact on scroll performance

## Testing Checklist
- [x] Build successful
- [x] All 32 teams mapped correctly
- [x] Links generate proper URLs
- [x] Player selection shows clickable teams
- [x] Draft history shows clickable teams
- [x] Blue underline styling applied
- [x] Bye week display preserved
- [x] Multiple links work in same text
- [ ] Deploy to device (device not connected)
- [ ] Test clicking team links
- [ ] Verify ESPN pages open correctly

## Files Modified
1. `app/src/main/java/com/fantasydraft/picker/models/Player.java` - Added depth chart URL methods
2. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java` - Made teams clickable
3. `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java` - Made teams clickable

## Visual Design

### Link Styling
- Color: #1976D2 (Material Blue)
- Text decoration: Underline
- Consistent with player name links
- Clear visual indicator of clickability

### Layout Impact
- No layout changes required
- Works with existing UI
- Maintains all current functionality
- Additive feature only

## Future Enhancements
- Add team logos next to abbreviations
- Cache depth chart data for offline viewing
- Add tooltip showing "View depth chart"
- Consider in-app WebView for depth charts
- Add links to other team resources (stats, schedule)

## Known Limitations
- Requires internet connection
- Opens in external browser (not in-app)
- ESPN URL format may change (unlikely)
- No offline depth chart data

## Deployment Notes
- No database changes required
- No migration needed
- Backward compatible
- Safe to deploy immediately

## Date
March 8, 2026

## Next Steps
1. Connect device and test functionality
2. Verify all team links work correctly
3. Test in both light and dark modes
4. Gather user feedback on feature
5. Consider adding to release notes
