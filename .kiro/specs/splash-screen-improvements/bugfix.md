# Bugfix Requirements Document

## Introduction

The Fantasy Draft Picker splash screen has three cosmetic and UX defects that affect every app launch. The icon area displays a text emoji ("🏈" in a `TextView`) instead of the actual `FootballIcon1024.png` drawable. The version label is hardcoded to "Version 2.0" even though the app is at version 3.10 (`BuildConfig.VERSION_NAME`). The splash screen lingers for 2 seconds with a fake progress bar, adding unnecessary startup friction. These defects are confined to `SplashActivity.java` and `activity_splash.xml`.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN the app launches and the splash screen is displayed THEN the system shows a `TextView` with the emoji text "🏈" at 72sp instead of the actual `FootballIcon1024.png` drawable as an image

1.2 WHEN the app launches and the splash screen is displayed THEN the system shows the hardcoded text "Version 2.0" instead of the current version from `BuildConfig.VERSION_NAME` (which is "3.10")

1.3 WHEN the app launches and the splash screen is displayed THEN the system delays for 2000ms (`SPLASH_DURATION = 2000`) before navigating to `MainActivity`, resulting in an unnecessarily long wait

### Expected Behavior (Correct)

2.1 WHEN the app launches and the splash screen is displayed THEN the system SHALL show the `FootballIcon1024.png` drawable in an `ImageView` (approximately 120dp × 120dp) with an appropriate content description for accessibility

2.2 WHEN the app launches and the splash screen is displayed THEN the system SHALL show the version text dynamically as "Version " followed by `BuildConfig.VERSION_NAME`, so it always reflects the current version defined in `build.gradle`

2.3 WHEN the app launches and the splash screen is displayed THEN the system SHALL complete the splash within approximately 1000ms (`SPLASH_DURATION = 1000`) before navigating to `MainActivity`

### Unchanged Behavior (Regression Prevention)

3.1 WHEN the app launches THEN the system SHALL CONTINUE TO display the app name ("Fantasy Draft Picker" via `@string/app_name`) in bold white 32sp text

3.2 WHEN the app launches THEN the system SHALL CONTINUE TO display the tagline "Your Draft Day Companion" in 16sp #BBDEFB text

3.3 WHEN the app launches THEN the system SHALL CONTINUE TO use the #1976D2 (Material Blue 700) background color with 32dp padding

3.4 WHEN the app launches THEN the system SHALL CONTINUE TO animate the progress bar from 0 to 100% with white tint (#FFFFFF) on a #64B5F6 background

3.5 WHEN the app launches THEN the system SHALL CONTINUE TO cycle the loading text through "Loading players...", "Preparing draft...", "Almost ready...", and "Let's draft!" based on progress percentage

3.6 WHEN the splash duration completes THEN the system SHALL CONTINUE TO navigate to `MainActivity` via an explicit intent and finish `SplashActivity`

3.7 WHEN the splash screen is displayed THEN the system SHALL CONTINUE TO hide the action bar if present

3.8 WHEN `SplashActivity` is destroyed THEN the system SHALL CONTINUE TO remove all handler callbacks and messages to prevent memory leaks

3.9 WHEN the app launches THEN the system SHALL CONTINUE TO use the existing layout structure (RelativeLayout with centered LinearLayout content and version text anchored at the bottom)
