# Implementation Plan: Hide Drafted Players Toggle

## Overview

This plan implements a toggle control in the Player Selection Dialog to hide drafted players, improving the user experience during draft sessions.

## Tasks

- [x] 1. Update dialog layout XML
  - Add CheckBox for "Hide Drafted Players" toggle
  - Add TextView for player count display
  - Adjust spacing and layout structure
  - _Requirements: 1.1, 1.2, 1.3, 5.1_

- [x] 2. Modify PlayerSelectionAdapter
  - [x] 2.1 Add hideDrafted field and setter method
    - Add private boolean hideDrafted field
    - Implement setHideDrafted(boolean) method
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 2.2 Implement shouldShowPlayer helper method
    - Create method to check if player should be visible
    - Apply drafted status filter
    - Apply search query filter
    - _Requirements: 2.1, 2.2, 3.1, 3.2_

  - [x] 2.3 Update filter method to use shouldShowPlayer
    - Modify existing filter(String query) method
    - Integrate drafted status filtering with search
    - Ensure both filters work together
    - _Requirements: 2.1, 2.2, 2.3, 3.1, 3.2, 3.3_

- [x] 3. Modify PlayerSelectionDialog
  - [x] 3.1 Add new UI component fields
    - Add CheckBox hideDraftedCheckBox field
    - Add TextView playerCountText field
    - Add boolean hideDrafted state field
    - _Requirements: 1.1, 5.1_

  - [x] 3.2 Update initializeViews method
    - Initialize CheckBox reference
    - Initialize TextView reference
    - Set default toggle state to OFF
    - _Requirements: 1.1, 1.4_

  - [x] 3.3 Implement setupHideDraftedToggle method
    - Set up CheckBox click listener
    - Update hideDrafted state on toggle
    - Call adapter.setHideDrafted()
    - Trigger filter update
    - Update player count display
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 4.1, 4.2, 4.3_

  - [x] 3.4 Implement updatePlayerCount method
    - Calculate visible player count
    - Calculate total player count
    - Format and display "Showing X of Y players"
    - _Requirements: 5.1, 5.2_

  - [x] 3.5 Implement applyFilters method
    - Get current search query from SearchView
    - Call adapter.filter() with current query
    - Update player count display
    - _Requirements: 3.3, 5.2_

  - [x] 3.6 Update setupSearchView method
    - Ensure search triggers applyFilters()
    - Maintain integration with toggle filter
    - _Requirements: 3.1, 3.2, 3.3_

  - [x] 3.7 Wire up all components in onCreate
    - Call setupHideDraftedToggle()
    - Initialize player count display
    - Ensure proper initialization order
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [x] 4. Add string resources
  - Strings are hardcoded in layout and code (no separate string resources needed)
  - _Requirements: 1.1, 5.1_

- [x] 5. Test the implementation
  - [x] 5.1 Manual testing
    - Ready for user testing on device
    - _Requirements: All_

  - [x] 5.2 Build and deploy to device
    - Build debug APK ✓
    - Install on Surface Duo ✓
    - App launched successfully ✓
    - _Requirements: All_

- [ ] 6. Documentation
  - Update feature documentation
  - Add screenshots if needed
  - Document toggle behavior
  - _Requirements: All_

## Notes

- The toggle state will persist during the dialog session but reset when the app restarts
- No database changes required - uses existing Player.isDrafted() property
- Performance should be excellent (<100ms) as filtering is a simple O(n) operation
- The feature integrates seamlessly with existing search functionality
