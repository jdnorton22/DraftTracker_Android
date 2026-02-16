package com.fantasydraft.picker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Pick implements Parcelable {
    private int pickNumber;
    private int round;
    private int pickInRound;
    private String teamId;
    private String playerId;
    private long timestamp;

    public Pick() {
    }

    public Pick(int pickNumber, int round, int pickInRound, String teamId, String playerId, long timestamp) {
        this.pickNumber = pickNumber;
        this.round = round;
        this.pickInRound = pickInRound;
        this.teamId = teamId;
        this.playerId = playerId;
        this.timestamp = timestamp;
    }

    protected Pick(Parcel in) {
        pickNumber = in.readInt();
        round = in.readInt();
        pickInRound = in.readInt();
        teamId = in.readString();
        playerId = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Pick> CREATOR = new Creator<Pick>() {
        @Override
        public Pick createFromParcel(Parcel in) {
            return new Pick(in);
        }

        @Override
        public Pick[] newArray(int size) {
            return new Pick[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pickNumber);
        dest.writeInt(round);
        dest.writeInt(pickInRound);
        dest.writeString(teamId);
        dest.writeString(playerId);
        dest.writeLong(timestamp);
    }

    public int getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(int pickNumber) {
        this.pickNumber = pickNumber;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getPickInRound() {
        return pickInRound;
    }

    public void setPickInRound(int pickInRound) {
        this.pickInRound = pickInRound;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
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
        Pick pick = (Pick) o;
        return pickNumber == pick.pickNumber &&
                round == pick.round &&
                pickInRound == pick.pickInRound &&
                timestamp == pick.timestamp &&
                Objects.equals(teamId, pick.teamId) &&
                Objects.equals(playerId, pick.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickNumber, round, pickInRound, teamId, playerId, timestamp);
    }
}
