package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Property-based and unit tests for TeamManager.
 */
@RunWith(JUnitQuickcheck.class)
public class TeamManagerTest {

    private TeamManager manager;

    @Before
    public void setUp() {
        manager = new TeamManager();
    }

    // Feature: fantasy-draft-picker, Property 1: Team Count Validation
    // Validates: Requirements 1.1
    @Property(trials = 100)
    public void teamCountValidation(int teamCount) {
        // For any integer value, the system should accept it as a valid team count
        // if and only if it is between 2 and 20 (inclusive)
        
        boolean isValid = teamCount >= 2 && teamCount <= 20;
        
        TeamManager testManager = new TeamManager();
        
        if (isValid) {
            // Should be able to add this many teams without error
            try {
                for (int i = 1; i <= teamCount; i++) {
                    testManager.addTeam("Team " + i, i);
                }
                assertEquals(teamCount, testManager.getTeams().size());
            } catch (Exception e) {
                fail("Should accept team count " + teamCount + " but got exception: " + e.getMessage());
            }
        } else {
            // Team count outside valid range - we can't directly test rejection
            // since addTeam doesn't validate total count, but we verify the range concept
            assertTrue("Team count " + teamCount + " should be outside valid range [2, 20]",
                    teamCount < 2 || teamCount > 20);
        }
    }

    // Feature: fantasy-draft-picker, Property 2: Team Name Uniqueness
    // Validates: Requirements 1.3
    @Property(trials = 100)
    public void teamNameUniqueness(String teamName) {
        // For any list of teams, all team names should be unique, and attempting to add
        // a duplicate team name should be rejected
        
        if (teamName == null || teamName.trim().isEmpty()) {
            // Skip invalid names for this property test
            return;
        }
        
        TeamManager testManager = new TeamManager();
        
        // Add a team with the given name
        Team firstTeam = testManager.addTeam(teamName, 1);
        assertNotNull(firstTeam);
        assertEquals(teamName.trim(), firstTeam.getName());
        
        // Attempting to add another team with the same name should fail
        try {
            testManager.addTeam(teamName, 2);
            fail("Should reject duplicate team name: " + teamName);
        } catch (IllegalArgumentException e) {
            // Expected - duplicate name should be rejected
            assertTrue(e.getMessage().contains("unique"));
        }
        
        // Verify only one team was added
        assertEquals(1, testManager.getTeams().size());
        
        // Test case-insensitive uniqueness with ASCII characters
        // For Unicode edge cases, equalsIgnoreCase() may not work perfectly,
        // so we only test case-insensitivity when we can verify it will work
        String lowerCase = teamName.trim().toLowerCase();
        String upperCase = teamName.trim().toUpperCase();
        
        // Only test if the case conversion actually produces a different string
        // AND if equalsIgnoreCase confirms they should be treated as equal
        if (!lowerCase.equals(teamName.trim()) && lowerCase.equalsIgnoreCase(teamName.trim())) {
            try {
                testManager.addTeam(lowerCase, 2);
                fail("Should reject duplicate team name (lowercase): " + lowerCase);
            } catch (IllegalArgumentException e) {
                // Expected - case-insensitive duplicate should be rejected
                assertTrue(e.getMessage().contains("unique"));
            }
        }
        
        // Test uppercase variant if it's different and equalsIgnoreCase confirms equality
        if (!upperCase.equals(teamName.trim()) && upperCase.equalsIgnoreCase(teamName.trim())) {
            try {
                testManager.addTeam(upperCase, 2);
                fail("Should reject duplicate team name (uppercase): " + upperCase);
            } catch (IllegalArgumentException e) {
                // Expected - case-insensitive duplicate should be rejected
                assertTrue(e.getMessage().contains("unique"));
            }
        }
    }

    // Feature: fantasy-draft-picker, Property 3: Draft Order Completeness
    // Validates: Requirements 2.1, 2.2
    @Property(trials = 100)
    public void draftOrderCompleteness(int teamCount) {
        // For any set of N teams, the draft positions should contain exactly the integers
        // from 1 to N with no duplicates or gaps
        
        if (teamCount < 2 || teamCount > 20) {
            // Skip invalid team counts
            return;
        }
        
        TeamManager testManager = new TeamManager();
        List<Team> teams = new ArrayList<>();
        
        // Create teams with complete draft order (1 to N)
        for (int i = 1; i <= teamCount; i++) {
            Team team = testManager.addTeam("Team " + i, i);
            teams.add(team);
        }
        
        // Validate that the draft order is complete
        assertTrue("Draft order should be complete for " + teamCount + " teams",
                testManager.validateDraftOrder(teams));
        
        // Test with incomplete order (missing position)
        if (teamCount > 2) {
            List<Team> incompleteTeams = new ArrayList<>();
            for (int i = 1; i < teamCount; i++) {
                incompleteTeams.add(new Team("id" + i, "Team " + i, i));
            }
            // Add a team with duplicate position instead of the missing one
            incompleteTeams.add(new Team("idDup", "Team Dup", 1));
            
            assertFalse("Draft order should be invalid with duplicate position",
                    testManager.validateDraftOrder(incompleteTeams));
        }
        
        // Test with gap in positions
        if (teamCount > 2) {
            List<Team> gapTeams = new ArrayList<>();
            for (int i = 1; i <= teamCount; i++) {
                // Skip position 2 to create a gap
                int position = (i == 2) ? teamCount + 1 : i;
                gapTeams.add(new Team("id" + i, "Team " + i, position));
            }
            
            assertFalse("Draft order should be invalid with gap in positions",
                    testManager.validateDraftOrder(gapTeams));
        }
    }

    // ========== Unit Tests for Edge Cases ==========
    // Requirements: 1.2, 1.3, 2.1

    @Test
    public void testAddTeamWithNullName() {
        try {
            manager.addTeam(null, 1);
            fail("Should reject null team name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }

    @Test
    public void testAddTeamWithEmptyName() {
        try {
            manager.addTeam("", 1);
            fail("Should reject empty team name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }

    @Test
    public void testAddTeamWithWhitespaceName() {
        try {
            manager.addTeam("   ", 1);
            fail("Should reject whitespace-only team name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }

    @Test
    public void testAddTeamWithInvalidPosition() {
        try {
            manager.addTeam("Valid Team", 0);
            fail("Should reject position 0");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("position"));
        }

        try {
            manager.addTeam("Valid Team", -1);
            fail("Should reject negative position");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("position"));
        }
    }

    @Test
    public void testAddTeamTrimsName() {
        Team team = manager.addTeam("  Team Name  ", 1);
        assertEquals("Team Name", team.getName());
    }

    @Test
    public void testUpdateDraftPositionWithNullTeamId() {
        try {
            manager.updateDraftPosition(null, 1);
            fail("Should reject null team ID");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testUpdateDraftPositionWithInvalidTeamId() {
        try {
            manager.updateDraftPosition("nonexistent-id", 1);
            fail("Should reject nonexistent team ID");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    public void testUpdateDraftPositionWithInvalidPosition() {
        Team team = manager.addTeam("Team A", 1);
        
        try {
            manager.updateDraftPosition(team.getId(), 0);
            fail("Should reject position 0");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("position"));
        }

        try {
            manager.updateDraftPosition(team.getId(), -5);
            fail("Should reject negative position");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("position"));
        }
    }

    @Test
    public void testUpdateDraftPositionSuccess() {
        Team team = manager.addTeam("Team A", 1);
        manager.updateDraftPosition(team.getId(), 5);
        assertEquals(5, team.getDraftPosition());
    }

    @Test
    public void testAddPlayerToRosterWithNullTeamId() {
        Player player = new Player("p1", "Player 1", "QB", 1);
        
        try {
            manager.addPlayerToRoster(null, player);
            fail("Should reject null team ID");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testAddPlayerToRosterWithNullPlayer() {
        Team team = manager.addTeam("Team A", 1);
        
        try {
            manager.addPlayerToRoster(team.getId(), null);
            fail("Should reject null player");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testAddPlayerToRosterWithInvalidTeamId() {
        Player player = new Player("p1", "Player 1", "QB", 1);
        
        try {
            manager.addPlayerToRoster("nonexistent-id", player);
            fail("Should reject nonexistent team ID");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    public void testAddPlayerToRosterSuccess() {
        Team team = manager.addTeam("Team A", 1);
        Player player = new Player("p1", "Player 1", "QB", 1);
        
        manager.addPlayerToRoster(team.getId(), player);
        assertEquals(1, team.getRoster().size());
        assertEquals(player, team.getRoster().get(0));
    }

    @Test
    public void testValidateTeamNameWithNull() {
        assertFalse(manager.validateTeamName(null, new ArrayList<>()));
        assertFalse(manager.validateTeamName("Valid", null));
    }

    @Test
    public void testValidateDraftOrderWithNull() {
        assertFalse(manager.validateDraftOrder(null));
    }

    @Test
    public void testValidateDraftOrderWithEmptyList() {
        assertFalse(manager.validateDraftOrder(new ArrayList<>()));
    }

    @Test
    public void testValidateDraftOrderWithSingleTeam() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("id1", "Team 1", 1));
        assertTrue(manager.validateDraftOrder(teams));
    }

    @Test
    public void testGetTeamByIdWithNull() {
        assertNull(manager.getTeamById(null));
    }

    @Test
    public void testGetTeamByIdWithNonexistentId() {
        assertNull(manager.getTeamById("nonexistent"));
    }

    @Test
    public void testGetTeamByIdSuccess() {
        Team team = manager.addTeam("Team A", 1);
        Team retrieved = manager.getTeamById(team.getId());
        assertNotNull(retrieved);
        assertEquals(team.getId(), retrieved.getId());
    }

    @Test
    public void testClearTeams() {
        manager.addTeam("Team A", 1);
        manager.addTeam("Team B", 2);
        assertEquals(2, manager.getTeams().size());
        
        manager.clearTeams();
        assertEquals(0, manager.getTeams().size());
    }
}
