package com.fantasydraft.picker.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DraftSnapshot {
    private List<Team> teams;
    private List<Player> players;
    private DraftState draftState;
    private DraftConfig draftConfig;
    private List<Pick> pickHistory;
    private long timestamp;

    public DraftSnapshot() {
        this.teams = new ArrayList<>();
        this.players = new ArrayList<>();
        this.pickHistory = new ArrayList<>();
    }

    public DraftSnapshot(List<Team> teams, List<Player> players, DraftState draftState,
                         DraftConfig draftConfig, List<Pick> pickHistory, long timestamp) {
        this.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
        this.players = players != null ? new ArrayList<>(players) : new ArrayList<>();
        this.draftState = draftState;
        this.draftConfig = draftConfig;
        this.pickHistory = pickHistory != null ? new ArrayList<>(pickHistory) : new ArrayList<>();
        this.timestamp = timestamp;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players != null ? new ArrayList<>(players) : new ArrayList<>();
    }

    public DraftState getDraftState() {
        return draftState;
    }

    public void setDraftState(DraftState draftState) {
        this.draftState = draftState;
    }

    public DraftConfig getDraftConfig() {
        return draftConfig;
    }

    public void setDraftConfig(DraftConfig draftConfig) {
        this.draftConfig = draftConfig;
    }

    public List<Pick> getPickHistory() {
        return pickHistory;
    }

    public void setPickHistory(List<Pick> pickHistory) {
        this.pickHistory = pickHistory != null ? new ArrayList<>(pickHistory) : new ArrayList<>();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftSnapshot that = (DraftSnapshot) o;
        return timestamp == that.timestamp &&
                Objects.equals(teams, that.teams) &&
                Objects.equals(players, that.players) &&
                Objects.equals(draftState, that.draftState) &&
                Objects.equals(draftConfig, that.draftConfig) &&
                Objects.equals(pickHistory, that.pickHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams, players, draftState, draftConfig, pickHistory, timestamp);
    }
}
