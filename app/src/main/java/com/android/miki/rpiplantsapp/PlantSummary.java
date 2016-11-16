package com.android.miki.rpiplantsapp;

/**
 * A summary of vital information about the plant. Firebase-friendly.
 */

public class PlantSummary {

    private String plantName;
    private double currentLight;
    private double currentMoisture;
    private double currentTemp;

    public PlantSummary(){

    }

    public double getCurrentLight() {
        return currentLight;
    }

    public String getPlantName() {
        return plantName;
    }

    public double getCurrentMoisture() {
        return currentMoisture;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }
}
