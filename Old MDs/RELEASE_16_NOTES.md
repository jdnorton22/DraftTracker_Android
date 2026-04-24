# Release 16 - Version 3.2

**Release Date:** March 9, 2026  
**Version Code:** 16  
**Version Name:** 3.2  
**Bundle:** `app-release.aab`

## What's New in Version 3.2

### Major Navigation Redesign

#### Hamburger Menu Navigation
- Replaced bottom navigation bar with hamburger menu in upper left corner
- Modern slide-out drawer navigation from the left side
- More screen space for draft content (no bottom bar taking up space)
- Smooth drawer animation and transitions
- Professional, clean interface

### New Features

#### ESPN Team Depth Chart Links
- Team abbreviations (SF, KC, DAL, etc.) are now clickable
- Tap any team abbreviation to view ESPN depth chart
- Works in both player selection dialog and draft history
- All 32 NFL teams mapped to ESPN depth charts
- Quick access to positional depth and competition info

#### Wider Player Selection Dialog
- Player selection dialog now uses 95% of screen width
- More room to view player information
- Better visibility of stats, rankings, and team info
- Improved readability on all screen sizes

## Feature Details

### Hamburger Menu Navigation

**Location:** Upper left corner of the screen

**How to Use:**
- Tap the hamburger icon (☰) to open the navigation drawer
- Select "Draft" to view the draft board
- Select "Config" to configure league settings
- Tap outside the drawer or press back to close it

**Benefits:**
- Full-screen fragments without bottom bar obstruction
- More modern, professional appearance
- Consistent with Material Design guidelines
- Better use of screen real estate

**Navigation Drawer Header:**
- App icon and branding
- App name: "Fantasy Draft Picker"
- Version number display

### ESPN Depth Chart Links

**How It Works:**
- Team abbreviations appear in blue with underline
- Tap to open: `https://www.espn.com/nfl/team/depth/_/name/{team}`
- Opens in device's default browser
- Requires internet connection

**Where Available:**
- Player selection dialog (next to player names)
- Draft history (in player details)
- Recent picks section

**Strategic Value:**
- Check team depth before drafting
- Identify handcuff opportunities
- Understand positional competition
- Make informed draft decisions

### Player Selection Dialog Enhancement

**Improvements:**
- Dialog width increased from default to 95% of screen
- Better visibility of all player information
- More comfortable reading experience
- Maintains all existing functionality

## Technical Details

### Version Information
- **Version Code:** 16 (incremented from 15)
- **Version Name:** 3.2 (minor version bump)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 24 (Android 7.0)

### Bundle Information
- **File:** `app\build\outputs\bundle\release\app-release.aab`
- **Size:** ~4.4 MB
- **Format:** Android App Bundle (AAB)
- **Signed:** Yes (release keystore)

### Modified Files

#### Navigation Redesign
1. `app/src/main/res/layout/activity_main.xml` - Converted to DrawerLayout
2. `app/src/main/java/com/fantasydraft/picker/ui/MainActivity.java` - Drawer navigation logic
3. `app/src/main/res/menu/menu_drawer_navigation.xml` - Drawer menu items
4. `app/src/main/res/layout/nav_header_main.xml` - Drawer header layout
5. `app/src/main/res/values/strings.xml` - Drawer strings

#### ESPN Depth Charts
6. `app/src/main/java/com/fantasydraft/picker/models/Player.java` - Depth chart URL methods
7. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java` - Clickable teams
8. `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java` - Clickable teams

#### Dialog Enhancement
9. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionDialog.java` - Wider dialog

### New Resources
- `menu_drawer_navigation.xml` - Navigation menu
- `nav_header_main.xml` - Drawer header
- Navigation drawer strings
- Color resources for drawer

## User Experience Improvements

### Navigation
**Before:**
- Bottom navigation bar always visible
- Takes up screen space
- Limited to 2 tabs

**After:**
- Hamburger menu in upper left
- Full-screen content area
- Slide-out drawer with smooth animation
- More professional appearance

### Research Tools
**Before:**
- Team abbreviations were static text
- Manual research required

**After:**
- One-tap access to depth charts
- Quick strategic insights
- Seamless ESPN integration

### Dialog Usability
**Before:**
- Narrower dialog
- Cramped information display

**After:**
- 95% screen width
- Comfortable viewing
- Better information visibility

## Testing Checklist
- [x] Build successful
- [x] Bundle created and signed
- [x] Version updated to 3.2 (code 16)
- [x] Hamburger menu opens/closes correctly
- [x] Navigation between Draft and Config works
- [x] Back button closes drawer when open
- [x] ESPN depth chart links work
- [x] Player selection dialog is wider
- [x] All existing features functional
- [x] App launches without crashes

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
- **Version 3.2 (Code 16):** Hamburger menu, ESPN depth charts, wider dialog
- **Version 3.1 (Code 15):** League name input visibility fix
- **Version 3.0 (Code 14):** Slider toggle, ESPN links, dark mode fixes
- **Version 2.1 (Code 12):** ESPN player profile links enabled

## Breaking Changes
None - This is a feature enhancement release with full backward compatibility.

## Migration Notes
No migration required. Users can update directly from any previous version.

## Known Issues
None identified in this release.

## Future Enhancements
- Add more navigation menu items (History, Settings, Help)
- Add team logos to depth chart links
- Consider in-app WebView for ESPN content
- Add drawer customization options
- Implement navigation drawer gestures

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

**Build Date:** March 9, 2026  
**Build Time:** 13 seconds  
**Tasks Executed:** 19 of 38

## Release Summary

Version 3.2 represents a significant UI/UX improvement release focused on:

1. **Modern Navigation** - Hamburger menu replaces bottom navigation
2. **Enhanced Research** - ESPN depth chart links for strategic drafting
3. **Better Usability** - Wider player selection dialog
4. **Screen Real Estate** - Full-screen content without bottom bar
5. **Professional Polish** - Material Design compliant navigation

This release modernizes the app's navigation while adding powerful research tools that help users make better draft decisions. The hamburger menu provides a cleaner, more professional interface while the ESPN depth chart integration gives users instant access to critical team information.

## Recommendation
Deploy to production immediately. This release provides significant UX improvements and valuable new features without any breaking changes or known issues.
