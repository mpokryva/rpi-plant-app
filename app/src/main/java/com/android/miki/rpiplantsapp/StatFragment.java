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
import android.widget.TextView;

/**
 * Created by Miki on 8/3/2016.
 */
public class StatFragment extends Fragment {
    private TextView currentStatText;
    private TextView optimalStatText;
    private PlantStat mStat = new PlantStat();
    private final String TAG = "StatFragment";
    public static Handler sUpdateHandler;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initializeBroadcastSystem(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        currentStatText = (TextView) v.findViewById(R.id.current_level_light);
        optimalStatText = (TextView) v.findViewById(R.id.optimal_level_light);
        
        return v;
    }

    /**
     *
     * @param intentKey Key for intent
     * @param fragmentKey Fragment-specific key (one for light, one for moisture, etc.)
     */
    protected void initializeBroadcastSystem(String intentKey, String fragmentKey){
        if (getArguments() != null){
            update(fragmentKey);
        }
        IntentFilter filter = new IntentFilter(intentKey);

        final String intentKeyFinal = intentKey;
        final String fragmentKeyFinal = fragmentKey;
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intentKeyFinal.equals(intent.getAction())){
                    double value = intent.getIntExtra(fragmentKeyFinal, 0);
                    setCurrentStatText(String.valueOf(value));
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,filter);
    }



    public TextView getStatText() {
        return currentStatText;
    }


    public void setCurrentStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newCurrentText = newText + " " + activity.getTempUnit();
            currentStatText.setText(newCurrentText);
            mStat.setCurrentLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    public void setOptimalStatText(String newText) {
        try{
            PlantStatsActivity activity = (PlantStatsActivity) getActivity();
            String newOptimalText = newText + " " + activity.getTempUnit();
            optimalStatText.setText(newOptimalText);
            mStat.setOptimalLevel(Double.parseDouble(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    public void setCurrentTextView(TextView newTextView){
        currentStatText = newTextView;
    }
    
    public void setOptimalTextView(TextView newTextView){
        optimalStatText = newTextView;
    }

    public PlantStat getStat(){
        return mStat;
    }


    public void update(String statKey){
        Bundle data = getArguments();
        double  statLevel = data.getDouble(statKey);
        getStat().setCurrentLevel(statLevel);
    }

    protected void initializeTexts(View v, int currentResId, int optimalResId){
        double currentStat = getStat().getCurrentLevel();
        double optimalStat = getStat().getOptimalLevel();
        setCurrentTextView((TextView)v.findViewById(currentResId));
        setOptimalTextView((TextView)v.findViewById(optimalResId));
        setCurrentStatText(String.valueOf(currentStat));
        setOptimalStatText(String.valueOf(optimalStat));
    }

    protected static StatFragment newInstance(String statKey, double  stat){
        StatFragment statFragment = new StatFragment();
        Bundle args = new Bundle();
        args.putDouble(statKey, stat);
        statFragment.setArguments(args);

        return statFragment;
    }

    public void refresh(){
        String currentStat = String.valueOf(getStat().getCurrentLevel());
        String optimalStat = String.valueOf(getStat().getOptimalLevel());
        setCurrentStatText(currentStat);
        setOptimalStatText(optimalStat);
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    /**
     * Initializes the BroadcastReceiver. Basically a replacement for its constructor.
     * @param intentKey
     * @param fragmentKey
     * @return
     */



}
