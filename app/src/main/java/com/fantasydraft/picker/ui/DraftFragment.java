package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.fantasydraft.picker.R;
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
import com.fantasydraft.picker.utils.DraftCsvExporter;
import com.fantasydraft.picker.utils.PositionColors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment containing the draft board UI.
 * Displays current pick information, best available player, recent picks, and draft action buttons.
 * Requirements: 2.1, 5.1
 */
public class DraftFragment extends Fragment {
    
    // UI Components - Current Pick Section
    private TextView textLeagueName;
    private TextView textOverallPick;
    private TextView textRoundPick;
    private TextView textCurrentTeam;
    private Button buttonMakePick;
    
    // UI Components - Best Available Player Section
    private Button buttonDraftBestPlayer;
    private TextView textBestPlayerName;
    private TextView textBestPlayerPosition;
    private TextView textBestPlayerStats;
    
    // UI Components - Position Counts
    private CheckBox checkboxCurrentTeam;
    private TextView textCountWR;
    private TextView textCountRB;
    private TextView textCountQB;
    private TextView textCountTE;
    private TextView textCountDST;
    private TextView textCountK;
    
    // Position counts mode: true = league-wide, false = current team only
    private boolean showLeagueCounts = false;
    
    // UI Components - Recent Picks Section
    private LinearLayout pickSlot1;
    private LinearLayout pickSlot2;
    private LinearLayout pickSlot3;
    private TextView textPick1Number;
    private TextView textPick1Player;
    private TextView textPick1Details;
    private TextView textPick2Number;
    private TextView textPick2Player;
    private TextView textPick2Details;
    private TextView textPick3Number;
    private TextView textPick3Player;
    private TextView textPick3Details;
    
    // UI Components - Action Buttons
    private Button buttonViewHistory;
    private Button buttonExportCsv;
    private Button buttonResetDraft;
    
;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_draft, container, false);
        
        // Initialize all UI component references
        initializeViews(view);
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh UI with current data when fragment is displayed
        // Requirements: 11.2
        // Add a small delay to ensure MainActivity's data is fully updated after undo
        if (getView() != null) {
            getView().postDelayed(this::updateUI, 100);
        } else {
            updateUI();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Notify MainActivity to save state when fragment is paused
        // Requirements: 11.2
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            mainActivity.saveDraftState();
        }
    }
    
    /**
     * Initialize all UI view references.
     * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
     */
    private void initializeViews(View view) {
        // Current Pick Section
        textLeagueName = view.findViewById(R.id.text_league_name);
        textOverallPick = view.findViewById(R.id.text_overall_pick);
        textRoundPick = view.findViewById(R.id.text_round_pick);
        textCurrentTeam = view.findViewById(R.id.text_current_team);
        buttonMakePick = view.findViewById(R.id.button_make_pick);
        
        // Best Available Player Section
        buttonDraftBestPlayer = view.findViewById(R.id.button_draft_best_player);
        textBestPlayerName = view.findViewById(R.id.text_best_player_name);
        textBestPlayerPosition = view.findViewById(R.id.text_best_player_position);
        textBestPlayerStats = view.findViewById(R.id.text_best_player_stats);
        
        // Position Counts
        checkboxCurrentTeam = view.findViewById(R.id.checkbox_current_team);
        textCountWR = view.findViewById(R.id.text_count_wr);
        textCountRB = view.findViewById(R.id.text_count_rb);
        textCountQB = view.findViewById(R.id.text_count_qb);
        textCountTE = view.findViewById(R.id.text_count_te);
        textCountDST = view.findViewById(R.id.text_count_dst);
        textCountK = view.findViewById(R.id.text_count_k);
        
        // Set up checkbox change listener
        checkboxCurrentTeam.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showLeagueCounts = !isChecked;
            updatePositionCounts();
        });
        
        // Recent Picks Section
        pickSlot1 = view.findViewById(R.id.pick_slot_1);
        pickSlot2 = view.findViewById(R.id.pick_slot_2);
        pickSlot3 = view.findViewById(R.id.pick_slot_3);
        textPick1Number = view.findViewById(R.id.text_pick_1_number);
        textPick1Player = view.findViewById(R.id.text_pick_1_player);
        textPick1Details = view.findViewById(R.id.text_pick_1_details);
        textPick2Number = view.findViewById(R.id.text_pick_2_number);
        textPick2Player = view.findViewById(R.id.text_pick_2_player);
        textPick2Details = view.findViewById(R.id.text_pick_2_details);
        textPick3Number = view.findViewById(R.id.text_pick_3_number);
        textPick3Player = view.findViewById(R.id.text_pick_3_player);
        textPick3Details = view.findViewById(R.id.text_pick_3_details);
        
        // Action Buttons
        buttonViewHistory = view.findViewById(R.id.button_view_history);
        buttonExportCsv = view.findViewById(R.id.button_export_csv);
        buttonResetDraft = view.findViewById(R.id.button_reset_draft);
        
        // Set up button click handlers
        buttonViewHistory.setOnClickListener(v -> launchDraftHistoryActivity());
        buttonResetDraft.setOnClickListener(v -> showResetConfirmationDialog());
    }
    
    /**
     * Get MainActivity reference safely.
     * Returns null if the activity is not MainActivity or is not available.
     * Requirements: 5.1
     */
    @Nullable
    private MainActivity getMainActivity() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return (MainActivity) activity;
        }
        return null;
    }
    
    /**
     * Update all UI components with current draft state.
     * Requirements: 2.2, 2.3, 2.4, 2.7
     */
    public void updateUI() {
        updateCurrentPick();
        updateBestAvailable();
        updatePositionCounts();
        updateRecentPicks();
        updateDraftButtons();
    }
    
    /**
     * Update the current pick display section.
     * Requirements: 4.1, 2.4
     */
    private void updateCurrentPick() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        DraftState currentState = mainActivity.getCurrentState();
        DraftConfig currentConfig = mainActivity.getCurrentConfig();
        List<Team> teams = mainActivity.getTeams();
        DraftManager draftManager = mainActivity.getDraftManager();
        
        if (currentState != null && currentConfig != null && teams != null) {
            // Update league name
            String leagueName = currentConfig.getLeagueName();
            if (leagueName != null && !leagueName.isEmpty()) {
                textLeagueName.setText(leagueName);
            } else {
                textLeagueName.setText("My Fantasy League");
            }
            
            // Calculate overall pick number
            int currentPickNumber = ((currentState.getCurrentRound() - 1) * teams.size()) 
                    + currentState.getCurrentPickInRound();
            int totalPicks = currentConfig.getNumberOfRounds() * teams.size();
            
            // Update overall pick display
            String overallPick = "Pick " + currentPickNumber + " of " + totalPicks;
            textOverallPick.setText(overallPick);
            
            // Update round and pick display with total rounds
            String roundPick = "Round " + currentState.getCurrentRound() + 
                    " of " + currentConfig.getNumberOfRounds() +
                    ", Pick " + currentState.getCurrentPickInRound();
            textRoundPick.setText(roundPick);
            
            // Get current team
            if (!teams.isEmpty()) {
                int teamIndex = draftManager.getCurrentTeamIndex(
                        currentState, currentConfig, teams.size());
                
                if (teamIndex >= 0 && teamIndex < teams.size()) {
                    Team currentTeam = teams.get(teamIndex);
                    textCurrentTeam.setText("Team: " + currentTeam.getName());
                }
            }
        }
    }
    
    /**
     * Update the best available player display section.
     * Requirements: 5.1, 5.2
     */
    private void updateBestAvailable() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        PlayerManager playerManager = mainActivity.getPlayerManager();
        Player bestPlayer = playerManager.getBestAvailable(playerManager.getPlayers());
        
        if (bestPlayer != null) {
            textBestPlayerName.setText(bestPlayer.getName());
            textBestPlayerPosition.setText(bestPlayer.getPosition() + " - #" + bestPlayer.getRank());
            
            // Display stats if available
            if (bestPlayer.getLastYearStats() != null && !bestPlayer.getLastYearStats().isEmpty()) {
                textBestPlayerStats.setText(bestPlayer.getLastYearStats());
                textBestPlayerStats.setVisibility(View.VISIBLE);
            } else {
                textBestPlayerStats.setVisibility(View.GONE);
            }
            
            // Set player name to black
            textBestPlayerName.setTextColor(0xFF000000);
            
            // Set position text to FFL position color
            int positionColor = PositionColors.getDarkColorForPosition(bestPlayer.getPosition());
            textBestPlayerPosition.setTextColor(positionColor);
            
            // Set button background to position color
            int buttonColor = PositionColors.getColorForPosition(bestPlayer.getPosition());
            buttonDraftBestPlayer.setBackgroundColor(buttonColor);
            buttonDraftBestPlayer.setTextColor(0xFF333333); // Dark grey text
            
            // Enable draft button
            buttonDraftBestPlayer.setEnabled(true);
        } else {
            textBestPlayerName.setText("No players available");
            textBestPlayerPosition.setText("--");
            textBestPlayerStats.setVisibility(View.GONE);
            
            // Reset to default colors
            textBestPlayerName.setTextColor(0xFF000000);
            textBestPlayerPosition.setTextColor(0xFF666666);
            
            // Reset button to default background
            buttonDraftBestPlayer.setBackgroundColor(0xFFCCCCCC);
            
            // Disable draft button
            buttonDraftBestPlayer.setEnabled(false);
        }
    }
    
    /**
     * Update the position counts display section.
     */
    private void updatePositionCounts() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        List<Pick> pickHistory = mainActivity.getPickHistory();
        PlayerManager playerManager = mainActivity.getPlayerManager();
        DraftState currentState = mainActivity.getCurrentState();
        DraftConfig currentConfig = mainActivity.getCurrentConfig();
        List<Team> teams = mainActivity.getTeams();
        DraftManager draftManager = mainActivity.getDraftManager();
        
        // Count positions
        int wrCount = 0;
        int rbCount = 0;
        int qbCount = 0;
        int teCount = 0;
        int dstCount = 0;
        int kCount = 0;
        
        if (pickHistory != null && playerManager != null) {
            // Determine which team to filter by if in "My Team" mode
            String currentTeamId = null;
            if (!showLeagueCounts && currentState != null && currentConfig != null && teams != null) {
                int teamIndex = draftManager.getCurrentTeamIndex(
                        currentState, currentConfig, teams.size());
                if (teamIndex >= 0 && teamIndex < teams.size()) {
                    currentTeamId = teams.get(teamIndex).getId();
                }
            }
            
            for (Pick pick : pickHistory) {
                // Skip picks not from current team if in "My Team" mode
                if (!showLeagueCounts && currentTeamId != null && !pick.getTeamId().equals(currentTeamId)) {
                    continue;
                }
                
                Player player = playerManager.getPlayerById(pick.getPlayerId());
                if (player != null) {
                    String position = player.getPosition();
                    switch (position) {
                        case "WR":
                            wrCount++;
                            break;
                        case "RB":
                            rbCount++;
                            break;
                        case "QB":
                            qbCount++;
                            break;
                        case "TE":
                            teCount++;
                            break;
                        case "DST":
                            dstCount++;
                            break;
                        case "K":
                            kCount++;
                            break;
                    }
                }
            }
        }
        
        // Update UI
        textCountWR.setText(String.valueOf(wrCount));
        textCountRB.setText(String.valueOf(rbCount));
        textCountQB.setText(String.valueOf(qbCount));
        textCountTE.setText(String.valueOf(teCount));
        textCountDST.setText(String.valueOf(dstCount));
        textCountK.setText(String.valueOf(kCount));
    }
    
    /**
     * Update the recent picks display section.
     * Requirements: 8.1
     */
    private void updateRecentPicks() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        List<Pick> pickHistory = mainActivity.getPickHistory();
        
        if (pickHistory == null || pickHistory.isEmpty()) {
            // No picks yet - clear all slots
            clearPickSlot(textPick1Number, textPick1Player, textPick1Details);
            clearPickSlot(textPick2Number, textPick2Player, textPick2Details);
            clearPickSlot(textPick3Number, textPick3Player, textPick3Details);
            
            // Set first slot message
            textPick1Player.setText("No picks yet");
            return;
        }
        
        // Get the 3 most recent picks (in reverse order - most recent first)
        int totalPicks = pickHistory.size();
        
        // Pick 1 (most recent)
        if (totalPicks >= 1) {
            Pick pick1 = pickHistory.get(totalPicks - 1);
            updatePickSlot(pick1, textPick1Number, textPick1Player, textPick1Details);
        } else {
            clearPickSlot(textPick1Number, textPick1Player, textPick1Details);
        }
        
        // Pick 2 (second most recent)
        if (totalPicks >= 2) {
            Pick pick2 = pickHistory.get(totalPicks - 2);
            updatePickSlot(pick2, textPick2Number, textPick2Player, textPick2Details);
        } else {
            clearPickSlot(textPick2Number, textPick2Player, textPick2Details);
        }
        
        // Pick 3 (third most recent)
        if (totalPicks >= 3) {
            Pick pick3 = pickHistory.get(totalPicks - 3);
            updatePickSlot(pick3, textPick3Number, textPick3Player, textPick3Details);
        } else {
            clearPickSlot(textPick3Number, textPick3Player, textPick3Details);
        }
    }
    
    /**
     * Update a single pick slot with pick information.
     */
    private void updatePickSlot(Pick pick, TextView numberView, TextView playerView, TextView detailsView) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        PlayerManager playerManager = mainActivity.getPlayerManager();
        List<Team> teams = mainActivity.getTeams();
        
        // Set pick number
        numberView.setText(String.valueOf(pick.getPickNumber()));
        
        // Get player info
        Player player = playerManager.getPlayerById(pick.getPlayerId());
        if (player != null) {
            playerView.setText(player.getName());
            
            // Apply position-based background color to the pick slot
            LinearLayout pickSlot = (LinearLayout) numberView.getParent();
            int backgroundColor = PositionColors.getColorForPosition(player.getPosition());
            pickSlot.setBackgroundColor(backgroundColor);
            
            // Get team info
            Team team = null;
            for (Team t : teams) {
                if (t.getId().equals(pick.getTeamId())) {
                    team = t;
                    break;
                }
            }
            
            String details = player.getPosition() + " - " + player.getNflTeam();
            if (team != null) {
                details += " → " + team.getName();
            }
            detailsView.setText(details);
        } else {
            playerView.setText("Unknown Player");
            detailsView.setText("");
        }
    }
    
    /**
     * Clear a pick slot (show placeholder).
     */
    private void clearPickSlot(TextView numberView, TextView playerView, TextView detailsView) {
        numberView.setText("--");
        playerView.setText("--");
        detailsView.setText("");
        
        // Reset background color to default
        LinearLayout pickSlot = (LinearLayout) numberView.getParent();
        pickSlot.setBackgroundColor(0x00000000); // Transparent
    }
    
    /**
     * Update draft button states based on draft completion.
     * Disable all draft buttons except reset when draft is complete.
     */
    private void updateDraftButtons() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        DraftState currentState = mainActivity.getCurrentState();
        boolean isDraftComplete = currentState != null && currentState.isComplete();
        
        // Disable draft action buttons when draft is complete
        buttonMakePick.setEnabled(!isDraftComplete);
        buttonMakePick.setAlpha(isDraftComplete ? 0.5f : 1.0f);
        
        buttonDraftBestPlayer.setEnabled(!isDraftComplete);
        buttonDraftBestPlayer.setAlpha(isDraftComplete ? 0.5f : 1.0f);
        
        // Update click handlers to show message when disabled
        if (isDraftComplete) {
            buttonMakePick.setOnClickListener(v -> {
                Toast.makeText(getContext(), 
                        "Draft is complete. Reset draft to start a new one.", 
                        Toast.LENGTH_SHORT).show();
            });
            
            buttonDraftBestPlayer.setOnClickListener(v -> {
                Toast.makeText(getContext(), 
                        "Draft is complete. Reset draft to start a new one.", 
                        Toast.LENGTH_SHORT).show();
            });
        } else {
            // Re-enable normal click handlers
            buttonMakePick.setOnClickListener(v -> showPlayerSelectionDialog());
            buttonDraftBestPlayer.setOnClickListener(v -> draftBestAvailablePlayer());
        }
        
        // Reset button is always enabled
        buttonResetDraft.setEnabled(true);
        buttonResetDraft.setAlpha(1.0f);
        
        // View History button is always enabled
        buttonViewHistory.setEnabled(true);
        buttonViewHistory.setAlpha(1.0f);
        
        // Export CSV button is only enabled when draft is complete
        buttonExportCsv.setEnabled(isDraftComplete);
        buttonExportCsv.setAlpha(isDraftComplete ? 1.0f : 0.5f);
        
        // Update click handler for export button when disabled
        if (!isDraftComplete) {
            buttonExportCsv.setOnClickListener(v -> {
                Toast.makeText(getContext(), 
                        "Complete the draft before exporting to CSV.", 
                        Toast.LENGTH_SHORT).show();
            });
        } else {
            // Re-enable normal click handler
            buttonExportCsv.setOnClickListener(v -> exportDraftToCSV());
        }
    }
    
    /**
     * Show player selection dialog for drafting a player.
     * Requirements: 2.7
     */
    private void showPlayerSelectionDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        PlayerManager playerManager = mainActivity.getPlayerManager();
        List<Player> availablePlayers = playerManager.getPlayers();
        
        PlayerSelectionDialog dialog = new PlayerSelectionDialog(
                getActivity(),
                availablePlayers,
                this::handlePlayerSelection
        );
        
        dialog.show();
    }
    
    /**
     * Handle player selection from the draft dialog.
     * Requirements: 2.7
     */
    private void handlePlayerSelection(Player selectedPlayer) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        try {
            // Get managers and state from MainActivity
            DraftManager draftManager = mainActivity.getDraftManager();
            PlayerManager playerManager = mainActivity.getPlayerManager();
            DraftState currentState = mainActivity.getCurrentState();
            DraftConfig currentConfig = mainActivity.getCurrentConfig();
            List<Team> teams = mainActivity.getTeams();
            List<Pick> pickHistory = mainActivity.getPickHistory();
            
            // Validate player is not already drafted
            if (selectedPlayer.isDrafted()) {
                Toast.makeText(getContext(), R.string.error_player_already_drafted, 
                        Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get current team
            int teamIndex = draftManager.getCurrentTeamIndex(
                    currentState, currentConfig, teams.size());
            
            if (teamIndex < 0 || teamIndex >= teams.size()) {
                Toast.makeText(getContext(), "Error: Invalid team index", 
                        Toast.LENGTH_SHORT).show();
                return;
            }
            
            Team currentTeam = teams.get(teamIndex);
            
            // Draft the player
            playerManager.draftPlayer(selectedPlayer.getId(), currentTeam.getId());
            
            // Add player to team roster
            currentTeam.getRoster().add(selectedPlayer);
            
            // Create pick record
            int overallPickNumber = ((currentState.getCurrentRound() - 1) * teams.size()) 
                    + currentState.getCurrentPickInRound();
            
            Pick pick = new Pick();
            pick.setPickNumber(overallPickNumber);
            pick.setRound(currentState.getCurrentRound());
            pick.setPickInRound(currentState.getCurrentPickInRound());
            pick.setTeamId(currentTeam.getId());
            pick.setPlayerId(selectedPlayer.getId());
            pick.setTimestamp(System.currentTimeMillis());
            
            // Add to history
            pickHistory.add(pick);
            draftManager.addPickToHistory(pick);
            
            // Advance to next pick
            DraftState newState = draftManager.advancePick(currentState, currentConfig, teams.size());
            mainActivity.setCurrentState(newState);
            
            // Save state through MainActivity
            mainActivity.saveDraftState();
            
            // Update UI
            updateUI();
            
            // Check if draft is complete and show completion dialog
            if (newState.isComplete()) {
                showDraftCompletionDialog();
            }
            
            Toast.makeText(getContext(), 
                    selectedPlayer.getName() + " drafted by " + currentTeam.getName(), 
                    Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error drafting player: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Draft the best available player with confirmation.
     * Requirements: 2.7
     */
    private void draftBestAvailablePlayer() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        PlayerManager playerManager = mainActivity.getPlayerManager();
        Player bestPlayer = playerManager.getBestAvailable(playerManager.getPlayers());
        
        if (bestPlayer == null) {
            Toast.makeText(getContext(), "No players available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (bestPlayer.isDrafted()) {
            Toast.makeText(getContext(), R.string.error_player_already_drafted, 
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        new AlertDialog.Builder(getActivity())
                .setTitle("Draft Best Available")
                .setMessage("Draft " + bestPlayer.getName() + " (" + bestPlayer.getPosition() + ")?")
                .setPositiveButton("Draft", (dialog, which) -> {
                    handlePlayerSelection(bestPlayer);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show dialog to draft a custom/unlisted player.
     * Requirements: 2.7
     */
    public void showCustomPlayerDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom_player, null);
        
        com.google.android.material.textfield.TextInputEditText inputName = 
                dialogView.findViewById(R.id.input_player_name);
        com.google.android.material.textfield.TextInputEditText inputPosition = 
                dialogView.findViewById(R.id.input_player_position);
        com.google.android.material.textfield.TextInputEditText inputTeam = 
                dialogView.findViewById(R.id.input_player_team);
        
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.button_draft).setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String position = inputPosition.getText().toString().trim().toUpperCase();
            String team = inputTeam.getText().toString().trim().toUpperCase();
            
            // Validate inputs
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Player name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (position.isEmpty()) {
                Toast.makeText(getContext(), "Position is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (team.isEmpty()) {
                Toast.makeText(getContext(), "NFL team is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create custom player
            Player customPlayer = new Player();
            customPlayer.setId("custom_" + System.currentTimeMillis());
            customPlayer.setName(name);
            customPlayer.setPosition(position);
            customPlayer.setNflTeam(team);
            customPlayer.setRank(999); // Low rank for custom players
            customPlayer.setPffRank(0);
            customPlayer.setPositionRank(0);
            customPlayer.setLastYearStats("Custom Player");
            customPlayer.setInjuryStatus("HEALTHY");
            customPlayer.setEspnId("");
            
            // Add to player manager
            PlayerManager playerManager = mainActivity.getPlayerManager();
            playerManager.addPlayer(customPlayer);
            
            // Draft the custom player
            handlePlayerSelection(customPlayer);
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    /**
     * Show confirmation dialog before resetting the draft.
     * Requirements: 2.5
     */
    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Reset Draft Board")
                .setMessage("Are you sure you want to reset the draft? This will clear all picks.")
                .setPositiveButton("Confirm", (dialog, which) -> resetDraft())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show dialog when draft is complete.
     * Requirements: 2.8
     */
    private void showDraftCompletionDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Draft Complete!")
                .setMessage("The draft is complete! You can now export the draft results to a CSV file.")
                .setPositiveButton("Export Now", (dialog, which) -> exportDraftToCSV())
                .setNegativeButton("Later", null)
                .setCancelable(true)
                .show();
    }
    
    /**
     * Reset the draft to initial state.
     * Requirements: 2.5, 2.6
     */
    private void resetDraft() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        try {
            // Get managers and state from MainActivity
            DraftCoordinator draftCoordinator = mainActivity.getDraftCoordinator();
            DraftConfig currentConfig = mainActivity.getCurrentConfig();
            List<Pick> pickHistory = mainActivity.getPickHistory();
            
            // Reset draft using coordinator
            DraftState newState = draftCoordinator.resetDraft(currentConfig);
            mainActivity.setCurrentState(newState);
            
            // Clear pick history
            pickHistory.clear();
            
            // Save the reset state
            mainActivity.saveDraftState();
            
            // Update UI
            updateUI();
            
            Toast.makeText(getContext(), "Draft reset successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error resetting draft: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Launch the draft history activity to view all picks.
     * Requirements: 2.6
     */
    private void launchDraftHistoryActivity() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        PlayerManager playerManager = mainActivity.getPlayerManager();
        List<Pick> pickHistory = mainActivity.getPickHistory();
        List<Team> teams = mainActivity.getTeams();
        
        Intent intent = new Intent(getActivity(), DraftHistoryActivity.class);
        intent.putParcelableArrayListExtra(DraftHistoryActivity.EXTRA_PICK_HISTORY, 
                new ArrayList<>(pickHistory));
        intent.putParcelableArrayListExtra(DraftHistoryActivity.EXTRA_TEAMS, 
                new ArrayList<>(teams));
        intent.putParcelableArrayListExtra(DraftHistoryActivity.EXTRA_PLAYERS, 
                new ArrayList<>(playerManager.getPlayers()));
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_HISTORY);
    }
    
    /**
     * Export draft history to CSV file.
     * Requirements: 2.8
     */
    private void exportDraftToCSV() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        List<Pick> pickHistory = mainActivity.getPickHistory();
        
        // Check if there are any picks to export
        if (pickHistory == null || pickHistory.isEmpty()) {
            Toast.makeText(getContext(), "No draft picks to export", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Perform export directly (no permission needed for app's external files directory)
        performCsvExport();
    }
    
    /**
     * Perform the actual CSV export operation.
     * Requirements: 2.8
     */
    private void performCsvExport() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        try {
            DraftConfig currentConfig = mainActivity.getCurrentConfig();
            List<Pick> pickHistory = mainActivity.getPickHistory();
            List<Team> teams = mainActivity.getTeams();
            PlayerManager playerManager = mainActivity.getPlayerManager();
            
            String leagueName = currentConfig != null ? currentConfig.getLeagueName() : "My League";
            
            File csvFile = DraftCsvExporter.exportToCSV(
                    getActivity(),
                    pickHistory,
                    teams,
                    playerManager.getPlayers(),
                    leagueName
            );
            
            // Show success message
            Toast.makeText(getContext(),
                    "Draft exported successfully!",
                    Toast.LENGTH_SHORT).show();
            
            // Prompt user to open the file
            openCsvFile(csvFile);
            
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error exporting draft: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Open the CSV file with an external app.
     * Requirements: 2.8
     */
    private void openCsvFile(File csvFile) {
        try {
            // Create a content URI using FileProvider
            android.net.Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                    getActivity(),
                    "com.fantasydraft.picker.fileprovider",
                    csvFile
            );
            
            // Create intent to view the file - use text/plain for better compatibility
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // Force the chooser to show even if there's a default app
            startActivity(Intent.createChooser(intent, "Open CSV file"));
            
        } catch (Exception e) {
            // Fallback: show the file location
            Toast.makeText(getContext(),
                    "File saved to: " + csvFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
