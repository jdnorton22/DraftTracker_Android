package com.fantasydraft.picker.utils;

/**
 * Parses player stat lines to extract key fantasy metrics.
 */
public class StatParser {

    /**
     * Parse total touchdowns from a stat line.
     * Handles: rush TDs, rec TDs, pass TDs
     * @return total TDs, or -1 if no TD data found
     */
    public static int parseTotalTDs(String stats) {
        if (stats == null || stats.isEmpty()) return -1;
        
        int total = 0;
        boolean found = false;
        
        // Split by comma to handle each stat segment
        String[] segments = stats.split(",");
        for (String segment : segments) {
            String trimmed = segment.trim();
            String upper = trimmed.toUpperCase();
            int tdIndex = upper.indexOf("TD");
            if (tdIndex > 0) {
                // Extract all numbers from the segment before "TD"
                String before = trimmed.substring(0, tdIndex);
                // Find the number - extract digits from the segment
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(before);
                int lastNum = -1;
                while (m.find()) {
                    lastNum = Integer.parseInt(m.group(1));
                }
                if (lastNum >= 0) {
                    total += lastNum;
                    found = true;
                }
            }
        }
        
        return found ? total : -1;
    }

    /**
     * Parse total yards from a stat line.
     * Handles: rush yds, rec yds, pass yds
     * @return total yards, or -1 if no yard data found
     */
    public static int parseTotalYards(String stats) {
        if (stats == null || stats.isEmpty()) return -1;
        
        int total = 0;
        boolean found = false;
        
        String[] segments = stats.split(",");
        for (String segment : segments) {
            String trimmed = segment.trim();
            String upper = trimmed.toUpperCase();
            int ydsIndex = upper.indexOf("YDS");
            if (ydsIndex > 0) {
                String before = trimmed.substring(0, ydsIndex);
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(before);
                int lastNum = -1;
                while (m.find()) {
                    lastNum = Integer.parseInt(m.group(1));
                }
                if (lastNum >= 0) {
                    total += lastNum;
                    found = true;
                }
            }
        }
        
        return found ? total : -1;
    }

    /**
     * Parse receptions from a stat line.
     * @return receptions, or -1 if not found
     */
    public static int parseReceptions(String stats) {
        if (stats == null || stats.isEmpty()) return -1;
        
        String[] segments = stats.split(",");
        for (String segment : segments) {
            String trimmed = segment.trim().toUpperCase();
            if (trimmed.contains("REC") && !trimmed.contains("REC YDS") && !trimmed.contains("REC TD")) {
                String numStr = trimmed.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    try {
                        return Integer.parseInt(numStr);
                    } catch (NumberFormatException e) {
                        // skip
                    }
                }
            }
        }
        
        return -1;
    }

    /**
     * Get a compact summary string: "14 TD, 1456 YDS"
     */
    public static String getCompactSummary(String stats) {
        if (stats == null || stats.isEmpty()) return null;
        
        int tds = parseTotalTDs(stats);
        int yards = parseTotalYards(stats);
        
        if (tds < 0 && yards < 0) return null;
        
        StringBuilder sb = new StringBuilder();
        if (tds >= 0) {
            sb.append(tds).append(" TD");
        }
        if (yards >= 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(yards).append(" YDS");
        }
        return sb.toString();
    }
}
