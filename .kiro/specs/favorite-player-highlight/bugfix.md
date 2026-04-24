# Bugfix Requirements Document

## Introduction

The Fantasy Draft Picker app currently provides no mechanism for users to mark players as "favorites" or visually distinguish desirable players from the rest of the player pool. Users must mentally track which players they want to target during a draft, leading to missed picks and a poor drafting experience. This bugfix introduces a `favorite` boolean field on the Player model (sourced from `players.json`), a dedicated Favorites management screen accessible via the side navigation drawer, and yellow card highlighting across all screens that display player data.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN a user wants to mark a player as a favorite THEN the system provides no mechanism to flag or persist that preference on the Player model.

1.2 WHEN `players.json` contains a `"favorite": true` field on a player entry THEN the system ignores the field during parsing and the Player object has no corresponding property.

1.3 WHEN a user navigates the side drawer menu THEN the system does not offer a dedicated Favorites page to view or manage favorite players.

1.4 WHEN a player is considered a favorite and appears on the draft screen (best available section, player cards) THEN the system renders the player card with the default background, providing no visual distinction.

1.5 WHEN a player is considered a favorite and appears in the Player Selection Dialog THEN the system renders the player row with the default background, providing no visual distinction.

1.6 WHEN a player is considered a favorite and appears in the Draft History list THEN the system renders the pick entry with the default background, providing no visual distinction.

1.7 WHEN a player is considered a favorite and appears in the Team Roster Popup THEN the system renders the roster entry with the default background, providing no visual distinction.

### Expected Behavior (Correct)

2.1 WHEN a user wants to mark a player as a favorite THEN the system SHALL store a boolean `favorite` field on the Player model that defaults to `false`.

2.2 WHEN `players.json` contains a `"favorite": true` field on a player entry THEN the system SHALL parse the field and set the corresponding `favorite` property on the Player object to `true`.

2.3 WHEN a user opens the side drawer menu THEN the system SHALL display a "Favorites" menu item that navigates to a dedicated Favorites screen listing all players marked as favorite, allowing the user to toggle the favorite status of any player.

2.4 WHEN a player with `favorite == true` appears on the draft screen (best available section, player cards) THEN the system SHALL render the player card with a yellow background highlight to visually distinguish it.

2.5 WHEN a player with `favorite == true` appears in the Player Selection Dialog THEN the system SHALL render the player row with a yellow background highlight.

2.6 WHEN a player with `favorite == true` appears in the Draft History list THEN the system SHALL render the pick entry with a yellow background highlight.

2.7 WHEN a player with `favorite == true` appears in the Team Roster Popup THEN the system SHALL render the roster entry with a yellow background highlight.

### Unchanged Behavior (Regression Prevention)

3.1 WHEN a player has `favorite == false` or the field is absent from `players.json` THEN the system SHALL CONTINUE TO render the player card with the default background on all screens.

3.2 WHEN parsing `players.json` entries that do not contain a `"favorite"` field THEN the system SHALL CONTINUE TO parse all other player fields correctly and default `favorite` to `false`.

3.3 WHEN navigating to the Draft or Config screens via the side drawer THEN the system SHALL CONTINUE TO display and navigate to those screens as before.

3.4 WHEN drafting, undoing, or resetting picks THEN the system SHALL CONTINUE TO function identically regardless of a player's favorite status.

3.5 WHEN filtering, searching, or sorting players in the Player Selection Dialog THEN the system SHALL CONTINUE TO apply filters and display results correctly, with favorite players simply receiving the yellow highlight.

3.6 WHEN the Player model is parceled and unparceled (e.g., passed between activities) THEN the system SHALL CONTINUE TO preserve all existing fields and additionally preserve the `favorite` field.
