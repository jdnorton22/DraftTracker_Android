package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.DraftCoordinator;
import com.fantasydraft.picker.managers.DraftManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.managers.TeamManager;
import com.fantasydraft.picker.models.DraftAnalytics;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.utils.DraftCsvExporter;
import com.fantasydraft.picker.utils.PickValueCalculator;
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
    
    // UI Components - Draft Info Collapse/Expand
    private LinearLayout layoutDraftInfoHeader;
    private LinearLayout layoutDraftInfoDetails;
    private TextView iconCollapseExpand;
    private boolean isDraftInfoExpanded = true;
    
    // UI Components - Best Available Player Section
    private androidx.cardview.widget.CardView cardBestAvailable;
    private Button buttonDraftBestPlayer;
    private TextView textBestPlayerName;
    private TextView textBestPlayerPositionBadge;
    private TextView textBestPlayerPosition;
    private TextView textBestPlayerInjuryStatus;
    private TextView textBestPlayerStats;
    private LinearLayout layoutBestAvailableFilters;
    private String selectedPositionFilter = "Overall";
    private static final String[] POSITION_FILTER_OPTIONS = {"Overall", "Fav", "QB", "RB", "WR", "TE", "K", "DST"};
    
    // UI Components - Position Counts
    private TextView buttonTeamView;
    private TextView buttonLeagueView;
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
    private TextView textPick1ValueIcon;
    private TextView textPick1ValueScore;
    private TextView textPick2Number;
    private TextView textPick2Player;
    private TextView textPick2InjuryStatus;
    private TextView textPick2Details;
    private TextView textPick2Rank;
    private TextView textPick2Adp;
    private TextView textPick2PositionRank;
    private TextView textPick2Stats;
    private TextView textPick2ValueIcon;
    private TextView textPick2ValueScore;
    private TextView textPick3Number;
    private TextView textPick3Player;
    private TextView textPick3InjuryStatus;
    private TextView textPick3Details;
    private TextView textPick3Rank;
    private TextView textPick3Adp;
    private TextView textPick3PositionRank;
    private TextView textPick3Stats;
    private TextView textPick3ValueIcon;
    private TextView textPick3ValueScore;
    
    // UI Components - Action Buttons
    private TextView buttonViewHistory;
    private TextView buttonViewAnalytics;
    private TextView buttonUndoLastPick;
    private Button buttonResetDraft;
    
    // UI Components - Team Roster Button (Req 1.1, 1.2)
    private ImageButton buttonViewRoster;
    
    // UI Components - Stopwatch
    private TextView textStopwatch;
    private android.os.Handler stopwatchHandler;
    private long stopwatchStartTime;
    private boolean stopwatchRunning;
    private static final String PREF_STOPWATCH_START = "stopwatch_start_time";
    
    // Track last pick count for animation
    private int lastPickCount = -1;
    
    // Track last best available player for animation
    private String lastBestPlayerId = null;
    
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
            getView().postDelayed(() -> {
                updateUI();
                showWalkthroughIfNeeded();
            }, 100);
        } else {
            updateUI();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Stop local UI updates but keep the notification service running
        stopwatchRunning = false;
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
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
        cardBestAvailable = view.findViewById(R.id.card_best_available);
        buttonDraftBestPlayer = view.findViewById(R.id.button_draft_best_player);
        buttonDraftBestPlayer.setTextColor(0xFFFFFFFF); // Force white text color
        textBestPlayerName = view.findViewById(R.id.text_best_player_name);
        textBestPlayerPositionBadge = view.findViewById(R.id.text_best_player_position_badge);
        textBestPlayerPosition = view.findViewById(R.id.text_best_player_position);
        textBestPlayerInjuryStatus = view.findViewById(R.id.text_best_player_injury_status);
        textBestPlayerStats = view.findViewById(R.id.text_best_player_stats);
        
        // Position Filter Buttons
        layoutBestAvailableFilters = view.findViewById(R.id.layout_best_available_filters);
        setupPositionFilterButtons();
        
        // Position Counts
        buttonTeamView = view.findViewById(R.id.button_team_view);
        buttonLeagueView = view.findViewById(R.id.button_league_view);
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
        
        // Set up toggle button click listeners
        buttonTeamView.setOnClickListener(v -> {
            if (showLeagueCounts) {
                showLeagueCounts = false;
                updateToggleAppearance();
                updatePositionCounts();
            }
        });
        
        buttonLeagueView.setOnClickListener(v -> {
            if (!showLeagueCounts) {
                showLeagueCounts = true;
                updateToggleAppearance();
                updatePositionCounts();
            }
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
        textPick1ValueIcon = view.findViewById(R.id.text_pick_1_value_icon);
        textPick1ValueScore = view.findViewById(R.id.text_pick_1_value_score);
        textPick2Number = view.findViewById(R.id.text_pick_2_number);
        textPick2Player = view.findViewById(R.id.text_pick_2_player);
        textPick2InjuryStatus = view.findViewById(R.id.text_pick_2_injury_status);
        textPick2Details = view.findViewById(R.id.text_pick_2_details);
        textPick2Rank = view.findViewById(R.id.text_pick_2_rank);
        textPick2Adp = view.findViewById(R.id.text_pick_2_adp);
        textPick2PositionRank = view.findViewById(R.id.text_pick_2_position_rank);
        textPick2Stats = view.findViewById(R.id.text_pick_2_stats);
        textPick2ValueIcon = view.findViewById(R.id.text_pick_2_value_icon);
        textPick2ValueScore = view.findViewById(R.id.text_pick_2_value_score);
        textPick3Number = view.findViewById(R.id.text_pick_3_number);
        textPick3Player = view.findViewById(R.id.text_pick_3_player);
        textPick3InjuryStatus = view.findViewById(R.id.text_pick_3_injury_status);
        textPick3Details = view.findViewById(R.id.text_pick_3_details);
        textPick3Rank = view.findViewById(R.id.text_pick_3_rank);
        textPick3Adp = view.findViewById(R.id.text_pick_3_adp);
        textPick3PositionRank = view.findViewById(R.id.text_pick_3_position_rank);
        textPick3Stats = view.findViewById(R.id.text_pick_3_stats);
        textPick3ValueIcon = view.findViewById(R.id.text_pick_3_value_icon);
        textPick3ValueScore = view.findViewById(R.id.text_pick_3_value_score);
        
        // Action Buttons
        buttonViewHistory = view.findViewById(R.id.button_view_history);
        buttonViewAnalytics = view.findViewById(R.id.button_view_analytics);
        buttonUndoLastPick = view.findViewById(R.id.button_undo_last_pick);
        buttonResetDraft = view.findViewById(R.id.button_reset_draft);
        
        // Team Roster Button (Req 1.1, 1.2)
        buttonViewRoster = view.findViewById(R.id.button_view_roster);
        buttonViewRoster.setOnClickListener(v -> showTeamRosterDialog());
        
        // Stopwatch
        textStopwatch = view.findViewById(R.id.text_stopwatch);
        stopwatchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        
        // Draft Info Collapse/Expand
        layoutDraftInfoHeader = view.findViewById(R.id.layout_draft_info_header);
        layoutDraftInfoDetails = view.findViewById(R.id.layout_draft_info_details);
        iconCollapseExpand = view.findViewById(R.id.icon_collapse_expand);
        
        // Set up collapse/expand click handler
        layoutDraftInfoHeader.setOnClickListener(v -> toggleDraftInfoCollapse());
        
        // Set up button click handlers
        buttonViewHistory.setOnClickListener(v -> launchDraftHistoryActivity());
        buttonViewAnalytics.setOnClickListener(v -> showDraftCompletionDialog());
        buttonUndoLastPick.setOnClickListener(v -> showUndoLastPickConfirmation());
        buttonResetDraft.setOnClickListener(v -> showResetConfirmationDialog());
    }
    
    /**
     * Toggle the collapse/expand state of the draft info section.
     */
    private void toggleDraftInfoCollapse() {
        isDraftInfoExpanded = !isDraftInfoExpanded;
        
        if (isDraftInfoExpanded) {
            // Expand
            layoutDraftInfoDetails.setVisibility(View.VISIBLE);
            iconCollapseExpand.setText("▼");
        } else {
            // Collapse
            layoutDraftInfoDetails.setVisibility(View.GONE);
            iconCollapseExpand.setText("▶");
        }
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
    
    // Walkthrough
    private DraftWalkthrough walkthrough;
    
    /**
     * Show the walkthrough overlay on first launch.
     */
    private void showWalkthroughIfNeeded() {
        if (getView() == null) return;
        walkthrough = new DraftWalkthrough(getView());
        if (walkthrough.shouldShow()) {
            walkthrough.start();
        }
    }
    
    private final Runnable stopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            if (stopwatchRunning && textStopwatch != null) {
                long elapsed = System.currentTimeMillis() - stopwatchStartTime;
                int totalSeconds = (int) (elapsed / 1000);
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                textStopwatch.setText(String.format("%d:%02d", minutes, seconds));
                
                // Color changes: orange < 1min, red >= 2min
                if (totalSeconds >= 120) {
                    textStopwatch.setTextColor(0xFFD32F2F); // Red
                } else if (totalSeconds >= 60) {
                    textStopwatch.setTextColor(0xFFFF8C00); // Orange
                } else {
                    textStopwatch.setTextColor(0xFF4CAF50); // Green
                }
                
                stopwatchHandler.postDelayed(this, 1000);
            }
        }
    };
    
    /**
     * Start or restart the stopwatch for the current pick.
     */
    private void startStopwatch() {
        stopwatchStartTime = System.currentTimeMillis();
        stopwatchRunning = true;
        textStopwatch.setText("0:00");
        textStopwatch.setTextColor(0xFF4CAF50); // Green
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        stopwatchHandler.post(stopwatchRunnable);
        
        // Persist start time so it survives app switching
        if (getContext() != null) {
            getContext().getSharedPreferences("FantasyDraftPrefs", 0)
                    .edit().putLong(PREF_STOPWATCH_START, stopwatchStartTime).apply();
        }
    }
    
    /**
     * Resume the stopwatch from a persisted start time.
     */
    private void resumeStopwatch() {
        if (getContext() == null) return;
        long saved = getContext().getSharedPreferences("FantasyDraftPrefs", 0)
                .getLong(PREF_STOPWATCH_START, 0);
        if (saved > 0) {
            stopwatchStartTime = saved;
        } else {
            stopwatchStartTime = System.currentTimeMillis();
        }
        stopwatchRunning = true;
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        stopwatchHandler.post(stopwatchRunnable);
    }
    
    /**
     * Stop the stopwatch.
     */
    private void stopStopwatch() {
        stopwatchRunning = false;
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
    }
    
    /**
     * Update stopwatch visibility based on config setting.
     */
    private void updateStopwatchVisibility() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) return;
        
        DraftConfig config = mainActivity.getCurrentConfig();
        DraftState state = mainActivity.getCurrentState();
        boolean enabled = config != null && config.isStopwatchEnabled();
        boolean draftActive = state != null && !state.isComplete();
        
        if (enabled && draftActive) {
            textStopwatch.setVisibility(View.VISIBLE);
            if (!stopwatchRunning) {
                resumeStopwatch();
            }
        } else {
            textStopwatch.setVisibility(View.GONE);
            stopStopwatch();
        }
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
        updateStopwatchVisibility();
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
                    textCurrentTeam.setVisibility(View.VISIBLE);
                }
            }
            
            // Show/hide roster button based on draft active state (Req 1.4)
            boolean isDraftActive = !currentState.isComplete();
            buttonViewRoster.setVisibility(isDraftActive ? View.VISIBLE : View.GONE);
            textCurrentTeam.setVisibility(isDraftActive ? View.VISIBLE : View.GONE);
        } else {
            // Hide roster button and team when state is not available
            buttonViewRoster.setVisibility(View.GONE);
            textCurrentTeam.setVisibility(View.GONE);
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
        Player bestPlayer;
        if ("Overall".equals(selectedPositionFilter)) {
            bestPlayer = playerManager.getBestAvailable(playerManager.getPlayers());
        } else if ("Fav".equals(selectedPositionFilter)) {
            bestPlayer = playerManager.getBestAvailableFavorite(playerManager.getPlayers());
        } else {
            bestPlayer = playerManager.getBestAvailableByPosition(playerManager.getPlayers(), selectedPositionFilter);
        }
        
        if (bestPlayer != null) {
            textBestPlayerName.setText(bestPlayer.getName());

            // Hyperlink player name to ESPN page in blue
            String espnUrl = bestPlayer.getEspnUrl();
            if (espnUrl != null) {
                textBestPlayerName.setTextColor(0xFF1565C0); // Blue
                textBestPlayerName.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(espnUrl));
                    startActivity(browserIntent);
                });
            } else {
                textBestPlayerName.setTextColor(0xFF1565C0); // Blue even without link
                textBestPlayerName.setOnClickListener(null);
            }
            
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
            
            
            // Set position text to FFL position color
            int positionColor = PositionColors.getDarkColorForPosition(bestPlayer.getPosition());
            textBestPlayerPosition.setTextColor(positionColor);
            
            // Set button background to position color
            int buttonColor = PositionColors.getColorForPosition(bestPlayer.getPosition());
            buttonDraftBestPlayer.setBackgroundColor(buttonColor);
            buttonDraftBestPlayer.setTextColor(0xFFFFFFFF); // White text
            
            // Enable draft button
            buttonDraftBestPlayer.setEnabled(true);
            
            // Apply favorite highlight or position gradient to best available card
            if (bestPlayer.isFavorite()) {
                // Solid favorite color for the whole card
                cardBestAvailable.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.favorite_highlight));
            } else {
                // Subtle gradient from position color tint to white/dark based on theme
                int posColor = PositionColors.getColorForPosition(bestPlayer.getPosition());
                
                // Check if dark mode is enabled
                boolean isDarkMode = (getResources().getConfiguration().uiMode & 
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                        android.content.res.Configuration.UI_MODE_NIGHT_YES;
                
                int lightTint, endColor;
                if (isDarkMode) {
                    // Dark mode: blend with dark gray instead of white
                    lightTint = blendColor(posColor, 0xFF2C2C2C, 0.70f);
                    endColor = 0xFF1E1E1E;
                } else {
                    // Light mode: blend with white
                    lightTint = blendColor(posColor, 0xFFFFFFFF, 0.85f);
                    endColor = 0xFFFFFFFF;
                }
                
                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{lightTint, endColor});
                gradient.setCornerRadius(8);
                cardBestAvailable.setBackground(gradient);
            }
            
            // Animate swipe-up when best available player changes
            String currentId = bestPlayer.getId();
            if (lastBestPlayerId != null && !lastBestPlayerId.equals(currentId)) {
                cardBestAvailable.setTranslationY(cardBestAvailable.getHeight() > 0 ? cardBestAvailable.getHeight() : 120);
                cardBestAvailable.setAlpha(0f);
                cardBestAvailable.animate()
                        .translationY(0)
                        .alpha(1f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
            }
            lastBestPlayerId = currentId;
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
            android.util.TypedValue tv2 = new android.util.TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, tv2, true);
            textBestPlayerName.setTextColor(getResources().getColor(tv2.resourceId, null));
            textBestPlayerName.setOnClickListener(null);
            textBestPlayerPosition.setTextColor(getResources().getColor(R.color.text_secondary, null));
            
            // Reset button to default background
            buttonDraftBestPlayer.setBackgroundColor(0xFFCCCCCC);
            
            // Disable draft button
            buttonDraftBestPlayer.setEnabled(false);
            
            // Reset favorite highlight on best available card
            View bestPlayerInfoView = (View) textBestPlayerName.getParent().getParent();
            bestPlayerInfoView.setBackgroundColor(0x00000000); // Transparent default
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
        
        // Update accessibility content descriptions
        updatePositionCountAccessibility(wrCount, rbCount, qbCount, teCount, dstCount, kCount);
    }
    
    /**
     * Update accessibility content descriptions for position counts
     */
    private void updatePositionCountAccessibility(int wrCount, int rbCount, int qbCount, 
                                                   int teCount, int dstCount, int kCount) {
        String scope = showLeagueCounts ? "league-wide" : "on your team";
        
        if (textPositionWR != null) {
            textPositionWR.setContentDescription("Wide Receiver. " + wrCount + " drafted " + scope);
        }
        if (textPositionRB != null) {
            textPositionRB.setContentDescription("Running Back. " + rbCount + " drafted " + scope);
        }
        if (textPositionQB != null) {
            textPositionQB.setContentDescription("Quarterback. " + qbCount + " drafted " + scope);
        }
        if (textPositionTE != null) {
            textPositionTE.setContentDescription("Tight End. " + teCount + " drafted " + scope);
        }
        if (textPositionDST != null) {
            textPositionDST.setContentDescription("Defense. " + dstCount + " drafted " + scope);
        }
        if (textPositionK != null) {
            textPositionK.setContentDescription("Kicker. " + kCount + " drafted " + scope);
        }
    }
    
    /*
     * Draft board methods removed - not enough screen space
     * Keeping analytics dialog for post-draft grading
     */
    
    /**
     * Update the toggle slider appearance based on current mode.
     */
    private void updateToggleAppearance() {
        if (showLeagueCounts) {
            // League mode selected
            buttonLeagueView.setBackgroundResource(R.drawable.toggle_slider_selected);
            buttonLeagueView.setTextColor(0xFFFFFFFF); // White
            buttonTeamView.setBackgroundResource(android.R.color.transparent);
            if (getContext() != null) {
                buttonTeamView.setTextColor(getContext().getResources().getColor(R.color.text_secondary, null));
            }
        } else {
            // Team mode selected
            buttonTeamView.setBackgroundResource(R.drawable.toggle_slider_selected);
            buttonTeamView.setTextColor(0xFFFFFFFF); // White
            buttonLeagueView.setBackgroundResource(android.R.color.transparent);
            if (getContext() != null) {
                buttonLeagueView.setTextColor(getContext().getResources().getColor(R.color.text_secondary, null));
            }
        }
    }
    
    /**
     * Set up the horizontal position filter buttons for Best Available.
     */
    private void setupPositionFilterButtons() {
        layoutBestAvailableFilters.removeAllViews();
        for (String option : POSITION_FILTER_OPTIONS) {
            TextView btn = new TextView(getContext());
            btn.setText(option);
            btn.setTextSize(12);
            btn.setPadding(28, 12, 28, 12);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(6);
            btn.setLayoutParams(params);

            updateFilterButtonAppearance(btn, option.equals(selectedPositionFilter));

            btn.setOnClickListener(v -> {
                selectedPositionFilter = option;
                updatePositionFilterButtons();
                updateBestAvailable();
            });

            layoutBestAvailableFilters.addView(btn);
        }
    }

    /**
     * Update all position filter button appearances to reflect current selection.
     */
    private void updatePositionFilterButtons() {
        for (int i = 0; i < layoutBestAvailableFilters.getChildCount(); i++) {
            TextView btn = (TextView) layoutBestAvailableFilters.getChildAt(i);
            updateFilterButtonAppearance(btn, POSITION_FILTER_OPTIONS[i].equals(selectedPositionFilter));
        }
    }

    /**
     * Update a single filter button's appearance based on selected state.
     */
    private void updateFilterButtonAppearance(TextView btn, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(48);
        if (selected) {
            bg.setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            btn.setTextColor(0xFFFFFFFF);
        } else {
            bg.setColor(0xFFE0E0E0);
            btn.setTextColor(0xFF333333);
        }
        btn.setBackground(bg);
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
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats,
                    textPick1ValueIcon, textPick1ValueScore);
            clearPickSlot(textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats,
                    textPick2ValueIcon, textPick2ValueScore);
            clearPickSlot(textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats,
                    textPick3ValueIcon, textPick3ValueScore);
            
            // Set first slot message
            textPick1Player.setText("No picks yet");
            
            // Hide undo button when no picks
            buttonUndoLastPick.setVisibility(View.GONE);
            lastPickCount = 0;
            return;
        }
        
        // Show undo button when there are picks
        buttonUndoLastPick.setVisibility(View.VISIBLE);
        
        // Detect if a new pick was just made
        int totalPicks = pickHistory.size();
        boolean isNewPick = lastPickCount >= 0 && totalPicks > lastPickCount;
        lastPickCount = totalPicks;
        
        // Pick 1 (most recent)
        if (totalPicks >= 1) {
            Pick pick1 = pickHistory.get(totalPicks - 1);
            updatePickSlot(pick1, textPick1Number, textPick1Player, textPick1InjuryStatus, textPick1Details,
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats,
                    textPick1ValueIcon, textPick1ValueScore, pickSlot1);
        } else {
            clearPickSlot(textPick1Number, textPick1Player, textPick1InjuryStatus, textPick1Details,
                    textPick1Rank, textPick1Adp, textPick1PositionRank, textPick1Stats,
                    textPick1ValueIcon, textPick1ValueScore);
        }
        
        // Pick 2 (second most recent)
        if (totalPicks >= 2) {
            Pick pick2 = pickHistory.get(totalPicks - 2);
            updatePickSlot(pick2, textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats,
                    textPick2ValueIcon, textPick2ValueScore, pickSlot2);
        } else {
            clearPickSlot(textPick2Number, textPick2Player, textPick2InjuryStatus, textPick2Details,
                    textPick2Rank, textPick2Adp, textPick2PositionRank, textPick2Stats,
                    textPick2ValueIcon, textPick2ValueScore);
        }
        
        // Pick 3 (third most recent)
        if (totalPicks >= 3) {
            Pick pick3 = pickHistory.get(totalPicks - 3);
            updatePickSlot(pick3, textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats,
                    textPick3ValueIcon, textPick3ValueScore, pickSlot3);
        } else {
            clearPickSlot(textPick3Number, textPick3Player, textPick3InjuryStatus, textPick3Details,
                    textPick3Rank, textPick3Adp, textPick3PositionRank, textPick3Stats,
                    textPick3ValueIcon, textPick3ValueScore);
        }
        
        // Animate slots when a new pick arrives
        if (isNewPick) {
            animateNewPick();
        }
    }
    
    /**
     * Animate the recent pick slots when a new pick is made.
     * Slot 1 slides in from the left, slots 2 and 3 fade in from their new positions.
     */
    private void animateNewPick() {
        // Slot 1 (newest): slide in from left with fade
        pickSlot1.setTranslationX(-pickSlot1.getWidth());
        pickSlot1.setAlpha(0f);
        pickSlot1.animate()
                .translationX(0)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
        
        // Slot 2: subtle fade to show content shift
        pickSlot2.setAlpha(0.3f);
        pickSlot2.animate()
                .alpha(1f)
                .setDuration(250)
                .setStartDelay(100)
                .start();
        
        // Slot 3: subtle fade
        pickSlot3.setAlpha(0.3f);
        pickSlot3.animate()
                .alpha(1f)
                .setDuration(250)
                .setStartDelay(150)
                .start();
    }
    
    /**
     * Blend two colors together. ratio=0 returns color1, ratio=1 returns color2.
     */
    private static int blendColor(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;
        float inv = 1f - ratio;
        return ((int)(a1 * inv + a2 * ratio) << 24) | ((int)(r1 * inv + r2 * ratio) << 16)
                | ((int)(g1 * inv + g2 * ratio) << 8) | (int)(b1 * inv + b2 * ratio);
    }
    
    /**
     * Update a single pick slot with pick information.
     */
    private void updatePickSlot(Pick pick, TextView numberView, TextView playerView, TextView injuryStatusView, TextView detailsView,
            TextView rankView, TextView adpView, TextView positionRankView, TextView statsView,
            TextView valueIconView, TextView valueScoreView, LinearLayout slotContainer) {
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
            
            // Hyperlink player name to ESPN page in blue
            String espnUrl = player.getEspnUrl();
            if (espnUrl != null) {
                playerView.setTextColor(0xFF1565C0); // Blue
                playerView.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(espnUrl));
                    startActivity(browserIntent);
                });
            } else {
                playerView.setTextColor(0xFF1565C0); // Blue even without link
                playerView.setOnClickListener(null);
            }
            
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
            
            // Calculate and display value score
            int valueScore = PickValueCalculator.calculateValueScore(pick, player);
            android.util.Log.d("DraftFragment", "Pick " + pick.getPickNumber() + " - Player: " + player.getName() + 
                ", ADP: " + player.getPffRank() + ", ValueScore: " + valueScore);
            if (player.getPffRank() > 0) {
                PickValueCalculator.ValueTier tier = PickValueCalculator.getValueTier(valueScore);
                String icon = PickValueCalculator.getValueIcon(tier);
                int color = PickValueCalculator.getValueColor(tier);
                String scoreText = PickValueCalculator.getValueString(valueScore);
                
                android.util.Log.d("DraftFragment", "Showing value indicator: " + icon + " " + scoreText);
                
                valueIconView.setText(icon);
                valueIconView.setTextColor(color);
                valueIconView.setVisibility(View.VISIBLE);
                
                valueScoreView.setText(scoreText);
                valueScoreView.setTextColor(color);
                valueScoreView.setVisibility(View.VISIBLE);
            } else {
                android.util.Log.d("DraftFragment", "Hiding value indicator - pffRank: " + player.getPffRank());
                valueIconView.setVisibility(View.GONE);
                valueScoreView.setVisibility(View.GONE);
            }
            
            // Apply favorite highlight to pick slot
            if (player.isFavorite()) {
                slotContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.favorite_highlight));
                // Use theme-aware text color on favorite highlight (dark in light mode, white in dark mode)
                int textOnFavorite = ContextCompat.getColor(requireContext(), R.color.text_on_favorite);
                playerView.setTextColor(0xFF1565C0); // Keep blue for URL
                detailsView.setTextColor(textOnFavorite);
                rankView.setTextColor(textOnFavorite);
                adpView.setTextColor(textOnFavorite);
                positionRankView.setTextColor(textOnFavorite);
                statsView.setTextColor(textOnFavorite);
            } else {
                slotContainer.setBackgroundColor(0x00000000); // Transparent default
                playerView.setTextColor(0xFF1565C0); // Blue for URL
                detailsView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                rankView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
                adpView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                positionRankView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                statsView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            }
        } else {
            playerView.setText("Unknown Player");
            playerView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            playerView.setOnClickListener(null);
            rankView.setText("-");
            adpView.setVisibility(View.GONE);
            positionRankView.setVisibility(View.GONE);
            statsView.setVisibility(View.GONE);
            injuryStatusView.setVisibility(View.GONE);
            detailsView.setText("");
            valueIconView.setVisibility(View.GONE);
            valueScoreView.setVisibility(View.GONE);
            slotContainer.setBackgroundColor(0x00000000); // Reset highlight for unknown player
        }
    }
    
    /**
     * Clear a pick slot (show placeholder).
     */
    private void clearPickSlot(TextView numberView, TextView playerView, TextView injuryStatusView, TextView detailsView,
            TextView rankView, TextView adpView, TextView positionRankView, TextView statsView,
            TextView valueIconView, TextView valueScoreView) {
        numberView.setText("--");
        playerView.setText("--");
        playerView.setOnClickListener(null);
        playerView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        rankView.setText("-");
        adpView.setVisibility(View.GONE);
        positionRankView.setVisibility(View.GONE);
        statsView.setVisibility(View.GONE);
        valueIconView.setVisibility(View.GONE);
        valueScoreView.setVisibility(View.GONE);
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
        
        // Show Analytics button when draft is complete
        buttonViewAnalytics.setVisibility(isDraftComplete ? View.VISIBLE : View.GONE);
        
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
     * Show the team roster dialog for viewing drafted players.
     * Gets teams from MainActivity, determines the current on-the-clock team,
     * and constructs/shows the TeamRosterDialog.
     * Requirements: 1.1, 1.2, 1.3
     */
    private void showTeamRosterDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        List<Team> teams = mainActivity.getTeams();
        DraftManager draftManager = mainActivity.getDraftManager();
        PlayerManager playerManager = mainActivity.getPlayerManager();
        DraftState currentState = mainActivity.getCurrentState();
        DraftConfig currentConfig = mainActivity.getCurrentConfig();
        
        if (teams == null || teams.isEmpty() || currentState == null || currentConfig == null) {
            return;
        }
        
        // Get current on-the-clock team ID (Req 1.3)
        int teamIndex = draftManager.getCurrentTeamIndex(
                currentState, currentConfig, teams.size());
        String currentTeamId = "";
        if (teamIndex >= 0 && teamIndex < teams.size()) {
            currentTeamId = teams.get(teamIndex).getId();
        }
        
        TeamRosterDialog dialog = new TeamRosterDialog(
                getActivity(), teams, currentTeamId, draftManager, playerManager, currentConfig);
        dialog.show();
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
            
            // Restart stopwatch for next pick
            if (textStopwatch.getVisibility() == View.VISIBLE) {
                startStopwatch();
            }
            
            // Check if draft is complete and show completion dialog
            if (newState.isComplete()) {
                showDraftCompletionDialog();
            }
            
            Toast.makeText(getContext(), 
                    selectedPlayer.getName() + " drafted by " + currentTeam.getName(), 
                    Toast.LENGTH_SHORT).show();
            
            // Send SMS update if enabled
            sendSmsDraftUpdate(selectedPlayer, currentTeam, currentState, overallPickNumber);
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error drafting player: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Send SMS draft update to configured recipients.
     */
    private void sendSmsDraftUpdate(Player player, Team team, DraftState state, int overallPick) {
        if (getContext() == null) return;
        
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("FantasyDraftPrefs", 0);
        boolean smsEnabled = prefs.getBoolean("sms_enabled", false);
        String numbersRaw = prefs.getString("sms_numbers", "");
        
        if (!smsEnabled || numbersRaw.isEmpty()) return;
        
        // Build the message
        StringBuilder msg = new StringBuilder();
        msg.append("🏈 DRAFT PICK #").append(overallPick).append("\n");
        msg.append("Drafted by: ").append(team.getName()).append("\n");
        msg.append("Round ").append(state.getCurrentRound())
           .append(", Pick ").append(state.getCurrentPickInRound()).append("\n\n");
        msg.append(player.getName()).append(" - ").append(player.getPosition());
        if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
            msg.append(", ").append(player.getNflTeam());
        }
        msg.append("\n");
        
        // Parse phone numbers (one per line, strip non-digits for the URI)
        String[] lines = numbersRaw.split("\\n");
        StringBuilder recipients = new StringBuilder();
        for (String line : lines) {
            String num = line.trim();
            if (!num.isEmpty()) {
                if (recipients.length() > 0) recipients.append(";");
                recipients.append(num);
            }
        }
        
        if (recipients.length() == 0) return;
        
        // Launch SMS intent
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(android.net.Uri.parse("smsto:" + recipients));
        smsIntent.putExtra("sms_body", msg.toString());
        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No SMS app found", Toast.LENGTH_SHORT).show();
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
        Player bestPlayer;
        if ("Overall".equals(selectedPositionFilter)) {
            bestPlayer = playerManager.getBestAvailable(playerManager.getPlayers());
        } else if ("Fav".equals(selectedPositionFilter)) {
            bestPlayer = playerManager.getBestAvailableFavorite(playerManager.getPlayers());
        } else {
            bestPlayer = playerManager.getBestAvailableByPosition(playerManager.getPlayers(), selectedPositionFilter);
        }
        
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
     * Show confirmation dialog before undoing the last pick.
     */
    private void showUndoLastPickConfirmation() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) return;
        
        List<Pick> pickHistory = mainActivity.getPickHistory();
        if (pickHistory == null || pickHistory.isEmpty()) {
            Toast.makeText(getContext(), "No picks to undo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Pick lastPick = pickHistory.get(pickHistory.size() - 1);
        PlayerManager playerManager = mainActivity.getPlayerManager();
        Player player = playerManager.getPlayerById(lastPick.getPlayerId());
        String playerName = player != null ? player.getName() : "Unknown Player";
        
        new AlertDialog.Builder(getActivity())
                .setTitle("Undo Pick")
                .setMessage("Undo pick #" + lastPick.getPickNumber() + " (" + playerName + ")?")
                .setPositiveButton("Undo", (dialog, which) -> undoLastPick())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Undo the most recent pick directly from the Recent Picks section.
     */
    private void undoLastPick() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) return;
        
        List<Pick> pickHistory = mainActivity.getPickHistory();
        if (pickHistory == null || pickHistory.isEmpty()) return;
        
        Pick lastPick = pickHistory.get(pickHistory.size() - 1);
        mainActivity.undoPick(lastPick);
        updateUI();
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
     * Show dialog when draft is complete with analytics.
     * Requirements: 2.8
     */
    private void showDraftCompletionDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) return;
        
        // Get current team
        DraftState currentState = mainActivity.getCurrentState();
        DraftConfig currentConfig = mainActivity.getCurrentConfig();
        List<Team> teams = mainActivity.getTeams();
        DraftManager draftManager = mainActivity.getDraftManager();
        PlayerManager playerManager = mainActivity.getPlayerManager();
        List<Pick> pickHistory = mainActivity.getPickHistory();
        
        if (teams == null || teams.isEmpty()) {
            // Fallback to simple dialog
            new AlertDialog.Builder(getActivity())
                    .setTitle("Draft Complete!")
                    .setMessage("The draft is complete! You can export the results from the Draft History screen.")
                    .setPositiveButton("OK", null)
                    .setCancelable(true)
                    .show();
            return;
        }
        
        // Get user's team (first team in list)
        Team userTeam = teams.get(0);
        
        // Generate analytics with curve-based grading for all teams
        List<com.fantasydraft.picker.models.DraftAnalytics> allAnalytics = 
                com.fantasydraft.picker.utils.DraftAnalyzer.analyzeAllTeamsWithCurve(
                        teams, pickHistory, playerManager);
        
        // Show analytics dialog with all teams
        DraftAnalyticsDialog analyticsDialog = new DraftAnalyticsDialog(
                getActivity(), allAnalytics, userTeam.getId());
        analyticsDialog.show();
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
            
            // Reset position filter
            selectedPositionFilter = "Overall";
            updatePositionFilterButtons();
            
            // Save the reset state
            mainActivity.saveDraftState();
            
            // Clear stopwatch persisted time
            if (getContext() != null) {
                getContext().getSharedPreferences("FantasyDraftPrefs", 0)
                        .edit().remove(PREF_STOPWATCH_START).apply();
            }
            
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
