package com.fantasydraft.picker.integration;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fantasydraft.picker.managers.DraftManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.managers.TeamManager;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftSnapshot;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.persistence.PersistenceException;
import com.fantasydraft.picker.persistence.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for persistence across simulated app restart.
 * Tests save/load functionality with complete draft state.
 * 
 * Requirements: 6.1, 6.2, 6.3
 */
@RunWith(AndroidJUnit4.class)
public class PersistenceIntegrationTest {
    
    private Context context;
    private PersistenceManager persistenceManager;
    
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        persistenceManager = new PersistenceManager(context);
        
        // Clear any existing draft data
        persistenceManager.clearDraft();
    }
    
    @After
    public void tearDown() {
        // Clean up after tests
        if (persistenceManager != null) {
            persistenceManager.clearDraft();
        }
    }
    
    /**
     * Test save and load of complete draft state across simulated restart.
     * Simulates app restart by creating new persistence manager instance.
     */
    @Test
    public void testSaveAndLoadAcrossRestart() throws PersistenceException {
        // Step 1: Create initial draft state
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team Alpha", 1));
        teams.add(new Team("team2", "Team Beta", 2));
        teams.add(new Team("team3", "Team Gamma", 3));
        
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", "Player One", "QB", 1));
        players.add(new Player("player2", "Player Two", "RB", 2));
        players.add(new Player("player3", "Player Three", "WR", 3));
        players.add(new Player("player4", "Player Four", "TE", 4));
        
        // Draft some players
        players.get(0).setDrafted(true);
        players.get(0).setDraftedBy("team1");
        players.get(1).setDrafted(true);
        players.get(1).setDraftedBy("team2");
        
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 5);
        DraftState state = new DraftState(1, 3, false);
        
        List<Pick> pickHistory = new ArrayList<>();
        pickHistory.add(new Pick(1, 1, 1, "team1", "player1", System.currentTimeMillis()));
        pickHistory.add(new Pick(2, 1, 2, "team2", "player2", System.currentTimeMillis()));
        
        DraftSnapshot originalSnapshot = new DraftSnapshot(
            teams,
            players,
            state,
            config,
            pickHistory,
            System.currentTimeMillis()
        );
        
        // Step 2: Save draft state (Requirement 6.1, 6.2)
        persistenceManager.saveDraft(originalSnapshot);
        
        // Step 3: Simulate app restart by creating new persistence manager
        PersistenceManager newPersistenceManager = new PersistenceManager(context);
        
        // Step 4: Load draft state (Requirement 6.3)
        DraftSnapshot loadedSnapshot = newPersistenceManager.loadDraft();
        
        // Step 5: Verify all data was preserved
        assertNotNull("Loaded snapshot should not be null", loadedSnapshot);
        
        // Verify teams
        assertNotNull("Teams should be loaded", loadedSnapshot.getTeams());
        assertEquals("Team count should match", 3, loadedSnapshot.getTeams().size());
        assertEquals("First team name should match", "Team Alpha", loadedSnapshot.getTeams().get(0).getName());
        assertEquals("First team position should match", 1, loadedSnapshot.getTeams().get(0).getDraftPosition());
        assertEquals("Second team name should match", "Team Beta", loadedSnapshot.getTeams().get(1).getName());
        assertEquals("Third team name should match", "Team Gamma", loadedSnapshot.getTeams().get(2).getName());
        
        // Verify players
        assertNotNull("Players should be loaded", loadedSnapshot.getPlayers());
        assertEquals("Player count should match", 4, loadedSnapshot.getPlayers().size());
        
        Player loadedPlayer1 = loadedSnapshot.getPlayers().get(0);
        assertEquals("Player 1 name should match", "Player One", loadedPlayer1.getName());
        assertEquals("Player 1 position should match", "QB", loadedPlayer1.getPosition());
        assertEquals("Player 1 rank should match", 1, loadedPlayer1.getRank());
        assertTrue("Player 1 should be drafted", loadedPlayer1.isDrafted());
        assertEquals("Player 1 drafted by should match", "team1", loadedPlayer1.getDraftedBy());
        
        Player loadedPlayer2 = loadedSnapshot.getPlayers().get(1);
        assertTrue("Player 2 should be drafted", loadedPlayer2.isDrafted());
        assertEquals("Player 2 drafted by should match", "team2", loadedPlayer2.getDraftedBy());
        
        Player loadedPlayer3 = loadedSnapshot.getPlayers().get(2);
        assertFalse("Player 3 should not be drafted", loadedPlayer3.isDrafted());
        assertNull("Player 3 should have no team", loadedPlayer3.getDraftedBy());
        
        // Verify draft state
        assertNotNull("Draft state should be loaded", loadedSnapshot.getDraftState());
        assertEquals("Current round should match", 1, loadedSnapshot.getDraftState().getCurrentRound());
        assertEquals("Current pick should match", 3, loadedSnapshot.getDraftState().getCurrentPickInRound());
        assertFalse("Draft complete flag should match", loadedSnapshot.getDraftState().isComplete());
        
        // Verify draft config
        assertNotNull("Draft config should be loaded", loadedSnapshot.getDraftConfig());
        assertEquals("Flow type should match", FlowType.SERPENTINE, loadedSnapshot.getDraftConfig().getFlowType());
        assertEquals("Number of rounds should match", 5, loadedSnapshot.getDraftConfig().getNumberOfRounds());
        
        // Verify pick history
        assertNotNull("Pick history should be loaded", loadedSnapshot.getPickHistory());
        assertEquals("Pick history count should match", 2, loadedSnapshot.getPickHistory().size());
        
        Pick loadedPick1 = loadedSnapshot.getPickHistory().get(0);
        assertEquals("Pick 1 number should match", 1, loadedPick1.getPickNumber());
        assertEquals("Pick 1 round should match", 1, loadedPick1.getRound());
        assertEquals("Pick 1 team should match", "team1", loadedPick1.getTeamId());
        assertEquals("Pick 1 player should match", "player1", loadedPick1.getPlayerId());
        
        Pick loadedPick2 = loadedSnapshot.getPickHistory().get(1);
        assertEquals("Pick 2 number should match", 2, loadedPick2.getPickNumber());
        assertEquals("Pick 2 team should match", "team2", loadedPick2.getTeamId());
    }
    
    /**
     * Test multiple save/load cycles maintain data integrity.
     */
    @Test
    public void testMultipleSaveLoadCycles() throws PersistenceException {
        // Create initial state
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team One", 1));
        teams.add(new Team("team2", "Team Two", 2));
        
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", "Player A", "QB", 1));
        
        DraftConfig config = new DraftConfig(FlowType.LINEAR, 3);
        DraftState state = new DraftState(1, 1, false);
        
        DraftSnapshot snapshot1 = new DraftSnapshot(
            teams,
            players,
            state,
            config,
            new ArrayList<>(),
            System.currentTimeMillis()
        );
        
        // First save/load cycle
        persistenceManager.saveDraft(snapshot1);
        DraftSnapshot loaded1 = persistenceManager.loadDraft();
        assertNotNull("First load should succeed", loaded1);
        assertEquals("Team count should match after first cycle", 2, loaded1.getTeams().size());
        
        // Modify state
        loaded1.getDraftState().setCurrentRound(2);
        loaded1.getDraftState().setCurrentPickInRound(1);
        loaded1.getPlayers().get(0).setDrafted(true);
        loaded1.getPlayers().get(0).setDraftedBy("team1");
        
        // Second save/load cycle
        persistenceManager.saveDraft(loaded1);
        DraftSnapshot loaded2 = persistenceManager.loadDraft();
        assertNotNull("Second load should succeed", loaded2);
        assertEquals("Round should be updated", 2, loaded2.getDraftState().getCurrentRound());
        assertTrue("Player should be drafted", loaded2.getPlayers().get(0).isDrafted());
        
        // Third save/load cycle with more changes
        loaded2.getPickHistory().add(new Pick(1, 1, 1, "team1", "player1", System.currentTimeMillis()));
        persistenceManager.saveDraft(loaded2);
        DraftSnapshot loaded3 = persistenceManager.loadDraft();
        assertNotNull("Third load should succeed", loaded3);
        assertEquals("Pick history should be preserved", 1, loaded3.getPickHistory().size());
    }
    
    /**
     * Test loading when no saved draft exists.
     */
    @Test
    public void testLoadWhenNoDraftExists() throws PersistenceException {
        // Ensure no draft exists
        persistenceManager.clearDraft();
        
        // Attempt to load
        DraftSnapshot loaded = persistenceManager.loadDraft();
        
        // Should return null or empty snapshot
        assertNull("Should return null when no draft exists", loaded);
    }
    
    /**
     * Test clear draft functionality.
     */
    @Test
    public void testClearDraft() throws PersistenceException {
        // Create and save a draft
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team One", 1));
        
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", "Player A", "QB", 1));
        
        DraftSnapshot snapshot = new DraftSnapshot(
            teams,
            players,
            new DraftState(1, 1, false),
            new DraftConfig(FlowType.SERPENTINE, 5),
            new ArrayList<>(),
            System.currentTimeMillis()
        );
        
        persistenceManager.saveDraft(snapshot);
        
        // Verify it was saved
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNotNull("Draft should be saved", loaded);
        
        // Clear the draft
        persistenceManager.clearDraft();
        
        // Verify it was cleared
        DraftSnapshot loadedAfterClear = persistenceManager.loadDraft();
        assertNull("Draft should be cleared", loadedAfterClear);
    }
    
    /**
     * Test persistence with empty pick history.
     */
    @Test
    public void testPersistenceWithEmptyPickHistory() throws PersistenceException {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team One", 1));
        
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", "Player A", "QB", 1));
        
        DraftSnapshot snapshot = new DraftSnapshot(
            teams,
            players,
            new DraftState(1, 1, false),
            new DraftConfig(FlowType.SERPENTINE, 5),
            new ArrayList<>(), // Empty pick history
            System.currentTimeMillis()
        );
        
        persistenceManager.saveDraft(snapshot);
        DraftSnapshot loaded = persistenceManager.loadDraft();
        
        assertNotNull("Snapshot should be loaded", loaded);
        assertNotNull("Pick history should not be null", loaded.getPickHistory());
        assertEquals("Pick history should be empty", 0, loaded.getPickHistory().size());
    }
    
    /**
     * Test persistence with large dataset.
     */
    @Test
    public void testPersistenceWithLargeDataset() throws PersistenceException {
        // Create 12 teams
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            teams.add(new Team("team" + i, "Team " + i, i));
        }
        
        // Create 200 players
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            Player player = new Player("player" + i, "Player " + i, "RB", i);
            if (i <= 24) { // Draft first 24 players (2 rounds for 12 teams)
                player.setDrafted(true);
                player.setDraftedBy("team" + ((i - 1) % 12 + 1));
            }
            players.add(player);
        }
        
        // Create pick history for 24 picks
        List<Pick> pickHistory = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            int round = (i - 1) / 12 + 1;
            int pickInRound = (i - 1) % 12 + 1;
            pickHistory.add(new Pick(i, round, pickInRound, "team" + pickInRound, "player" + i, System.currentTimeMillis()));
        }
        
        DraftSnapshot snapshot = new DraftSnapshot(
            teams,
            players,
            new DraftState(3, 1, false),
            new DraftConfig(FlowType.SERPENTINE, 15),
            pickHistory,
            System.currentTimeMillis()
        );
        
        // Save and load
        persistenceManager.saveDraft(snapshot);
        DraftSnapshot loaded = persistenceManager.loadDraft();
        
        // Verify large dataset
        assertNotNull("Snapshot should be loaded", loaded);
        assertEquals("All teams should be loaded", 12, loaded.getTeams().size());
        assertEquals("All players should be loaded", 200, loaded.getPlayers().size());
        assertEquals("All picks should be loaded", 24, loaded.getPickHistory().size());
        
        // Verify some drafted players
        int draftedCount = 0;
        for (Player player : loaded.getPlayers()) {
            if (player.isDrafted()) {
                draftedCount++;
            }
        }
        assertEquals("24 players should be drafted", 24, draftedCount);
    }
}
