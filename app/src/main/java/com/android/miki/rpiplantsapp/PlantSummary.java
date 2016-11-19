package com.android.miki.rpiplantsapp;

import com.google.firebase.database.Exclude;

/**
 * A summary of vital information about the plant. Firebase-friendly.
 */

public class PlantSummary {

    private String plantName;
    private double currentLight;
    private double currentMoisture;
    private double currentTemp;
    private double lightGPIO;
    private double moistureGPIO;
    private double tempGPIO;
    @Exclude
    private static final String STATUS_DELETED = "deleted";
    @Exclude
    private static final String STATUS_MODIFIED = "modified";
    @Exclude
    private static final String STATUS_UNCHANGED = null;
    /*
    Either "deleted", "modified", or null
     */
    private String status;

    public PlantSummary(){

    }

    public PlantSummary(String plantName, double currentLight, double currentMoisture, double currentTemp) {
        this.plantName = plantName;
        this.currentLight = currentLight;
        this.currentMoisture = currentMoisture;
        this.currentTemp = currentTemp;
    }

    public PlantSummary(String plantName) {
        this.plantName = plantName;
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

    public String getStatus(){
        return status;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }

    public double getTempGPIO() {
        return tempGPIO;
    }

    public void setTempGPIO(double tempGPIO) {
        this.tempGPIO = tempGPIO;
    }

    public double getMoistureGPIO() {
        return moistureGPIO;
    }

    public void setMoistureGPIO(double moistureGPIO) {
        this.moistureGPIO = moistureGPIO;
    }

    public double getLightGPIO() {
        return lightGPIO;
    }

    public void setLightGPIO(double lightGPIO) {
        this.lightGPIO = lightGPIO;
    }


    public static String getStatusUnchanged() {
        return STATUS_UNCHANGED;
    }

    public static String getStatusModified() {
        return STATUS_MODIFIED;
    }

    public static String getStatusDeleted() {
        return STATUS_DELETED;
    }


}
