package com.fantasydraft.picker.managers;

import android.content.Context;
import android.util.Log;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftSnapshot;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.persistence.PersistenceException;
import com.fantasydraft.picker.persistence.PersistenceManager;
import com.fantasydraft.picker.utils.ESPNDataFetcher;
import com.fantasydraft.picker.utils.PlayerDataParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the player data refresh operation.
 * Coordinates fetching, parsing, writing, and draft reset.
 */
public class PlayerDataRefreshManager {
    
    private static final String TAG = "PlayerDataRefresh";
    
    private Context context;
    private PlayerManager playerManager;
    private DraftCoordinator draftCoordinator;
    private PersistenceManager persistenceManager;
    private List<Team> teams;
    private List<Pick> pickHistory;
    
    public interface RefreshCallback {
        void onRefreshStart();
        void onRefreshSuccess(int playerCount);
        void onRefreshError(String errorMessage);
    }
    
    public PlayerDataRefreshManager(Context context, PlayerManager playerManager, 
                                   DraftCoordinator draftCoordinator,
                                   PersistenceManager persistenceManager,
                                   List<Team> teams, List<Pick> pickHistory) {
        this.context = context;
        this.playerManager = playerManager;
        this.draftCoordinator = draftCoordinator;
        this.persistenceManager = persistenceManager;
        this.teams = teams;
        this.pickHistory = pickHistory;
    }
    
    /**
     * Check if a draft is currently in progress.
     */
    public boolean isDraftInProgress() {
        return pickHistory != null && !pickHistory.isEmpty();
    }
    
    /**
     * Refresh player data from ESPN.
     */
    public void refreshPlayerData(final RefreshCallback callback) {
        Log.d(TAG, "Refresh operation started");
        callback.onRefreshStart();
        
        // Create fetcher and parser
        ESPNDataFetcher fetcher = new ESPNDataFetcher(context);
        final PlayerDataParser parser = new PlayerDataParser();
        
        // Fetch data from ESPN
        fetcher.fetchPlayerData(new ESPNDataFetcher.FetchCallback() {
            @Override
            public void onFetchSuccess(String jsonData) {
                Log.d(TAG, "Fetch successful, starting parse");
                try {
                    // Parse the data
                    List<Player> players = parser.parseESPNData(jsonData);
                    Log.d(TAG, "Parse successful, " + players.size() + " players");
                    
                    // Write to file
                    Log.d(TAG, "Writing players to file");
                    writePlayersToFile(players);
                    Log.d(TAG, "File write successful");
                    
                    // Reset draft state
                    Log.d(TAG, "Resetting draft state");
                    resetDraftState(players);
                    Log.d(TAG, "Draft state reset successful");
                    
                    // Success
                    Log.d(TAG, "Refresh completed successfully");
                    callback.onRefreshSuccess(players.size());
                    
                } catch (PlayerDataParser.ParseException e) {
                    Log.e(TAG, "Parse error", e);
                    callback.onRefreshError("Invalid data format: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "File write error", e);
                    callback.onRefreshError("Unable to save player data: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error", e);
                    callback.onRefreshError("Refresh failed: " + e.getMessage());
                }
            }
            
            @Override
            public void onFetchError(ESPNDataFetcher.FetchError error, String message) {
                Log.e(TAG, "Fetch error: " + error + " - " + message);
                String errorMessage;
                
                switch (error) {
                    case NO_NETWORK:
                        errorMessage = "No internet connection. Please check your network and try again.";
                        break;
                    case SERVER_UNREACHABLE:
                        errorMessage = "Unable to reach server. Please try again later.";
                        break;
                    case TIMEOUT:
                        errorMessage = "Request timed out. Please try again.";
                        break;
                    case INVALID_RESPONSE:
                        errorMessage = "Invalid response from server. Please try again later.";
                        break;
                    default:
                        errorMessage = "Refresh failed: " + message;
                        break;
                }
                
                callback.onRefreshError(errorMessage);
            }
        });
    }
    
    /**
     * Write players to internal storage file.
     */
    private void writePlayersToFile(List<Player> players) throws IOException {
        try {
            // Create JSON array
            JSONArray jsonArray = new JSONArray();
            
            for (Player player : players) {
                JSONObject playerJson = new JSONObject();
                playerJson.put("id", player.getId());
                playerJson.put("name", player.getName());
                playerJson.put("position", player.getPosition());
                playerJson.put("rank", player.getRank());
                playerJson.put("pffRank", player.getPffRank());
                playerJson.put("positionRank", player.getPositionRank());
                playerJson.put("nflTeam", player.getNflTeam());
                playerJson.put("lastYearStats", player.getLastYearStats());
                playerJson.put("injuryStatus", player.getInjuryStatus());
                playerJson.put("espnId", player.getEspnId());
                
                jsonArray.put(playerJson);
            }
            
            // Write to internal storage
            String filename = "players_updated.json";
            File file = new File(context.getFilesDir(), filename);
            
            // Backup existing file if it exists
            if (file.exists()) {
                File backup = new File(context.getFilesDir(), "players_backup.json");
                if (backup.exists()) {
                    backup.delete();
                }
                file.renameTo(backup);
            }
            
            // Write new file
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString(2).getBytes());
            fos.close();
            
        } catch (Exception e) {
            throw new IOException("Failed to write player data", e);
        }
    }
    
    /**
     * Reset draft state after refresh.
     */
    private void resetDraftState(List<Player> newPlayers) {
        try {
            // Clear player manager and add new players
            playerManager.clearPlayers();
            for (Player player : newPlayers) {
                playerManager.addPlayer(player);
            }
            
            // Clear pick history
            if (pickHistory != null) {
                pickHistory.clear();
            }
            
            // Clear team rosters
            if (teams != null) {
                for (Team team : teams) {
                    if (team.getRoster() != null) {
                        team.getRoster().clear();
                    }
                }
            }
            
            // Get current config or use default
            DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 15);
            
            // Reset the draft using the coordinator (this will reset state to Round 1, Pick 1)
            DraftState newState = draftCoordinator.resetDraft(config);
            
            // Save clean state to persistence
            DraftSnapshot snapshot = new DraftSnapshot();
            snapshot.setTeams(teams != null ? teams : new ArrayList<Team>());
            snapshot.setPlayers(newPlayers);
            snapshot.setDraftState(newState);
            snapshot.setDraftConfig(config);
            snapshot.setPickHistory(new ArrayList<Pick>());
            snapshot.setTimestamp(System.currentTimeMillis());
            
            persistenceManager.saveDraft(snapshot);
            
        } catch (PersistenceException e) {
            // Log error but don't fail the refresh
            e.printStackTrace();
        }
    }
}
