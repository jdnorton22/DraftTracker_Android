# Requirements Document

## Introduction

This feature adds bottom navigation tabs to the Fantasy Draft Picker Android app, allowing users to easily switch between the draft board and league configuration screens without navigating through separate activities. The current architecture uses two separate activities (MainActivity for drafting and ConfigActivity for configuration) with explicit navigation between them. The new architecture will use a single activity with fragments and a BottomNavigationView, providing a more modern and fluid user experience.

## Glossary

- **Draft_Screen**: The main draft interface showing current pick, best available player, recent picks, and draft action buttons
- **Config_Screen**: The league configuration interface for team setup, draft settings, and league parameters
- **Bottom_Navigation**: The navigation bar displayed at the bottom of the screen with tabs for switching between screens
- **Fragment**: An Android UI component representing a portion of the user interface within an activity
- **MainActivity**: The single host activity that contains the bottom navigation and fragment container
- **DraftFragment**: Fragment containing the draft board UI (current MainActivity content)
- **ConfigFragment**: Fragment containing the configuration UI (current ConfigActivity content)
- **Draft_State**: The current state of the draft including round, pick number, and completion status
- **Draft_Config**: The configuration settings for the draft including flow type, number of rounds, and keeper league status
- **Manager_Classes**: Singleton or shared instances of DraftManager, PlayerManager, TeamManager, and DraftCoordinator

## Requirements

### Requirement 1: Bottom Navigation UI

**User Story:** As a user, I want to see navigation tabs at the bottom of the screen, so that I can easily switch between the draft board and configuration screens.

#### Acceptance Criteria

1. THE Bottom_Navigation SHALL display two tabs labeled "Draft" and "Config"
2. THE Bottom_Navigation SHALL remain visible at the bottom of the screen at all times
3. WHEN a tab is selected, THE Bottom_Navigation SHALL highlight the active tab with a visual indicator
4. THE Bottom_Navigation SHALL use Material Design icons for each tab (draft icon for Draft tab, settings icon for Config tab)
5. THE Bottom_Navigation SHALL occupy a fixed height at the bottom of the screen without overlapping content

### Requirement 2: Draft Screen Fragment

**User Story:** As a user, I want to access the draft board through the Draft tab, so that I can make picks and view draft progress.

#### Acceptance Criteria

1. WHEN the Draft tab is selected, THE Draft_Screen SHALL display the current draft board interface
2. THE Draft_Screen SHALL show the current pick information including overall pick number, round, and current team
3. THE Draft_Screen SHALL display the best available player with position and rank
4. THE Draft_Screen SHALL show the three most recent picks with player details
5. THE Draft_Screen SHALL provide action buttons for making picks, viewing history, resetting draft, and exporting CSV
6. THE Draft_Screen SHALL display the league configuration summary in a collapsible header
7. WHEN a user makes a pick, THE Draft_Screen SHALL update immediately to reflect the new draft state
8. WHEN the draft is complete, THE Draft_Screen SHALL disable draft action buttons except reset and export

### Requirement 3: Config Screen Fragment

**User Story:** As a user, I want to access league configuration through the Config tab, so that I can modify team names, draft order, and league settings.

#### Acceptance Criteria

1. WHEN the Config tab is selected, THE Config_Screen SHALL display the league configuration interface
2. THE Config_Screen SHALL provide input fields for league name, team count, number of rounds, and draft flow type
3. THE Config_Screen SHALL display a list of teams with editable names and draft positions
4. THE Config_Screen SHALL provide a checkbox for enabling keeper league mode
5. WHEN configuration changes are saved, THE Config_Screen SHALL validate team names are unique and non-empty
6. WHEN configuration changes are saved, THE Config_Screen SHALL validate draft order is complete (1 to N with no gaps)
7. WHEN configuration is saved, THE Config_Screen SHALL persist changes immediately
8. WHEN switching away from the Config tab, THE Config_Screen SHALL preserve unsaved changes in the UI

### Requirement 4: Fragment Navigation

**User Story:** As a user, I want to switch between Draft and Config tabs seamlessly, so that I can quickly access different parts of the app.

#### Acceptance Criteria

1. WHEN a user taps the Draft tab, THE MainActivity SHALL display the DraftFragment
2. WHEN a user taps the Config tab, THE MainActivity SHALL display the ConfigFragment
3. WHEN switching between tabs, THE MainActivity SHALL preserve the state of each fragment
4. WHEN switching between tabs, THE MainActivity SHALL complete the transition within 300ms
5. THE MainActivity SHALL display the Draft tab as the default screen on app launch

### Requirement 5: State Management

**User Story:** As a developer, I want fragments to share draft state through manager classes, so that changes in one fragment are reflected in the other.

#### Acceptance Criteria

1. THE DraftFragment SHALL access draft state through shared Manager_Classes instances
2. THE ConfigFragment SHALL access draft state through shared Manager_Classes instances
3. WHEN the ConfigFragment updates Draft_Config, THE DraftFragment SHALL reflect the updated configuration when displayed
4. WHEN the DraftFragment makes a pick, THE updated Draft_State SHALL be available to the ConfigFragment
5. THE MainActivity SHALL initialize Manager_Classes instances once and share them with both fragments
6. WHEN the app is paused or stopped, THE MainActivity SHALL persist the current Draft_State and Draft_Config

### Requirement 6: Configuration Editing Restrictions

**User Story:** As a user, I want to be prevented from editing certain configuration settings during an active draft, so that I don't accidentally invalidate the draft state.

#### Acceptance Criteria

1. WHEN the draft has at least one pick, THE Config_Screen SHALL disable the team count picker
2. WHEN the draft has at least one pick, THE Config_Screen SHALL disable the number of rounds picker
3. WHEN the draft has at least one pick, THE Config_Screen SHALL disable the draft flow type spinner
4. WHEN the draft has at least one pick, THE Config_Screen SHALL display a message indicating these settings cannot be changed during an active draft
5. WHEN the draft has at least one pick, THE Config_Screen SHALL allow editing of team names and league name
6. WHEN the draft is reset, THE Config_Screen SHALL re-enable all configuration controls

### Requirement 7: Remove Edit Config Button

**User Story:** As a user, I no longer need the "Edit Config" button on the draft screen, so that the UI is cleaner and I can use tabs for navigation.

#### Acceptance Criteria

1. THE Draft_Screen SHALL NOT display an "Edit Config" button
2. WHEN a user wants to edit configuration, THE user SHALL tap the Config tab in the Bottom_Navigation
3. THE Draft_Screen SHALL maintain all other action buttons (Make Pick, Reset Draft, View History, Export CSV)

### Requirement 8: Persistence Integration

**User Story:** As a user, I want my draft state and configuration to be saved automatically, so that I don't lose progress when switching tabs or closing the app.

#### Acceptance Criteria

1. WHEN switching between tabs, THE MainActivity SHALL save the current Draft_State to persistence
2. WHEN the app is paused, THE MainActivity SHALL save the current Draft_State and Draft_Config to persistence
3. WHEN the app is resumed, THE MainActivity SHALL load the saved Draft_State and Draft_Config from persistence
4. WHEN persistence fails, THE MainActivity SHALL display an error message and continue without saving
5. THE persistence mechanism SHALL save the complete draft snapshot including teams, players, pick history, and configuration

### Requirement 9: Layout Adaptation

**User Story:** As a user, I want the app to use screen space efficiently with bottom navigation, so that I can see all content without scrolling excessively.

#### Acceptance Criteria

1. THE Draft_Screen SHALL adjust its layout to account for the Bottom_Navigation height
2. THE Config_Screen SHALL adjust its layout to account for the Bottom_Navigation height
3. WHEN the Bottom_Navigation is displayed, THE content area SHALL not be obscured by the navigation bar
4. THE Bottom_Navigation SHALL use a fixed height of 56dp (Material Design standard)
5. THE content area SHALL be scrollable if content exceeds available height

### Requirement 10: Back Button Behavior

**User Story:** As a user, I want the back button to exit the app rather than navigate between tabs, so that the behavior is predictable.

#### Acceptance Criteria

1. WHEN the user presses the back button, THE MainActivity SHALL exit the app
2. THE MainActivity SHALL NOT navigate between tabs when the back button is pressed
3. WHEN the user presses the back button, THE MainActivity SHALL save the current state before exiting

### Requirement 11: Fragment Lifecycle Management

**User Story:** As a developer, I want fragments to properly manage their lifecycle, so that resources are used efficiently and state is preserved correctly.

#### Acceptance Criteria

1. WHEN a fragment is created, THE fragment SHALL initialize its UI components
2. WHEN a fragment is resumed, THE fragment SHALL refresh its UI with current data from Manager_Classes
3. WHEN a fragment is paused, THE fragment SHALL not perform any background operations
4. WHEN a fragment is destroyed, THE fragment SHALL release any resources it holds
5. THE fragments SHALL NOT retain references to Manager_Classes that could cause memory leaks
