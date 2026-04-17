# Implementation Plan: Team Roster Popup

## Overview

Implement a dialog that displays all players drafted by a selected team during a fantasy draft session. The dialog is accessed via a button next to the "On the clock" team name in DraftFragment, includes a Spinner to switch between teams, and shows a scrollable list of roster items with comprehensive player attributes. The implementation follows the existing `PlayerSelectionDialog` pattern.

## Tasks

- [x] 1. Create RosterEntry data holder and layout files
  - [x] 1.1 Create `RosterEntry` POJO class at `com.fantasydraft.picker.ui.RosterEntry`
    - Define fields: `Pick pick`, `Player player`
    - Add constructor, getters
    - _Requirements: 6.3, 6.4_

  - [x] 1.2 Create `dialog_team_roster.xml` layout file
    - Add dialog title TextView for team name
    - Add Spinner for team selection
    - Add RecyclerView for roster list
    - Add empty state TextView ("No players drafted yet")
    - Add close Button to dismiss dialog
    - Set max height constraint to leave visible space around dialog edges
    - _Requirements: 2.1, 3.1, 3.2, 3.3, 3.4, 5.2_

  - [x] 1.3 Create `item_roster_entry.xml` layout file
    - Add TextViews for: player name, position (with color-coded background), NFL team, overall rank, PFF rank, position rank, last year stats, injury status, bye week, round/pick number
    - Add clickable ESPN link TextView
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 4.10, 4.11_

- [x] 2. Implement TeamRosterAdapter
  - [x] 2.1 Create `TeamRosterAdapter` RecyclerView.Adapter at `com.fantasydraft.picker.ui.TeamRosterAdapter`
    - Implement `onCreateViewHolder` inflating `item_roster_entry.xml`
    - Implement `onBindViewHolder` binding all player attributes from `RosterEntry`
    - Use `PositionColors.getColorForPosition()` for position badge color coding
    - Hide injury status field when value is null or empty (Req 4.12)
    - Hide ESPN link when `espnId` is null or empty
    - Show "Unknown Player" placeholder when `RosterEntry.getPlayer()` is null (Req 6.4)
    - Open browser via Intent using `Player.getEspnUrl()` on ESPN link click (Req 4.11)
    - Implement `updateEntries(List<RosterEntry>)` to refresh the list
    - _Requirements: 4.1–4.13, 6.4_

  - [ ]* 2.2 Write property test: Pick filtering by team returns exactly that team's picks
    - **Property 1: Pick filtering by team returns exactly that team's picks**
    - Generate random pick histories with multiple teams, select a random team ID, filter, and assert only matching picks are returned
    - **Validates: Requirements 2.3, 3.2, 6.1, 6.2**

  - [ ]* 2.3 Write property test: Roster entries are ordered by pick number ascending
    - **Property 3: Roster entries are ordered by pick number ascending**
    - Generate random sets of picks for a team, build roster entries, and assert the resulting list is sorted by pick number
    - **Validates: Requirements 4.13**

- [x] 3. Implement TeamRosterDialog
  - [x] 3.1 Create `TeamRosterDialog` Dialog subclass at `com.fantasydraft.picker.ui.TeamRosterDialog`
    - Accept constructor params: `Context`, `List<Team>`, `String defaultTeamId`, `DraftManager`, `PlayerManager`
    - In `initializeViews()`: bind title, Spinner, RecyclerView, empty state, close button
    - Set up close button to dismiss dialog
    - _Requirements: 3.1, 3.4_

  - [x] 3.2 Implement team Spinner setup and selection handling
    - Populate Spinner with all teams sorted by `draftPosition` ascending (Req 2.5)
    - Set default selection to `defaultTeamId` (the on-the-clock team) (Req 2.2)
    - On team selection change: update dialog title with team name (Req 2.4) and call `loadRosterForTeam()` (Req 2.3)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

  - [x] 3.3 Implement `buildRosterEntries(String teamId)` and `loadRosterForTeam(String teamId)`
    - Filter `DraftManager.getPickHistory()` by selected team ID (Req 6.1, 6.2)
    - Resolve each pick's `playerId` via `PlayerManager.getPlayerById()` (Req 6.3)
    - Create `RosterEntry` for each pick/player pair, using null player for unknown players (Req 6.4)
    - Sort entries by `pick.getPickNumber()` ascending (Req 4.13)
    - Show/hide empty state vs RecyclerView based on entry count (Req 3.3)
    - Update adapter with new entries
    - _Requirements: 3.2, 3.3, 4.13, 6.1, 6.2, 6.3, 6.4_

  - [ ]* 3.4 Write property test: Team selector contains all teams in draft position order
    - **Property 2: Team selector contains all teams in draft position order**
    - Generate random lists of teams with various draft positions, build the spinner data, and assert all teams are present and sorted by draft position
    - **Validates: Requirements 2.1, 2.5**

  - [ ]* 3.5 Write property test: Dialog title reflects selected team name
    - **Property 6: Dialog title reflects selected team name**
    - Generate random team names, simulate selection, and verify the title contains the team name
    - **Validates: Requirements 2.4, 3.1**

- [x] 4. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Integrate roster button into DraftFragment
  - [x] 5.1 Add roster button to `fragment_draft.xml`
    - Add `ImageButton` with id `button_view_roster` adjacent to the "On the clock" team name (`textCurrentTeam`)
    - Use an appropriate icon (e.g., `ic_list` or `ic_group`) to make it visually distinguishable as interactive
    - _Requirements: 1.1, 1.2_

  - [x] 5.2 Wire roster button in `DraftFragment.java`
    - Add `ImageButton buttonViewRoster` field
    - In `initializeViews()`: bind `R.id.button_view_roster`
    - Set click listener to call new `showTeamRosterDialog()` method
    - In `showTeamRosterDialog()`: get teams from `MainActivity`, get current team ID, construct and show `TeamRosterDialog`
    - In `updateCurrentPick()`: show/hide roster button based on draft active state (Req 1.4)
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [ ]* 5.3 Write property test: Roster item contains all required player attributes
    - **Property 4: Roster item contains all required player attributes**
    - Generate random Player and Pick objects, create a RosterEntry, and assert all required fields are present in the bound data
    - **Validates: Requirements 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.9, 4.10**

  - [ ]* 5.4 Write property test: ESPN URL construction round trip
    - **Property 5: ESPN URL construction round trip**
    - Generate random non-empty espnId strings and verify `getEspnUrl()` returns the correct URL pattern. Generate null/empty espnIds and verify null is returned
    - **Validates: Requirements 4.11**

- [x] 6. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests use jqwik as the property-based testing library (per design)
- The implementation follows the existing `PlayerSelectionDialog` pattern for consistency
- All existing models (Player, Pick, Team) are used as-is with no modifications needed
