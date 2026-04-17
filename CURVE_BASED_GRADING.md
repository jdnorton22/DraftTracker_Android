# Curve-Based Draft Grading System

## Overview
The draft grading system now uses curve-based grading to ensure proper grade distribution across all teams. Instead of absolute thresholds, teams are graded relative to each other based on their percentile ranking.

## How It Works

### Step 1: Calculate Raw Scores
Each team receives a raw score (0-100+) based on:
- **Base score**: 70 points
- **Value picks**: +2 points each (drafted below ADP)
- **Reach picks**: -1.5 points each (drafted above ADP)
- **Average ADP difference**: +0.5 points per point of positive difference
- **Balanced roster**: +5 points (1+ QB, 2+ RB, 2+ WR, 1+ TE)

### Step 2: Rank Teams
Teams are sorted by their raw scores from highest to lowest.

### Step 3: Apply Curve
Grades are assigned based on percentile ranking:

| Percentile | Letter Grade | Score Range | Distribution |
|------------|--------------|-------------|--------------|
| 90-100% | A+ | 95-100 | Top 10% |
| 75-89% | A, A- | 85-94 | Next 15% |
| 60-74% | B+, B, B- | 70-84 | Next 15% |
| 40-59% | C+, C, C- | 60-69 | Middle 20% |
| 20-39% | D | 50-59 | Next 20% |
| 0-19% | F | 0-49 | Bottom 20% |

## Benefits

### 1. Guaranteed Grade Distribution
- Every draft will have teams across the full grade spectrum
- No more situations where all teams get B's or C's
- Clear differentiation between best and worst performers

### 2. Relative Performance
- Grades reflect how well you drafted compared to other teams
- More meaningful in competitive drafts
- Accounts for draft difficulty and competition level

### 3. Fair Comparison
- Teams are judged against their peers, not arbitrary standards
- A team that drafts well in a tough league gets proper credit
- Weak drafts in easy leagues don't get inflated grades

## Examples

### 12-Team League
- **Top team** (100th percentile): A+ (95-100)
- **2nd-3rd teams** (83-92nd percentile): A to A- (85-94)
- **4th-6th teams** (58-75th percentile): B+, B, B- (70-84)
- **7th-9th teams** (33-50th percentile): C+, C, C- (60-69)
- **10th-11th teams** (17-25th percentile): D (50-59)
- **Bottom team** (8th percentile): F (20)

### 8-Team League
- **Top team** (100th percentile): A+ (100)
- **2nd team** (87.5th percentile): A (88)
- **3rd-4th teams** (62.5-75th percentile): B+, B (72-78)
- **5th-6th teams** (37.5-50th percentile): C+, C (63-66)
- **7th team** (25th percentile): D (55)
- **Bottom team** (12.5th percentile): F (31)

## Grade Letter Breakdown

The curve ensures these letter grades are distributed:

- **A+ (90-100)**: Exceptional draft, top 10%
- **A (85-89)**: Excellent draft, top 25%
- **A- (80-84)**: Very good draft, top 25%
- **B+ (77-79)**: Good draft, above average
- **B (73-76)**: Good draft, above average
- **B- (70-72)**: Solid draft, above average
- **C+ (67-69)**: Average draft
- **C (63-66)**: Average draft
- **C- (60-62)**: Below average draft
- **D (50-59)**: Poor draft, bottom 40%
- **F (0-49)**: Very poor draft, bottom 20%

## Technical Implementation

### Method: `analyzeAllTeamsWithCurve()`
```java
List<DraftAnalytics> analyzeAllTeamsWithCurve(
    List<Team> teams, 
    List<Pick> allPicks, 
    PlayerManager playerManager
)
```

This method:
1. Analyzes each team individually to get raw scores
2. Sorts teams by raw score (descending)
3. Calculates percentile for each team
4. Applies curve formula to assign final grades
5. Returns list of DraftAnalytics with curved grades

### Curve Formula
```
percentile = (totalTeams - rank) / totalTeams * 100

if percentile >= 90:
    grade = 95 + (percentile - 90) * 0.5
else if percentile >= 75:
    grade = 85 + (percentile - 75) * 0.6
else if percentile >= 60:
    grade = 70 + (percentile - 60)
else if percentile >= 40:
    grade = 60 + (percentile - 40) * 0.5
else if percentile >= 20:
    grade = 50 + (percentile - 20) * 0.5
else:
    grade = percentile * 2.5
```

## User Experience

When the draft completes:
1. All teams are analyzed and graded on the curve
2. User sees their team's curved grade in the analytics dialog
3. Grade reflects their performance relative to other teams
4. Full spectrum of grades (A+ to F) is utilized

## Migration

- Existing absolute grading logic remains as fallback
- Curve-based grading is now the default for draft completion
- No changes to pick value indicators or other analytics
- Backward compatible with existing data
