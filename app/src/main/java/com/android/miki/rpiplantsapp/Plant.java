
package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Miki on 7/22/2016.
 **/
public class Plant {
    public static final String ARG_PLANT_NUMBER = "plant_number";
    private LightFragment light;
    private MoistureFragment moisture;
    private TemperatureFragment temperature;
    private String plantName;
    private String plantSpecies;
    private int lightGPIO;
    private int moistureGPIO;
    private int tempGPIO;
    public static final String PLANT_NAME_KEY = "plantName";
    public static final String PLANT_SPECIES_KEY= "plantSpecies";
    public static final String OPTIMAL_LIGHT_KEY = "optimalLight";
    public static final String OPTIMAL_MOISTURE_KEY = "optimalMoisture";
    public static final String OPTIMAL_TEMP_KEY = "optimalTemp";
    public static final String GPIO_LIGHT_KEY = "gpio_light_key";
    public static final String GPIO_MOISTURE_KEY = "gpio_moisture_key";
    public static final String GPIO_TEMP_KEY = "gpio_temp_key";


    public Plant (String plantName, String plantSpecies) {
        this.plantName = plantName;
        this.plantSpecies = plantSpecies;
        if (light == null) {
            light = new LightFragment();
            moisture = new MoistureFragment();
            temperature = new TemperatureFragment();
        }


    }


    /**
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // View rootView = inflater.inflate(R.layout.fra)
    }
    **/

    public LightFragment getLightFrag() {
        return light;
    }

    public MoistureFragment getMoistureFrag() {
        return moisture;
    }

    public TemperatureFragment getTempFrag() {
        return temperature;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantSpecies() {
        return plantSpecies;
    }

    public void setPlantSpecies(String plantSpecies) {
        this.plantSpecies = plantSpecies;
    }

    public int getMoistureGPIO() {
        return moistureGPIO;
    }

    public void setMoistureGPIO(int moistureGPIO) {
        this.moistureGPIO = moistureGPIO;
    }

    public int getLightGPIO() {
        return lightGPIO;
    }

    public void setLightGPIO(int lightGPIO) {
        this.lightGPIO = lightGPIO;
    }

    public int getTempGPIO() {
        return tempGPIO;
    }

    public void setTempGPIO(int tempGPIO) {
        this.tempGPIO = tempGPIO;
    }
}
