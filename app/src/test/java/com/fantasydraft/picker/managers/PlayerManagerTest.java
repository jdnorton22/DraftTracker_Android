package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.Player;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.generator.InRange;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class PlayerManagerTest {

    // Feature: fantasy-draft-picker, Property 8: Best Available Player Calculation
    // Validates: Requirements 5.1, 5.2, 5.3, 5.5
    @Property(trials = 100)
    public void bestAvailablePlayerCalculation(
            @InRange(minInt = 1, maxInt = 50) int playerCount,
            @InRange(minInt = 0, maxInt = 49) int draftedCount) {
        
        // Ensure we don't draft more players than we have
        if (draftedCount >= playerCount) {
            draftedCount = playerCount - 1;
        }
        
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(playerCount);
        
        // Draft some players (mark first draftedCount players as drafted)
        for (int i = 0; i < draftedCount; i++) {
            players.get(i).setDrafted(true);
            players.get(i).setDraftedBy("team1");
        }
        
        Player bestAvailable = playerManager.getBestAvailable(players);
        
        if (draftedCount < playerCount) {
            // Should return the lowest-ranked undrafted player
            assertNotNull("Best available should not be null when undrafted players exist", bestAvailable);
            assertFalse("Best available should not be drafted", bestAvailable.isDrafted());
            
            // Verify it's the lowest rank among undrafted players
            int lowestRank = bestAvailable.getRank();
            for (Player player : players) {
                if (!player.isDrafted()) {
                    assertTrue("Best available should have lowest rank among undrafted players",
                            lowestRank <= player.getRank());
                }
            }
            
            // Draft the best available player and verify the next best available updates
            String playerId = bestAvailable.getId();
            playerManager.addPlayer(bestAvailable);
            playerManager.draftPlayer(playerId, "team2");
            
            Player nextBestAvailable = playerManager.getBestAvailable(players);
            
            if (draftedCount + 1 < playerCount) {
                assertNotNull("Next best available should exist", nextBestAvailable);
                assertNotEquals("Next best available should be different player",
                        playerId, nextBestAvailable.getId());
                assertTrue("Next best available rank should be >= previous best",
                        nextBestAvailable.getRank() >= lowestRank);
            }
        } else {
            // All players drafted
            assertNull("Best available should be null when all players are drafted", bestAvailable);
        }
    }

    // Feature: fantasy-draft-picker, Property 9: Tiebreaker Consistency
    // Validates: Requirements 5.4
    @Property(trials = 100)
    public void tiebreakerConsistency(
            @InRange(minInt = 2, maxInt = 20) int tiedPlayerCount) {
        
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = new ArrayList<>();
        
        // Create multiple players with the same rank
        int tiedRank = 10;
        for (int i = 0; i < tiedPlayerCount; i++) {
            Player player = new Player(
                    "player" + i,
                    "Player " + i,
                    "RB",
                    tiedRank
            );
            players.add(player);
        }
        
        // Get best available multiple times
        Player firstCall = playerManager.getBestAvailable(players);
        Player secondCall = playerManager.getBestAvailable(players);
        Player thirdCall = playerManager.getBestAvailable(players);
        
        assertNotNull("Best available should not be null", firstCall);
        assertNotNull("Best available should not be null on second call", secondCall);
        assertNotNull("Best available should not be null on third call", thirdCall);
        
        // Verify consistency - should always return the same player
        assertEquals("Tiebreaker should be consistent across calls",
                firstCall.getId(), secondCall.getId());
        assertEquals("Tiebreaker should be consistent across calls",
                firstCall.getId(), thirdCall.getId());
    }

    // Feature: fantasy-draft-picker, Property 11: Player Draft State Update
    // Validates: Requirements 7.2, 7.3
    @Property(trials = 100)
    public void playerDraftStateUpdate(
            @InRange(minInt = 1, maxInt = 50) int playerCount,
            @InRange(minInt = 0, maxInt = 49) int playerIndex) {
        
        // Ensure valid index
        if (playerIndex >= playerCount) {
            playerIndex = playerCount - 1;
        }
        
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(playerCount);
        
        // Add all players to manager
        for (Player player : players) {
            playerManager.addPlayer(player);
        }
        
        Player targetPlayer = players.get(playerIndex);
        String playerId = targetPlayer.getId();
        String teamId = "team123";
        
        // Verify player is initially undrafted
        assertFalse("Player should start undrafted", targetPlayer.isDrafted());
        assertNull("Player should not have a team initially", targetPlayer.getDraftedBy());
        
        // Draft the player
        playerManager.draftPlayer(playerId, teamId);
        
        // Verify player is now drafted
        assertTrue("Player should be marked as drafted", targetPlayer.isDrafted());
        assertEquals("Player should be associated with the team", teamId, targetPlayer.getDraftedBy());
        
        // Verify the player is no longer in available players list
        List<Player> availablePlayers = playerManager.getAvailablePlayers(players);
        for (Player available : availablePlayers) {
            assertNotEquals("Drafted player should not be in available list",
                    playerId, available.getId());
        }
    }

    // Feature: fantasy-draft-picker, Property 12: Drafted Player Rejection
    // Validates: Requirements 7.4
    @Property(trials = 100)
    public void draftedPlayerRejection(
            @InRange(minInt = 1, maxInt = 50) int playerCount,
            @InRange(minInt = 0, maxInt = 49) int playerIndex) {
        
        // Ensure valid index
        if (playerIndex >= playerCount) {
            playerIndex = playerCount - 1;
        }
        
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(playerCount);
        
        // Add all players to manager
        for (Player player : players) {
            playerManager.addPlayer(player);
        }
        
        Player targetPlayer = players.get(playerIndex);
        String playerId = targetPlayer.getId();
        
        // Draft the player once
        playerManager.draftPlayer(playerId, "team1");
        
        // Verify player is drafted
        assertTrue("Player should be drafted", targetPlayer.isDrafted());
        
        // Attempt to draft the same player again - should throw exception
        try {
            playerManager.draftPlayer(playerId, "team2");
            fail("Should throw IllegalArgumentException when drafting already-drafted player");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue("Exception message should indicate player is already drafted",
                    e.getMessage().contains("already drafted"));
        }
        
        // Verify player is still associated with original team
        assertEquals("Player should still be associated with original team",
                "team1", targetPlayer.getDraftedBy());
    }

    // Unit Tests for Edge Cases
    
    @Test
    public void testGetBestAvailable_EmptyList() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = new ArrayList<>();
        
        Player bestAvailable = playerManager.getBestAvailable(players);
        
        assertNull("Best available should be null for empty list", bestAvailable);
    }
    
    @Test
    public void testGetBestAvailable_NullList() {
        PlayerManager playerManager = new PlayerManager();
        
        Player bestAvailable = playerManager.getBestAvailable(null);
        
        assertNull("Best available should be null for null list", bestAvailable);
    }
    
    @Test
    public void testGetBestAvailable_AllDrafted() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(10);
        
        // Draft all players
        for (Player player : players) {
            player.setDrafted(true);
            player.setDraftedBy("team1");
        }
        
        Player bestAvailable = playerManager.getBestAvailable(players);
        
        assertNull("Best available should be null when all players are drafted", bestAvailable);
    }
    
    @Test
    public void testGetAvailablePlayers_EmptyList() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = new ArrayList<>();
        
        List<Player> available = playerManager.getAvailablePlayers(players);
        
        assertNotNull("Available players list should not be null", available);
        assertTrue("Available players list should be empty", available.isEmpty());
    }
    
    @Test
    public void testGetAvailablePlayers_NullList() {
        PlayerManager playerManager = new PlayerManager();
        
        List<Player> available = playerManager.getAvailablePlayers(null);
        
        assertNotNull("Available players list should not be null", available);
        assertTrue("Available players list should be empty", available.isEmpty());
    }
    
    @Test
    public void testGetAvailablePlayers_MixedDraftStatus() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(10);
        
        // Draft half the players
        for (int i = 0; i < 5; i++) {
            players.get(i).setDrafted(true);
            players.get(i).setDraftedBy("team1");
        }
        
        List<Player> available = playerManager.getAvailablePlayers(players);
        
        assertEquals("Should have 5 available players", 5, available.size());
        for (Player player : available) {
            assertFalse("Available players should not be drafted", player.isDrafted());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDraftPlayer_NullPlayerId() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.draftPlayer(null, "team1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDraftPlayer_EmptyPlayerId() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.draftPlayer("", "team1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDraftPlayer_NullTeamId() {
        PlayerManager playerManager = new PlayerManager();
        Player player = new Player("player1", "Player 1", "QB", 1);
        playerManager.addPlayer(player);
        playerManager.draftPlayer("player1", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDraftPlayer_EmptyTeamId() {
        PlayerManager playerManager = new PlayerManager();
        Player player = new Player("player1", "Player 1", "QB", 1);
        playerManager.addPlayer(player);
        playerManager.draftPlayer("player1", "");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDraftPlayer_PlayerNotFound() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.draftPlayer("nonexistent", "team1");
    }
    
    @Test
    public void testResetPlayers() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = createPlayers(10);
        
        // Add players to manager
        for (Player player : players) {
            playerManager.addPlayer(player);
        }
        
        // Draft some players
        for (int i = 0; i < 5; i++) {
            players.get(i).setDrafted(true);
            players.get(i).setDraftedBy("team1");
        }
        
        // Reset all players
        playerManager.resetPlayers();
        
        // Verify all players are undrafted
        for (Player player : playerManager.getPlayers()) {
            assertFalse("Player should be undrafted after reset", player.isDrafted());
            assertNull("Player should not have a team after reset", player.getDraftedBy());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddPlayer_Null() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.addPlayer(null);
    }
    
    @Test
    public void testGetPlayerById_Found() {
        PlayerManager playerManager = new PlayerManager();
        Player player = new Player("player1", "Player 1", "QB", 1);
        playerManager.addPlayer(player);
        
        Player found = playerManager.getPlayerById("player1");
        
        assertNotNull("Player should be found", found);
        assertEquals("Should return correct player", "player1", found.getId());
    }
    
    @Test
    public void testGetPlayerById_NotFound() {
        PlayerManager playerManager = new PlayerManager();
        
        Player found = playerManager.getPlayerById("nonexistent");
        
        assertNull("Player should not be found", found);
    }
    
    @Test
    public void testGetPlayerById_Null() {
        PlayerManager playerManager = new PlayerManager();
        
        Player found = playerManager.getPlayerById(null);
        
        assertNull("Player should not be found for null ID", found);
    }

    // Helper method to create players with sequential ranks
    private List<Player> createPlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Player player = new Player(
                    UUID.randomUUID().toString(),
                    "Player " + i,
                    "RB",
                    i + 1  // Rank from 1 to count
            );
            players.add(player);
        }
        return players;
    }
}
