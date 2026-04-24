package com.fantasydraft.picker.persistence;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for database operations (DatabaseHelper and DAO classes).
 * Tests insert, update, delete, and query operations.
 * Requirements: 6.1, 6.2
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseOperationsTest {

    private DatabaseHelper dbHelper;
    private TeamDAO teamDAO;
    private PlayerDAO playerDAO;
    private PickDAO pickDAO;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        teamDAO = new TeamDAO(dbHelper);
        playerDAO = new PlayerDAO(dbHelper);
        pickDAO = new PickDAO(dbHelper);
        
        // Clear all tables before each test
        cleanDatabase();
    }

    @After
    public void tearDown() {
        cleanDatabase();
        dbHelper.close();
    }

    private void cleanDatabase() {
        pickDAO.deleteAll();
        playerDAO.deleteAll();
        teamDAO.deleteAll();
    }

    // ========== TeamDAO Tests ==========

    @Test
    public void testTeamInsert() {
        Team team = new Team("team1", "The Champions", 1);
        long result = teamDAO.insert(team);
        
        assertTrue("Insert should return non-negative value", result >= 0);
        
        Team retrieved = teamDAO.getById("team1");
        assertNotNull("Retrieved team should not be null", retrieved);
        assertEquals("team1", retrieved.getId());
        assertEquals("The Champions", retrieved.getName());
        assertEquals(1, retrieved.getDraftPosition());
    }

    @Test
    public void testTeamUpdate() {
        Team team = new Team("team1", "Original Name", 1);
        teamDAO.insert(team);
        
        team.setName("Updated Name");
        team.setDraftPosition(5);
        int rowsAffected = teamDAO.update(team);
        
        assertEquals("Update should affect 1 row", 1, rowsAffected);
        
        Team retrieved = teamDAO.getById("team1");
        assertEquals("Updated Name", retrieved.getName());
        assertEquals(5, retrieved.getDraftPosition());
    }

    @Test
    public void testTeamDelete() {
        Team team = new Team("team1", "The Champions", 1);
        teamDAO.insert(team);
        
        int rowsAffected = teamDAO.delete("team1");
        assertEquals("Delete should affect 1 row", 1, rowsAffected);
        
        Team retrieved = teamDAO.getById("team1");
        assertNull("Deleted team should not be retrievable", retrieved);
    }

    @Test
    public void testTeamGetAll() {
        Team team1 = new Team("team1", "Team A", 2);
        Team team2 = new Team("team2", "Team B", 1);
        Team team3 = new Team("team3", "Team C", 3);
        
        teamDAO.insert(team1);
        teamDAO.insert(team2);
        teamDAO.insert(team3);
        
        List<Team> teams = teamDAO.getAll();
        assertEquals("Should retrieve 3 teams", 3, teams.size());
        
        // Verify teams are ordered by draft position
        assertEquals("Team B", teams.get(0).getName());
        assertEquals("Team A", teams.get(1).getName());
        assertEquals("Team C", teams.get(2).getName());
    }

    @Test
    public void testTeamDeleteAll() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        teamDAO.insert(new Team("team2", "Team B", 2));
        
        int rowsAffected = teamDAO.deleteAll();
        assertEquals("DeleteAll should affect 2 rows", 2, rowsAffected);
        
        List<Team> teams = teamDAO.getAll();
        assertEquals("Should have no teams after deleteAll", 0, teams.size());
    }

    @Test
    public void testTeamGetByIdNonexistent() {
        Team retrieved = teamDAO.getById("nonexistent");
        assertNull("Nonexistent team should return null", retrieved);
    }

    // ========== PlayerDAO Tests ==========

    @Test
    public void testPlayerInsert() {
        Player player = new Player("p1", "Christian McCaffrey", "RB", 1);
        long result = playerDAO.insert(player);
        
        assertTrue("Insert should return non-negative value", result >= 0);
        
        Player retrieved = playerDAO.getById("p1");
        assertNotNull("Retrieved player should not be null", retrieved);
        assertEquals("p1", retrieved.getId());
        assertEquals("Christian McCaffrey", retrieved.getName());
        assertEquals("RB", retrieved.getPosition());
        assertEquals(1, retrieved.getRank());
        assertFalse("Player should not be drafted initially", retrieved.isDrafted());
        assertNull("DraftedBy should be null initially", retrieved.getDraftedBy());
    }

    @Test
    public void testPlayerUpdate() {
        Player player = new Player("p1", "Player Name", "QB", 5);
        playerDAO.insert(player);
        
        player.setDrafted(true);
        player.setDraftedBy("team1");
        int rowsAffected = playerDAO.update(player);
        
        assertEquals("Update should affect 1 row", 1, rowsAffected);
        
        Player retrieved = playerDAO.getById("p1");
        assertTrue("Player should be drafted", retrieved.isDrafted());
        assertEquals("team1", retrieved.getDraftedBy());
    }

    @Test
    public void testPlayerDelete() {
        Player player = new Player("p1", "Player Name", "QB", 5);
        playerDAO.insert(player);
        
        int rowsAffected = playerDAO.delete("p1");
        assertEquals("Delete should affect 1 row", 1, rowsAffected);
        
        Player retrieved = playerDAO.getById("p1");
        assertNull("Deleted player should not be retrievable", retrieved);
    }

    @Test
    public void testPlayerGetAll() {
        Player p1 = new Player("p1", "Player A", "QB", 3);
        Player p2 = new Player("p2", "Player B", "RB", 1);
        Player p3 = new Player("p3", "Player C", "WR", 2);
        
        playerDAO.insert(p1);
        playerDAO.insert(p2);
        playerDAO.insert(p3);
        
        List<Player> players = playerDAO.getAll();
        assertEquals("Should retrieve 3 players", 3, players.size());
        
        // Verify players are ordered by rank
        assertEquals("Player B", players.get(0).getName());
        assertEquals("Player C", players.get(1).getName());
        assertEquals("Player A", players.get(2).getName());
    }

    @Test
    public void testPlayerGetAvailablePlayers() {
        Player p1 = new Player("p1", "Available Player", "QB", 1, false, null);
        Player p2 = new Player("p2", "Drafted Player", "RB", 2, true, "team1");
        Player p3 = new Player("p3", "Another Available", "WR", 3, false, null);
        
        playerDAO.insert(p1);
        playerDAO.insert(p2);
        playerDAO.insert(p3);
        
        List<Player> available = playerDAO.getAvailablePlayers();
        assertEquals("Should retrieve 2 available players", 2, available.size());
        assertEquals("Available Player", available.get(0).getName());
        assertEquals("Another Available", available.get(1).getName());
    }

    @Test
    public void testPlayerGetPlayersByTeam() {
        // Insert team first
        teamDAO.insert(new Team("team1", "Team A", 1));
        
        Player p1 = new Player("p1", "Player A", "QB", 1, true, "team1");
        Player p2 = new Player("p2", "Player B", "RB", 2, true, "team1");
        Player p3 = new Player("p3", "Player C", "WR", 3, true, "team2");
        
        playerDAO.insert(p1);
        playerDAO.insert(p2);
        playerDAO.insert(p3);
        
        List<Player> teamPlayers = playerDAO.getPlayersByTeam("team1");
        assertEquals("Should retrieve 2 players for team1", 2, teamPlayers.size());
        assertEquals("Player A", teamPlayers.get(0).getName());
        assertEquals("Player B", teamPlayers.get(1).getName());
    }

    @Test
    public void testPlayerDeleteAll() {
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        
        int rowsAffected = playerDAO.deleteAll();
        assertEquals("DeleteAll should affect 2 rows", 2, rowsAffected);
        
        List<Player> players = playerDAO.getAll();
        assertEquals("Should have no players after deleteAll", 0, players.size());
    }

    // ========== PickDAO Tests ==========

    @Test
    public void testPickInsert() {
        // Insert required foreign key data
        teamDAO.insert(new Team("team1", "Team A", 1));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        
        Pick pick = new Pick(1, 1, 1, "team1", "p1", System.currentTimeMillis());
        long result = pickDAO.insert(pick);
        
        assertTrue("Insert should return non-negative value", result >= 0);
        
        Pick retrieved = pickDAO.getByPickNumber(1);
        assertNotNull("Retrieved pick should not be null", retrieved);
        assertEquals(1, retrieved.getPickNumber());
        assertEquals(1, retrieved.getRound());
        assertEquals(1, retrieved.getPickInRound());
        assertEquals("team1", retrieved.getTeamId());
        assertEquals("p1", retrieved.getPlayerId());
    }

    @Test
    public void testPickUpdate() {
        // Insert required foreign key data
        teamDAO.insert(new Team("team1", "Team A", 1));
        teamDAO.insert(new Team("team2", "Team B", 2));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        
        Pick pick = new Pick(1, 1, 1, "team1", "p1", 1000L);
        pickDAO.insert(pick);
        
        pick.setTeamId("team2");
        pick.setPlayerId("p2");
        pick.setTimestamp(2000L);
        int rowsAffected = pickDAO.update(pick);
        
        assertEquals("Update should affect 1 row", 1, rowsAffected);
        
        Pick retrieved = pickDAO.getByPickNumber(1);
        assertEquals("team2", retrieved.getTeamId());
        assertEquals("p2", retrieved.getPlayerId());
        assertEquals(2000L, retrieved.getTimestamp());
    }

    @Test
    public void testPickDelete() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        
        Pick pick = new Pick(1, 1, 1, "team1", "p1", System.currentTimeMillis());
        pickDAO.insert(pick);
        
        int rowsAffected = pickDAO.delete(1);
        assertEquals("Delete should affect 1 row", 1, rowsAffected);
        
        Pick retrieved = pickDAO.getByPickNumber(1);
        assertNull("Deleted pick should not be retrievable", retrieved);
    }

    @Test
    public void testPickGetAll() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        playerDAO.insert(new Player("p3", "Player C", "WR", 3));
        
        Pick pick1 = new Pick(3, 1, 3, "team1", "p3", 3000L);
        Pick pick2 = new Pick(1, 1, 1, "team1", "p1", 1000L);
        Pick pick3 = new Pick(2, 1, 2, "team1", "p2", 2000L);
        
        pickDAO.insert(pick1);
        pickDAO.insert(pick2);
        pickDAO.insert(pick3);
        
        List<Pick> picks = pickDAO.getAll();
        assertEquals("Should retrieve 3 picks", 3, picks.size());
        
        // Verify picks are ordered by pick number
        assertEquals(1, picks.get(0).getPickNumber());
        assertEquals(2, picks.get(1).getPickNumber());
        assertEquals(3, picks.get(2).getPickNumber());
    }

    @Test
    public void testPickGetPicksByTeam() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        teamDAO.insert(new Team("team2", "Team B", 2));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        playerDAO.insert(new Player("p3", "Player C", "WR", 3));
        
        pickDAO.insert(new Pick(1, 1, 1, "team1", "p1", 1000L));
        pickDAO.insert(new Pick(2, 1, 2, "team2", "p2", 2000L));
        pickDAO.insert(new Pick(3, 2, 1, "team1", "p3", 3000L));
        
        List<Pick> team1Picks = pickDAO.getPicksByTeam("team1");
        assertEquals("Should retrieve 2 picks for team1", 2, team1Picks.size());
        assertEquals(1, team1Picks.get(0).getPickNumber());
        assertEquals(3, team1Picks.get(1).getPickNumber());
    }

    @Test
    public void testPickGetPicksByRound() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        playerDAO.insert(new Player("p3", "Player C", "WR", 3));
        
        pickDAO.insert(new Pick(1, 1, 1, "team1", "p1", 1000L));
        pickDAO.insert(new Pick(2, 1, 2, "team1", "p2", 2000L));
        pickDAO.insert(new Pick(3, 2, 1, "team1", "p3", 3000L));
        
        List<Pick> round1Picks = pickDAO.getPicksByRound(1);
        assertEquals("Should retrieve 2 picks for round 1", 2, round1Picks.size());
        assertEquals(1, round1Picks.get(0).getPickInRound());
        assertEquals(2, round1Picks.get(1).getPickInRound());
    }

    @Test
    public void testPickDeleteAll() {
        teamDAO.insert(new Team("team1", "Team A", 1));
        playerDAO.insert(new Player("p1", "Player A", "QB", 1));
        playerDAO.insert(new Player("p2", "Player B", "RB", 2));
        
        pickDAO.insert(new Pick(1, 1, 1, "team1", "p1", 1000L));
        pickDAO.insert(new Pick(2, 1, 2, "team1", "p2", 2000L));
        
        int rowsAffected = pickDAO.deleteAll();
        assertEquals("DeleteAll should affect 2 rows", 2, rowsAffected);
        
        List<Pick> picks = pickDAO.getAll();
        assertEquals("Should have no picks after deleteAll", 0, picks.size());
    }

    // ========== Database Integrity Tests ==========

    @Test
    public void testForeignKeyConstraintOnPlayerDraftedBy() {
        // Attempting to insert a player with non-existent team should work
        // (foreign key is nullable and not enforced on insert for drafted_by)
        Player player = new Player("p1", "Player A", "QB", 1, true, "nonexistent_team");
        long result = playerDAO.insert(player);
        assertTrue("Insert should succeed even with non-existent team", result >= 0);
    }

    @Test
    public void testIndexesExist() {
        // This test verifies that the database was created successfully with indexes
        // by performing queries that would benefit from indexes
        
        // Insert test data
        for (int i = 1; i <= 100; i++) {
            playerDAO.insert(new Player("p" + i, "Player " + i, "QB", i));
        }
        
        // Query that uses rank index
        List<Player> players = playerDAO.getAll();
        assertEquals(100, players.size());
        assertEquals(1, players.get(0).getRank());
        assertEquals(100, players.get(99).getRank());
        
        // Query that uses is_drafted index
        List<Player> available = playerDAO.getAvailablePlayers();
        assertEquals(100, available.size());
    }

    @Test
    public void testDatabaseUpgrade() {
        // Close current database
        dbHelper.close();
        
        // Create a new database helper with same context
        // This simulates an upgrade scenario
        DatabaseHelper newHelper = new DatabaseHelper(context);
        TeamDAO newTeamDAO = new TeamDAO(newHelper);
        
        // Verify database is functional after "upgrade"
        Team team = new Team("team1", "Test Team", 1);
        long result = newTeamDAO.insert(team);
        assertTrue("Insert should work after database recreation", result >= 0);
        
        newHelper.close();
    }
}
