# Requirements Document

## Introduction

This feature adds a popup dialog that displays the roster of players drafted by any team in the Fantasy Draft Picker Android app. Users can access this popup via a clickable link/button next to the "On the clock" team name in the draft UI. The popup defaults to showing the on-the-clock team's roster but includes a team selector to switch between any team in the draft. Each roster entry displays comprehensive player attributes including rankings, stats, injury status, and an ESPN profile link, allowing users to thoroughly assess any team's draft strategy.

## Glossary

- **Team_Roster_Popup**: A dialog component that displays a scrollable list of players drafted by a selected team, with a team selector to switch between teams
- **Draft_Fragment**: The main draft UI fragment that displays current pick information and draft controls
- **On_The_Clock_Team**: The team that is currently making their draft pick
- **Pick_History**: The chronological list of all draft picks made during the current draft session
- **Player_Manager**: The component responsible for managing player data and lookup operations
- **Roster_Item**: A single player entry displayed in the Team_Roster_Popup list, showing comprehensive player attributes
- **Team_Selector**: A dropdown or selector control within the Team_Roster_Popup that allows the user to switch between any team in the draft
- **Player**: A data model representing a draft-eligible player with attributes: id (String), name (String), position (String), rank (int), nflTeam (String), pffRank (int), positionRank (int), lastYearStats (String), injuryStatus (String), espnId (String), byeWeek (int), isDrafted (boolean), draftedBy (String)

## Requirements

### Requirement 1: Roster Popup Access Button

**User Story:** As a fantasy draft user, I want to see a clickable button next to the "On the clock" team name, so that I can quickly view that team's current roster.

#### Acceptance Criteria

1. THE Draft_Fragment SHALL display a clickable button adjacent to the "On the clock" team name text
2. THE button SHALL be visually distinguishable as an interactive element using an icon or text indicator
3. WHEN the button is tapped, THE Draft_Fragment SHALL open the Team_Roster_Popup dialog with the On_The_Clock_Team selected by default
4. THE button SHALL remain visible whenever a team is on the clock during an active draft

### Requirement 2: Team Selection

**User Story:** As a fantasy draft user, I want to select which team's roster to view, so that I can assess any team's draft strategy and not just the on-the-clock team.

#### Acceptance Criteria

1. THE Team_Roster_Popup SHALL display a Team_Selector control that lists all teams participating in the draft
2. WHEN the Team_Roster_Popup is first opened, THE Team_Selector SHALL default to the On_The_Clock_Team
3. WHEN the user selects a different team from the Team_Selector, THE Team_Roster_Popup SHALL update the roster list to display players drafted by the newly selected team
4. WHEN the user selects a different team from the Team_Selector, THE Team_Roster_Popup SHALL update the dialog title to reflect the selected team name
5. THE Team_Selector SHALL display all team names in a consistent order matching the draft order

### Requirement 3: Team Roster Popup Display

**User Story:** As a fantasy draft user, I want to see a popup showing all players drafted by the selected team, so that I can understand their roster composition.

#### Acceptance Criteria

1. WHEN the Team_Roster_Popup is opened, THE Team_Roster_Popup SHALL display the selected team name in the dialog title
2. THE Team_Roster_Popup SHALL display a scrollable list of all players drafted by the selected team
3. WHEN the selected team has drafted zero players, THE Team_Roster_Popup SHALL display an empty state message indicating no players have been drafted
4. THE Team_Roster_Popup SHALL include a close button to dismiss the dialog


### Requirement 4: Roster Item Display

**User Story:** As a fantasy draft user, I want to see comprehensive player information in the roster list, so that I can thoroughly assess the team's draft strategy and player quality.

#### Acceptance Criteria

1. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the player name
2. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the player position with position-specific color coding
3. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the NFL team abbreviation (nflTeam)
4. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the overall rank
5. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the PFF ranking (pffRank)
6. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the position-specific ranking (positionRank)
7. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the previous season statistics (lastYearStats)
8. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the current injury status (injuryStatus)
9. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the bye week number (byeWeek)
10. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL display the round and pick number when the player was drafted
11. FOR EACH Roster_Item, THE Team_Roster_Popup SHALL provide a clickable link to the player's ESPN profile using the espnId to construct the ESPN profile URL
12. WHEN a player has no injury status value, THE Team_Roster_Popup SHALL omit the injury status field for that Roster_Item
13. THE Roster_Item list SHALL be ordered by draft pick number in ascending order

### Requirement 5: Scrollable Roster List

**User Story:** As a fantasy draft user, I want the roster list to be scrollable, so that I can view all drafted players even when a team has many picks.

#### Acceptance Criteria

1. WHEN the roster contains more players than can fit in the visible area, THE Team_Roster_Popup SHALL enable vertical scrolling
2. THE Team_Roster_Popup SHALL have a maximum height that leaves visible space around the dialog edges
3. THE scrollable list SHALL support smooth scrolling behavior consistent with Android platform conventions

### Requirement 6: Data Retrieval

**User Story:** As a fantasy draft user, I want the roster popup to show accurate and current data, so that I can make informed decisions.

#### Acceptance Criteria

1. WHEN the Team_Roster_Popup is opened, THE system SHALL retrieve all picks from Pick_History where the team ID matches the selected team
2. WHEN the user selects a different team via the Team_Selector, THE system SHALL retrieve all picks from Pick_History where the team ID matches the newly selected team
3. FOR EACH pick retrieved, THE system SHALL use Player_Manager to look up the full Player details including all Player attributes
4. IF a player cannot be found in Player_Manager, THEN THE Team_Roster_Popup SHALL display the pick with a placeholder indicating unknown player