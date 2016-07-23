package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Miki on 7/22/2016.
 */
public class PlantFragment extends Fragment {
    public static final String ARG_PLANT_NUMBER = "plant_number";
    private LightFragment light;
    private MoistureFragment moisture;
    private TemperatureFragment temperature;



    public PlantFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static Fragment newInstance(int position) {
        Fragment fragment = new PlantFragment();
        Bundle args = new Bundle();
        args.putInt(PlantFragment.ARG_PLANT_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fra)
    }

    public LightFragment getLight() {
        return light;
    }

    public MoistureFragment getMoisture() {
        return moisture;
    }

    public TemperatureFragment getTemperature() {
        return temperature;
    }
}
