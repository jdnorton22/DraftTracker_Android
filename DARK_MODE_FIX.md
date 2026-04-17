# Dark Mode Fix - Import Custom Player Data Section

## Issue
The "Import Custom Player Data" section in the Config screen was not visible in dark mode due to missing text color attributes and a hardcoded light background color.

## Root Cause
1. TextViews in the import section had no explicit `textColor` attributes
2. The example code block used a hardcoded light gray background (`#F5F5F5`)
3. In dark mode, black text on a dark background made content invisible

## Solution

### 1. Added Color Resources
**Files Modified:**
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values-night/colors.xml`

Added `code_block_background` color resource:
- Light mode: `#F5F5F5` (light gray)
- Dark mode: `#2B2B2B` (dark gray)

### 2. Updated Layout
**File Modified:** `app/src/main/res/layout/fragment_config.xml`

Added explicit text colors to all TextViews in the Import section:
- Title: `@color/text_primary` (white in dark mode, black in light mode)
- Description: `@color/text_secondary` (light gray in dark mode, dark gray in light mode)
- Requirements header: `@color/text_primary`
- Requirements list: `@color/text_secondary`
- Example code block: `@color/text_secondary` with `@color/code_block_background`

## Changes Summary

### Color Resources Added
```xml
<!-- Light mode (values/colors.xml) -->
<color name="code_block_background">#F5F5F5</color>

<!-- Dark mode (values-night/colors.xml) -->
<color name="code_block_background">#2B2B2B</color>
```

### Layout Updates
All TextViews in the Import Custom Player Data section now have:
- Explicit `android:textColor` attributes using theme-aware color resources
- Code block background uses `@color/code_block_background` instead of hardcoded `#F5F5F5`

## Visual Result

### Light Mode
- Black text on white card background
- Light gray code block background
- Clear contrast and readability

### Dark Mode
- White text on dark card background
- Dark gray code block background
- Proper contrast and visibility

## Testing
- [x] Build successful
- [x] Deployed to device
- [x] Light mode: Text visible and readable
- [x] Dark mode: Text visible and readable
- [x] Code block background adapts to theme

## Files Modified
1. `app/src/main/res/values/colors.xml` - Added code_block_background
2. `app/src/main/res/values-night/colors.xml` - Added code_block_background for dark mode
3. `app/src/main/res/layout/fragment_config.xml` - Added textColor attributes to all TextViews

## Best Practices Applied
- Use theme-aware color resources instead of hardcoded colors
- Always specify explicit text colors for custom layouts
- Create separate color values for light and dark modes
- Test UI in both light and dark themes

## Date
February 23, 2026
