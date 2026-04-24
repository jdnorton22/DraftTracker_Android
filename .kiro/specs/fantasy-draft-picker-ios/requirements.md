# Requirements Document - Fantasy Draft Picker iOS

## Introduction

The Fantasy Draft Picker iOS is a native iOS application that helps users manage fantasy football drafts by tracking team names, draft order, draft flow patterns, and providing real-time best available player recommendations. This is a port of the Android application to iOS using Swift and SwiftUI.

## Glossary

- **Draft_System**: The complete fantasy football draft management iOS application
- **Draft_Order**: The sequence in which teams make their selections
- **Serpentine_Draft**: A draft pattern where the order reverses each round (e.g., 1-2-3-3-2-1)
- **Linear_Draft**: A draft pattern where the order remains constant each round (e.g., 1-2-3-1-2-3)
- **Best_Available_Player**: The highest-ranked player recommendation displayed to users
- **Team**: A participating fantasy football team with an assigned name and draft position
- **Draft_Pick**: A selection made by a team during the draft
- **Player_Data**: Information about fantasy football players including name, position, NFL team, and rankings
- **Core_Data**: Apple's framework for data persistence and object graph management
- **UserDefaults**: iOS system for storing simple key-value data
- **SwiftUI**: Apple's declarative UI framework for building iOS interfaces

## Requirements

### Requirement 1: Team Configuration

**User Story:** As a draft administrator, I want to configure participating teams with names and count, so that I can set up the draft with all participants.

#### Acceptance Criteria

1. WHEN the administrator specifies a team count, THE Draft_System SHALL accept any positive integer between 2 and 20
2. WHEN the administrator assigns team names, THE Draft_System SHALL store each unique team name
3. WHEN duplicate team names are provided, THE Draft_System SHALL reject the duplicate and request a unique name
4. WHEN all teams are configured, THE Draft_System SHALL display the complete list of participating teams
5. THE Draft_System SHALL provide iOS-native input controls (TextField, Stepper) for team configuration
6. WHEN team configuration is modified, THE Draft_System SHALL persist changes immediately using Core Data

### Requirement 2: Draft Order Management

**User Story:** As a draft administrator, I want to set the draft order for all teams, so that picks proceed in the correct sequence.

#### Acceptance Criteria

1. WHEN the administrator sets the draft order, THE Draft_System SHALL assign each team a unique position from 1 to N (where N is the team count)
2. WHEN the draft order is set, THE Draft_System SHALL validate that all positions from 1 to N are assigned exactly once
3. WHEN the draft order is modified, THE Draft_System SHALL update the pick sequence accordingly
4. THE Draft_System SHALL display the current draft order with team names and positions
5. THE Draft_System SHALL provide drag-and-drop reordering using iOS List with onMove modifier
6. THE Draft_System SHALL provide a shuffle button to randomize draft order

### Requirement 3: Draft Flow Configuration

**User Story:** As a draft administrator, I want to choose between serpentine and linear draft flows, so that the draft follows the desired pattern.

#### Acceptance Criteria

1. THE Draft_System SHALL support serpentine draft flow where pick order reverses each round
2. THE Draft_System SHALL support linear draft flow where pick order remains constant each round
3. WHEN serpentine flow is selected, THE Draft_System SHALL alternate the direction of picks between rounds
4. WHEN linear flow is selected, THE Draft_System SHALL maintain the same pick order for all rounds
5. WHEN the draft flow is changed, THE Draft_System SHALL recalculate the complete pick sequence
6. THE Draft_System SHALL use iOS Picker or Segmented Control for flow type selection

### Requirement 4: Current Pick Tracking

**User Story:** As a draft participant, I want to see whose turn it is to pick, so that I know when to make my selection.

#### Acceptance Criteria

1. WHEN the draft is in progress, THE Draft_System SHALL display the current team on the clock
2. WHEN a pick is made, THE Draft_System SHALL advance to the next team in the sequence
3. WHEN a round completes, THE Draft_System SHALL transition to the first pick of the next round according to the draft flow
4. THE Draft_System SHALL display the current round number and pick number within the round
5. THE Draft_System SHALL use prominent iOS-style cards or sections to highlight the current pick
6. THE Draft_System SHALL provide haptic feedback when advancing to the next pick

### Requirement 5: Best Available Player Display

**User Story:** As a draft participant, I want to see the best available player recommendation, so that I can make informed draft decisions.

#### Acceptance Criteria

1. THE Draft_System SHALL display the best available player recommendation prominently
2. WHEN a player is drafted, THE Draft_System SHALL update the best available player recommendation immediately
3. THE Draft_System SHALL maintain the best available player display consistently throughout the draft
4. WHEN multiple players have equal rankings, THE Draft_System SHALL display one player according to a consistent tiebreaker rule
5. THE Draft_System SHALL exclude already-drafted players from the best available recommendation
6. THE Draft_System SHALL use iOS-native card design with SF Symbols for position icons
7. THE Draft_System SHALL support tapping the best available card to draft that player directly
8. THE Draft_System SHALL show a confirmation alert before drafting from the best available card

### Requirement 6: Draft State Persistence

**User Story:** As a draft administrator, I want the draft state to be saved, so that the draft can continue if interrupted.

#### Acceptance Criteria

1. WHEN any draft configuration changes, THE Draft_System SHALL persist the configuration immediately using Core Data
2. WHEN a pick is made, THE Draft_System SHALL persist the updated draft state immediately
3. WHEN the application restarts, THE Draft_System SHALL restore the most recent draft state from Core Data
4. THE Draft_System SHALL maintain data integrity during save and restore operations
5. THE Draft_System SHALL use Core Data with proper entity relationships for complex data
6. THE Draft_System SHALL use UserDefaults for simple configuration preferences
7. WHEN persistence fails, THE Draft_System SHALL display an iOS-native alert and attempt recovery

### Requirement 7: Player Selection

**User Story:** As a draft participant, I want to select players during my turn, so that I can build my fantasy team.

#### Acceptance Criteria

1. WHEN it is a team's turn to pick, THE Draft_System SHALL allow that team to select an available player
2. WHEN a player is selected, THE Draft_System SHALL mark that player as drafted
3. WHEN a player is selected, THE Draft_System SHALL associate the player with the selecting team
4. WHEN an already-drafted player is selected, THE Draft_System SHALL reject the selection and display an iOS-native alert
5. WHEN a pick is completed, THE Draft_System SHALL advance to the next pick in the sequence
6. THE Draft_System SHALL provide a searchable player list using iOS SearchBar
7. THE Draft_System SHALL support filtering players by position using iOS Picker or Segmented Control
8. THE Draft_System SHALL show player details in a sheet or modal presentation
9. THE Draft_System SHALL require confirmation before finalizing a pick using iOS Alert

### Requirement 8: Draft History

**User Story:** As a draft participant, I want to view all completed picks, so that I can see which players have been drafted and by whom.

#### Acceptance Criteria

1. THE Draft_System SHALL display a chronological list of all completed picks using iOS List
2. WHEN a pick is made, THE Draft_System SHALL add it to the draft history immediately
3. WHEN displaying draft history, THE Draft_System SHALL show the team name, player name, position, and pick number for each selection
4. THE Draft_System SHALL maintain the complete draft history throughout the draft session
5. THE Draft_System SHALL support sorting draft history by pick number, team, or position
6. THE Draft_System SHALL use iOS-native list styling with dividers and section headers
7. THE Draft_System SHALL support pull-to-refresh gesture to update the history view
8. THE Draft_System SHALL provide an undo button for the most recent pick
9. THE Draft_System SHALL use color coding by position (matching iOS design guidelines)

### Requirement 9: Draft Reset

**User Story:** As a draft administrator, I want to reset the draft to its initial state, so that I can start a new draft or redo the current one.

#### Acceptance Criteria

1. WHEN the administrator initiates a draft reset, THE Draft_System SHALL clear all completed picks
2. WHEN a draft is reset, THE Draft_System SHALL mark all previously drafted players as available
3. WHEN a draft is reset, THE Draft_System SHALL return the current pick to the first position in the draft order
4. WHEN a draft is reset, THE Draft_System SHALL preserve the team configuration and draft settings
5. WHEN a draft reset is requested, THE Draft_System SHALL prompt for confirmation using iOS ActionSheet
6. THE Draft_System SHALL provide haptic feedback when reset is confirmed
7. THE Draft_System SHALL animate the reset transition using SwiftUI animations

### Requirement 10: Player Data Management

**User Story:** As a draft administrator, I want to have access to comprehensive player data including rankings, positions, and NFL teams, so that I can make informed draft decisions.

#### Acceptance Criteria

1. WHEN the application starts, THE Draft_System SHALL load player data from a bundled JSON file
2. THE Draft_System SHALL parse and store player name, position, NFL team, overall rank, ADP rank, and position rank
3. THE Draft_System SHALL display player statistics including last year's performance data
4. THE Draft_System SHALL show injury status for players with color-coded indicators
5. THE Draft_System SHALL support manual player data refresh from bundled resources
6. THE Draft_System SHALL cache player data in Core Data for offline access
7. WHEN player data is updated, THE Draft_System SHALL preserve draft state and only update player information

### Requirement 11: iOS-Specific Features

**User Story:** As an iOS user, I want the app to follow iOS design guidelines and support iOS-specific features, so that it feels native to the platform.

#### Acceptance Criteria

1. THE Draft_System SHALL use SwiftUI for all user interface components
2. THE Draft_System SHALL support both light and dark mode with appropriate color schemes
3. THE Draft_System SHALL use SF Symbols for all icons
4. THE Draft_System SHALL support Dynamic Type for accessibility
5. THE Draft_System SHALL provide VoiceOver support for visually impaired users
6. THE Draft_System SHALL use iOS-native navigation patterns (NavigationView, TabView)
7. THE Draft_System SHALL support iPad with adaptive layouts using size classes
8. THE Draft_System SHALL use iOS-native gestures (swipe, long-press, drag)
9. THE Draft_System SHALL provide haptic feedback for important actions
10. THE Draft_System SHALL support landscape orientation on iPhone and iPad

### Requirement 12: Export and Sharing

**User Story:** As a draft administrator, I want to export and share draft results, so that participants can review their teams.

#### Acceptance Criteria

1. THE Draft_System SHALL support exporting draft results to CSV format
2. THE Draft_System SHALL use iOS Share Sheet for sharing draft results
3. WHEN exporting to CSV, THE Draft_System SHALL include all pick details (pick number, round, team, player, position)
4. THE Draft_System SHALL support sharing draft history as formatted text via Messages, Mail, or other apps
5. THE Draft_System SHALL allow saving draft results to Files app
6. THE Draft_System SHALL support AirDrop for quick sharing to nearby devices

### Requirement 13: Custom Player Entry

**User Story:** As a draft administrator, I want to add custom players not in the default list, so that I can include players from other leagues or positions.

#### Acceptance Criteria

1. THE Draft_System SHALL provide a form for adding custom players
2. WHEN adding a custom player, THE Draft_System SHALL require name and position
3. WHEN adding a custom player, THE Draft_System SHALL allow optional fields for NFL team and ranking
4. THE Draft_System SHALL validate custom player data before saving
5. THE Draft_System SHALL persist custom players in Core Data
6. THE Draft_System SHALL display custom players alongside default players in the player list
7. THE Draft_System SHALL allow editing or deleting custom players

### Requirement 14: Draft Timer

**User Story:** As a draft administrator, I want to set a time limit for each pick, so that the draft proceeds at a reasonable pace.

#### Acceptance Criteria

1. THE Draft_System SHALL support configuring a pick timer (30 seconds to 5 minutes)
2. WHEN the timer is enabled, THE Draft_System SHALL display a countdown for the current pick
3. WHEN the timer expires, THE Draft_System SHALL play a sound notification
4. WHEN the timer expires, THE Draft_System SHALL optionally auto-draft the best available player
5. THE Draft_System SHALL allow pausing and resuming the timer
6. THE Draft_System SHALL use iOS-native progress indicators for the countdown
7. THE Draft_System SHALL provide haptic feedback when timer reaches 10 seconds remaining

### Requirement 15: Multiple Draft Boards

**User Story:** As a user managing multiple leagues, I want to maintain separate draft boards, so that I can manage drafts for different leagues independently.

#### Acceptance Criteria

1. THE Draft_System SHALL support creating multiple draft boards
2. WHEN creating a new draft board, THE Draft_System SHALL prompt for a league name
3. THE Draft_System SHALL display a list of all saved draft boards
4. WHEN selecting a draft board, THE Draft_System SHALL load that board's configuration and state
5. THE Draft_System SHALL allow deleting draft boards with confirmation
6. THE Draft_System SHALL use Core Data to persist multiple draft boards
7. THE Draft_System SHALL provide a tab or navigation interface to switch between draft boards
