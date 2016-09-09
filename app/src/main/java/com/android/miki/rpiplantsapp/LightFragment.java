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
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(LIGHT_INTENT.equals(intent.getAction())){
                double value = intent.getIntExtra(PlantStatsActivity.lightKey, 0);
                setCurrentStatText(String.valueOf(value));
            }
        }
    };

    public static String getIntentKeyWord(){
        return LIGHT_INTENT;
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //if(this.isVisible()) {
           // update(PlantStatsActivity.lightKey);
        //}
        if (getArguments() != null){
            update(PlantStatsActivity.lightKey);
        }
        IntentFilter filter = new IntentFilter(LIGHT_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,filter);
        //setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        double currentStat = getStat().getCurrentLevel();
        double optimalStat = getStat().getOptimalLevel();
        initializeTexts(v,R.id.current_level_light, R.id.optimal_level_light, currentStat, optimalStat);
        //setCurrentTextView((TextView)v.findViewById(R.id.current_level_light));
        //setOptimalTextView((TextView)v.findViewById(R.id.current_level_light));

      //  setCurrentStatText(String.valueOf(getStat().getCurrentLevel()));
        //setOptimalStatText(String.valueOf(getStat().getOptimalLevel()));
        return v;
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    protected static LightFragment newInstance(String statKey, double stat){
        LightFragment statFragment = new LightFragment();
        Bundle args = new Bundle();
        args.putDouble(statKey, stat);
        statFragment.setArguments(args);

        return statFragment;
    }


}