package com.fantasydraft.picker.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fantasydraft.picker.models.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private final DatabaseHelper dbHelper;

    public PlayerDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Player player) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DRAFT_ID, 0); // Active draft
        values.put(DatabaseHelper.COLUMN_PLAYER_ID, player.getId());
        values.put(DatabaseHelper.COLUMN_PLAYER_NAME, player.getName());
        values.put(DatabaseHelper.COLUMN_PLAYER_POSITION, player.getPosition());
        values.put(DatabaseHelper.COLUMN_PLAYER_RANK, player.getRank());
        values.put(DatabaseHelper.COLUMN_PLAYER_IS_DRAFTED, player.isDrafted() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_PLAYER_DRAFTED_BY, player.getDraftedBy());
        values.put(DatabaseHelper.COLUMN_PLAYER_BYE_WEEK, player.getByeWeek());

        return db.insert(DatabaseHelper.TABLE_PLAYERS, null, values);
    }

    public int update(Player player) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PLAYER_NAME, player.getName());
        values.put(DatabaseHelper.COLUMN_PLAYER_POSITION, player.getPosition());
        values.put(DatabaseHelper.COLUMN_PLAYER_RANK, player.getRank());
        values.put(DatabaseHelper.COLUMN_PLAYER_IS_DRAFTED, player.isDrafted() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_PLAYER_DRAFTED_BY, player.getDraftedBy());
        values.put(DatabaseHelper.COLUMN_PLAYER_BYE_WEEK, player.getByeWeek());

        return db.update(DatabaseHelper.TABLE_PLAYERS, values,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PLAYER_ID + " = ?",
                new String[]{"0", player.getId()});
    }

    public int delete(String playerId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PLAYERS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PLAYER_ID + " = ?",
                new String[]{"0", playerId});
    }

    public Player getById(String playerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PLAYERS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PLAYER_ID + " = ?",
                new String[]{"0", playerId},
                null, null, null);

        Player player = null;
        if (cursor != null && cursor.moveToFirst()) {
            player = cursorToPlayer(cursor);
            cursor.close();
        }
        return player;
    }

    public List<Player> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Player> players = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PLAYERS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"},
                null, null,
                DatabaseHelper.COLUMN_PLAYER_RANK + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                players.add(cursorToPlayer(cursor));
            }
            cursor.close();
        }
        return players;
    }

    public List<Player> getAvailablePlayers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Player> players = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PLAYERS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PLAYER_IS_DRAFTED + " = ?",
                new String[]{"0", "0"},
                null, null,
                DatabaseHelper.COLUMN_PLAYER_RANK + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                players.add(cursorToPlayer(cursor));
            }
            cursor.close();
        }
        return players;
    }

    public List<Player> getPlayersByTeam(String teamId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Player> players = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PLAYERS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_PLAYER_DRAFTED_BY + " = ?",
                new String[]{"0", teamId},
                null, null,
                DatabaseHelper.COLUMN_PLAYER_RANK + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                players.add(cursorToPlayer(cursor));
            }
            cursor.close();
        }
        return players;
    }

    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PLAYERS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"});
    }

    private Player cursorToPlayer(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_NAME));
        String position = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_POSITION));
        int rank = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_RANK));
        boolean isDrafted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_IS_DRAFTED)) == 1;
        String draftedBy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_DRAFTED_BY));
        int byeWeek = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_BYE_WEEK));

        Player player = new Player(id, name, position, rank, isDrafted, draftedBy);
        player.setByeWeek(byeWeek);
        return player;
    }
}
