package com.fantasydraft.picker.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.DraftSnapshot;
import com.fantasydraft.picker.models.DraftState;
import com.fantasydraft.picker.models.FlowType;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {
    private static final String PREFS_NAME = "FantasyDraftPrefs";
    private static final String KEY_DRAFT_EXISTS = "draft_exists";

    private final DatabaseHelper dbHelper;
    private final SharedPreferences preferences;
    private final TeamDAO teamDAO;
    private final PlayerDAO playerDAO;
    private final PickDAO pickDAO;

    public PersistenceManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.teamDAO = new TeamDAO(dbHelper);
        this.playerDAO = new PlayerDAO(dbHelper);
        this.pickDAO = new PickDAO(dbHelper);
    }

    /**
     * Save the current draft state to persistent storage.
     * Uses SQLite for structured data (teams, players, picks, draft state).
     * Uses SharedPreferences to track if a draft exists.
     * 
     * @throws PersistenceException if save operation fails
     */
    public void saveDraft(DraftSnapshot snapshot) throws PersistenceException {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null");
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            throw new PersistenceException("Failed to open database for writing", 
                    PersistenceException.ErrorType.DATABASE_ERROR, e);
        }
        
        db.beginTransaction();
        try {
            // Clear existing data
            clearDraftInternal(db);

            // Save teams
            if (snapshot.getTeams() != null) {
                for (Team team : snapshot.getTeams()) {
                    teamDAO.insert(team);
                }
            }

            // Save players
            if (snapshot.getPlayers() != null) {
                for (Player player : snapshot.getPlayers()) {
                    playerDAO.insert(player);
                }
            }

            // Save picks
            if (snapshot.getPickHistory() != null) {
                for (Pick pick : snapshot.getPickHistory()) {
                    pickDAO.insert(pick);
                }
            }

            // Save draft state and config
            saveDraftStateAndConfig(db, snapshot.getDraftState(), snapshot.getDraftConfig());

            db.setTransactionSuccessful();

            // Mark that a draft exists
            preferences.edit().putBoolean(KEY_DRAFT_EXISTS, true).apply();
        } catch (android.database.sqlite.SQLiteFullException e) {
            throw new PersistenceException("Storage is full", 
                    PersistenceException.ErrorType.STORAGE_FULL, e);
        } catch (Exception e) {
            throw new PersistenceException("Failed to save draft data", 
                    PersistenceException.ErrorType.DATABASE_ERROR, e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Load the saved draft state from persistent storage.
     * Returns null if no draft exists.
     * 
     * @throws PersistenceException if load operation fails
     */
    public DraftSnapshot loadDraft() throws PersistenceException {
        // Check if a draft exists
        if (!preferences.getBoolean(KEY_DRAFT_EXISTS, false)) {
            return null;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            throw new PersistenceException("Failed to open database for reading", 
                    PersistenceException.ErrorType.DATABASE_ERROR, e);
        }

        try {
            // Load teams
            List<Team> teams = teamDAO.getAll();

            // Load players
            List<Player> players = playerDAO.getAll();

            // Load picks
            List<Pick> picks = pickDAO.getAll();

            // Load draft state and config
            DraftState draftState = null;
            DraftConfig draftConfig = null;

            Cursor cursor = db.query(DatabaseHelper.TABLE_DRAFT_STATE,
                    null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                try {
                    int currentRound = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATE_CURRENT_ROUND));
                    int currentPickInRound = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATE_CURRENT_PICK_IN_ROUND));
                    boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATE_IS_COMPLETE)) == 1;
                    String flowTypeStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATE_FLOW_TYPE));
                    int numberOfRounds = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATE_NUMBER_OF_ROUNDS));
                    
                    // Load league name (with fallback for older database versions)
                    String leagueName = "My League";
                    int leagueNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE_LEAGUE_NAME);
                    if (leagueNameIndex >= 0) {
                        leagueName = cursor.getString(leagueNameIndex);
                    }
                    
                    // Load skip first round (with fallback for older database versions)
                    boolean skipFirstRound = false;
                    int skipFirstRoundIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE_SKIP_FIRST_ROUND);
                    if (skipFirstRoundIndex >= 0) {
                        skipFirstRound = cursor.getInt(skipFirstRoundIndex) == 1;
                    }

                    draftState = new DraftState(currentRound, currentPickInRound, isComplete);
                    draftConfig = new DraftConfig(FlowType.valueOf(flowTypeStr), numberOfRounds, leagueName, skipFirstRound);
                } catch (IllegalArgumentException e) {
                    cursor.close();
                    throw new PersistenceException("Draft data is corrupted", 
                            PersistenceException.ErrorType.CORRUPTED_DATA, e);
                } finally {
                    cursor.close();
                }
            }

            // If no draft state found, return null
            if (draftState == null || draftConfig == null) {
                return null;
            }

            long timestamp = System.currentTimeMillis();
            return new DraftSnapshot(teams, players, draftState, draftConfig, picks, timestamp);
        } catch (PersistenceException e) {
            throw e; // Re-throw PersistenceException
        } catch (Exception e) {
            throw new PersistenceException("Failed to load draft data", 
                    PersistenceException.ErrorType.DATABASE_ERROR, e);
        }
    }

    /**
     * Clear all saved draft data from persistent storage.
     */
    public void clearDraft() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            clearDraftInternal(db);
            db.setTransactionSuccessful();

            // Mark that no draft exists
            preferences.edit().putBoolean(KEY_DRAFT_EXISTS, false).apply();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Internal method to clear draft data within a transaction.
     */
    private void clearDraftInternal(SQLiteDatabase db) {
        // Delete in correct order due to foreign key constraints
        db.delete(DatabaseHelper.TABLE_PICKS, null, null);
        db.delete(DatabaseHelper.TABLE_PLAYERS, null, null);
        db.delete(DatabaseHelper.TABLE_TEAMS, null, null);
        db.delete(DatabaseHelper.TABLE_DRAFT_STATE, null, null);
    }

    /**
     * Save draft state and config to the database.
     */
    private void saveDraftStateAndConfig(SQLiteDatabase db, DraftState draftState, DraftConfig draftConfig) {
        if (draftState == null || draftConfig == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DRAFT_ID, 0); // Always use draft_id 0 for active draft
        values.put(DatabaseHelper.COLUMN_STATE_CURRENT_ROUND, draftState.getCurrentRound());
        values.put(DatabaseHelper.COLUMN_STATE_CURRENT_PICK_IN_ROUND, draftState.getCurrentPickInRound());
        values.put(DatabaseHelper.COLUMN_STATE_IS_COMPLETE, draftState.isComplete() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_STATE_FLOW_TYPE, draftConfig.getFlowType().name());
        values.put(DatabaseHelper.COLUMN_STATE_NUMBER_OF_ROUNDS, draftConfig.getNumberOfRounds());
        values.put(DatabaseHelper.COLUMN_STATE_LEAGUE_NAME, draftConfig.getLeagueName());
        values.put(DatabaseHelper.COLUMN_STATE_SKIP_FIRST_ROUND, draftConfig.isSkipFirstRound() ? 1 : 0);

        db.insert(DatabaseHelper.TABLE_DRAFT_STATE, null, values);
    }
}
