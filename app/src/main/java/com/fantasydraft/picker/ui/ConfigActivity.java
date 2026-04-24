package com.fantasydraft.picker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.DraftCoordinator;
import com.fantasydraft.picker.managers.PlayerDataRefreshManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.managers.TeamManager;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.persistence.PersistenceManager;
import com.fantasydraft.picker.utils.PlayerDataParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity for configuring teams and draft settings.
 * Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 3.1, 3.2, 3.5
 */
public class ConfigActivity extends AppCompatActivity {
    
    public static final String EXTRA_TEAMS = "extra_teams";
    public static final String EXTRA_DRAFT_CONFIG = "extra_draft_config";
    
    private static final int MIN_TEAMS = 2;
    private static final int MAX_TEAMS = 20;
    private static final int DEFAULT_TEAMS = 10;
    private static final int MIN_ROUNDS = 1;
    private static final int MAX_ROUNDS = 20;
    private static final int DEFAULT_ROUNDS = 15;
    
    // UI Components
    private com.google.android.material.textfield.TextInputEditText inputLeagueName;
    private NumberPicker numberPickerTeamCount;
    private NumberPicker numberPickerRounds;
    private Spinner spinnerDraftFlow;
    private CheckBox checkboxSkipFirstRound;
    private RecyclerView recyclerTeams;
    private Button buttonSaveConfig;
    private Button buttonImportPlayers;
    // COMMENTED OUT - Refresh button disabled for future revisit
    // private Button buttonRefreshPlayerData;
    
    // Adapter and data
    private TeamConfigAdapter teamAdapter;
    private TeamManager teamManager;
    private List<Team> teams;
    private DraftConfig draftConfig;
    
    // Progress dialog
    private AlertDialog progressDialog;
    
    // File picker request code
    private static final int PICK_JSON_FILE = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        
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
        
        // Initialize manager
        teamManager = new TeamManager();
        
        // Initialize UI components
        initializeViews();
        
        // Load existing configuration or create default
        loadConfiguration();
        
        // Set up UI components
        setupNumberPicker();
        setupRoundsPicker();
        setupSpinner();
        setupRecyclerView();
        setupSaveButton();
        setupImportButton();
        // COMMENTED OUT - Refresh button disabled
        // setupRefreshButton();
    }
    
    /**
     * Initialize all UI view references.
     */
    private void initializeViews() {
        inputLeagueName = findViewById(R.id.input_league_name);
        numberPickerTeamCount = findViewById(R.id.number_picker_team_count);
        numberPickerRounds = findViewById(R.id.number_picker_rounds);
        spinnerDraftFlow = findViewById(R.id.spinner_draft_flow);
        checkboxSkipFirstRound = findViewById(R.id.checkbox_skip_first_round);
        recyclerTeams = findViewById(R.id.recycler_teams);
        buttonSaveConfig = findViewById(R.id.button_save_config);
        buttonImportPlayers = findViewById(R.id.button_import_players);
        // COMMENTED OUT - Refresh button disabled
        // buttonRefreshPlayerData = findViewById(R.id.button_refresh_player_data);
    }
    
    /**
     * Load existing configuration from intent or create default.
     * Requirements: 1.1, 1.2, 3.1
     */
    private void loadConfiguration() {
        Intent intent = getIntent();
        
        // Load teams if provided
        ArrayList<Team> existingTeams = intent.getParcelableArrayListExtra(EXTRA_TEAMS);
        if (existingTeams != null && !existingTeams.isEmpty()) {
            teams = new ArrayList<>(existingTeams);
        } else {
            // Create default teams
            teams = createDefaultTeams(DEFAULT_TEAMS);
        }
        
        // Load draft config if provided
        DraftConfig existingConfig = intent.getParcelableExtra(EXTRA_DRAFT_CONFIG);
        if (existingConfig != null) {
            draftConfig = existingConfig;
        } else {
            // Create default config
            draftConfig = new DraftConfig(FlowType.SERPENTINE, DEFAULT_ROUNDS);
        }
        
        // Set league name in input field
        if (inputLeagueName != null && draftConfig != null) {
            inputLeagueName.setText(draftConfig.getLeagueName());
        }
        
        // Set skip first round checkbox
        if (checkboxSkipFirstRound != null && draftConfig != null) {
            checkboxSkipFirstRound.setChecked(draftConfig.isSkipFirstRound());
        }
    }
    
    /**
     * Create default teams with sequential names and positions.
     * Requirements: 1.2, 2.1
     */
    private List<Team> createDefaultTeams(int count) {
        List<Team> defaultTeams = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Team team = new Team();
            team.setId("team_" + i);
            team.setName("Team " + i);
            team.setDraftPosition(i);
            team.setRoster(new ArrayList<>());
            defaultTeams.add(team);
        }
        return defaultTeams;
    }
    
    /**
     * Set up the number picker for team count.
     * Requirements: 1.1
     */
    private void setupNumberPicker() {
        numberPickerTeamCount.setMinValue(MIN_TEAMS);
        numberPickerTeamCount.setMaxValue(MAX_TEAMS);
        numberPickerTeamCount.setValue(teams.size());
        numberPickerTeamCount.setWrapSelectorWheel(false);
        
        // Handle team count changes
        numberPickerTeamCount.setOnValueChangedListener((picker, oldVal, newVal) -> {
            handleTeamCountChange(newVal);
        });
    }
    
    /**
     * Set up the number picker for rounds.
     */
    private void setupRoundsPicker() {
        numberPickerRounds.setMinValue(MIN_ROUNDS);
        numberPickerRounds.setMaxValue(MAX_ROUNDS);
        numberPickerRounds.setValue(draftConfig.getNumberOfRounds());
        numberPickerRounds.setWrapSelectorWheel(false);
    }
    
    /**
     * Handle changes to team count.
     * Requirements: 1.1, 2.1
     */
    private void handleTeamCountChange(int newCount) {
        // Validate team count
        if (newCount < MIN_TEAMS || newCount > MAX_TEAMS) {
            Toast.makeText(this, R.string.error_invalid_team_count, 
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        int currentCount = teams.size();
        
        if (newCount > currentCount) {
            // Add new teams
            for (int i = currentCount + 1; i <= newCount; i++) {
                Team team = new Team();
                team.setId("team_" + i);
                team.setName("Team " + i);
                team.setDraftPosition(i);
                team.setRoster(new ArrayList<>());
                teams.add(team);
            }
        } else if (newCount < currentCount) {
            // Remove teams from the end
            teams = new ArrayList<>(teams.subList(0, newCount));
        }
        
        // Update adapter
        teamAdapter.setTeams(teams);
    }
    
    /**
     * Set up the spinner for draft flow selection.
     * Requirements: 3.1, 3.2
     */
    private void setupSpinner() {
        // Create adapter with flow types
        String[] flowTypes = {
                getString(R.string.serpentine),
                getString(R.string.linear)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                flowTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDraftFlow.setAdapter(adapter);
        
        // Set current selection
        int selection = draftConfig.getFlowType() == FlowType.SERPENTINE ? 0 : 1;
        spinnerDraftFlow.setSelection(selection);
    }
    
    /**
     * Set up the RecyclerView for team list.
     * Requirements: 1.2, 1.3, 2.1
     */
    private void setupRecyclerView() {
        teamAdapter = new TeamConfigAdapter();
        teamAdapter.setTeams(teams);
        
        // Set up listener for team name changes
        teamAdapter.setTeamNameChangeListener((position, newName) -> {
            // Validation will happen on save
        });
        
        recyclerTeams.setLayoutManager(new LinearLayoutManager(this));
        recyclerTeams.setAdapter(teamAdapter);
    }
    
    /**
     * Set up the save button.
     * Requirements: 1.4, 2.4
     */
    private void setupSaveButton() {
        buttonSaveConfig.setOnClickListener(v -> saveConfiguration());
    }
    
    /**
     * Validate and save configuration.
     * Requirements: 1.2, 1.3, 2.1, 2.2, 3.5
     */
    private void saveConfiguration() {
        // Get current teams from adapter
        teams = teamAdapter.getTeams();
        
        // Validate team names
        if (!validateTeamNames()) {
            return;
        }
        
        // Validate draft order
        if (!validateDraftOrder()) {
            return;
        }
        
        // Update league name from input
        String leagueName = inputLeagueName.getText().toString().trim();
        if (!leagueName.isEmpty()) {
            draftConfig.setLeagueName(leagueName);
        }
        
        // Update draft config with selected flow type
        int flowSelection = spinnerDraftFlow.getSelectedItemPosition();
        FlowType flowType = flowSelection == 0 ? FlowType.SERPENTINE : FlowType.LINEAR;
        draftConfig.setFlowType(flowType);
        
        // Update number of rounds from picker
        int numberOfRounds = numberPickerRounds.getValue();
        draftConfig.setNumberOfRounds(numberOfRounds);
        
        // Update skip first round from checkbox
        boolean skipFirstRound = checkboxSkipFirstRound.isChecked();
        boolean wasSkipFirstRound = draftConfig.isSkipFirstRound();
        draftConfig.setSkipFirstRound(skipFirstRound);
        
        // Prepare result intent
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra(EXTRA_TEAMS, new ArrayList<>(teams));
        resultIntent.putExtra(EXTRA_DRAFT_CONFIG, draftConfig);
        
        setResult(RESULT_OK, resultIntent);
        
        // Show appropriate message based on whether keeper setting changed
        if (skipFirstRound != wasSkipFirstRound && skipFirstRound) {
            Toast.makeText(this, "Keeper league enabled. Reset draft to apply changes.", Toast.LENGTH_LONG).show();
        } else if (skipFirstRound != wasSkipFirstRound && !skipFirstRound) {
            Toast.makeText(this, "Keeper league disabled. Reset draft to apply changes.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    
    /**
     * Validate that all team names are unique and non-empty.
     * Requirements: 1.2, 1.3
     */
    private boolean validateTeamNames() {
        Set<String> nameSet = new HashSet<>();
        
        for (Team team : teams) {
            String name = team.getName();
            
            // Check for empty names
            if (name == null || name.trim().isEmpty()) {
                Toast.makeText(this, R.string.error_empty_team_name, 
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Check for duplicate names
            String trimmedName = name.trim();
            if (nameSet.contains(trimmedName)) {
                Toast.makeText(this, R.string.error_duplicate_team_name, 
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            
            nameSet.add(trimmedName);
        }
        
        return true;
    }
    
    /**
     * Validate that draft order is complete (1 to N with no gaps).
     * Requirements: 2.1, 2.2
     */
    private boolean validateDraftOrder() {
        if (!teamManager.validateDraftOrder(teams)) {
            Toast.makeText(this, R.string.error_incomplete_draft_order, 
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    /**
     * Set up the import players button.
     */
    private void setupImportButton() {
        // Check feature flag
        if (com.fantasydraft.picker.utils.FeatureFlags.ENABLE_IMPORT_PLAYERS) {
            buttonImportPlayers.setVisibility(View.VISIBLE);
            buttonImportPlayers.setOnClickListener(v -> openFilePicker());
        } else {
            buttonImportPlayers.setVisibility(View.GONE);
        }
    }
    
    /**
     * Open file picker to select players.json file.
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        
        // Also accept all files in case JSON MIME type isn't recognized
        String[] mimeTypes = {"application/json", "text/plain", "*/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        
        try {
            startActivityForResult(intent, PICK_JSON_FILE);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No file picker app found. Please install a file manager.", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_JSON_FILE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                android.net.Uri uri = data.getData();
                importPlayersFromUri(uri);
            }
        }
    }
    
    /**
     * Import players from the selected file URI.
     */
    private void importPlayersFromUri(android.net.Uri uri) {
        try {
            // Read file content
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Read the entire file into a string
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            inputStream.close();
            
            String jsonContent = stringBuilder.toString();
            
            // Parse and validate the JSON
            PlayerDataParser parser = new PlayerDataParser();
            List<Player> players = parser.parseESPNData(jsonContent);
            
            if (players == null || players.isEmpty()) {
                Toast.makeText(this, "No valid player data found in file", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Save to internal storage
            savePlayersToInternalStorage(jsonContent);
            
            // Show success message
            String message = "Successfully imported " + players.size() + " players. " +
                    "Restart the app or reset the draft to use the new data.";
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Import Successful")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error importing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    /**
     * Save players JSON to internal storage.
     */
    private void savePlayersToInternalStorage(String jsonContent) throws java.io.IOException {
        java.io.File file = new java.io.File(getFilesDir(), "players_updated.json");
        java.io.FileWriter writer = new java.io.FileWriter(file);
        writer.write(jsonContent);
        writer.close();
    }
    
    /**
     * Set up the refresh player data button.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void setupRefreshButton() {
        buttonRefreshPlayerData.setOnClickListener(v -> showRefreshConfirmation());
    }
    */
    
    /**
     * Show confirmation dialog before refreshing player data.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void showRefreshConfirmation() {
        // Get pick history from intent to check if draft is in progress
        Intent intent = getIntent();
        ArrayList<Pick> pickHistory = intent.getParcelableArrayListExtra("EXTRA_PICK_HISTORY");
        boolean isDraftInProgress = pickHistory != null && !pickHistory.isEmpty();
        
        String message;
        if (isDraftInProgress) {
            message = "This will fetch the latest player data from ESPN and reset your current draft. All picks will be cleared. Continue?";
        } else {
            message = "This will fetch the latest player data from ESPN including updated injury statuses. Continue?";
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Refresh Player Data?")
                .setMessage(message)
                .setPositiveButton("Refresh", (dialog, which) -> performRefresh())
                .setNegativeButton("Cancel", null)
                .show();
    }
    */
    /**
     * Perform the player data refresh operation.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void performRefresh() {
        // Add immediate feedback to confirm button click
        Toast.makeText(this, "Starting refresh...", Toast.LENGTH_SHORT).show();
        
        // Show progress dialog
        showRefreshProgress();
        
        // Disable button during operation
        buttonRefreshPlayerData.setEnabled(false);
        
        // Get dependencies from intent
        Intent intent = getIntent();
        ArrayList<Pick> pickHistory = intent.getParcelableArrayListExtra("EXTRA_PICK_HISTORY");
        if (pickHistory == null) {
            pickHistory = new ArrayList<>();
        }
        
        // Create managers (simplified - in real implementation these would come from MainActivity)
        PlayerManager playerManager = new PlayerManager();
        DraftCoordinator draftCoordinator = new DraftCoordinator(null, playerManager, teamManager);
        PersistenceManager persistenceManager = new PersistenceManager(this);
        
        // Create refresh manager
        PlayerDataRefreshManager refreshManager = new PlayerDataRefreshManager(
                this,
                playerManager,
                draftCoordinator,
                persistenceManager,
                teams,
                pickHistory
        );
        
        // Perform refresh
        refreshManager.refreshPlayerData(new PlayerDataRefreshManager.RefreshCallback() {
            @Override
            public void onRefreshStart() {
                // Already showing progress
                Log.d("PlayerDataRefresh", "onRefreshStart called");
            }
            
            @Override
            public void onRefreshSuccess(int playerCount) {
                Log.d("PlayerDataRefresh", "onRefreshSuccess called with " + playerCount + " players");
                // Run on UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    dismissRefreshProgress();
                    buttonRefreshPlayerData.setEnabled(true);
                    
                    // Show success confirmation dialog
                    showRefreshSuccessDialog(playerCount);
                    
                    // Set result to notify MainActivity to reload data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("PLAYER_DATA_REFRESHED", true);
                    setResult(RESULT_OK, resultIntent);
                });
            }
            
            @Override
            public void onRefreshError(String errorMessage) {
                Log.e("PlayerDataRefresh", "onRefreshError called: " + errorMessage);
                // Run on UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    dismissRefreshProgress();
                    buttonRefreshPlayerData.setEnabled(true);
                    
                    // Show error confirmation dialog
                    showRefreshErrorDialog(errorMessage);
                });
            }
        });
    }
    
    /**
     * Show progress dialog during refresh.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void showRefreshProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Refreshing Player Data");
        builder.setMessage("Fetching latest data from ESPN...");
        builder.setCancelable(false);
        
        progressDialog = builder.create();
        progressDialog.show();
    }
    
    /**
     * Dismiss progress dialog.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void dismissRefreshProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    /**
     * Show success confirmation dialog after refresh.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void showRefreshSuccessDialog(int playerCount) {
        String message = "ESPN API Call: Successful\n\n" +
                "✓ Successfully connected to ESPN servers\n" +
                "✓ Retrieved player data from ESPN Fantasy Football\n" +
                "✓ Parsed and validated " + playerCount + " players\n" +
                "✓ Player data has been updated\n\n" +
                "Your player rankings and statistics are now current.";
        
        new AlertDialog.Builder(this)
                .setTitle("✓ Refresh Successful")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // User acknowledged success
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Show error confirmation dialog after refresh failure.
     * COMMENTED OUT - Disabled for future revisit
     */
    /*
    private void showRefreshErrorDialog(String errorMessage) {
        String message = "ESPN API Call: Failed\n\n" +
                "✗ Unable to refresh player data\n\n" +
                "Error Details:\n" + errorMessage + "\n\n" +
                "Your existing player data has not been changed. " +
                "Please check your internet connection and try again.";
        
        new AlertDialog.Builder(this)
                .setTitle("✗ Refresh Failed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // User acknowledged error
                    dialog.dismiss();
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    // User wants to retry
                    dialog.dismiss();
                    performRefresh();
                })
                .setCancelable(false)
                .show();
    }
    */
}
