package com.fantasydraft.picker.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fantasydraft.picker.models.Pick;

import java.util.ArrayList;
import java.util.List;

public class PickDAO {
    private final DatabaseHelper dbHelper;

    public PickDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Pick pick) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DRAFT_ID, 0); // Active draft
        values.put(DatabaseHelper.COLUMN_PICK_NUMBER, pick.getPickNumber());
        values.put(DatabaseHelper.COLUMN_PICK_ROUND, pick.getRound());
        values.put(DatabaseHelper.COLUMN_PICK_IN_ROUND, pick.getPickInRound());
        values.put(DatabaseHelper.COLUMN_PICK_TEAM_ID, pick.getTeamId());
        values.put(DatabaseHelper.COLUMN_PICK_PLAYER_ID, pick.getPlayerId());
        values.put(DatabaseHelper.COLUMN_PICK_TIMESTAMP, pick.getTimestamp());

        return db.insert(DatabaseHelper.TABLE_PICKS, null, values);
    }

    public int update(Pick pick) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PICK_ROUND, pick.getRound());
        values.put(DatabaseHelper.COLUMN_PICK_IN_ROUND, pick.getPickInRound());
        values.put(DatabaseHelper.COLUMN_PICK_TEAM_ID, pick.getTeamId());
        values.put(DatabaseHelper.COLUMN_PICK_PLAYER_ID, pick.getPlayerId());
        values.put(DatabaseHelper.COLUMN_PICK_TIMESTAMP, pick.getTimestamp());

        return db.update(DatabaseHelper.TABLE_PICKS, values,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PICK_NUMBER + " = ?",
                new String[]{"0", String.valueOf(pick.getPickNumber())});
    }

    public int delete(int pickNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PICKS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PICK_NUMBER + " = ?",
                new String[]{"0", String.valueOf(pickNumber)});
    }

    public Pick getByPickNumber(int pickNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PICKS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PICK_NUMBER + " = ?",
                new String[]{"0", String.valueOf(pickNumber)},
                null, null, null);

        Pick pick = null;
        if (cursor != null && cursor.moveToFirst()) {
            pick = cursorToPick(cursor);
            cursor.close();
        }
        return pick;
    }

    public List<Pick> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Pick> picks = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PICKS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"},
                null, null,
                DatabaseHelper.COLUMN_PICK_NUMBER + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                picks.add(cursorToPick(cursor));
            }
            cursor.close();
        }
        return picks;
    }

    public List<Pick> getPicksByTeam(String teamId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Pick> picks = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PICKS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PICK_TEAM_ID + " = ?",
                new String[]{"0", teamId},
                null, null,
                DatabaseHelper.COLUMN_PICK_NUMBER + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                picks.add(cursorToPick(cursor));
            }
            cursor.close();
        }
        return picks;
    }

    public List<Pick> getPicksByRound(int round) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Pick> picks = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PICKS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PICK_ROUND + " = ?",
                new String[]{"0", String.valueOf(round)},
                null, null,
                DatabaseHelper.COLUMN_PICK_IN_ROUND + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                picks.add(cursorToPick(cursor));
            }
            cursor.close();
        }
        return picks;
    }

    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PICKS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"});
    }

    private Pick cursorToPick(Cursor cursor) {
        int pickNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_NUMBER));
        int round = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_ROUND));
        int pickInRound = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_IN_ROUND));
        String teamId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_TEAM_ID));
        String playerId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_PLAYER_ID));
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PICK_TIMESTAMP));

        return new Pick(pickNumber, round, pickInRound, teamId, playerId, timestamp);
    }
}
