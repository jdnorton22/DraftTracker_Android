# Requirements Document

## Introduction

The Fantasy Draft Picker app currently displays a single "Best Available Player" in the DraftFragment, showing the highest-ranked undrafted player regardless of position. This feature adds a position filter (Spinner/dropdown) to the Best Available section, allowing users to filter by a specific position (QB, RB, WR, TE, K, DST) or view the overall best available player. When a position is selected, the display updates to show the top-ranked undrafted player at that position.

## Glossary

- **Position_Filter_Spinner**: An Android Spinner widget placed in the Best Available card that allows the user to select a position filter value
- **Best_Available_Display**: The UI section in the DraftFragment that shows the top-ranked undrafted player based on the current filter selection
- **PlayerManager**: The manager class responsible for player data operations including finding the best available player
- **DraftFragment**: The main UI fragment that contains the draft interface including the Best Available section
- **Player**: The data model representing a fantasy football player, containing fields such as name, position, rank, and drafted status
- **Position_Value**: One of the following filter options: "Overall", "QB", "RB", "WR", "TE", "K", "DST"

## Requirements

### Requirement 1: Position Filter Spinner

**User Story:** As a fantasy football drafter, I want a position filter dropdown in the Best Available section, so that I can quickly see the best undrafted player at a specific position.

#### Acceptance Criteria

1. THE DraftFragment SHALL display a Position_Filter_Spinner within the Best Available card, above the player info display
2. THE Position_Filter_Spinner SHALL contain exactly seven options in the following order: "Overall", "QB", "RB", "WR", "TE", "K", "DST"
3. WHEN the DraftFragment is first displayed, THE Position_Filter_Spinner SHALL default to "Overall"
4. THE Position_Filter_Spinner SHALL be accessible with a content description indicating its purpose as a position filter

### Requirement 2: Filtered Best Available Lookup

**User Story:** As a fantasy football drafter, I want the best available player to update based on my position selection, so that I can make informed draft decisions by position.

#### Acceptance Criteria

1. WHEN "Overall" is selected in the Position_Filter_Spinner, THE Best_Available_Display SHALL show the highest-ranked undrafted Player across all positions (existing behavior)
2. WHEN a specific position is selected in the Position_Filter_Spinner, THE PlayerManager SHALL return the highest-ranked undrafted Player whose position field matches the selected Position_Value
3. WHEN the user selects a new Position_Value, THE Best_Available_Display SHALL update immediately to reflect the filtered result
4. THE PlayerManager SHALL determine "highest-ranked" as the undrafted Player with the lowest rank value among players matching the selected position

### Requirement 3: Empty Position Handling

**User Story:** As a fantasy football drafter, I want clear feedback when no players are available at a selected position, so that I know to look at other positions.

#### Acceptance Criteria

1. IF no undrafted players exist for the selected Position_Value, THEN THE Best_Available_Display SHALL show "No players available" as the player name
2. IF no undrafted players exist for the selected Position_Value, THEN THE Best_Available_Display SHALL disable the "Draft This Player" button
3. IF no undrafted players exist for the selected Position_Value, THEN THE Best_Available_Display SHALL hide the injury status and stats text views

### Requirement 4: Filter Persistence Across UI Updates

**User Story:** As a fantasy football drafter, I want my position filter selection to persist when the draft state changes, so that I don't have to reselect my filter after each pick.

#### Acceptance Criteria

1. WHEN a player is drafted and the UI refreshes, THE Position_Filter_Spinner SHALL retain the previously selected Position_Value
2. WHEN updateUI is called on the DraftFragment, THE Best_Available_Display SHALL re-evaluate the best available player using the current Position_Filter_Spinner selection
3. WHEN the draft is reset, THE Position_Filter_Spinner SHALL reset to "Overall"

### Requirement 5: Draft Best Available with Filter

**User Story:** As a fantasy football drafter, I want the "Draft This Player" button to draft whichever player is currently shown in the filtered Best Available display, so that I can quickly draft the best player at my desired position.

#### Acceptance Criteria

1. WHEN the user taps "Draft This Player", THE DraftFragment SHALL draft the player currently displayed in the Best_Available_Display, regardless of the active Position_Value filter
2. AFTER a player is drafted via the "Draft This Player" button, THE Best_Available_Display SHALL update to show the next best available player for the currently selected Position_Value
