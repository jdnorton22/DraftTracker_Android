package com.fantasydraft.picker.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Analytics and grading for a completed draft.
 * Provides insights on draft performance, value picks, and position distribution.
 */
public class DraftAnalytics {
    
    private String teamId;
    private String teamName;
    private double overallGrade; // 0-100 scale
    private Map<String, Integer> positionCounts;
    private Map<String, Double> positionGrades;
    private int valuePicks; // Picks where player was drafted below ADP
    private int reachPicks; // Picks where player was drafted above ADP
    private double averageAdpDifference;
    private String bestPick;
    private String worstPick;
    private String[] positionNeeds;
    private String draftStrategy; // e.g., "RB Heavy", "Balanced", "Zero RB"
    
    public DraftAnalytics() {
        this.positionCounts = new HashMap<>();
        this.positionGrades = new HashMap<>();
    }
    
    // Getters and setters
    
    public String getTeamId() {
        return teamId;
    }
    
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public double getOverallGrade() {
        return overallGrade;
    }
    
    public void setOverallGrade(double overallGrade) {
        this.overallGrade = overallGrade;
    }
    
    public String getOverallGradeLetter() {
        if (overallGrade >= 90) return "A+";
        if (overallGrade >= 85) return "A";
        if (overallGrade >= 80) return "A-";
        if (overallGrade >= 77) return "B+";
        if (overallGrade >= 73) return "B";
        if (overallGrade >= 70) return "B-";
        if (overallGrade >= 67) return "C+";
        if (overallGrade >= 63) return "C";
        if (overallGrade >= 60) return "C-";
        if (overallGrade >= 50) return "D";
        return "F";
    }
    
    public Map<String, Integer> getPositionCounts() {
        return positionCounts;
    }
    
    public void setPositionCounts(Map<String, Integer> positionCounts) {
        this.positionCounts = positionCounts;
    }
    
    public Map<String, Double> getPositionGrades() {
        return positionGrades;
    }
    
    public void setPositionGrades(Map<String, Double> positionGrades) {
        this.positionGrades = positionGrades;
    }
    
    public int getValuePicks() {
        return valuePicks;
    }
    
    public void setValuePicks(int valuePicks) {
        this.valuePicks = valuePicks;
    }
    
    public int getReachPicks() {
        return reachPicks;
    }
    
    public void setReachPicks(int reachPicks) {
        this.reachPicks = reachPicks;
    }
    
    public double getAverageAdpDifference() {
        return averageAdpDifference;
    }
    
    public void setAverageAdpDifference(double averageAdpDifference) {
        this.averageAdpDifference = averageAdpDifference;
    }
    
    public String getBestPick() {
        return bestPick;
    }
    
    public void setBestPick(String bestPick) {
        this.bestPick = bestPick;
    }
    
    public String getWorstPick() {
        return worstPick;
    }
    
    public void setWorstPick(String worstPick) {
        this.worstPick = worstPick;
    }
    
    public String[] getPositionNeeds() {
        return positionNeeds;
    }
    
    public void setPositionNeeds(String[] positionNeeds) {
        this.positionNeeds = positionNeeds;
    }
    
    public String getDraftStrategy() {
        return draftStrategy;
    }
    
    public void setDraftStrategy(String draftStrategy) {
        this.draftStrategy = draftStrategy;
    }
}
