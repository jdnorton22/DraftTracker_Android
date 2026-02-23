package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
    private TextView textBestPlayerPositionBadge;
    private TextView textBestPlayerPosition;
    private TextView textBestPlayerInjuryStatus;
    private TextView textBestPlayerStats;
    
    // UI Components - Position Counts
    private ImageButton buttonToggleView;
    private TextView textViewMode;
    private TextView textPositionWR;
    private TextView textPositionRB;
    private TextView textPositionQB;
    private TextView textPositionTE;
    private TextView textPositionDST;
    private TextView textPositionK;
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
    private TextView textPick1InjuryStatus;
    private TextView textPick1Details;
    private TextView textPick1Rank;
    private TextView textPick1Adp;
    private TextView textPick1PositionRank;
    private TextView textPick1Stats;
    private TextView textPick2Number;
    private TextView textPick2Player;
    private TextView textPick2InjuryStatus;
    private TextView textPick2Details;
    private TextView textPick2Rank;
    private TextView textPick2Adp;
    private TextView textPick2PositionRank;
    private TextView textPick2Stats;
    private TextView textPick3Number;
    private TextView textPick3Player;
    private TextView textPick3InjuryStatus;
    private TextView textPick3Details;
    private TextView textPick3Rank;
    private TextView textPick3Adp;
    private TextView textPick3PositionRank;
    private TextView textPick3Stats;
    
    // UI Components - Action Buttons
    private TextView buttonViewHistory;
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
        buttonMakePick.setTextColor(0xFFFFFFFF); // Force white text color
        
        // Best Available Player Section
        buttonDraftBestPlayer = view.findViewById(R.id.button_draft_best_player);
        buttonDraftBestPlayer.setTextColor(0xFFFFFFFF); // Force white text color
        textBestPlayerName = view.findViewById(R.id.text_best_player_name);
        textBestPlayerPositionBadge = view.findViewById(R.id.text_best_player_position_badge);
        textBestPlayerPosition = view.findViewById(R.id.text_best_player_position);
        textBestPlayerInjuryStatus = view.findViewById(R.id.text_best_player_injury_status);
        textBestPlayerStats = view.findViewById(R.id.text_best_player_stats);
        
        // Position Counts
        buttonToggleView = view.findViewById(R.id.button_toggle_view);
        textViewMode = view.findViewById(R.id.text_view_mode);
        textPositionWR = view.findViewById(R.id.text_position_wr);
        textPositionRB = view.findViewById(R.id.text_position_rb);
        textPositionQB = view.findViewById(R.id.text_position_qb);
        textPositionTE = view.findViewById(R.id.text_position_te);
        textPositionDST = view.findViewById(R.id.text_position_dst);
        textPositionK = view.findViewById(R.id.text_position_k);
        textCountWR = view.findViewById(R.id.text_count_wr);
        textCountRB = view.findViewById(R.id.text_count_rb);
        textCountQB = view.findViewById(R.id.text_count_qb);
        textCountTE = view.findViewById(R.id.text_count_te);
        textCountDST = view.findViewById(R.id.text_count_dst);
        textCountK = view.findViewById(R.id.text_count_k);
        
        // Set position badge colors as circles
        GradientDrawable wrCircle = new GradientDrawable();
        wrCircle.setShape(GradientDrawable.OVAL);
        wrCircle.setColor(PositionColors.getColorForPosition("WR"));
        textPositionWR.setBackground(wrCircle);
        
        GradientDrawable rbCircle = new GradientDrawable();
        rbCircle.setShape(GradientDrawable.OVAL);
        rbCircle.setColor(PositionColors.getColorForPosition("RB"));
        textPositionRB.setBackground(rbCircle);
        
        GradientDrawable qbCircle = new GradientDrawable();
        qbCircle.setShape(GradientDrawable.OVAL);
        qbCircle.setColor(PositionColors.getColorForPosition("QB"));
        textPositionQB.setBackground(qbCircle);
        
        GradientDrawable teCircle = new GradientDrawable();
        teCircle.setShape(GradientDrawable.OVAL);
        teCircle.setColor(PositionColors.getColorForPosition("TE"));
        textPositionTE.setBackground(teCircle);
        
        GradientDrawable dstCircle = new GradientDrawable();
        dstCircle.setShape(GradientDrawable.OVAL);
        dstCircle.setColor(PositionColors.getColorForPosition("DST"));
        textPositionDST.setBackground(dstCircle);
        
        GradientDrawable kCircle = new GradientDrawable();
        kCircle.setShape(GradientDrawable.OVAL);
        kCircle.setColor(PositionColors.getColorForPosition("K"));
        textPositionK.setBackground(kCircle);
        
        // Set up toggle button click listener
        buttonToggleView.setOnClickListener(v -> {
            showLeagueCounts = !showLeagueCounts;
            if (showLeagueCounts) {
                buttonToggleView.setImageResource(R.drawable.ic_group_league);
                textViewMode.setText("League");
            } else {
                buttonToggleView.setImageResource(R.drawable.ic_person_team);
                textViewMode.setText("Team");
            }
            updatePositionCounts();
        });
        
        // Recent Picks Section
        pickSlot1 = view.findViewById(R.id.pick_slot_1);
        pickSlot2 = view.findViewById(R.id.pick_slot_2);
        pickSlot3 = view.findViewById(R.id.pick_slot_3);
        textPick1Number = view.findViewById(R.id.text_pick_1_number);
        textPick1Player = view.findViewById(R.id.text_pick_1_player);
        textPick1InjuryStatus = view.findViewById(R.id.text_pick_1_injury_status);
        textPick1Details = view.findViewById(R.id.text_pick_1_details);
        textPick1Rank = view.findViewById(R.id.text_pick_1_rank);
        textPick1Adp = view.findViewById(R.id.text_pick_1_adp);
        textPick1PositionRank = view.findViewById(R.id.text_pick_1_position_rank);
        textPick1Stats = view.findViewById(R.id.text_pick_1_stats);
        textPick2Number = view.findViewById(R.id.text_pick_2_number);
        textPick2Player = view.findViewById(R.id.text_pick_2_player);
        textPick2InjuryStatus = view.findViewById(R.id.text_pick_2_injury_status);
        textPick2Details = view.findViewById(R.id.text_pick_2_details);
        textPick2Rank = view.findViewById(R.id.text_pick_2_rank);
        textPick2Adp = view.findViewById(R.id.text_pick_2_adp);
        textPick2PositionRank = view.findViewById(R.id.text_pick_2_position_rank);
        textPick2Stats = view.findViewById(R.id.text_pick_2_stats);
        textPick3Number = view.findViewById(R.id.text_pick_3_number);
        textPick3Player = view.findViewById(R.id.text_pick_3_player);
        textPick3InjuryStatus = view.findViewById(R.id.text_pick_3_injury_status);
        textPick3Details = view.findViewById(R.id.text_pick_3_details);
        textPick3Rank = view.findViewById(R.id.text_pick_3_rank);
        textPick3Adp = view.findViewById(R.id.text_pick_3_adp);
        textPick3PositionRank = view.findViewById(R.id.text_pick_3_position_rank);
        textPick3Stats = view.findViewById(R.id.text_pick_3_stats);
        
        // Action Buttons
        buttonViewHistory = view.findViewById(R.id.button_view_history);
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
                    textCurrentTeam.setText("On the clock: " + currentTeam.getName());
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
            
            // Display team and position rank instead of position code and overall rank
            String positionInfo = "";
            if (bestPlayer.getNflTeam() != null && !bestPlayer.getNflTeam().isEmpty()) {
                positionInfo = bestPlayer.getNflTeam();
            }
            if (bestPlayer.getPositionRank() > 0) {
                if (!positionInfo.isEmpty()) {
                    positionInfo += " - ";
                }
                positionInfo += bestPlayer.getPosition() + bestPlayer.getPositionRank();
            }
            // Add bye week to position info
            if (bestPlayer.getByeWeek() > 0) {
                if (!positionInfo.isEmpty()) {
                    positionInfo += " ";
                }
                positionInfo += "(bye-" + bestPlayer.getByeWeek() + ")";
            }
            textBestPlayerPosition.setText(positionInfo);
            
            // Set position badge with color
            textBestPlayerPositionBadge.setText(bestPlayer.getPosition());
            GradientDrawable badgeCircle = new GradientDrawable();
            badgeCircle.setShape(GradientDrawable.OVAL);
            badgeCircle.setColor(PositionColors.getColorForPosition(bestPlayer.getPosition()));
            textBestPlayerPositionBadge.setBackground(badgeCircle);
            
            // Display injury status with color coding
            if (bestPlayer.getInjuryStatus() != null && !bestPlayer.getInjuryStatus().isEmpty() && 
                !bestPlayer.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
                textBestPlayerInjuryStatus.setText(bestPlayer.getInjuryStatus());
                textBestPlayerInjuryStatus.setVisibility(View.VISIBLE);
                
                // Color code based on severity
                String status = bestPlayer.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    textBestPlayerInjuryStatus.setTextColor(0xFFD32F2F); // Red
                } else if (status.equals("DOUBTFUL")) {
                    textBestPlayerInjuryStatus.setTextColor(0xFFFF6F00); // Dark Orange
                } else if (status.equals("QUESTIONABLE")) {
                    textBestPlayerInjuryStatus.setTextColor(0xFFFFA000); // Orange/Yellow
                } else {
                    textBestPlayerInjuryStatus.setTextColor(0xFF757575); // Gray
                }
            } else {
                textBestPlayerInjuryStatus.setVisibility(View.GONE);
            }
            
            // Display stats if available
            if (bestPlayer.getLastYearStats() != null && !bestPlayer.getLastYearStats().isEmpty()) {
                textBestPlayerStats.setText(bestPlayer.getLastYearStats());
                textBestPlayerStats.setVisibility(View.VISIBLE);
            } else {
                textBestPlayerStats.setVisibility(View.GONE);
            }
            
            // Set player name to dark text color (for light background)
            textBestPlayerName.setTextColor(getResources().getColor(R.color.text_on_light_bg, null));
            
            // Set position text to FFL position color
            int positionColor = PositionColors.getDarkColorForPosition(bestPlayer.getPosition());
            textBestPlayerPosition.setTextColor(positionColor);
            
            // Set button background to position color
            int buttonColor = PositionColors.getColorForPosition(bestPlayer.getPosition());
            buttonDraftBestPlayer.setBackgroundColor(buttonColor);
            buttonDraftBestPlayer.setTextColor(0xFFFFFFFF); // White text
            
            // Enable draft button
            buttonDraftBestPlayer.setEnabled(true);
        } else {
            textBestPlayerName.setText("No players available");
            textBestPlayerPosition.setText("--");
            textBestPlayerInjuryStatus.setVisibility(View.GONE);
            textBestPlayerStats.setVisibility(View.GONE);
            
            // Reset position badge to default gray
            textBestPlayerPositionBadge.setText("--");
            GradientDrawable badgeCircle = new GradientDrawable();
            badgeCircle.setShape(GradientDrawable.OVAL);
            badgeCircle.setColor(0xFFCCCCCC);
            textBestPlayerPositionBadge.setBackground(badgeCircle);
            
            // Reset to default colors
            textBestPlayerName.setTextColor(getResources().getColor(R.color.text_on_light_bg, null));
            textBestPlayerPosition.setTextColor(getResources().getColor(R.color.text_secondary, null));
            
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
            clearPickSlot(textPick1Number, textPick1Player, textPick1InjuryStatus, textPick1Details, 
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats);
            clearPickSlot(textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats);
            clearPickSlot(textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats);
            
            // Set first slot message
            textPick1Player.setText("No picks yet");
            return;
        }
        
        // Get the 3 most recent picks (in reverse order - most recent first)
        int totalPicks = pickHistory.size();
        
        // Pick 1 (most recent)
        if (totalPicks >= 1) {
            Pick pick1 = pickHistory.get(totalPicks - 1);
            updatePickSlot(pick1, textPick1Number, textPick1Player, textPick1InjuryStatus, textPick1Details,
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats);
        } else {
            clearPickSlot(textPick1Number, textPick1Player, textPick1InjuryStatus, textPick1Details,
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats);
        }
        
        // Pick 2 (second most recent)
        if (totalPicks >= 2) {
            Pick pick2 = pickHistory.get(totalPicks - 2);
            updatePickSlot(pick2, textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats);
        } else {
            clearPickSlot(textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats);
        }
        
        // Pick 3 (third most recent)
        if (totalPicks >= 3) {
            Pick pick3 = pickHistory.get(totalPicks - 3);
            updatePickSlot(pick3, textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats);
        } else {
            clearPickSlot(textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats);
        }
    }
    
    /**
     * Update a single pick slot with pick information.
     */
    private void updatePickSlot(Pick pick, TextView numberView, TextView playerView, TextView injuryStatusView, TextView detailsView,
            TextView rankView, TextView adpView, TextView positionRankView, TextView statsView) {
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
            
            // Display rank
            rankView.setText(String.valueOf(player.getRank()));
            
            // Display ADP if available
            if (player.getPffRank() > 0) {
                adpView.setText("ADP:" + player.getPffRank());
                adpView.setVisibility(View.VISIBLE);
            } else {
                adpView.setVisibility(View.GONE);
            }
            
            // Display position rank if available
            if (player.getPositionRank() > 0) {
                positionRankView.setText(player.getPosition() + player.getPositionRank());
                positionRankView.setVisibility(View.VISIBLE);
            } else {
                positionRankView.setVisibility(View.GONE);
            }
            
            // Display stats with bye week if available
            String statsText = "";
            if (player.getLastYearStats() != null && !player.getLastYearStats().isEmpty()) {
                statsText = "Last Year: " + player.getLastYearStats();
            }
            if (player.getByeWeek() > 0) {
                if (!statsText.isEmpty()) {
                    statsText += " ";
                }
                statsText += "*(bye-" + player.getByeWeek() + ")";
            }
            if (!statsText.isEmpty()) {
                statsView.setText(statsText);
                statsView.setVisibility(View.VISIBLE);
            } else {
                statsView.setVisibility(View.GONE);
            }
            
            // Display injury status with color coding
            if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty() && 
                !player.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
                injuryStatusView.setText(player.getInjuryStatus());
                injuryStatusView.setVisibility(View.VISIBLE);
                
                // Color code based on severity
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    injuryStatusView.setTextColor(0xFFD32F2F); // Red
                } else if (status.equals("DOUBTFUL")) {
                    injuryStatusView.setTextColor(0xFFFF6F00); // Dark Orange
                } else if (status.equals("QUESTIONABLE")) {
                    injuryStatusView.setTextColor(0xFFFFA000); // Orange/Yellow
                } else {
                    injuryStatusView.setTextColor(0xFF757575); // Gray
                }
            } else {
                injuryStatusView.setVisibility(View.GONE);
            }
            
            // Apply position-based color to the pick number circle
            int backgroundColor = PositionColors.getColorForPosition(player.getPosition());
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(backgroundColor);
            numberView.setBackground(circle);
            
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
            rankView.setText("-");
            adpView.setVisibility(View.GONE);
            positionRankView.setVisibility(View.GONE);
            statsView.setVisibility(View.GONE);
            injuryStatusView.setVisibility(View.GONE);
            detailsView.setText("");
        }
    }
    
    /**
     * Clear a pick slot (show placeholder).
     */
    private void clearPickSlot(TextView numberView, TextView playerView, TextView injuryStatusView, TextView detailsView,
            TextView rankView, TextView adpView, TextView positionRankView, TextView statsView) {
        numberView.setText("--");
        playerView.setText("--");
        rankView.setText("-");
        adpView.setVisibility(View.GONE);
        positionRankView.setVisibility(View.GONE);
        statsView.setVisibility(View.GONE);
        injuryStatusView.setVisibility(View.GONE);
        detailsView.setText("");
        
        // Reset circle to default gray color
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFFCCCCCC); // Gray
        numberView.setBackground(circle);
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
        
        // View History link is always enabled (TextView, no need to set enabled/alpha)
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
        android.widget.Spinner spinnerPosition = 
                dialogView.findViewById(R.id.spinner_player_position);
        android.widget.Spinner spinnerTeam = 
                dialogView.findViewById(R.id.spinner_player_team);
        
        // Setup position spinner
        String[] positions = {"QB", "RB", "WR", "TE", "K", "DST"};
        android.widget.ArrayAdapter<String> positionAdapter = new android.widget.ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, positions);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(positionAdapter);
        
        // Setup NFL team spinner
        String[] nflTeams = {"ARI", "ATL", "BAL", "BUF", "CAR", "CHI", "CIN", "CLE", "DAL", "DEN", 
                            "DET", "GB", "HOU", "IND", "JAX", "KC", "LV", "LAC", "LAR", "MIA", 
                            "MIN", "NE", "NO", "NYG", "NYJ", "PHI", "PIT", "SF", "SEA", "TB", 
                            "TEN", "WAS"};
        android.widget.ArrayAdapter<String> teamAdapter = new android.widget.ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, nflTeams);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeam.setAdapter(teamAdapter);
        
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.button_draft).setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String position = (String) spinnerPosition.getSelectedItem();
            String team = (String) spinnerTeam.getSelectedItem();
            
            // Validate inputs
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Player name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (position == null || position.isEmpty()) {
                Toast.makeText(getContext(), "Position is required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (team == null || team.isEmpty()) {
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
                .setMessage("The draft is complete! You can export the results from the Draft History screen.")
                .setPositiveButton("OK", null)
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
}
