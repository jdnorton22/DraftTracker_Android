# Fantasy Draft Picker

An Android application for managing fantasy football drafts with support for serpentine and linear draft flows.

## Quick Start

### Updating Player Data

Keep your player rankings current with the automated update script:

```bash
# Windows
scripts\update_players.bat

# Or run Python directly
python scripts/update_players.py
```

This fetches current rankings from ESPN Fantasy Football and updates `app/src/main/res/raw/players.json`.

See [scripts/README.md](scripts/README.md) for detailed documentation.

## Project Structure

```
app/src/main/java/com/fantasydraft/picker/
├── models/          # Data models (Team, Player, Pick, DraftState, DraftConfig, FlowType)
├── managers/        # Business logic (DraftManager, TeamManager, PlayerManager)
├── persistence/     # Data persistence (DatabaseHelper, PersistenceManager)
└── ui/              # User interface (Activities, Fragments, Adapters)

scripts/             # Data update scripts
├── update_players.py    # Python script to fetch current rankings
├── update_players.bat   # Windows batch file wrapper
└── README.md            # Detailed script documentation
```

## Requirements

- Android SDK 24+ (Android 7.0 Nougat)
- Java 8+
- Gradle 8.0+
- Python 3.6+ (for data updates)

## Dependencies

- AndroidX libraries for UI components
- JUnit 4 for unit testing
- junit-quickcheck for property-based testing
- Mockito for mocking in tests
- Espresso for UI testing

## Building

```bash
# Build debug APK
.\gradlew assembleDebug --console=plain

# Deploy to connected device
C:\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```

## Testing

```bash
# Run unit tests
.\gradlew test

# Run instrumented tests
.\gradlew connectedAndroidTest
```

## Features

### Draft Management
- Configure teams and draft order
- Support for serpentine and linear draft flows
- Real-time best available player recommendations
- Draft history tracking with undo capability
- Team-based filtering in draft history

### Player Database
- 220+ players (30 QBs, 60 RBs, 60 WRs, 30 TEs, 20 Kickers, 20 Defense/Special Teams)
- Overall rankings, PFF rankings, and position rankings
- Realistic last year statistics by position (based on 2024 season)
- Injury status indicators
- NFL team assignments
- Search and filter capabilities
- Hide drafted players toggle
- Draft custom/unlisted players

### Data Management
- ESPN API credentials management (secure encrypted storage)
- Player data refresh from ESPN Fantasy Football
- Automated data updates via Python script
- Persistent draft state across app restarts

### User Interface
- Optimized for foldable devices (Surface Duo)
- Compact layout with scrollable sections
- Material Design components
- Blue football app icon
- Green and gold theme colors
- Persistent draft state across sessions
- Draft reset functionality
