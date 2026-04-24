package com.fantasydraft.picker.integration;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fantasydraft.picker.managers.DraftCoordinator;
import com.fantasydraft.picker.managers.DraftManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.managers.TeamManager;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * End-to-end integration test for complete draft flow.
 * Tests configuration through multiple rounds of drafting.
 * 
 * Requirements: All
 */
@RunWith(AndroidJUnit4.class)
public class EndToEndDraftFlowTest {
    
    private Context context;
    private TeamManager teamManager;
    private PlayerManager playerManager;
    private DraftManager draftManager;
    private DraftCoordinator draftCoordinator;
    
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        
        // Initialize managers
        teamManager = new TeamManager();
        playerManager = new PlayerManager();
        draftManager = new DraftManager();
        draftCoordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
    }
    
    /**
     * Test complete draft flow with serpentine draft.
     * Configures 4 teams, drafts through 3 rounds, verifies all state.
     */
    @Test
    public void testCompleteDraftFlowSerpentine() {
        // Step 1: Configure teams (Requirements 1.1, 1.2, 1.3)
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team Alpha", 1));
        teams.add(new Team("team2", "Team Beta", 2));
        teams.add(new Team("team3", "Team Gamma", 3));
        teams.add(new Team("team4", "Team Delta", 4));
        
        // Validate team configuration
        assertTrue("Team count should be valid", teams.size() >= 2 && teams.size() <= 20);
        assertTrue("Team names should be unique", teamManager.validateTeamName("Team Alpha", teams));
        assertTrue("Draft order should be complete", teamManager.validateDraftOrder(teams));
        
        // Step 2: Configure draft settings (Requirements 3.1, 3.2)
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 3);
        assertEquals("Flow type should be serpentine", FlowType.SERPENTINE, config.getFlowType());
        assertEquals("Number of rounds should be 3", 3, config.getNumberOfRounds());
        
        // Step 3: Set up player pool (Requirement 5.1)
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            players.add(new Player("player" + i, "Player " + i, "RB", i));
        }
        for (Player player : players) {
            playerManager.addPlayer(player);
        }
        
        // Step 4: Initialize draft state (Requirement 4.1)
        DraftState state = new DraftState(1, 1, false);
        assertEquals("Should start at round 1", 1, state.getCurrentRound());
        assertEquals("Should start at pick 1", 1, state.getCurrentPickInRound());
        assertFalse("Draft should not be complete", state.isComplete());
        
        // Step 5: Execute Round 1 (serpentine forward: 1, 2, 3, 4)
        // Pick 1: Team Alpha drafts Player 1
        int teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("First pick should be team 0", 0, teamIndex);
        Team currentTeam = teams.get(teamIndex);
        assertEquals("Current team should be Team Alpha", "Team Alpha", currentTeam.getName());
        
        Player bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
        assertNotNull("Best available player should exist", bestAvailable);
        assertEquals("Best available should be Player 1", "Player 1", bestAvailable.getName());
        
        playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
        Pick pick1 = new Pick(1, 1, 1, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis());
        draftManager.addPickToHistory(pick1);
        state = draftManager.advancePick(state, config, teams.size());
        
        // Pick 2: Team Beta drafts Player 2
        teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("Second pick should be team 1", 1, teamIndex);
        currentTeam = teams.get(teamIndex);
        bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
        assertEquals("Best available should be Player 2", "Player 2", bestAvailable.getName());
        playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
        Pick pick2 = new Pick(2, 1, 2, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis());
        draftManager.addPickToHistory(pick2);
        state = draftManager.advancePick(state, config, teams.size());
        
        // Pick 3: Team Gamma drafts Player 3
        teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("Third pick should be team 2", 2, teamIndex);
        currentTeam = teams.get(teamIndex);
        bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
        playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
        Pick pick3 = new Pick(3, 1, 3, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis());
        draftManager.addPickToHistory(pick3);
        state = draftManager.advancePick(state, config, teams.size());
        
        // Pick 4: Team Delta drafts Player 4
        teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("Fourth pick should be team 3", 3, teamIndex);
        currentTeam = teams.get(teamIndex);
        bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
        playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
        Pick pick4 = new Pick(4, 1, 4, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis());
        draftManager.addPickToHistory(pick4);
        state = draftManager.advancePick(state, config, teams.size());
        
        // Verify round transition (Requirement 4.3)
        assertEquals("Should be in round 2", 2, state.getCurrentRound());
        assertEquals("Should be at pick 1 of round 2", 1, state.getCurrentPickInRound());
        
        // Step 6: Execute Round 2 (serpentine reverse: 4, 3, 2, 1)
        // Pick 5: Team Delta drafts Player 5
        teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("First pick of round 2 should be team 3 (serpentine)", 3, teamIndex);
        currentTeam = teams.get(teamIndex);
        bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
        playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
        draftManager.addPickToHistory(new Pick(5, 2, 1, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis()));
        state = draftManager.advancePick(state, config, teams.size());
        
        // Continue through round 2
        for (int i = 2; i <= 4; i++) {
            teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
            currentTeam = teams.get(teamIndex);
            bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
            playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
            draftManager.addPickToHistory(new Pick(4 + i, 2, i, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis()));
            state = draftManager.advancePick(state, config, teams.size());
        }
        
        // Verify round 3 transition
        assertEquals("Should be in round 3", 3, state.getCurrentRound());
        
        // Step 7: Execute Round 3 (serpentine forward: 1, 2, 3, 4)
        for (int i = 1; i <= 4; i++) {
            teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
            currentTeam = teams.get(teamIndex);
            bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
            playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
            draftManager.addPickToHistory(new Pick(8 + i, 3, i, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis()));
            
            if (i < 4) {
                state = draftManager.advancePick(state, config, teams.size());
            }
        }
        
        // Step 8: Verify draft history (Requirements 8.1, 8.2, 8.3, 8.4)
        List<Pick> history = draftManager.getPickHistory();
        assertEquals("History should contain 12 picks", 12, history.size());
        
        // Verify first pick
        Pick firstPick = history.get(0);
        assertEquals("First pick should be pick number 1", 1, firstPick.getPickNumber());
        assertEquals("First pick should be round 1", 1, firstPick.getRound());
        assertEquals("First pick should be Team Alpha", "team1", firstPick.getTeamId());
        
        // Verify serpentine pattern in history
        assertEquals("Pick 5 should be Team Delta (serpentine reverse)", "team4", history.get(4).getTeamId());
        assertEquals("Pick 9 should be Team Alpha (serpentine forward)", "team1", history.get(8).getTeamId());
        
        // Step 9: Verify all players drafted correctly (Requirements 7.2, 7.3)
        List<Player> availablePlayers = playerManager.getAvailablePlayers(playerManager.getPlayers());
        assertEquals("No players should be available", 0, availablePlayers.size());
        
        for (Player player : playerManager.getPlayers()) {
            assertTrue("All players should be drafted", player.isDrafted());
            assertNotNull("All players should have a team", player.getDraftedBy());
        }
        
        // Step 10: Test draft reset (Requirements 9.1, 9.2, 9.3, 9.4)
        DraftState resetState = draftCoordinator.resetDraft(config);
        
        assertEquals("Reset should return to round 1", 1, resetState.getCurrentRound());
        assertEquals("Reset should return to pick 1", 1, resetState.getCurrentPickInRound());
        
        List<Pick> historyAfterReset = draftManager.getPickHistory();
        assertEquals("History should be cleared", 0, historyAfterReset.size());
        
        List<Player> availableAfterReset = playerManager.getAvailablePlayers(playerManager.getPlayers());
        assertEquals("All players should be available after reset", 12, availableAfterReset.size());
        
        // Verify configuration preserved
        assertEquals("Flow type should be preserved", FlowType.SERPENTINE, config.getFlowType());
        assertEquals("Number of rounds should be preserved", 3, config.getNumberOfRounds());
        assertEquals("Team count should be preserved", 4, teams.size());
    }
    
    /**
     * Test complete draft flow with linear draft.
     * Verifies linear flow maintains consistent order across rounds.
     */
    @Test
    public void testCompleteDraftFlowLinear() {
        // Configure 3 teams
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team One", 1));
        teams.add(new Team("team2", "Team Two", 2));
        teams.add(new Team("team3", "Team Three", 3));
        
        // Configure linear draft
        DraftConfig config = new DraftConfig(FlowType.LINEAR, 2);
        
        // Set up player pool
        for (int i = 1; i <= 6; i++) {
            playerManager.addPlayer(new Player("player" + i, "Player " + i, "WR", i));
        }
        
        // Initialize draft
        DraftState state = new DraftState(1, 1, false);
        
        // Execute Round 1 (linear: 1, 2, 3)
        for (int i = 1; i <= 3; i++) {
            int teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
            assertEquals("Linear round 1 pick " + i + " should be team " + (i-1), i-1, teamIndex);
            
            Team currentTeam = teams.get(teamIndex);
            Player bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
            playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
            draftManager.addPickToHistory(new Pick(i, 1, i, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis()));
            state = draftManager.advancePick(state, config, teams.size());
        }
        
        // Verify round 2 starts with team 1 (linear maintains order)
        assertEquals("Should be in round 2", 2, state.getCurrentRound());
        int teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
        assertEquals("Linear round 2 should start with team 0", 0, teamIndex);
        
        // Execute Round 2 (linear: 1, 2, 3)
        for (int i = 1; i <= 3; i++) {
            teamIndex = draftManager.getCurrentTeamIndex(state, config, teams.size());
            assertEquals("Linear round 2 pick " + i + " should be team " + (i-1), i-1, teamIndex);
            
            Team currentTeam = teams.get(teamIndex);
            Player bestAvailable = playerManager.getBestAvailable(playerManager.getPlayers());
            playerManager.draftPlayer(bestAvailable.getId(), currentTeam.getId());
            draftManager.addPickToHistory(new Pick(3 + i, 2, i, currentTeam.getId(), bestAvailable.getId(), System.currentTimeMillis()));
            
            if (i < 3) {
                state = draftManager.advancePick(state, config, teams.size());
            }
        }
        
        // Verify history shows linear pattern
        List<Pick> history = draftManager.getPickHistory();
        assertEquals("History should contain 6 picks", 6, history.size());
        assertEquals("Pick 4 should be Team One (linear)", "team1", history.get(3).getTeamId());
        assertEquals("Pick 5 should be Team Two (linear)", "team2", history.get(4).getTeamId());
    }
}
