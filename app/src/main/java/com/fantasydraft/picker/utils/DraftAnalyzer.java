package com.fantasydraft.picker.utils;

import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.models.DraftAnalytics;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyzes draft performance and generates grades and insights.
 * Supports both absolute and curve-based grading.
 */
public class DraftAnalyzer {
    
    /**
     * Analyze all teams' draft performance and apply curve-based grading.
     * This ensures a proper distribution of grades across all teams.
     * 
     * @param teams List of all teams
     * @param allPicks List of all picks
     * @param playerManager Player manager for resolving player details
     * @return List of DraftAnalytics for each team with curve-based grades
     */
    public static List<DraftAnalytics> analyzeAllTeamsWithCurve(List<Team> teams, List<Pick> allPicks, 
                                                                 PlayerManager playerManager) {
        List<DraftAnalytics> allAnalytics = new ArrayList<>();
        
        // First, analyze each team to get raw scores
        for (Team team : teams) {
            DraftAnalytics analytics = analyzeDraft(team, allPicks, playerManager);
            allAnalytics.add(analytics);
        }
        
        // Apply curve-based grading
        applyCurveGrading(allAnalytics);
        
        return allAnalytics;
    }
    
    /**
     * Apply curve-based grading to ensure proper grade distribution.
     * Grades are assigned based on percentile ranking among all teams.
     */
    private static void applyCurveGrading(List<DraftAnalytics> allAnalytics) {
        if (allAnalytics.isEmpty()) return;
        
        // Sort by raw grade (descending)
        List<DraftAnalytics> sorted = new ArrayList<>(allAnalytics);
        sorted.sort((a, b) -> Double.compare(b.getOverallGrade(), a.getOverallGrade()));
        
        int totalTeams = sorted.size();
        
        // Apply curve based on percentile
        for (int i = 0; i < sorted.size(); i++) {
            DraftAnalytics analytics = sorted.get(i);
            double percentile = (double) (totalTeams - i) / totalTeams * 100;
            
            // Assign grade based on percentile with proper distribution
            double curvedGrade;
            if (percentile >= 90) {
                // Top 10%: A+ (95-100)
                curvedGrade = 95 + (percentile - 90) * 0.5;
            } else if (percentile >= 75) {
                // 75-90%: A to A- (85-94)
                curvedGrade = 85 + (percentile - 75) * 0.6;
            } else if (percentile >= 60) {
                // 60-75%: B+, B, B- (70-84)
                curvedGrade = 70 + (percentile - 60);
            } else if (percentile >= 40) {
                // 40-60%: C+, C, C- (60-69)
                curvedGrade = 60 + (percentile - 40) * 0.5;
            } else if (percentile >= 20) {
                // 20-40%: D (50-59)
                curvedGrade = 50 + (percentile - 20) * 0.5;
            } else {
                // Bottom 20%: F (0-49)
                curvedGrade = percentile * 2.5;
            }
            
            analytics.setOverallGrade(Math.max(0, Math.min(100, curvedGrade)));
        }
    }
    
    /**
     * Analyze a team's draft performance
     */
    public static DraftAnalytics analyzeDraft(Team team, List<Pick> allPicks, PlayerManager playerManager) {
        DraftAnalytics analytics = new DraftAnalytics();
        analytics.setTeamId(team.getId());
        analytics.setTeamName(team.getName());
        
        // Get team's picks
        List<Pick> teamPicks = new ArrayList<>();
        for (Pick pick : allPicks) {
            if (pick.getTeamId().equals(team.getId())) {
                teamPicks.add(pick);
            }
        }
        
        if (teamPicks.isEmpty()) {
            analytics.setOverallGrade(0);
            return analytics;
        }
        
        // Analyze position distribution
        Map<String, Integer> positionCounts = new HashMap<>();
        double totalAdpDiff = 0;
        int valuePicks = 0;
        int reachPicks = 0;
        String bestPickName = null;
        int bestPickValue = 0;
        String worstPickName = null;
        int worstPickValue = 0;
        
        for (Pick pick : teamPicks) {
            Player player = playerManager.getPlayerById(pick.getPlayerId());
            if (player == null) continue;
            
            String position = player.getPosition();
            positionCounts.put(position, positionCounts.getOrDefault(position, 0) + 1);
            
            // Calculate value score using PickValueCalculator
            int valueScore = PickValueCalculator.calculateValueScore(pick, player);
            if (valueScore != 0) {
                totalAdpDiff += valueScore;
                
                if (valueScore >= PickValueCalculator.GOOD_VALUE_THRESHOLD) {
                    valuePicks++;
                    if (valueScore > bestPickValue) {
                        bestPickValue = valueScore;
                        bestPickName = player.getName() + " (" + PickValueCalculator.getValueString(valueScore) + ")";
                    }
                } else if (valueScore <= PickValueCalculator.SLIGHT_REACH_THRESHOLD) {
                    reachPicks++;
                    if (valueScore < worstPickValue) {
                        worstPickValue = valueScore;
                        worstPickName = player.getName() + " (" + PickValueCalculator.getValueString(valueScore) + ")";
                    }
                }
            }
        }
        
        analytics.setPositionCounts(positionCounts);
        analytics.setValuePicks(valuePicks);
        analytics.setReachPicks(reachPicks);
        analytics.setAverageAdpDifference(totalAdpDiff / teamPicks.size());
        analytics.setBestPick(bestPickName);
        analytics.setWorstPick(worstPickName);
        
        // Calculate overall grade
        double grade = calculateGrade(teamPicks, positionCounts, valuePicks, reachPicks, totalAdpDiff);
        analytics.setOverallGrade(grade);
        
        // Determine draft strategy
        String strategy = determineDraftStrategy(positionCounts, teamPicks.size());
        analytics.setDraftStrategy(strategy);
        
        // Identify position needs
        String[] needs = identifyPositionNeeds(positionCounts);
        analytics.setPositionNeeds(needs);
        
        return analytics;
    }
    
    /**
     * Calculate overall draft grade (0-100)
     */
    private static double calculateGrade(List<Pick> picks, Map<String, Integer> positionCounts, 
                                        int valuePicks, int reachPicks, double totalAdpDiff) {
        double grade = 70.0; // Base grade
        
        // Bonus for value picks
        grade += valuePicks * 2.0;
        
        // Penalty for reaches
        grade -= reachPicks * 1.5;
        
        // Bonus for positive average ADP difference
        double avgAdpDiff = totalAdpDiff / picks.size();
        grade += avgAdpDiff * 0.5;
        
        // Bonus for balanced roster
        int qb = positionCounts.getOrDefault("QB", 0);
        int rb = positionCounts.getOrDefault("RB", 0);
        int wr = positionCounts.getOrDefault("WR", 0);
        int te = positionCounts.getOrDefault("TE", 0);
        
        if (qb >= 1 && rb >= 2 && wr >= 2 && te >= 1) {
            grade += 5.0; // Bonus for meeting minimum requirements
        }
        
        // Cap grade between 0 and 100
        return Math.max(0, Math.min(100, grade));
    }
    
    /**
     * Determine draft strategy based on position distribution
     */
    private static String determineDraftStrategy(Map<String, Integer> positionCounts, int totalPicks) {
        int rb = positionCounts.getOrDefault("RB", 0);
        int wr = positionCounts.getOrDefault("WR", 0);
        int qb = positionCounts.getOrDefault("QB", 0);
        
        if (rb >= totalPicks * 0.4) {
            return "RB Heavy";
        } else if (wr >= totalPicks * 0.4) {
            return "WR Heavy";
        } else if (rb <= 1) {
            return "Zero RB";
        } else if (qb >= 2 && totalPicks >= 10) {
            return "QB Stacking";
        } else if (Math.abs(rb - wr) <= 1) {
            return "Balanced";
        } else {
            return "Flexible";
        }
    }
    
    /**
     * Identify position needs based on current roster
     */
    private static String[] identifyPositionNeeds(Map<String, Integer> positionCounts) {
        List<String> needs = new ArrayList<>();
        
        int qb = positionCounts.getOrDefault("QB", 0);
        int rb = positionCounts.getOrDefault("RB", 0);
        int wr = positionCounts.getOrDefault("WR", 0);
        int te = positionCounts.getOrDefault("TE", 0);
        int k = positionCounts.getOrDefault("K", 0);
        int dst = positionCounts.getOrDefault("DST", 0);
        
        if (qb == 0) needs.add("QB");
        if (rb < 2) needs.add("RB");
        if (wr < 2) needs.add("WR");
        if (te == 0) needs.add("TE");
        if (k == 0) needs.add("K");
        if (dst == 0) needs.add("DST");
        
        return needs.toArray(new String[0]);
    }
    
    /**
     * Get upcoming picks for a specific team
     */
    public static List<Integer> getUpcomingPicks(String teamId, List<Team> teams, 
                                                 List<Pick> completedPicks, int totalRounds) {
        List<Integer> upcomingPicks = new ArrayList<>();
        
        // Find team index
        int teamIndex = -1;
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getId().equals(teamId)) {
                teamIndex = i;
                break;
            }
        }
        
        if (teamIndex == -1) return upcomingPicks;
        
        int teamCount = teams.size();
        int nextPickNumber = completedPicks.size() + 1;
        int totalPicks = totalRounds * teamCount;
        
        // Calculate next 3 picks for this team
        for (int pickNum = nextPickNumber; pickNum <= totalPicks && upcomingPicks.size() < 3; pickNum++) {
            int round = ((pickNum - 1) / teamCount) + 1;
            int pickInRound = ((pickNum - 1) % teamCount) + 1;
            
            // Determine which team picks (assuming serpentine)
            int pickingTeamIndex;
            if (round % 2 == 1) {
                pickingTeamIndex = pickInRound - 1;
            } else {
                pickingTeamIndex = teamCount - pickInRound;
            }
            
            if (pickingTeamIndex == teamIndex) {
                upcomingPicks.add(pickNum);
            }
        }
        
        return upcomingPicks;
    }
}
