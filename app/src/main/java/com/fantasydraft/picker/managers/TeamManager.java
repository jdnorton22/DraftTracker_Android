package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Manages team configuration and draft order.
 */
public class TeamManager {
    private List<Team> teams;

    public TeamManager() {
        this.teams = new ArrayList<>();
    }

    /**
     * Add a new team with the specified name and draft position.
     * 
     * @param name The team name
     * @param position The draft position (1 to N)
     * @return The created Team object
     * @throws IllegalArgumentException if name is null/empty or position is invalid
     */
    public Team addTeam(String name, int position) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        
        if (position < 1) {
            throw new IllegalArgumentException("Draft position must be at least 1");
        }
        
        if (!validateTeamName(name, teams)) {
            throw new IllegalArgumentException("Team name must be unique");
        }
        
        String id = UUID.randomUUID().toString();
        Team team = new Team(id, name.trim(), position);
        teams.add(team);
        return team;
    }

    /**
     * Validate that a team name is unique among existing teams.
     * 
     * @param name The team name to validate
     * @param existingTeams The list of existing teams
     * @return true if the name is unique, false otherwise
     */
    public boolean validateTeamName(String name, List<Team> existingTeams) {
        if (name == null || existingTeams == null) {
            return false;
        }
        
        String trimmedName = name.trim();
        for (Team team : existingTeams) {
            if (team.getName().equalsIgnoreCase(trimmedName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate that the draft order is complete (contains positions 1 to N with no gaps or duplicates).
     * 
     * @param teams The list of teams to validate
     * @return true if draft order is complete, false otherwise
     */
    public boolean validateDraftOrder(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return false;
        }
        
        int teamCount = teams.size();
        Set<Integer> positions = new HashSet<>();
        
        for (Team team : teams) {
            int position = team.getDraftPosition();
            
            // Check if position is in valid range
            if (position < 1 || position > teamCount) {
                return false;
            }
            
            // Check for duplicates
            if (positions.contains(position)) {
                return false;
            }
            
            positions.add(position);
        }
        
        // Verify all positions from 1 to N are present
        return positions.size() == teamCount;
    }

    /**
     * Update the draft position of a team.
     * 
     * @param teamId The ID of the team to update
     * @param newPosition The new draft position
     * @throws IllegalArgumentException if team is not found or position is invalid
     */
    public void updateDraftPosition(String teamId, int newPosition) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
        
        if (newPosition < 1) {
            throw new IllegalArgumentException("Draft position must be at least 1");
        }
        
        Team targetTeam = null;
        for (Team team : teams) {
            if (team.getId().equals(teamId)) {
                targetTeam = team;
                break;
            }
        }
        
        if (targetTeam == null) {
            throw new IllegalArgumentException("Team not found with ID: " + teamId);
        }
        
        targetTeam.setDraftPosition(newPosition);
    }

    /**
     * Add a player to a team's roster.
     * 
     * @param teamId The ID of the team
     * @param player The player to add
     * @throws IllegalArgumentException if team is not found or player is null
     */
    public void addPlayerToRoster(String teamId, Player player) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
        
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        Team targetTeam = null;
        for (Team team : teams) {
            if (team.getId().equals(teamId)) {
                targetTeam = team;
                break;
            }
        }
        
        if (targetTeam == null) {
            throw new IllegalArgumentException("Team not found with ID: " + teamId);
        }
        
        targetTeam.getRoster().add(player);
    }

    /**
     * Get all teams.
     * 
     * @return List of all teams
     */
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    /**
     * Clear all teams.
     */
    public void clearTeams() {
        teams.clear();
    }

    /**
     * Get a team by ID.
     * 
     * @param teamId The team ID
     * @return The team, or null if not found
     */
    public Team getTeamById(String teamId) {
        if (teamId == null) {
            return null;
        }
        
        for (Team team : teams) {
            if (team.getId().equals(teamId)) {
                return team;
            }
        }
        return null;
    }
}
