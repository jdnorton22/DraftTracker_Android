package com.fantasydraft.picker.utils;

import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;

/**
 * Utility class for calculating draft pick value based on ADP vs actual pick position.
 * Positive value = drafted later than ADP (good value - got player later than expected)
 * Negative value = drafted earlier than ADP (reached - took player earlier than expected)
 */
public class PickValueCalculator {
    
    // Value tier thresholds
    public static final int GREAT_VALUE_THRESHOLD = 20;
    public static final int GOOD_VALUE_THRESHOLD = 8;
    public static final int FAIR_VALUE_THRESHOLD = 8;
    public static final int SLIGHT_REACH_THRESHOLD = -9;
    public static final int BIG_REACH_THRESHOLD = -20;
    
    /**
     * Calculate the value score for a pick.
     * @param pick The draft pick
     * @param player The player drafted
     * @return Value score (Pick Number - ADP), or 0 if ADP not available
     */
    public static int calculateValueScore(Pick pick, Player player) {
        if (player == null || player.getPffRank() <= 0) {
            return 0;
        }
        return pick.getPickNumber() - player.getPffRank();
    }
    
    // Elite player threshold (top N at their position)
    public static final int ELITE_POSITION_RANK_THRESHOLD = 5;
    
    /**
     * Check if a player is considered elite (top 5 at their position).
     */
    public static boolean isElitePlayer(Player player) {
        return player != null && player.getPositionRank() > 0 
                && player.getPositionRank() <= ELITE_POSITION_RANK_THRESHOLD;
    }
    
    /**
     * Get the value tier for a pick, factoring in elite status.
     * @param valueScore The calculated value score
     * @param player The player (for elite check)
     * @return ValueTier enum
     */
    public static ValueTier getValueTier(int valueScore, Player player) {
        if (isElitePlayer(player)) {
            return ValueTier.ELITE;
        }
        return getValueTier(valueScore);
    }
    
    /**
     * Get the value tier for a pick (without elite check).
     * @param valueScore The calculated value score
     * @return ValueTier enum
     */
    public static ValueTier getValueTier(int valueScore) {
        if (valueScore >= GREAT_VALUE_THRESHOLD) {
            return ValueTier.GREAT_VALUE;
        } else if (valueScore >= GOOD_VALUE_THRESHOLD) {
            return ValueTier.GOOD_VALUE;
        } else if (valueScore >= SLIGHT_REACH_THRESHOLD) {
            return ValueTier.FAIR_VALUE;
        } else if (valueScore >= BIG_REACH_THRESHOLD) {
            return ValueTier.SLIGHT_REACH;
        } else {
            return ValueTier.BIG_REACH;
        }
    }
    
    /**
     * Get the display icon for a value tier.
     * @param tier The value tier
     * @return Display icon string
     */
    public static String getValueIcon(ValueTier tier) {
        switch (tier) {
            case ELITE:
                return "🏆";
            case GREAT_VALUE:
                return "★";
            case GOOD_VALUE:
                return "↑";
            case FAIR_VALUE:
                return "=";
            case SLIGHT_REACH:
                return "↓";
            case BIG_REACH:
                return "⚠";
            default:
                return "";
        }
    }
    
    /**
     * Get the color for a value tier.
     * @param tier The value tier
     * @return Color as integer (ARGB)
     */
    public static int getValueColor(ValueTier tier) {
        switch (tier) {
            case ELITE:
                return 0xFF6A1B9A; // Purple
            case GREAT_VALUE:
                return 0xFF4CAF50; // Green
            case GOOD_VALUE:
                return 0xFF8BC34A; // Light Green
            case FAIR_VALUE:
                return 0xFF9E9E9E; // Gray
            case SLIGHT_REACH:
                return 0xFFFF9800; // Orange
            case BIG_REACH:
                return 0xFFF44336; // Red
            default:
                return 0xFF9E9E9E; // Gray
        }
    }
    
    /**
     * Get the formatted value string for display.
     * @param valueScore The calculated value score
     * @return Formatted string like "+25" or "-12"
     */
    public static String getValueString(int valueScore) {
        if (valueScore > 0) {
            return "+" + valueScore;
        } else if (valueScore < 0) {
            return String.valueOf(valueScore);
        } else {
            return "0";
        }
    }
    
    /**
     * Get the value description for a tier.
     * @param tier The value tier
     * @return Human-readable description
     */
    public static String getValueDescription(ValueTier tier) {
        switch (tier) {
            case ELITE:
                return "Elite";
            case GREAT_VALUE:
                return "Great Value";
            case GOOD_VALUE:
                return "Good Value";
            case FAIR_VALUE:
                return "Fair Value";
            case SLIGHT_REACH:
                return "Slight Reach";
            case BIG_REACH:
                return "Big Reach";
            default:
                return "Unknown";
        }
    }
    
    /**
     * Enum representing value tiers.
     */
    public enum ValueTier {
        ELITE,
        GREAT_VALUE,
        GOOD_VALUE,
        FAIR_VALUE,
        SLIGHT_REACH,
        BIG_REACH
    }
}
