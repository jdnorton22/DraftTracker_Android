package com.fantasydraft.picker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class DraftConfig implements Parcelable {
    private FlowType flowType;
    private int numberOfRounds;
    private String leagueName;
    private boolean skipFirstRound; // For keeper leagues

    public DraftConfig() {
        this.leagueName = "My League"; // Default league name
        this.skipFirstRound = false;
    }

    public DraftConfig(FlowType flowType, int numberOfRounds) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = "My League"; // Default league name
        this.skipFirstRound = false;
    }

    public DraftConfig(FlowType flowType, int numberOfRounds, String leagueName) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = leagueName != null && !leagueName.trim().isEmpty() ? leagueName : "My League";
        this.skipFirstRound = false;
    }

    public DraftConfig(FlowType flowType, int numberOfRounds, String leagueName, boolean skipFirstRound) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = leagueName != null && !leagueName.trim().isEmpty() ? leagueName : "My League";
        this.skipFirstRound = skipFirstRound;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName != null && !leagueName.trim().isEmpty() ? leagueName : "My League";
    }

    public boolean isSkipFirstRound() {
        return skipFirstRound;
    }

    public void setSkipFirstRound(boolean skipFirstRound) {
        this.skipFirstRound = skipFirstRound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftConfig that = (DraftConfig) o;
        return numberOfRounds == that.numberOfRounds && 
               skipFirstRound == that.skipFirstRound &&
               flowType == that.flowType &&
               Objects.equals(leagueName, that.leagueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowType, numberOfRounds, leagueName, skipFirstRound);
    }
    
    // Parcelable implementation
    protected DraftConfig(Parcel in) {
        flowType = FlowType.valueOf(in.readString());
        numberOfRounds = in.readInt();
        leagueName = in.readString();
        skipFirstRound = in.readByte() != 0;
    }
    
    public static final Creator<DraftConfig> CREATOR = new Creator<DraftConfig>() {
        @Override
        public DraftConfig createFromParcel(Parcel in) {
            return new DraftConfig(in);
        }
        
        @Override
        public DraftConfig[] newArray(int size) {
            return new DraftConfig[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flowType.name());
        dest.writeInt(numberOfRounds);
        dest.writeString(leagueName);
        dest.writeByte((byte) (skipFirstRound ? 1 : 0));
    }
}
