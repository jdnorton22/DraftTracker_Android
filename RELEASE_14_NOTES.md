# Release 14 - Version 3.0

**Release Date:** March 4, 2026  
**Version Code:** 14  
**Version Name:** 3.0  
**Bundle:** `app-release.aab`

## What's New in Version 3.0

### Major UI Improvements

#### 1. Prominent Slider Toggle for Draft Summary
- Replaced small icon toggle with full-width slider control
- Clear "Team" and "League" labels make the feature obvious
- Blue highlighting shows which mode is active
- Encourages users to explore both viewing modes
- 44dp height meets accessibility standards

#### 2. ESPN Player Profile Links
- Player names are now clickable links to ESPN profiles
- Blue underlined styling indicates clickable names
- Works in both player selection dialog and draft history
- Opens ESPN profiles in your browser with one tap
- Available for top ~50 players with ESPN IDs

#### 3. Dark Mode Fix for Import Section
- Fixed visibility issue in "Import Custom Player Data" section
- All text now properly visible in dark mode
- Code example block adapts to theme
- Improved contrast and readability

## Feature Details

### Slider Toggle Enhancement
**Location:** Draft Summary section on main draft screen

**Benefits:**
- Much more visible and discoverable
- Invites interaction with clear labeling
- Modern, polished appearance
- Works beautifully in light and dark modes

**How to Use:**
- Tap "Team" to see your roster position counts
- Tap "League" to see league-wide position distribution
- Use League mode to identify position scarcity
- Use Team mode to track your roster needs

### ESPN Integration
**How It Works:**
- Player names with ESPN IDs appear in blue with underline
- Tap any player name to view their ESPN profile
- View detailed stats, news, and analysis
- Works in player selection and draft history

**Coverage:**
- Top 50 players have ESPN profile links
- URL format: `https://www.espn.com/nfl/player/_/id/{espnId}`

### Dark Mode Support
**Improvements:**
- Import Custom Player Data section fully visible
- Proper text colors throughout the app
- Code blocks adapt to theme
- Consistent experience across light and dark modes

## Technical Details

### Version Information
- **Version Code:** 14 (incremented from 12)
- **Version Name:** 3.0 (major version bump)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 24 (Android 7.0)

### Bundle Information
- **File:** `app\build\outputs\bundle\release\app-release.aab`
- **Size:** ~4.4 MB
- **Format:** Android App Bundle (AAB)
- **Signed:** Yes (release keystore)

### Modified Files

#### UI Enhancements
1. `app/src/main/res/layout/fragment_draft.xml` - New slider toggle
2. `app/src/main/java/com/fantasydraft/picker/ui/DraftFragment.java` - Toggle logic
3. `app/src/main/res/drawable/toggle_slider_background.xml` - Slider background
4. `app/src/main/res/drawable/toggle_slider_selected.xml` - Selected state

#### ESPN Links
5. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java` - Clickable names
6. `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java` - Clickable names

#### Dark Mode
7. `app/src/main/res/layout/fragment_config.xml` - Text colors
8. `app/src/main/res/values/colors.xml` - Color resources
9. `app/src/main/res/values-night/colors.xml` - Dark mode colors

### New Color Resources
- `toggle_background` - Slider container background (theme-aware)
- `code_block_background` - Code example background (theme-aware)

## User Experience Improvements

### Discoverability
- Slider toggle is impossible to miss
- Clear labels eliminate confusion
- Visual feedback encourages exploration

### Engagement
- Users more likely to try both Team/League modes
- ESPN links provide quick access to player information
- Smooth, polished interactions throughout

### Accessibility
- 44dp touch targets meet guidelines
- High contrast in both themes
- Clear text labels for screen readers
- Proper color contrast ratios

## Testing Checklist
- [x] Build successful
- [x] Bundle created and signed
- [x] Version updated to 3.0 (code 14)
- [x] Slider toggle functional
- [x] Toggle updates position counts correctly
- [x] ESPN links work in player selection
- [x] ESPN links work in draft history
- [x] Dark mode displays correctly
- [x] Import section visible in dark mode
- [x] Light mode displays correctly
- [x] App launches successfully

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

## Version History
- **Version 3.0 (Code 14):** Slider toggle, ESPN links, dark mode fixes
- **Version 2.1 (Code 12):** ESPN player profile links enabled
- **Version 2.0 (Code 11):** Previous release

## Known Limitations
- ESPN IDs only available for top ~50 players
- ESPN links require internet connection
- Links open in external browser (not in-app)

## Future Enhancements
- Add ESPN IDs for all 300 players
- Add animation to slider toggle transitions
- Add haptic feedback on toggle
- Consider in-app WebView for ESPN profiles
- Add links to other fantasy resources (PFF, FantasyPros)
- Track analytics on feature usage

## Breaking Changes
None - This is a feature enhancement release with full backward compatibility.

## Migration Notes
No migration required. Users can update directly from any previous version.

## Support
For issues or questions, refer to the app documentation or contact support.

---

## Build Information

**Build Command:**
```bash
.\gradlew bundleRelease --console=plain
```

**Output Location:**
```
app\build\outputs\bundle\release\app-release.aab
```

**Build Date:** March 4, 2026  
**Build Time:** 11 seconds  
**Tasks Executed:** 19 of 38

## Release Summary

Version 3.0 represents a significant UI/UX improvement release focused on:
1. **Visibility** - Making features more discoverable
2. **Engagement** - Encouraging users to explore functionality
3. **Polish** - Modern, professional appearance
4. **Accessibility** - Meeting platform guidelines
5. **Integration** - Connecting to external resources (ESPN)

This release enhances the core drafting experience without changing fundamental functionality, making it a safe and valuable update for all users.
