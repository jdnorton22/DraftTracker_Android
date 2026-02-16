# Implementation Plan: Fantasy Draft Picker

## Overview

This implementation plan breaks down the Fantasy Draft Picker Android application into incremental coding tasks. Each task builds on previous work, starting with core data models and business logic, then adding persistence, and finally implementing the UI layer. The plan includes property-based tests to validate correctness properties from the design document.

## Tasks

- [x] 1. Set up Android project structure and dependencies
  - Create new Android project with minimum SDK 24
  - Add dependencies: junit-quickcheck for property testing, Mockito for mocking
  - Set up project package structure: models, managers, persistence, ui
  - Create initial Gradle configuration
  - _Requirements: All_

- [x] 2. Implement core data models
  - [x] 2.1 Create Team, Player, Pick, DraftState, DraftConfig, and FlowType classes
    - Implement all model classes with proper constructors, getters, and setters
    - Add equals() and hashCode() methods for proper comparison
    - _Requirements: 1.1, 1.2, 2.1, 3.1, 3.2, 4.1, 7.2, 8.1_

  - [x] 2.2 Write unit tests for data models
    - Test model creation, getters, setters, and equality
    - _Requirements: 1.1, 1.2, 2.1_

- [x] 3. Implement TeamManager
  - [x] 3.1 Create TeamManager class with team management methods
    - Implement addTeam(), validateTeamName(), validateDraftOrder(), updateDraftPosition()
    - _Requirements: 1.2, 1.3, 2.1, 2.2_

  - [x] 3.2 Write property test for team count validation
    - **Property 1: Team Count Validation**
    - **Validates: Requirements 1.1**

  - [x] 3.3 Write property test for team name uniqueness
    - **Property 2: Team Name Uniqueness**
    - **Validates: Requirements 1.3**

  - [x] 3.4 Write property test for draft order completeness
    - **Property 3: Draft Order Completeness**
    - **Validates: Requirements 2.1, 2.2**

  - [x] 3.5 Write unit tests for TeamManager edge cases
    - Test invalid team positions, empty names, null handling
    - _Requirements: 1.2, 1.3, 2.1_

- [x] 4. Implement DraftManager
  - [x] 4.1 Create DraftManager class with draft flow logic
    - Implement getCurrentTeamIndex(), advancePick(), generatePickSequence(), resetDraft()
    - Implement serpentine and linear flow calculations
    - _Requirements: 3.1, 3.2, 4.2, 4.3, 9.1, 9.3_

  - [x] 4.2 Write property test for serpentine flow alternation
    - **Property 4: Serpentine Flow Alternation**
    - **Validates: Requirements 3.1, 3.3**

  - [x] 4.3 Write property test for linear flow consistency
    - **Property 5: Linear Flow Consistency**
    - **Validates: Requirements 3.2, 3.4**

  - [x] 4.4 Write property test for pick sequence recalculation
    - **Property 6: Pick Sequence Recalculation**
    - **Validates: Requirements 2.3, 3.5**

  - [x] 4.5 Write property test for pick advancement
    - **Property 7: Pick Advancement**
    - **Validates: Requirements 4.2, 4.3, 7.5**

  - [x] 4.6 Write unit tests for DraftManager edge cases
    - Test boundary conditions (first pick, last pick, round transitions)
    - _Requirements: 4.2, 4.3_

- [x] 5. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 6. Implement PlayerManager
  - [x] 6.1 Create PlayerManager class with player management methods
    - Implement getBestAvailable(), draftPlayer(), getAvailablePlayers(), resetPlayers()
    - _Requirements: 5.1, 5.2, 5.5, 7.2, 7.4, 9.2_

  - [x] 6.2 Write property test for best available player calculation
    - **Property 8: Best Available Player Calculation**
    - **Validates: Requirements 5.1, 5.2, 5.3, 5.5**

  - [x] 6.3 Write property test for tiebreaker consistency
    - **Property 9: Tiebreaker Consistency**
    - **Validates: Requirements 5.4**

  - [x] 6.4 Write property test for player draft state update
    - **Property 11: Player Draft State Update**
    - **Validates: Requirements 7.2, 7.3**

  - [x] 6.5 Write property test for drafted player rejection
    - **Property 12: Drafted Player Rejection**
    - **Validates: Requirements 7.4**

  - [x] 6.6 Write unit tests for PlayerManager edge cases
    - Test empty player pool, all players drafted, null handling
    - _Requirements: 5.1, 7.4_

- [x] 7. Implement SQLite database schema and helper
  - [x] 7.1 Create DatabaseHelper class extending SQLiteOpenHelper
    - Define tables for teams, players, picks, and draft state
    - Implement onCreate() and onUpgrade() methods
    - Add proper indexes on frequently queried columns
    - _Requirements: 6.1, 6.2, 8.1_

  - [x] 7.2 Create DAO (Data Access Object) classes
    - Implement TeamDAO, PlayerDAO, PickDAO for CRUD operations
    - _Requirements: 6.1, 6.2_

  - [x] 7.3 Write unit tests for database operations
    - Test insert, update, delete, and query operations
    - _Requirements: 6.1, 6.2_

- [x] 8. Implement PersistenceManager
  - [x] 8.1 Create PersistenceManager class
    - Implement saveDraft(), loadDraft(), clearDraft()
    - Use SQLite for structured data and SharedPreferences for configuration
    - _Requirements: 6.1, 6.2, 6.3_

  - [x] 8.2 Write property test for persistence round trip
    - **Property 10: Persistence Round Trip**
    - **Validates: Requirements 6.1, 6.2, 6.3, 6.4**

  - [x] 8.3 Write unit tests for persistence error handling
    - Test corrupted data, missing data, storage errors
    - _Requirements: 6.3_

- [x] 9. Implement draft history tracking
  - [x] 9.1 Add draft history methods to DraftManager
    - Implement addPickToHistory(), getPickHistory(), clearHistory()
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

  - [x] 9.2 Write property test for draft history completeness
    - **Property 13: Draft History Completeness**
    - **Validates: Requirements 8.1, 8.2, 8.3, 8.4**

  - [x] 9.3 Write unit tests for history edge cases
    - Test empty history, large history, duplicate prevention
    - _Requirements: 8.1, 8.2_

- [x] 10. Checkpoint - Ensure all business logic tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 11. Implement draft reset functionality
  - [x] 11.1 Add reset logic to coordinate all managers
    - Implement full reset that clears picks and player states
    - Preserve team configuration and draft settings
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

  - [x] 11.2 Write property test for draft reset clears picks
    - **Property 14: Draft Reset Clears Picks**
    - **Validates: Requirements 9.1, 9.2, 9.3**

  - [x] 11.3 Write property test for draft reset preserves configuration
    - **Property 15: Draft Reset Preserves Configuration**
    - **Validates: Requirements 9.4**

  - [x] 11.4 Write unit tests for reset edge cases
    - Test reset with no picks, reset mid-draft, multiple resets
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [x] 12. Create player data resource file
  - [x] 12.1 Create JSON file with sample player data
    - Add res/raw/players.json with top 300 fantasy football players
    - Include id, name, position, and rank for each player
    - _Requirements: 5.1_

  - [x] 12.2 Create PlayerDataLoader utility class
    - Implement method to load and parse player JSON from resources
    - _Requirements: 5.1_

- [x] 13. Implement MainActivity (Draft Screen)
  - [x] 13.1 Create MainActivity layout XML
    - Design layout with draft configuration section, current pick display, draft history RecyclerView
    - Add buttons for "Make Pick" and "Reset Draft"
    - _Requirements: 1.4, 2.4, 4.1, 4.4, 8.1, 9.5_

  - [x] 13.2 Implement MainActivity Java class
    - Initialize managers and load draft state
    - Wire up UI components to display current draft state
    - Implement button click handlers
    - _Requirements: 1.4, 2.4, 4.1, 4.4, 8.1_

  - [x] 13.3 Create RecyclerView adapter for draft history
    - Display pick number, team name, player name, and position
    - _Requirements: 8.1, 8.3_

- [x] 14. Implement PlayerFragment (Best Available Display)
  - [x] 14.1 Create PlayerFragment layout XML
    - Design card-style layout for best available player
    - Display player name, position, and rank
    - Add "View All Players" button
    - _Requirements: 5.1, 5.2_

  - [x] 14.2 Implement PlayerFragment Java class
    - Load and display best available player
    - Update display when draft state changes
    - _Requirements: 5.1, 5.2, 5.3_

- [x] 15. Implement ConfigActivity (Team Configuration)
  - [x] 15.1 Create ConfigActivity layout XML
    - Add number picker for team count
    - Add spinner for draft flow selection
    - Add RecyclerView for team list with editable names and positions
    - Add "Save Configuration" button
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 3.1, 3.2, 3.5_

  - [x] 15.2 Implement ConfigActivity Java class
    - Handle team count changes
    - Handle draft flow selection
    - Implement team name editing with validation
    - Implement draft position assignment
    - Save configuration and return to MainActivity
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 3.1, 3.2, 3.5_

  - [x] 15.3 Create RecyclerView adapter for team configuration
    - Display editable team names and draft positions
    - Validate uniqueness and completeness
    - _Requirements: 1.2, 1.3, 2.1, 2.2_

- [x] 16. Implement player selection dialog
  - [x] 16.1 Create PlayerSelectionDialog layout XML
    - Add SearchView for filtering players
    - Add RecyclerView for player list
    - Display player name, position, rank, and availability
    - _Requirements: 7.1_

  - [x] 16.2 Implement PlayerSelectionDialog Java class
    - Load available players
    - Implement search/filter functionality
    - Handle player selection and return to MainActivity
    - Validate player is not already drafted
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

  - [x] 16.3 Create RecyclerView adapter for player list
    - Display player information
    - Disable already-drafted players
    - _Requirements: 7.1, 7.4_

- [x] 17. Implement draft reset confirmation dialog
  - [x] 17.1 Create confirmation dialog for draft reset
    - Display warning message about clearing all picks
    - Add "Confirm" and "Cancel" buttons
    - _Requirements: 9.5_

  - [x] 17.2 Wire reset dialog to MainActivity
    - Show dialog when reset button is clicked
    - Execute reset on confirmation
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 18. Implement error handling and validation UI
  - [x] 18.1 Add Toast messages for validation errors
    - Display errors for invalid team count, duplicate names, incomplete draft order
    - Display errors for invalid player selection
    - _Requirements: 1.1, 1.3, 2.1, 7.4_

  - [x] 18.2 Add error handling for persistence failures
    - Display user-friendly messages for storage errors
    - Offer to continue without persistence or reset
    - _Requirements: 6.1, 6.2, 6.3_

- [x] 19. Checkpoint - Ensure all tests pass and app runs
  - Ensure all tests pass, ask the user if questions arise.

- [x] 20. Write integration tests
  - [x] 20.1 Write end-to-end draft flow test
    - Test complete draft from configuration through multiple rounds
    - _Requirements: All_

  - [x] 20.2 Write persistence integration test
    - Test save/load across simulated app restart
    - _Requirements: 6.1, 6.2, 6.3_

  - [x] 20.3 Write UI integration tests with Espresso
    - Test MainActivity interactions
    - Test ConfigActivity interactions
    - Test PlayerSelectionDialog interactions
    - _Requirements: All UI requirements_

- [x] 21. Final polish and testing
  - [x] 21.1 Add app icon and branding
    - Create launcher icon
    - Set app name and theme colors

  - [x] 21.2 Test on multiple screen sizes
    - Verify layout on phone and tablet
    - Adjust layouts if needed for different screen sizes

  - [x] 21.3 Final manual testing
    - Test complete draft scenarios
    - Test error conditions
    - Test persistence across app restarts

## Notes

- All tasks are required for comprehensive implementation
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document
- Unit tests validate specific examples and edge cases
- The implementation follows a bottom-up approach: models → business logic → persistence → UI
