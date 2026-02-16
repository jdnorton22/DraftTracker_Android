# Hide Drafted Players Toggle - Feature Specification

## Overview

I've created a complete specification for adding a toggle to hide drafted players in the "View All Players" dialog. This feature will make it easier to focus on available players during your draft.

## What's Being Added

### Visual Changes
- **CheckBox**: "Hide Drafted Players" toggle control
- **Player Count**: Display showing "Showing X of Y players"
- **Location**: Between the search bar and player list

### Functionality
1. **Toggle ON**: Hides all drafted players, showing only available players
2. **Toggle OFF** (default): Shows all players including drafted ones
3. **Works with Search**: Toggle and search filters work together
4. **State Persistence**: Toggle state remembered while dialog is open

## User Experience

### Before (Current)
```
┌─────────────────────────────┐
│ Select Player               │
│ [Search box...............]  │
│                             │
│ Player List:                │
│ ✓ Christian McCaffrey       │ ← Drafted (grayed out)
│   Tyreek Hill               │ ← Available
│ ✓ Justin Jefferson          │ ← Drafted (grayed out)
│   CeeDee Lamb               │ ← Available
│ ...                         │
│ [Cancel]                    │
└─────────────────────────────┘
```

### After (With Toggle)
```
┌─────────────────────────────┐
│ Select Player               │
│ [Search box...............]  │
│ ☑ Hide Drafted Players      │ ← NEW TOGGLE
│ Showing 2 of 4 players      │ ← NEW COUNT
│                             │
│ Player List:                │
│   Tyreek Hill               │ ← Available only
│   CeeDee Lamb               │ ← Available only
│ ...                         │
│ [Cancel]                    │
└─────────────────────────────┘
```

## Specification Files Created

### 1. Requirements Document
**Location**: `.kiro/specs/hide-drafted-players-toggle/requirements.md`

**Key Requirements**:
- Requirement 1: Toggle Control Display
- Requirement 2: Hide Drafted Players
- Requirement 3: Toggle Interaction with Search
- Requirement 4: Visual Feedback
- Requirement 5: Player Count Display
- Requirement 6: Toggle State Persistence
- Requirement 7: Performance (<100ms response time)

### 2. Design Document
**Location**: `.kiro/specs/hide-drafted-players-toggle/design.md`

**Key Design Elements**:
- Component structure and data flow
- Filter logic implementation
- UI layout changes
- Performance optimizations
- Accessibility considerations

### 3. Tasks Document
**Location**: `.kiro/specs/hide-drafted-players-toggle/tasks.md`

**Implementation Tasks**:
1. Update dialog layout XML
2. Modify PlayerSelectionAdapter (3 subtasks)
3. Modify PlayerSelectionDialog (7 subtasks)
4. Add string resources
5. Test the implementation
6. Documentation

## Technical Details

### Files to Modify
1. `app/src/main/res/layout/dialog_player_selection.xml` - Add toggle and count UI
2. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java` - Add filter logic
3. `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionDialog.java` - Wire up toggle
4. `app/src/main/res/values/strings.xml` - Add new strings

### No Database Changes
- Uses existing `Player.isDrafted()` property
- No schema changes required
- No migration needed

### Performance
- Filter operation: O(n) where n = number of players
- Expected time: <10ms for 300 players
- Target: <100ms response time (easily achievable)

## Next Steps

### Option 1: Review the Spec
Review the specification files to ensure they meet your needs:
- `.kiro/specs/hide-drafted-players-toggle/requirements.md`
- `.kiro/specs/hide-drafted-players-toggle/design.md`
- `.kiro/specs/hide-drafted-players-toggle/tasks.md`

### Option 2: Start Implementation
If the spec looks good, I can begin implementing the feature by executing the tasks in the tasks.md file.

### Option 3: Modify the Spec
If you'd like any changes to the requirements or design, let me know and I'll update the specification.

## Benefits

1. **Improved Focus**: Easily see only available players during draft
2. **Faster Drafting**: No need to mentally filter out drafted players
3. **Better UX**: Clean, simple toggle that's easy to understand
4. **Flexible**: Works with search to find specific available players
5. **Fast**: Instant response when toggling (<100ms)

## Example Use Cases

### Use Case 1: Mid-Draft Focus
- You're in round 5 of your draft
- Many players are already drafted
- Toggle ON to see only available players
- Search for "RB" to find available running backs

### Use Case 2: Reviewing All Players
- You want to see who's been drafted
- Toggle OFF to see everyone
- Drafted players are grayed out for easy identification

### Use Case 3: Quick Available Player Check
- Toggle ON to instantly see how many players are still available
- Player count shows "Showing 187 of 300 players"
- Quickly assess remaining options

## Questions?

Let me know if you'd like to:
1. Review or modify the specification
2. Start implementing the feature
3. Add additional functionality (e.g., position filters, sort options)
