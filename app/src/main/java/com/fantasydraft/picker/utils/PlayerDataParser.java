package com.fantasydraft.picker.utils;

import android.util.Log;

import com.fantasydraft.picker.models.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses player data from ESPN JSON format into Player objects.
 */
public class PlayerDataParser {
    
    private static final String TAG = "PlayerDataRefresh";
    
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
        
        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Parse ESPN JSON data into list of Player objects.
     * For now, this expects our simplified JSON format.
     * TODO: Implement full ESPN API response parsing when API is available.
     */
    public List<Player> parseESPNData(String jsonData) throws ParseException {
        Log.d(TAG, "Starting to parse player data");
        
        if (jsonData == null || jsonData.trim().isEmpty()) {
            Log.e(TAG, "JSON data is null or empty");
            throw new ParseException("JSON data is null or empty");
        }
        
        Log.d(TAG, "JSON data length: " + jsonData.length() + " characters");
        
        try {
            JSONArray playersArray = new JSONArray(jsonData);
            Log.d(TAG, "Found " + playersArray.length() + " players in JSON array");
            
            List<Player> players = new ArrayList<>();
            
            for (int i = 0; i < playersArray.length(); i++) {
                try {
                    JSONObject playerJson = playersArray.getJSONObject(i);
                    Player player = parsePlayerObject(playerJson);
                    
                    if (player != null) {
                        players.add(player);
                    }
                } catch (JSONException e) {
                    Log.w(TAG, "Skipping player at index " + i + " due to parsing error", e);
                    // Skip players with parsing errors
                    continue;
                }
            }
            
            Log.d(TAG, "Successfully parsed " + players.size() + " players");
            
            // Validate minimum player count (temporarily reduced for debugging)
            if (players.size() < 10) {
                Log.e(TAG, "Insufficient player data: only " + players.size() + " players");
                throw new ParseException("Insufficient player data: only " + players.size() + " players found (minimum 10 required)");
            }
            
            return players;
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON data", e);
            throw new ParseException("Failed to parse JSON data", e);
        }
    }
    
    /**
     * Parse a single player object from JSON.
     */
    private Player parsePlayerObject(JSONObject playerJson) throws JSONException {
        // Validate required fields
        if (!playerJson.has("id") || !playerJson.has("name") || 
            !playerJson.has("position") || !playerJson.has("rank")) {
            return null; // Skip players missing critical fields
        }
        
        Player player = new Player();
        
        // Required fields
        player.setId(playerJson.getString("id"));
        player.setName(playerJson.getString("name"));
        player.setPosition(playerJson.getString("position"));
        player.setRank(playerJson.getInt("rank"));
        
        // Optional fields with defaults
        player.setPffRank(playerJson.optInt("pffRank", 0));
        player.setPositionRank(playerJson.optInt("positionRank", 0));
        player.setNflTeam(playerJson.optString("nflTeam", ""));
        player.setLastYearStats(playerJson.optString("lastYearStats", ""));
        player.setInjuryStatus(playerJson.optString("injuryStatus", "HEALTHY"));
        player.setEspnId(playerJson.optString("espnId", ""));
        player.setByeWeek(playerJson.optInt("byeWeek", 0));
        
        // Draft status defaults
        player.setDrafted(false);
        player.setDraftedBy(null);
        
        // Parse favorite status (defaults to false if absent)
        player.setFavorite(playerJson.optBoolean("favorite", false));
        
        return player;
    }
    
    /**
     * Parse injury status from ESPN format.
     * ESPN uses codes like "ACTIVE", "OUT", "QUESTIONABLE", etc.
     */
    private String parseInjuryStatus(JSONObject playerJson) {
        String status = playerJson.optString("injuryStatus", "ACTIVE");
        
        // Map ESPN status codes to our format
        switch (status.toUpperCase()) {
            case "ACTIVE":
            case "HEALTHY":
                return "HEALTHY";
            case "OUT":
                return "OUT";
            case "DOUBTFUL":
                return "DOUBTFUL";
            case "QUESTIONABLE":
            case "PROBABLE":
                return "QUESTIONABLE";
            case "IR":
            case "INJURED_RESERVE":
                return "IR";
            default:
                return "HEALTHY";
        }
    }
    
    /**
     * Parse last year's statistics based on position.
     */
    private String parseLastYearStats(JSONObject playerJson, String position) {
        // This would parse ESPN's stats format
        // For now, return the stats string if present
        return playerJson.optString("lastYearStats", "");
    }
    
    /**
     * Format stats for QB position.
     */
    private String formatQBStats(int passYards, int passTD, int passINT) {
        return passYards + " YDS, " + passTD + " TD, " + passINT + " INT";
    }
    
    /**
     * Format stats for RB position.
     */
    private String formatRBStats(int rushYards, int rushTD, int receptions) {
        return rushYards + " YDS, " + rushTD + " TD, " + receptions + " REC";
    }
    
    /**
     * Format stats for WR/TE position.
     */
    private String formatWRTEStats(int recYards, int recTD, int receptions) {
        return recYards + " YDS, " + recTD + " TD, " + receptions + " REC";
    }
}
