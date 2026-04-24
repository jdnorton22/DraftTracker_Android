package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
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
public class DraftManagerTest {

    // Feature: fantasy-draft-picker, Property 4: Serpentine Flow Alternation
    // Validates: Requirements 3.1, 3.3
    @Property(trials = 100)
    public void serpentineFlowAlternation(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 20) int numberOfRounds) {
        
        DraftManager draftManager = new DraftManager();
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, numberOfRounds);
        List<Team> teams = createTeams(teamCount);
        
        List<Integer> pickSequence = draftManager.generatePickSequence(config, teams);
        
        // Verify the pick sequence follows serpentine pattern
        int expectedPicksPerRound = teamCount;
        
        for (int round = 1; round <= numberOfRounds; round++) {
            int startIndex = (round - 1) * teamCount;
            int endIndex = round * teamCount;
            
            if (endIndex > pickSequence.size()) {
                break; // Handle edge case where sequence is shorter
            }
            
            List<Integer> roundPicks = pickSequence.subList(startIndex, endIndex);
            
            if (round % 2 == 1) {
                // Odd rounds: should be [0, 1, 2, ..., N-1]
                for (int i = 0; i < teamCount; i++) {
                    assertEquals("Odd round " + round + " pick " + i + " should follow ascending order",
                            Integer.valueOf(i), roundPicks.get(i));
                }
            } else {
                // Even rounds: should be [N-1, N-2, ..., 1, 0]
                for (int i = 0; i < teamCount; i++) {
                    assertEquals("Even round " + round + " pick " + i + " should follow descending order",
                            Integer.valueOf(teamCount - 1 - i), roundPicks.get(i));
                }
            }
        }
    }

    // Feature: fantasy-draft-picker, Property 5: Linear Flow Consistency
    // Validates: Requirements 3.2, 3.4
    @Property(trials = 100)
    public void linearFlowConsistency(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 20) int numberOfRounds) {
        
        DraftManager draftManager = new DraftManager();
        DraftConfig config = new DraftConfig(FlowType.LINEAR, numberOfRounds);
        List<Team> teams = createTeams(teamCount);
        
        List<Integer> pickSequence = draftManager.generatePickSequence(config, teams);
        
        // Verify the pick sequence follows linear pattern (same order every round)
        for (int round = 1; round <= numberOfRounds; round++) {
            int startIndex = (round - 1) * teamCount;
            int endIndex = round * teamCount;
            
            if (endIndex > pickSequence.size()) {
                break; // Handle edge case where sequence is shorter
            }
            
            List<Integer> roundPicks = pickSequence.subList(startIndex, endIndex);
            
            // All rounds: should be [0, 1, 2, ..., N-1]
            for (int i = 0; i < teamCount; i++) {
                assertEquals("Linear flow round " + round + " pick " + i + " should follow ascending order",
                        Integer.valueOf(i), roundPicks.get(i));
            }
        }
    }

    // Feature: fantasy-draft-picker, Property 6: Pick Sequence Recalculation
    // Validates: Requirements 2.3, 3.5
    @Property(trials = 100)
    public void pickSequenceRecalculation(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 20) int numberOfRounds) {
        
        DraftManager draftManager = new DraftManager();
        List<Team> teams = createTeams(teamCount);
        
        // Generate sequence with serpentine flow
        DraftConfig serpentineConfig = new DraftConfig(FlowType.SERPENTINE, numberOfRounds);
        List<Integer> serpentineSequence = draftManager.generatePickSequence(serpentineConfig, teams);
        
        // Generate sequence with linear flow
        DraftConfig linearConfig = new DraftConfig(FlowType.LINEAR, numberOfRounds);
        List<Integer> linearSequence = draftManager.generatePickSequence(linearConfig, teams);
        
        // Verify sequences are different (unless teamCount is 1, which is edge case)
        if (teamCount > 1 && numberOfRounds > 1) {
            assertNotEquals("Changing flow type should produce different pick sequences",
                    serpentineSequence, linearSequence);
        }
        
        // Verify both sequences have the correct total length
        int expectedLength = teamCount * numberOfRounds;
        assertEquals("Serpentine sequence should have correct length",
                expectedLength, serpentineSequence.size());
        assertEquals("Linear sequence should have correct length",
                expectedLength, linearSequence.size());
        
        // Verify that changing the configuration produces a new sequence
        DraftConfig newSerpentineConfig = new DraftConfig(FlowType.SERPENTINE, numberOfRounds);
        List<Integer> newSerpentineSequence = draftManager.generatePickSequence(newSerpentineConfig, teams);
        
        // The new sequence should be equal to the original serpentine sequence
        assertEquals("Same configuration should produce same sequence",
                serpentineSequence, newSerpentineSequence);
    }

    // Feature: fantasy-draft-picker, Property 7: Pick Advancement
    // Validates: Requirements 4.2, 4.3, 7.5
    @Property(trials = 100)
    public void pickAdvancement(
            @InRange(minInt = 2, maxInt = 20) int teamCount,
            @InRange(minInt = 1, maxInt = 10) int numberOfRounds,
            @InRange(minInt = 1, maxInt = 10) int currentRound,
            @InRange(minInt = 1, maxInt = 20) int currentPickInRound) {
        
        // Ensure valid state
        if (currentRound > numberOfRounds) {
            return; // Skip invalid combinations
        }
        if (currentPickInRound > teamCount) {
            return; // Skip invalid combinations
        }
        
        DraftManager draftManager = new DraftManager();
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, numberOfRounds);
        DraftState state = new DraftState(currentRound, currentPickInRound, false);
        
        DraftState nextState = draftManager.advancePick(state, config, teamCount);
        
        // Verify advancement logic
        if (currentPickInRound < teamCount) {
            // Should advance to next pick in same round
            assertEquals("Should stay in same round", currentRound, nextState.getCurrentRound());
            assertEquals("Should advance to next pick", currentPickInRound + 1, nextState.getCurrentPickInRound());
            assertFalse("Should not be complete", nextState.isComplete());
        } else if (currentRound < numberOfRounds) {
            // Should advance to first pick of next round
            assertEquals("Should advance to next round", currentRound + 1, nextState.getCurrentRound());
            assertEquals("Should be first pick of round", 1, nextState.getCurrentPickInRound());
            assertFalse("Should not be complete", nextState.isComplete());
        } else {
            // Draft should be complete
            assertEquals("Should stay in same round", currentRound, nextState.getCurrentRound());
            assertEquals("Should stay at same pick", currentPickInRound, nextState.getCurrentPickInRound());
            assertTrue("Should be complete", nextState.isComplete());
        }
    }

    // Unit Tests for Edge Cases
    
    @Test
    public void testGetCurrentTeamIndex_FirstPickFirstRound_Serpentine() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 1, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        int teamIndex = draftManager.getCurrentTeamIndex(state, config, 10);
        assertEquals("First pick of first round should be team 0", 0, teamIndex);
    }
    
    @Test
    public void testGetCurrentTeamIndex_LastPickFirstRound_Serpentine() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 10, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        int teamIndex = draftManager.getCurrentTeamIndex(state, config, 10);
        assertEquals("Last pick of first round should be team 9", 9, teamIndex);
    }
    
    @Test
    public void testGetCurrentTeamIndex_FirstPickSecondRound_Serpentine() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(2, 1, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        int teamIndex = draftManager.getCurrentTeamIndex(state, config, 10);
        assertEquals("First pick of second round should be team 9 (reversed)", 9, teamIndex);
    }
    
    @Test
    public void testGetCurrentTeamIndex_Linear() {
        DraftManager draftManager = new DraftManager();
        DraftConfig config = new DraftConfig(FlowType.LINEAR, 10);
        
        // First round, first pick
        DraftState state1 = new DraftState(1, 1, false);
        assertEquals("Linear first pick should be team 0", 0, draftManager.getCurrentTeamIndex(state1, config, 10));
        
        // Second round, first pick (should still be team 0)
        DraftState state2 = new DraftState(2, 1, false);
        assertEquals("Linear second round first pick should be team 0", 0, draftManager.getCurrentTeamIndex(state2, config, 10));
    }
    
    @Test
    public void testAdvancePick_WithinRound() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 5, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        
        DraftState nextState = draftManager.advancePick(state, config, 10);
        
        assertEquals("Should stay in round 1", 1, nextState.getCurrentRound());
        assertEquals("Should advance to pick 6", 6, nextState.getCurrentPickInRound());
        assertFalse("Should not be complete", nextState.isComplete());
    }
    
    @Test
    public void testAdvancePick_RoundTransition() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 10, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 5);
        
        DraftState nextState = draftManager.advancePick(state, config, 10);
        
        assertEquals("Should advance to round 2", 2, nextState.getCurrentRound());
        assertEquals("Should be pick 1 of new round", 1, nextState.getCurrentPickInRound());
        assertFalse("Should not be complete", nextState.isComplete());
    }
    
    @Test
    public void testAdvancePick_DraftCompletion() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(5, 10, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 5);
        
        DraftState nextState = draftManager.advancePick(state, config, 10);
        
        assertEquals("Should stay in round 5", 5, nextState.getCurrentRound());
        assertEquals("Should stay at pick 10", 10, nextState.getCurrentPickInRound());
        assertTrue("Should be complete", nextState.isComplete());
    }
    
    @Test
    public void testAdvancePick_AlreadyComplete() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(5, 10, true);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 5);
        
        DraftState nextState = draftManager.advancePick(state, config, 10);
        
        assertTrue("Should remain complete", nextState.isComplete());
        assertEquals("Should not change state", state, nextState);
    }
    
    @Test
    public void testResetDraft() {
        DraftManager draftManager = new DraftManager();
        
        DraftState resetState = draftManager.resetDraft();
        
        assertEquals("Should be round 1", 1, resetState.getCurrentRound());
        assertEquals("Should be pick 1", 1, resetState.getCurrentPickInRound());
        assertFalse("Should not be complete", resetState.isComplete());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetCurrentTeamIndex_NullState() {
        DraftManager draftManager = new DraftManager();
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        draftManager.getCurrentTeamIndex(null, config, 10);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetCurrentTeamIndex_NullConfig() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 1, false);
        draftManager.getCurrentTeamIndex(state, null, 10);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetCurrentTeamIndex_InvalidTeamCount() {
        DraftManager draftManager = new DraftManager();
        DraftState state = new DraftState(1, 1, false);
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 10);
        draftManager.getCurrentTeamIndex(state, config, 0);
    }

    // Feature: fantasy-draft-picker, Property 13: Draft History Completeness
    // Validates: Requirements 8.1, 8.2, 8.3, 8.4
    @Property(trials = 100)
    public void draftHistoryCompleteness(
            @InRange(minInt = 1, maxInt = 20) int numberOfPicks) {
        
        DraftManager draftManager = new DraftManager();
        List<Pick> addedPicks = new ArrayList<>();
        
        // Add a sequence of picks to the history
        for (int i = 1; i <= numberOfPicks; i++) {
            Pick pick = new Pick(
                i,                              // pickNumber
                (i - 1) / 10 + 1,              // round (assuming 10 teams)
                ((i - 1) % 10) + 1,            // pickInRound
                "team" + (((i - 1) % 10) + 1), // teamId
                "player" + i,                   // playerId
                System.currentTimeMillis() + i  // timestamp
            );
            draftManager.addPickToHistory(pick);
            addedPicks.add(pick);
        }
        
        // Get the history
        List<Pick> history = draftManager.getPickHistory();
        
        // Verify completeness: all picks should be in history
        assertEquals("History should contain all added picks", numberOfPicks, history.size());
        
        // Verify chronological order and completeness
        for (int i = 0; i < numberOfPicks; i++) {
            Pick expected = addedPicks.get(i);
            Pick actual = history.get(i);
            
            assertEquals("Pick number should match", expected.getPickNumber(), actual.getPickNumber());
            assertEquals("Round should match", expected.getRound(), actual.getRound());
            assertEquals("Pick in round should match", expected.getPickInRound(), actual.getPickInRound());
            assertEquals("Team ID should match", expected.getTeamId(), actual.getTeamId());
            assertEquals("Player ID should match", expected.getPlayerId(), actual.getPlayerId());
            assertEquals("Timestamp should match", expected.getTimestamp(), actual.getTimestamp());
        }
    }

    // Unit Tests for Draft History Edge Cases
    
    @Test
    public void testGetPickHistory_EmptyHistory() {
        DraftManager draftManager = new DraftManager();
        
        List<Pick> history = draftManager.getPickHistory();
        
        assertNotNull("History should not be null", history);
        assertEquals("Empty history should have size 0", 0, history.size());
    }
    
    @Test
    public void testAddPickToHistory_SinglePick() {
        DraftManager draftManager = new DraftManager();
        Pick pick = new Pick(1, 1, 1, "team1", "player1", System.currentTimeMillis());
        
        draftManager.addPickToHistory(pick);
        List<Pick> history = draftManager.getPickHistory();
        
        assertEquals("History should contain 1 pick", 1, history.size());
        assertEquals("Pick should match", pick, history.get(0));
    }
    
    @Test
    public void testAddPickToHistory_LargeHistory() {
        DraftManager draftManager = new DraftManager();
        int largeCount = 300; // 20 teams * 15 rounds
        
        for (int i = 1; i <= largeCount; i++) {
            Pick pick = new Pick(i, (i - 1) / 20 + 1, ((i - 1) % 20) + 1, 
                    "team" + (((i - 1) % 20) + 1), "player" + i, System.currentTimeMillis() + i);
            draftManager.addPickToHistory(pick);
        }
        
        List<Pick> history = draftManager.getPickHistory();
        
        assertEquals("Large history should contain all picks", largeCount, history.size());
        
        // Verify first and last picks
        assertEquals("First pick number should be 1", 1, history.get(0).getPickNumber());
        assertEquals("Last pick number should be " + largeCount, largeCount, history.get(largeCount - 1).getPickNumber());
    }
    
    @Test
    public void testClearHistory() {
        DraftManager draftManager = new DraftManager();
        
        // Add some picks
        for (int i = 1; i <= 10; i++) {
            Pick pick = new Pick(i, 1, i, "team" + i, "player" + i, System.currentTimeMillis() + i);
            draftManager.addPickToHistory(pick);
        }
        
        assertEquals("History should have 10 picks", 10, draftManager.getPickHistory().size());
        
        // Clear history
        draftManager.clearHistory();
        
        List<Pick> history = draftManager.getPickHistory();
        assertEquals("Cleared history should be empty", 0, history.size());
    }
    
    @Test
    public void testGetPickHistory_ReturnsDefensiveCopy() {
        DraftManager draftManager = new DraftManager();
        Pick pick1 = new Pick(1, 1, 1, "team1", "player1", System.currentTimeMillis());
        draftManager.addPickToHistory(pick1);
        
        List<Pick> history1 = draftManager.getPickHistory();
        List<Pick> history2 = draftManager.getPickHistory();
        
        // Modify the first returned list
        Pick pick2 = new Pick(2, 1, 2, "team2", "player2", System.currentTimeMillis());
        history1.add(pick2);
        
        // Verify the second list is not affected
        assertEquals("Original history should still have 1 pick", 1, history2.size());
        assertEquals("Manager's history should still have 1 pick", 1, draftManager.getPickHistory().size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddPickToHistory_NullPick() {
        DraftManager draftManager = new DraftManager();
        draftManager.addPickToHistory(null);
    }

    // Helper method to create teams
    private List<Team> createTeams(int count) {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            teams.add(new Team("team" + i, "Team " + i, i + 1));
        }
        return teams;
    }
}
