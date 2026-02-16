# ActionBar Alignment Fix

## Issue
The ActionBar was overlaying content instead of pushing it down on MainActivity, DraftHistoryActivity, and ConfigActivity. This caused the top portion of content to be hidden behind the ActionBar.

## Root Cause
The app theme (`Theme.MaterialComponents.DayNight.DarkActionBar`) was causing the ActionBar to render in overlay mode on the Surface Duo device (Android 12), despite theme attributes attempting to disable this behavior.

## Solution
Implemented a programmatic fix in the `onCreate()` method of all affected activities:

1. **Get ActionBar height** from the theme's `actionBarSize` attribute
2. **Apply top padding** to the root content view (`android.R.id.content`) equal to the ActionBar height
3. This pushes all content down below the ActionBar

## Files Modified

### MainActivity.java
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Fix ActionBar overlay by adding top padding programmatically
    View rootView = findViewById(android.R.id.content);
    if (rootView != null && getSupportActionBar() != null) {
        int actionBarHeight = getSupportActionBar().getHeight();
        if (actionBarHeight == 0) {
            // ActionBar height not yet measured, use standard height
            android.util.TypedValue tv = new android.util.TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = android.util.TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
            }
        }
        rootView.setPadding(0, actionBarHeight, 0, 0);
    }
    
    // Initialize UI components
    initializeViews();
    // ... rest of onCreate
}
```

### DraftHistoryActivity.java
Applied the same programmatic padding fix in `onCreate()`.

### ConfigActivity.java
Applied the same programmatic padding fix in `onCreate()`.
Added `import android.view.View;` to support the fix.

### Layout Files
Added `android:fitsSystemWindows="true"` to root views in:
- `activity_main.xml`
- `activity_draft_history.xml`
- `activity_config.xml`

### Theme File (themes.xml)
Added attributes to prevent overlay mode:
```xml
<item name="windowActionBarOverlay">false</item>
<item name="android:windowActionBarOverlay">false</item>
```

## Result
✅ ActionBar now displays above content on all three screens
✅ Content is properly positioned below the ActionBar
✅ No content is hidden or cut off by the ActionBar

## Testing
Tested on Surface Duo (Android 12) - all screens display correctly with ActionBar properly positioned.
