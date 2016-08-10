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
 * Created by Miki on 8/3/2016.
 */
public class StatFragment extends Fragment implements RealTimeUpdate {
    private TextView currentStatText;
    private TextView optimalStatText;
    private PlantStat mStat = new PlantStat();
    private final String TAG = "StatFragment";
    public static Handler sUpdateHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        currentStatText = (TextView) v.findViewById(R.id.current_level_light);
        optimalStatText = (TextView) v.findViewById(R.id.optimal_level_light);
        
        return v;
    }



    public TextView getStatText() {
        return currentStatText;
    }


    public void setCurrentStatText(String newText) {
        try{
            currentStatText.setText(newText);
            mStat.setCurrentLevel(Integer.parseInt(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Level not changed.");
        }

    }

    public void setOptimalStatText(String newText) {
        try{
            optimalStatText.setText(newText);
            mStat.setOptimalLevel(Integer.parseInt(newText));
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

    @Override
    public void update(String statKey){
        Bundle data = getArguments();
        int statLevel = data.getInt(statKey);
        getStat().setCurrentLevel(statLevel);
        //setCurrentStatText(String.valueOf(statLevel));
    }

    protected void initializeTexts(View v, int currentResId, int optimalResId, int currentStat, int optimalStat){
        setCurrentTextView((TextView)v.findViewById(currentResId));
        setOptimalTextView((TextView)v.findViewById(optimalResId));
        setCurrentStatText(String.valueOf(currentStat));
        setOptimalStatText(String.valueOf(optimalStat));
    }

    protected static StatFragment newInstance(String statKey, int stat){
        StatFragment statFragment = new StatFragment();
        Bundle args = new Bundle();
        args.putInt(statKey, stat);
        statFragment.setArguments(args);

        return statFragment;
    }
}
