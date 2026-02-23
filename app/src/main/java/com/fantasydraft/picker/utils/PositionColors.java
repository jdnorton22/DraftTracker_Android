package com.fantasydraft.picker.utils;

import android.graphics.Color;

/**
 * Utility class for position-based color coding following FFL standard colors.
 * Colors are bold and vibrant for better visual distinction.
 */
public class PositionColors {
    
    // Bold position colors
    private static final int COLOR_WR = Color.parseColor("#E91E63");  // Material Pink
    private static final int COLOR_QB = Color.parseColor("#FFD700");  // Gold
    private static final int COLOR_RB = Color.parseColor("#32CD32");  // Lime Green
    private static final int COLOR_TE = Color.parseColor("#FF4500");  // Orange Red
    private static final int COLOR_DST = Color.parseColor("#FF8C00"); // Dark Orange
    private static final int COLOR_K = Color.parseColor("#1E90FF");   // Dodger Blue
    private static final int COLOR_DEFAULT = Color.parseColor("#9E9E9E"); // Gray
    
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
            return Color.DKGRAY;
        }
        
        switch (position.toUpperCase()) {
            case "WR":
                return Color.parseColor("#C71585"); // Medium Violet Red
            case "QB":
                return Color.parseColor("#DAA520"); // Goldenrod
            case "RB":
                return Color.parseColor("#228B22"); // Forest Green
            case "TE":
                return Color.parseColor("#DC143C"); // Crimson
            case "DST":
            case "DEF":
                return Color.parseColor("#D2691E"); // Chocolate
            case "K":
                return Color.parseColor("#1874CD"); // Dodger Blue 3
            default:
                return Color.DKGRAY;
        }
    }
}
