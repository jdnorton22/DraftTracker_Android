package com.fantasydraft.picker.utils;

import android.content.Context;

import com.fantasydraft.picker.models.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading player data from JSON resources.
 */
public class PlayerDataLoader {

    /**
     * Load players from internal storage if available, otherwise from resource.
     * This allows refreshed data to take precedence over bundled data.
     * 
     * @param context The Android context
     * @return List of Player objects
     * @throws IOException if there's an error reading the file
     * @throws JSONException if there's an error parsing the JSON
     */
    public static List<Player> loadPlayers(Context context) throws IOException, JSONException {
        // Try loading from internal storage first (refreshed data)
        File updatedFile = new File(context.getFilesDir(), "players_updated.json");
        
        if (updatedFile.exists()) {
            // Load from updated file
            String jsonString = readInternalFile(context, "players_updated.json");
            return parsePlayersFromJson(jsonString);
        } else {
            // Fall back to bundled resource
            return loadPlayersFromResource(context, com.fantasydraft.picker.R.raw.players);
        }
    }

    /**
     * Load players from a raw resource JSON file.
     *
     * @param context The Android context
     * @param resourceId The resource ID of the JSON file (e.g., R.raw.players)
     * @return List of Player objects loaded from the JSON file
     * @throws IOException if there's an error reading the file
     * @throws JSONException if there's an error parsing the JSON
     */
    public static List<Player> loadPlayersFromResource(Context context, int resourceId) 
            throws IOException, JSONException {
        String jsonString = readResourceFile(context, resourceId);
        return parsePlayersFromJson(jsonString);
    }

    /**
     * Read the contents of a raw resource file as a string.
     *
     * @param context The Android context
     * @param resourceId The resource ID of the file
     * @return The file contents as a string
     * @throws IOException if there's an error reading the file
     */
    private static String readResourceFile(Context context, int resourceId) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            reader.close();
            inputStream.close();
        }

        return stringBuilder.toString();
    }

    /**
     * Read a file from internal storage.
     * 
     * @param context The Android context
     * @param filename The name of the file to read
     * @return The file contents as a string
     * @throws IOException if there's an error reading the file
     */
    private static String readInternalFile(Context context, String filename) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            reader.close();
        }
        
        return stringBuilder.toString();
    }

    /**
     * Parse a JSON string into a list of Player objects.
     *
     * @param jsonString The JSON string containing player data
     * @return List of Player objects
     * @throws JSONException if there's an error parsing the JSON
     */
    private static List<Player> parsePlayersFromJson(String jsonString) throws JSONException {
        List<Player> players = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject playerJson = jsonArray.getJSONObject(i);
            
            String id = playerJson.getString("id");
            String name = playerJson.getString("name");
            String position = playerJson.getString("position");
            int rank = playerJson.getInt("rank");

            Player player = new Player(id, name, position, rank);
            
            // Parse optional fields
            if (playerJson.has("lastYearStats")) {
                player.setLastYearStats(playerJson.getString("lastYearStats"));
            }
            
            if (playerJson.has("pffRank")) {
                player.setPffRank(playerJson.getInt("pffRank"));
            }
            
            if (playerJson.has("positionRank")) {
                player.setPositionRank(playerJson.getInt("positionRank"));
            }
            
            if (playerJson.has("nflTeam")) {
                player.setNflTeam(playerJson.getString("nflTeam"));
            }
            
            if (playerJson.has("injuryStatus")) {
                player.setInjuryStatus(playerJson.getString("injuryStatus"));
            }
            
            if (playerJson.has("espnId")) {
                player.setEspnId(playerJson.getString("espnId"));
            }
            
            if (playerJson.has("byeWeek")) {
                player.setByeWeek(playerJson.getInt("byeWeek"));
            }
            
            players.add(player);
        }

        return players;
    }
}
