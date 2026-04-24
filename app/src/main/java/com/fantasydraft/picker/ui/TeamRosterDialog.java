package com.fantasydraft.picker.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.DraftManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Dialog that displays all players drafted by a selected team during a fantasy draft session.
 * Includes a team selector Spinner to switch between teams and shows a scrollable list of
 * roster items with comprehensive player attributes.
 */
public class TeamRosterDialog extends Dialog {

    private final List<Team> teams;
    private final String defaultTeamId;
    private final DraftManager draftManager;
    private final PlayerManager playerManager;
    private final Context context;
    private final com.fantasydraft.picker.models.DraftConfig draftConfig;

    // UI elements
    private TextView dialogTitle;
    private Spinner spinnerTeamSelector;
    private TextView textPositionRequirements;
    private RecyclerView recyclerRoster;
    private TextView textEmptyState;
    private Button buttonClose;

    private TeamRosterAdapter adapter;
    private List<Team> sortedTeams;

    /**
     * Creates a new TeamRosterDialog.
     *
     * @param context The context
     * @param teams List of all teams in the draft
     * @param defaultTeamId The team ID to select by default (typically the on-the-clock team)
     * @param draftManager The draft manager for accessing pick history
     * @param playerManager The player manager for resolving player details
     * @param draftConfig The draft configuration with position requirements
     */
    public TeamRosterDialog(@NonNull Context context,
                            List<Team> teams,
                            String defaultTeamId,
                            DraftManager draftManager,
                            PlayerManager playerManager,
                            com.fantasydraft.picker.models.DraftConfig draftConfig) {
        super(context);
        this.context = context;
        this.teams = teams;
        this.defaultTeamId = defaultTeamId;
        this.draftManager = draftManager;
        this.playerManager = playerManager;
        this.draftConfig = draftConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_team_roster);

        // Set dialog width to 95% of screen width for better visibility
        Window window = getWindow();
        if (window != null) {
            android.view.WindowManager.LayoutParams params = window.getAttributes();
            android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            params.width = (int) (displayMetrics.widthPixels * 0.95);
            window.setAttributes(params);
        }

        initializeViews();
    }

    /**
     * Binds all UI elements from the layout.
     * Sets up the close button to dismiss the dialog.
     */
    private void initializeViews() {
        // Bind UI elements (Req 3.1)
        dialogTitle = findViewById(R.id.dialog_title);
        spinnerTeamSelector = findViewById(R.id.spinner_team_selector);
        textPositionRequirements = findViewById(R.id.text_position_requirements);
        recyclerRoster = findViewById(R.id.recycler_roster);
        textEmptyState = findViewById(R.id.text_empty_state);
        buttonClose = findViewById(R.id.button_close);

        // Set up RecyclerView with adapter
        adapter = new TeamRosterAdapter(null);
        recyclerRoster.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRoster.setAdapter(adapter);

        // Set up close button to dismiss dialog (Req 3.4)
        buttonClose.setOnClickListener(v -> dismiss());

        // Set up team spinner (Req 2.1, 2.2, 2.5)
        setupTeamSpinner();
    }

    /**
     * Sets up the team selector Spinner.
     * Sorts teams by draftPosition ascending (Req 2.5), populates the Spinner with team names (Req 2.1),
     * sets the default selection to defaultTeamId (Req 2.2), and handles selection changes
     * to update the dialog title (Req 2.4) and load the roster (Req 2.3).
     */
    private void setupTeamSpinner() {
        // Sort teams by draftPosition ascending (Req 2.5)
        sortedTeams = new ArrayList<>(teams);
        Collections.sort(sortedTeams, Comparator.comparingInt(Team::getDraftPosition));

        // Create adapter with team names
        List<String> teamNames = new ArrayList<>();
        for (Team team : sortedTeams) {
            teamNames.add(team.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, teamNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeamSelector.setAdapter(spinnerAdapter);

        // Find index of defaultTeamId and set as selection (Req 2.2)
        int defaultIndex = 0;
        for (int i = 0; i < sortedTeams.size(); i++) {
            if (sortedTeams.get(i).getId().equals(defaultTeamId)) {
                defaultIndex = i;
                break;
            }
        }
        spinnerTeamSelector.setSelection(defaultIndex);

        // Handle team selection changes (Req 2.3, 2.4)
        spinnerTeamSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Team selectedTeam = sortedTeams.get(position);
                dialogTitle.setText(selectedTeam.getName() + " Roster");
                loadRosterForTeam(selectedTeam.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    /**
     * Builds a list of RosterEntry objects for the given team.
     * Filters pick history by team ID, resolves player details, and sorts by pick number.
     *
     * @param teamId The ID of the team whose roster entries to build
     * @return List of RosterEntry objects sorted by pick number ascending
     */
    private List<RosterEntry> buildRosterEntries(String teamId) {
        List<RosterEntry> entries = new ArrayList<>();

        // Get pick history and filter by team ID (Req 6.1, 6.2)
        List<Pick> pickHistory = draftManager.getPickHistory();
        for (Pick pick : pickHistory) {
            if (pick.getTeamId().equals(teamId)) {
                // Resolve player via PlayerManager (Req 6.3)
                // Player may be null for unknown players (Req 6.4)
                Player player = playerManager.getPlayerById(pick.getPlayerId());
                entries.add(new RosterEntry(pick, player));
            }
        }

        // Sort entries by pick number ascending (Req 4.13)
        Collections.sort(entries, Comparator.comparingInt(entry -> entry.getPick().getPickNumber()));

        return entries;
    }

    /**
     * Loads and displays the roster for the given team.
     * Shows empty state when team has no picks, otherwise displays the roster list.
     *
     * @param teamId The ID of the team whose roster to load
     */
    private void loadRosterForTeam(String teamId) {
        List<RosterEntry> entries = buildRosterEntries(teamId);

        // Show/hide empty state vs RecyclerView based on entry count (Req 3.3)
        if (entries.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            recyclerRoster.setVisibility(View.GONE);
        } else {
            textEmptyState.setVisibility(View.GONE);
            recyclerRoster.setVisibility(View.VISIBLE);
        }

        // Update adapter with new entries
        adapter.updateEntries(entries);
        
        // Update position requirements display
        updatePositionRequirements(entries);
    }
    
    /**
     * Updates the position requirements display with current counts.
     * Shows a compact summary of position counts vs requirements.
     *
     * @param entries The roster entries for the current team
     */
    private void updatePositionRequirements(List<RosterEntry> entries) {
        if (draftConfig == null || textPositionRequirements == null) {
            return;
        }
        
        // Count players by position
        java.util.Map<String, Integer> positionCounts = new java.util.HashMap<>();
        for (RosterEntry entry : entries) {
            if (entry.getPlayer() != null) {
                String position = entry.getPlayer().getPosition();
                positionCounts.put(position, positionCounts.getOrDefault(position, 0) + 1);
            }
        }
        
        // Build compact display string
        StringBuilder sb = new StringBuilder();
        String[] positions = {"QB", "RB", "WR", "TE", "K", "DST"};
        
        for (int i = 0; i < positions.length; i++) {
            String pos = positions[i];
            int count = positionCounts.getOrDefault(pos, 0);
            com.fantasydraft.picker.models.DraftConfig.PositionRequirement req = 
                draftConfig.getPositionRequirement(pos);
            
            if (req != null) {
                int min = req.getMin();
                int max = req.getMax();
                
                // Color code based on requirements
                boolean meetsMin = count >= min;
                boolean exceedsMax = max >= 0 && count > max;
                
                if (exceedsMax) {
                    sb.append("⚠️ "); // Warning for exceeding max
                } else if (!meetsMin) {
                    sb.append("❌ "); // X for not meeting min
                } else {
                    sb.append("✓ "); // Check for meeting requirements
                }
                
                sb.append(pos).append(": ").append(count);
                
                // Show requirements
                if (max >= 0) {
                    sb.append("/").append(min).append("-").append(max);
                } else {
                    sb.append("/").append(min).append("+");
                }
                
                if (i < positions.length - 1) {
                    sb.append("  ");
                }
            }
        }
        
        textPositionRequirements.setText(sb.toString());
        textPositionRequirements.setVisibility(View.VISIBLE);
    }
}
