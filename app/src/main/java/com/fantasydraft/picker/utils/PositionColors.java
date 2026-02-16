package com.fantasydraft.picker.utils;

import android.graphics.Color;

/**
 * Utility class for position-based color coding following FFL standard colors.
 * Colors are subdued/pastel versions for better readability.
 */
public class PositionColors {
    
    // Subdued position colors (lighter/pastel versions)
    private static final int COLOR_WR = Color.parseColor("#FFE0F0");  // Light Pink
    private static final int COLOR_QB = Color.parseColor("#FFF9E0");  // Light Yellow
    private static final int COLOR_RB = Color.parseColor("#E0F5E0");  // Light Green
    private static final int COLOR_TE = Color.parseColor("#FFE5E5");  // Light Red
    private static final int COLOR_DST = Color.parseColor("#FFE8D5"); // Light Orange
    private static final int COLOR_K = Color.parseColor("#E0EFFF");   // Light Blue
    private static final int COLOR_DEFAULT = Color.parseColor("#F5F5F5"); // Light Gray
    
    /**
     * Get the background color for a given position.
     * 
     * @param position The player position (WR, QB, RB, TE, DST, K)
     * @return The color int for the position
     */
    public static int getColorForPosition(String position) {
        if (position == null) {
            return COLOR_DEFAULT;
        }
        
        switch (position.toUpperCase()) {
            case "WR":
                return COLOR_WR;
            case "QB":
                return COLOR_QB;
            case "RB":
                return COLOR_RB;
            case "TE":
                return COLOR_TE;
            case "DST":
            case "DEF":
                return COLOR_DST;
            case "K":
                return COLOR_K;
            default:
                return COLOR_DEFAULT;
        }
    }
    
    /**
     * Get a darker version of the position color for text/borders.
     * 
     * @param position The player position
     * @return The darker color int for the position
     */
    public static int getDarkColorForPosition(String position) {
        if (position == null) {
            return Color.GRAY;
        }
        
        switch (position.toUpperCase()) {
            case "WR":
                return Color.parseColor("#FF69B4"); // Hot Pink
            case "QB":
                return Color.parseColor("#FFD700"); // Gold
            case "RB":
                return Color.parseColor("#32CD32"); // Lime Green
            case "TE":
                return Color.parseColor("#DC143C"); // Crimson
            case "DST":
            case "DEF":
                return Color.parseColor("#FF8C00"); // Dark Orange
            case "K":
                return Color.parseColor("#4169E1"); // Royal Blue
            default:
                return Color.GRAY;
        }
    }
}
