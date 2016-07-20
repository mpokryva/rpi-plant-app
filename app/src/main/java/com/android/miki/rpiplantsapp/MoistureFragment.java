package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Miki on 6/11/2016.
 * Deals with representing moisture levels of plant. Appears as a tab in PlantStatsActivity.
 */
public class MoistureFragment extends Fragment {
    // Dummy moisture level variable
    public Moisture mMoisture;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mMoisture = new Moisture();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_moisture, container, false);


    // May have to wire up the 3 "BLANK" TextViews in code here.




        return v;
    }
}
