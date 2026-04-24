package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.List;

public class DraftManager {

    private List<Pick> pickHistory;

    public DraftManager() {
        this.pickHistory = new ArrayList<>();
    }

    /**
     * Calculate the team index (0-based) for the current pick based on draft state and configuration.
     * 
     * @param state Current draft state
     * @param config Draft configuration
     * @param teamCount Number of teams in the draft
     * @return Zero-based team index for the current pick
     */
    public int getCurrentTeamIndex(DraftState state, DraftConfig config, int teamCount) {
        if (state == null || config == null || teamCount <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        int currentRound = state.getCurrentRound();
        int currentPickInRound = state.getCurrentPickInRound();

        if (currentRound < 1 || currentPickInRound < 1 || currentPickInRound > teamCount) {
            throw new IllegalArgumentException("Invalid draft state");
        }

        // If keeper linear rounds are configured and we're in a linear round, always use linear
        int keeperRounds = config.getKeeperLinearRounds();
        if (keeperRounds > 0 && currentRound <= keeperRounds) {
            return currentPickInRound - 1;
        }

        if (config.getFlowType() == FlowType.SERPENTINE) {
            // When keeper rounds are set, offset the serpentine so the first non-keeper round is treated as odd
            int effectiveRound = keeperRounds > 0 ? currentRound - keeperRounds : currentRound;
            
            // Odd rounds: 1, 2, 3, ..., N (pickInRound - 1 gives 0-based index)
            // Even rounds: N, N-1, N-2, ..., 1 (teamCount - pickInRound gives 0-based index)
            if (effectiveRound % 2 == 1) {
                return currentPickInRound - 1;
            } else {
                return teamCount - currentPickInRound;
            }
        } else {
            // Linear: always 1, 2, 3, ..., N
            return currentPickInRound - 1;
        }
    }

    /**
     * Advance to the next pick in the draft sequence.
     * 
     * @param state Current draft state
     * @param config Draft configuration
     * @param teamCount Number of teams in the draft
     * @return New draft state after advancing
     */
    public DraftState advancePick(DraftState state, DraftConfig config, int teamCount) {
        if (state == null || config == null || teamCount <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        if (state.isComplete()) {
            return state; // Already complete, don't advance
        }

        int currentRound = state.getCurrentRound();
        int currentPickInRound = state.getCurrentPickInRound();
        int numberOfRounds = config.getNumberOfRounds();

        // Advance to next pick
        if (currentPickInRound < teamCount) {
            // Still picks left in this round
            return new DraftState(currentRound, currentPickInRound + 1, false);
        } else {
            // Round is complete, move to next round
            if (currentRound < numberOfRounds) {
                return new DraftState(currentRound + 1, 1, false);
            } else {
                // Draft is complete
                return new DraftState(currentRound, currentPickInRound, true);
            }
        }
    }

    /**
     * Generate the complete pick sequence for the draft.
     * Returns a list of team indices (0-based) in the order they pick.
     * 
     * @param config Draft configuration
     * @param teams List of teams (must be sorted by draft position)
     * @return List of team indices representing the pick sequence
     */
    public List<Integer> generatePickSequence(DraftConfig config, List<Team> teams) {
        if (config == null || teams == null || teams.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        int teamCount = teams.size();
        int numberOfRounds = config.getNumberOfRounds();
        List<Integer> pickSequence = new ArrayList<>();

        for (int round = 1; round <= numberOfRounds; round++) {
            // If keeper linear rounds are configured and we're in a keeper round, force linear
            int keeperRounds = config.getKeeperLinearRounds();
            if (keeperRounds > 0 && round <= keeperRounds) {
                // Keeper rounds are always linear
                for (int i = 0; i < teamCount; i++) {
                    pickSequence.add(i);
                }
            } else if (config.getFlowType() == FlowType.SERPENTINE) {
                // Offset serpentine so first non-keeper round is treated as odd
                int effectiveRound = keeperRounds > 0 ? round - keeperRounds : round;
                
                if (effectiveRound % 2 == 1) {
                    // Odd round: 0, 1, 2, ..., N-1
                    for (int i = 0; i < teamCount; i++) {
                        pickSequence.add(i);
                    }
                } else {
                    // Even round: N-1, N-2, ..., 1, 0
                    for (int i = teamCount - 1; i >= 0; i--) {
                        pickSequence.add(i);
                    }
                }
            } else {
                // Linear: always 0, 1, 2, ..., N-1
                for (int i = 0; i < teamCount; i++) {
                    pickSequence.add(i);
                }
            }
        }

        return pickSequence;
    }

    /**
     * Reset the draft to its initial state.
     * 
     * @param config Draft configuration (to check if first round should be skipped)
     * @return New draft state at the beginning
     */
    public DraftState resetDraft(DraftConfig config) {
        // Always start at round 1, pick 1 (skipFirstRound now means round 1 is linear, not skipped)
        return new DraftState(1, 1, false);
    }

    /**
     * Reset the draft to its initial state (legacy method).
     * 
     * @return New draft state at the beginning (round 1, pick 1)
     */
    public DraftState resetDraft() {
        return new DraftState(1, 1, false);
    }

    /**
     * Add a pick to the draft history.
     * 
     * @param pick The pick to add to history
     */
    public void addPickToHistory(Pick pick) {
        if (pick == null) {
            throw new IllegalArgumentException("Pick cannot be null");
        }
        pickHistory.add(pick);
    }

    /**
     * Get the complete draft history.
     * 
     * @return List of all picks in chronological order
     */
    public List<Pick> getPickHistory() {
        return new ArrayList<>(pickHistory);
    }

    /**
     * Clear the draft history.
     */
    public void clearHistory() {
        pickHistory.clear();
    }
}
