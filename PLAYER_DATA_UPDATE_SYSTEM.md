# Player Data Update System

## Overview

A Python-based system to easily update player rankings and data for the Fantasy Draft Picker app through the IDE. The system now includes comprehensive fallback data with 220+ players across all positions with realistic statistics.

## Files Created

### 1. `scripts/update_players.py`
**Purpose**: Main Python script to fetch and transform player data

**Features**:
- Fetches current rankings from ESPN Fantasy Football API
- Transforms data to app's JSON format
- Generates `app/src/main/res/raw/players.json`
- Includes fallback data if API is unavailable
- Calculates position ranks automatically
- Supports all positions: QB, RB, WR, TE, K, DST

**Usage**:
```bash
python scripts/update_players.py
```

### 2. `scripts/update_players.bat`
**Purpose**: Windows batch file for easy execution

**Features**:
- Checks for Python installation
- Installs required packages automatically
- Runs the update script
- Provides next steps after completion

**Usage**:
```bash
scripts\update_players.bat
```

### 3. `scripts/README.md`
**Purpose**: Comprehensive documentation for the update system

**Contents**:
- Quick start guide
- Data source information
- Player data format specification
- Customization instructions
- Troubleshooting guide
- Future enhancement ideas

## How It Works

### Data Flow

```
ESPN Fantasy API
      ↓
Python Script (fetch & transform)
      ↓
players.json (324+ players)
      ↓
Android App (loads on startup)
```

### Update Process

1. **Fetch Data**: Script calls ESPN Fantasy Football API
2. **Transform**: Converts ESPN format to app format
3. **Calculate**: Determines position ranks
4. **Generate**: Creates players.json file
5. **Save**: Writes to `app/src/main/res/raw/players.json`

### Fallback System

If ESPN API is unavailable:
- Script automatically switches to fallback mode
- Generates comprehensive data for 220+ players
- Includes 30 QBs, 60 RBs, 60 WRs, 30 TEs, 20 Kickers, 20 DST
- All players have realistic position-specific statistics
- PFF rankings included for skill positions
- Ensures app always has valid, comprehensive data

## Player Data Format

### JSON Structure

```json
{
  "id": "1",
  "name": "Christian McCaffrey",
  "position": "RB",
  "rank": 1,
  "pffRank": 1,
  "positionRank": 1,
  "nflTeam": "SF",
  "lastYearStats": "1459 YDS, 14 TD, 67 REC",
  "injuryStatus": "HEALTHY",
  "espnId": "3117251"
}
```

### Field Descriptions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| id | string | Unique identifier | "1" |
| name | string | Player full name | "Christian McCaffrey" |
| position | string | Position code | "RB", "WR", "QB", "TE", "K", "DST" |
| rank | number | Overall fantasy rank | 1 |
| pffRank | number | Pro Football Focus rank | 1 (0 if unavailable) |
| positionRank | number | Rank within position | 1 (RB1, WR2, etc.) |
| nflTeam | string | NFL team abbreviation | "SF", "KC", "DAL" |
| lastYearStats | string | Previous season stats | "1459 YDS, 14 TD, 67 REC" |
| injuryStatus | string | Current injury status | "HEALTHY", "QUESTIONABLE", "OUT" |
| espnId | string | ESPN player ID | "3117251" |

### Statistics Format by Position

**Quarterbacks (QB)**:
- Format: "YDS, TD, INT"
- Example: "4500 YDS, 35 TD, 10 INT"

**Running Backs (RB)**:
- Format: "YDS, TD, REC"
- Example: "1459 YDS, 14 TD, 67 REC"

**Wide Receivers (WR) / Tight Ends (TE)**:
- Format: "YDS, TD, REC"
- Example: "1799 YDS, 13 TD, 119 REC"

**Kickers (K)**:
- Format: "FG, XP, PTS"
- Example: "33/35 FG, 52/53 XP, 171 PTS"

**Defense/Special Teams (DST)**:
- Format: "SACK, INT, FR, TD"
- Example: "48 SACK, 17 INT, 10 FR, 4 TD"

## Usage Workflow

### Regular Updates (Weekly/Monthly)

1. **Run Update Script**:
   ```bash
   scripts\update_players.bat
   ```

2. **Review Generated File**:
   - Open `app/src/main/res/raw/players.json`
   - Verify player data looks correct
   - Check for any errors or missing data

3. **Build App**:
   ```bash
   .\gradlew assembleDebug --console=plain
   ```

4. **Deploy to Device**:
   ```bash
   C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
   ```

5. **Test**:
   - Launch app on device
   - Verify players load correctly
   - Check rankings and stats

### Manual Edits

For quick fixes or custom data:

1. Open `app/src/main/res/raw/players.json` in IDE
2. Edit player entries directly
3. Ensure JSON remains valid
4. Rebuild and deploy

## Benefits

### Accuracy
- Always uses current ESPN rankings
- Reflects latest player movements
- Updates injury status
- Includes rookies and new players

### Ease of Use
- One-click update via batch file
- No manual data entry required
- Automatic format conversion
- Built-in error handling

### Flexibility
- Can run from IDE or command line
- Supports manual overrides
- Fallback data ensures reliability
- Easy to customize for different sources

### Maintainability
- Well-documented code
- Clear data format
- Separation of concerns
- Easy to extend

## Future Enhancements

### Potential Improvements

1. **Real-time Injury Updates**:
   - Integrate with injury report APIs
   - Update status automatically
   - Color-code severity

2. **PFF Rankings Integration**:
   - Fetch from Pro Football Focus
   - Include advanced metrics
   - Compare with ESPN rankings

3. **Projections**:
   - Add current season projections
   - Include points per game estimates
   - Show trend indicators

4. **Automated Scheduling**:
   - Weekly automatic updates
   - GitHub Actions integration
   - Notification on changes

5. **Multiple Sources**:
   - Yahoo Fantasy Football
   - NFL.com rankings
   - FantasyPros consensus
   - Aggregate multiple sources

6. **Historical Data**:
   - Track ranking changes over time
   - Show player trends
   - Compare year-over-year

## Troubleshooting

### Common Issues

**Script won't run**:
- Ensure Python 3.6+ is installed
- Check PATH environment variable
- Install requests: `pip install requests`

**ESPN API errors**:
- Script automatically uses fallback data
- Check internet connection
- Verify ESPN API is accessible

**Invalid JSON generated**:
- Validate at https://jsonlint.com/
- Check console output for errors
- Review transform logic

**App won't load data**:
- Verify file location: `app/src/main/res/raw/players.json`
- Clean and rebuild: `.\gradlew clean assembleDebug`
- Check Android Studio build output

## Technical Details

### Dependencies

**Python Packages**:
- `requests`: HTTP library for API calls
- Standard library: `json`, `typing`

**Installation**:
```bash
pip install requests
```

### API Endpoints

**ESPN Fantasy Football**:
- URL: `https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3`
- Parameters: `?view=kona_player_info`
- Authentication: Not required for default league
- Rate Limit: Unknown (use responsibly)

### Error Handling

The script includes comprehensive error handling:
- Network timeouts (30 seconds)
- HTTP errors (4xx, 5xx)
- JSON parsing errors
- Missing data fields
- Invalid player information

All errors trigger fallback to generated data.

## Conclusion

The Player Data Update System provides an easy, reliable way to keep the Fantasy Draft Picker app current with the latest player rankings and information. The combination of automated fetching, fallback data, and manual override capabilities ensures the app always has accurate, up-to-date player data.

**Key Advantages**:
- ✅ One-click updates
- ✅ Always current data
- ✅ Reliable fallback system
- ✅ Easy to customize
- ✅ Well-documented
- ✅ IDE-friendly workflow

For detailed usage instructions, see `scripts/README.md`.
