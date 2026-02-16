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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_player_selection);

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
    }

    private void setupRecyclerView() {
        adapter = new PlayerSelectionAdapter(players, this::handlePlayerSelection);
        adapter.setHideDrafted(hideDrafted); // Apply default hide drafted setting
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

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Confirm Draft Pick")
            .setMessage("Draft " + player.getName() + " (" + player.getPosition() + ")?")
            .setPositiveButton("Draft", (dialog, which) -> {
                // Return selected player to listener
                if (listener != null) {
                    listener.onPlayerSelected(player);
                }
                dismiss();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    public void updatePlayers(List<Player> newPlayers) {
        this.players = newPlayers;
        if (adapter != null) {
            adapter.updatePlayers(newPlayers);
            updatePlayerCount();
        }
    }
}
