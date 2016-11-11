package com.android.miki.rpiplantsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {


    private SeekBar mRefreshRateBar;
    private TextView mRefreshRateText;
    private SettingsConfig mSettingsConfig;
    private String refreshUnit = " hours";
    public static final String TEMP_UNIT_INTENT_KEY = "tempUnitKey";
    public static final String REFRESH_RATE_INTENT_KEY = "refreshRateKey";
    private DatabaseReference mRootRef;
    private DatabaseReference mSettingsRef;
    public Double refreshRateValue;
    private Switch mTempSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRefreshRateBar = (SeekBar) findViewById(R.id.refresh_rate_bar);
        mRefreshRateText = (TextView) findViewById(R.id.refresh_bar_text);
        mTempSwitch = (Switch) findViewById(R.id.temp_switch);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //mRootRef.keepSynced(true);
        mSettingsRef = mRootRef.child("settings");

        mSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mSettingsConfig = dataSnapshot.getValue(SettingsConfig.class);
                    mRefreshRateBar.setProgress( mSettingsConfig.getRefreshRate().intValue());
                    mTempSwitch.setChecked(mSettingsConfig.getIsFahrenheit());
                }
                else {
                    mSettingsConfig = new SettingsConfig(1.0, true);
                    mSettingsRef.setValue(mSettingsConfig);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSettingsConfig = dataSnapshot.getValue(SettingsConfig.class);
                mRefreshRateText.setText(mSettingsConfig.getRefreshRate().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Sets up SeekBar
        mRefreshRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String barValueText = String.valueOf(progress) + refreshUnit;
                refreshRateValue = ((double) mRefreshRateBar.getProgress());
                mSettingsConfig.setRefreshRate(refreshRateValue);
                mSettingsRef.child("refreshRate").setValue(mSettingsConfig.getRefreshRate());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mTempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettingsConfig.setIsFahrenheit(isChecked);
                mSettingsRef.child("isFahrenheit").setValue(mSettingsConfig.getIsFahrenheit());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        /**
         String publishText = publishKeyText.getText().toString();
         String subscribeText = subscribeKeyText.getText().toString();
         String channelString = channelText.getText().toString();
         output.putExtra(PUBLISH_INTENT_KEY, publishText);
         output.putExtra(SUBSCRIBE_INTENT_KEY, subscribeText);
         output.putExtra(CHANNEL_INTENT_KEY, channelString);output.putExtra(REFRESH_RATE_INTENT_KEY, mRefreshRateBar.getProgress());
         **/

        super.onBackPressed();
    }

    public SettingsConfig getSettingsConfig() {
        return mSettingsConfig;
    }


}
