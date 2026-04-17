package com.fantasydraft.picker.ui;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.DraftCoordinator;
import com.fantasydraft.picker.managers.DraftManager;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.managers.TeamManager;
import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftSnapshot;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.persistence.PersistenceException;
import com.fantasydraft.picker.persistence.PersistenceManager;
import com.fantasydraft.picker.utils.PlayerDataLoader;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity hosting fragments with drawer navigation.
 * Requirements: 4.1, 4.2, 4.5, 5.1, 5.2, 5.5, 5.6, 8.2, 10.1, 10.2, 10.3
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    public static final int REQUEST_CODE_HISTORY = 1002;
    
    // Managers
    private DraftManager draftManager;
    private PlayerManager playerManager;
    private TeamManager teamManager;
    private DraftCoordinator draftCoordinator;
    private PersistenceManager persistenceManager;
    
    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private androidx.fragment.app.FragmentContainerView fragmentContainer;
    
    // State
    private DraftState currentState;
    private DraftConfig currentConfig;
    private List<Team> teams;
    private List<Pick> pickHistory;
    private boolean persistenceEnabled = true; // Track if persistence is working
    
    // Getter methods for fragments to access managers and state
    // Requirements: 5.1, 5.2, 5.5
    
    public DraftManager getDraftManager() {
        return draftManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public TeamManager getTeamManager() {
        return teamManager;
    }
    
    public DraftCoordinator getDraftCoordinator() {
        return draftCoordinator;
    }
    
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }
    
    public DraftState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(DraftState state) {
        this.currentState = state;
    }
    
    public DraftConfig getCurrentConfig() {
        return currentConfig;
    }
    
    public void setCurrentConfig(DraftConfig config) {
        this.currentConfig = config;
    }
    
    public List<Team> getTeams() {
        return teams;
    }
    
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    public List<Pick> getPickHistory() {
        return pickHistory;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        fragmentContainer = findViewById(R.id.fragment_container);
        
        // Set up navigation drawer
        setupNavigationDrawer();
        
        // Initialize managers and load draft state
        initializeManagers();
        loadDraftState();
        
        // Display default fragment (DraftFragment) if no saved state
        if (savedInstanceState == null) {
            showFragment(new DraftFragment());
            navigationView.setCheckedItem(R.id.navigation_draft);
        }
    }
    
    /**
     * Initialize managers and load player data.
     */
    private void initializeManagers() {
        // Initialize persistence manager
        persistenceManager = new PersistenceManager(this);
        
        // Initialize player manager and load player data
        playerManager = new PlayerManager();
        loadPlayerData();
        
        // Initialize team manager
        teamManager = new TeamManager();
        
        // Initialize draft manager
        draftManager = new DraftManager();
        
        // Initialize coordinator
        draftCoordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
        
        // Initialize state
        teams = new ArrayList<>();
        pickHistory = new ArrayList<>();
    }
    
    /**
     * Load player data from JSON resource file.
     * Checks internal storage first for refreshed data, then falls back to bundled resource.
     */
    private void loadPlayerData() {
        try {
            // Load from internal storage if available (refreshed data), 
            // otherwise from bundled resource
            List<Player> players = PlayerDataLoader.loadPlayers(this);
            for (Player player : players) {
                playerManager.addPlayer(player);
            }
            
            // Restore favorites from SharedPreferences (survives player refresh)
            java.util.Set<String> favoriteKeys = getSharedPreferences("FantasyDraftPrefs", MODE_PRIVATE)
                    .getStringSet("favorite_players", null);
            if (favoriteKeys != null) {
                for (Player player : playerManager.getPlayers()) {
                    if (favoriteKeys.contains(player.getName() + "|" + player.getPosition())) {
                        player.setFavorite(true);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading player data: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Load saved draft state or create default state.
     * Requirements: 6.3
     */
    private void loadDraftState() {
        try {
            DraftSnapshot snapshot = persistenceManager.loadDraft();
            
            if (snapshot != null) {
                // Restore saved state
                teams = snapshot.getTeams();
                currentState = snapshot.getDraftState();
                currentConfig = snapshot.getDraftConfig();
                pickHistory = snapshot.getPickHistory();
                
                // IMPORTANT: Reset ALL players to undrafted first
                // This ensures players from other drafts don't show as drafted
                List<Player> allPlayers = playerManager.getPlayers();
                for (Player player : allPlayers) {
                    player.setDrafted(false);
                    player.setDraftedBy(null);
                }
                
                // Now restore player draft status from THIS draft only
                List<Player> savedPlayers = snapshot.getPlayers();
                if (savedPlayers != null) {
                    for (Player savedPlayer : savedPlayers) {
                        Player player = playerManager.getPlayerById(savedPlayer.getId());
                        if (player != null) {
                            player.setDrafted(savedPlayer.isDrafted());
                            player.setDraftedBy(savedPlayer.getDraftedBy());
                        }
                    }
                }
                
                // Clear and rebuild draft manager with pick history
                draftManager = new DraftManager();
                if (pickHistory != null) {
                    for (Pick pick : pickHistory) {
                        draftManager.addPickToHistory(pick);
                    }
                }
                
                // Update coordinator with rebuilt draft manager
                draftCoordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
            } else {
                // Create default state
                createDefaultState();
            }
        } catch (PersistenceException e) {
            handlePersistenceError(e, true);
            createDefaultState();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_load_failed, 
                    Toast.LENGTH_LONG).show();
            createDefaultState();
        }
    }
    
    /**
     * Create default draft state for new drafts.
     */
    private void createDefaultState() {
        // Create default configuration
        currentConfig = new DraftConfig(FlowType.SERPENTINE, 15);
        
        // Always start at round 1, pick 1
        currentState = new DraftState(1, 1, false);
        
        // Create default teams
        teams = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Team team = new Team();
            team.setId("team_" + i);
            team.setName("Team " + i);
            team.setDraftPosition(i);
            team.setRoster(new ArrayList<>());
            teams.add(team);
        }
        
        pickHistory = new ArrayList<>();
    }
    
    /**
     * Set up navigation drawer with hamburger menu.
     * Requirements: 4.1, 4.2, 4.5
     */
    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        
        // Force green background with white text/icons for nav drawer
        navigationView.setBackgroundColor(android.graphics.Color.parseColor("#2E7D32"));
        navigationView.setItemTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        navigationView.setItemIconTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        
        // Set up hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, 
                R.string.navigation_drawer_open, 
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.navigation_draft) {
            showFragment(new DraftFragment());
        } else if (itemId == R.id.navigation_favorites) {
            showFragment(new FavoritesFragment());
        } else if (itemId == R.id.navigation_config) {
            showFragment(new ConfigFragment());
        } else if (itemId == R.id.navigation_tutorial) {
            DraftWalkthrough.reset(this);
            showFragment(new DraftFragment());
            navigationView.setCheckedItem(R.id.navigation_draft);
        } else if (itemId == R.id.navigation_refresh_players) {
            showRefreshPlayersConfirmation();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a fragment in the fragment container.
     * Requirements: 4.1, 4.2
     */
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    
    /**
     * Save current draft state to persistence.
     * Requirements: 5.6, 8.2
     */
    public void saveDraftState() {
        if (!persistenceEnabled) {
            // Persistence is disabled, skip saving
            return;
        }
        
        try {
            DraftSnapshot snapshot = new DraftSnapshot();
            snapshot.setTeams(teams);
            snapshot.setPlayers(playerManager.getPlayers());
            snapshot.setDraftState(currentState);
            snapshot.setDraftConfig(currentConfig);
            snapshot.setPickHistory(pickHistory);
            snapshot.setTimestamp(System.currentTimeMillis());
            
            persistenceManager.saveDraft(snapshot);
            
            // Also persist favorites to SharedPreferences (survives player refresh)
            java.util.Set<String> favoriteKeys = new java.util.HashSet<>();
            for (Player p : playerManager.getPlayers()) {
                if (p.isFavorite()) {
                    favoriteKeys.add(p.getName() + "|" + p.getPosition());
                }
            }
            getSharedPreferences("FantasyDraftPrefs", MODE_PRIVATE).edit()
                    .putStringSet("favorite_players", favoriteKeys).apply();
        } catch (PersistenceException e) {
            handlePersistenceError(e, false);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_save_failed, 
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Handle persistence errors with user-friendly messages and recovery options.
     */
    private void handlePersistenceError(PersistenceException e, boolean isLoad) {
        String message;
        
        switch (e.getErrorType()) {
            case STORAGE_FULL:
                message = getString(R.string.error_storage_full);
                break;
            case CORRUPTED_DATA:
                message = getString(R.string.error_corrupted_data);
                break;
            case DATABASE_ERROR:
            case UNKNOWN:
            default:
                message = isLoad ? getString(R.string.error_load_failed) 
                                 : getString(R.string.error_save_failed);
                break;
        }
        
        // Show error dialog with options
        new AlertDialog.Builder(this)
                .setTitle(R.string.persistence_disabled_title)
                .setMessage(message + "\n\n" + getString(R.string.persistence_disabled_message))
                .setPositiveButton(R.string.continue_without_saving, (dialog, which) -> {
                    // Disable persistence and continue
                    persistenceEnabled = false;
                    Toast.makeText(this, "Continuing without saving", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.reset_and_retry, (dialog, which) -> {
                    // Clear corrupted data and reset
                    try {
                        persistenceManager.clearDraft();
                        createDefaultState();
                        Toast.makeText(this, "Draft reset successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(this, "Failed to reset draft", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Show confirmation dialog before refreshing player data from the nav menu.
     */
    private void showRefreshPlayersConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Refresh Player Data?")
            .setMessage("This will download the latest player data and reset your current draft. All picks will be cleared and cannot be recovered. Continue?")
            .setPositiveButton("Refresh", (dialog, which) -> refreshPlayerDataFromMenu())
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    /**
     * Refresh player data (same logic as ConfigFragment).
     */
    private void refreshPlayerDataFromMenu() {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setTitle("Refreshing Player Data");
        progressDialog.setMessage("Downloading latest data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        com.fantasydraft.picker.managers.PlayerDataRefreshManager refreshManager = 
            new com.fantasydraft.picker.managers.PlayerDataRefreshManager(
                this, playerManager, draftCoordinator, persistenceManager, teams, pickHistory);
        
        refreshManager.refreshPlayerData(new com.fantasydraft.picker.managers.PlayerDataRefreshManager.RefreshCallback() {
            @Override
            public void onRefreshStart() {}
            
            @Override
            public void onRefreshSuccess(int playerCount) {
                progressDialog.dismiss();
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Refresh Complete")
                    .setMessage("Successfully refreshed " + playerCount + " players. Draft has been reset.")
                    .setPositiveButton("OK", (d, w) -> recreate())
                    .setCancelable(false)
                    .show();
            }
            
            @Override
            public void onRefreshError(String errorMessage) {
                progressDialog.dismiss();
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Refresh Failed")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Save state when activity is paused
        // Requirements: 5.6, 8.2, 10.3
        saveDraftState();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Handle results from DraftHistoryActivity
        // Requirements: 8.2
        if (requestCode == REQUEST_CODE_HISTORY && resultCode == RESULT_OK && data != null) {
            // Handle undo from DraftHistoryActivity
            Pick pickToUndo = data.getParcelableExtra(DraftHistoryActivity.RESULT_UNDO_PICK);
            int position = data.getIntExtra(DraftHistoryActivity.RESULT_UNDO_POSITION, -1);
            
            if (pickToUndo != null && position >= 0) {
                undoPick(pickToUndo);
                
                // Force UI refresh after undo completes
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof DraftFragment) {
                    // Post to ensure this runs after onResume
                    currentFragment.getView().post(() -> {
                        ((DraftFragment) currentFragment).updateUI();
                    });
                }
            }
        }
    }
    
    /**
     * Undo a specific pick from the draft history.
     * Made public so DraftFragment can call it directly for the undo button.
     */
    public void undoPick(Pick pickToUndo) {
        try {
            // Find the actual position of the pick in the pick history
            // (position parameter may be incorrect if history was filtered/sorted)
            int actualPosition = -1;
            for (int i = 0; i < pickHistory.size(); i++) {
                Pick pick = pickHistory.get(i);
                if (pick.getPickNumber() == pickToUndo.getPickNumber() &&
                    pick.getPlayerId().equals(pickToUndo.getPlayerId())) {
                    actualPosition = i;
                    break;
                }
            }
            
            if (actualPosition == -1) {
                Toast.makeText(this, "Error: Pick not found in history", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Remove the pick from history
            Pick removedPick = pickHistory.remove(actualPosition);
            
            // Un-draft the player
            Player player = playerManager.getPlayerById(pickToUndo.getPlayerId());
            if (player != null) {
                player.setDrafted(false);
                player.setDraftedBy(null);
                
                // Remove player from team roster
                Team team = null;
                for (Team t : teams) {
                    if (t.getId().equals(pickToUndo.getTeamId())) {
                        team = t;
                        break;
                    }
                }
                
                if (team != null) {
                    team.getRoster().remove(player);
                }
            }
            
            // Recalculate draft state based on remaining picks
            if (pickHistory.isEmpty()) {
                // No picks left, reset to start (always round 1, pick 1)
                currentState = new DraftState(1, 1, false);
            } else {
                // Set state to the last pick + 1
                Pick lastPick = pickHistory.get(pickHistory.size() - 1);
                int nextRound = lastPick.getRound();
                int nextPick = lastPick.getPickInRound() + 1;
                
                if (nextPick > teams.size()) {
                    nextRound++;
                    nextPick = 1;
                }
                
                boolean isDraftComplete = nextRound > currentConfig.getNumberOfRounds();
                currentState = new DraftState(nextRound, nextPick, isDraftComplete);
            }
            
            // Clear draft manager history and rebuild
            draftManager = new DraftManager();
            for (Pick pick : pickHistory) {
                draftManager.addPickToHistory(pick);
            }
            
            // Update coordinator with new draft manager
            draftCoordinator = new DraftCoordinator(draftManager, playerManager, teamManager);
            
            // Save state
            saveDraftState();
            
            Toast.makeText(this, "Pick undone successfully", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error undoing pick: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Show dialog to draft a custom/unlisted player.
     * Called from PlayerSelectionDialog.
     */
    public void showCustomPlayerDialog() {
        // Delegate to DraftFragment if it's currently visible
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof DraftFragment) {
            ((DraftFragment) currentFragment).showCustomPlayerDialog();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Exit app and save draft state
            // Requirements: 10.1, 10.2, 10.3
            saveDraftState();
            super.onBackPressed();
        }
    }
}
