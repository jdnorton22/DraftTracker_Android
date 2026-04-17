# Changelog - Version 3.4 (Build 29)

## Major Features

### 🎯 Pick Value Scoring System
- Added value indicators for every draft pick showing if it was good value or a reach
- Visual indicators in Recent Picks and Draft History:
  - ★ Great Value (+20 or more) - Green
  - ↑ Good Value (+10 to +19) - Light Green
  - = Fair Value (-9 to +9) - Gray
  - ↓ Slight Reach (-10 to -19) - Orange
  - ⚠ Big Reach (-20 or worse) - Red
- Value score calculated as: Pick Number - Player ADP
- Positive scores = good value (player fell to you)
- Negative scores = reach (drafted earlier than ADP)

### 📊 Position Roster Requirements
- Configurable min/max requirements for each position in League Configuration
- Settings for QB, RB, WR, TE, K, DST
- "No Limit" option for maximum (default)
- Requirements displayed in Team Roster Dialog with status indicators:
  - ✓ Meets minimum requirements
  - ❌ Below minimum requirements
  - ⚠️ Exceeds maximum limit
- Compact format: `QB: 2/1+` (current: 2, min: 1, max: no limit)

### 🏆 Curve-Based Draft Grading
- League-wide draft analytics with relative grading
- All teams graded on a curve for fair comparison
- Grade distribution ensures full spectrum (A+ to F):
  - Top 10%: A+ (95-100)
  - 75-90%: A to A- (85-94)
  - 60-75%: B+, B, B- (70-84)
  - 40-60%: C+, C, C- (60-69)
  - 20-40%: D (50-59)
  - Bottom 20%: F (0-49)
- Team selector dropdown in analytics dialog to view all teams
- Grades shown in dropdown for quick comparison

### 📈 Draft Analytics Dialog Enhancements
- View analytics for all teams in the league
- Team selector with grades: "Team 1 (A+)", "Team 2 (B-)"
- Detailed stats for each team:
  - Overall grade and score
  - Draft strategy classification
  - Value picks and reach picks
  - Average ADP difference
  - Best and worst picks
  - Position distribution
- "View Draft" button now properly loads complete draft history
- "Analytics" button appears after draft completion for easy access

## UI/UX Improvements

### 🎨 Best Available Card
- Gradient tint now covers entire card (not just player details)
- Theme-aware gradients:
  - Light mode: Position color → white
  - Dark mode: Position color → dark gray
- Better text visibility in both themes
- Smooth animations when best player changes

### 🎨 Visual Enhancements
- Position requirements display in Team Roster Dialog
- Monospace font for alignment in requirements display
- Color-coded status indicators throughout
- Improved dark mode support

## Bug Fixes

### 🐛 Fixed Issues
- **Pick value indicators**: Corrected calculation logic (was backwards)
  - Now correctly shows great value when player falls to you
  - Shows reach when you draft earlier than ADP
- **Draft analytics**: Fixed "View Draft" button opening empty screen
  - Now passes pick history, teams, and players data
- **Dark mode**: Fixed white text on white gradient in Best Available card
  - Gradient now uses dark colors in dark mode
- **Visibility**: Fixed value indicators not showing for picks at exactly ADP
  - Now shows indicators even when value score is 0

## Technical Improvements

### 🔧 Code Quality
- Added `PickValueCalculator` utility class for consistent value calculations
- Refactored `DraftAnalyzer` to support curve-based grading
- Added `analyzeAllTeamsWithCurve()` method for league-wide analysis
- Improved `DraftAnalyticsDialog` to handle multiple teams
- Better separation of concerns in analytics logic

### 📦 Data Model Updates
- Added `PositionRequirement` inner class to `DraftConfig`
- Added `positionRequirements` Map to store min/max per position
- Updated Parcelable implementation for persistence
- Added `hasMaxLimit()` helper method

## Files Modified

### New Files
- `app/src/main/java/com/fantasydraft/picker/utils/PickValueCalculator.java`
- `app/src/main/res/layout/item_position_requirement.xml`
- `POSITION_REQUIREMENTS_IMPLEMENTATION.md`
- `CURVE_BASED_GRADING.md`
- `ROSTER_REQUIREMENTS_DISPLAY.md`
- `GRADING_AND_ICONS_REFERENCE.md`

### Modified Files
- `app/src/main/java/com/fantasydraft/picker/utils/DraftAnalyzer.java`
- `app/src/main/java/com/fantasydraft/picker/models/DraftConfig.java`
- `app/src/main/java/com/fantasydraft/picker/models/DraftAnalytics.java`
- `app/src/main/java/com/fantasydraft/picker/ui/DraftFragment.java`
- `app/src/main/java/com/fantasydraft/picker/ui/DraftHistoryAdapter.java`
- `app/src/main/java/com/fantasydraft/picker/ui/DraftAnalyticsDialog.java`
- `app/src/main/java/com/fantasydraft/picker/ui/ConfigFragment.java`
- `app/src/main/java/com/fantasydraft/picker/ui/TeamRosterDialog.java`
- `app/src/main/res/layout/fragment_draft.xml`
- `app/src/main/res/layout/fragment_config.xml`
- `app/src/main/res/layout/item_draft_pick.xml`
- `app/src/main/res/layout/dialog_draft_analytics.xml`
- `app/src/main/res/layout/dialog_team_roster.xml`
- `app/build.gradle` (version updated to 3.4, build 29)

## Grading Scale Reference

### Letter Grades
- A+ : 90-100 points
- A  : 85-89 points
- A- : 80-84 points
- B+ : 77-79 points
- B  : 73-76 points
- B- : 70-72 points
- C+ : 67-69 points
- C  : 63-66 points
- C- : 60-62 points
- D  : 50-59 points
- F  : 0-49 points

### Grade Calculation
Starting from base 70:
- +2 points per value pick
- -1.5 points per reach pick
- +0.5 points per point of positive average ADP difference
- +5 points for balanced roster (1+ QB, 2+ RB, 2+ WR, 1+ TE)
- Curve applied based on percentile ranking among all teams

## Known Issues
None at this time.

## Upgrade Notes
- Position requirements default to "No Limit" for maximum
- Existing drafts will have default requirements applied
- Draft analytics now requires all teams to be analyzed (slight performance impact on completion)
- Analytics button appears after draft completion in Recent Picks section

## Future Enhancements
- Use position requirements for roster validation warnings during draft
- Add position requirement progress indicators during draft
- Export analytics to CSV/PDF
- Historical analytics comparison across multiple drafts
