package com.fantasydraft.picker.utils;

import android.content.Context;
import android.os.Environment;

import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for exporting draft history to CSV format.
 */
public class DraftCsvExporter {
    
    private static final String CSV_HEADER = "Pick Number,Round,Pick in Round,Team Name,Player Name,Position,NFL Team,Overall Rank,ADP Rank,Position Rank,Last Year Stats,Injury Status,Timestamp";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    
    /**
     * Export draft history to a CSV file in the Downloads directory.
     * 
     * @param context Application context
     * @param picks List of picks in draft order
     * @param teams List of teams
     * @param players List of players
     * @param leagueName Name of the league for the filename
     * @return File object pointing to the created CSV file
     * @throws IOException if file writing fails
     */
    public static File exportToCSV(Context context, List<Pick> picks, List<Team> teams, 
                                   List<Player> players, String leagueName) throws IOException {
        
        // Create maps for quick lookup
        Map<String, Team> teamMap = new HashMap<>();
        for (Team team : teams) {
            teamMap.put(team.getId(), team);
        }
        
        Map<String, Player> playerMap = new HashMap<>();
        for (Player player : players) {
            playerMap.put(player.getId(), player);
        }
        
        // Generate filename with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String sanitizedLeagueName = leagueName.replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "DraftHistory_" + sanitizedLeagueName + "_" + timestamp + ".csv";
        
        // Use app's external files directory (no permission needed on Android 10+)
        File exportDir = new File(context.getExternalFilesDir(null), "DraftExports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File csvFile = new File(exportDir, filename);
        
        // Write CSV content
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write header
            writer.append(CSV_HEADER);
            writer.append('\n');
            
            // Write picks in order
            for (Pick pick : picks) {
                Team team = teamMap.get(pick.getTeamId());
                Player player = playerMap.get(pick.getPlayerId());
                
                if (team != null && player != null) {
                    writer.append(String.valueOf(pick.getPickNumber()));
                    writer.append(',');
                    writer.append(String.valueOf(pick.getRound()));
                    writer.append(',');
                    writer.append(String.valueOf(pick.getPickInRound()));
                    writer.append(',');
                    writer.append(escapeCsvValue(team.getName()));
                    writer.append(',');
                    writer.append(escapeCsvValue(player.getName()));
                    writer.append(',');
                    writer.append(escapeCsvValue(player.getPosition()));
                    writer.append(',');
                    writer.append(escapeCsvValue(player.getNflTeam()));
                    writer.append(',');
                    writer.append(String.valueOf(player.getRank()));
                    writer.append(',');
                    writer.append(String.valueOf(player.getPffRank()));
                    writer.append(',');
                    writer.append(String.valueOf(player.getPositionRank()));
                    writer.append(',');
                    writer.append(escapeCsvValue(player.getLastYearStats()));
                    writer.append(',');
                    writer.append(escapeCsvValue(player.getInjuryStatus()));
                    writer.append(',');
                    writer.append(DATE_FORMAT.format(new Date(pick.getTimestamp())));
                    writer.append('\n');
                }
            }
            
            writer.flush();
        }
        
        return csvFile;
    }
    
    /**
     * Escape CSV values that contain commas, quotes, or newlines.
     * 
     * @param value The value to escape
     * @return Escaped value suitable for CSV
     */
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        // If value contains comma, quote, or newline, wrap in quotes and escape internal quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}
