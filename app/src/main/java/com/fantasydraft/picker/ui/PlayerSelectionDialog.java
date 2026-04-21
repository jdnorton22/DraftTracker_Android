package com.fantasydraft.picker.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Player;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerSelectionDialog extends Dialog {

    private List<Player> players;
    private OnPlayerSelectedListener listener;
    private PlayerSelectionAdapter adapter;
    private Context context; // Store context reference
    
    // Draft context for advisor scores
    private com.fantasydraft.picker.models.Team currentTeam;
    private List<com.fantasydraft.picker.models.Pick> pickHistory;
    private List<com.fantasydraft.picker.models.Team> allTeams;
    private com.fantasydraft.picker.models.DraftConfig draftConfig;
    private int currentPickNumber;
    private int currentRound;
    
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Button cancelButton;
    private Button buttonDraftCustom;
    private android.widget.CheckBox hideDraftedCheckBox;
    private android.widget.TextView playerCountText;
    private ChipGroup positionChipGroup;
    private Spinner teamSpinner;
    private android.widget.LinearLayout filterHeader;
    private android.widget.LinearLayout filterContent;
    private android.widget.TextView filterExpandIcon;
    private android.widget.ProgressBar loadingIndicator;
    
    private boolean hideDrafted = true; // Default to hiding drafted players
    private String selectedPosition = "ALL"; // Default to all positions
    private String selectedTeam = "All Teams"; // Default to all teams
    private boolean filtersExpanded = false; // Default to collapsed

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(Player player);
    }

    public PlayerSelectionDialog(@NonNull Context context, List<Player> players, OnPlayerSelectedListener listener) {
        super(context);
        this.context = context;
        this.players = players;
        this.listener = listener;
    }
    
    /**
     * Constructor with draft context for advisor scores.
     */
    public PlayerSelectionDialog(@NonNull Context context, List<Player> players, 
                                  OnPlayerSelectedListener listener,
                                  com.fantasydraft.picker.models.Team currentTeam,
                                  List<com.fantasydraft.picker.models.Pick> pickHistory,
                                  List<com.fantasydraft.picker.models.Team> allTeams,
                                  com.fantasydraft.picker.models.DraftConfig draftConfig,
                                  int currentPickNumber,
                                  int currentRound) {
        super(context);
        this.context = context;
        this.players = players;
        this.listener = listener;
        this.currentTeam = currentTeam;
        this.pickHistory = pickHistory;
        this.allTeams = allTeams;
        this.draftConfig = draftConfig;
        this.currentPickNumber = currentPickNumber;
        this.currentRound = currentRound;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_player_selection);

        // Set dialog width to 95% of screen width for better visibility
        Window window = getWindow();
        if (window != null) {
            android.view.WindowManager.LayoutParams params = window.getAttributes();
            android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            params.width = (int) (displayMetrics.widthPixels * 0.95);
            window.setAttributes(params);
        }

        initializeViews();
        setupRecyclerView();
        setupSearchView();
        setupFilterToggle();
        setupPositionFilter();
        setupTeamFilter();
        setupHideDraftedToggle();
        setupCancelButton();
        setupCustomPlayerButton();
        updatePlayerCount();
    }

    private void initializeViews() {
        searchView = findViewById(R.id.player_search_view);
        recyclerView = findViewById(R.id.player_list_recycler_view);
        cancelButton = findViewById(R.id.cancel_button);
        buttonDraftCustom = findViewById(R.id.button_draft_custom);
        hideDraftedCheckBox = findViewById(R.id.checkbox_hide_drafted);
        playerCountText = findViewById(R.id.text_player_count);
        positionChipGroup = findViewById(R.id.position_chip_group);
        teamSpinner = findViewById(R.id.team_spinner);
        filterHeader = findViewById(R.id.filter_header);
        filterContent = findViewById(R.id.filter_content);
        filterExpandIcon = findViewById(R.id.filter_expand_icon);
        loadingIndicator = findViewById(R.id.loading_indicator);
    }

    private void setupRecyclerView() {
        adapter = new PlayerSelectionAdapter(players, this::handlePlayerSelection);
        adapter.setHideDrafted(hideDrafted); // Apply default hide drafted setting
        adapter.setCurrentPickNumber(currentPickNumber);
        adapter.setCurrentRound(currentRound);
        adapter.setSortByAdp(draftConfig != null && draftConfig.isSortByAdp());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        applyFilters(); // Apply initial filter to hide drafted players
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });
    }

    private void setupFilterToggle() {
        filterHeader.setOnClickListener(v -> {
            filtersExpanded = !filtersExpanded;
            filterContent.setVisibility(filtersExpanded ? android.view.View.VISIBLE : android.view.View.GONE);
            filterExpandIcon.setText(filtersExpanded ? "▲" : "▼");
        });
    }

    private void setupPositionFilter() {
        positionChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // If nothing is checked, default to "All"
                selectedPosition = "ALL";
                findViewById(R.id.chip_all_positions).performClick();
                return;
            }
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all_positions) {
                selectedPosition = "ALL";
            } else if (checkedId == R.id.chip_qb) {
                selectedPosition = "QB";
            } else if (checkedId == R.id.chip_rb) {
                selectedPosition = "RB";
            } else if (checkedId == R.id.chip_wr) {
                selectedPosition = "WR";
            } else if (checkedId == R.id.chip_te) {
                selectedPosition = "TE";
            } else if (checkedId == R.id.chip_k) {
                selectedPosition = "K";
            } else if (checkedId == R.id.chip_dst) {
                selectedPosition = "DST";
            }
            
            applyFilters();
        });
    }

    private void setupTeamFilter() {
        // Get unique teams from players list
        Set<String> teamsSet = new HashSet<>();
        teamsSet.add("All Teams");
        for (Player player : players) {
            if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
                teamsSet.add(player.getNflTeam());
            }
        }
        
        // Convert to sorted list
        List<String> teamsList = new ArrayList<>(teamsSet);
        teamsList.sort((a, b) -> {
            if (a.equals("All Teams")) return -1;
            if (b.equals("All Teams")) return 1;
            return a.compareTo(b);
        });
        
        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            teamsList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(adapter);
        
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedTeam = teamsList.get(position);
                applyFilters();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTeam = "All Teams";
                applyFilters();
            }
        });
    }

    private void setupHideDraftedToggle() {
        // Set checkbox to checked by default
        hideDraftedCheckBox.setChecked(true);
        
        hideDraftedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hideDrafted = isChecked;
            adapter.setHideDrafted(hideDrafted);
            applyFilters();
        });
    }

    private void applyFilters() {
        String query = searchView.getQuery().toString();
        adapter.filter(query, selectedPosition, selectedTeam);
        updatePlayerCount();
    }

    private void updatePlayerCount() {
        int visibleCount = adapter.getFilteredCount();
        int totalCount = adapter.getTotalCount();
        playerCountText.setText("Showing " + visibleCount + " of " + totalCount + " players");
    }

    private void setupCancelButton() {
        cancelButton.setOnClickListener(v -> dismiss());
    }
    
    private void setupCustomPlayerButton() {
        if (buttonDraftCustom == null) {
            // Button not found in layout
            return;
        }
        
        buttonDraftCustom.setOnClickListener(v -> {
            // Dismiss this dialog first
            dismiss();
            
            // Try to call showCustomPlayerDialog on the context
            // This will work once MainActivity is refactored to use fragments (task 5)
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.showCustomPlayerDialog();
            } else {
                Toast.makeText(getContext(), 
                    "Unable to open custom player dialog", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePlayerSelection(Player player) {
        // Validate player is not already drafted
        if (player.isDrafted()) {
            Toast.makeText(getContext(), 
                R.string.error_player_already_drafted, 
                Toast.LENGTH_SHORT).show();
            return;
        }

        // Show player detail card dialog
        showPlayerDetailCard(player);
    }
    
    /**
     * Show a detail card for the selected player with stats, links, and draft button.
     */
    private void showPlayerDetailCard(Player player) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        
        // Build the card view programmatically
        android.widget.ScrollView scrollView = new android.widget.ScrollView(getContext());
        android.widget.LinearLayout card = new android.widget.LinearLayout(getContext());
        card.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getContext().getResources().getDisplayMetrics().density);
        card.setPadding(pad, pad, pad, pad);
        scrollView.addView(card);
        
        float density = getContext().getResources().getDisplayMetrics().density;
        
        // Position badge + Name row
        android.widget.LinearLayout nameRow = new android.widget.LinearLayout(getContext());
        nameRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        nameRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        android.widget.TextView badge = new android.widget.TextView(getContext());
        badge.setText(player.getPosition());
        badge.setTextSize(14);
        badge.setTextColor(0xFFFFFFFF);
        badge.setGravity(android.view.Gravity.CENTER);
        int badgeSize = (int) (44 * density);
        android.widget.LinearLayout.LayoutParams badgeParams = new android.widget.LinearLayout.LayoutParams(badgeSize, badgeSize);
        badgeParams.rightMargin = (int) (12 * density);
        badge.setLayoutParams(badgeParams);
        android.graphics.drawable.GradientDrawable badgeCircle = new android.graphics.drawable.GradientDrawable();
        badgeCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        badgeCircle.setColor(com.fantasydraft.picker.utils.PositionColors.getColorForPosition(player.getPosition()));
        badge.setBackground(badgeCircle);
        nameRow.addView(badge);
        
        android.widget.LinearLayout nameCol = new android.widget.LinearLayout(getContext());
        nameCol.setOrientation(android.widget.LinearLayout.VERTICAL);
        
        android.widget.TextView nameText = new android.widget.TextView(getContext());
        nameText.setText(player.isFavorite() ? player.getName() + " ⭐" : player.getName());
        nameText.setTextSize(18);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        nameCol.addView(nameText);
        
        // Team + Position rank subtitle
        StringBuilder subtitle = new StringBuilder();
        if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
            subtitle.append(player.getNflTeam());
        }
        if (player.getPositionRank() > 0) {
            if (subtitle.length() > 0) subtitle.append(" · ");
            subtitle.append(player.getPosition()).append(player.getPositionRank());
        }
        if (player.getByeWeek() > 0) {
            if (subtitle.length() > 0) subtitle.append(" · ");
            subtitle.append("Bye ").append(player.getByeWeek());
        }
        
        if (subtitle.length() > 0) {
            android.widget.TextView subtitleText = new android.widget.TextView(getContext());
            subtitleText.setText(subtitle.toString());
            subtitleText.setTextSize(13);
            nameCol.addView(subtitleText);
        }
        nameRow.addView(nameCol);
        card.addView(nameRow);
        
        // ESPN + Depth Chart buttons row
        String espnUrlFinal = player.getEspnUrl();
        String depthChartUrlFinal = player.getEspnDepthChartUrl();
        if (espnUrlFinal != null || depthChartUrlFinal != null) {
            android.widget.LinearLayout buttonRow = new android.widget.LinearLayout(getContext());
            buttonRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            buttonRow.setGravity(android.view.Gravity.CENTER);
            android.widget.LinearLayout.LayoutParams btnRowParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            btnRowParams.topMargin = (int) (10 * density);
            btnRowParams.bottomMargin = (int) (4 * density);
            buttonRow.setLayoutParams(btnRowParams);
            
            if (espnUrlFinal != null) {
                android.widget.Button espnBtn = new android.widget.Button(getContext());
                espnBtn.setText("View on ESPN");
                espnBtn.setTextSize(13);
                espnBtn.setAllCaps(false);
                espnBtn.setTextColor(0xFFFFFFFF);
                espnBtn.setBackgroundColor(0xFF1976D2); // Blue
                android.widget.LinearLayout.LayoutParams espnParams = new android.widget.LinearLayout.LayoutParams(
                    0, (int) (48 * density), 1);
                espnParams.rightMargin = (int) (4 * density);
                espnBtn.setLayoutParams(espnParams);
                espnBtn.setOnClickListener(v -> {
                    android.content.Intent browserIntent = new android.content.Intent(
                        android.content.Intent.ACTION_VIEW, android.net.Uri.parse(espnUrlFinal));
                    getContext().startActivity(browserIntent);
                });
                buttonRow.addView(espnBtn);
            }
            
            if (depthChartUrlFinal != null) {
                android.widget.Button depthBtn = new android.widget.Button(getContext());
                depthBtn.setText("Depth Chart");
                depthBtn.setTextSize(13);
                depthBtn.setAllCaps(false);
                depthBtn.setTextColor(0xFFFFFFFF);
                depthBtn.setBackgroundColor(0xFF4CAF50); // Green
                android.widget.LinearLayout.LayoutParams depthParams = new android.widget.LinearLayout.LayoutParams(
                    0, (int) (48 * density), 1);
                depthParams.leftMargin = (int) (4 * density);
                depthBtn.setLayoutParams(depthParams);
                depthBtn.setOnClickListener(v -> {
                    android.content.Intent browserIntent = new android.content.Intent(
                        android.content.Intent.ACTION_VIEW, android.net.Uri.parse(depthChartUrlFinal));
                    getContext().startActivity(browserIntent);
                });
                buttonRow.addView(depthBtn);
            }
            
            card.addView(buttonRow);
        }
        
        // Divider
        android.view.View divider = new android.view.View(getContext());
        divider.setBackgroundColor(0xFFE0E0E0);
        android.widget.LinearLayout.LayoutParams divParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, (int) (1 * density));
        divParams.topMargin = (int) (12 * density);
        divParams.bottomMargin = (int) (12 * density);
        divider.setLayoutParams(divParams);
        card.addView(divider);
        
        // Stats row: ADP | Position Rank | Rank
        android.widget.LinearLayout statsRow = new android.widget.LinearLayout(getContext());
        statsRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        statsRow.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams statsRowParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        statsRowParams.bottomMargin = (int) (8 * density);
        statsRow.setLayoutParams(statsRowParams);
        
        if (player.getPffRank() > 0) {
            addStatChip(statsRow, "ADP", String.valueOf(player.getPffRank()), density);
        }
        if (player.getPositionRank() > 0) {
            addStatChip(statsRow, "Pos Rank", player.getPosition() + player.getPositionRank(), density);
        }
        addStatChip(statsRow, "Overall", "#" + player.getRank(), density);
        card.addView(statsRow);
        
        // Last year stats
        if (player.getLastYearStats() != null && !player.getLastYearStats().isEmpty()) {
            android.widget.TextView statsText = new android.widget.TextView(getContext());
            statsText.setText("📊 " + player.getLastYearStats());
            statsText.setTextSize(13);
            android.widget.LinearLayout.LayoutParams stParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            stParams.bottomMargin = (int) (8 * density);
            statsText.setLayoutParams(stParams);
            card.addView(statsText);
        }
        
        // Injury status
        if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty() && 
            !player.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
            android.widget.TextView injuryText = new android.widget.TextView(getContext());
            injuryText.setText("🏥 " + player.getInjuryStatus());
            injuryText.setTextSize(13);
            injuryText.setTypeface(null, android.graphics.Typeface.BOLD);
            String status = player.getInjuryStatus().toUpperCase();
            if (status.equals("OUT") || status.equals("IR")) {
                injuryText.setTextColor(0xFFD32F2F);
            } else if (status.equals("DOUBTFUL")) {
                injuryText.setTextColor(0xFFFF6F00);
            } else if (status.equals("QUESTIONABLE")) {
                injuryText.setTextColor(0xFFFFA000);
            }
            android.widget.LinearLayout.LayoutParams injParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            injParams.bottomMargin = (int) (8 * density);
            injuryText.setLayoutParams(injParams);
            card.addView(injuryText);
        }
        
        // Draft Advisor score
        if (currentTeam != null && pickHistory != null && draftConfig != null) {
            com.fantasydraft.picker.utils.DraftAdvisor.Recommendation rec = 
                com.fantasydraft.picker.utils.DraftAdvisor.getRecommendation(
                    players, currentTeam, pickHistory, allTeams, draftConfig, currentPickNumber);
            
            // Get this specific player's recommendation using full pool for context
            com.fantasydraft.picker.utils.DraftAdvisor.Recommendation playerRec = 
                com.fantasydraft.picker.utils.DraftAdvisor.getRecommendationForPlayer(
                    player, players, currentTeam, pickHistory, allTeams, draftConfig, currentPickNumber);
            
            if (playerRec != null) {
                // Divider before advisor
                android.view.View advDivider = new android.view.View(getContext());
                advDivider.setBackgroundColor(0xFFE0E0E0);
                android.widget.LinearLayout.LayoutParams advDivParams = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT, (int) (1 * density));
                advDivParams.topMargin = (int) (4 * density);
                advDivParams.bottomMargin = (int) (8 * density);
                advDivider.setLayoutParams(advDivParams);
                card.addView(advDivider);
                
                String tagEmoji;
                int tagColor;
                switch (playerRec.getTag()) {
                    case "VALUE": tagEmoji = "💰"; tagColor = 0xFF4CAF50; break;
                    case "NEED": tagEmoji = "🎯"; tagColor = 0xFF2196F3; break;
                    case "SCARCITY": tagEmoji = "⚡"; tagColor = 0xFFFF9800; break;
                    default: tagEmoji = "📋"; tagColor = 0xFF757575; break;
                }
                
                android.widget.TextView advisorLabel = new android.widget.TextView(getContext());
                advisorLabel.setText("Draft Advisor");
                advisorLabel.setTextSize(12);
                advisorLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                android.widget.LinearLayout.LayoutParams labelParams = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                labelParams.bottomMargin = (int) (2 * density);
                advisorLabel.setLayoutParams(labelParams);
                card.addView(advisorLabel);
                
                android.widget.TextView advisorText = new android.widget.TextView(getContext());
                advisorText.setText(tagEmoji + " " + playerRec.getReasoning());
                advisorText.setTextSize(13);
                advisorText.setTextColor(tagColor);
                android.widget.LinearLayout.LayoutParams advParams = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                advParams.bottomMargin = (int) (4 * density);
                advisorText.setLayoutParams(advParams);
                card.addView(advisorText);
                
                // Show if this player is the top recommendation
                if (rec != null && rec.getPlayer().getId().equals(player.getId())) {
                    android.widget.TextView topPickText = new android.widget.TextView(getContext());
                    topPickText.setText("⭐ #1 Recommended Pick");
                    topPickText.setTextSize(13);
                    topPickText.setTypeface(null, android.graphics.Typeface.BOLD);
                    topPickText.setTextColor(0xFF4CAF50);
                    card.addView(topPickText);
                }
            }
        }
        
        // Action buttons row
        android.widget.LinearLayout actionRow = new android.widget.LinearLayout(getContext());
        actionRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        actionRow.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams actionParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        actionParams.topMargin = (int) (12 * density);
        actionRow.setLayoutParams(actionParams);
        
        android.widget.Button cancelBtn = new android.widget.Button(getContext());
        cancelBtn.setText("Cancel");
        cancelBtn.setTextSize(14);
        cancelBtn.setAllCaps(false);
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setBackgroundColor(0xFF757575); // Gray
        android.widget.LinearLayout.LayoutParams cancelParams = new android.widget.LinearLayout.LayoutParams(
            0, (int) (48 * density), 1);
        cancelParams.rightMargin = (int) (4 * density);
        cancelBtn.setLayoutParams(cancelParams);
        actionRow.addView(cancelBtn);
        
        android.widget.Button draftBtn = new android.widget.Button(getContext());
        draftBtn.setText("Draft");
        draftBtn.setTextSize(14);
        draftBtn.setAllCaps(false);
        draftBtn.setTextColor(0xFFFFFFFF);
        draftBtn.setBackgroundColor(0xFF4682B4); // Steel Blue
        android.widget.LinearLayout.LayoutParams draftParams = new android.widget.LinearLayout.LayoutParams(
            0, (int) (48 * density), 1);
        draftParams.leftMargin = (int) (4 * density);
        draftBtn.setLayoutParams(draftParams);
        actionRow.addView(draftBtn);
        
        card.addView(actionRow);
        
        // Build and show dialog
        builder.setView(scrollView);
        android.app.AlertDialog dialog = builder.create();
        
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        draftBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerSelected(player);
            }
            dialog.dismiss();
            dismiss();
        });
        
        dialog.show();
    }
    
    /**
     * Add a stat chip (label + value) to a row.
     */
    private void addStatChip(android.widget.LinearLayout row, String label, String value, float density) {
        android.widget.LinearLayout chip = new android.widget.LinearLayout(getContext());
        chip.setOrientation(android.widget.LinearLayout.VERTICAL);
        chip.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams chipParams = new android.widget.LinearLayout.LayoutParams(
            0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        chip.setLayoutParams(chipParams);
        
        android.widget.TextView valueText = new android.widget.TextView(getContext());
        valueText.setText(value);
        valueText.setTextSize(16);
        valueText.setTypeface(null, android.graphics.Typeface.BOLD);
        valueText.setGravity(android.view.Gravity.CENTER);
        chip.addView(valueText);
        
        android.widget.TextView labelText = new android.widget.TextView(getContext());
        labelText.setText(label);
        labelText.setTextSize(11);
        labelText.setGravity(android.view.Gravity.CENTER);
        chip.addView(labelText);
        
        row.addView(chip);
    }

    public void updatePlayers(List<Player> newPlayers) {
        this.players = newPlayers;
        if (adapter != null) {
            adapter.updatePlayers(newPlayers);
            updatePlayerCount();
        }
    }
    
    /**
     * Show loading indicator and hide content
     */
    public void showLoading() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(android.view.View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(android.view.View.GONE);
        }
        if (cancelButton != null) {
            cancelButton.setEnabled(false);
        }
        if (buttonDraftCustom != null) {
            buttonDraftCustom.setEnabled(false);
        }
    }
    
    /**
     * Hide loading indicator and show content
     */
    public void hideLoading() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(android.view.View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
        if (cancelButton != null) {
            cancelButton.setEnabled(true);
        }
        if (buttonDraftCustom != null) {
            buttonDraftCustom.setEnabled(true);
        }
    }
}
