package com.fantasydraft.picker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Player implements Parcelable {
    private String id;
    private String name;
    private String position;
    private int rank;
    private boolean isDrafted;
    private String draftedBy;
    
    // Last year's statistics
    private String lastYearStats;
    
    // Rankings
    private int pffRank;
    private int positionRank;
    
    // NFL Team
    private String nflTeam;
    
    // Injury Status
    private String injuryStatus;
    
    // ESPN Player ID
    private String espnId;
    
    // Bye Week
    private int byeWeek;

    // Favorite
    private boolean favorite;

    public Player() {
        this.favorite = false;
    }

    public Player(String id, String name, String position, int rank) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.rank = rank;
        this.isDrafted = false;
        this.draftedBy = null;
        this.lastYearStats = "";
        this.pffRank = 0;
        this.positionRank = 0;
        this.nflTeam = "";
        this.injuryStatus = "";
        this.espnId = "";
        this.byeWeek = 0;
        this.favorite = false;
    }

    public Player(String id, String name, String position, int rank, boolean isDrafted, String draftedBy) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.rank = rank;
        this.isDrafted = isDrafted;
        this.draftedBy = draftedBy;
        this.lastYearStats = "";
        this.pffRank = 0;
        this.positionRank = 0;
        this.nflTeam = "";
        this.injuryStatus = "";
        this.espnId = "";
        this.byeWeek = 0;
        this.favorite = false;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isDrafted() {
        return isDrafted;
    }

    public void setDrafted(boolean drafted) {
        isDrafted = drafted;
    }

    public String getDraftedBy() {
        return draftedBy;
    }

    public void setDraftedBy(String draftedBy) {
        this.draftedBy = draftedBy;
    }

    public String getLastYearStats() {
        return lastYearStats;
    }

    public void setLastYearStats(String lastYearStats) {
        this.lastYearStats = lastYearStats;
    }

    public int getPffRank() {
        return pffRank;
    }

    public void setPffRank(int pffRank) {
        this.pffRank = pffRank;
    }

    public int getPositionRank() {
        return positionRank;
    }

    public void setPositionRank(int positionRank) {
        this.positionRank = positionRank;
    }

    public String getNflTeam() {
        return nflTeam;
    }

    public void setNflTeam(String nflTeam) {
        this.nflTeam = nflTeam;
    }

    public String getInjuryStatus() {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus) {
        this.injuryStatus = injuryStatus;
    }

    public String getEspnId() {
        return espnId;
    }

    public void setEspnId(String espnId) {
        this.espnId = espnId;
    }

    public int getByeWeek() {
        return byeWeek;
    }

    public void setByeWeek(int byeWeek) {
        this.byeWeek = byeWeek;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return rank == player.rank &&
                isDrafted == player.isDrafted &&
                pffRank == player.pffRank &&
                positionRank == player.positionRank &&
                byeWeek == player.byeWeek &&
                favorite == player.favorite &&
                Objects.equals(id, player.id) &&
                Objects.equals(name, player.name) &&
                Objects.equals(position, player.position) &&
                Objects.equals(draftedBy, player.draftedBy) &&
                Objects.equals(lastYearStats, player.lastYearStats) &&
                Objects.equals(nflTeam, player.nflTeam) &&
                Objects.equals(injuryStatus, player.injuryStatus) &&
                Objects.equals(espnId, player.espnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, position, rank, isDrafted, draftedBy, lastYearStats, pffRank, positionRank, nflTeam, injuryStatus, espnId, byeWeek, favorite);
    }
    
    // Parcelable implementation
    protected Player(Parcel in) {
        id = in.readString();
        name = in.readString();
        position = in.readString();
        rank = in.readInt();
        isDrafted = in.readByte() != 0;
        draftedBy = in.readString();
        lastYearStats = in.readString();
        pffRank = in.readInt();
        positionRank = in.readInt();
        nflTeam = in.readString();
        injuryStatus = in.readString();
        espnId = in.readString();
        byeWeek = in.readInt();
        favorite = in.readByte() != 0;
    }
    
    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }
        
        @Override
        public Player[] newArray(int size) {
            return new Player[size];
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
        dest.writeString(position);
        dest.writeInt(rank);
        dest.writeByte((byte) (isDrafted ? 1 : 0));
        dest.writeString(draftedBy);
        dest.writeString(lastYearStats);
        dest.writeInt(pffRank);
        dest.writeInt(positionRank);
        dest.writeString(nflTeam);
        dest.writeString(injuryStatus);
        dest.writeString(espnId);
        dest.writeInt(byeWeek);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }
    
    /**
     * Get the ESPN player profile URL
     * @return ESPN URL for this player, or null if no ESPN ID is set
     */
    public String getEspnUrl() {
        if (espnId != null && !espnId.isEmpty()) {
            return "https://www.espn.com/nfl/player/_/id/" + espnId;
        }
        return null;
    }
    
    /**
     * Get the ESPN depth chart URL for the player's NFL team
     * @return ESPN depth chart URL for the team, or null if no team is set
     */
    public String getEspnDepthChartUrl() {
        if (nflTeam != null && !nflTeam.isEmpty()) {
            // Map team abbreviations to ESPN team names
            String espnTeamName = getEspnTeamName(nflTeam);
            if (espnTeamName != null) {
                return "https://www.espn.com/nfl/team/depth/_/name/" + espnTeamName;
            }
        }
        return null;
    }
    
    /**
     * Convert NFL team abbreviation to ESPN team name format
     * @param teamAbbr Team abbreviation (e.g., "SF", "KC")
     * @return ESPN team name (e.g., "sf", "kc"), or null if unknown
     */
    private static String getEspnTeamName(String teamAbbr) {
        if (teamAbbr == null) return null;
        
        switch (teamAbbr.toUpperCase()) {
            case "ARI": return "ari";
            case "ATL": return "atl";
            case "BAL": return "bal";
            case "BUF": return "buf";
            case "CAR": return "car";
            case "CHI": return "chi";
            case "CIN": return "cin";
            case "CLE": return "cle";
            case "DAL": return "dal";
            case "DEN": return "den";
            case "DET": return "det";
            case "GB": return "gb";
            case "HOU": return "hou";
            case "IND": return "ind";
            case "JAX": return "jax";
            case "KC": return "kc";
            case "LV": return "lv";
            case "LAC": return "lac";
            case "LAR": return "lar";
            case "MIA": return "mia";
            case "MIN": return "min";
            case "NE": return "ne";
            case "NO": return "no";
            case "NYG": return "nyg";
            case "NYJ": return "nyj";
            case "PHI": return "phi";
            case "PIT": return "pit";
            case "SF": return "sf";
            case "SEA": return "sea";
            case "TB": return "tb";
            case "TEN": return "ten";
            case "WAS": return "wsh";
            default: return null;
        }
    }
}
