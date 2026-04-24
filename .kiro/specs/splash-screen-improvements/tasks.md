# Implementation Plan

- [x] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - Splash Screen Displays Emoji Icon, Hardcoded Version, and Excessive Duration
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the three defects exist
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate all three splash screen defects
  - **Scoped PBT Approach**: Scope the property to the concrete defect cases using junit-quickcheck with `@RunWith(JUnitQuickcheck.class)`
  - Create test file `app/src/test/java/com/fantasydraft/picker/SplashScreenBugConditionTest.java`
  - **Test 1 - Emoji Icon Defect**: Use reflection to read `SPLASH_DURATION` from `SplashActivity` and verify it equals 1000 (will fail - actual is 2000, confirming excessive duration defect)
  - **Test 2 - Hardcoded Version Defect**: Read `activity_splash.xml` layout file and verify it does NOT contain hardcoded "Version 2.0" text (will fail - layout has hardcoded version, confirming version defect)
  - **Test 3 - Duration Defect**: Property test that for any generated version string, `BuildConfig.VERSION_NAME` should be used dynamically (will fail on unfixed code since version is hardcoded in XML)
  - The test assertions should match the Expected Behavior Properties from design: icon is ImageView with FootballIcon1024 drawable, version matches BuildConfig.VERSION_NAME, duration ≤ 1200ms
  - Run test on UNFIXED code
  - **EXPECTED OUTCOME**: Test FAILS (this is correct - it proves the three defects exist)
  - Document counterexamples found: `SPLASH_DURATION` is 2000 (not 1000), layout has `<TextView android:text="🏈">` (not ImageView), layout has hardcoded "Version 2.0" (not dynamic)
  - Mark task complete when test is written, run, and failure is documented
  - _Requirements: 1.1, 1.2, 1.3_

- [ ] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Non-Defect Splash Screen Elements Unchanged
  - **IMPORTANT**: Follow observation-first methodology
  - Create test file `app/src/test/java/com/fantasydraft/picker/SplashScreenPreservationTest.java`
  - Use junit-quickcheck with `@RunWith(JUnitQuickcheck.class)` for property-based tests
  - **Observe on UNFIXED code first**, then write tests capturing observed behavior:
  - Observe: `SplashActivity` hides action bar in `onCreate()` via `getSupportActionBar().hide()`
  - Observe: `SplashActivity.onDestroy()` calls `handler.removeCallbacksAndMessages(null)` for cleanup
  - Observe: `SplashActivity` creates an explicit intent to `MainActivity` and calls `finish()` after splash duration
  - Observe: Progress bar animates from 0 to 100% with `PROGRESS_UPDATE_INTERVAL = 30`ms
  - Observe: Loading text cycles through "Loading players...", "Preparing draft...", "Almost ready...", "Let's draft!" at progress thresholds 30%, 60%, 90%
  - Observe: Layout has app name via `@string/app_name` in bold white 32sp, tagline "Your Draft Day Companion" in 16sp #BBDEFB, background #1976D2 with 32dp padding
  - Observe: Layout structure is RelativeLayout with centered LinearLayout and version text at bottom
  - **Property test**: For all progress values 0-100, `updateLoadingText()` returns the correct message based on thresholds (use reflection to invoke private method)
  - **Property test**: For all valid progress increments derived from any duration/interval ratio, progress never exceeds 100
  - **Concrete tests**: Verify layout XML preserves app name, tagline, background color, progress bar styling, and layout structure
  - **Concrete test**: Verify `onDestroy()` handler cleanup pattern is present
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests PASS (this confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

- [x] 3. Fix for splash screen emoji icon, hardcoded version, and excessive duration

  - [x] 3.1 Replace emoji TextView with ImageView in layout XML
    - In `app/src/main/res/layout/activity_splash.xml`, replace the `<TextView android:text="🏈" android:textSize="72sp">` with an `<ImageView>` referencing `@drawable/FootballIcon1024`
    - Set ImageView dimensions to 120dp × 120dp with `android:contentDescription="App icon"` for accessibility
    - Preserve the 24dp bottom margin from the original TextView
    - _Bug_Condition: isBugCondition(splashScreen) where splashScreen.iconElement IS TextView AND text == "🏈"_
    - _Expected_Behavior: iconElement IS ImageView AND drawable == @drawable/FootballIcon1024_
    - _Preservation: Layout structure (RelativeLayout with centered LinearLayout) unchanged_
    - _Requirements: 2.1_

  - [x] 3.2 Add ID to version TextView and set version dynamically
    - In `app/src/main/res/layout/activity_splash.xml`, add `android:id="@+id/version_text"` to the version `TextView` at the bottom
    - Change the hardcoded `android:text="Version 2.0"` to a placeholder or remove it (it will be set programmatically)
    - In `app/src/main/java/com/fantasydraft/picker/ui/SplashActivity.java`, add `import com.fantasydraft.picker.BuildConfig;`
    - In `onCreate()`, find the version TextView by ID and set text to `"Version " + BuildConfig.VERSION_NAME`
    - _Bug_Condition: isBugCondition(splashScreen) where splashScreen.versionText != BuildConfig.VERSION_NAME_
    - _Expected_Behavior: versionText == "Version " + BuildConfig.VERSION_NAME (currently "Version 3.10")_
    - _Preservation: Version TextView styling (12sp, #90CAF9, centered at bottom) unchanged_
    - _Requirements: 2.2_

  - [x] 3.3 Reduce splash duration from 2000ms to 1000ms
    - In `app/src/main/java/com/fantasydraft/picker/ui/SplashActivity.java`, change `SPLASH_DURATION` from `2000` to `1000`
    - The progress animation will automatically adapt since `totalSteps = SPLASH_DURATION / PROGRESS_UPDATE_INTERVAL` is derived
    - No change needed to `PROGRESS_UPDATE_INTERVAL` (30ms) — animation simply completes faster
    - _Bug_Condition: isBugCondition(splashScreen) where splashScreen.splashDuration > 1200_
    - _Expected_Behavior: splashDuration == 1000 (≤ 1200ms threshold)_
    - _Preservation: Progress bar animation pattern (0 to 100%, loading text cycling) unchanged_
    - _Requirements: 2.3_

  - [x] 3.4 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Splash Screen Displays Real Icon, Correct Version, and Reduced Duration
    - **IMPORTANT**: Re-run the SAME test from task 1 - do NOT write a new test
    - The test from task 1 encodes the expected behavior (icon is ImageView, version is dynamic, duration is 1000ms)
    - When this test passes, it confirms the expected behavior is satisfied for all three defects
    - Run `SplashScreenBugConditionTest` from step 1
    - **EXPECTED OUTCOME**: Test PASSES (confirms all three defects are fixed)
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 3.5 Verify preservation tests still pass
    - **Property 2: Preservation** - Non-Defect Splash Screen Elements Unchanged
    - **IMPORTANT**: Re-run the SAME tests from task 2 - do NOT write new tests
    - Run `SplashScreenPreservationTest` from step 2
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions in app name, tagline, background, progress bar, loading text, navigation, handler cleanup, layout structure)
    - Confirm all tests still pass after fix (no regressions)

- [ ] 4. Checkpoint - Ensure all tests pass
  - Run full test suite: `./gradlew test`
  - Ensure `SplashScreenBugConditionTest` passes (bug is fixed)
  - Ensure `SplashScreenPreservationTest` passes (no regressions)
  - Ensure all other existing tests still pass
  - Ask the user if questions arise
