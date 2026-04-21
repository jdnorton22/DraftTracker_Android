package com.fantasydraft.picker.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages configuration backup to a JSON file that persists across
 * app reinstalls. Saves to the shared Documents directory.
 * 
 * Backs up: league config, team names, position requirements, 
 * favorites, SMS settings, and other preferences.
 */
public class ConfigBackupManager {

    private static final String TAG = "ConfigBackupManager";
    private static final String BACKUP_FILENAME = "fantasy_draft_config.json";
    private static final String BACKUP_DIR = "FantasyDraftPicker";
    private static final int BACKUP_VERSION = 1;

    private final Context context;

    public ConfigBackupManager(Context context) {
        this.context = context;
    }

    /**
     * Save current configuration to a JSON backup file.
     */
    public boolean saveBackup(DraftConfig config, List<Team> teams, Set<String> favoritePlayerIds) {
        try {
            JSONObject backup = new JSONObject();
            backup.put("backupVersion", BACKUP_VERSION);
            backup.put("timestamp", System.currentTimeMillis());
            backup.put("appVersion", getAppVersion());

            // Draft config
            if (config != null) {
                JSONObject configJson = new JSONObject();
                configJson.put("leagueName", config.getLeagueName());
                configJson.put("numberOfRounds", config.getNumberOfRounds());
                configJson.put("flowType", config.getFlowType() != null ? config.getFlowType().name() : "SERPENTINE");
                configJson.put("skipFirstRound", config.isSkipFirstRound());
                configJson.put("stopwatchEnabled", config.isStopwatchEnabled());

                // Position requirements
                JSONObject posReqs = new JSONObject();
                Map<String, DraftConfig.PositionRequirement> requirements = config.getPositionRequirements();
                if (requirements != null) {
                    for (Map.Entry<String, DraftConfig.PositionRequirement> entry : requirements.entrySet()) {
                        JSONObject req = new JSONObject();
                        req.put("min", entry.getValue().getMin());
                        req.put("max", entry.getValue().getMax());
                        posReqs.put(entry.getKey(), req);
                    }
                }
                configJson.put("positionRequirements", posReqs);
                backup.put("config", configJson);
            }

            // Teams
            if (teams != null && !teams.isEmpty()) {
                JSONArray teamsJson = new JSONArray();
                for (Team team : teams) {
                    JSONObject teamJson = new JSONObject();
                    teamJson.put("id", team.getId());
                    teamJson.put("name", team.getName());
                    teamJson.put("draftPosition", team.getDraftPosition());
                    teamsJson.put(teamJson);
                }
                backup.put("teams", teamsJson);
            }

            // Favorites
            if (favoritePlayerIds != null && !favoritePlayerIds.isEmpty()) {
                JSONArray favs = new JSONArray();
                for (String id : favoritePlayerIds) {
                    favs.put(id);
                }
                backup.put("favorites", favs);
            }

            // SMS settings
            SharedPreferences prefs = context.getSharedPreferences("FantasyDraftPrefs", Context.MODE_PRIVATE);
            JSONObject smsJson = new JSONObject();
            smsJson.put("enabled", prefs.getBoolean("sms_enabled", false));
            smsJson.put("numbers", prefs.getString("sms_numbers", ""));
            backup.put("smsSettings", smsJson);

            // Write to file
            File backupFile = getBackupFile();
            if (backupFile == null) return false;

            // Ensure directory exists
            File dir = backupFile.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            FileWriter writer = new FileWriter(backupFile);
            writer.write(backup.toString(2));
            writer.close();

            Log.d(TAG, "Config backup saved to: " + backupFile.getAbsolutePath());
            return true;

        } catch (JSONException | IOException e) {
            Log.e(TAG, "Failed to save config backup", e);
            return false;
        }
    }

    /**
     * Load configuration from the JSON backup file.
     * Returns null if no backup exists or it's corrupted.
     */
    public BackupData loadBackup() {
        File backupFile = getBackupFile();
        if (backupFile == null || !backupFile.exists()) {
            Log.d(TAG, "No backup file found");
            return null;
        }

        try {
            // Read file
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(backupFile));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject backup = new JSONObject(sb.toString());
            BackupData data = new BackupData();

            // Parse config
            if (backup.has("config")) {
                JSONObject configJson = backup.getJSONObject("config");
                String flowTypeStr = configJson.optString("flowType", "SERPENTINE");
                FlowType flowType = FlowType.valueOf(flowTypeStr);
                int rounds = configJson.optInt("numberOfRounds", 15);
                String leagueName = configJson.optString("leagueName", "My League");

                DraftConfig config = new DraftConfig(flowType, rounds, leagueName);
                config.setSkipFirstRound(configJson.optBoolean("skipFirstRound", false));
                config.setStopwatchEnabled(configJson.optBoolean("stopwatchEnabled", false));

                // Position requirements
                if (configJson.has("positionRequirements")) {
                    JSONObject posReqs = configJson.getJSONObject("positionRequirements");
                    java.util.Iterator<String> keys = posReqs.keys();
                    while (keys.hasNext()) {
                        String pos = keys.next();
                        JSONObject req = posReqs.getJSONObject(pos);
                        config.setPositionRequirement(pos, req.getInt("min"), req.getInt("max"));
                    }
                }

                data.config = config;
            }

            // Parse teams
            if (backup.has("teams")) {
                JSONArray teamsJson = backup.getJSONArray("teams");
                data.teams = new ArrayList<>();
                for (int i = 0; i < teamsJson.length(); i++) {
                    JSONObject teamJson = teamsJson.getJSONObject(i);
                    Team team = new Team();
                    team.setId(teamJson.getString("id"));
                    team.setName(teamJson.getString("name"));
                    team.setDraftPosition(teamJson.getInt("draftPosition"));
                    team.setRoster(new ArrayList<>());
                    data.teams.add(team);
                }
            }

            // Parse favorites
            if (backup.has("favorites")) {
                JSONArray favs = backup.getJSONArray("favorites");
                data.favoritePlayerIds = new java.util.HashSet<>();
                for (int i = 0; i < favs.length(); i++) {
                    data.favoritePlayerIds.add(favs.getString(i));
                }
            }

            // Parse SMS settings
            if (backup.has("smsSettings")) {
                JSONObject smsJson = backup.getJSONObject("smsSettings");
                data.smsEnabled = smsJson.optBoolean("enabled", false);
                data.smsNumbers = smsJson.optString("numbers", "");
            }

            data.timestamp = backup.optLong("timestamp", 0);
            data.appVersion = backup.optString("appVersion", "unknown");

            Log.d(TAG, "Config backup loaded from: " + backupFile.getAbsolutePath());
            return data;

        } catch (JSONException | IOException e) {
            Log.e(TAG, "Failed to load config backup", e);
            return null;
        }
    }

    /**
     * Check if a backup file exists.
     */
    public boolean hasBackup() {
        File backupFile = getBackupFile();
        return backupFile != null && backupFile.exists();
    }

    /**
     * Get the backup file path. Uses Documents directory which persists across reinstalls
     * on devices with scoped storage, or falls back to external files dir.
     */
    private File getBackupFile() {
        // Try shared Documents directory first (survives uninstall)
        File docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File backupDir = new File(docsDir, BACKUP_DIR);
        return new File(backupDir, BACKUP_FILENAME);
    }

    private String getAppVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Data class holding all backed-up configuration.
     */
    public static class BackupData {
        public DraftConfig config;
        public List<Team> teams;
        public Set<String> favoritePlayerIds;
        public boolean smsEnabled;
        public String smsNumbers;
        public long timestamp;
        public String appVersion;
    }
}
