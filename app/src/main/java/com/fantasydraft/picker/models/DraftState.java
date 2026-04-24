package com.fantasydraft.picker.models;

import java.util.Objects;

public class DraftState {
    private int currentRound;
    private int currentPickInRound;
    private boolean isComplete;

    public DraftState() {
    }

    public DraftState(int currentRound, int currentPickInRound, boolean isComplete) {
        this.currentRound = currentRound;
        this.currentPickInRound = currentPickInRound;
        this.isComplete = isComplete;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentPickInRound() {
        return currentPickInRound;
    }

    public void setCurrentPickInRound(int currentPickInRound) {
        this.currentPickInRound = currentPickInRound;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftState that = (DraftState) o;
        return currentRound == that.currentRound &&
                currentPickInRound == that.currentPickInRound &&
                isComplete == that.isComplete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentRound, currentPickInRound, isComplete);
    }
}
