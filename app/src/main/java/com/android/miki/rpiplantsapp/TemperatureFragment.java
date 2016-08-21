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
public class TemperatureFragment extends StatFragment implements RealTimeUpdate {
    // Dummy light level variable
    private TextView tempText;
    private final String TAG = "TempFragment";
    public static Handler sUpdateHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (this.isVisible()) {
            update(PlantStatsActivity.tempKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_temp, container, false);
        double  currentStat = getStat().getCurrentLevel();
        double  optimalStat = getStat().getOptimalLevel();
        initializeTexts(v,R.id.current_level_temp, R.id.optimal_level_temp, currentStat, optimalStat);
        //setTextView((TextView)v.findViewById(R.id.current_level_light));
        return v;
    }

    protected static TemperatureFragment newInstance(String statKey, double  stat){
        TemperatureFragment statFragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putDouble (statKey, stat);
        statFragment.setArguments(args);

        return statFragment;
    }
}
