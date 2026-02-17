package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
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
    private RecyclerView recyclerTeams;
    private Button buttonSaveConfig;
    private Button buttonRefreshPlayerData;
    
    // Adapter
    private TeamConfigAdapter teamAdapter;
    
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
        recyclerTeams = view.findViewById(R.id.recycler_teams);
        buttonSaveConfig = view.findViewById(R.id.button_save_config);
        buttonRefreshPlayerData = view.findViewById(R.id.button_refresh_player_data);
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
     * Set up the refresh player data button.
     */
    private void setupRefreshButton() {
        if (buttonRefreshPlayerData != null) {
            buttonRefreshPlayerData.setOnClickListener(v -> showRefreshConfirmationDialog());
        }
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
        
        // Update MainActivity's config
        mainActivity.setCurrentConfig(config);
        
        // Save state to persistence
        mainActivity.saveDraftState();
        
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
