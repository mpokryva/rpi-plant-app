package com.android.miki.rpiplantsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Miki on 6/15/2016.
 */
public class ViewPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ViewPageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                MoistureFragment moistureTab = new MoistureFragment();
                if (moistureTab != null){
                    moistureTab.update();
                }
                return moistureTab;
            case 1:
                LightFragment lightTab = new LightFragment();
                if (lightTab != null){
                    lightTab.update();
                }
                return lightTab;
            case 2:
                TemperatureFragment tempTab = new TemperatureFragment();
                if (lightTab != null){
                    tempTab.update();
                }
                return tempTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mNumOfTabs;
    }








}

