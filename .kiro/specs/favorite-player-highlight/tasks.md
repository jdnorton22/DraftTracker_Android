# Implementation Plan

- [x] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - Favorite Field Missing from Player Model and Parser
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate the bug exists
  - **Scoped PBT Approach**: Use junit-quickcheck `@Property` with a custom generator that produces JSON player objects with `"favorite": true` and varying other fields
  - Create test file `app/src/test/java/com/fantasydraft/picker/FavoritePlayerBugConditionTest.java`
  - Test that `Player` class has an `isFavorite()` method that returns `true` when constructed from JSON with `"favorite": true`
  - Test that `PlayerDataParser.parsePlayerObject()` reads the `"favorite"` field and sets it on the `Player` object
  - For concrete failing cases: parse `{"id":"1","name":"Test","position":"QB","rank":1,"favorite":true}` and assert `player.isFavorite() == true`
  - Property: for all generated JSON entries with `"favorite": true`, the parsed Player's `isFavorite()` returns `true`; for `"favorite": false` or absent, returns `false`
  - Run test on UNFIXED code
  - **EXPECTED OUTCOME**: Test FAILS (compilation error - `isFavorite()` does not exist on Player, and `parsePlayerObject` ignores the field)
  - Document counterexamples: `Player` has no `favorite` field, no `isFavorite()` method, `PlayerDataParser` does not call `optBoolean("favorite", false)`
  - Mark task complete when test is written, run, and failure is documented
  - _Requirements: 1.1, 1.2, 2.1, 2.2_

- [x] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Existing Field Parsing and Non-Favorite Rendering Unchanged
  - **IMPORTANT**: Follow observation-first methodology
  - Create test file `app/src/test/java/com/fantasydraft/picker/FavoritePlayerPreservationTest.java`
  - Observe on UNFIXED code: `PlayerDataParser.parseESPNData()` correctly parses all existing fields (id, name, position, rank, pffRank, positionRank, nflTeam, lastYearStats, injuryStatus, espnId, byeWeek) from JSON entries that do NOT have a `"favorite"` field
  - Observe on UNFIXED code: `Player` equals/hashCode work correctly for all existing fields
  - Write property-based test using junit-quickcheck: for all generated JSON player entries WITHOUT a `"favorite"` field, all existing fields parse to the same values as before (id, name, position, rank, pffRank, positionRank, nflTeam, lastYearStats, injuryStatus, espnId, byeWeek)
  - Write property-based test: for all `Player` objects created via constructor, `equals()` and `hashCode()` behave consistently for existing fields
  - Write concrete preservation tests: parse a JSON array of 10+ players without `"favorite"` field and verify all fields parse correctly
  - Verify tests PASS on UNFIXED code
  - **EXPECTED OUTCOME**: Tests PASS (confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3, 3.5, 3.6_

- [x] 3. Implement favorite player highlight feature

  - [x] 3.1 Add `favorite` field to `Player` model
    - Add `private boolean favorite;` field to `app/src/main/java/com/fantasydraft/picker/models/Player.java`
    - Add `isFavorite()` getter and `setFavorite(boolean)` setter
    - Initialize `favorite = false` in the no-arg constructor, the 4-arg constructor `Player(id, name, position, rank)`, and the 6-arg constructor `Player(id, name, position, rank, isDrafted, draftedBy)`
    - Add `favorite` to `equals()`: include `favorite == player.favorite` in the comparison chain
    - Add `favorite` to `hashCode()`: include `favorite` in the `Objects.hash(...)` call
    - Update `writeToParcel()`: add `dest.writeByte((byte) (favorite ? 1 : 0));` after the `byeWeek` write
    - Update `Player(Parcel in)` constructor: add `favorite = in.readByte() != 0;` after the `byeWeek` read
    - _Bug_Condition: isBugCondition(input) where input.player has "favorite": true in JSON but Player has no favorite field_
    - _Expected_Behavior: Player.isFavorite() returns the parsed value; defaults to false when absent_
    - _Preservation: All existing fields, constructors, equals, hashCode, and Parcelable behavior unchanged_
    - _Requirements: 2.1, 2.2, 3.2, 3.6_

  - [x] 3.2 Parse `favorite` field in `PlayerDataParser`
    - In `app/src/main/java/com/fantasydraft/picker/utils/PlayerDataParser.java`, method `parsePlayerObject()`
    - Add `player.setFavorite(playerJson.optBoolean("favorite", false));` after the `player.setDraftedBy(null);` line
    - This ensures `"favorite": true` in JSON sets the field, and missing/false defaults to `false`
    - _Bug_Condition: parsePlayerObject ignores "favorite" field in JSON_
    - _Expected_Behavior: "favorite": true → isFavorite() == true; absent/false → isFavorite() == false_
    - _Preservation: All other field parsing (id, name, position, rank, pffRank, etc.) unchanged_
    - _Requirements: 2.2, 3.2_

  - [x] 3.3 Add `favorite_highlight` color resource
    - In `app/src/main/res/values/colors.xml`, add `<color name="favorite_highlight">#FFFFF9C4</color>` (light yellow)
    - This color will be used by all adapters for consistent favorite highlighting
    - _Requirements: 2.4, 2.5, 2.6, 2.7_

  - [x] 3.4 Apply yellow highlight in `PlayerSelectionAdapter`
    - In `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java`, method `PlayerViewHolder.bind()`
    - Replace the existing `rootView.setBackgroundColor(0x00000000);` line
    - Add: if `player.isFavorite()` set `rootView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.favorite_highlight))`, else set `rootView.setBackgroundColor(0x00000000)` (transparent/default)
    - _Bug_Condition: Favorite player in Player Selection Dialog has default background_
    - _Expected_Behavior: Favorite player row has yellow (#FFFFF9C4) background_
    - _Preservation: Non-favorite players continue to have transparent/default background_
    - _Requirements: 2.5, 3.1_

  - [x] 3.5 Apply yellow highlight in `DraftHistoryAdapter`
    - In `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java`, method `onBindViewHolder()`
    - After the player lookup (`Player player = playerMap != null ? ...`), add highlight logic
    - If `player != null && player.isFavorite()`, set `holder.rootView.setBackgroundColor(ContextCompat.getColor(..., R.color.favorite_highlight))`, else reset to default (transparent)
    - _Bug_Condition: Favorite player in Draft History has default background_
    - _Expected_Behavior: Favorite player pick entry has yellow background_
    - _Preservation: Non-favorite player entries keep default background_
    - _Requirements: 2.6, 3.1_

  - [x] 3.6 Apply yellow highlight in `TeamRosterAdapter`
    - In `app/src/main/java/com/fantasydraft/picker/ui/TeamRosterAdapter.java`, method `ViewHolder.bind()`
    - After the null check for player, add: if `player.isFavorite()` set `itemView.setBackgroundColor(ContextCompat.getColor(..., R.color.favorite_highlight))`, else set default
    - Also reset background in `bindUnknownPlayer()` to ensure no stale highlight
    - _Bug_Condition: Favorite player in Team Roster Popup has default background_
    - _Expected_Behavior: Favorite player roster entry has yellow background_
    - _Preservation: Non-favorite and unknown player entries keep default background_
    - _Requirements: 2.7, 3.1_

  - [x] 3.7 Apply yellow highlight in `DraftFragment` best available and pick slots
    - In `app/src/main/java/com/fantasydraft/picker/ui/DraftFragment.java`
    - In `updateBestAvailable()`: after populating the best available card, check `bestPlayer.isFavorite()` and set card background to `R.color.favorite_highlight` if true, else default
    - In `updatePickSlot()`: after populating the pick slot, check `player.isFavorite()` and set slot background to `R.color.favorite_highlight` if true, else default
    - _Bug_Condition: Favorite player on draft screen has default card background_
    - _Expected_Behavior: Favorite player cards/slots have yellow background_
    - _Preservation: Non-favorite player cards/slots keep default background_
    - _Requirements: 2.4, 3.1_

  - [x] 3.8 Add Favorites menu item to navigation drawer
    - In `app/src/main/res/menu/menu_drawer_navigation.xml`, add a new `<item>` between Draft and Config:
      ```xml
      <item
          android:id="@+id/navigation_favorites"
          android:title="Favorites" />
      ```
    - _Preservation: Existing Draft and Config menu items unchanged_
    - _Requirements: 2.3, 3.3_

  - [x] 3.9 Create `FavoritesFragment` and layout
    - Create `app/src/main/res/layout/fragment_favorites.xml` with a RecyclerView and an empty-state TextView ("No favorite players")
    - Create `app/src/main/java/com/fantasydraft/picker/ui/FavoritesFragment.java`
    - Fragment queries `PlayerManager` for all players where `isFavorite() == true`
    - Display each favorite player with name, position, rank, and a toggle button to remove from favorites
    - Toggling calls `player.setFavorite(false)` and refreshes the list
    - _Requirements: 2.3_

  - [x] 3.10 Handle Favorites navigation in `MainActivity`
    - In `app/src/main/java/com/fantasydraft/picker/ui/MainActivity.java`, in `setupNavigationDrawer()` navigation item selected listener
    - Add case for `R.id.navigation_favorites` that replaces the current fragment with `FavoritesFragment`
    - _Preservation: Existing navigation to Draft and Config screens unchanged_
    - _Requirements: 2.3, 3.3_

  - [x] 3.11 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Favorite Field Parsing and Storage
    - **IMPORTANT**: Re-run the SAME test from task 1 - do NOT write a new test
    - The test from task 1 encodes the expected behavior: `isFavorite()` returns correct value based on JSON `"favorite"` field
    - Run `FavoritePlayerBugConditionTest` from step 1
    - **EXPECTED OUTCOME**: Test PASSES (confirms `Player.isFavorite()` exists, `PlayerDataParser` reads the field, and values are correct)
    - _Requirements: 2.1, 2.2_

  - [x] 3.12 Verify preservation tests still pass
    - **Property 2: Preservation** - Existing Field Parsing and Non-Favorite Rendering Unchanged
    - **IMPORTANT**: Re-run the SAME tests from task 2 - do NOT write new tests
    - Run `FavoritePlayerPreservationTest` from step 2
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions in field parsing, equals, hashCode, or existing behavior)
    - Confirm all preservation tests still pass after the fix

- [x] 4. Checkpoint - Ensure all tests pass
  - Run full test suite: `./gradlew test`
  - Ensure `FavoritePlayerBugConditionTest` passes (bug is fixed)
  - Ensure `FavoritePlayerPreservationTest` passes (no regressions)
  - Ensure all existing tests in `managers/` and `models/` still pass
  - Ask the user if questions arise
