# Player Data Update Scripts

This directory contains scripts to update player data for the Fantasy Draft Picker app.

## Quick Start

### Update Player Data

```bash
# Install required Python package
pip install requests

# Run the update script
python scripts/update_players.py
```

This will:
1. Fetch current player rankings from ESPN Fantasy Football
2. Transform the data to the app's format
3. Generate `app/src/main/res/raw/players.json`
4. Include QBs, RBs, WRs, TEs, Kickers, and Defense/Special Teams

### After Updating

1. **Review the generated file**: Check `app/src/main/res/raw/players.json`
2. **Build the app**: `.\gradlew assembleDebug --console=plain`
3. **Deploy to device**: `C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk`

## Data Sources

### ESPN Fantasy Football API
- **Endpoint**: `https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3`
- **Parameters**: `?view=kona_player_info`
- **Public Access**: No authentication required for default league rankings

### Fallback Data
If the ESPN API is unavailable, the script generates fallback data with top players by position.

## Player Data Format

Each player in `players.json` includes:

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

### Fields

- **id**: Unique identifier (string)
- **name**: Player full name
- **position**: QB, RB, WR, TE, K, or DST
- **rank**: Overall fantasy ranking
- **pffRank**: Pro Football Focus ranking (0 if unavailable)
- **positionRank**: Rank within position (RB1, WR2, etc.)
- **nflTeam**: NFL team abbreviation (SF, KC, DAL, etc.)
- **lastYearStats**: Previous season statistics (position-specific format)
- **injuryStatus**: HEALTHY, QUESTIONABLE, DOUBTFUL, OUT, or IR
- **espnId**: ESPN player ID for API integration

### Statistics Format by Position

- **QB**: "YDS, TD, INT" (e.g., "4500 YDS, 35 TD, 10 INT")
- **RB**: "YDS, TD, REC" (e.g., "1459 YDS, 14 TD, 67 REC")
- **WR/TE**: "YDS, TD, REC" (e.g., "1799 YDS, 13 TD, 119 REC")
- **K**: "FG, XP, PTS" (e.g., "33/35 FG, 52/53 XP, 171 PTS")
- **DST**: "SACK, INT, FR, TD" (e.g., "48 SACK, 17 INT, 10 FR, 4 TD")

## Customization

### Adding More Players

Edit the `generate_fallback_data()` function to add more players to each position list.

### Changing Data Source

To use a different API:
1. Update the `fetch_espn_rankings()` function
2. Adjust the `transform_to_app_format()` function to match the new API's structure

### Manual Updates

You can also manually edit `app/src/main/res/raw/players.json` directly:
1. Open the file in your IDE
2. Add/modify player entries
3. Ensure JSON is valid
4. Rebuild and deploy the app

## Troubleshooting

### Script Errors

**"Module 'requests' not found"**
```bash
pip install requests
```

**"ESPN API returned error"**
- The script will automatically use fallback data
- Check your internet connection
- ESPN API may be temporarily unavailable

### Invalid JSON

If the generated JSON is invalid:
1. Check the console output for errors
2. Validate JSON at https://jsonlint.com/
3. Ensure all player entries have required fields

### App Not Loading Data

1. Verify `players.json` is in `app/src/main/res/raw/`
2. Check Android Studio build output for errors
3. Clean and rebuild: `.\gradlew clean assembleDebug`

## Future Enhancements

Potential improvements:
- Fetch PFF rankings from Pro Football Focus API
- Include current season projections
- Add injury status from real-time sources
- Automated weekly updates
- Integration with multiple fantasy platforms

## Support

For issues or questions:
1. Check the main README.md
2. Review CURRENT_STATUS.md for app features
3. Examine the generated players.json for data issues


---

## NEW: ESPN Player Scraper (scrape_espn_players.py)

**Enhanced scraper that fetches 300+ players with 2024 stats from ESPN Fantasy Football**

### Setup

```bash
cd scripts
pip install -r requirements.txt
```

### Usage

```bash
python scrape_espn_players.py
```

### Features

- Fetches 300+ players from ESPN Fantasy Football API
- Includes actual 2024 season statistics
- Position-specific stats (passing, rushing, receiving, etc.)
- Injury status information
- ESPN player IDs for integration
- Automatic position rank calculation
- Respects ESPN's rate limits

### What It Fetches

**Positions**: QB, RB, WR, TE, K, DEF

**Stats by Position**:
- **QB**: Passing yards, passing TDs, interceptions
- **RB**: Rushing yards, rushing TDs, receptions, receiving yards
- **WR/TE**: Receptions, receiving yards, receiving TDs
- **K**: Field goals made, extra points made
- **DEF**: Sacks, interceptions

### Output

Updates `app/src/main/res/raw/players.json` with 300+ players including:
- Current rankings
- 2024 season stats
- Injury status
- NFL team information
- Position ranks

### After Running

1. Review the generated `players.json`
2. Rebuild the app: `.\gradlew assembleDebug`
3. Deploy: `adb install -r app\build\outputs\apk\debug\app-debug.apk`
