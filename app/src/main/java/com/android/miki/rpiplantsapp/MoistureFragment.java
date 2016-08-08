package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Miki on 6/11/2016.
 * Deals with representing moisture levels of plant. Appears as a tab in PlantStatsActivity.
 */
public class MoistureFragment extends StatFragment implements RealTimeUpdate {
    private TextView moistureText;
    private final String TAG = "MoistureFragment";
    public static Handler sUpdateHandler;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_moisture, container, false);
        int currentStat = getStat().getCurrentLevel();
        int optimalStat = getStat().getOptimalLevel();
        initializeTexts(v, R.id.current_level_moisture, R.id.optimal_level_moisture, currentStat, optimalStat);
       // setTextView((TextView)v.findViewById(R.id.current_level_light));
        return v;
    }

}
