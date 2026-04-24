# Design Document: Hide Drafted Players Toggle

## Overview

This design implements a toggle control in the Player Selection Dialog that allows users to filter out drafted players from the displayed list. The feature enhances usability by letting users focus on available players during the draft process.

## Architecture

### Component Structure

```
PlayerSelectionDialog
├── SearchView (existing)
├── CheckBox (new - "Hide Drafted Players")
├── TextView (new - player count display)
├── RecyclerView (existing)
└── Button (existing - Cancel)
```

### Data Flow

1. User toggles the "Hide Drafted Players" checkbox
2. Dialog updates the adapter's filter state
3. Adapter re-filters the player list based on:
   - Current search query (if any)
   - Drafted status (if toggle is ON)
4. RecyclerView updates to show filtered results
5. Player count display updates

## Components and Interfaces

### 1. PlayerSelectionDialog (Modified)

**New Fields:**
```java
private CheckBox hideD raftedCheckBox;
private TextView playerCountText;
private boolean hideDrafted = false; // Toggle state
```

**New Methods:**
```java
private void setupHideDraftedToggle()
private void updatePlayerCount()
private void applyFilters()
```

**Modified Methods:**
```java
private void initializeViews() // Add new UI components
private void setupSearchView() // Integrate with toggle filter
```

### 2. PlayerSelectionAdapter (Modified)

**New Fields:**
```java
private boolean hideDrafted = false;
```

**New Methods:**
```java
public void setHideDrafted(boolean hideDrafted)
private boolean shouldShowPlayer(Player player, String query)
```

**Modified Methods:**
```java
public void filter(String query) // Apply both search and drafted filters
```

### 3. Layout Changes

**dialog_player_selection.xml:**
- Add CheckBox for "Hide Drafted Players" toggle
- Add TextView for player count display
- Adjust spacing and margins

## Data Models

No changes to existing data models. The feature uses existing Player properties:
- `Player.isDrafted()` - boolean indicating if player is drafted
- `Player.getName()` - for search filtering
- `Player.getPosition()` - for search filtering

## Implementation Details

### Filter Logic

```java
private boolean shouldShowPlayer(Player player, String query) {
    // Check drafted status filter
    if (hideDrafted && player.isDrafted()) {
        return false;
    }
    
    // Check search query filter
    if (query != null && !query.trim().isEmpty()) {
        String lowerQuery = query.toLowerCase().trim();
        return player.getName().toLowerCase().contains(lowerQuery) ||
               player.getPosition().toLowerCase().contains(lowerQuery);
    }
    
    return true;
}
```

### Toggle State Management

The toggle state will be stored as an instance variable in `PlayerSelectionDialog`. When the dialog is recreated during the same app session, the state can be passed via constructor or saved in a static field for session persistence.

### Player Count Display

Format: "Showing X of Y players"
- X = number of visible players after filtering
- Y = total number of players in the list

## UI Design

### Layout Structure

```xml
<LinearLayout orientation="vertical">
    <TextView id="dialog_title" />
    <SearchView id="player_search_view" />
    
    <!-- NEW: Filter controls container -->
    <LinearLayout orientation="horizontal">
        <CheckBox 
            id="checkbox_hide_drafted"
            text="Hide Drafted Players" />
        <TextView 
            id="text_player_count"
            text="Showing X of Y players" />
    </LinearLayout>
    
    <RecyclerView id="player_list_recycler_view" />
    <Button id="cancel_button" />
</LinearLayout>
```

### Visual Design

- **CheckBox**: Standard Android Material Design checkbox
- **Player Count**: Small gray text, right-aligned
- **Spacing**: 8dp margins between components
- **Colors**: Use theme colors for consistency

## Error Handling

### Edge Cases

1. **All players drafted**: Display message "No available players" when toggle is ON and all players are drafted
2. **No search results**: Display "No players found" when search + toggle filters return empty list
3. **Rapid toggle clicks**: Debounce not needed as filtering is fast (<100ms)

### Error States

No specific error states - filtering is a pure UI operation with no failure modes.

## Testing Strategy

### Unit Tests

1. **Filter Logic Tests**:
   - Test `shouldShowPlayer()` with various combinations of drafted status and search queries
   - Verify correct filtering when toggle is ON/OFF
   - Verify search works with toggle ON/OFF

2. **Player Count Tests**:
   - Verify count updates correctly when toggle changes
   - Verify count updates correctly when search changes
   - Verify count displays correct format

### Integration Tests

1. **Dialog Interaction Tests**:
   - Open dialog, toggle checkbox, verify filtered list
   - Search with toggle ON, verify combined filtering
   - Toggle multiple times, verify state consistency

2. **State Persistence Tests**:
   - Set toggle ON, close dialog, reopen, verify state persisted
   - Restart app, verify toggle defaults to OFF

### Manual Testing

1. Open "View All Players" dialog
2. Verify toggle is visible and defaults to OFF
3. Toggle ON - verify drafted players disappear
4. Toggle OFF - verify all players reappear
5. Toggle ON + search - verify combined filtering works
6. Check player count updates correctly
7. Close and reopen dialog - verify toggle state persists

## Performance Considerations

### Optimization

- **Filtering**: O(n) operation where n = number of players (max 300)
- **Expected time**: <10ms for 300 players on modern devices
- **Memory**: No additional memory overhead (reuses existing lists)

### Benchmarks

- Target: Filter operation completes in <100ms
- Typical: ~5-10ms for 300 players
- UI update: Immediate (RecyclerView.notifyDataSetChanged())

## Accessibility

- CheckBox has proper content description: "Hide drafted players toggle"
- Player count text is readable by screen readers
- Toggle state changes are announced by accessibility services

## Future Enhancements

1. **Additional Filters**: Position filter (show only RB, WR, etc.)
2. **Sort Options**: Sort by rank, position, name
3. **Filter Presets**: Save custom filter combinations
4. **Visual Indicators**: Show filter chips/tags when active
