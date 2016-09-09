package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements SingleEditDialog.SingleEditDialogListener {

    private boolean isFahrenheit;
    private TextView publishKeyText;
    private TextView subscribeKeyText;
    private SeekBar refreshRateBar;
    private TextView refreshRateText;
    private TextView channelText;
    private String refreshUnit = " hours";
    public static final String PUBLISH_INTENT_KEY = "publishKey";
    public static final String SUBSCRIBE_INTENT_KEY = "subscribeKey";
    public static final String TEMP_UNIT_INTENT_KEY = "tempUnitKey";
    public static final String CHANNEL_INTENT_KEY = "channelKey";
    public static final String REFRESH_RATE_INTENT_KEY = "refreshRateKey";
    private final String PUB_DIALOG = "pubDialog";
    private final String SUB_DIALOG = "subDialog";
    public static final String CHANNEL_DIALOG = "channelDialog";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        publishKeyText = (TextView) findViewById(R.id.secondary_pub_key);
        subscribeKeyText = (TextView) findViewById(R.id.secondary_sub_key);
        Switch tempSwitch = (Switch) findViewById(R.id.temp_switch);
        refreshRateBar = (SeekBar) findViewById(R.id.refresh_rate_bar);
        refreshRateText = (TextView) findViewById(R.id.refresh_bar_text);
        channelText = (TextView) findViewById(R.id.secondary_pubnub_channel);


        // Sets up SeekBar
        refreshRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String barValueText = String.valueOf(progress) + refreshUnit;
                refreshRateText.setText(barValueText);
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
        if (extras != null){
            isFahrenheit = extras.getBoolean(TEMP_UNIT_INTENT_KEY);
            publishKeyText.setText(extras.getString(PUBLISH_INTENT_KEY));
            subscribeKeyText.setText(extras.getString(SUBSCRIBE_INTENT_KEY));
            channelText.setText(extras.getString(CHANNEL_INTENT_KEY));
        }

        // Switch is off by default. If unit is Fahrenheit in PlantStatsActivity, then switch to ON.
        if (isFahrenheit){
            tempSwitch.toggle();
        }


        publishKeyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog
                SingleEditDialog dialog = SingleEditDialog.newInstance("Change publish key", publishKeyText.getText().toString());
                dialog.show(getSupportFragmentManager(), PUB_DIALOG);
            }
        });

        subscribeKeyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog
                SingleEditDialog dialog = SingleEditDialog.newInstance("Change subscribe key", subscribeKeyText.getText().toString());
                dialog.show(getSupportFragmentManager(), SUB_DIALOG);
            }
        });

        channelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog
                SingleEditDialog dialog = SingleEditDialog.newInstance("Change channel", channelText.getText().toString());
                dialog.show(getSupportFragmentManager(), CHANNEL_DIALOG);
            }
        });

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Fahrenheit
                if (isChecked){
                    isFahrenheit = true;
                }
                // Celsius
                else{
                    isFahrenheit = false;
                }


            }
        });




    }

    public void onDialogPositiveClick(String pubOrSub, String newSetting){
        if (pubOrSub.equals(PUB_DIALOG)){
            publishKeyText.setText(newSetting);
        }
        else if (pubOrSub.equals(SUB_DIALOG)) {
            subscribeKeyText.setText(newSetting);
        }
        else if (pubOrSub.equals(CHANNEL_DIALOG)){
            channelText.setText(newSetting);
        }
    }



    @Override
    public void onBackPressed(){
        Intent output = new Intent();
        String publishText = publishKeyText.getText().toString();
        String subscribeText = subscribeKeyText.getText().toString();
        String channelString = channelText.getText().toString();
        output.putExtra(PUBLISH_INTENT_KEY, publishText);
        output.putExtra(SUBSCRIBE_INTENT_KEY, subscribeText);
        output.putExtra(CHANNEL_INTENT_KEY, channelString);
        output.putExtra(TEMP_UNIT_INTENT_KEY, isFahrenheit);
        output.putExtra(REFRESH_RATE_INTENT_KEY, refreshRateBar.getProgress());
        setResult(Activity.RESULT_OK, output);
        super.onBackPressed();
    }


}
