package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.content.Intent;
import android.media.audiofx.BassBoost;
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

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private boolean isFahrenheit;
    private SeekBar mRefreshRateBar;
    private TextView mRefreshRateText;
    private SettingsConfig mSettingsConfig;
    private String refreshUnit = " hours";
    public static final String TEMP_UNIT_INTENT_KEY = "tempUnitKey";
    public static final String REFRESH_RATE_INTENT_KEY = "refreshRateKey";
    final private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSettingsRef = mRootRef.child("settings");
    public Double refreshRateValue;
    private Switch tempSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRefreshRateBar = (SeekBar) findViewById(R.id.refresh_rate_bar);
        mRefreshRateText = (TextView) findViewById(R.id.refresh_bar_text);
        tempSwitch = (Switch) findViewById(R.id.temp_switch);


        // Sets up SeekBar
        mRefreshRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String barValueText = String.valueOf(progress) + refreshUnit;
                mRefreshRateText.setText(barValueText);

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

        // Gets data from PlantStatsActivity and applies it (flips switch, displays pub/sub text, etc)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isFahrenheit = extras.getBoolean(TEMP_UNIT_INTENT_KEY);

        }

        // Switch is off by default. If unit is Fahrenheit in PlantStatsActivity, then switch to ON.
        if (isFahrenheit) {
            tempSwitch.toggle();
        }


        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Fahrenheit
                if (isChecked) {
                    isFahrenheit = true;
                }
                // Celsius
                else {
                    isFahrenheit = false;
                }
                mSettingsConfig.setIsFahrenheit(isFahrenheit);
               HashMap<String, Object> isFahrenheitUpdate  = new HashMap<String, Object>();isFahrenheitUpdate.put("isFahrenheit", isChecked);
               mSettingsRef.updateChildren(isFahrenheitUpdate);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get values from Firebase
        mSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(SettingsConfig.class) == null){
                    mSettingsConfig = new SettingsConfig();
                    //Change hardcoded values
                    mSettingsConfig.setIsFahrenheit(true);
                    mSettingsConfig.setRefreshRate(1.0);
                    mSettingsRef.setValue(mSettingsConfig);
                }
                else {
                    mSettingsConfig = dataSnapshot.getValue(SettingsConfig.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        output.putExtra(TEMP_UNIT_INTENT_KEY, isFahrenheit);
        setResult(Activity.RESULT_OK, output);

        super.onBackPressed();
    }

    public SettingsConfig getSettingsConfig(){
        return mSettingsConfig;
    }



}
