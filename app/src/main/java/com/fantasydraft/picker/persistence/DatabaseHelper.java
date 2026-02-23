package com.fantasydraft.picker.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fantasy_draft.db";
    private static final int DATABASE_VERSION = 5; // Incremented for byeWeek column

    // Table names
    public static final String TABLE_TEAMS = "teams";
    public static final String TABLE_PLAYERS = "players";
    public static final String TABLE_PICKS = "picks";
    public static final String TABLE_DRAFT_STATE = "draft_state";
    public static final String TABLE_SAVED_DRAFTS = "saved_drafts";

    // Common column
    public static final String COLUMN_DRAFT_ID = "draft_id";

    // Saved drafts table columns
    public static final String COLUMN_SAVED_DRAFT_ID = "id";
    public static final String COLUMN_SAVED_DRAFT_NAME = "draft_name";
    public static final String COLUMN_SAVED_DRAFT_CREATED = "created_timestamp";
    public static final String COLUMN_SAVED_DRAFT_MODIFIED = "modified_timestamp";
    public static final String COLUMN_SAVED_DRAFT_LEAGUE_NAME = "league_name";
    public static final String COLUMN_SAVED_DRAFT_TEAM_COUNT = "team_count";
    public static final String COLUMN_SAVED_DRAFT_PICK_COUNT = "pick_count";

    // Teams table columns
    public static final String COLUMN_TEAM_ID = "id";
    public static final String COLUMN_TEAM_NAME = "name";
    public static final String COLUMN_TEAM_DRAFT_POSITION = "draft_position";

    // Players table columns
    public static final String COLUMN_PLAYER_ID = "id";
    public static final String COLUMN_PLAYER_NAME = "name";
    public static final String COLUMN_PLAYER_POSITION = "position";
    public static final String COLUMN_PLAYER_RANK = "rank";
    public static final String COLUMN_PLAYER_IS_DRAFTED = "is_drafted";
    public static final String COLUMN_PLAYER_DRAFTED_BY = "drafted_by";
    public static final String COLUMN_PLAYER_BYE_WEEK = "bye_week";

    // Picks table columns
    public static final String COLUMN_PICK_NUMBER = "pick_number";
    public static final String COLUMN_PICK_ROUND = "round";
    public static final String COLUMN_PICK_IN_ROUND = "pick_in_round";
    public static final String COLUMN_PICK_TEAM_ID = "team_id";
    public static final String COLUMN_PICK_PLAYER_ID = "player_id";
    public static final String COLUMN_PICK_TIMESTAMP = "timestamp";

    // Draft state table columns
    public static final String COLUMN_STATE_CURRENT_ROUND = "current_round";
    public static final String COLUMN_STATE_CURRENT_PICK_IN_ROUND = "current_pick_in_round";
    public static final String COLUMN_STATE_IS_COMPLETE = "is_complete";
    public static final String COLUMN_STATE_FLOW_TYPE = "flow_type";
    public static final String COLUMN_STATE_NUMBER_OF_ROUNDS = "number_of_rounds";
    public static final String COLUMN_STATE_LEAGUE_NAME = "league_name";
    public static final String COLUMN_STATE_SKIP_FIRST_ROUND = "skip_first_round";

    // Create table statements
    private static final String CREATE_TABLE_SAVED_DRAFTS = 
        "CREATE TABLE " + TABLE_SAVED_DRAFTS + " (" +
        COLUMN_SAVED_DRAFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_SAVED_DRAFT_NAME + " TEXT NOT NULL UNIQUE, " +
        COLUMN_SAVED_DRAFT_CREATED + " INTEGER NOT NULL, " +
        COLUMN_SAVED_DRAFT_MODIFIED + " INTEGER NOT NULL, " +
        COLUMN_SAVED_DRAFT_LEAGUE_NAME + " TEXT NOT NULL, " +
        COLUMN_SAVED_DRAFT_TEAM_COUNT + " INTEGER NOT NULL, " +
        COLUMN_SAVED_DRAFT_PICK_COUNT + " INTEGER NOT NULL" +
        ");";

    private static final String CREATE_TABLE_TEAMS = 
        "CREATE TABLE " + TABLE_TEAMS + " (" +
        COLUMN_DRAFT_ID + " INTEGER NOT NULL DEFAULT 0, " +
        COLUMN_TEAM_ID + " TEXT NOT NULL, " +
        COLUMN_TEAM_NAME + " TEXT NOT NULL, " +
        COLUMN_TEAM_DRAFT_POSITION + " INTEGER NOT NULL, " +
        "PRIMARY KEY(" + COLUMN_DRAFT_ID + ", " + COLUMN_TEAM_ID + ")" +
        ");";

    private static final String CREATE_TABLE_PLAYERS = 
        "CREATE TABLE " + TABLE_PLAYERS + " (" +
        COLUMN_DRAFT_ID + " INTEGER NOT NULL DEFAULT 0, " +
        COLUMN_PLAYER_ID + " TEXT NOT NULL, " +
        COLUMN_PLAYER_NAME + " TEXT NOT NULL, " +
        COLUMN_PLAYER_POSITION + " TEXT NOT NULL, " +
        COLUMN_PLAYER_RANK + " INTEGER NOT NULL, " +
        COLUMN_PLAYER_IS_DRAFTED + " INTEGER NOT NULL DEFAULT 0, " +
        COLUMN_PLAYER_DRAFTED_BY + " TEXT, " +
        COLUMN_PLAYER_BYE_WEEK + " INTEGER NOT NULL DEFAULT 0, " +
        "PRIMARY KEY(" + COLUMN_DRAFT_ID + ", " + COLUMN_PLAYER_ID + ")" +
        ");";

    private static final String CREATE_TABLE_PICKS = 
        "CREATE TABLE " + TABLE_PICKS + " (" +
        COLUMN_DRAFT_ID + " INTEGER NOT NULL DEFAULT 0, " +
        COLUMN_PICK_NUMBER + " INTEGER NOT NULL, " +
        COLUMN_PICK_ROUND + " INTEGER NOT NULL, " +
        COLUMN_PICK_IN_ROUND + " INTEGER NOT NULL, " +
        COLUMN_PICK_TEAM_ID + " TEXT NOT NULL, " +
        COLUMN_PICK_PLAYER_ID + " TEXT NOT NULL, " +
        COLUMN_PICK_TIMESTAMP + " INTEGER NOT NULL, " +
        "PRIMARY KEY(" + COLUMN_DRAFT_ID + ", " + COLUMN_PICK_NUMBER + ")" +
        ");";

    private static final String CREATE_TABLE_DRAFT_STATE = 
        "CREATE TABLE " + TABLE_DRAFT_STATE + " (" +
        COLUMN_DRAFT_ID + " INTEGER PRIMARY KEY DEFAULT 0, " +
        COLUMN_STATE_CURRENT_ROUND + " INTEGER NOT NULL, " +
        COLUMN_STATE_CURRENT_PICK_IN_ROUND + " INTEGER NOT NULL, " +
        COLUMN_STATE_IS_COMPLETE + " INTEGER NOT NULL DEFAULT 0, " +
        COLUMN_STATE_FLOW_TYPE + " TEXT NOT NULL, " +
        COLUMN_STATE_NUMBER_OF_ROUNDS + " INTEGER NOT NULL, " +
        COLUMN_STATE_LEAGUE_NAME + " TEXT NOT NULL DEFAULT 'My League', " +
        COLUMN_STATE_SKIP_FIRST_ROUND + " INTEGER NOT NULL DEFAULT 0" +
        ");";

    // Create index statements for frequently queried columns
    private static final String CREATE_INDEX_PLAYERS_RANK = 
        "CREATE INDEX idx_players_rank ON " + TABLE_PLAYERS + "(" + COLUMN_PLAYER_RANK + ");";

    private static final String CREATE_INDEX_PLAYERS_IS_DRAFTED = 
        "CREATE INDEX idx_players_is_drafted ON " + TABLE_PLAYERS + "(" + COLUMN_PLAYER_IS_DRAFTED + ");";

    private static final String CREATE_INDEX_PICKS_PICK_NUMBER = 
        "CREATE INDEX idx_picks_pick_number ON " + TABLE_PICKS + "(" + COLUMN_PICK_NUMBER + ");";

    private static final String CREATE_INDEX_TEAMS_DRAFT_POSITION = 
        "CREATE INDEX idx_teams_draft_position ON " + TABLE_TEAMS + "(" + COLUMN_TEAM_DRAFT_POSITION + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_SAVED_DRAFTS);
        db.execSQL(CREATE_TABLE_TEAMS);
        db.execSQL(CREATE_TABLE_PLAYERS);
        db.execSQL(CREATE_TABLE_PICKS);
        db.execSQL(CREATE_TABLE_DRAFT_STATE);

        // Create indexes
        db.execSQL(CREATE_INDEX_PLAYERS_RANK);
        db.execSQL(CREATE_INDEX_PLAYERS_IS_DRAFTED);
        db.execSQL(CREATE_INDEX_PICKS_PICK_NUMBER);
        db.execSQL(CREATE_INDEX_TEAMS_DRAFT_POSITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 2) {
                // Add league_name column to draft_state table
                db.execSQL("ALTER TABLE " + TABLE_DRAFT_STATE + " ADD COLUMN " + 
                          COLUMN_STATE_LEAGUE_NAME + " TEXT NOT NULL DEFAULT 'My League'");
            }
            
            if (oldVersion < 3) {
                // Add skip_first_round column to draft_state table
                db.execSQL("ALTER TABLE " + TABLE_DRAFT_STATE + " ADD COLUMN " + 
                          COLUMN_STATE_SKIP_FIRST_ROUND + " INTEGER NOT NULL DEFAULT 0");
            }
            
            if (oldVersion < 4) {
                // Add multi-draft support
                // This is a complex migration, so we'll use a temporary table approach
                
                // Create saved_drafts table
                db.execSQL(CREATE_TABLE_SAVED_DRAFTS);
                
                // Migrate teams table
                db.execSQL("ALTER TABLE " + TABLE_TEAMS + " RENAME TO teams_old");
                db.execSQL(CREATE_TABLE_TEAMS);
                db.execSQL("INSERT INTO " + TABLE_TEAMS + " (" + COLUMN_DRAFT_ID + ", " + 
                          COLUMN_TEAM_ID + ", " + COLUMN_TEAM_NAME + ", " + COLUMN_TEAM_DRAFT_POSITION + ") " +
                          "SELECT 0, " + COLUMN_TEAM_ID + ", " + COLUMN_TEAM_NAME + ", " + 
                          COLUMN_TEAM_DRAFT_POSITION + " FROM teams_old");
                db.execSQL("DROP TABLE teams_old");
                
                // Migrate players table
                db.execSQL("ALTER TABLE " + TABLE_PLAYERS + " RENAME TO players_old");
                db.execSQL(CREATE_TABLE_PLAYERS);
                db.execSQL("INSERT INTO " + TABLE_PLAYERS + " (" + COLUMN_DRAFT_ID + ", " + 
                          COLUMN_PLAYER_ID + ", " + COLUMN_PLAYER_NAME + ", " + COLUMN_PLAYER_POSITION + ", " +
                          COLUMN_PLAYER_RANK + ", " + COLUMN_PLAYER_IS_DRAFTED + ", " + COLUMN_PLAYER_DRAFTED_BY + ") " +
                          "SELECT 0, " + COLUMN_PLAYER_ID + ", " + COLUMN_PLAYER_NAME + ", " + 
                          COLUMN_PLAYER_POSITION + ", " + COLUMN_PLAYER_RANK + ", " + 
                          COLUMN_PLAYER_IS_DRAFTED + ", " + COLUMN_PLAYER_DRAFTED_BY + " FROM players_old");
                db.execSQL("DROP TABLE players_old");
                
                // Migrate picks table
                db.execSQL("ALTER TABLE " + TABLE_PICKS + " RENAME TO picks_old");
                db.execSQL(CREATE_TABLE_PICKS);
                db.execSQL("INSERT INTO " + TABLE_PICKS + " (" + COLUMN_DRAFT_ID + ", " + 
                          COLUMN_PICK_NUMBER + ", " + COLUMN_PICK_ROUND + ", " + COLUMN_PICK_IN_ROUND + ", " +
                          COLUMN_PICK_TEAM_ID + ", " + COLUMN_PICK_PLAYER_ID + ", " + COLUMN_PICK_TIMESTAMP + ") " +
                          "SELECT 0, " + COLUMN_PICK_NUMBER + ", " + COLUMN_PICK_ROUND + ", " + 
                          COLUMN_PICK_IN_ROUND + ", " + COLUMN_PICK_TEAM_ID + ", " + 
                          COLUMN_PICK_PLAYER_ID + ", " + COLUMN_PICK_TIMESTAMP + " FROM picks_old");
                db.execSQL("DROP TABLE picks_old");
                
                // Migrate draft_state table
                db.execSQL("ALTER TABLE " + TABLE_DRAFT_STATE + " RENAME TO draft_state_old");
                db.execSQL(CREATE_TABLE_DRAFT_STATE);
                db.execSQL("INSERT INTO " + TABLE_DRAFT_STATE + " (" + COLUMN_DRAFT_ID + ", " + 
                          COLUMN_STATE_CURRENT_ROUND + ", " + COLUMN_STATE_CURRENT_PICK_IN_ROUND + ", " +
                          COLUMN_STATE_IS_COMPLETE + ", " + COLUMN_STATE_FLOW_TYPE + ", " + 
                          COLUMN_STATE_NUMBER_OF_ROUNDS + ", " + COLUMN_STATE_LEAGUE_NAME + ", " +
                          COLUMN_STATE_SKIP_FIRST_ROUND + ") " +
                          "SELECT 0, " + COLUMN_STATE_CURRENT_ROUND + ", " + COLUMN_STATE_CURRENT_PICK_IN_ROUND + ", " +
                          COLUMN_STATE_IS_COMPLETE + ", " + COLUMN_STATE_FLOW_TYPE + ", " + 
                          COLUMN_STATE_NUMBER_OF_ROUNDS + ", " + COLUMN_STATE_LEAGUE_NAME + ", " +
                          COLUMN_STATE_SKIP_FIRST_ROUND + " FROM draft_state_old");
                db.execSQL("DROP TABLE draft_state_old");
                
                // Recreate indexes
                db.execSQL(CREATE_INDEX_PLAYERS_RANK);
                db.execSQL(CREATE_INDEX_PLAYERS_IS_DRAFTED);
                db.execSQL(CREATE_INDEX_PICKS_PICK_NUMBER);
                db.execSQL(CREATE_INDEX_TEAMS_DRAFT_POSITION);
            }
            
            if (oldVersion < 5) {
                // Add bye_week column to players table
                db.execSQL("ALTER TABLE " + TABLE_PLAYERS + " ADD COLUMN " + 
                          COLUMN_PLAYER_BYE_WEEK + " INTEGER NOT NULL DEFAULT 0");
            }
        } catch (Exception e) {
            // If migration fails, drop all tables and recreate from scratch
            android.util.Log.e("DatabaseHelper", "Database migration failed, recreating database", e);
            dropAllTables(db);
            onCreate(db);
        }
    }
    
    /**
     * Drop all tables in case of migration failure.
     */
    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_DRAFTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAFT_STATE);
        db.execSQL("DROP TABLE IF EXISTS teams_old");
        db.execSQL("DROP TABLE IF EXISTS players_old");
        db.execSQL("DROP TABLE IF EXISTS picks_old");
        db.execSQL("DROP TABLE IF EXISTS draft_state_old");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }
}
