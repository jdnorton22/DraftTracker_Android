# Requirements Document

## Introduction

This specification defines the requirements for adding a toggle control to the "View All Players" dialog that allows users to hide players who have already been drafted, making it easier to focus on available players during the draft.

## Glossary

- **Player_Selection_Dialog**: The dialog window that displays the list of all players when the "View All Players" button is clicked
- **Drafted_Player**: A player whose isDrafted property is set to true
- **Available_Player**: A player whose isDrafted property is set to false
- **Toggle_Control**: A UI switch or checkbox that can be turned on/off to control visibility
- **Filter**: The process of showing or hiding players based on their drafted status

## Requirements

### Requirement 1: Toggle Control Display

**User Story:** As a user, I want to see a toggle control in the player selection dialog, so that I can easily control whether drafted players are shown or hidden.

#### Acceptance Criteria

1. WHEN the Player_Selection_Dialog is opened, THE System SHALL display a toggle control labeled "Hide Drafted Players"
2. THE toggle control SHALL be positioned between the search bar and the player list
3. THE toggle control SHALL be clearly visible and accessible
4. THE toggle control SHALL default to OFF (showing all players including drafted ones)

### Requirement 2: Hide Drafted Players

**User Story:** As a user, I want to hide drafted players when the toggle is enabled, so that I can focus only on available players.

#### Acceptance Criteria

1. WHEN the toggle is turned ON, THE System SHALL hide all Drafted_Players from the displayed list
2. WHEN the toggle is turned ON, THE System SHALL continue to display all Available_Players
3. WHEN the toggle is turned OFF, THE System SHALL display all players regardless of drafted status
4. THE System SHALL maintain the toggle state while the dialog remains open

### Requirement 3: Toggle Interaction with Search

**User Story:** As a user, I want the toggle to work together with the search functionality, so that I can search within available players only.

#### Acceptance Criteria

1. WHEN both the toggle is ON and a search query is active, THE System SHALL display only Available_Players that match the search query
2. WHEN the toggle is OFF and a search query is active, THE System SHALL display all players (drafted and available) that match the search query
3. WHEN the toggle state changes, THE System SHALL re-apply the current search filter to the new player set

### Requirement 4: Visual Feedback

**User Story:** As a user, I want clear visual feedback about the toggle state, so that I understand what players are being shown.

#### Acceptance Criteria

1. WHEN the toggle is ON, THE System SHALL display the toggle in an active/enabled visual state
2. WHEN the toggle is OFF, THE System SHALL display the toggle in an inactive/disabled visual state
3. THE toggle control SHALL provide immediate visual feedback when clicked

### Requirement 5: Player Count Display

**User Story:** As a user, I want to see how many players are currently displayed, so that I understand the impact of the filter.

#### Acceptance Criteria

1. THE System SHALL display a count of visible players in the dialog
2. WHEN the toggle or search filter changes, THE System SHALL update the player count immediately
3. THE player count SHALL be displayed near the toggle control or search bar

### Requirement 6: Toggle State Persistence

**User Story:** As a user, I want the toggle state to be remembered during my draft session, so that I don't have to re-enable it each time I open the dialog.

#### Acceptance Criteria

1. WHEN the Player_Selection_Dialog is closed and reopened, THE System SHALL remember the previous toggle state
2. THE toggle state SHALL persist throughout the draft session
3. WHEN the app is restarted, THE toggle SHALL default to OFF

### Requirement 7: Performance

**User Story:** As a user, I want the toggle to respond instantly, so that the interface feels responsive.

#### Acceptance Criteria

1. WHEN the toggle state changes, THE System SHALL update the player list within 100 milliseconds
2. THE filtering operation SHALL not cause visible lag or stuttering in the UI
3. THE System SHALL handle filtering efficiently even with 300+ players in the list
