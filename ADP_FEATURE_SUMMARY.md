# ADP (Average Draft Position) Feature Implementation

## Summary

Successfully renamed "PFF Rank" to "ADP Rank" throughout the application and created a script to fetch ADP data from FantasyPros.

## Changes Made

### 1. UI Updates
- **item_player_selection.xml**: Changed "PFF:1" to "ADP:1"
- **item_draft_pick.xml**: Changed "PFF:1" to "ADP:1"
- **PlayerSelectionAdapter.java**: Updated display text from "PFF:" to "ADP:"
- **DraftHistoryAdapter.java**: Updated display text from "PFF:" to "ADP:"
- **DraftCsvExporter.java**: Updated CSV header from "PFF Rank" to "ADP Rank"

### 2. New Scripts Created

#### `scripts/fetch_fantasypros_adp.py`
Python script that:
- Fetches ADP (Average Draft Position) data from FantasyPros
- Scrapes the PPR (Points Per Reception) ADP rankings
- Updates the `pffRank` field in `players.json` with ADP values
- Handles name matching with normalization (removes Jr., Sr., etc.)
- Provides fallback for partial name matches

#### `scripts/fetch_adp.bat`
Windows batch file wrapper for easy execution

### 3. Data Source

**FantasyPros ADP Rankings**
- URL: https://www.fantasypros.com/nfl/adp/ppr-overall.php
- Format: PPR (Points Per Reception) scoring
- Updates: Regularly updated by FantasyPros based on real draft data
- Free access: No authentication required

## How to Use

### Fetch ADP Data

```bash
# Option 1: Run the batch file (Windows)
scripts\fetch_adp.bat

# Option 2: Run Python directly
python scripts/fetch_fantasypros_adp.py
```

### What It Does

1. Fetches current ADP rankings from FantasyPros
2. Matches players by name (with fuzzy matching for variations)
3. Updates the `pffRank` field in `app/src/main/res/raw/players.json`
4. Reports how many players were successfully matched

### After Running

1. **Review the updated file**: Check `app/src/main/res/raw/players.json`
2. **Build the app**: `.\gradlew assembleDebug --console=plain`
3. **Deploy to device**: `C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk`

## Display Format

The app now displays ADP rankings as:
- **Player Selection Dialog**: "ADP:15" (if available)
- **Draft History**: "ADP:15" (if available)
- **CSV Export**: Column header "ADP Rank"

If ADP data is not available for a player (value = 0), the ADP rank is hidden.

## Data Structure

The `pffRank` field in `players.json` now contains ADP values:

```json
{
  "id": "10000",
  "name": "Ja'Marr Chase",
  "position": "WR",
  "rank": 1,
  "pffRank": 5,  // <-- ADP value from FantasyPros
  "positionRank": 1,
  "nflTeam": "CIN",
  "lastYearStats": "125 rec, 1412 rec yds, 15 rec TDs",
  "injuryStatus": "HEALTHY",
  "espnId": "10000"
}
```

## Benefits

1. **Dual Rankings**: Users can now see both ESPN rankings and FantasyPros ADP
2. **Real Draft Data**: ADP reflects where players are actually being drafted
3. **Value Identification**: Helps identify players being drafted later than their ESPN rank
4. **Free Data Source**: FantasyPros ADP is publicly available

## Technical Notes

### Field Name
- The internal field name remains `pffRank` for backward compatibility
- Only the display text changed from "PFF" to "ADP"
- This avoids database migration issues

### Name Matching
The script uses intelligent name matching:
- Normalizes names (removes Jr., Sr., III, etc.)
- Tries exact match first
- Falls back to last name matching
- Reports unmatched players

### Dependencies
```bash
pip install requests beautifulsoup4
```

## Future Enhancements

Potential improvements:
- Automated weekly ADP updates
- Support for different scoring formats (Standard, Half-PPR)
- ADP trend tracking (rising/falling)
- Integration with the in-app refresh feature

## Troubleshooting

### Script Errors

**"Module not found"**
```bash
pip install requests beautifulsoup4
```

**"Could not find ADP table"**
- FantasyPros may have changed their page structure
- Check the URL is still valid
- Verify internet connection

### Low Match Rate

If many players aren't matched:
1. Check player names in `players.json` match FantasyPros format
2. Review the script output for unmatched names
3. Manually adjust names if needed

## Deployment Status

✅ Code changes complete
✅ Build successful
⏸️ Device deployment pending (device not connected)

When device is connected, deploy with:
```bash
C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```
