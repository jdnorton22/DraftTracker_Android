# Implementation Plan: Player Data Refresh

## Overview

This plan implements a player data refresh feature that fetches updated information from ESPN Fantasy Football and resets the draft state to ensure data consistency.

## Tasks

- [x] 1. Create ESPNDataFetcher class
  - [x] 1.1 Implement HTTP request to ESPN API
    - Create ESPNDataFetcher.java class
    - Add HTTP client (HttpURLConnection or OkHttp)
    - Implement fetchPlayerData() method with callback
    - Set 30-second timeout
    - Handle network errors (no connection, timeout, server error)
    - _Requirements: 2.1, 2.3, 2.4, 7.5_

  - [x] 1.2 Add error handling and retry logic
    - Define FetchError enum (NO_NETWORK, SERVER_UNREACHABLE, TIMEOUT, INVALID_RESPONSE)
    - Check network connectivity before fetch
    - Handle HTTP status codes (4xx, 5xx)
    - Return appropriate error types via callback
    - _Requirements: 2.3, 2.4, 6.1, 6.2_

- [x] 2. Create PlayerDataParser class
  - [x] 2.1 Implement JSON parsing logic
    - Create PlayerDataParser.java class
    - Implement parseESPNData(String jsonData) method
    - Parse player objects from ESPN JSON format
    - Extract: id, name, position, rank, team, injury status, stats
    - Handle missing or null fields gracefully
    - _Requirements: 2.2, 2.5_

  - [x] 2.2 Add position-specific stat parsing
    - Parse QB stats: passing yards, TDs, INTs
    - Parse RB stats: rushing yards, TDs, receptions
    - Parse WR/TE stats: receiving yards, TDs, receptions
    - Format stats as strings for display
    - _Requirements: 2.2_

  - [x] 2.3 Add data validation
    - Validate minimum player count (at least 100 players)
    - Validate required fields present (id, name, position, rank)
    - Skip players with missing critical data
    - Throw ParseException for invalid data
    - _Requirements: 2.5, 6.3_

- [x] 3. Create PlayerDataRefreshManager class
  - [x] 3.1 Implement refresh orchestration
    - Create PlayerDataRefreshManager.java class
    - Add constructor with dependencies (Context, PlayerManager, DraftCoordinator, PersistenceManager)
    - Implement refreshPlayerData(RefreshCallback callback) method
    - Coordinate: fetch → parse → validate → write → reset → save
    - _Requirements: 1.3, 3.1, 3.2, 3.3, 3.4, 3.5_

  - [x] 3.2 Implement draft state check
    - Add isDraftInProgress() method
    - Check if pick history is not empty
    - Return boolean indicating draft status
    - _Requirements: 5.5, 8.5_

  - [x] 3.3 Implement file writing
    - Write parsed players to internal storage
    - Format as JSON matching existing players.json structure
    - Create backup of existing file before overwrite
    - Handle IOException and storage errors
    - _Requirements: 4.1, 4.4, 6.4_

  - [x] 3.4 Implement draft reset logic
    - Clear all pick history
    - Reset all player draft status to false
    - Clear all team rosters
    - Reset draft state to Round 1, Pick 1
    - Save clean state to persistence
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

  - [x] 3.5 Add comprehensive error handling
    - Wrap all operations in try-catch blocks
    - Map exceptions to user-friendly error messages
    - Ensure data consistency on error (rollback if needed)
    - Call callback.onRefreshError() with appropriate message
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [x] 4. Update ConfigActivity UI
  - [x] 4.1 Add refresh button to layout
    - Open activity_config.xml
    - Add "Refresh Player Data" button
    - Use outlined button style
    - Add refresh icon (ic_menu_rotate)
    - Position below existing configuration options
    - _Requirements: 1.1, 8.1, 8.2_

  - [x] 4.2 Initialize refresh button in ConfigActivity
    - Add button field to ConfigActivity
    - Initialize in onCreate()
    - Set up click listener to call showRefreshConfirmation()
    - _Requirements: 1.1, 8.1_

  - [x] 4.3 Implement confirmation dialog
    - Create showRefreshConfirmation() method
    - Check if draft is in progress using PlayerDataRefreshManager
    - If draft in progress: show warning dialog
    - If no draft: proceed directly to refresh
    - Dialog message: "This will fetch the latest player data from ESPN and reset your current draft. All picks will be cleared. Continue?"
    - Buttons: Cancel (dismiss), Refresh (call performRefresh())
    - _Requirements: 1.2, 5.1, 5.2, 5.3, 5.4, 5.5, 8.4, 8.5_

  - [x] 4.4 Implement progress dialog
    - Create showRefreshProgress() method
    - Show indeterminate progress dialog
    - Title: "Refreshing Player Data"
    - Message: "Fetching latest data from ESPN..."
    - Non-cancelable
    - Create dismissRefreshProgress() method
    - _Requirements: 1.4, 7.1, 7.2, 7.4_

  - [x] 4.5 Implement refresh execution
    - Create performRefresh() method
    - Show progress dialog
    - Create PlayerDataRefreshManager instance
    - Call refreshPlayerData() with callback
    - On success: dismiss progress, show success toast with player count
    - On error: dismiss progress, show error dialog with message
    - Disable refresh button during operation
    - Re-enable button after completion
    - _Requirements: 1.3, 1.4, 1.5, 7.3, 8.3_

- [x] 5. Update MainActivity for data reload
  - [x] 5.1 Add player data reload method
    - Create reloadPlayerData() method
    - Clear existing players from PlayerManager
    - Reload players from updated players.json file
    - Update UI to reflect new data
    - _Requirements: 4.2, 4.3_

  - [x] 5.2 Add refresh completion handler
    - Create onPlayerDataRefreshed() method
    - Call reloadPlayerData()
    - Reset UI to initial state
    - Update best available player display
    - _Requirements: 4.2, 4.3_

  - [x] 5.3 Add broadcast receiver or callback
    - Implement mechanism for ConfigActivity to notify MainActivity
    - Option 1: Use LocalBroadcastManager
    - Option 2: Use result code in onActivityResult()
    - MainActivity receives notification and calls onPlayerDataRefreshed()
    - _Requirements: 4.2, 4.3_

- [x] 6. Add network permissions
  - Update AndroidManifest.xml
  - Add INTERNET permission
  - Add ACCESS_NETWORK_STATE permission
  - _Requirements: 2.1, 2.4_

- [x] 7. Add string resources
  - Add refresh button text
  - Add confirmation dialog strings
  - Add progress dialog strings
  - Add success message format
  - Add error message strings
  - _Requirements: 1.1, 1.2, 1.4, 1.5, 6.1, 6.2, 6.3, 6.4_

- [x] 8. Testing and validation
  - [x] 8.1 Test successful refresh flow
    - Start with no draft in progress
    - Tap refresh button
    - Verify no confirmation dialog
    - Verify progress dialog appears
    - Verify success message shows
    - Verify player data updated
    - _Requirements: All_

  - [x] 8.2 Test refresh with draft in progress
    - Make several draft picks
    - Tap refresh button
    - Verify confirmation dialog appears
    - Tap "Refresh" to confirm
    - Verify draft resets completely
    - Verify player data updated
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 5.1, 5.2, 5.3, 5.4, 5.5_

  - [x] 8.3 Test error scenarios
    - Test with no network connection
    - Test with airplane mode
    - Verify appropriate error messages
    - Verify data not corrupted
    - _Requirements: 2.3, 2.4, 6.1, 6.2, 6.5_

  - [x] 8.4 Test cancellation
    - Tap refresh button
    - Tap "Cancel" in confirmation dialog
    - Verify refresh aborted
    - Verify draft state unchanged
    - _Requirements: 5.4_

  - [x] 8.5 Build and deploy to device
    - Build debug APK
    - Install on Surface Duo
    - Test all scenarios on actual device
    - _Requirements: All_

- [x] 9. Documentation
  - Create user guide for refresh feature
  - Document ESPN API endpoint and data format
  - Add troubleshooting section for common errors
  - _Requirements: All_

## Notes

- ESPN API endpoint may require investigation to find correct URL
- Consider using OkHttp library for better HTTP handling
- File writing to res/raw may require alternative approach (write to internal storage instead)
- Progress dialog should run on UI thread, network operations on background thread
- Consider adding a "Last Updated" timestamp to show when data was last refreshed
- May need to handle ESPN API rate limiting or authentication in future

## Implementation Order

1. Start with ESPNDataFetcher (tasks 1.1, 1.2)
2. Then PlayerDataParser (tasks 2.1, 2.2, 2.3)
3. Then PlayerDataRefreshManager (tasks 3.1-3.5)
4. Then UI updates (tasks 4.1-4.5, 5.1-5.3)
5. Finally testing (tasks 8.1-8.5)
