# Implementation Plan: Bottom Navigation Tabs

## Overview

This implementation transforms the Fantasy Draft Picker app from a multi-activity architecture to a single-activity architecture with fragments and bottom navigation. The core implementation is complete, with all fragments, layouts, and navigation working. Remaining work focuses on testing and validation.

## Implementation Status

**Completed:**
- ✅ Bottom navigation UI with Draft and Config tabs
- ✅ Fragment layouts (fragment_draft.xml, fragment_config.xml)
- ✅ DraftFragment with all UI logic and action handlers
- ✅ ConfigFragment with configuration management and validation
- ✅ MainActivity refactored to host fragments and manage shared state
- ✅ State sharing and persistence between fragments
- ✅ AndroidManifest updated (ConfigActivity removed)

**Remaining:**
- Testing tasks (property tests, unit tests, integration tests)
- Final validation and checkpoint

## Tasks

- [x] 1. Add dependencies and create bottom navigation menu
  - Add AndroidX Navigation Component dependencies to build.gradle
  - Create menu resource file (menu_bottom_navigation.xml) with Draft and Config items
  - Add Material Design icons for tabs (ic_draft, ic_settings)
  - _Requirements: 1.1, 1.4_

- [x] 2. Create fragment layout files
  - [x] 2.1 Create fragment_draft.xml layout
    - Extract content from activity_main.xml (everything except ActionBar)
    - Wrap content in ScrollView to handle overflow
    - Remove "Edit Config" button from layout
    - Ensure layout accounts for bottom navigation height (add bottom padding)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 7.1, 7.3, 9.1_
  
  - [x] 2.2 Create fragment_config.xml layout
    - Extract content from activity_config.xml
    - Wrap content in ScrollView to handle overflow
    - Ensure layout accounts for bottom navigation height (add bottom padding)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 9.2_

- [x] 3. Create DraftFragment class
  - [x] 3.1 Create DraftFragment.java with basic structure
    - Extend Fragment
    - Implement onCreateView to inflate fragment_draft.xml
    - Add method to get MainActivity reference safely
    - Initialize all UI component references (TextViews, Buttons, CardView, etc.)
    - _Requirements: 2.1, 5.1_
  
  - [x] 3.2 Migrate UI update logic from MainActivity
    - Copy updateUI(), updateDraftConfiguration(), updateCurrentPick(), updateBestAvailable(), updateRecentPicks() methods
    - Modify methods to get data from MainActivity's managers
    - Add updateDraftButtons() and updatePickSlot() helper methods
    - _Requirements: 2.2, 2.3, 2.4, 2.7_
  
  - [x] 3.3 Migrate draft action handlers
    - Copy showPlayerSelectionDialog(), handlePlayerSelection(), draftBestAvailablePlayer() methods
    - Copy showCustomPlayerDialog() method
    - Modify to call MainActivity.saveDraftState() after state changes
    - Update to use getMainActivity() for accessing managers
    - _Requirements: 2.7_
  
  - [x] 3.4 Migrate other action handlers
    - Copy resetDraft(), showResetConfirmationDialog(), showDraftCompletionDialog() methods
    - Copy launchDraftHistoryActivity(), exportDraftToCSV(), performCsvExport(), openCsvFile() methods
    - Copy toggleConfigSection() method for collapsible header
    - Update all methods to use getMainActivity() for accessing managers and state
    - _Requirements: 2.5, 2.6, 2.8_
  
  - [x] 3.5 Implement fragment lifecycle methods
    - Override onResume() to refresh UI with current data
    - Override onPause() to notify MainActivity to save state
    - Add null checks for MainActivity reference in all methods
    - _Requirements: 11.1, 11.2_
  
  - [ ]* 3.6 Write property test for draft screen UI updates
    - **Property 7: UI updates after pick**
    - **Validates: Requirements 2.7**
  
  - [ ]* 3.7 Write property test for recent picks display
    - **Property 6: Recent picks are displayed correctly**
    - **Validates: Requirements 2.4**

- [x] 4. Create ConfigFragment class
  - [x] 4.1 Create ConfigFragment.java with basic structure
    - Extend Fragment
    - Implement onCreateView to inflate fragment_config.xml
    - Add method to get MainActivity reference safely
    - Initialize all UI component references (EditText, NumberPickers, Spinner, RecyclerView, Button)
    - _Requirements: 3.1, 5.2_
  
  - [x] 4.2 Migrate configuration setup logic
    - Copy setupNumberPicker(), setupRoundsPicker(), setupSpinner(), setupRecyclerView() methods
    - Copy handleTeamCountChange() method
    - Modify to get data from MainActivity's state
    - _Requirements: 3.2, 3.3, 3.4_
  
  - [x] 4.3 Migrate validation and save logic
    - Copy saveConfiguration(), validateTeamNames(), validateDraftOrder() methods
    - Modify to update MainActivity's state and call saveDraftState()
    - Add updateControlStates() method to enable/disable controls based on draft progress
    - _Requirements: 3.5, 3.6, 3.7, 6.1, 6.2, 6.3, 6.4, 6.5_
  
  - [x] 4.4 Implement fragment lifecycle methods
    - Override onResume() to load current configuration and update control states
    - Override onPause() to preserve unsaved UI changes
    - Add null checks for MainActivity reference in all methods
    - _Requirements: 3.8, 11.1, 11.2_
  
  - [ ]* 4.5 Write property test for team name validation
    - **Property 9: Team name validation rejects invalid names**
    - **Validates: Requirements 3.5**
  
  - [ ]* 4.6 Write property test for draft order validation
    - **Property 10: Draft order validation rejects incomplete sequences**
    - **Validates: Requirements 3.6**
  
  - [ ]* 4.7 Write property test for config control states
    - **Property 15: Structural config controls disabled during active draft**
    - **Validates: Requirements 6.1, 6.2, 6.3, 6.5**

- [x] 5. Refactor MainActivity to host fragments
  - [x] 5.1 Update MainActivity layout (activity_main.xml)
    - Replace existing content with BottomNavigationView and FragmentContainerView
    - Set BottomNavigationView height to 56dp
    - Position BottomNavigationView at bottom with app:layout_constraintBottom_toBottomOf="parent"
    - Position FragmentContainerView above BottomNavigationView
    - Set menu resource to menu_bottom_navigation
    - _Requirements: 1.1, 1.2, 1.5, 9.3, 9.4_
  
  - [x] 5.2 Refactor MainActivity.java to remove UI logic
    - Remove all UI component references (TextViews, Buttons, etc.) - now in fragments
    - Remove all UI update methods - now in fragments
    - Remove all action handler methods - now in fragments
    - Keep only manager initialization and state management
    - _Requirements: 5.5_
  
  - [x] 5.3 Add fragment management to MainActivity
    - Add BottomNavigationView and FragmentContainerView references
    - Implement setupBottomNavigation() to handle tab selection
    - Implement showFragment() to perform fragment transactions
    - Add getter methods for all managers (getDraftManager, getPlayerManager, etc.)
    - Add getter/setter methods for state (getCurrentState, setCurrentState, etc.)
    - _Requirements: 4.1, 4.2, 4.5, 5.1, 5.2, 5.5_
  
  - [x] 5.4 Update MainActivity lifecycle methods
    - Keep onCreate() for manager initialization and fragment setup
    - Keep onPause() for persistence
    - Update onActivityResult() to handle results from DraftHistoryActivity
    - Override onBackPressed() to exit app (call finish())
    - _Requirements: 5.6, 8.2, 10.1, 10.2, 10.3_
  
  - [ ]* 5.5 Write property test for tab selection
    - **Property 1: Tab selection displays correct fragment**
    - **Validates: Requirements 2.1, 3.1, 4.1, 4.2**
  
  - [ ]* 5.6 Write property test for fragment state preservation
    - **Property 13: Fragment state is preserved during tab switches**
    - **Validates: Requirements 4.3**

- [-] 6. Checkpoint - Ensure basic navigation works
  - Verify app launches with Draft tab selected
  - Test switching between Draft and Config tabs
  - Verify fragments display correctly
  - Test making a pick and switching tabs
  - Test configuration changes and switching tabs
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Implement state sharing between fragments
  - [x] 7.1 Add state update notifications
    - In DraftFragment, call mainActivity.saveDraftState() after picks
    - In ConfigFragment, call mainActivity.saveDraftState() after config changes
    - Ensure fragments refresh UI in onResume() to reflect changes from other fragment
    - _Requirements: 5.3, 5.4_
  
  - [ ]* 7.2 Write property test for data consistency
    - **Property 14: Data consistency across fragments**
    - **Validates: Requirements 5.3, 5.4**
  
  - [ ]* 7.3 Write property test for unsaved UI preservation
    - **Property 12: Unsaved UI changes are preserved during tab switches**
    - **Validates: Requirements 3.8**

- [x] 8. Implement persistence integration
  - [x] 8.1 Update MainActivity persistence methods
    - Ensure saveDraftState() saves complete snapshot (teams, players, state, config, history)
    - Ensure loadDraftState() restores complete snapshot
    - Add error handling for persistence failures
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [ ]* 8.2 Write property test for persistence on lifecycle events
    - **Property 17: State persisted on lifecycle events**
    - **Validates: Requirements 5.6, 8.1, 8.2**
  
  - [ ]* 8.3 Write property test for persistence load
    - **Property 18: State loaded on app resume**
    - **Validates: Requirements 8.3**
  
  - [ ]* 8.4 Write property test for complete snapshot
    - **Property 20: Complete snapshot persisted**
    - **Validates: Requirements 8.5**

- [x] 9. Update AndroidManifest.xml
  - [x] 9.1 Remove ConfigActivity declaration
    - Remove <activity> entry for ConfigActivity
    - Keep MainActivity as launcher activity
    - _Requirements: 4.5_

- [ ] 10. Add unit tests for UI elements
  - [ ]* 10.1 Write unit test for bottom navigation structure
    - Test that BottomNavigationView has exactly 2 menu items
    - Test that tabs have correct labels ("Draft" and "Config")
    - Test that tabs have correct icons
    - _Requirements: 1.1, 1.4_
  
  - [ ]* 10.2 Write unit test for default tab
    - Test that Draft tab is selected on app launch
    - Test that DraftFragment is displayed on launch
    - _Requirements: 4.5_
  
  - [ ]* 10.3 Write unit test for Edit Config button removal
    - Test that DraftFragment layout does not contain Edit Config button
    - Test that other action buttons are present
    - _Requirements: 7.1, 7.3_
  
  - [ ]* 10.4 Write unit test for back button behavior
    - Test that back button calls finish() on MainActivity
    - Test that back button does not change selected tab
    - Test that state is saved before exit
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [ ]* 10.5 Write unit test for layout dimensions
    - Test that BottomNavigationView height is 56dp
    - Test that content area does not overlap navigation
    - _Requirements: 9.4, 9.3_

- [ ] 11. Add integration tests
  - [ ]* 11.1 Write integration test for fragment-activity communication
    - Test that fragments can access managers from MainActivity
    - Test that manager instances are shared (same objects)
    - _Requirements: 5.1, 5.2, 5.5_
  
  - [ ]* 11.2 Write integration test for navigation flow
    - Test tab switching sequence (Draft → Config → Draft)
    - Test that correct fragments are displayed after each switch
    - _Requirements: 4.1, 4.2_
  
  - [ ]* 11.3 Write integration test for persistence error handling
    - Test that persistence failures show error message
    - Test that app continues operating after persistence failure
    - _Requirements: 8.4_

- [~] 12. Final checkpoint - Ensure all tests pass
  - Run all unit tests and verify they pass
  - Run all property tests and verify they pass
  - Run all integration tests and verify they pass
  - Perform manual testing of all user workflows
  - Verify no regressions in existing functionality
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties
- Unit tests validate specific examples and edge cases
- The core implementation is complete and functional
- All testing tasks are optional but recommended for production quality
- ConfigActivity.java can be safely deleted as it's no longer used
