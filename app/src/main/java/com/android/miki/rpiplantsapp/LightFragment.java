package com.android.miki.rpiplantsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
public class LightFragment extends Fragment {
    // Dummy light level variable
    private TextView lightText;
    private PlantStat light;
    private final String TAG = "LightFragment";
    public static Handler sUpdateHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        light = new PlantStat();
        /**
         Bundle data = getArguments();
         lightLevel = data.getInt("lightValue");
         **/
        sUpdateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                int newLightValue = msg.getData().getInt("lightMessage");
                LightFragment.this.setLightText(String.valueOf(newLightValue));
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        lightText = (TextView) v.findViewById(R.id.current_level_light);
        return v;
    }



    public TextView getLightText() {
        return lightText;
    }


    public void setLightText(String newText) {
        try{
            lightText.setText(newText);
            light.setCurrentLevel(Integer.parseInt(newText));
        }
        catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid parameter. Light not changed.");
        }

    }


}