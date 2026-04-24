# Player Import Feature

## Overview
The Player Import feature allows users to upload custom player data files to override the default player rankings and statistics. This enables users to use their own rankings, custom scoring systems, or updated player information.

## Feature Location
- **Screen**: Configuration Screen (Config Fragment)
- **Access**: Main Menu → Settings/Configuration → Scroll to bottom
- **Position**: Below "Refresh Player Data" section

## User Interface

### Import Section Layout
The import feature is presented in a card view with:
1. **Title**: "Import Custom Player Data"
2. **Description**: Brief explanation of the feature
3. **File Format Requirements**: List of technical requirements
4. **Example JSON**: Code sample showing proper format
5. **Import Button**: Triggers the file picker

## File Format Requirements

### Required Structure
- **Format**: JSON array
- **Minimum Players**: 10
- **File Extension**: `.json` (recommended)

### Required Fields
Each player object must contain:
- `id` (string): Unique identifier for the player
- `name` (string): Player's full name
- `position` (string): Player position (QB, RB, WR, TE, K, DST)
- `rank` (integer): Overall player ranking

### Optional Fields
- `pffRank` (integer): PFF/ADP ranking
- `positionRank` (integer): Position-specific ranking (e.g., RB1, WR5)
- `nflTeam` (string): NFL team abbreviation (e.g., "DAL", "KC")
- `lastYearStats` (string): Previous season statistics
- `injuryStatus` (string): Current injury status (HEALTHY, OUT, QUESTIONABLE, DOUBTFUL, IR)
- `espnId` (string): ESPN player ID (optional, for future features)

### Example JSON Format
```json
[
  {
    "id": "10000",
    "name": "Christian McCaffrey",
    "position": "RB",
    "nflTeam": "SF",
    "rank": 1,
    "pffRank": 1,
    "positionRank": 1,
    "lastYearStats": "202 rush yds, 0 rush TDs, 15 rec, 146 rec yds",
    "injuryStatus": "QUESTIONABLE",
    "espnId": "3117251",
    "byeWeek": 7
  }
]
```

## Import Process

### Step-by-Step Flow
1. User taps "Import Players" button
2. System opens file picker (ACTION_GET_CONTENT)
3. User selects a JSON file from device storage
4. System reads and validates the file:
   - Checks JSON syntax
   - Validates required fields
   - Ensures minimum player count
5. If valid, saves to internal storage as `players_updated.json`
6. Shows success dialog with player count
7. User must restart app or reset draft to use new data

### File Storage
- **Location**: App's internal storage (`context.getFilesDir()`)
- **Filename**: `players_updated.json`
- **Priority**: Takes precedence over bundled `players.json` resource
- **Persistence**: Remains until overwritten by another import or app data is cleared

## Error Handling

### Validation Errors
The system validates and provides specific error messages for:

1. **File Read Errors**
   - Cannot open file
   - IO exceptions
   - Permission issues

2. **Parse Errors**
   - Invalid JSON syntax
   - Missing required fields
   - Incorrect data types
   - Insufficient player count (< 10)

3. **Format Errors**
   - Not a JSON array
   - Missing player properties
   - Invalid position codes

### Error Messages
Users receive clear error dialogs explaining:
- What went wrong
- What the file requirements are
- How to fix the issue

## Technical Implementation

### Files Modified
1. **Layout**: `app/src/main/res/layout/fragment_config.xml`
   - Added CardView with instructions and button
   
2. **Fragment**: `app/src/main/java/com/fantasydraft/picker/ui/ConfigFragment.java`
   - Added file picker intent handling
   - Implemented import logic
   - Added validation and error handling

3. **Activity**: `app/src/main/java/com/fantasydraft/picker/ui/ConfigActivity.java`
   - Mirror implementation for activity-based config (if used)

### Key Methods

#### `openFilePicker()`
- Creates ACTION_GET_CONTENT intent
- Accepts multiple MIME types (JSON, text, all files)
- Uses chooser dialog for better compatibility

#### `onActivityResult()`
- Handles file picker result
- Extracts URI from intent
- Calls import method

#### `importPlayersFromUri(Uri uri)`
- Reads file content from URI
- Parses JSON using PlayerDataParser
- Validates player data
- Saves to internal storage
- Shows success/error feedback

#### `savePlayersToInternalStorage(String jsonContent)`
- Writes JSON to `players_updated.json`
- Stored in app's private files directory

### Data Loading Priority
The app loads player data in this order:
1. **First**: Check for `players_updated.json` in internal storage (imported file)
2. **Fallback**: Load bundled `players.json` from resources

This is handled in `PlayerDataLoader.loadPlayers()`.

## User Experience

### Success Flow
1. Tap "Import Players"
2. Select file from device
3. See success dialog: "Successfully imported X players. Restart the app or reset the draft to use the new data."
4. Restart app or reset draft
5. New player data is active

### Error Flow
1. Tap "Import Players"
2. Select invalid file
3. See error dialog with specific issue
4. Fix file and try again

## Use Cases

### Primary Use Cases
1. **Custom Rankings**: Users with their own player evaluation system
2. **League-Specific Scoring**: Adjust rankings for custom scoring rules
3. **Updated Data**: Import more recent player information
4. **Injury Updates**: Reflect latest injury news
5. **Keeper League Adjustments**: Modify rankings based on kept players

### Example Scenarios
- Fantasy analyst wants to use their proprietary rankings
- League commissioner updates player data mid-season
- User prefers a different ranking source (FantasyPros, ESPN, etc.)
- Dynasty league needs multi-year projections

## Limitations

### Current Limitations
1. **No Real-Time Sync**: Manual import required for updates
2. **Single File**: Only one custom file active at a time
3. **No Merge**: Imported data completely replaces default data
4. **Manual Restart**: App restart required to load new data
5. **No Validation Preview**: Can't preview data before committing

### Future Enhancements
- [ ] Auto-sync from URL
- [ ] Multiple ranking profiles
- [ ] Merge with default data option
- [ ] In-app data editor
- [ ] Import history/versioning
- [ ] Validation preview before import
- [ ] Export current data

## Testing

### Test Cases
1. **Valid Import**: Import properly formatted file with 10+ players
2. **Invalid JSON**: Import file with syntax errors
3. **Missing Fields**: Import file missing required fields
4. **Insufficient Players**: Import file with < 10 players
5. **Large File**: Import file with 500+ players
6. **Special Characters**: Import file with unicode/special characters
7. **Empty File**: Import empty or null file
8. **Wrong File Type**: Import non-JSON file

### Manual Testing Steps
1. Create test JSON file with valid data
2. Transfer to device (Downloads, Google Drive, etc.)
3. Open app → Config screen
4. Tap "Import Players"
5. Select test file
6. Verify success message
7. Restart app
8. Verify new data is loaded in player selection

## Security Considerations

### Data Validation
- All JSON is parsed and validated before use
- Malformed data is rejected with clear errors
- No code execution from imported files

### Storage Security
- Files stored in app's private directory
- Not accessible to other apps
- Cleared when app data is cleared

### Privacy
- No data sent to external servers
- Import process is entirely local
- User controls all data

## Support & Troubleshooting

### Common Issues

**Issue**: "No file picker app found"
- **Solution**: Install a file manager app (Files, Google Files, etc.)

**Issue**: "Parse error: Insufficient player data"
- **Solution**: Ensure file has at least 10 player objects

**Issue**: "Failed to parse JSON data"
- **Solution**: Validate JSON syntax using online validator (jsonlint.com)

**Issue**: "No file selected"
- **Solution**: Make sure to tap on the file to select it, not just view it

**Issue**: "New data not showing after import"
- **Solution**: Completely close and restart the app, or reset the draft

### Getting Help
For issues or questions:
1. Check file format matches example exactly
2. Validate JSON syntax
3. Ensure all required fields are present
4. Check app logs for detailed error messages

## Version History

### Version 1.9 (Current)
- Initial release of player import feature
- Basic file picker integration
- JSON validation and parsing
- Error handling and user feedback
- Documentation and examples

### Planned Updates
- Version 2.0: URL-based import
- Version 2.1: Import history
- Version 2.2: Data preview before import
