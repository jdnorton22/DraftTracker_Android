# Screen Size Testing Guide

## Testing Checklist

### Phone Screens (Portrait)
- [ ] Test on 4.7" screen (e.g., iPhone SE size) - 320dp width
- [ ] Test on 5.5" screen (e.g., standard phone) - 360-411dp width
- [ ] Test on 6.5" screen (e.g., large phone) - 411-428dp width

**Expected Behavior:**
- All text should be readable without zooming
- Buttons should be easily tappable (minimum 48dp touch target)
- ScrollViews should work smoothly
- Cards should not be cramped
- Draft history should scroll properly

### Phone Screens (Landscape)
- [ ] Test rotation to landscape on standard phone

**Expected Behavior:**
- Horizontal layout should display properly
- Best available player section should be visible
- No content should be cut off

### Tablet Screens (7" - 10")
- [ ] Test on 7" tablet (600dp width)
- [ ] Test on 10" tablet (720dp+ width)

**Expected Behavior:**
- Horizontal layout should have good spacing
- Text should not be too small
- Cards should use available space effectively
- Best available section should be prominent

## Layout Variants

### Current Implementation
- **Default layout** (`layout/activity_main.xml`): Horizontal layout suitable for landscape and tablets
- **Config activity**: Uses ScrollView for vertical scrolling on all screen sizes
- **Dialogs**: Responsive to screen size

### Responsive Design Features
1. **ScrollView**: Config activity uses ScrollView to handle small screens
2. **Weight-based layouts**: Main activity uses layout_weight for flexible sizing
3. **match_parent/wrap_content**: All layouts use proper sizing attributes
4. **CardView**: Provides consistent spacing and elevation
5. **RecyclerView**: Efficiently handles lists of any size

## Testing in Android Studio

### Using Layout Inspector
1. Open Android Studio
2. Go to Tools > Layout Inspector
3. Select running device/emulator
4. Inspect layouts at different screen sizes

### Using Emulator
1. Create AVDs for different screen sizes:
   - Pixel 4 (5.7", 1080x2280, 440dpi)
   - Pixel 7" Tablet (7", 800x1280, 213dpi)
   - Pixel 10" Tablet (10", 1920x1200, 240dpi)

2. Test rotation on each device
3. Verify all UI elements are accessible

### Manual Testing Steps
1. **Launch app** on each screen size
2. **Navigate to Config Activity**
   - Verify team list scrolls properly
   - Verify all controls are accessible
   - Test with 2 teams and 20 teams
3. **Return to Main Activity**
   - Verify draft configuration is readable
   - Verify current pick section is clear
   - Verify draft history scrolls
   - Verify best available player is visible
4. **Open Player Selection Dialog**
   - Verify search bar is accessible
   - Verify player list scrolls
   - Verify players are selectable
5. **Test rotation**
   - Rotate device to landscape
   - Verify layout adjusts properly
   - Rotate back to portrait

## Known Considerations

### Small Phones (< 360dp width)
- Horizontal layout in portrait may be cramped
- Consider creating a `layout-port` variant with vertical stacking if needed

### Large Tablets (> 900dp width)
- Current layout works well
- Could add more padding for better aesthetics

### Accessibility
- All touch targets meet 48dp minimum
- Text sizes are readable (14sp minimum for body text)
- Color contrast meets WCAG guidelines

## Adjustments Made
1. ✅ Theme colors updated to fantasy football theme (green/gold)
2. ✅ Layouts use responsive design patterns
3. ✅ ScrollViews prevent content from being cut off
4. ✅ CardViews provide consistent spacing
5. ✅ RecyclerViews handle variable content sizes

## Future Enhancements
- Create `layout-port` variant for vertical stacking on small phones
- Add `layout-sw720dp` for extra-large tablets with more padding
- Consider split-screen/multi-window support
- Add landscape-specific optimizations
