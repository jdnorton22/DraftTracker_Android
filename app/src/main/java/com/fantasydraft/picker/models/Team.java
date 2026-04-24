package com.fantasydraft.picker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team implements Parcelable {
    private String id;
    private String name;
    private int draftPosition;
    private List<Player> roster;

    public Team() {
        this.roster = new ArrayList<>();
    }

    public Team(String id, String name, int draftPosition) {
        this.id = id;
        this.name = name;
        this.draftPosition = draftPosition;
        this.roster = new ArrayList<>();
    }

    public Team(String id, String name, int draftPosition, List<Player> roster) {
        this.id = id;
        this.name = name;
        this.draftPosition = draftPosition;
        this.roster = roster != null ? new ArrayList<>(roster) : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDraftPosition() {
        return draftPosition;
    }

    public void setDraftPosition(int draftPosition) {
        this.draftPosition = draftPosition;
    }

    public List<Player> getRoster() {
        return roster;
    }

    public void setRoster(List<Player> roster) {
        this.roster = roster != null ? new ArrayList<>(roster) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return draftPosition == team.draftPosition &&
                Objects.equals(id, team.id) &&
                Objects.equals(name, team.name) &&
                Objects.equals(roster, team.roster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, draftPosition, roster);
    }
    
    // Parcelable implementation
    protected Team(Parcel in) {
        id = in.readString();
        name = in.readString();
        draftPosition = in.readInt();
        roster = in.createTypedArrayList(Player.CREATOR);
    }
    
    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }
        
        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(draftPosition);
        dest.writeTypedList(roster);
    }
}
