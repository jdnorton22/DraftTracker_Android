package com.fantasydraft.picker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DraftConfig implements Parcelable {
    private FlowType flowType;
    private int numberOfRounds;
    private String leagueName;
    private boolean skipFirstRound; // For keeper leagues
    private boolean stopwatchEnabled; // Show pick timer on draft screen
    
    // Position roster requirements (min/max for each position)
    private Map<String, PositionRequirement> positionRequirements;

    public DraftConfig() {
        this.leagueName = "My League"; // Default league name
        this.skipFirstRound = false;
        this.stopwatchEnabled = false;
        this.positionRequirements = getDefaultPositionRequirements();
    }

    public DraftConfig(FlowType flowType, int numberOfRounds) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = "My League"; // Default league name
        this.skipFirstRound = false;
        this.stopwatchEnabled = false;
        this.positionRequirements = getDefaultPositionRequirements();
    }

    public DraftConfig(FlowType flowType, int numberOfRounds, String leagueName) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = leagueName != null && !leagueName.trim().isEmpty() ? leagueName : "My League";
        this.skipFirstRound = false;
        this.stopwatchEnabled = false;
        this.positionRequirements = getDefaultPositionRequirements();
    }

    public DraftConfig(FlowType flowType, int numberOfRounds, String leagueName, boolean skipFirstRound) {
        this.flowType = flowType;
        this.numberOfRounds = numberOfRounds;
        this.leagueName = leagueName != null && !leagueName.trim().isEmpty() ? leagueName : "My League";
        this.skipFirstRound = skipFirstRound;
        this.stopwatchEnabled = false;
        this.positionRequirements = getDefaultPositionRequirements();
    }
    
    /**
     * Get default position requirements for standard fantasy football leagues.
     * Max value of -1 means no limit.
     */
    private static Map<String, PositionRequirement> getDefaultPositionRequirements() {
        Map<String, PositionRequirement> defaults = new HashMap<>();
        defaults.put("QB", new PositionRequirement(1, -1));
        defaults.put("RB", new PositionRequirement(2, -1));
        defaults.put("WR", new PositionRequirement(2, -1));
        defaults.put("TE", new PositionRequirement(1, -1));
        defaults.put("K", new PositionRequirement(1, -1));
        defaults.put("DST", new PositionRequirement(1, -1));
        return defaults;
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

    public boolean isStopwatchEnabled() {
        return stopwatchEnabled;
    }

    public void setStopwatchEnabled(boolean stopwatchEnabled) {
        this.stopwatchEnabled = stopwatchEnabled;
    }
    
    public Map<String, PositionRequirement> getPositionRequirements() {
        return positionRequirements;
    }
    
    public void setPositionRequirements(Map<String, PositionRequirement> positionRequirements) {
        this.positionRequirements = positionRequirements;
    }
    
    public PositionRequirement getPositionRequirement(String position) {
        return positionRequirements.get(position);
    }
    
    public void setPositionRequirement(String position, int min, int max) {
        positionRequirements.put(position, new PositionRequirement(min, max));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftConfig that = (DraftConfig) o;
        return numberOfRounds == that.numberOfRounds && 
               skipFirstRound == that.skipFirstRound &&
               stopwatchEnabled == that.stopwatchEnabled &&
               flowType == that.flowType &&
               Objects.equals(leagueName, that.leagueName) &&
               Objects.equals(positionRequirements, that.positionRequirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowType, numberOfRounds, leagueName, skipFirstRound, stopwatchEnabled, positionRequirements);
    }
    
    // Parcelable implementation
    protected DraftConfig(Parcel in) {
        flowType = FlowType.valueOf(in.readString());
        numberOfRounds = in.readInt();
        leagueName = in.readString();
        skipFirstRound = in.readByte() != 0;
        stopwatchEnabled = in.readByte() != 0;
        
        // Read position requirements
        int size = in.readInt();
        positionRequirements = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String position = in.readString();
            int min = in.readInt();
            int max = in.readInt();
            positionRequirements.put(position, new PositionRequirement(min, max));
        }
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
        dest.writeByte((byte) (stopwatchEnabled ? 1 : 0));
        
        // Write position requirements
        dest.writeInt(positionRequirements.size());
        for (Map.Entry<String, PositionRequirement> entry : positionRequirements.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue().getMin());
            dest.writeInt(entry.getValue().getMax());
        }
    }
    
    /**
     * Inner class to represent min/max requirements for a position.
     * Max value of -1 means no limit.
     */
    public static class PositionRequirement {
        private int min;
        private int max; // -1 means no limit
        
        public PositionRequirement(int min, int max) {
            this.min = min;
            this.max = max;
        }
        
        public int getMin() {
            return min;
        }
        
        public void setMin(int min) {
            this.min = min;
        }
        
        public int getMax() {
            return max;
        }
        
        public void setMax(int max) {
            this.max = max;
        }
        
        public boolean hasMaxLimit() {
            return max >= 0;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PositionRequirement that = (PositionRequirement) o;
            return min == that.min && max == that.max;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }
    }
}
