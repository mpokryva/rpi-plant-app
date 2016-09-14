package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by Miki on 8/16/2016.
 */
public class AddPlantActivity extends Activity {

    private EditText plantNameEdit;
    private EditText plantSpeciesEdit;
    private EditText optimalTempEdit;
    private EditText optimalMoistureEdit;
    private EditText optimalLightEdit;
    private EditText lightGPIOEdit;
    private EditText moistureGPIOEdit;
    private EditText tempGPIOEdit;
    private DBHandler mDBHandler;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        plantNameEdit = (EditText) findViewById(R.id.plant_name_edit);
        plantSpeciesEdit = (EditText) findViewById(R.id.plant_species_edit);
        optimalTempEdit = (EditText) findViewById(R.id.optimal_temp_edit);
        optimalMoistureEdit = (EditText) findViewById(R.id.optimal_moisture_edit);
        optimalLightEdit = (EditText) findViewById(R.id.optimal_light_edit);
        lightGPIOEdit = (EditText) findViewById(R.id.gpio_light_edit);
        moistureGPIOEdit = (EditText) findViewById(R.id.gpio_moisture_edit);
        tempGPIOEdit = (EditText) findViewById(R.id.gpio_temp_edit);
    }
}
