package com.android.miki.rpiplantsapp;

/**
 * Created by Miki on 7/28/2016.
 */
public class PlantStat {
    private double currentLevel;
    private double optimalLevel;
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

    public double getOptimalLevel() {
        return optimalLevel;
    }

    public void setOptimalLevel(double optimalLevel) {
        this.optimalLevel = optimalLevel;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(double currentLevel) {
        this.currentLevel = currentLevel;
    }
}
