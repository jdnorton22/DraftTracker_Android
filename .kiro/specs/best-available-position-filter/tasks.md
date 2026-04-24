# Implementation Plan: Best Available Position Filter

## Overview

Add a position filter Spinner to the Best Available card in DraftFragment, a filtered lookup method to PlayerManager, and wire the filter into the draft-best-available flow. Changes touch three files: `fragment_draft.xml`, `PlayerManager.java`, and `DraftFragment.java`, plus a new test file.

## Tasks

- [x] 1. Add `getBestAvailableByPosition` method to PlayerManager
  - [x] 1.1 Implement `getBestAvailableByPosition(List<Player> players, String position)` in `PlayerManager.java`
    - Add a new public method that iterates over the player list, skipping drafted players and players whose position does not match the given `position` string
    - Return the undrafted player with the lowest rank value matching the position, or `null` if none found
    - Handle null/empty list by returning `null` (same pattern as `getBestAvailable`)
    - Use `position.equals(player.getPosition())` so null player positions are safely skipped
    - File: `app/src/main/java/com/fantasydraft/picker/managers/PlayerManager.java`
    - _Requirements: 2.2, 2.4, 3.1_

  - [ ]* 1.2 Write property test: Filtered best available returns the correct player
    - **Property 1: Filtered best available returns the correct player**
    - **Validates: Requirements 2.1, 2.2, 2.4**
    - Create test file: `app/src/test/java/com/fantasydraft/picker/managers/PlayerManagerFilterTest.java`
    - Generate random player lists with random positions, ranks, and drafted states
    - For each position filter value (including "Overall"), assert the returned player is the lowest-rank undrafted match
    - When filter is "Overall", assert result equals `getBestAvailable`
    - Use `@Property(trials = 100)` annotation

  - [ ]* 1.3 Write unit tests for `getBestAvailableByPosition` edge cases
    - Test with null player list → returns null
    - Test with empty player list → returns null
    - Test with all players drafted at selected position → returns null
    - Test with multiple undrafted players at same position → returns lowest rank
    - Test with no players matching position → returns null
    - Add to `PlayerManagerFilterTest.java`
    - _Requirements: 2.2, 2.4, 3.1_

- [x] 2. Checkpoint - Verify PlayerManager changes
  - Ensure all tests pass, ask the user if questions arise.

- [x] 3. Add Position Filter Spinner to the layout
  - [x] 3.1 Add `Spinner` widget to `fragment_draft.xml` inside the Best Available card
    - Insert a `Spinner` with `android:id="@+id/spinner_position_filter"` between the "Best Available" title `TextView` and the Player Info `LinearLayout`
    - Set `android:layout_width="match_parent"`, `android:layout_height="wrap_content"`, `android:layout_marginBottom="8dp"`
    - Set `android:contentDescription="Filter best available by position"` for accessibility
    - File: `app/src/main/res/layout/fragment_draft.xml`
    - _Requirements: 1.1, 1.4_

- [x] 4. Wire Spinner and update DraftFragment logic
  - [x] 4.1 Add Spinner field, position filter constant, and state field to `DraftFragment`
    - Add `import android.widget.Spinner;` and `import android.widget.ArrayAdapter;`
    - Add field: `private Spinner spinnerPositionFilter;`
    - Add field: `private String selectedPositionFilter = "Overall";`
    - Add constant: `private static final String[] POSITION_FILTER_OPTIONS = {"Overall", "QB", "RB", "WR", "TE", "K", "DST"};`
    - File: `app/src/main/java/com/fantasydraft/picker/ui/DraftFragment.java`
    - _Requirements: 1.2, 1.3_

  - [x] 4.2 Initialize Spinner in `initializeViews` with adapter and listener
    - In `initializeViews(View view)`, find the Spinner by ID `R.id.spinner_position_filter`
    - Create an `ArrayAdapter<String>` using `POSITION_FILTER_OPTIONS` with `android.R.layout.simple_spinner_item` and `simple_spinner_dropdown_item`
    - Set the adapter on the Spinner
    - Attach an `OnItemSelectedListener` that updates `selectedPositionFilter` and calls `updateBestAvailable()` on selection change
    - _Requirements: 1.1, 1.2, 1.3, 2.3_

  - [x] 4.3 Update `updateBestAvailable()` to use position filter
    - If `selectedPositionFilter` equals "Overall", call `playerManager.getBestAvailable(...)` (existing behavior)
    - Otherwise, call `playerManager.getBestAvailableByPosition(playerManager.getPlayers(), selectedPositionFilter)`
    - When result is `null`: show "No players available" in name, disable Draft button, hide injury status and stats views
    - _Requirements: 2.1, 2.2, 2.3, 3.1, 3.2, 3.3_

  - [x] 4.4 Update `draftBestAvailablePlayer()` to use position filter
    - Apply the same branching logic: if "Overall" use `getBestAvailable`, else use `getBestAvailableByPosition`
    - The drafted player must match what is currently displayed in the Best Available section
    - _Requirements: 5.1, 5.2_

  - [x] 4.5 Update `resetDraft()` to reset the position filter
    - Set `selectedPositionFilter = "Overall"`
    - Set `spinnerPositionFilter.setSelection(0)` to reset the Spinner to index 0
    - _Requirements: 4.3_

  - [ ]* 4.6 Write property test: Filter selection persists across draft actions
    - **Property 2: Filter selection persists across draft actions**
    - **Validates: Requirements 4.1**
    - Simulate selecting a position filter, then drafting a player and triggering `updateUI()`
    - Assert `selectedPositionFilter` is unchanged after the draft action
    - Add to `PlayerManagerFilterTest.java` or a new UI-level test file

  - [ ]* 4.7 Write property test: Draft action targets the filtered best available player
    - **Property 3: Draft action targets the filtered best available player**
    - **Validates: Requirements 5.1**
    - For random player lists and filter values, assert the player resolved by `draftBestAvailablePlayer` matches the result of the filtered lookup
    - Add to `PlayerManagerFilterTest.java`

- [x] 5. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- The design requires no new classes or data models — only additions to existing files
- Property tests use `junit-quickcheck` with `@Property(trials = 100)`
- The Spinner retains its selection across `updateUI()` calls automatically (Android Spinner behavior), satisfying Requirement 4.1 and 4.2
