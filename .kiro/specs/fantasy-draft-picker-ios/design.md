# Design Document: Fantasy Draft Picker iOS

## Overview

The Fantasy Draft Picker iOS is a native iOS application written in Swift using SwiftUI that manages fantasy football drafts. It provides an intuitive iOS-native interface for configuring teams, managing draft order, tracking picks in real-time, and displaying player recommendations. The system supports both serpentine and linear draft flows and persists state using Core Data to allow draft continuation across sessions.

The application follows iOS design guidelines and MVVM (Model-View-ViewModel) architecture, making it feel native to iOS users while maintaining feature parity with the Android version.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     SwiftUI Views                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Config     │  │    Draft     │  │   Player     │  │
│  │    View      │  │    View      │  │  List View   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     ViewModels                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Config     │  │    Draft     │  │   Player     │  │
│  │  ViewModel   │  │  ViewModel   │  │  ViewModel   │  │
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
│  │  Core Data   │  │ UserDefaults │                     │
│  │   Manager    │  │              │                     │
│  └──────────────┘  └──────────────┘                     │
└─────────────────────────────────────────────────────────┘
```

### MVVM Pattern

The app uses MVVM (Model-View-ViewModel) architecture:

- **Models**: Core Data entities and Swift structs for data representation
- **Views**: SwiftUI views that observe ViewModels
- **ViewModels**: ObservableObject classes that manage state and business logic
- **Managers**: Service classes that handle data operations and business rules

### Component Responsibilities

- **ConfigView**: SwiftUI view for team setup, draft order, and flow configuration
- **DraftView**: Main draft interface showing current pick, history, and controls
- **PlayerListView**: Searchable, filterable list of all players
- **BestAvailableCard**: Prominent card showing top recommendation
- **ConfigViewModel**: Manages team configuration state and validation
- **DraftViewModel**: Coordinates draft flow, pick sequencing, and state
- **PlayerViewModel**: Manages player pool, filtering, and availability
- **DraftManager**: Core draft logic and pick sequence calculation
- **TeamManager**: Team data management and validation
- **PlayerManager**: Player pool management and ranking logic
- **CoreDataManager**: Handles all Core Data operations
- **UserDefaults**: Stores simple preferences and configuration

## Components and Interfaces

### Draft Manager

The Draft Manager is the core orchestrator of the draft process.

```swift
enum FlowType: String, Codable {
    case serpentine
    case linear
}

struct DraftConfig: Codable {
    var flowType: FlowType
    var numberOfRounds: Int
    var timerEnabled: Bool
    var timerSeconds: Int
}

struct DraftState: Codable {
    var currentRound: Int
    var currentPickInRound: Int
    var isComplete: Bool
    var totalPicks: Int
}

class DraftManager {
    // Calculate the team index for the current pick
    func getCurrentTeamIndex(state: DraftState, config: DraftConfig, teamCount: Int) -> Int
    
    // Advance to the next pick
    func advancePick(state: DraftState, config: DraftConfig, teamCount: Int) -> DraftState
    
    // Generate the complete pick sequence for the draft
    func generatePickSequence(config: DraftConfig, teams: [Team]) -> [Int]
    
    // Reset draft to initial state
    func resetDraft() -> DraftState
    
    // Check if draft is complete
    func isDraftComplete(state: DraftState, config: DraftConfig, teamCount: Int) -> Bool
}
```

### Team Manager

Manages team configuration and draft order.

```swift
struct Team: Identifiable, Codable {
    var id: UUID
    var name: String
    var draftPosition: Int
    var roster: [Player]
    
    init(name: String, position: Int) {
        self.id = UUID()
        self.name = name
        self.draftPosition = position
        self.roster = []
    }
}

class TeamManager {
    // Add a new team
    func addTeam(name: String, position: Int) -> Team
    
    // Validate team names are unique
    func validateTeamName(_ name: String, existingTeams: [Team]) -> Bool
    
    // Validate draft order is complete (1 to N with no gaps)
    func validateDraftOrder(teams: [Team]) -> Bool
    
    // Update team draft position
    func updateDraftPosition(teamId: UUID, newPosition: Int, teams: inout [Team])
    
    // Add player to team roster
    func addPlayerToRoster(teamId: UUID, player: Player, teams: inout [Team])
    
    // Shuffle draft order randomly
    func shuffleDraftOrder(teams: inout [Team])
}
```

### Player Manager

Manages the player pool and rankings.

```swift
struct Player: Identifiable, Codable {
    var id: UUID
    var name: String
    var position: String
    var nflTeam: String?
    var rank: Int
    var adpRank: Int?
    var positionRank: Int?
    var isDrafted: Bool
    var draftedBy: UUID? // team id
    var lastYearStats: String?
    var injuryStatus: String?
    var isCustom: Bool
    
    init(name: String, position: String, rank: Int) {
        self.id = UUID()
        self.name = name
        self.position = position
        self.rank = rank
        self.isDrafted = false
        self.isCustom = false
    }
}

class PlayerManager {
    // Get the best available player
    func getBestAvailable(players: [Player]) -> Player?
    
    // Mark player as drafted
    func draftPlayer(playerId: UUID, teamId: UUID, players: inout [Player])
    
    // Get all available players
    func getAvailablePlayers(players: [Player]) -> [Player]
    
    // Filter players by position
    func filterByPosition(players: [Player], position: String) -> [Player]
    
    // Search players by name
    func searchPlayers(players: [Player], query: String) -> [Player]
    
    // Reset all players to undrafted state
    func resetPlayers(players: inout [Player])
    
    // Load players from JSON bundle
    func loadPlayersFromJSON() -> [Player]
    
    // Add custom player
    func addCustomPlayer(name: String, position: String, rank: Int?, players: inout [Player]) -> Player
}
```

### Core Data Manager

Handles all Core Data operations for persistence.

```swift
class CoreDataManager: ObservableObject {
    static let shared = CoreDataManager()
    let container: NSPersistentContainer
    
    init() {
        container = NSPersistentContainer(name: "FantasyDraftPicker")
        container.loadPersistentStores { description, error in
            if let error = error {
                print("Core Data failed to load: \(error.localizedDescription)")
            }
        }
    }
    
    // Save current draft state
    func saveDraft(snapshot: DraftSnapshot)
    
    // Load saved draft state
    func loadDraft() -> DraftSnapshot?
    
    // Clear saved draft state
    func clearDraft()
    
    // Save multiple draft boards
    func saveDraftBoard(name: String, snapshot: DraftSnapshot)
    
    // Load all draft boards
    func loadAllDraftBoards() -> [DraftBoard]
    
    // Delete draft board
    func deleteDraftBoard(id: UUID)
}

struct DraftSnapshot: Codable {
    var id: UUID
    var leagueName: String
    var teams: [Team]
    var players: [Player]
    var draftState: DraftState
    var draftConfig: DraftConfig
    var pickHistory: [Pick]
    var timestamp: Date
}

struct DraftBoard: Identifiable {
    var id: UUID
    var leagueName: String
    var lastModified: Date
    var teamCount: Int
    var currentRound: Int
}
```

### ViewModels

ViewModels connect the UI to the business logic using Combine framework.

```swift
class DraftViewModel: ObservableObject {
    @Published var teams: [Team] = []
    @Published var players: [Player] = []
    @Published var draftState: DraftState
    @Published var draftConfig: DraftConfig
    @Published var pickHistory: [Pick] = []
    @Published var bestAvailable: Player?
    @Published var currentTeam: Team?
    @Published var showingPlayerList = false
    @Published var showingResetAlert = false
    @Published var timerRemaining: Int = 0
    
    private let draftManager = DraftManager()
    private let teamManager = TeamManager()
    private let playerManager = PlayerManager()
    private let coreDataManager = CoreDataManager.shared
    private var timerCancellable: AnyCancellable?
    
    init() {
        // Initialize with default or loaded state
    }
    
    func makePick(player: Player)
    func advanceToNextPick()
    func resetDraft()
    func undoLastPick()
    func updateBestAvailable()
    func startTimer()
    func pauseTimer()
    func saveDraft()
    func loadDraft()
    func exportToCSV() -> String
}

class ConfigViewModel: ObservableObject {
    @Published var teams: [Team] = []
    @Published var teamCount: Int = 10
    @Published var flowType: FlowType = .serpentine
    @Published var numberOfRounds: Int = 15
    @Published var timerEnabled: Bool = false
    @Published var timerSeconds: Int = 90
    @Published var validationError: String?
    
    private let teamManager = TeamManager()
    
    func addTeam(name: String)
    func removeTeam(id: UUID)
    func updateTeamName(id: UUID, newName: String)
    func moveTeam(from: IndexSet, to: Int)
    func shuffleTeams()
    func validateConfiguration() -> Bool
    func saveConfiguration()
}

class PlayerViewModel: ObservableObject {
    @Published var players: [Player] = []
    @Published var filteredPlayers: [Player] = []
    @Published var searchText: String = ""
    @Published var selectedPosition: String = "ALL"
    @Published var showDrafted: Bool = false
    @Published var sortBy: SortOption = .rank
    
    private let playerManager = PlayerManager()
    
    enum SortOption {
        case rank, name, position, adp
    }
    
    func loadPlayers()
    func filterPlayers()
    func addCustomPlayer(name: String, position: String, rank: Int?)
    func deleteCustomPlayer(id: UUID)
}
```

## Data Models

### Core Data Entities

**DraftBoardEntity**
- `id`: UUID
- `leagueName`: String
- `lastModified`: Date
- `teamCount`: Int16
- `currentRound`: Int16
- `draftData`: Binary Data (encoded DraftSnapshot)

**CustomPlayerEntity**
- `id`: UUID
- `name`: String
- `position`: String
- `rank`: Int16
- `nflTeam`: String (optional)
- `createdDate`: Date

### Swift Structs

**Pick**
```swift
struct Pick: Identifiable, Codable {
    var id: UUID
    var pickNumber: Int
    var round: Int
    var pickInRound: Int
    var teamId: UUID
    var teamName: String
    var playerId: UUID
    var playerName: String
    var playerPosition: String
    var timestamp: Date
}
```

### Draft Flow Calculation

For serpentine drafts, the pick order alternates each round:
- Odd rounds: 1, 2, 3, ..., N
- Even rounds: N, N-1, N-2, ..., 1

For linear drafts, the pick order remains constant:
- All rounds: 1, 2, 3, ..., N

The team index for a given pick is calculated as:
```swift
func getCurrentTeamIndex(state: DraftState, config: DraftConfig, teamCount: Int) -> Int {
    let pickInRound = state.currentPickInRound
    
    if config.flowType == .serpentine {
        if state.currentRound % 2 == 1 { // Odd round
            return pickInRound - 1
        } else { // Even round
            return teamCount - pickInRound
        }
    } else { // Linear
        return pickInRound - 1
    }
}
```

## User Interface Design

### SwiftUI View Hierarchy

```
ContentView (TabView)
├── DraftView
│   ├── DraftHeaderView (current pick info)
│   ├── BestAvailableCard (tappable)
│   ├── DraftControlsView (Make Pick, Reset buttons)
│   └── DraftHistoryList
├── ConfigView
│   ├── TeamCountStepper
│   ├── TeamListView (drag-to-reorder)
│   ├── FlowTypePicker
│   ├── RoundCountStepper
│   └── TimerConfigView
├── PlayerListView
│   ├── SearchBar
│   ├── PositionFilterPicker
│   ├── SortPicker
│   └── PlayerList (LazyVStack)
└── DraftBoardsView
    └── DraftBoardList
```

### Color Scheme

Following iOS design guidelines with support for light and dark mode:

**Position Colors** (matching Android version):
- QB: Blue (#2196F3)
- RB: Green (#4CAF50)
- WR: Orange (#FF9800)
- TE: Purple (#9C27B0)
- K: Gray (#757575)
- DST: Brown (#795548)

**App Colors**:
- Primary: iOS Blue (system)
- Secondary: iOS Gray (system)
- Background: iOS Background (adaptive)
- Card Background: iOS Secondary Background (adaptive)
- Text: iOS Label (adaptive)

### SF Symbols Usage

- Football: `sportscourt.fill`
- Team: `person.3.fill`
- Draft Order: `list.number`
- Timer: `timer`
- Search: `magnifyingglass`
- Filter: `line.3.horizontal.decrease.circle`
- Export: `square.and.arrow.up`
- Reset: `arrow.counterclockwise`
- Settings: `gearshape.fill`
- Checkmark: `checkmark.circle.fill`
- Position Icons: Custom or `figure.american.football`

### Adaptive Layouts

**iPhone Portrait**:
- Single column layout
- Best Available at top
- Draft history below
- Tab bar navigation

**iPhone Landscape**:
- Two column layout
- Best Available on right
- Draft controls on left

**iPad**:
- Three column layout
- Config/Teams on left sidebar
- Draft view in center
- Best Available and history on right
- Split view support

## iOS-Specific Features

### Haptic Feedback

```swift
import CoreHaptics

class HapticManager {
    static let shared = HapticManager()
    
    func playSuccess() {
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)
    }
    
    func playWarning() {
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.warning)
    }
    
    func playSelection() {
        let generator = UISelectionFeedbackGenerator()
        generator.selectionChanged()
    }
    
    func playImpact(style: UIImpactFeedbackGenerator.FeedbackStyle = .medium) {
        let generator = UIImpactFeedbackGenerator(style: style)
        generator.impactOccurred()
    }
}
```

### Accessibility

```swift
// VoiceOver support
Text("Best Available Player")
    .accessibilityLabel("Best available player recommendation")
    .accessibilityHint("Tap to draft this player")

// Dynamic Type support
Text(player.name)
    .font(.body)
    .dynamicTypeSize(.large...accessibility5)

// Accessibility identifiers for UI testing
Button("Make Pick")
    .accessibilityIdentifier("makePickButton")
```

### Share Sheet Integration

```swift
struct ShareSheet: UIViewControllerRepresentable {
    let items: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(
            activityItems: items,
            applicationActivities: nil
        )
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

// Usage
.sheet(isPresented: $showingShareSheet) {
    ShareSheet(items: [csvData])
}
```

### Animations

```swift
// Pick animation
withAnimation(.spring(response: 0.3, dampingFraction: 0.7)) {
    draftState.currentPickInRound += 1
}

// Card flip animation
.rotation3DEffect(
    .degrees(isFlipped ? 180 : 0),
    axis: (x: 0, y: 1, z: 0)
)

// List item insertion
.transition(.asymmetric(
    insertion: .move(edge: .leading).combined(with: .opacity),
    removal: .move(edge: .trailing).combined(with: .opacity)
))
```

## Testing Strategy

### Unit Testing (XCTest)

Unit tests will verify specific examples, edge cases, and error conditions:

```swift
import XCTest
@testable import FantasyDraftPicker

class DraftManagerTests: XCTestCase {
    var draftManager: DraftManager!
    
    override func setUp() {
        super.setUp()
        draftManager = DraftManager()
    }
    
    func testSerpentineFlowOddRound() {
        let config = DraftConfig(flowType: .serpentine, numberOfRounds: 15)
        let state = DraftState(currentRound: 1, currentPickInRound: 1, isComplete: false)
        let teamIndex = draftManager.getCurrentTeamIndex(state: state, config: config, teamCount: 10)
        XCTAssertEqual(teamIndex, 0)
    }
    
    func testSerpentineFlowEvenRound() {
        let config = DraftConfig(flowType: .serpentine, numberOfRounds: 15)
        let state = DraftState(currentRound: 2, currentPickInRound: 1, isComplete: false)
        let teamIndex = draftManager.getCurrentTeamIndex(state: state, config: config, teamCount: 10)
        XCTAssertEqual(teamIndex, 9)
    }
}
```

### Property-Based Testing (SwiftCheck)

Property-based tests will verify universal properties across all inputs:

```swift
import SwiftCheck
@testable import FantasyDraftPicker

class DraftPropertyTests: XCTestCase {
    func testTeamCountValidation() {
        property("Team count between 2 and 20 is valid") <- forAll { (count: Int) in
            let teamManager = TeamManager()
            let isValid = teamManager.isValidTeamCount(count)
            return (count >= 2 && count <= 20) == isValid
        }
    }
    
    func testDraftOrderCompleteness() {
        property("Draft order contains all positions 1 to N") <- forAll { (teamCount: Positive<Int>) in
            let count = min(teamCount.getPositive, 20)
            var teams: [Team] = []
            for i in 1...count {
                teams.append(Team(name: "Team \(i)", position: i))
            }
            let teamManager = TeamManager()
            return teamManager.validateDraftOrder(teams: teams)
        }
    }
    
    func testPersistenceRoundTrip() {
        property("Save and load produces equivalent state") <- forAll { (teamCount: Positive<Int>) in
            let count = min(teamCount.getPositive, 20)
            let snapshot = createRandomDraftSnapshot(teamCount: count)
            let coreDataManager = CoreDataManager.shared
            
            coreDataManager.saveDraft(snapshot: snapshot)
            let loaded = coreDataManager.loadDraft()
            
            return loaded?.teams.count == snapshot.teams.count &&
                   loaded?.draftState.currentRound == snapshot.draftState.currentRound
        }
    }
}
```

### UI Testing (XCUITest)

UI tests will verify user interactions and flows:

```swift
import XCTest

class FantasyDraftPickerUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }
    
    func testMakePick() {
        // Navigate to draft view
        app.tabBars.buttons["Draft"].tap()
        
        // Tap best available card
        app.buttons["bestAvailableCard"].tap()
        
        // Confirm pick
        app.alerts.buttons["Draft"].tap()
        
        // Verify pick was made
        XCTAssertTrue(app.staticTexts.containing(NSPredicate(format: "label CONTAINS 'Pick 1'")).element.exists)
    }
    
    func testSearchPlayers() {
        app.tabBars.buttons["Players"].tap()
        
        let searchField = app.searchFields["Search players"]
        searchField.tap()
        searchField.typeText("Mahomes")
        
        XCTAssertTrue(app.staticTexts["Patrick Mahomes"].exists)
    }
}
```

### Testing Tools

- **Unit Testing**: XCTest (built-in)
- **Property-Based Testing**: SwiftCheck
- **UI Testing**: XCUITest (built-in)
- **Mocking**: Protocol-based mocking or third-party libraries
- **Code Coverage**: Xcode's built-in coverage tools

## Implementation Notes

### Technology Stack

- **Language**: Swift 5.9+
- **UI Framework**: SwiftUI
- **Minimum iOS Version**: iOS 16.0
- **Persistence**: Core Data + UserDefaults
- **Reactive Programming**: Combine framework
- **Build Tool**: Xcode 15+
- **Dependency Management**: Swift Package Manager
- **Testing**: XCTest, SwiftCheck

### Project Structure

```
FantasyDraftPicker/
├── App/
│   ├── FantasyDraftPickerApp.swift
│   └── ContentView.swift
├── Models/
│   ├── Team.swift
│   ├── Player.swift
│   ├── Pick.swift
│   ├── DraftState.swift
│   └── DraftConfig.swift
├── ViewModels/
│   ├── DraftViewModel.swift
│   ├── ConfigViewModel.swift
│   └── PlayerViewModel.swift
├── Views/
│   ├── Draft/
│   │   ├── DraftView.swift
│   │   ├── BestAvailableCard.swift
│   │   └── DraftHistoryList.swift
│   ├── Config/
│   │   ├── ConfigView.swift
│   │   └── TeamListView.swift
│   └── Players/
│       ├── PlayerListView.swift
│       └── PlayerDetailView.swift
├── Managers/
│   ├── DraftManager.swift
│   ├── TeamManager.swift
│   ├── PlayerManager.swift
│   ├── CoreDataManager.swift
│   └── HapticManager.swift
├── Utilities/
│   ├── Extensions.swift
│   ├── Constants.swift
│   └── ColorScheme.swift
├── Resources/
│   ├── players.json
│   └── Assets.xcassets
└── Tests/
    ├── UnitTests/
    ├── PropertyTests/
    └── UITests/
```

### Player Data Source

The initial implementation will include a bundled JSON file with player data:

```json
{
  "players": [
    {
      "name": "Christian McCaffrey",
      "position": "RB",
      "nflTeam": "SF",
      "rank": 1,
      "adpRank": 1,
      "positionRank": 1,
      "lastYearStats": "1459 YDS, 14 TD",
      "injuryStatus": "HEALTHY"
    }
  ]
}
```

### Performance Considerations

- Use `LazyVStack` and `LazyHStack` for efficient list rendering
- Implement pagination for large player lists
- Use `@StateObject` and `@ObservedObject` appropriately to minimize re-renders
- Cache filtered/sorted player lists
- Use background threads for Core Data operations
- Implement debouncing for search text input
- Use `Identifiable` protocol for efficient list updates

### Future Enhancements

- iCloud sync for draft boards across devices
- Watch app for quick draft updates
- Widget for current pick and best available
- Siri Shortcuts for common actions
- Live Activities for draft timer
- SharePlay for collaborative drafting
- Machine learning for personalized recommendations
- Integration with fantasy football APIs
- Draft simulation mode
- Mock draft support
- Trade analyzer
- Waiver wire management

## Deployment

### App Store Requirements

- App icon in all required sizes (1024x1024 for App Store)
- Launch screen
- Privacy policy (if collecting any data)
- App Store screenshots (iPhone and iPad)
- App description and keywords
- Age rating
- Support URL

### Build Configuration

```swift
// Info.plist
<key>CFBundleDisplayName</key>
<string>Fantasy Draft Picker</string>
<key>CFBundleShortVersionString</key>
<string>1.0</string>
<key>CFBundleVersion</key>
<string>1</string>
<key>UILaunchScreen</key>
<dict/>
<key>UISupportedInterfaceOrientations</key>
<array>
    <string>UIInterfaceOrientationPortrait</string>
    <string>UIInterfaceOrientationLandscapeLeft</string>
    <string>UIInterfaceOrientationLandscapeRight</string>
</array>
```

### Code Signing

- Development certificate for testing
- Distribution certificate for App Store
- Provisioning profiles
- App ID with appropriate capabilities

This design provides a comprehensive blueprint for building a native iOS version of the Fantasy Draft Picker that maintains feature parity with the Android version while following iOS design guidelines and best practices.
