# Design Document: Player Data Refresh

## Overview

This design implements an on-demand player data refresh feature that fetches updated player information from ESPN Fantasy Football and resets the draft state to ensure data consistency. The feature includes network operations, data parsing, file I/O, and comprehensive error handling.

## Architecture

### Component Structure

```
ConfigActivity
├── RefreshPlayerDataButton (new)
└── PlayerDataRefreshManager (new)
    ├── ESPNDataFetcher (new)
    ├── PlayerDataParser (new)
    └── DraftResetCoordinator (existing)

MainActivity
└── Receives refresh completion broadcast
```

### Data Flow

1. User taps "Refresh Player Data" button in ConfigActivity
2. System shows confirmation dialog (if draft in progress)
3. User confirms → RefreshPlayerDataButton triggers refresh
4. PlayerDataRefreshManager orchestrates the operation:
   - ESPNDataFetcher fetches data from ESPN
   - PlayerDataParser parses and validates data
   - Writes updated data to players.json
   - DraftResetCoordinator resets draft state
   - Saves clean state to persistence
5. System shows success/error message
6. MainActivity reloads player data

## Components and Interfaces

### 1. PlayerDataRefreshManager (New)

**Responsibility:** Orchestrates the entire refresh operation

**Methods:**
```java
public class PlayerDataRefreshManager {
    private Context context;
    private PlayerManager playerManager;
    private DraftCoordinator draftCoordinator;
    private PersistenceManager persistenceManager;
    
    public interface RefreshCallback {
        void onRefreshStart();
        void onRefreshSuccess(int playerCount);
        void onRefreshError(String errorMessage);
    }
    
    public void refreshPlayerData(RefreshCallback callback)
    public boolean isDraftInProgress()
}
```

### 2. ESPNDataFetcher (New)

**Responsibility:** Fetches player data from ESPN Fantasy Football

**Methods:**
```java
public class ESPNDataFetcher {
    private static final String ESPN_API_URL = "https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3";
    private static final int TIMEOUT_SECONDS = 30;
    
    public interface FetchCallback {
        void onFetchSuccess(String jsonData);
        void onFetchError(FetchError error);
    }
    
    public void fetchPlayerData(FetchCallback callback)
}
```

**Error Types:**
- NO_NETWORK: Device has no internet connection
- SERVER_UNREACHABLE: Cannot reach ESPN servers
- TIMEOUT: Request took longer than 30 seconds
- INVALID_RESPONSE: Server returned non-200 status

### 3. PlayerDataParser (New)

**Responsibility:** Parses ESPN JSON data into Player objects

**Methods:**
```java
public class PlayerDataParser {
    public List<Player> parseESPNData(String jsonData) throws ParseException
    private Player parsePlayerObject(JSONObject playerJson)
    private String parseInjuryStatus(JSONObject playerJson)
    private String parseLastYearStats(JSONObject playerJson, String position)
}
```

### 4. ConfigActivity (Modified)

**New UI Elements:**
- Button: "Refresh Player Data"
- ProgressDialog: Shows during refresh operation
- AlertDialog: Confirmation before refresh

**New Methods:**
```java
private void setupRefreshButton()
private void showRefreshConfirmation()
private void performRefresh()
private void showRefreshProgress()
private void dismissRefreshProgress()
```

### 5. MainActivity (Modified)

**New Methods:**
```java
public void onPlayerDataRefreshed()
private void reloadPlayerData()
```

## Data Models

### Player Model (No Changes)

Existing Player model already has all required fields:
- id, name, position, rank, pffRank, positionRank
- nflTeam, injuryStatus, lastYearStats, espnId
- drafted, draftedBy

### ESPN API Response Format

```json
{
  "players": [
    {
      "id": 4242335,
      "fullName": "Christian McCaffrey",
      "defaultPositionId": 2,
      "stats": [...],
      "injuryStatus": "ACTIVE",
      "proTeamId": 25,
      "rankings": {
        "overall": 1,
        "position": 1
      }
    }
  ]
}
```

## Implementation Details

### Refresh Operation Flow

```
1. User taps "Refresh Player Data"
2. Check if draft in progress
   ├─ Yes → Show confirmation dialog
   │         ├─ Cancel → Abort
   │         └─ Confirm → Continue to step 3
   └─ No → Continue to step 3
3. Show progress dialog
4. Fetch data from ESPN (async)
5. Parse JSON data
6. Validate player data
7. Write to players.json file
8. Reset draft state:
   - Clear pick history
   - Reset player draft status
   - Clear team rosters
   - Reset to Round 1, Pick 1
9. Save clean state to persistence
10. Dismiss progress dialog
11. Show success message
12. Notify MainActivity to reload
```

### Network Operation

**Technology:** Android HttpURLConnection or OkHttp
**Threading:** AsyncTask or Kotlin Coroutines
**Timeout:** 30 seconds
**Retry:** No automatic retry (user can manually retry)

### File I/O

**Target File:** `app/src/main/res/raw/players.json`
**Write Location:** Internal storage cache, then copy to res/raw on next build
**Backup:** Keep previous players.json as players.json.backup before overwriting

### Error Handling Strategy

```java
try {
    // Fetch data
    String jsonData = espnDataFetcher.fetchPlayerData();
    
    // Parse data
    List<Player> players = playerDataParser.parseESPNData(jsonData);
    
    // Validate
    if (players.size() < 100) {
        throw new ValidationException("Insufficient player data");
    }
    
    // Write to file
    writePlayersToFile(players);
    
    // Reset draft
    resetDraftState();
    
    // Success
    callback.onSuccess(players.size());
    
} catch (NetworkException e) {
    callback.onError("No internet connection");
} catch (TimeoutException e) {
    callback.onError("Request timed out");
} catch (ParseException e) {
    callback.onError("Invalid data format");
} catch (IOException e) {
    callback.onError("Unable to save player data");
} catch (Exception e) {
    callback.onError("Refresh failed: " + e.getMessage());
}
```

## UI Design

### ConfigActivity Layout Changes

Add refresh button to existing layout:

```xml
<Button
    android:id="@+id/button_refresh_player_data"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Refresh Player Data"
    android:drawableStart="@android:drawable/ic_menu_rotate"
    style="?attr/materialButtonOutlinedStyle" />
```

### Confirmation Dialog

```
Title: "Refresh Player Data?"
Message: "This will fetch the latest player data from ESPN and reset your current draft. All picks will be cleared. Continue?"
Buttons: [Cancel] [Refresh]
```

### Progress Dialog

```
Title: "Refreshing Player Data"
Message: "Fetching latest data from ESPN..."
Progress: Indeterminate spinner
Cancelable: No
```

### Success Message

```
Toast: "Player data refreshed successfully! 300 players updated."
```

### Error Messages

- "No internet connection. Please check your network and try again."
- "Unable to reach ESPN servers. Please try again later."
- "Request timed out. Please try again."
- "Invalid data format. Please try again later."
- "Unable to save player data. Please check storage space."

## Error Handling

### Network Errors

1. **No Connection**: Check ConnectivityManager before attempting fetch
2. **Timeout**: Set 30-second timeout on HTTP request
3. **Server Error**: Handle 4xx/5xx HTTP status codes
4. **SSL Error**: Handle certificate validation failures

### Data Errors

1. **Parse Error**: Catch JSONException and show user-friendly message
2. **Validation Error**: Check minimum player count, required fields
3. **Incomplete Data**: Skip players with missing critical fields

### File I/O Errors

1. **Write Permission**: Check storage permissions
2. **Disk Full**: Handle IOException for insufficient space
3. **File Corruption**: Restore from backup if write fails

## Testing Strategy

### Unit Tests

1. **PlayerDataParser Tests**:
   - Test parsing valid ESPN JSON
   - Test handling missing fields
   - Test handling invalid data types
   - Test position-specific stat parsing

2. **ESPNDataFetcher Tests**:
   - Mock HTTP responses
   - Test timeout handling
   - Test error status codes
   - Test network unavailable

3. **PlayerDataRefreshManager Tests**:
   - Test full refresh flow
   - Test draft reset logic
   - Test error propagation
   - Test callback invocation

### Integration Tests

1. **End-to-End Refresh**:
   - Start with draft in progress
   - Trigger refresh
   - Verify draft reset
   - Verify player data updated
   - Verify persistence saved

2. **Error Recovery**:
   - Simulate network failure
   - Verify data not corrupted
   - Verify draft state preserved

### Manual Testing

1. Refresh with no draft in progress
2. Refresh with draft in progress (confirm warning)
3. Cancel refresh confirmation
4. Test with no network connection
5. Test with slow network (timeout)
6. Verify player data updates correctly
7. Verify draft resets completely
8. Restart app and verify data persists

## Performance Considerations

### Network Performance

- **Expected fetch time**: 2-5 seconds on good connection
- **Timeout**: 30 seconds maximum
- **Data size**: ~500KB JSON response
- **Compression**: Use gzip encoding if supported

### File I/O Performance

- **Write time**: <1 second for 300 players
- **Parse time**: <500ms for JSON parsing
- **Total operation**: 3-10 seconds typical

### Memory Usage

- **JSON data**: ~500KB in memory during parse
- **Player objects**: ~2MB for 300 players
- **Peak usage**: ~3MB during refresh

## Security Considerations

1. **HTTPS Only**: Use HTTPS for ESPN API calls
2. **Certificate Validation**: Validate SSL certificates
3. **Input Validation**: Sanitize all parsed data
4. **No Credentials**: No authentication required for public ESPN data

## Future Enhancements

1. **Automatic Refresh**: Check for updates on app start
2. **Incremental Updates**: Only update changed players
3. **Multiple Sources**: Support other data sources beyond ESPN
4. **Offline Mode**: Cache data for offline use
5. **Refresh Schedule**: Allow scheduled automatic refreshes
6. **Diff View**: Show what changed after refresh
