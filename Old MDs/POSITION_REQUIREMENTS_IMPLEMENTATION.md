# Position Requirements Implementation

## Summary
Added configurable min/max roster requirements for each position in the league configuration.

## Changes Made:

### 1. Model Updates
- **DraftConfig.java**: Added `positionRequirements` Map and `PositionRequirement` inner class
  - Default requirements: QB(1-3), RB(2-6), WR(2-6), TE(1-3), K(1-2), DST(1-2)
  - Parcelable implementation updated to serialize/deserialize requirements

### 2. UI Updates
- **fragment_config.xml**: Added "Position Roster Requirements" card section
- **item_position_requirement.xml**: Created layout for individual position requirement controls
  - Shows position badge with color
  - Min/Max controls with +/- buttons

### 3. Next Steps (TODO):
- Update ConfigFragment.java to:
  - Populate position requirements UI from config
  - Handle +/- button clicks to adjust min/max values
  - Save position requirements when config is saved
  - Validate that min <= max for each position

- Use position requirements in:
  - Draft analytics to check if roster meets requirements
  - Position needs identification
  - Draft grade calculation (penalty for not meeting requirements)

## Usage:
Users can now configure their league's specific roster requirements (e.g., 2-QB leagues, RB-heavy leagues, etc.)
