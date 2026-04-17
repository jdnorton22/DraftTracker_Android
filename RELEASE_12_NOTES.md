# Release 12 - Version 2.1

**Release Date:** February 23, 2026  
**Version Code:** 12  
**Version Name:** 2.1  
**Bundle:** `app-release.aab`

## What's New

### ESPN Player Profile Links
- Re-enabled clickable ESPN player profile links throughout the app
- Player names now appear as blue underlined links when ESPN ID is available
- Click any player name to open their ESPN profile page in your browser
- Works in both player selection dialog and draft history

### Features
- **Player Selection Dialog:** Top ~50 players have clickable ESPN profile links
- **Draft History:** Drafted player names are clickable links to their ESPN profiles
- **Smart Linking:** Only players with ESPN IDs show as links; others display as regular text
- **Visual Indicators:** Blue color and underline styling clearly indicate clickable links

## Technical Details

### Version Information
- **Version Code:** 12 (incremented from 11)
- **Version Name:** 2.1 (updated from 2.0)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 24 (Android 7.0)

### Bundle Information
- **File:** `app\build\outputs\bundle\release\app-release.aab`
- **Size:** ~4.4 MB
- **Format:** Android App Bundle (AAB)
- **Signed:** Yes (release keystore)

### Modified Files
1. `app/build.gradle` - Updated version code and name
2. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java` - Re-enabled ESPN links
3. `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java` - Re-enabled ESPN links

## ESPN Profile URL Format
```
https://www.espn.com/nfl/player/_/id/{espnId}
```

## Data Coverage
- Top 50 players have ESPN IDs configured
- Remaining players display without links
- All player data includes rankings, stats, injury status, and bye weeks

## Testing Checklist
- [x] Build successful
- [x] Bundle created and signed
- [x] Version updated to 2.1 (code 12)
- [x] ESPN links functional in player selection
- [x] ESPN links functional in draft history
- [x] App launches successfully on device

## Deployment

### Google Play Console
Upload the bundle to Google Play Console:
```
app\build\outputs\bundle\release\app-release.aab
```

### Release Track Options
- **Internal Testing:** Quick deployment for testing
- **Closed Testing:** Limited audience testing
- **Open Testing:** Public beta testing
- **Production:** Full public release

## Previous Version
- **Version 2.0 (Code 11):** Previous release
- **Changes:** Added ESPN player profile linking feature

## Known Limitations
- ESPN IDs only available for top ~50 players
- Requires internet connection to view ESPN profiles
- Opens in external browser (not in-app)

## Future Enhancements
- Add ESPN IDs for all 300 players
- Add visual icon indicator for external links
- Consider in-app WebView for ESPN profiles
- Add links to other fantasy resources (PFF, FantasyPros)

## Support
For issues or questions, refer to the app documentation or contact support.

---

**Build Command:**
```bash
.\gradlew bundleRelease --console=plain
```

**Output Location:**
```
app\build\outputs\bundle\release\app-release.aab
```
