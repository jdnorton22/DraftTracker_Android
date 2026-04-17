package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.PlayerDataRefreshManager;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.utils.PlayerDataParser;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment containing the configuration UI.
 * Displays league settings, team configuration, and draft parameters.
 * Requirements: 3.1, 5.2
 */
public class ConfigFragment extends Fragment {
    
    private static final int MIN_TEAMS = 2;
    private static final int MAX_TEAMS = 20;
    private static final int MIN_ROUNDS = 1;
    private static final int MAX_ROUNDS = 20;
    
    // UI Components
    private TextInputEditText inputLeagueName;
    private NumberPicker numberPickerTeamCount;
    private NumberPicker numberPickerRounds;
    private Spinner spinnerDraftFlow;
    private CheckBox checkboxSkipFirstRound;
    private CheckBox checkboxStopwatchEnabled;
    private CheckBox checkboxSmsEnabled;
    private com.google.android.material.textfield.TextInputEditText inputSmsNewNumber;
    private android.widget.LinearLayout layoutSmsNumbersList;
    private java.util.ArrayList<String> smsNumbers = new java.util.ArrayList<>();
    private RecyclerView recyclerTeams;
    private Button buttonSaveConfig;
    private Button buttonImportPlayers;
    private Button buttonRefreshPlayerData;
    private TextView textPlayerDataAge;
    private TextView textDraftInProgressBanner;
    
    // Adapter
    private TeamConfigAdapter teamAdapter;
    
    // File picker request code
    private static final int PICK_JSON_FILE = 1;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        
        // Initialize all UI component references
        initializeViews(view);
        
        // Set up UI components
        setupNumberPicker();
        setupRoundsPicker();
        setupSpinner();
        setupRecyclerView();
        setupSaveButton();
        setupImportButton();
        setupRefreshButton();
        
        return view;
    }
    
    /**
     * Load current configuration and update control states when fragment is resumed.
     * Requirements: 3.8, 11.1, 11.2
     */
    @Override
    public void onResume() {
        super.onResume();
        
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Load current configuration into UI
        loadCurrentConfiguration();
        
        // Update control states based on draft progress
        updateControlStates();
    }
    
    /**
     * Preserve unsaved UI changes when fragment is paused.
     * Requirements: 3.8, 11.1
     */
    @Override
    public void onPause() {
        super.onPause();
        
        // UI changes are preserved automatically by the fragment
        // No explicit action needed as we're not clearing the views
    }
    
    /**
     * Load current configuration from MainActivity into UI components.
     * Requirements: 11.2
     */
    private void loadCurrentConfiguration() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Load draft config
        DraftConfig config = mainActivity.getCurrentConfig();
        if (config != null) {
            // Set league name
            if (inputLeagueName != null) {
                inputLeagueName.setText(config.getLeagueName());
            }
            
            // Set number of rounds
            if (numberPickerRounds != null) {
                numberPickerRounds.setValue(config.getNumberOfRounds());
            }
            
            // Set flow type
            if (spinnerDraftFlow != null) {
                int selection = config.getFlowType() == FlowType.SERPENTINE ? 0 : 1;
                spinnerDraftFlow.setSelection(selection);
            }
            
            // Set skip first round checkbox
            if (checkboxSkipFirstRound != null) {
                checkboxSkipFirstRound.setChecked(config.isSkipFirstRound());
            }
            
            // Set stopwatch enabled checkbox
            if (checkboxStopwatchEnabled != null) {
                checkboxStopwatchEnabled.setChecked(config.isStopwatchEnabled());
            }
        }
        
        // Load SMS settings from SharedPreferences
        if (getContext() != null) {
            android.content.SharedPreferences prefs = getContext().getSharedPreferences("FantasyDraftPrefs", 0);
            if (checkboxSmsEnabled != null) {
                checkboxSmsEnabled.setChecked(prefs.getBoolean("sms_enabled", false));
            }
            String numbersRaw = prefs.getString("sms_numbers", "");
            smsNumbers.clear();
            if (!numbersRaw.isEmpty()) {
                for (String line : numbersRaw.split("\\n")) {
                    String num = line.trim();
                    if (!num.isEmpty()) smsNumbers.add(num);
                }
            }
            refreshSmsNumbersList();
        }
        
        // Load teams
        List<Team> teams = mainActivity.getTeams();
        if (teams != null) {
            // Update team count picker
            if (numberPickerTeamCount != null) {
                numberPickerTeamCount.setValue(teams.size());
            }
            
            // Update team adapter
            if (teamAdapter != null) {
                teamAdapter.setTeams(teams);
            }
        }
    }
    
    /**
     * Initialize all UI view references.
     * Requirements: 3.1, 3.2, 3.3, 3.4
     */
    private void initializeViews(View view) {
        inputLeagueName = view.findViewById(R.id.input_league_name);
        numberPickerTeamCount = view.findViewById(R.id.number_picker_team_count);
        numberPickerRounds = view.findViewById(R.id.number_picker_rounds);
        spinnerDraftFlow = view.findViewById(R.id.spinner_draft_flow);
        checkboxSkipFirstRound = view.findViewById(R.id.checkbox_skip_first_round);
        checkboxStopwatchEnabled = view.findViewById(R.id.checkbox_stopwatch_enabled);
        checkboxSmsEnabled = view.findViewById(R.id.checkbox_sms_enabled);
        inputSmsNewNumber = view.findViewById(R.id.input_sms_new_number);
        layoutSmsNumbersList = view.findViewById(R.id.layout_sms_numbers_list);
        
        view.findViewById(R.id.button_add_sms_number).setOnClickListener(v -> {
            String num = inputSmsNewNumber.getText().toString().trim();
            if (!num.isEmpty()) {
                smsNumbers.add(num);
                inputSmsNewNumber.setText("");
                refreshSmsNumbersList();
            }
        });
        recyclerTeams = view.findViewById(R.id.recycler_teams);
        buttonSaveConfig = view.findViewById(R.id.button_save_config);
        buttonImportPlayers = view.findViewById(R.id.button_import_players);
        buttonRefreshPlayerData = view.findViewById(R.id.button_refresh_player_data);
        textPlayerDataAge = view.findViewById(R.id.text_player_data_age);
        textDraftInProgressBanner = view.findViewById(R.id.text_draft_in_progress_banner);
        
        // Position requirements container
        LinearLayout layoutPositionRequirements = view.findViewById(R.id.layout_position_requirements);
        if (layoutPositionRequirements != null) {
            populatePositionRequirements(layoutPositionRequirements);
        }
    }
    
    /**
     * Get MainActivity reference safely.
     * Returns null if the activity is not MainActivity or is not available.
     * Requirements: 5.2
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
     * Set up the number picker for team count.
     * Requirements: 3.2
     */
    private void setupNumberPicker() {
        numberPickerTeamCount.setMinValue(MIN_TEAMS);
        numberPickerTeamCount.setMaxValue(MAX_TEAMS);
        numberPickerTeamCount.setWrapSelectorWheel(false);
        
        // Get initial value from MainActivity
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            List<Team> teams = mainActivity.getTeams();
            if (teams != null) {
                numberPickerTeamCount.setValue(teams.size());
            }
        }
        
        // Handle team count changes
        numberPickerTeamCount.setOnValueChangedListener((picker, oldVal, newVal) -> {
            handleTeamCountChange(newVal);
        });
    }
    
    /**
     * Set up the number picker for rounds.
     * Requirements: 3.2
     */
    private void setupRoundsPicker() {
        numberPickerRounds.setMinValue(MIN_ROUNDS);
        numberPickerRounds.setMaxValue(MAX_ROUNDS);
        numberPickerRounds.setWrapSelectorWheel(false);
        
        // Get initial value from MainActivity
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            DraftConfig config = mainActivity.getCurrentConfig();
            if (config != null) {
                numberPickerRounds.setValue(config.getNumberOfRounds());
            }
        }
    }
    
    /**
     * Populate position requirements UI with controls for each position.
     * Requirements: Position roster requirements configuration
     */
    private void populatePositionRequirements(android.widget.LinearLayout container) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null || getContext() == null) {
            return;
        }
        
        DraftConfig config = mainActivity.getCurrentConfig();
        if (config == null) {
            return;
        }
        
        // Define positions in display order
        String[] positions = {"QB", "RB", "WR", "TE", "K", "DST"};
        
        // Clear existing views
        container.removeAllViews();
        
        // Create a view for each position
        for (String position : positions) {
            View itemView = LayoutInflater.from(getContext()).inflate(
                R.layout.item_position_requirement, container, false);
            
            // Get views
            TextView badgeText = itemView.findViewById(R.id.text_position_badge);
            TextView minValueText = itemView.findViewById(R.id.text_min_value);
            TextView maxValueText = itemView.findViewById(R.id.text_max_value);
            Button minDecreaseBtn = itemView.findViewById(R.id.button_min_decrease);
            Button minIncreaseBtn = itemView.findViewById(R.id.button_min_increase);
            Button maxDecreaseBtn = itemView.findViewById(R.id.button_max_decrease);
            Button maxIncreaseBtn = itemView.findViewById(R.id.button_max_increase);
            Button maxDisableBtn = itemView.findViewById(R.id.button_max_disable);
            
            // Set position badge
            badgeText.setText(position);
            int color = com.fantasydraft.picker.utils.PositionColors.getColorForPosition(position);
            badgeText.setBackgroundColor(color);
            
            // Get current requirements
            DraftConfig.PositionRequirement requirement = config.getPositionRequirement(position);
            if (requirement == null) {
                requirement = new DraftConfig.PositionRequirement(1, -1);
                config.setPositionRequirement(position, 1, -1);
            }
            
            // Set initial values
            minValueText.setText(String.valueOf(requirement.getMin()));
            updateMaxValueDisplay(maxValueText, requirement.getMax());
            
            // Set up min click listeners
            minDecreaseBtn.setOnClickListener(v -> {
                int currentMin = Integer.parseInt(minValueText.getText().toString());
                if (currentMin > 0) {
                    int newMin = currentMin - 1;
                    minValueText.setText(String.valueOf(newMin));
                    DraftConfig.PositionRequirement req = config.getPositionRequirement(position);
                    config.setPositionRequirement(position, newMin, req != null ? req.getMax() : -1);
                }
            });
            
            minIncreaseBtn.setOnClickListener(v -> {
                int currentMin = Integer.parseInt(minValueText.getText().toString());
                DraftConfig.PositionRequirement req = config.getPositionRequirement(position);
                int currentMax = req != null ? req.getMax() : -1;
                
                // Allow increase if max is disabled (-1) or if min < max
                if (currentMax == -1 || currentMin < currentMax) {
                    int newMin = currentMin + 1;
                    minValueText.setText(String.valueOf(newMin));
                    config.setPositionRequirement(position, newMin, currentMax);
                } else if (getContext() != null) {
                    Toast.makeText(getContext(), "Min cannot exceed Max", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Set up max click listeners
            maxDecreaseBtn.setOnClickListener(v -> {
                int currentMin = Integer.parseInt(minValueText.getText().toString());
                DraftConfig.PositionRequirement req = config.getPositionRequirement(position);
                int currentMax = req != null ? req.getMax() : -1;
                
                if (currentMax == -1) {
                    // If disabled, set to a reasonable starting value (min + 5)
                    int newMax = currentMin + 5;
                    updateMaxValueDisplay(maxValueText, newMax);
                    config.setPositionRequirement(position, currentMin, newMax);
                } else if (currentMax > currentMin) {
                    int newMax = currentMax - 1;
                    updateMaxValueDisplay(maxValueText, newMax);
                    config.setPositionRequirement(position, currentMin, newMax);
                } else if (getContext() != null) {
                    Toast.makeText(getContext(), "Max cannot be less than Min", Toast.LENGTH_SHORT).show();
                }
            });
            
            maxIncreaseBtn.setOnClickListener(v -> {
                int currentMin = Integer.parseInt(minValueText.getText().toString());
                DraftConfig.PositionRequirement req = config.getPositionRequirement(position);
                int currentMax = req != null ? req.getMax() : -1;
                
                if (currentMax == -1) {
                    // If disabled, set to a reasonable starting value (min + 5)
                    int newMax = currentMin + 5;
                    updateMaxValueDisplay(maxValueText, newMax);
                    config.setPositionRequirement(position, currentMin, newMax);
                } else {
                    int newMax = currentMax + 1;
                    updateMaxValueDisplay(maxValueText, newMax);
                    config.setPositionRequirement(position, currentMin, newMax);
                }
            });
            
            maxDisableBtn.setOnClickListener(v -> {
                int currentMin = Integer.parseInt(minValueText.getText().toString());
                updateMaxValueDisplay(maxValueText, -1);
                config.setPositionRequirement(position, currentMin, -1);
            });
            
            // Add to container
            container.addView(itemView);
        }
    }
    
    /**
     * Update the max value display text.
     * Shows "None" for -1 (no limit), otherwise shows the number.
     */
    private void updateMaxValueDisplay(TextView textView, int maxValue) {
        if (maxValue == -1) {
            textView.setText("None");
        } else {
            textView.setText(String.valueOf(maxValue));
        }
    }
    
    /**
     * Handle changes to team count.
     * Requirements: 3.2, 3.3
     */
    private void handleTeamCountChange(int newCount) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Validate team count
        if (newCount < MIN_TEAMS || newCount > MAX_TEAMS) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.error_invalid_team_count, 
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        List<Team> teams = mainActivity.getTeams();
        if (teams == null) {
            teams = new ArrayList<>();
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
        
        // Update MainActivity's teams
        mainActivity.setTeams(teams);
        
        // Update adapter
        if (teamAdapter != null) {
            teamAdapter.setTeams(teams);
        }
    }
    
    /**
     * Set up the spinner for draft flow selection.
     * Requirements: 3.2, 3.4
     */
    private void setupSpinner() {
        if (getContext() == null) {
            return;
        }
        
        // Create adapter with flow types
        String[] flowTypes = {
                getString(R.string.serpentine),
                getString(R.string.linear)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                flowTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDraftFlow.setAdapter(adapter);
        
        // Set current selection from MainActivity
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            DraftConfig config = mainActivity.getCurrentConfig();
            if (config != null) {
                int selection = config.getFlowType() == FlowType.SERPENTINE ? 0 : 1;
                spinnerDraftFlow.setSelection(selection);
            }
        }
    }
    
    /**
     * Set up the RecyclerView for team list.
     * Requirements: 3.3
     */
    private void setupRecyclerView() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null || getContext() == null) {
            return;
        }
        
        teamAdapter = new TeamConfigAdapter();
        
        List<Team> teams = mainActivity.getTeams();
        if (teams != null) {
            teamAdapter.setTeams(teams);
        }
        
        // Set up listener for team name changes
        teamAdapter.setTeamNameChangeListener((position, newName) -> {
            // Validation will happen on save
        });
        
        recyclerTeams.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTeams.setAdapter(teamAdapter);
    }
    
    /**
     * Set up the save button.
     * Requirements: 3.7
     */
    private void setupSaveButton() {
        if (buttonSaveConfig != null) {
            buttonSaveConfig.setOnClickListener(v -> saveConfiguration());
        }
    }
    
    /**
     * Set up the import players button.
     */
    private void setupImportButton() {
        if (buttonImportPlayers != null) {
            // Check feature flag
            if (com.fantasydraft.picker.utils.FeatureFlags.ENABLE_IMPORT_PLAYERS) {
                buttonImportPlayers.setVisibility(View.VISIBLE);
                buttonImportPlayers.setOnClickListener(v -> openFilePicker());
            } else {
                buttonImportPlayers.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Open file picker to select players.json file.
     */
    private void openFilePicker() {
        // Try ACTION_GET_CONTENT first (more compatible)
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        // Add extra MIME types
        String[] mimeTypes = {"application/json", "text/plain", "application/octet-stream"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        
        try {
            startActivityForResult(Intent.createChooser(intent, "Select players.json file"), PICK_JSON_FILE);
        } catch (android.content.ActivityNotFoundException e) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "No file picker app found. Please install a file manager.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_JSON_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    android.net.Uri uri = data.getData();
                    importPlayersFromUri(uri);
                } else {
                    Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     * Import players from the selected file URI.
     */
    private void importPlayersFromUri(android.net.Uri uri) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        try {
            // Read file content
            java.io.InputStream inputStream = mainActivity.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(mainActivity, "Failed to open file", Toast.LENGTH_SHORT).show();
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
            List<com.fantasydraft.picker.models.Player> players = null;
            
            try {
                players = parser.parseESPNData(jsonContent);
            } catch (PlayerDataParser.ParseException e) {
                // Show detailed error dialog
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Parse Error")
                        .setMessage("Failed to parse player data:\n\n" + e.getMessage() + 
                                "\n\nPlease ensure your JSON file:\n" +
                                "- Is a valid JSON array\n" +
                                "- Contains at least 10 players\n" +
                                "- Each player has: id, name, position, rank")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            
            if (players == null || players.isEmpty()) {
                Toast.makeText(mainActivity, "No valid player data found in file", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Save to internal storage
            savePlayersToInternalStorage(jsonContent);
            
            // Show success message
            String message = "Successfully imported " + players.size() + " players. " +
                    "Restart the app or reset the draft to use the new data.";
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Import Successful")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
            
        } catch (java.io.IOException e) {
            // Show detailed error dialog
            new AlertDialog.Builder(mainActivity)
                    .setTitle("File Read Error")
                    .setMessage("Failed to read file:\n\n" + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            // Show detailed error dialog
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Import Failed")
                    .setMessage("Unexpected error: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
    
    /**
     * Save players JSON to internal storage.
     */
    private void savePlayersToInternalStorage(String jsonContent) throws java.io.IOException {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Save as players_updated.json so the app will load it on next start
        java.io.File file = new java.io.File(mainActivity.getFilesDir(), "players_updated.json");
        java.io.FileWriter writer = new java.io.FileWriter(file);
        writer.write(jsonContent);
        writer.close();
    }
    
    /**
     * Set up the refresh player data button.
     */
    private void setupRefreshButton() {
        if (buttonRefreshPlayerData != null) {
            // Check feature flag
            if (com.fantasydraft.picker.utils.FeatureFlags.ENABLE_REFRESH_PLAYER_DATA) {
                buttonRefreshPlayerData.setVisibility(View.VISIBLE);
                buttonRefreshPlayerData.setOnClickListener(v -> showRefreshConfirmationDialog());
            } else {
                buttonRefreshPlayerData.setVisibility(View.GONE);
            }
        }
        updatePlayerDataAge();
    }

    private void updatePlayerDataAge() {
        if (textPlayerDataAge == null) return;
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            textPlayerDataAge.setText("Player data: bundled default");
            return;
        }

        java.io.File file = new java.io.File(mainActivity.getFilesDir(), "players_updated.json");
        if (!file.exists()) {
            textPlayerDataAge.setText("Player data: bundled default");
            return;
        }

        long lastModified = file.lastModified();
        long ageMs = System.currentTimeMillis() - lastModified;

        // Format the date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM d, yyyy h:mm a", java.util.Locale.getDefault());
        String dateStr = sdf.format(new java.util.Date(lastModified));

        // Format the age
        String ageStr;
        long minutes = ageMs / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            ageStr = days + (days == 1 ? " day" : " days") + " ago";
        } else if (hours > 0) {
            ageStr = hours + (hours == 1 ? " hour" : " hours") + " ago";
        } else {
            ageStr = minutes + (minutes == 1 ? " minute" : " minutes") + " ago";
        }

        textPlayerDataAge.setText("Last refresh: " + dateStr + " (" + ageStr + ")");
    }
    
    /**
     * Show confirmation dialog before refreshing player data.
     */
    private void showRefreshConfirmationDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        new AlertDialog.Builder(mainActivity)
            .setTitle("Refresh Player Data?")
            .setMessage("This will download the latest player data and reset your current draft. All picks will be cleared and cannot be recovered. Continue?")
            .setPositiveButton("Refresh", (dialog, which) -> refreshPlayerData())
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    /**
     * Refresh player data from GitHub.
     */
    private void refreshPlayerData() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Show progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(mainActivity);
        progressDialog.setTitle("Refreshing Player Data");
        progressDialog.setMessage("Downloading latest data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Create refresh manager
        PlayerDataRefreshManager refreshManager = new PlayerDataRefreshManager(
            mainActivity,
            mainActivity.getPlayerManager(),
            mainActivity.getDraftCoordinator(),
            mainActivity.getPersistenceManager(),
            mainActivity.getTeams(),
            mainActivity.getPickHistory()
        );
        
        // Execute refresh
        refreshManager.refreshPlayerData(new PlayerDataRefreshManager.RefreshCallback() {
            @Override
            public void onRefreshStart() {
                // Already showing progress dialog
            }
            
            @Override
            public void onRefreshSuccess(int playerCount) {
                progressDialog.dismiss();
                
                new AlertDialog.Builder(mainActivity)
                    .setTitle("Refresh Complete")
                    .setMessage("Successfully refreshed " + playerCount + " players. Draft has been reset.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Reload the UI to reflect changes
                        if (getActivity() != null) {
                            getActivity().recreate();
                        }
                    })
                    .setCancelable(false)
                    .show();
            }
            
            @Override
            public void onRefreshError(String errorMessage) {
                progressDialog.dismiss();
                
                new AlertDialog.Builder(mainActivity)
                    .setTitle("Refresh Failed")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }
    
    /**
     * Refresh the SMS numbers list UI from the smsNumbers array.
     */
    private void refreshSmsNumbersList() {
        layoutSmsNumbersList.removeAllViews();
        for (int i = 0; i < smsNumbers.size(); i++) {
            final int index = i;
            String number = smsNumbers.get(i);
            
            android.widget.LinearLayout row = new android.widget.LinearLayout(getContext());
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(0, 4, 0, 4);
            
            android.widget.TextView tv = new android.widget.TextView(getContext());
            tv.setText("📱 " + number);
            tv.setTextSize(14);
            android.widget.LinearLayout.LayoutParams tvParams = new android.widget.LinearLayout.LayoutParams(
                    0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            tv.setLayoutParams(tvParams);
            row.addView(tv);
            
            android.widget.TextView removeBtn = new android.widget.TextView(getContext());
            removeBtn.setText("✕");
            removeBtn.setTextSize(18);
            removeBtn.setTextColor(0xFFD32F2F);
            removeBtn.setPadding(24, 8, 24, 8);
            removeBtn.setOnClickListener(v -> {
                smsNumbers.remove(index);
                refreshSmsNumbersList();
            });
            row.addView(removeBtn);
            
            layoutSmsNumbersList.addView(row);
        }
    }
    
    /**
     * Validate and save configuration.
     * Requirements: 3.5, 3.6, 3.7
     */
    private void saveConfiguration() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null || teamAdapter == null) {
            return;
        }
        
        // Get current teams from adapter
        List<Team> teams = teamAdapter.getTeams();
        
        // Validate team names
        if (!validateTeamNames(teams)) {
            return;
        }
        
        // Validate draft order
        if (!validateDraftOrder(teams)) {
            return;
        }
        
        // Update MainActivity's teams
        mainActivity.setTeams(teams);
        
        // Get current config
        DraftConfig config = mainActivity.getCurrentConfig();
        if (config == null) {
            config = new DraftConfig(FlowType.SERPENTINE, MIN_ROUNDS);
        }
        
        // Update league name from input
        String leagueName = inputLeagueName.getText().toString().trim();
        if (!leagueName.isEmpty()) {
            config.setLeagueName(leagueName);
        }
        
        // Update draft config with selected flow type
        int flowSelection = spinnerDraftFlow.getSelectedItemPosition();
        FlowType flowType = flowSelection == 0 ? FlowType.SERPENTINE : FlowType.LINEAR;
        config.setFlowType(flowType);
        
        // Update number of rounds from picker
        int numberOfRounds = numberPickerRounds.getValue();
        config.setNumberOfRounds(numberOfRounds);
        
        // Update skip first round from checkbox
        boolean skipFirstRound = checkboxSkipFirstRound.isChecked();
        boolean wasSkipFirstRound = config.isSkipFirstRound();
        config.setSkipFirstRound(skipFirstRound);
        
        // Update stopwatch enabled from checkbox
        config.setStopwatchEnabled(checkboxStopwatchEnabled.isChecked());
        
        // Update MainActivity's config
        mainActivity.setCurrentConfig(config);
        
        // Save state to persistence
        mainActivity.saveDraftState();
        
        // Save SMS settings to SharedPreferences
        if (getContext() != null) {
            StringBuilder numbersStr = new StringBuilder();
            for (String num : smsNumbers) {
                if (numbersStr.length() > 0) numbersStr.append("\n");
                numbersStr.append(num);
            }
            getContext().getSharedPreferences("FantasyDraftPrefs", 0).edit()
                    .putBoolean("sms_enabled", checkboxSmsEnabled.isChecked())
                    .putString("sms_numbers", numbersStr.toString())
                    .apply();
        }
        
        // Show appropriate message based on whether keeper setting changed
        if (getContext() != null) {
            if (skipFirstRound != wasSkipFirstRound && skipFirstRound) {
                Toast.makeText(getContext(), "Keeper league enabled. Reset draft to apply changes.", 
                        Toast.LENGTH_LONG).show();
            } else if (skipFirstRound != wasSkipFirstRound && !skipFirstRound) {
                Toast.makeText(getContext(), "Keeper league disabled. Reset draft to apply changes.", 
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), R.string.config_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Validate that all team names are unique and non-empty.
     * Requirements: 3.5
     */
    private boolean validateTeamNames(List<Team> teams) {
        java.util.Set<String> nameSet = new java.util.HashSet<>();
        
        for (Team team : teams) {
            String name = team.getName();
            
            // Check for empty names
            if (name == null || name.trim().isEmpty()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.error_empty_team_name, 
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            
            // Check for duplicate names
            String trimmedName = name.trim();
            if (nameSet.contains(trimmedName)) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.error_duplicate_team_name, 
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            
            nameSet.add(trimmedName);
        }
        
        return true;
    }
    
    /**
     * Validate that draft order is complete (1 to N with no gaps).
     * Requirements: 3.6
     */
    private boolean validateDraftOrder(List<Team> teams) {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return false;
        }
        
        if (!mainActivity.getTeamManager().validateDraftOrder(teams)) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.error_incomplete_draft_order, 
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Update control states based on draft progress.
     * Disables structural config controls during active draft.
     * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5
     */
    private void updateControlStates() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            return;
        }
        
        // Check if draft has any picks
        List<com.fantasydraft.picker.models.Pick> pickHistory = mainActivity.getPickHistory();
        boolean hasPicks = pickHistory != null && !pickHistory.isEmpty();
        
        // Disable structural controls if draft has picks
        numberPickerTeamCount.setEnabled(!hasPicks);
        numberPickerRounds.setEnabled(!hasPicks);
        spinnerDraftFlow.setEnabled(!hasPicks);
        checkboxSkipFirstRound.setEnabled(!hasPicks);
        
        // Show/hide draft in progress banner
        if (textDraftInProgressBanner != null) {
            textDraftInProgressBanner.setVisibility(hasPicks ? View.VISIBLE : View.GONE);
        }
        
        // Team names and league name remain editable
        // (inputLeagueName and RecyclerView items are always enabled)
        
        // Show message if controls are disabled
        if (hasPicks && getContext() != null) {
            // Note: In a full implementation, this would show a TextView with the message
            // For now, we'll just ensure the controls are disabled
            // The message can be added to the layout and shown/hidden here
        }
    }
}
