# Draft Grading Scale and Icons Reference

## Overall Draft Grade Scale (0-100)

### Letter Grades
- **A+** : 90-100 points
- **A**  : 85-89 points
- **A-** : 80-84 points
- **B+** : 77-79 points
- **B**  : 73-76 points
- **B-** : 70-72 points
- **C+** : 67-69 points
- **C**  : 63-66 points
- **C-** : 60-62 points
- **D**  : 50-59 points
- **F**  : 0-49 points

### Grade Colors
- **Green** (85-100): Excellent draft
- **Blue** (70-84): Good draft
- **Orange** (60-69): Average draft
- **Red** (0-59): Poor draft

### How Grades Are Calculated
Starting from a base grade of 70:
- **+2 points** per value pick (drafted below ADP)
- **-1.5 points** per reach pick (drafted above ADP)
- **+0.5 points** per point of positive average ADP difference
- **+5 points** for balanced roster (1+ QB, 2+ RB, 2+ WR, 1+ TE)
- Final grade capped between 0-100

---

## Pick Value Indicators

These icons appear in Recent Picks and Draft History to show draft value:

### Value Tiers

#### ★ Great Value (Green)
- **Threshold**: +20 or more
- **Meaning**: Player drafted 20+ picks later than their ADP
- **Color**: Green (#4CAF50)
- **Example**: Player with ADP 25 drafted at pick 50 = +25 value

#### ↑ Good Value (Light Green)
- **Threshold**: +10 to +19
- **Meaning**: Player drafted 10-19 picks later than their ADP
- **Color**: Light Green (#8BC34A)
- **Example**: Player with ADP 30 drafted at pick 42 = +12 value

#### = Fair Value (Gray)
- **Threshold**: -9 to +9
- **Meaning**: Player drafted within 9 picks of their ADP
- **Color**: Gray (#9E9E9E)
- **Example**: Player with ADP 50 drafted at pick 47 = +3 value

#### ↓ Slight Reach (Orange)
- **Threshold**: -10 to -19
- **Meaning**: Player drafted 10-19 picks earlier than their ADP
- **Color**: Orange (#FF9800)
- **Example**: Player with ADP 60 drafted at pick 48 = -12 value

#### ⚠ Big Reach (Red)
- **Threshold**: -20 or worse
- **Meaning**: Player drafted 20+ picks earlier than their ADP
- **Color**: Red (#F44336)
- **Example**: Player with ADP 80 drafted at pick 55 = -25 value

### Value Score Calculation
```
Value Score = Pick Number - Player ADP

Positive = Good value (drafted later than expected - player "fell" to you)
Negative = Reach (drafted earlier than expected - you "reached" for player)
Zero = Exactly at ADP
```

**Examples:**
- Player ADP 80, drafted at pick 50: 50 - 80 = **-30** (Big Reach ⚠)
- Player ADP 50, drafted at pick 80: 80 - 50 = **+30** (Great Value ★)
- Player ADP 45, drafted at pick 47: 47 - 45 = **+2** (Fair Value =)

---

## Position Requirements Indicators

These appear in the Team Roster Dialog:

### ✓ (Check Mark)
- **Meaning**: Position meets minimum requirements
- **Example**: `✓ QB: 2/1+` (have 2 QBs, need minimum 1)

### ❌ (Red X)
- **Meaning**: Position does NOT meet minimum requirements
- **Example**: `❌ RB: 1/2+` (have 1 RB, need minimum 2)

### ⚠️ (Warning)
- **Meaning**: Position EXCEEDS maximum limit (when max is set)
- **Example**: `⚠️ WR: 7/2-6` (have 7 WRs, max allowed is 6)

### Format Explanation
- `QB: 2/1+` = Current: 2, Min: 1, Max: No limit
- `RB: 3/2-6` = Current: 3, Min: 2, Max: 6
- `WR: 0/2+` = Current: 0, Min: 2, Max: No limit

---

## Draft Strategy Classifications

Based on position distribution:

- **RB Heavy**: 40%+ of picks are RBs
- **WR Heavy**: 40%+ of picks are WRs
- **Zero RB**: 1 or fewer RBs drafted
- **QB Stacking**: 2+ QBs in 10+ round draft
- **Balanced**: RB and WR counts within 1 of each other
- **Flexible**: None of the above patterns

---

## Draft Analytics Metrics

### Value Picks
Count of picks where player was drafted below their ADP (good value)

### Reach Picks
Count of picks where player was drafted above their ADP (reached)

### Average ADP Difference
Average value score across all picks (positive = good, negative = bad)

### Best Pick
The pick with the highest positive value score

### Worst Pick
The pick with the most negative value score (biggest reach)

### Position Needs
Positions that don't meet minimum roster requirements:
- QB: Need at least 1
- RB: Need at least 2
- WR: Need at least 2
- TE: Need at least 1
- K: Need at least 1
- DST: Need at least 1

---

## Quick Reference

| Icon | Meaning | Threshold | Color |
|------|---------|-----------|-------|
| ★ | Great Value | +20 or more | Green |
| ↑ | Good Value | +10 to +19 | Light Green |
| = | Fair Value | -9 to +9 | Gray |
| ↓ | Slight Reach | -10 to -19 | Orange |
| ⚠ | Big Reach | -20 or worse | Red |
| ✓ | Meets Requirements | - | - |
| ❌ | Below Minimum | - | - |
| ⚠️ | Exceeds Maximum | - | - |
