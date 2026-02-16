# Design Document: Fantasy Draft Picker

## Overview

The Fantasy Draft Picker is a native Android application written in Java that manages fantasy football drafts. It provides a clean interface for configuring teams, managing draft order, tracking picks in real-time, and displaying player recommendations. The system supports both serpentine and linear draft flows and persists state to allow draft continuation across sessions.

The application follows a native Android architecture with SharedPreferences or SQLite persistence, making it simple to use offline without requiring a backend server.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Android Activities/Fragments            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Config     │  │    Draft     │  │   Player     │  │
│  │   Activity   │  │   Activity   │  │   Fragment   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  Business Logic Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │    Draft     │  │    Team      │  │   Player     │  │
│  │   Manager    │  │   Manager    │  │   Manager    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  Data Access Layer                       │
│  ┌──────────────┐  ┌──────────────┐                     │
│  │   SQLite     │  │    Shared    │                     │
│  │   Database   │  │  Preferences │                     │
│  └──────────────┘  └──────────────┘                     │
└─────────────────────────────────────────────────────────┘
```

### Component Responsibilities

- **Config Activity**: Handles team setup, draft order configuration, and draft flow selection
- **Draft Activity**: Displays current pick, draft history, and pick controls
- **Player Fragment**: Shows best available player recommendation (right side or separate panel)
- **Draft Manager**: Coordinates draft flow, pick sequencing, and state transitions
- **Team Manager**: Manages team data, draft order, and team-player associations
- **Player Manager**: Maintains player pool, rankings, and availability status
- **SQLite Database**: Stores teams, players, picks, and draft state
- **SharedPreferences**: Stores draft configuration and simple state data

## Components and Interfaces

### Draft Manager

The Draft Manager is the core orchestrator of the draft process.

```java
public enum FlowType {
    SERPENTINE,
    LINEAR
}

public class DraftConfig {
    private FlowType flowType;
    private int numberOfRounds;
    
    // Constructor, getters, setters
}

public class DraftState {
    private int currentRound;
    private int currentPickInRound;
    private boolean isComplete;
    
    // Constructor, getters, setters
}

public class DraftManager {
    // Calculate the team index for the current pick
    public int getCurrentTeamIndex(DraftState state, DraftConfig config, int teamCount);
    
    // Advance to the next pick
    public DraftState advancePick(DraftState state, DraftConfig config, int teamCount);
    
    // Generate the complete pick sequence for the draft
    public List<Integer> generatePickSequence(DraftConfig config, List<Team> teams);
    
    // Reset draft to initial state
    public DraftState resetDraft();
}
```

### Team Manager

Manages team configuration and draft order.

```java
public class Team {
    private String id;
    private String name;
    private int draftPosition;
    private List<Player> roster;
    
    // Constructor, getters, setters
}

public class TeamManager {
    // Add a new team
    public Team addTeam(String name, int position);
    
    // Validate team names are unique
    public boolean validateTeamName(String name, List<Team> existingTeams);
    
    // Validate draft order is complete (1 to N with no gaps)
    public boolean validateDraftOrder(List<Team> teams);
    
    // Update team draft position
    public void updateDraftPosition(String teamId, int newPosition);
    
    // Add player to team roster
    public void addPlayerToRoster(String teamId, Player player);
}
```

### Player Manager

Manages the player pool and rankings.

```java
public class Player {
    private String id;
    private String name;
    private String position;
    private int rank;
    private boolean isDrafted;
    private String draftedBy; // team id
    
    // Constructor, getters, setters
}

public class PlayerManager {
    // Get the best available player
    public Player getBestAvailable(List<Player> players);
    
    // Mark player as drafted
    public void draftPlayer(String playerId, String teamId);
    
    // Get all available players
    public List<Player> getAvailablePlayers(List<Player> players);
    
    // Reset all players to undrafted state
    public void resetPlayers();
}
```

### Persistence Manager

Handles saving and loading draft state using SQLite and SharedPreferences.

```java
public class DraftSnapshot {
    private List<Team> teams;
    private List<Player> players;
    private DraftState draftState;
    private DraftConfig draftConfig;
    private List<Pick> pickHistory;
    private long timestamp;
    
    // Constructor, getters, setters
}

public class PersistenceManager {
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    
    // Save current draft state
    public void saveDraft(DraftSnapshot snapshot);
    
    // Load saved draft state
    public DraftSnapshot loadDraft();
    
    // Clear saved draft state
    public void clearDraft();
}
```

## Data Models

### Core Data Structures

**Team**
- `id`: Unique identifier
- `name`: Team name (must be unique)
- `draftPosition`: Position in draft order (1 to N)
- `roster`: Array of drafted players

**Player**
- `id`: Unique identifier
- `name`: Player name
- `position`: Football position (QB, RB, WR, TE, etc.)
- `rank`: Overall ranking (lower is better)
- `isDrafted`: Boolean flag
- `draftedBy`: Team ID (if drafted)

**Pick**
- `pickNumber`: Overall pick number (1, 2, 3, ...)
- `round`: Round number
- `pickInRound`: Pick number within the round
- `teamId`: Team making the pick
- `playerId`: Player selected
- `timestamp`: When the pick was made

**DraftState**
- `currentRound`: Current round number (1-based)
- `currentPickInRound`: Current pick within round (1-based)
- `isComplete`: Whether draft is finished

**DraftConfig**
- `flowType`: 'serpentine' or 'linear'
- `numberOfRounds`: Total rounds in the draft

### Draft Flow Calculation

For serpentine drafts, the pick order alternates each round:
- Odd rounds: 1, 2, 3, ..., N
- Even rounds: N, N-1, N-2, ..., 1

For linear drafts, the pick order remains constant:
- All rounds: 1, 2, 3, ..., N

The team index for a given pick is calculated as:
```
if flowType === 'serpentine':
  if round is odd:
    teamIndex = pickInRound - 1
  else:
    teamIndex = teamCount - pickInRound
else: // linear
  teamIndex = pickInRound - 1
```

## Correctness Properties


A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property 1: Team Count Validation

*For any* integer value, the system should accept it as a valid team count if and only if it is between 2 and 20 (inclusive).

**Validates: Requirements 1.1**

### Property 2: Team Name Uniqueness

*For any* list of teams, all team names should be unique, and attempting to add a duplicate team name should be rejected.

**Validates: Requirements 1.3**

### Property 3: Draft Order Completeness

*For any* set of N teams, the draft positions should contain exactly the integers from 1 to N with no duplicates or gaps.

**Validates: Requirements 2.1, 2.2**

### Property 4: Serpentine Flow Alternation

*For any* draft with serpentine flow and N teams, odd-numbered rounds should have pick order [1, 2, ..., N] and even-numbered rounds should have pick order [N, N-1, ..., 1].

**Validates: Requirements 3.1, 3.3**

### Property 5: Linear Flow Consistency

*For any* draft with linear flow and N teams, all rounds should have the same pick order [1, 2, ..., N].

**Validates: Requirements 3.2, 3.4**

### Property 6: Pick Sequence Recalculation

*For any* draft configuration, changing the draft flow type or draft order should result in a recalculated pick sequence that reflects the new settings.

**Validates: Requirements 2.3, 3.5**

### Property 7: Pick Advancement

*For any* draft state, making a pick should advance to the next team in the sequence according to the draft flow, and completing a round should transition to the first pick of the next round.

**Validates: Requirements 4.2, 4.3, 7.5**

### Property 8: Best Available Player Calculation

*For any* player pool, the best available player should be the undrafted player with the lowest rank value, and drafting that player should update the best available to the next lowest-ranked undrafted player.

**Validates: Requirements 5.1, 5.2, 5.3, 5.5**

### Property 9: Tiebreaker Consistency

*For any* set of players with equal rankings, selecting the best available player multiple times (in different contexts) should always return the same player when the tied players are available.

**Validates: Requirements 5.4**

### Property 10: Persistence Round Trip

*For any* draft state, saving the state and then loading it should produce an equivalent draft state with all teams, players, picks, and configuration preserved.

**Validates: Requirements 6.1, 6.2, 6.3, 6.4**

### Property 11: Player Draft State Update

*For any* available player, when that player is drafted by a team, the player should be marked as drafted and associated with that team.

**Validates: Requirements 7.2, 7.3**

### Property 12: Drafted Player Rejection

*For any* player that is already drafted, attempting to draft that player again should be rejected with an error.

**Validates: Requirements 7.4**

### Property 13: Draft History Completeness

*For any* sequence of picks made during a draft, the draft history should contain all picks in chronological order with complete information (team name, player name, pick number).

**Validates: Requirements 8.1, 8.2, 8.3, 8.4**

### Property 14: Draft Reset Clears Picks

*For any* draft with completed picks, resetting the draft should clear all picks, mark all players as available, and return the current pick to position 1 of round 1.

**Validates: Requirements 9.1, 9.2, 9.3**

### Property 15: Draft Reset Preserves Configuration

*For any* draft configuration (teams, draft order, flow type), resetting the draft should preserve all configuration settings while clearing only the pick history and player draft status.

**Validates: Requirements 9.4**

## Error Handling

### Input Validation Errors

- **Invalid Team Count**: Reject team counts outside the range [2, 20] with a clear error message
- **Duplicate Team Name**: Reject duplicate team names and prompt for a unique name
- **Invalid Draft Position**: Reject draft positions outside the range [1, N] or that create duplicates
- **Incomplete Draft Order**: Prevent draft from starting if draft order has gaps or duplicates

### Draft Operation Errors

- **Draft Already-Drafted Player**: Reject attempts to draft players who are already drafted
- **Draft When Not On Clock**: Prevent teams from drafting when it's not their turn (if implementing turn enforcement)
- **Invalid Player Selection**: Reject selections of non-existent players

### Persistence Errors

- **Storage Quota Exceeded**: Handle local storage quota errors gracefully with user notification
- **Corrupted State**: Detect and handle corrupted saved state by offering to start fresh
- **Load Failure**: If loading fails, start with a clean slate and notify the user

### Recovery Strategies

- **Validation Errors**: Display clear error messages and maintain current state
- **Persistence Errors**: Attempt to continue in-memory operation and notify user of persistence issues
- **State Corruption**: Offer to reset to a clean state rather than crash

## Testing Strategy

### Unit Testing

Unit tests will verify specific examples, edge cases, and error conditions:

- **Team Configuration**: Test adding teams with valid/invalid names and counts
- **Draft Order**: Test setting and validating draft order with various team counts
- **Flow Calculations**: Test serpentine and linear flow with specific team counts (2, 3, 10, 20)
- **Pick Advancement**: Test advancing through rounds with specific scenarios
- **Player Management**: Test drafting, availability checks, and best available calculation
- **Persistence**: Test save/load with specific draft states
- **Reset**: Test reset functionality with various draft states
- **Error Conditions**: Test all validation and error scenarios

### Property-Based Testing

Property-based tests will verify universal properties across all inputs using junit-quickcheck for Java:

- Each property test will run a minimum of 100 iterations
- Tests will generate random team counts, team names, draft configurations, and pick sequences
- Each test will be tagged with a comment referencing its design property
- Tag format: `// Feature: fantasy-draft-picker, Property N: [property description]`

**Property Test Coverage**:
- Generate random team counts (2-20) and verify validation
- Generate random team names and verify uniqueness enforcement
- Generate random draft orders and verify completeness
- Generate random draft configurations and verify flow calculations
- Generate random pick sequences and verify advancement logic
- Generate random player pools and verify best available calculation
- Generate random draft states and verify persistence round-trip
- Generate random pick histories and verify history completeness
- Generate random draft states and verify reset behavior

### Integration Testing

Integration tests will verify that components work together correctly:

- **End-to-End Draft Flow**: Configure teams, make picks through multiple rounds, verify state
- **Persistence Integration**: Make picks, save, simulate restart, verify loaded state
- **UI Integration**: Use Espresso to verify that UI components correctly display data from managers

### Testing Tools

- **Unit Testing**: JUnit 4 or JUnit 5
- **Property-Based Testing**: junit-quickcheck (Java)
- **UI Testing**: Espresso for Android UI testing
- **Mocking**: Mockito for mocking dependencies

## Implementation Notes

### Technology Stack Recommendations

- **Language**: Java
- **Platform**: Android (minimum SDK 24 / Android 7.0)
- **UI Framework**: Android XML layouts with Activities and Fragments
- **Persistence**: SQLite database for structured data, SharedPreferences for configuration
- **Build Tool**: Gradle
- **Testing**: JUnit 4/5 for unit tests, junit-quickcheck for property-based testing

### Player Data Source

The initial implementation should include a sample player dataset with rankings. This can be:
- Hardcoded as a JSON resource file in `res/raw/`
- Loaded from assets folder
- Eventually, could integrate with fantasy football APIs

### UI Layout

The Android app will use a master-detail layout pattern:

**Main Activity (Draft Screen)**
```
┌─────────────────────────────────────────────────────────────┐
│  ☰  Fantasy Draft Picker                          ⚙         │
├─────────────────────────────────────┬───────────────────────┤
│                                     │                       │
│  Draft Configuration                │  Best Available       │
│  Teams: 10                          │                       │
│  Flow: Serpentine                   │  ┌─────────────────┐ │
│  [Edit Config]                      │  │ Christian McCaffrey│
│                                     │  │ RB - #1          │ │
│  Current Pick                       │  │                  │ │
│  Round 1, Pick 3                    │  │ Recommended      │ │
│  Team: The Champs                   │  └─────────────────┘ │
│                                     │                       │
│  [MAKE PICK]  [RESET DRAFT]         │  [View All Players]  │
│                                     │                       │
│  Draft History                      │                       │
│  ┌─────────────────────────────┐   │                       │
│  │ 1. Team A - Player X (QB)   │   │                       │
│  │ 2. Team B - Player Y (RB)   │   │                       │
│  │ 3. ...                      │   │                       │
│  └─────────────────────────────┘   │                       │
│                                     │                       │
└─────────────────────────────────────┴───────────────────────┘
```

**Configuration Activity**
- RecyclerView for team list with editable names and positions
- Spinner for draft flow selection
- Number picker for team count

### Performance Considerations

- Player pool should use HashMap for O(1) lookups by ID
- Best available calculation should be optimized (pre-sorted ArrayList, binary search)
- RecyclerView for efficient rendering of draft history
- Database queries should use proper indexing on player rank and isDrafted fields
- Consider using ViewModel and LiveData for reactive UI updates

### Future Enhancements

- Import player rankings from CSV/API
- Support for keeper leagues (pre-drafted players)
- Draft timer with countdown
- Multiple draft boards (different leagues)
- Export draft results to CSV or share
- Undo last pick functionality
- Dark mode support
- Tablet-optimized layouts
