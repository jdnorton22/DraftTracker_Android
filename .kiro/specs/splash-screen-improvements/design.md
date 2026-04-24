# Splash Screen Improvements Bugfix Design

## Overview

The Fantasy Draft Picker splash screen has three cosmetic/UX defects: it uses a text emoji (🏈) instead of the actual app icon (`FootballIcon1024.png`), displays a hardcoded "Version 2.0" instead of the current version (3.10), and lingers for 2 seconds with a fake progress bar animation that adds no value. The fix replaces the emoji with the real icon, dynamically reads the version from `BuildConfig`, and reduces the splash duration to ~1 second. These are low-risk UI changes confined to `SplashActivity.java` and `activity_splash.xml`.

## Glossary

- **Bug_Condition (C)**: The set of visual/timing defects on the splash screen — wrong icon, wrong version, excessive duration
- **Property (P)**: The desired splash screen behavior — real icon displayed, correct version shown, ~1 second duration
- **Preservation**: Existing splash screen behaviors that must remain unchanged — app name, tagline, background color, progress bar style, navigation to MainActivity, action bar hiding, handler cleanup
- **SplashActivity**: The activity in `app/src/main/java/com/fantasydraft/picker/ui/SplashActivity.java` that displays the splash screen and navigates to MainActivity
- **activity_splash.xml**: The layout file in `app/src/main/res/layout/` defining the splash screen UI
- **BuildConfig.VERSION_NAME**: Auto-generated constant from `versionName` in `app/build.gradle`, currently "3.10"

## Bug Details

### Bug Condition

The splash screen has three defects that manifest every time the app launches:

1. **Wrong icon**: The layout uses a `TextView` with emoji text "🏈" instead of an `ImageView` referencing `@drawable/FootballIcon1024`
2. **Wrong version**: The layout hardcodes "Version 2.0" instead of reading from `BuildConfig.VERSION_NAME` (which is "3.10")
3. **Excessive duration**: `SPLASH_DURATION` is set to 2000ms with a fake progress bar that updates every 30ms, providing no real loading feedback

**Formal Specification:**
```
FUNCTION isBugCondition(splashScreen)
  INPUT: splashScreen of type SplashScreenState
  OUTPUT: boolean

  hasEmojiIcon := splashScreen.iconElement IS TextView
                  AND splashScreen.iconElement.text == "🏈"
  
  hasWrongVersion := splashScreen.versionText != BuildConfig.VERSION_NAME
  
  hasExcessiveDuration := splashScreen.splashDuration > 1200

  RETURN hasEmojiIcon OR hasWrongVersion OR hasExcessiveDuration
END FUNCTION
```

### Examples

- **Emoji icon**: User launches app → sees a text emoji "🏈" at 72sp instead of the actual `FootballIcon1024.png` app icon. Expected: the real PNG icon is displayed as an image.
- **Wrong version**: User launches app → sees "Version 2.0" at the bottom. Expected: "Version 3.10" (or whatever `BuildConfig.VERSION_NAME` returns).
- **Slow splash**: User launches app → waits 2 full seconds watching a fake progress bar cycle through "Loading players...", "Preparing draft...", "Almost ready...", "Let's draft!". Expected: splash completes in ~1 second.
- **Edge case — version update**: When `versionName` changes in `build.gradle` for a future release, the splash screen should automatically reflect the new version without any layout changes.

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- App name ("Fantasy Draft Picker" via `@string/app_name`) must continue to display in bold white 32sp text
- Tagline "Your Draft Day Companion" must continue to display in 16sp #BBDEFB text
- Background color must remain #1976D2 (Material Blue 700)
- Progress bar must continue to animate from 0 to 100% with white tint on #64B5F6 background
- Loading text must continue to cycle through the four status messages
- Navigation to `MainActivity` must occur after the splash duration completes
- Action bar must be hidden during splash
- Handler callbacks must be cleaned up in `onDestroy()`
- Overall layout structure (centered content with version at bottom) must be preserved

**Scope:**
All visual elements and behaviors not related to the icon display, version text content, or splash timing should be completely unaffected by this fix. This includes:
- App name and tagline text and styling
- Background color and padding
- Progress bar visual appearance and animation pattern
- Loading text message sequence
- Activity lifecycle management (handler cleanup, intent navigation)
- Layout structure (RelativeLayout with centered LinearLayout)

## Hypothesized Root Cause

Based on the code analysis, the causes are straightforward:

1. **Emoji placeholder never replaced**: The `activity_splash.xml` layout was created with a `TextView` containing "🏈" as a placeholder for the app icon. The `FootballIcon1024.png` was later added to `res/drawable/` but the layout was never updated to use an `ImageView` referencing it.

2. **Hardcoded version string**: The version text "Version 2.0" was hardcoded in the layout XML when the app was at version 2.0. It was never updated to read dynamically from `BuildConfig.VERSION_NAME`, so it fell out of sync as the app progressed to version 3.10.

3. **Arbitrary splash duration**: The `SPLASH_DURATION` of 2000ms with a fake progress bar was likely copied from a template or tutorial. The progress bar doesn't reflect real loading — it's purely cosmetic. The 2-second wait adds unnecessary friction to app startup.

## Correctness Properties

Property 1: Bug Condition - Splash Screen Displays Real Icon, Correct Version, and Reduced Duration

_For any_ app launch where the splash screen is displayed, the fixed splash screen SHALL show the `FootballIcon1024.png` drawable as an `ImageView` (not a text emoji), display the version string matching `BuildConfig.VERSION_NAME` (currently "3.10"), and complete the splash within approximately 1000ms.

**Validates: Requirements 2.1, 2.2, 2.3**

Property 2: Preservation - Non-Defect Splash Screen Elements Unchanged

_For any_ app launch, the fixed splash screen SHALL preserve the app name display, tagline text, background color (#1976D2), progress bar animation pattern, loading text message sequence, navigation to MainActivity, action bar hiding, and handler cleanup — producing the same visual and behavioral result as the original code for all elements not related to the icon, version text, or duration.

**Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9**

## Fix Implementation

### Changes Required

Assuming our root cause analysis is correct:

**File**: `app/src/main/res/layout/activity_splash.xml`

**Specific Changes**:
1. **Replace emoji TextView with ImageView**: Remove the `TextView` with text "🏈" and replace it with an `ImageView` referencing `@drawable/FootballIcon1024`. Size the image appropriately (e.g., 120dp × 120dp) with `contentDescription` for accessibility.

2. **Add version TextView ID**: Add an `android:id` to the version `TextView` so it can be updated programmatically from Java code.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/SplashActivity.java`

**Specific Changes**:
3. **Reduce splash duration**: Change `SPLASH_DURATION` from `2000` to `1000` (1 second).

4. **Set version text dynamically**: In `onCreate()`, find the version `TextView` by ID and set its text to `"Version " + BuildConfig.VERSION_NAME`. Import `com.fantasydraft.picker.BuildConfig`.

5. **Adjust progress animation interval**: The progress increment calculation (`SPLASH_DURATION / PROGRESS_UPDATE_INTERVAL`) will automatically adapt since it's derived from `SPLASH_DURATION`. No change needed to `PROGRESS_UPDATE_INTERVAL` — the animation will simply complete faster.

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the defects on unfixed code, then verify the fix works correctly and preserves existing behavior.

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the defects BEFORE implementing the fix. Confirm or refute the root cause analysis.

**Test Plan**: Inspect the layout XML and SplashActivity constants to confirm the three defects exist. Run unit tests that check the splash duration constant, and verify the layout structure.

**Test Cases**:
1. **Emoji Icon Test**: Verify the layout contains a `TextView` with "🏈" instead of an `ImageView` with a drawable source (will fail on unfixed code — confirms defect)
2. **Version Text Test**: Verify the layout contains hardcoded "Version 2.0" that doesn't match `BuildConfig.VERSION_NAME` "3.10" (will fail on unfixed code)
3. **Duration Test**: Verify `SPLASH_DURATION` is 2000ms, exceeding the desired ~1000ms (will fail on unfixed code)
4. **Progress Timing Test**: Verify the progress animation runs for 2 seconds with 30ms intervals (will fail on unfixed code)

**Expected Counterexamples**:
- Layout XML contains `<TextView ... android:text="🏈" ...>` instead of `<ImageView ... android:src="@drawable/FootballIcon1024" ...>`
- Layout XML contains `android:text="Version 2.0"` instead of dynamically set version
- `SPLASH_DURATION` constant equals 2000, not ~1000

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds, the fixed splash screen produces the expected behavior.

**Pseudocode:**
```
FOR ALL launch WHERE isBugCondition(splashScreen) DO
  result := displaySplashScreen_fixed(launch)
  ASSERT result.iconElement IS ImageView
  ASSERT result.iconElement.drawable == R.drawable.FootballIcon1024
  ASSERT result.versionText == "Version " + BuildConfig.VERSION_NAME
  ASSERT result.splashDuration <= 1200
END FOR
```

### Preservation Checking

**Goal**: Verify that for all splash screen elements not related to the bug condition, the fixed code produces the same result as the original code.

**Pseudocode:**
```
FOR ALL element WHERE NOT isBugCondition(element) DO
  ASSERT splashScreen_original(element) == splashScreen_fixed(element)
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It can generate random app states and verify the splash screen preserves all non-defect behaviors
- It catches edge cases like rapid activity recreation or configuration changes
- It provides strong guarantees that navigation, lifecycle, and visual elements are unchanged

**Test Plan**: Observe behavior on UNFIXED code first for all non-defect elements, then write tests capturing that behavior.

**Test Cases**:
1. **App Name Preservation**: Verify the app name TextView still displays `@string/app_name` in bold white 32sp after fix
2. **Navigation Preservation**: Verify `MainActivity` intent is still created and activity finishes after splash duration
3. **Handler Cleanup Preservation**: Verify `onDestroy()` still removes all handler callbacks
4. **Progress Animation Preservation**: Verify progress bar still animates from 0 to 100% and loading text cycles through all four messages

### Unit Tests

- Test that `SPLASH_DURATION` equals 1000
- Test that version text is set to `"Version " + BuildConfig.VERSION_NAME` in `onCreate()`
- Test that the layout contains an `ImageView` with `@drawable/FootballIcon1024`
- Test that progress animation completes within the new duration
- Test that `onDestroy()` cleans up handler callbacks

### Property-Based Tests

- Generate random sequences of activity lifecycle events (create, destroy, recreate) and verify the splash always navigates to MainActivity exactly once
- Generate random timing scenarios and verify the progress bar always reaches 100% before navigation
- Verify that for any `BuildConfig.VERSION_NAME` value, the version text is correctly formatted as "Version X.Y"

### Integration Tests

- Test full app launch flow: splash screen appears with real icon, correct version, and transitions to MainActivity within ~1 second
- Test that the splash screen renders correctly on different screen sizes (the 120dp icon scales appropriately)
- Test that rapid back-press during splash doesn't cause crashes (handler cleanup works correctly)
