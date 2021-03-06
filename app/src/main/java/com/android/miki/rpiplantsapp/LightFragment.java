package com.android.miki.rpiplantsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import java.security.InvalidParameterException;

/**
 * Created by Miki on 6/11/2016.
 * Deals with representing light levels of plant. Appears as a tab in PlantStatsActivity.
 */
public class LightFragment extends StatFragment {
    // Dummy light level variable
    private TextView lightText;
    private final String TAG = "LightFragment";
    private static final String LIGHT_INTENT = "lightIntent";
    protected BroadcastReceiver mReceiver;
    /**
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(LIGHT_INTENT.equals(intent.getAction())){
                double value = intent.getIntExtra(PlantStatsActivity.lightKey, 0);
                setCurrentStatText(String.valueOf(value));
            }
        }
    };
     **/

    public static String getIntentKeyWord(){
        return LIGHT_INTENT;
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initializeBroadcastSystem(LIGHT_INTENT, PlantStatsActivity.lightKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        initializeTexts(v,R.id.current_level_light, R.id.optimal_level_light, R.id.action_req_light);
        return v;
    }

    /**
     * Sets the current stat text (with light units), and updates the actual
     * current level in mStat as well.
     * @param newText The new text
     */
    @Override
    public void setCurrentStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newCurrentText = newText + " " + activity.getLightUnit();
            getCurrentStatTextView().setText(newCurrentText);
            getStat().setCurrentLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    /**
     * Sets the optimal stat text (with light units), and updates the actual
     * current level in mStat as well.
     * @param newText The new text
     */
    @Override
    public void setOptimalStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newOptimalText = newText + " " + activity.getLightUnit();
            getOptimalStatTextView().setText(newOptimalText);
            getStat().setOptimalLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }
}