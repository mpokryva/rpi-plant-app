package com.android.miki.rpiplantsapp;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
    private static final String MOISTURE_INTENT = "moistureIntent";
    protected BroadcastReceiver mReceiver;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initializeBroadcastSystem(MOISTURE_INTENT, PlantStatsActivity.moistureKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_moisture, container, false);
        initializeTexts(v, R.id.current_level_moisture, R.id.optimal_level_moisture, R.id.action_req_moisture);
        return v;
    }

    /**
     * Sets the current stat text (with moisture units), and updates the actual
     * current level in mStat as well.
     * @param newText The new text
     */
    @Override
    public void setCurrentStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newCurrentText = newText + " " + activity.getMoistureUnit();
            getCurrentStatTextView().setText(newCurrentText);
            getStat().setCurrentLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    /**
     * Sets the optimal stat text (with moisture units), and updates the actual
     * current level in mStat as well.
     * @param newText The new text
     */
    @Override
    public void setOptimalStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newOptimalText = newText + " " + activity.getMoistureUnit();
            getOptimalStatTextView().setText(newOptimalText);
            getStat().setOptimalLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    public static String getIntentKeyWord(){
        return MOISTURE_INTENT;
    }

}
