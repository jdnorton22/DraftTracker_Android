package com.fantasydraft.picker.persistence;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftSnapshot;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Property-based and unit tests for PersistenceManager.
 * Tests persistence round trip and error handling.
 */
@RunWith(AndroidJUnit4.class)
public class PersistenceManagerTest {

    private PersistenceManager persistenceManager;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        persistenceManager = new PersistenceManager(context);
        
        // Clear any existing draft data
        persistenceManager.clearDraft();
    }

    @After
    public void tearDown() {
        persistenceManager.clearDraft();
    }

    // Feature: fantasy-draft-picker, Property 10: Persistence Round Trip
    // Validates: Requirements 6.1, 6.2, 6.3, 6.4
    @Test
    public void persistenceRoundTrip() throws PersistenceException {
        // For any draft state, saving the state and then loading it should produce
        // an equivalent draft state with all teams, players, picks, and configuration preserved
        
        // Run 100 trials with different random draft states
        Random random = new Random(12345); // Fixed seed for reproducibility
        
        for (int trial = 0; trial < 100; trial++) {
            // Clear before each trial
            persistenceManager.clearDraft();
            
            // Generate random draft snapshot
            DraftSnapshot original = generateRandomDraftSnapshot(random, trial);
            
            // Save the draft
            persistenceManager.saveDraft(original);
            
            // Load the draft
            DraftSnapshot loaded = persistenceManager.loadDraft();
            
            // Verify loaded snapshot is not null
            assertNotNull("Loaded snapshot should not be null (trial " + trial + ")", loaded);
            
            // Verify draft state is preserved
            assertDraftStateEquals("Draft state mismatch (trial " + trial + ")",
                    original.getDraftState(), loaded.getDraftState());
            
            // Verify draft config is preserved
            assertDraftConfigEquals("Draft config mismatch (trial " + trial + ")",
                    original.getDraftConfig(), loaded.getDraftConfig());
            
            // Verify teams are preserved
            assertTeamsEqual("Teams mismatch (trial " + trial + ")",
                    original.getTeams(), loaded.getTeams());
            
            // Verify players are preserved
            assertPlayersEqual("Players mismatch (trial " + trial + ")",
                    original.getPlayers(), loaded.getPlayers());
            
            // Verify picks are preserved
            assertPicksEqual("Picks mismatch (trial " + trial + ")",
                    original.getPickHistory(), loaded.getPickHistory());
        }
    }

    /**
     * Generate a random draft snapshot for testing.
     */
    private DraftSnapshot generateRandomDraftSnapshot(Random random, int seed) {
        // Generate random team count (2-20)
        int teamCount = 2 + random.nextInt(19);
        
        // Generate random number of rounds (1-15)
        int numberOfRounds = 1 + random.nextInt(15);
        
        // Generate random flow type
        FlowType flowType = random.nextBoolean() ? FlowType.SERPENTINE : FlowType.LINEAR;
        
        // Generate random current round and pick
        int currentRound = 1 + random.nextInt(numberOfRounds);
        int currentPickInRound = 1 + random.nextInt(teamCount);
        boolean isComplete = random.nextDouble() < 0.1; // 10% chance of being complete
        
        // Create draft state and config
        DraftState draftState = new DraftState(currentRound, currentPickInRound, isComplete);
        DraftConfig draftConfig = new DraftConfig(flowType, numberOfRounds);
        
        // Generate teams
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= teamCount; i++) {
            Team team = new Team("team_" + seed + "_" + i, "Team " + i, i);
            teams.add(team);
        }
        
        // Generate players (20-50 players)
        int playerCount = 20 + random.nextInt(31);
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            boolean isDrafted = random.nextDouble() < 0.3; // 30% chance of being drafted
            String draftedBy = isDrafted ? teams.get(random.nextInt(teamCount)).getId() : null;
            Player player = new Player("player_" + seed + "_" + i, "Player " + i,
                    getRandomPosition(random), i, isDrafted, draftedBy);
            players.add(player);
        }
        
        // Generate picks (0 to teamCount * currentRound picks)
        List<Pick> picks = new ArrayList<>();
        int maxPicks = Math.min(teamCount * currentRound, playerCount);
        int pickCount = random.nextInt(maxPicks + 1);
        
        for (int i = 1; i <= pickCount; i++) {
            int round = ((i - 1) / teamCount) + 1;
            int pickInRound = ((i - 1) % teamCount) + 1;
            String teamId = teams.get(random.nextInt(teamCount)).getId();
            String playerId = players.get(random.nextInt(Math.min(i, playerCount))).getId();
            long timestamp = System.currentTimeMillis() - (pickCount - i) * 1000L;
            
            Pick pick = new Pick(i, round, pickInRound, teamId, playerId, timestamp);
            picks.add(pick);
        }
        
        long timestamp = System.currentTimeMillis();
        return new DraftSnapshot(teams, players, draftState, draftConfig, picks, timestamp);
    }

    private String getRandomPosition(Random random) {
        String[] positions = {"QB", "RB", "WR", "TE", "K", "DEF"};
        return positions[random.nextInt(positions.length)];
    }

    private void assertDraftStateEquals(String message, DraftState expected, DraftState actual) {
        assertNotNull(message + " - expected state should not be null", expected);
        assertNotNull(message + " - actual state should not be null", actual);
        assertEquals(message + " - current round", expected.getCurrentRound(), actual.getCurrentRound());
        assertEquals(message + " - current pick in round", expected.getCurrentPickInRound(), actual.getCurrentPickInRound());
        assertEquals(message + " - is complete", expected.isComplete(), actual.isComplete());
    }

    private void assertDraftConfigEquals(String message, DraftConfig expected, DraftConfig actual) {
        assertNotNull(message + " - expected config should not be null", expected);
        assertNotNull(message + " - actual config should not be null", actual);
        assertEquals(message + " - flow type", expected.getFlowType(), actual.getFlowType());
        assertEquals(message + " - number of rounds", expected.getNumberOfRounds(), actual.getNumberOfRounds());
    }

    private void assertTeamsEqual(String message, List<Team> expected, List<Team> actual) {
        assertNotNull(message + " - expected teams should not be null", expected);
        assertNotNull(message + " - actual teams should not be null", actual);
        assertEquals(message + " - team count", expected.size(), actual.size());
        
        for (int i = 0; i < expected.size(); i++) {
            Team expectedTeam = expected.get(i);
            Team actualTeam = actual.get(i);
            
            assertEquals(message + " - team " + i + " id", expectedTeam.getId(), actualTeam.getId());
            assertEquals(message + " - team " + i + " name", expectedTeam.getName(), actualTeam.getName());
            assertEquals(message + " - team " + i + " draft position",
                    expectedTeam.getDraftPosition(), actualTeam.getDraftPosition());
        }
    }

    private void assertPlayersEqual(String message, List<Player> expected, List<Player> actual) {
        assertNotNull(message + " - expected players should not be null", expected);
        assertNotNull(message + " - actual players should not be null", actual);
        assertEquals(message + " - player count", expected.size(), actual.size());
        
        for (int i = 0; i < expected.size(); i++) {
            Player expectedPlayer = expected.get(i);
            Player actualPlayer = actual.get(i);
            
            assertEquals(message + " - player " + i + " id", expectedPlayer.getId(), actualPlayer.getId());
            assertEquals(message + " - player " + i + " name", expectedPlayer.getName(), actualPlayer.getName());
            assertEquals(message + " - player " + i + " position",
                    expectedPlayer.getPosition(), actualPlayer.getPosition());
            assertEquals(message + " - player " + i + " rank", expectedPlayer.getRank(), actualPlayer.getRank());
            assertEquals(message + " - player " + i + " is drafted",
                    expectedPlayer.isDrafted(), actualPlayer.isDrafted());
            assertEquals(message + " - player " + i + " drafted by",
                    expectedPlayer.getDraftedBy(), actualPlayer.getDraftedBy());
        }
    }

    private void assertPicksEqual(String message, List<Pick> expected, List<Pick> actual) {
        assertNotNull(message + " - expected picks should not be null", expected);
        assertNotNull(message + " - actual picks should not be null", actual);
        assertEquals(message + " - pick count", expected.size(), actual.size());
        
        for (int i = 0; i < expected.size(); i++) {
            Pick expectedPick = expected.get(i);
            Pick actualPick = actual.get(i);
            
            assertEquals(message + " - pick " + i + " number",
                    expectedPick.getPickNumber(), actualPick.getPickNumber());
            assertEquals(message + " - pick " + i + " round", expectedPick.getRound(), actualPick.getRound());
            assertEquals(message + " - pick " + i + " pick in round",
                    expectedPick.getPickInRound(), actualPick.getPickInRound());
            assertEquals(message + " - pick " + i + " team id", expectedPick.getTeamId(), actualPick.getTeamId());
            assertEquals(message + " - pick " + i + " player id",
                    expectedPick.getPlayerId(), actualPick.getPlayerId());
            assertEquals(message + " - pick " + i + " timestamp",
                    expectedPick.getTimestamp(), actualPick.getTimestamp());
        }
    }

    // ========== Unit Tests for Error Handling ==========
    // Requirements: 6.3

    @Test
    public void testSaveDraftWithNullSnapshot() throws PersistenceException {
        try {
            persistenceManager.saveDraft(null);
            fail("Should reject null snapshot");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention null",
                    e.getMessage().toLowerCase().contains("null"));
        }
    }

    @Test
    public void testLoadDraftWhenNoDraftExists() throws PersistenceException {
        // Clear any existing draft
        persistenceManager.clearDraft();
        
        // Load should return null when no draft exists
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNull("Should return null when no draft exists", loaded);
    }

    @Test
    public void testClearDraftWhenNoDraftExists() throws PersistenceException {
        // Clear when no draft exists should not throw exception
        persistenceManager.clearDraft();
        
        // Verify no draft exists
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNull("Should return null after clear", loaded);
    }

    @Test
    public void testSaveDraftWithEmptySnapshot() throws PersistenceException {
        // Create an empty snapshot (no teams, players, or picks)
        DraftState draftState = new DraftState(1, 1, false);
        DraftConfig draftConfig = new DraftConfig(FlowType.SERPENTINE, 10);
        DraftSnapshot emptySnapshot = new DraftSnapshot(
                new ArrayList<>(),
                new ArrayList<>(),
                draftState,
                draftConfig,
                new ArrayList<>(),
                System.currentTimeMillis()
        );
        
        // Should be able to save empty snapshot
        persistenceManager.saveDraft(emptySnapshot);
        
        // Should be able to load it back
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNotNull("Should load empty snapshot", loaded);
        assertEquals(0, loaded.getTeams().size());
        assertEquals(0, loaded.getPlayers().size());
        assertEquals(0, loaded.getPickHistory().size());
    }

    @Test
    public void testSaveDraftWithNullDraftState() throws PersistenceException {
        // Create snapshot with null draft state
        DraftConfig draftConfig = new DraftConfig(FlowType.SERPENTINE, 10);
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team A", 1));
        
        DraftSnapshot snapshot = new DraftSnapshot(
                teams,
                new ArrayList<>(),
                null, // null draft state
                draftConfig,
                new ArrayList<>(),
                System.currentTimeMillis()
        );
        
        // Save should succeed (state/config are optional)
        persistenceManager.saveDraft(snapshot);
        
        // Load should return null because draft state is missing
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNull("Should return null when draft state is missing", loaded);
    }

    @Test
    public void testSaveDraftWithNullDraftConfig() throws PersistenceException {
        // Create snapshot with null draft config
        DraftState draftState = new DraftState(1, 1, false);
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team A", 1));
        
        DraftSnapshot snapshot = new DraftSnapshot(
                teams,
                new ArrayList<>(),
                draftState,
                null, // null draft config
                new ArrayList<>(),
                System.currentTimeMillis()
        );
        
        // Save should succeed (state/config are optional)
        persistenceManager.saveDraft(snapshot);
        
        // Load should return null because draft config is missing
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNull("Should return null when draft config is missing", loaded);
    }

    @Test
    public void testMultipleSaveAndLoadCycles() throws PersistenceException {
        // Test that multiple save/load cycles work correctly
        for (int i = 0; i < 5; i++) {
            DraftState draftState = new DraftState(i + 1, 1, false);
            DraftConfig draftConfig = new DraftConfig(FlowType.SERPENTINE, 10);
            List<Team> teams = new ArrayList<>();
            teams.add(new Team("team" + i, "Team " + i, 1));
            
            DraftSnapshot snapshot = new DraftSnapshot(
                    teams,
                    new ArrayList<>(),
                    draftState,
                    draftConfig,
                    new ArrayList<>(),
                    System.currentTimeMillis()
            );
            
            persistenceManager.saveDraft(snapshot);
            DraftSnapshot loaded = persistenceManager.loadDraft();
            
            assertNotNull("Loaded snapshot should not be null (cycle " + i + ")", loaded);
            assertEquals("Current round should match (cycle " + i + ")",
                    i + 1, loaded.getDraftState().getCurrentRound());
            assertEquals("Team name should match (cycle " + i + ")",
                    "Team " + i, loaded.getTeams().get(0).getName());
        }
    }

    @Test
    public void testSaveOverwritesPreviousDraft() throws PersistenceException {
        // Save first draft
        DraftState state1 = new DraftState(1, 1, false);
        DraftConfig config1 = new DraftConfig(FlowType.SERPENTINE, 10);
        List<Team> teams1 = new ArrayList<>();
        teams1.add(new Team("team1", "First Team", 1));
        
        DraftSnapshot snapshot1 = new DraftSnapshot(
                teams1,
                new ArrayList<>(),
                state1,
                config1,
                new ArrayList<>(),
                System.currentTimeMillis()
        );
        persistenceManager.saveDraft(snapshot1);
        
        // Save second draft (should overwrite)
        DraftState state2 = new DraftState(5, 3, false);
        DraftConfig config2 = new DraftConfig(FlowType.LINEAR, 15);
        List<Team> teams2 = new ArrayList<>();
        teams2.add(new Team("team2", "Second Team", 1));
        teams2.add(new Team("team3", "Third Team", 2));
        
        DraftSnapshot snapshot2 = new DraftSnapshot(
                teams2,
                new ArrayList<>(),
                state2,
                config2,
                new ArrayList<>(),
                System.currentTimeMillis()
        );
        persistenceManager.saveDraft(snapshot2);
        
        // Load should return second draft
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNotNull("Loaded snapshot should not be null", loaded);
        assertEquals("Should have 2 teams from second draft", 2, loaded.getTeams().size());
        assertEquals("Current round should be from second draft", 5, loaded.getDraftState().getCurrentRound());
        assertEquals("Flow type should be from second draft", FlowType.LINEAR, loaded.getDraftConfig().getFlowType());
        assertEquals("Team name should be from second draft", "Second Team", loaded.getTeams().get(0).getName());
    }

    @Test
    public void testClearDraftRemovesAllData() throws PersistenceException {
        // Save a draft with data
        DraftState draftState = new DraftState(2, 3, false);
        DraftConfig draftConfig = new DraftConfig(FlowType.SERPENTINE, 10);
        
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("team1", "Team A", 1));
        teams.add(new Team("team2", "Team B", 2));
        
        List<Player> players = new ArrayList<>();
        players.add(new Player("p1", "Player 1", "QB", 1));
        players.add(new Player("p2", "Player 2", "RB", 2));
        
        List<Pick> picks = new ArrayList<>();
        picks.add(new Pick(1, 1, 1, "team1", "p1", System.currentTimeMillis()));
        
        DraftSnapshot snapshot = new DraftSnapshot(teams, players, draftState, draftConfig, picks,
                System.currentTimeMillis());
        persistenceManager.saveDraft(snapshot);
        
        // Verify draft exists
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNotNull("Draft should exist before clear", loaded);
        
        // Clear the draft
        persistenceManager.clearDraft();
        
        // Verify draft no longer exists
        DraftSnapshot afterClear = persistenceManager.loadDraft();
        assertNull("Draft should not exist after clear", afterClear);
    }

    @Test
    public void testSaveDraftWithNullLists() throws PersistenceException {
        // Create snapshot with null lists (should be handled gracefully)
        DraftState draftState = new DraftState(1, 1, false);
        DraftConfig draftConfig = new DraftConfig(FlowType.SERPENTINE, 10);
        
        DraftSnapshot snapshot = new DraftSnapshot(
                null, // null teams
                null, // null players
                draftState,
                draftConfig,
                null, // null picks
                System.currentTimeMillis()
        );
        
        // Should handle null lists gracefully
        persistenceManager.saveDraft(snapshot);
        
        // Load should work
        DraftSnapshot loaded = persistenceManager.loadDraft();
        assertNotNull("Should load snapshot with null lists", loaded);
        assertNotNull("Teams list should not be null", loaded.getTeams());
        assertNotNull("Players list should not be null", loaded.getPlayers());
        assertNotNull("Picks list should not be null", loaded.getPickHistory());
        assertEquals(0, loaded.getTeams().size());
        assertEquals(0, loaded.getPlayers().size());
        assertEquals(0, loaded.getPickHistory().size());
    }
}
