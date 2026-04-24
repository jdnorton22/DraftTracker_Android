# ESPN Player Profile Links - Re-enabled

## Summary
Re-enabled ESPN player profile links in the Fantasy Draft Picker app. Player names now appear as clickable blue underlined links that open the player's ESPN profile page in the browser.

## Changes Made

### 1. PlayerSelectionAdapter.java
**Location:** `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java`

- Added imports for `Intent` and `Uri` to handle browser navigation
- Re-enabled ESPN link functionality in the `bind()` method:
  - Player names with ESPN IDs are displayed in blue with underline
  - Clicking the name opens the ESPN profile in the browser
  - Players without ESPN IDs display as regular text
  - Uses `player.getEspnUrl()` to construct the URL: `https://www.espn.com/nfl/player/_/id/{espnId}`

### 2. DraftHistoryAdapter.java
**Location:** `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java`

- Added imports for `Intent`, `Uri`, `SpannableString`, `ClickableSpan`, and related text styling classes
- Re-enabled ESPN link functionality in the `onBindViewHolder()` method:
  - Uses `SpannableString` to make only the player name portion clickable
  - Position and team info remain non-clickable
  - Blue underlined styling for player names with ESPN IDs
  - Clicking opens the ESPN profile in the browser
  - Players without ESPN IDs display as regular text

## How It Works

### Player Selection Dialog
When browsing available players:
- Players with ESPN IDs (top ~50 players) show blue underlined names
- Click the player name to open their ESPN profile
- Click anywhere else on the row to draft the player
- Players without ESPN IDs show normal black text

### Draft History
After drafting players:
- Player names in the draft history are clickable links
- Only the name portion is clickable (not the position/team info)
- Blue underlined styling indicates the link
- Opens ESPN profile when clicked

## ESPN Profile URL Format
```
https://www.espn.com/nfl/player/_/id/{espnId}
```

Example: Christian McCaffrey (ESPN ID: 3117251)
```
https://www.espn.com/nfl/player/_/id/3117251
```

## Data Coverage
Based on `players.json`:
- Top 50 players have ESPN IDs configured
- Remaining players have empty ESPN IDs (no link functionality)
- The `Player` model already has the `espnId` field and `getEspnUrl()` method

## Testing
To test the feature:
1. Build the app: `.\gradlew assembleDebug`
2. Install on device
3. Open the player selection dialog
4. Look for blue underlined player names (top players)
5. Click a player name to verify ESPN profile opens
6. Draft a player and check the draft history
7. Click the player name in draft history to verify the link works

## Technical Details
- Uses Android's `Intent.ACTION_VIEW` to open URLs
- Browser app is automatically selected by the system
- Works with any browser installed on the device
- Link color: `#1976D2` (Material Design blue)
- Text styling: Underlined for clickable names

## Future Enhancements
- Add ESPN IDs for all 300 players (currently only top ~50)
- Add a small icon next to linked names to indicate external link
- Consider adding links to other fantasy resources (PFF, FantasyPros, etc.)
- Add option to open links in-app using WebView

## Date
February 23, 2026
