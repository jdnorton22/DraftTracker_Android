package com.fantasydraft.picker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity to display the complete draft history.
 */
public class DraftHistoryActivity extends AppCompatActivity {

    public static final String EXTRA_PICK_HISTORY = "extra_pick_history";
    public static final String EXTRA_TEAMS = "extra_teams";
    public static final String EXTRA_PLAYERS = "extra_players";
    public static final String RESULT_UNDO_PICK = "result_undo_pick";
    public static final String RESULT_UNDO_POSITION = "result_undo_position";

    private RecyclerView recyclerView;
    private DraftHistoryAdapter adapter;
    private Button buttonClose;
    private Button buttonExportCsv;
    private Button buttonSort;
    private Spinner spinnerTeamFilter;
    
    private ArrayList<Pick> pickHistory;
    private ArrayList<Pick> allPicks; // Store all picks for filtering
    private Map<String, Team> teamMap;
    private Map<String, Player> playerMap;
    private String selectedTeamId = null; // null means "All Teams"
    private boolean sortDescending = true; // Default to descending (most recent first)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_history);
        
        // Fix ActionBar overlay by adding top padding programmatically
        View rootView = findViewById(android.R.id.content);
        if (rootView != null && getSupportActionBar() != null) {
            int actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight == 0) {
                // ActionBar height not yet measured, use standard height
                android.util.TypedValue tv = new android.util.TypedValue();
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    actionBarHeight = android.util.TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
                }
            }
            rootView.setPadding(0, actionBarHeight, 0, 0);
        }

        initializeViews();
        loadData();
        setupClickHandlers();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_draft_history);
        buttonClose = findViewById(R.id.button_close);
        buttonExportCsv = findViewById(R.id.button_export_csv);
        buttonSort = findViewById(R.id.button_sort);
        spinnerTeamFilter = findViewById(R.id.spinner_team_filter);

        adapter = new DraftHistoryAdapter();
        adapter.setUndoListener(this::handleUndoClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        // Get data from intent
        pickHistory = getIntent().getParcelableArrayListExtra(EXTRA_PICK_HISTORY);
        ArrayList<Team> teams = getIntent().getParcelableArrayListExtra(EXTRA_TEAMS);
        ArrayList<Player> players = getIntent().getParcelableArrayListExtra(EXTRA_PLAYERS);

        if (pickHistory == null) {
            pickHistory = new ArrayList<>();
        }
        if (teams == null) {
            teams = new ArrayList<>();
        }
        if (players == null) {
            players = new ArrayList<>();
        }

        // Store all picks for filtering
        allPicks = new ArrayList<>(pickHistory);

        // Create maps for quick lookup
        teamMap = new HashMap<>();
        for (Team team : teams) {
            teamMap.put(team.getId(), team);
        }

        playerMap = new HashMap<>();
        for (Player player : players) {
            playerMap.put(player.getId(), player);
        }

        // Setup team filter spinner
        setupTeamFilter(teams);

        // Apply default descending sort
        sortPicks();

        // Update adapter
        adapter.setData(pickHistory, teamMap, playerMap);
    }

    private void setupTeamFilter(ArrayList<Team> teams) {
        // Create list of team names for spinner
        List<String> teamNames = new ArrayList<>();
        teamNames.add("All Teams"); // First option shows all picks
        
        for (Team team : teams) {
            teamNames.add(team.getName());
        }

        // Create adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                teamNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeamFilter.setAdapter(spinnerAdapter);

        // Set up selection listener
        spinnerTeamFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All Teams" selected
                    selectedTeamId = null;
                    filterPicks();
                } else {
                    // Specific team selected
                    Team selectedTeam = teams.get(position - 1); // -1 because of "All Teams" at position 0
                    selectedTeamId = selectedTeam.getId();
                    filterPicks();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterPicks() {
        if (selectedTeamId == null) {
            // Show all picks
            pickHistory = new ArrayList<>(allPicks);
        } else {
            // Filter picks by selected team
            pickHistory = new ArrayList<>();
            for (Pick pick : allPicks) {
                if (pick.getTeamId().equals(selectedTeamId)) {
                    pickHistory.add(pick);
                }
            }
        }
        
        // Apply current sort order
        sortPicks();
        
        // Update adapter with filtered and sorted picks
        adapter.setData(pickHistory, teamMap, playerMap);
    }
    
    private void sortPicks() {
        if (sortDescending) {
            // Sort descending (most recent first - highest pick number first)
            pickHistory.sort((p1, p2) -> Integer.compare(p2.getPickNumber(), p1.getPickNumber()));
        } else {
            // Sort ascending (oldest first - lowest pick number first)
            pickHistory.sort((p1, p2) -> Integer.compare(p1.getPickNumber(), p2.getPickNumber()));
        }
    }
    
    private void toggleSort() {
        sortDescending = !sortDescending;
        
        // Update button text to show current sort direction
        if (sortDescending) {
            buttonSort.setText("Sort ▼");
        } else {
            buttonSort.setText("Sort ▲");
        }
        
        // Re-filter and sort
        filterPicks();
    }

    private void handleUndoClick(Pick pick, int position) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Undo Pick")
                .setMessage("Undo pick #" + pick.getPickNumber() + "?")
                .setPositiveButton("Undo", (dialog, which) -> {
                    // Return the pick to undo to MainActivity
                    android.content.Intent resultIntent = new android.content.Intent();
                    resultIntent.putExtra(RESULT_UNDO_PICK, pick);
                    resultIntent.putExtra(RESULT_UNDO_POSITION, position);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupClickHandlers() {
        buttonClose.setOnClickListener(v -> finish());
        buttonSort.setOnClickListener(v -> toggleSort());
        buttonExportCsv.setOnClickListener(v -> exportToCsv());
    }
    
    private void exportToCsv() {
        try {
            // Get all picks (not just filtered ones) for export
            ArrayList<Pick> picksToExport = new ArrayList<>(allPicks);
            ArrayList<Team> teams = new ArrayList<>(teamMap.values());
            ArrayList<Player> players = new ArrayList<>(playerMap.values());
            
            // Use DraftCsvExporter to export
            File csvFile = com.fantasydraft.picker.utils.DraftCsvExporter.exportToCSV(
                    this,
                    picksToExport,
                    teams,
                    players,
                    "Draft"
            );
            
            // Show success message and open file
            android.widget.Toast.makeText(this, "Draft exported successfully!", android.widget.Toast.LENGTH_SHORT).show();
            openCsvFile(csvFile);
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Error exporting: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    private void openCsvFile(java.io.File csvFile) {
        try {
            // Create a content URI using FileProvider
            android.net.Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    "com.fantasydraft.picker.fileprovider",
                    csvFile
            );
            
            // Create intent to view the file
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/plain");
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            
            startActivity(android.content.Intent.createChooser(intent, "Open CSV file"));
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "File saved to: " + csvFile.getAbsolutePath(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
}
