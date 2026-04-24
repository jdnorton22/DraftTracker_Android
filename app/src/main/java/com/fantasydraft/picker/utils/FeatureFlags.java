package com.fantasydraft.picker.utils;

/**
 * Feature flags for controlling app features.
 * Change these values to enable/disable features.
 */
public class FeatureFlags {
    
    /**
     * Enable or disable the Import Players feature.
     * Set to true to allow users to import custom player data.
     * Set to false to hide the feature (e.g., for free version).
     */
    public static final boolean ENABLE_IMPORT_PLAYERS = false;
    
    /**
     * Enable or disable the Refresh Player Data feature.
     * Set to true to allow users to refresh player data from ESPN.
     * Set to false to hide the feature.
     */
    public static final boolean ENABLE_REFRESH_PLAYER_DATA = true;
}
