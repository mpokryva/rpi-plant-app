
package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Miki on 7/22/2016.
 **/
public class Plant extends Fragment {
    public static final String ARG_PLANT_NUMBER = "plant_number";
    private LightFragment light;
    private MoistureFragment moisture;
    private TemperatureFragment temperature;
    private String plantName;
    private String plantSpecies;


    public Plant() {
        // Empty constructor required for fragment subclasses
    }

    public static Fragment newInstance(int position) {
        Fragment fragment = new Plant();
        Bundle args = new Bundle();
        args.putInt(Plant.ARG_PLANT_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }
    /**
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // View rootView = inflater.inflate(R.layout.fra)
    }
    **/

    public LightFragment getLight() {
        return light;
    }

    public MoistureFragment getMoisture() {
        return moisture;
    }

    public TemperatureFragment getTemperature() {
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
}
