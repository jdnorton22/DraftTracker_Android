# Favorite Player Highlight Bugfix Design

## Overview

The Fantasy Draft Picker app lacks any mechanism for users to mark players as "favorites" or visually distinguish them during a draft. The `Player` model has no `favorite` field, the JSON parser ignores any `"favorite"` data in `players.json`, there is no Favorites management screen, and all adapters render every player card with the same default background regardless of favorite status. This fix adds a `favorite` boolean to the model and parser, introduces a Favorites screen via the navigation drawer, and applies a yellow background highlight to favorite player cards across all four display contexts (draft screen, player selection dialog, draft history, team roster popup).

## Glossary

- **Bug_Condition (C)**: The condition that triggers the defect — a player has `"favorite": true` in `players.json` or is marked as favorite at runtime, but the system ignores the field and renders no visual distinction.
- **Property (P)**: The desired behavior — favorite players are parsed correctly, stored on the model, and rendered with a yellow background highlight on every screen.
- **Preservation**: Existing rendering of non-favorite players, JSON parsing of all other fields, navigation drawer behavior, draft operations, filtering/sorting, and Parcelable serialization must remain unchanged.
- **Player**: The model class in `Player.java` implementing `Parcelable`, representing a draftable player with fields like name, position, rank, etc.
- **PlayerDataParser**: The utility in `PlayerDataParser.java` that reads `players.json` and constructs `Player` objects.
- **PlayerManager**: The manager in `PlayerManager.java` that holds the player pool and provides query methods (best available, by position, by ID).
- **Adapter**: Any of `PlayerSelectionAdapter`, `DraftHistoryAdapter`, `TeamRosterAdapter`, or the inline binding in `DraftFragment` that renders player data into list items or card views.
- **FavoritesFragment**: A new Fragment accessible from the navigation drawer that lists all favorite players and allows toggling favorite status.

## Bug Details

### Bug Condition

The bug manifests when a player entry in `players.json` contains `"favorite": true` or when a user expects to mark/view favorite players. The `PlayerDataParser.parsePlayerObject()` method does not read the `"favorite"` field, the `Player` class has no `favorite` property, no Favorites screen exists in the navigation drawer, and all four adapters render every player with the default background.

**Formal Specification:**
```
FUNCTION isBugCondition(input)
  INPUT: input of type { player: Player, context: ScreenContext }
  OUTPUT: boolean

  RETURN (input.player.jsonData HAS "favorite" FIELD WITH VALUE true
          OR input.player IS marked as favorite at runtime)
         AND input.context IN [DraftScreen, PlayerSelectionDialog, DraftHistory, TeamRosterPopup, FavoritesScreen]
         AND NOT playerRenderedWithYellowBackground(input.player, input.context)
END FUNCTION
```

### Examples

- A player entry `{"id": "1", "name": "Patrick Mahomes", "position": "QB", "rank": 1, "favorite": true}` is parsed, but `Player.isFavorite()` does not exist, so the favorite status is lost. **Expected**: `player.isFavorite()` returns `true`. **Actual**: No such method; field is ignored.
- Patrick Mahomes (favorite) appears in the Player Selection Dialog. **Expected**: Row has yellow background (`#FFFFF9C4` or similar). **Actual**: Row has default `selectableItemBackground`.
- User opens the navigation drawer. **Expected**: "Favorites" menu item is present between "Draft" and "Config". **Actual**: Only "Draft" and "Config" items exist.
- A non-favorite player (e.g., `"favorite": false` or field absent) appears in any list. **Expected**: Default background, no yellow highlight. **Actual**: Default background (correct, must be preserved).

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- Non-favorite players (`favorite == false` or field absent) must continue to render with the default background on all screens.
- All existing `Player` fields (id, name, position, rank, isDrafted, draftedBy, lastYearStats, pffRank, positionRank, nflTeam, injuryStatus, espnId, byeWeek) must continue to parse correctly from JSON.
- Navigation to Draft and Config screens via the side drawer must continue to work as before.
- Drafting, undoing, and resetting picks must function identically regardless of a player's favorite status.
- Filtering, searching, and sorting in the Player Selection Dialog must continue to work correctly.
- Parceling and unparceling `Player` objects must preserve all existing fields.

**Scope:**
All inputs that do NOT involve a player with `favorite == true` should be completely unaffected by this fix. This includes:
- All rendering of non-favorite players across all screens
- All draft operations (pick, undo, reset)
- All navigation flows except the new Favorites menu item
- All JSON parsing of fields other than `"favorite"`

## Hypothesized Root Cause

Based on the bug description, the root causes are straightforward missing functionality:

1. **Missing Model Field**: `Player.java` has no `favorite` boolean field, no getter/setter, no inclusion in constructors, `equals()`, `hashCode()`, or `Parcelable` read/write methods.

2. **Missing JSON Parsing**: `PlayerDataParser.parsePlayerObject()` does not call `playerJson.optBoolean("favorite", false)` and does not set the field on the `Player` object.

3. **Missing Favorites Screen**: No `FavoritesFragment` exists, no menu item in `menu_drawer_navigation.xml`, and no navigation handling in `MainActivity.setupNavigationDrawer()`.

4. **Missing Highlight Logic in Adapters**: None of the four adapters (`PlayerSelectionAdapter`, `DraftHistoryAdapter`, `TeamRosterAdapter`, and `DraftFragment` inline binding) check `player.isFavorite()` or apply a yellow background color when it is `true`.

## Correctness Properties

Property 1: Bug Condition - Favorite Field Parsing and Storage

_For any_ JSON player entry containing `"favorite": true`, the parsed `Player` object SHALL have `isFavorite()` return `true`, and for any entry with `"favorite": false` or the field absent, `isFavorite()` SHALL return `false`.

**Validates: Requirements 2.1, 2.2**

Property 2: Bug Condition - Yellow Background Highlight on Favorite Players

_For any_ player where `isFavorite()` returns `true` and the player is bound to a view in any of the four display contexts (PlayerSelectionAdapter, DraftHistoryAdapter, TeamRosterAdapter, DraftFragment), the item view's background SHALL be set to yellow highlight color.

**Validates: Requirements 2.4, 2.5, 2.6, 2.7**

Property 3: Preservation - Non-Favorite Player Rendering

_For any_ player where `isFavorite()` returns `false`, the item view's background SHALL remain the default background (no yellow highlight), preserving the existing visual appearance across all screens.

**Validates: Requirements 3.1**

Property 4: Preservation - Existing Field Parsing

_For any_ JSON player entry, all existing fields (id, name, position, rank, pffRank, positionRank, nflTeam, lastYearStats, injuryStatus, espnId, byeWeek) SHALL parse to the same values as before the fix, regardless of whether a `"favorite"` field is present.

**Validates: Requirements 3.2**

Property 5: Preservation - Parcelable Round-Trip

_For any_ `Player` object with any combination of field values including `favorite`, writing to a `Parcel` and reading back SHALL produce an equal `Player` object with all fields preserved, including the `favorite` field.

**Validates: Requirements 3.6**

Property 6: Preservation - Draft Operations Unaffected

_For any_ draft operation (draft player, undo pick, reset draft), the operation SHALL produce the same result regardless of the player's `favorite` status, preserving all existing draft logic.

**Validates: Requirements 3.4, 3.5**

## Fix Implementation

### Changes Required

Assuming our root cause analysis is correct:

**File**: `app/src/main/java/com/fantasydraft/picker/models/Player.java`

**Changes**:
1. **Add `favorite` field**: Add `private boolean favorite;` field to the class.
2. **Add getter/setter**: Add `isFavorite()` and `setFavorite(boolean)` methods.
3. **Update constructors**: Initialize `favorite = false` in all existing constructors.
4. **Update `equals()` and `hashCode()`**: Include `favorite` in equality checks and hash computation.
5. **Update Parcelable**: Add `favorite` to `writeToParcel()` (as byte) and read it in the `Parcel` constructor.

**File**: `app/src/main/java/com/fantasydraft/picker/utils/PlayerDataParser.java`

**Function**: `parsePlayerObject()`

**Changes**:
1. **Parse favorite field**: Add `player.setFavorite(playerJson.optBoolean("favorite", false));` after the existing optional field parsing.

**File**: `app/src/main/res/menu/menu_drawer_navigation.xml`

**Changes**:
1. **Add Favorites menu item**: Add a new `<item>` with `android:id="@+id/navigation_favorites"` and `android:title="Favorites"` to the drawer menu group.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/MainActivity.java`

**Changes**:
1. **Handle Favorites navigation**: In the navigation item selected listener within `setupNavigationDrawer()`, add a case for `R.id.navigation_favorites` that shows a new `FavoritesFragment`.

**New File**: `app/src/main/java/com/fantasydraft/picker/ui/FavoritesFragment.java`

**Changes**:
1. **Create FavoritesFragment**: A Fragment with a RecyclerView listing all players where `isFavorite() == true`. Each item shows player info and a toggle to change favorite status.

**New File**: `app/src/main/res/layout/fragment_favorites.xml`

**Changes**:
1. **Create layout**: A layout with a RecyclerView for the favorites list and an empty-state message.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/PlayerSelectionAdapter.java`

**Function**: `PlayerViewHolder.bind()`

**Changes**:
1. **Apply yellow highlight**: After binding player data, check `player.isFavorite()` and set the root view's background color to yellow if true, or reset to default if false.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java`

**Function**: `onBindViewHolder()`

**Changes**:
1. **Apply yellow highlight**: After binding pick data, look up the player and check `isFavorite()`. Set the item view's background color to yellow if true, or reset to default if false.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/TeamRosterAdapter.java`

**Function**: `ViewHolder.bind()`

**Changes**:
1. **Apply yellow highlight**: After binding roster entry data, check `entry.getPlayer().isFavorite()` and set the item view's background color to yellow if true, or reset to default if false.

**File**: `app/src/main/java/com/fantasydraft/picker/ui/DraftFragment.java`

**Functions**: `updateBestAvailable()`, `updatePickSlot()`

**Changes**:
1. **Apply yellow highlight to best available card**: After populating the best available section, check `bestPlayer.isFavorite()` and set the card's background to yellow if true, or default if false.
2. **Apply yellow highlight to recent pick slots**: In `updatePickSlot()`, after populating the slot, check `player.isFavorite()` and set the slot's background to yellow if true, or default if false.

**File**: `app/src/main/res/values/colors.xml`

**Changes**:
1. **Add favorite highlight color**: Add `<color name="favorite_highlight">#FFFFF9C4</color>` (light yellow) as the standard highlight color used across all adapters.

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the bug on unfixed code, then verify the fix works correctly and preserves existing behavior.

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the bug BEFORE implementing the fix. Confirm or refute the root cause analysis. If we refute, we will need to re-hypothesize.

**Test Plan**: Write tests that attempt to access `player.isFavorite()`, parse JSON with `"favorite": true`, and check adapter background colors. Run these tests on the UNFIXED code to observe compilation failures and missing behavior.

**Test Cases**:
1. **Model Field Test**: Attempt to call `player.isFavorite()` — will fail to compile on unfixed code (method does not exist).
2. **JSON Parsing Test**: Parse a JSON entry with `"favorite": true` and check the Player object — will show the field is ignored on unfixed code.
3. **Adapter Highlight Test**: Bind a player to a PlayerSelectionAdapter ViewHolder and check background color — will show default background on unfixed code regardless of any favorite status.
4. **Parcelable Round-Trip Test**: Parcel and unparcel a Player, check favorite field — will fail on unfixed code (field not serialized).

**Expected Counterexamples**:
- `Player` class has no `isFavorite()` method (compilation error)
- JSON `"favorite"` field is silently ignored during parsing
- All adapter item views have default background regardless of player data

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds, the fixed function produces the expected behavior.

**Pseudocode:**
```
FOR ALL input WHERE isBugCondition(input) DO
  player := parsePlayerFromJson(input.json)
  ASSERT player.isFavorite() == input.json.favorite
  
  view := bindPlayerToAdapter(player, input.context)
  IF player.isFavorite() THEN
    ASSERT view.backgroundColor == YELLOW_HIGHLIGHT
  END IF
END FOR
```

### Preservation Checking

**Goal**: Verify that for all inputs where the bug condition does NOT hold, the fixed function produces the same result as the original function.

**Pseudocode:**
```
FOR ALL input WHERE NOT isBugCondition(input) DO
  player_original := parsePlayerFromJson_original(input.json)
  player_fixed := parsePlayerFromJson_fixed(input.json)
  
  ASSERT player_original.name == player_fixed.name
  ASSERT player_original.position == player_fixed.position
  ASSERT player_original.rank == player_fixed.rank
  // ... all existing fields match
  ASSERT player_fixed.isFavorite() == false
  
  view := bindPlayerToAdapter(player_fixed, input.context)
  ASSERT view.backgroundColor == DEFAULT_BACKGROUND
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It generates many random Player configurations to verify non-favorite players always get default backgrounds
- It generates random JSON entries to verify all existing fields parse identically with or without the `"favorite"` field
- It catches edge cases in Parcelable serialization across many field combinations

**Test Plan**: Observe behavior on UNFIXED code first for non-favorite player rendering and JSON parsing, then write property-based tests capturing that behavior.

**Test Cases**:
1. **Non-Favorite Rendering Preservation**: Verify that players with `favorite == false` continue to render with default background across all adapters.
2. **JSON Field Parsing Preservation**: Verify that adding `"favorite"` field parsing does not alter how any other field (name, position, rank, pffRank, etc.) is parsed.
3. **Parcelable Preservation**: Verify that all existing fields survive parcel round-trip and the new `favorite` field is also preserved.
4. **Draft Operation Preservation**: Verify that drafting, undoing, and resetting picks works identically for favorite and non-favorite players.

### Unit Tests

- Test `Player.isFavorite()` defaults to `false` in all constructors
- Test `Player.setFavorite(true)` / `Player.isFavorite()` round-trip
- Test `Player.equals()` and `hashCode()` include `favorite` field
- Test `PlayerDataParser.parsePlayerObject()` with `"favorite": true`, `"favorite": false`, and field absent
- Test each adapter's `bind()` method applies yellow background for favorite players and default for non-favorites
- Test `FavoritesFragment` displays only favorite players
- Test favorite toggle updates the player's favorite status

### Property-Based Tests

- Generate random JSON player objects with/without `"favorite"` field and verify parsing correctness for all fields including favorite
- Generate random `Player` objects and verify Parcelable round-trip preserves all fields including `favorite`
- Generate random player lists with mixed favorite statuses and verify adapter binding applies correct backgrounds (yellow for favorites, default for non-favorites)
- Generate random draft sequences and verify draft operations are unaffected by favorite status

### Integration Tests

- Test full flow: parse JSON with favorites → navigate to Favorites screen → verify favorite players listed
- Test navigation drawer shows Favorites item and navigates correctly
- Test that toggling favorite on the Favorites screen updates the highlight across other screens
- Test that yellow highlight appears correctly in PlayerSelectionDialog, DraftHistory, TeamRosterPopup, and DraftFragment for favorite players
