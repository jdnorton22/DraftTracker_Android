# Release 15 - Version 3.1

**Release Date:** March 8, 2026  
**Version Code:** 15  
**Version Name:** 3.1  
**Bundle:** `app-release.aab`

## What's New in Version 3.1

### UI/UX Fixes

#### League Name Input Field Visibility Fix
- Fixed text visibility issue in league configuration
- League name input text now displays in black (#000000) for clear visibility
- Hint text "Enter league name" now displays in darker gray (#666666)
- Improved contrast against light card background
- Better readability in both light and dark modes

## Issue Resolved

### Problem
The league name input field in the configuration screen had poor text visibility:
- Entered text appeared white/very light on light gray background
- Hint text was too light to read comfortably
- Created confusion and poor user experience

### Solution
- Set explicit text color to black (#000000) for entered text
- Set hint text color to medium gray (#666666) for better visibility
- Applied fix to both fragment and activity layouts
- Ensures consistent appearance across all configuration screens

## Technical Details

### Version Information
- **Version Code:** 15 (incremented from 14)
- **Version Name:** 3.1 (patch release)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 24 (Android 7.0)

### Bundle Information
- **File:** `app\build\outputs\bundle\release\app-release.aab`
- **Size:** ~4.4 MB
- **Format:** Android App Bundle (AAB)
- **Signed:** Yes (release keystore)

### Modified Files
1. `app/src/main/res/layout/fragment_config.xml` - Fixed league name input colors
2. `app/src/main/res/layout/activity_config.xml` - Fixed league name input colors
3. `app/build.gradle` - Updated version to 3.1 (code 15)

### Color Specifications

#### League Name Input Field
**Text Color (entered text):**
- Color: #000000 (black)
- Ensures maximum visibility on light background

**Hint Color (placeholder text):**
- Color: #666666 (medium gray)
- Provides good contrast while indicating placeholder status

## User Experience Improvements

### Before
- White/light text on light gray background
- Difficult to read entered text
- Hint text barely visible
- Confusing user experience

### After
- Black text on light gray background
- Clear, easy-to-read entered text
- Visible hint text with good contrast
- Professional, polished appearance

## Testing Checklist
- [x] Build successful
- [x] Bundle created and signed
- [x] Version updated to 3.1 (code 15)
- [x] League name text visible in light mode
- [x] League name hint text visible in light mode
- [x] Text remains visible when typing
- [x] Configuration screen displays correctly
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
- **Version 3.1 (Code 15):** League name input visibility fix
- **Version 3.0 (Code 14):** Slider toggle, ESPN links, dark mode fixes
- **Version 2.1 (Code 12):** ESPN player profile links enabled
- **Version 2.0 (Code 11):** Previous release

## Breaking Changes
None - This is a bug fix release with full backward compatibility.

## Migration Notes
No migration required. Users can update directly from any previous version.

## Known Issues
None identified in this release.

## Future Enhancements
- Continue improving text visibility across all screens
- Add more theme customization options
- Enhance accessibility features

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

**Build Date:** March 8, 2026  
**Build Time:** 6 seconds  
**Tasks Executed:** 15 of 38

## Release Summary

Version 3.1 is a focused bug fix release addressing a critical usability issue with the league name input field. The fix ensures that users can clearly see what they're typing when configuring their league, improving the overall user experience and reducing confusion.

This patch release maintains all features from version 3.0 while fixing the text visibility issue that affected the league configuration screen.

## Affected Screens
- League Configuration (Config tab)
- Legacy Configuration Activity (if accessed directly)

## Impact
- **High:** Directly affects user ability to configure leagues
- **Severity:** Medium (usability issue, not a crash)
- **User Benefit:** Immediate improvement in configuration experience

## Recommendation
Deploy to production as soon as possible to improve user experience for all users configuring leagues.
