package com.fantasydraft.picker.managers;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;

/**
 * Coordinates operations across multiple managers to maintain consistency.
 * Handles complex operations like draft reset that affect multiple components.
 */
public class DraftCoordinator {
    
    private final DraftManager draftManager;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    
    /**
     * Create a new DraftCoordinator with the specified managers.
     * 
     * @param draftManager The draft manager
     * @param playerManager The player manager
     * @param teamManager The team manager
     */
    public DraftCoordinator(DraftManager draftManager, PlayerManager playerManager, TeamManager teamManager) {
        if (draftManager == null || playerManager == null || teamManager == null) {
            throw new IllegalArgumentException("All managers must be non-null");
        }
        this.draftManager = draftManager;
        this.playerManager = playerManager;
        this.teamManager = teamManager;
    }
    
    /**
     * Reset the draft to its initial state.
     * Clears all picks and player draft states while preserving team configuration and draft settings.
     * Also removes any custom players that were added during the draft.
     * 
     * Requirements: 9.1, 9.2, 9.3, 9.4
     * 
     * @param config The draft configuration to preserve
     * @return New draft state at the beginning (round 1, pick 1)
     */
    public DraftState resetDraft(DraftConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Draft config cannot be null");
        }
        
        // Remove custom players (rank = 999) before resetting
        playerManager.removeCustomPlayers();
        
        // Clear all completed picks (Requirement 9.1)
        draftManager.clearHistory();
        
        // Mark all previously drafted players as available (Requirement 9.2)
        playerManager.resetPlayers();
        
        // Return current pick to first position (Requirement 9.3)
        // Team configuration and draft settings are preserved by not modifying them (Requirement 9.4)
        return draftManager.resetDraft(config);
    }
}
