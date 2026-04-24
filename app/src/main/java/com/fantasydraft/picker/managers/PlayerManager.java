package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the player pool and rankings.
 */
public class PlayerManager {
    private List<Player> players;

    public PlayerManager() {
        this.players = new ArrayList<>();
    }

    public PlayerManager(List<Player> players) {
        this.players = players != null ? new ArrayList<>(players) : new ArrayList<>();
    }

    /**
     * Get the best available player (highest-ranked undrafted player).
     * Returns the player with the lowest rank value among undrafted players.
     * For ties, returns the first player encountered with that rank.
     * 
     * @param players List of players to search
     * @return The best available player, or null if no players are available
     */
    public Player getBestAvailable(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return null;
        }

        Player bestAvailable = null;
        int lowestRank = Integer.MAX_VALUE;

        for (Player player : players) {
            if (!player.isDrafted() && player.getRank() < lowestRank) {
                lowestRank = player.getRank();
                bestAvailable = player;
            }
        }

        return bestAvailable;
    }

    /**
     * Get the best available player at a specific position.
     * Returns the undrafted player with the lowest rank whose position matches.
     *
     * @param players  List of players to search
     * @param position Position code to filter by (e.g. "QB", "RB")
     * @return The best available player at that position, or null if none available
     */
    public Player getBestAvailableByPosition(List<Player> players, String position) {
        if (players == null || players.isEmpty()) {
            return null;
        }

        Player bestAvailable = null;
        int lowestRank = Integer.MAX_VALUE;

        for (Player player : players) {
            if (!player.isDrafted() && position.equals(player.getPosition()) && player.getRank() < lowestRank) {
                lowestRank = player.getRank();
                bestAvailable = player;
            }
        }

        return bestAvailable;
    }

    /**
     * Get the best available favorite player (highest-ranked undrafted favorite).
     *
     * @param players List of players to search
     * @return The best available favorite player, or null if none available
     */
    public Player getBestAvailableFavorite(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return null;
        }

        Player bestAvailable = null;
        int lowestRank = Integer.MAX_VALUE;

        for (Player player : players) {
            if (!player.isDrafted() && player.isFavorite() && player.getRank() < lowestRank) {
                lowestRank = player.getRank();
                bestAvailable = player;
            }
        }

        return bestAvailable;
    }

    /**
     * Get the best available player sorted by ADP (pffRank).
     * Players without ADP data are sorted to the end.
     */
    public Player getBestAvailableByAdp(List<Player> players) {
        if (players == null || players.isEmpty()) return null;
        
        Player bestAvailable = null;
        int lowestAdp = Integer.MAX_VALUE;
        
        for (Player player : players) {
            if (!player.isDrafted() && player.getPffRank() > 0 && player.getPffRank() < lowestAdp) {
                lowestAdp = player.getPffRank();
                bestAvailable = player;
            }
        }
        
        // Fallback to rank-based if no ADP data
        if (bestAvailable == null) return getBestAvailable(players);
        return bestAvailable;
    }

    /**
     * Get the best available player at a position sorted by ADP.
     */
    public Player getBestAvailableByPositionAdp(List<Player> players, String position) {
        if (players == null || players.isEmpty()) return null;
        
        Player bestAvailable = null;
        int lowestAdp = Integer.MAX_VALUE;
        
        for (Player player : players) {
            if (!player.isDrafted() && position.equals(player.getPosition()) 
                    && player.getPffRank() > 0 && player.getPffRank() < lowestAdp) {
                lowestAdp = player.getPffRank();
                bestAvailable = player;
            }
        }
        
        if (bestAvailable == null) return getBestAvailableByPosition(players, position);
        return bestAvailable;
    }

    /**
     * Get the best available favorite player sorted by ADP.
     */
    public Player getBestAvailableFavoriteByAdp(List<Player> players) {
        if (players == null || players.isEmpty()) return null;
        
        Player bestAvailable = null;
        int lowestAdp = Integer.MAX_VALUE;
        
        for (Player player : players) {
            if (!player.isDrafted() && player.isFavorite() 
                    && player.getPffRank() > 0 && player.getPffRank() < lowestAdp) {
                lowestAdp = player.getPffRank();
                bestAvailable = player;
            }
        }
        
        if (bestAvailable == null) return getBestAvailableFavorite(players);
        return bestAvailable;
    }



    /**
     * Mark a player as drafted by a specific team.
     * 
     * @param playerId The ID of the player to draft
     * @param teamId The ID of the team drafting the player
     * @throws IllegalArgumentException if player is not found, already drafted, or parameters are invalid
     */
    public void draftPlayer(String playerId, String teamId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }

        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty");
        }

        Player targetPlayer = null;
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            throw new IllegalArgumentException("Player not found with ID: " + playerId);
        }

        if (targetPlayer.isDrafted()) {
            throw new IllegalArgumentException("Player is already drafted");
        }

        targetPlayer.setDrafted(true);
        targetPlayer.setDraftedBy(teamId);
    }

    /**
     * Get all available (undrafted) players.
     * 
     * @param players List of players to filter
     * @return List of undrafted players
     */
    public List<Player> getAvailablePlayers(List<Player> players) {
        if (players == null) {
            return new ArrayList<>();
        }

        List<Player> availablePlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isDrafted()) {
                availablePlayers.add(player);
            }
        }

        return availablePlayers;
    }

    /**
     * Reset all players to undrafted state.
     */
    public void resetPlayers() {
        for (Player player : players) {
            player.setDrafted(false);
            player.setDraftedBy(null);
        }
    }

    /**
     * Get all players.
     * 
     * @return List of all players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Add a player to the pool.
     * 
     * @param player The player to add
     * @throws IllegalArgumentException if player is null
     */
    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        players.add(player);
    }

    /**
     * Clear all players.
     */
    public void clearPlayers() {
        players.clear();
    }

    /**
     * Get a player by ID.
     * 
     * @param playerId The player ID
     * @return The player, or null if not found
     */
    public Player getPlayerById(String playerId) {
        if (playerId == null) {
            return null;
        }

        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Remove all custom players (identified by rank = 999).
     * Custom players are temporary additions created during a draft session.
     * 
     * @return Number of custom players removed
     */
    public int removeCustomPlayers() {
        List<Player> customPlayers = new ArrayList<>();
        
        // Find all custom players (rank = 999)
        for (Player player : players) {
            if (player.getRank() == 999) {
                customPlayers.add(player);
            }
        }
        
        // Remove them from the player list
        players.removeAll(customPlayers);
        
        return customPlayers.size();
    }
}
