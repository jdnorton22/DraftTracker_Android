package com.fantasydraft.picker.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    private final DatabaseHelper dbHelper;

    public TeamDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Team team) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DRAFT_ID, 0); // Active draft
        values.put(DatabaseHelper.COLUMN_TEAM_ID, team.getId());
        values.put(DatabaseHelper.COLUMN_TEAM_NAME, team.getName());
        values.put(DatabaseHelper.COLUMN_TEAM_DRAFT_POSITION, team.getDraftPosition());

        return db.insert(DatabaseHelper.TABLE_TEAMS, null, values);
    }

    public int update(Team team) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TEAM_NAME, team.getName());
        values.put(DatabaseHelper.COLUMN_TEAM_DRAFT_POSITION, team.getDraftPosition());

        return db.update(DatabaseHelper.TABLE_TEAMS, values,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_TEAM_ID + " = ?",
                new String[]{"0", team.getId()});
    }

    public int delete(String teamId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_TEAMS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_TEAM_ID + " = ?",
                new String[]{"0", teamId});
    }

    public Team getById(String teamId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TEAMS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ? AND " + DatabaseHelper.COLUMN_TEAM_ID + " = ?",
                new String[]{"0", teamId},
                null, null, null);

        Team team = null;
        if (cursor != null && cursor.moveToFirst()) {
            team = cursorToTeam(cursor);
            cursor.close();
        }
        return team;
    }

    public List<Team> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Team> teams = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TEAMS,
                null,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"},
                null, null,
                DatabaseHelper.COLUMN_TEAM_DRAFT_POSITION + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                teams.add(cursorToTeam(cursor));
            }
            cursor.close();
        }
        return teams;
    }

    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_TEAMS,
                DatabaseHelper.COLUMN_DRAFT_ID + " = ?",
                new String[]{"0"});
    }

    private Team cursorToTeam(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEAM_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEAM_NAME));
        int draftPosition = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEAM_DRAFT_POSITION));

        return new Team(id, name, draftPosition);
    }
}
