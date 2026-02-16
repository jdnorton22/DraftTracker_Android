package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.generator.InRange;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class DraftCoordinatorTest {

    // Feature: fantasy-draft-picker, Property 15: Draft Reset Preserves Configuration
    // Validates: Requirements 9.4
    @Property(trials = 100)
    public void draftResetPreservesConfiguration(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 10) int numberOfRounds,
            @InRange(minInt = 0, maxInt = 1) int flowTypeIndex) {
        
        // Create managers
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Create teams with specific names and positions
        List<Team> originalTeams = new ArrayList<>();
        for (int i = 1; i <= teamCount; i++) {
            Team team = teamManager.addTeam("Team " + i, i);
            originalTeams.add(team);
        }
        
        // Create draft config
        FlowType flowType = (flowTypeIndex == 0) ? FlowType.SERPENTINE : FlowType.LINEAR;
        DraftConfig config = new DraftConfig(flowType, numberOfRounds);
        
        // Create players and make some picks
        for (int i = 1; i <= 10; i++) {
            Player player = new Player("player" + i, "Player " + i, "RB", i);
            playerManager.addPlayer(player);
        }
        
        // Make some picks
        List<Player> players = playerManager.getPlayers();
        int pickCount = Math.min(5, players.size());
        for (int i = 0; i < pickCount; i++) {
            Player player = players.get(i);
            String teamId = originalTeams.get(i % teamCount).getId();
            playerManager.draftPlayer(player.getId(), teamId);
            
            Pick pick = new Pick(i + 1, 1, i + 1, teamId, player.getId(), System.currentTimeMillis());
            draftManager.addPickToHistory(pick);
        }
        
        // Reset the draft
        coordinator.resetDraft(config);
        
        // Verify team configuration is preserved (Requirement 9.4)
        List<Team> teamsAfterReset = teamManager.getTeams();
        assertEquals("Team count should be preserved", teamCount, teamsAfterReset.size());
        
        for (int i = 0; i < teamCount; i++) {
            Team original = originalTeams.get(i);
            Team afterReset = teamsAfterReset.get(i);
            
            assertEquals("Team ID should be preserved", original.getId(), afterReset.getId());
            assertEquals("Team name should be preserved", original.getName(), afterReset.getName());
            assertEquals("Team draft position should be preserved", original.getDraftPosition(), afterReset.getDraftPosition());
        }
        
        // Verify draft config is preserved (passed in, not modified)
        assertEquals("Flow type should be preserved", flowType, config.getFlowType());
        assertEquals("Number of rounds should be preserved", numberOfRounds, config.getNumberOfRounds());
        
        // Verify player pool is preserved (players exist, just reset to undrafted)
        List<Player> playersAfterReset = playerManager.getPlayers();
        assertEquals("Player count should be preserved", players.size(), playersAfterReset.size());
        
        for (int i = 0; i < players.size(); i++) {
            Player original = players.get(i);
            Player afterReset = playersAfterReset.get(i);
            
            assertEquals("Player ID should be preserved", original.getId(), afterReset.getId());
            assertEquals("Player name should be preserved", original.getName(), afterReset.getName());
            assertEquals("Player position should be preserved", original.getPosition(), afterReset.getPosition());
            assertEquals("Player rank should be preserved", original.getRank(), afterReset.getRank());
        }
    }

    // Feature: fantasy-draft-picker, Property 14: Draft Reset Clears Picks
    // Validates: Requirements 9.1, 9.2, 9.3
    @Property(trials = 100)
    public void draftResetClearsPicks(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 20) int numberOfPicks,
            @InRange(minInt = 1, maxInt = 10) int numberOfRounds) {
        
        // Create managers
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Create teams
        for (int i = 1; i <= teamCount; i++) {
            teamManager.addTeam("Team " + i, i);
        }
        
        // Create players
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= numberOfPicks; i++) {
            Player player = new Player("player" + i, "Player " + i, "RB", i);
            playerManager.addPlayer(player);
            players.add(player);
        }
        
        // Simulate some picks being made
        for (int i = 0; i < numberOfPicks && i < players.size(); i++) {
            Player player = players.get(i);
            String teamId = "team" + ((i % teamCount) + 1);
            
            // Draft the player
            playerManager.draftPlayer(player.getId(), teamId);
            
            // Add to history
            Pick pick = new Pick(
                i + 1,
                (i / teamCount) + 1,
                (i % teamCount) + 1,
                teamId,
                player.getId(),
                System.currentTimeMillis() + i
            );
            draftManager.addPickToHistory(pick);
        }
        
        // Verify picks were added
        assertEquals("History should contain all picks", numberOfPicks, draftManager.getPickHistory().size());
        
        // Verify players were drafted
        int draftedCount = 0;
        for (Player player : playerManager.getPlayers()) {
            if (player.isDrafted()) {
                draftedCount++;
            }
        }
        assertEquals("All picked players should be drafted", numberOfPicks, draftedCount);
        
        // Create draft config
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, numberOfRounds);
        
        // Reset the draft
        DraftState resetState = coordinator.resetDraft(config);
        
        // Verify picks are cleared (Requirement 9.1)
        assertEquals("Pick history should be empty after reset", 0, draftManager.getPickHistory().size());
        
        // Verify all players are marked as available (Requirement 9.2)
        for (Player player : playerManager.getPlayers()) {
            assertFalse("Player " + player.getId() + " should not be drafted after reset", player.isDrafted());
            assertNull("Player " + player.getId() + " should have no team after reset", player.getDraftedBy());
        }
        
        // Verify current pick is at position 1 of round 1 (Requirement 9.3)
        assertEquals("Should be at round 1 after reset", 1, resetState.getCurrentRound());
        assertEquals("Should be at pick 1 after reset", 1, resetState.getCurrentPickInRound());
        assertFalse("Draft should not be complete after reset", resetState.isComplete());
    }

    // Unit Tests for Reset Edge Cases
    // Requirements: 9.1, 9.2, 9.3, 9.4
    
    @Test
    public void testResetWithNoPicks() {
        // Test reset with no picks made
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Add teams
        teamManager.addTeam("Team A", 1);
        teamManager.addTeam("Team B", 2);
        
        // Add players
        playerManager.addPlayer(new Player("p1", "Player 1", "QB", 1));
        playerManager.addPlayer(new Player("p2", "Player 2", "RB", 2));
        
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        // Reset with no picks
        DraftState resetState = coordinator.resetDraft(config);
        
        assertEquals("History should be empty", 0, draftManager.getPickHistory().size());
        assertEquals("Should be at round 1", 1, resetState.getCurrentRound());
        assertEquals("Should be at pick 1", 1, resetState.getCurrentPickInRound());
        assertFalse("Should not be complete", resetState.isComplete());
        
        // Verify all players are still available
        for (Player player : playerManager.getPlayers()) {
            assertFalse("Player should not be drafted", player.isDrafted());
        }
    }
    
    @Test
    public void testResetMidDraft() {
        // Test reset in the middle of a draft
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Add teams
        teamManager.addTeam("Team A", 1);
        teamManager.addTeam("Team B", 2);
        teamManager.addTeam("Team C", 3);
        
        // Add players
        for (int i = 1; i <= 10; i++) {
            playerManager.addPlayer(new Player("p" + i, "Player " + i, "RB", i));
        }
        
        // Make 5 picks (mid-draft)
        List<Player> players = playerManager.getPlayers();
        for (int i = 0; i < 5; i++) {
            Player player = players.get(i);
            String teamId = "team" + ((i % 3) + 1);
            playerManager.draftPlayer(player.getId(), teamId);
            
            Pick pick = new Pick(i + 1, 1, i + 1, teamId, player.getId(), System.currentTimeMillis());
            draftManager.addPickToHistory(pick);
        }
        
        assertEquals("Should have 5 picks", 5, draftManager.getPickHistory().size());
        
        DraftConfig config = new DraftConfig(FlowType.LINEAR, 15);
        
        // Reset mid-draft
        DraftState resetState = coordinator.resetDraft(config);
        
        assertEquals("History should be cleared", 0, draftManager.getPickHistory().size());
        assertEquals("Should be at round 1", 1, resetState.getCurrentRound());
        assertEquals("Should be at pick 1", 1, resetState.getCurrentPickInRound());
        
        // Verify all players are available again
        for (Player player : playerManager.getPlayers()) {
            assertFalse("Player should be available after reset", player.isDrafted());
            assertNull("Player should have no team after reset", player.getDraftedBy());
        }
    }
    
    @Test
    public void testMultipleResets() {
        // Test multiple consecutive resets
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Add teams
        teamManager.addTeam("Team A", 1);
        teamManager.addTeam("Team B", 2);
        
        // Add players
        playerManager.addPlayer(new Player("p1", "Player 1", "QB", 1));
        playerManager.addPlayer(new Player("p2", "Player 2", "RB", 2));
        
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        // First reset
        DraftState state1 = coordinator.resetDraft(config);
        assertEquals(1, state1.getCurrentRound());
        assertEquals(1, state1.getCurrentPickInRound());
        
        // Make a pick
        Player player = playerManager.getPlayers().get(0);
        playerManager.draftPlayer(player.getId(), "team1");
        Pick pick = new Pick(1, 1, 1, "team1", player.getId(), System.currentTimeMillis());
        draftManager.addPickToHistory(pick);
        
        // Second reset
        DraftState state2 = coordinator.resetDraft(config);
        assertEquals(1, state2.getCurrentRound());
        assertEquals(1, state2.getCurrentPickInRound());
        assertEquals(0, draftManager.getPickHistory().size());
        
        // Third reset (with no picks)
        DraftState state3 = coordinator.resetDraft(config);
        assertEquals(1, state3.getCurrentRound());
        assertEquals(1, state3.getCurrentPickInRound());
        assertEquals(0, draftManager.getPickHistory().size());
        
        // All players should still be available
        for (Player p : playerManager.getPlayers()) {
            assertFalse("Player should be available after multiple resets", p.isDrafted());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testResetWithNullConfig() {
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        DraftCoordinator coordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        coordinator.resetDraft(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullDraftManager() {
        PlayerManager playerManager = new PlayerManager();
        TeamManager teamManager = new TeamManager();
        new DraftCoordinator(null, playerManager, teamManager);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullPlayerManager() {
        DraftManager draftManager = new DraftManager();
        TeamManager teamManager = new TeamManager();
        new DraftCoordinator(draftManager, null, teamManager);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullTeamManager() {
        DraftManager draftManager = new DraftManager();
        PlayerManager playerManager = new PlayerManager();
        new DraftCoordinator(draftManager, playerManager, null);
    }
}
