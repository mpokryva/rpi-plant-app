package com.android.miki.rpiplantsapp;

/**
 * Created by Miki on 7/28/2016.
 */
public class PlantStat {
    private int currentLevel;
    private int optimalLevel;
    private String actionRequired;

    public PlantStat(int currentLevel, int optimalLevel, String actionRequired) {
        this.currentLevel = currentLevel;
        this.optimalLevel = optimalLevel;
        this.actionRequired = actionRequired;
    }

    public PlantStat() {
        currentLevel = 0;
        optimalLevel = 0;
        actionRequired = "";
    }

    public int getOptimalLevel() {
        return optimalLevel;
    }

    public void setOptimalLevel(int optimalLevel) {
        this.optimalLevel = optimalLevel;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
