package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Miki on 8/16/2016.
 */
public class SetOptimalStatsActivity extends Activity {
    private EditText optimalTempEdit;
    private EditText optimalMoistureEdit;
    private EditText optimalLightEdit;
    private Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_optimal_stats);
        optimalTempEdit = (EditText) findViewById(R.id.optimal_temp_edit);
        optimalMoistureEdit = (EditText) findViewById(R.id.optimal_moisture_edit);
        optimalLightEdit = (EditText) findViewById(R.id.optimal_light_edit);
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(SetOptimalStatsActivity.this, SetGPIOActivity.class);
                // pass on info
                Intent receivedIntent = getIntent();
                receivedIntent.setClass(SetOptimalStatsActivity.this, SetGPIOActivity.class);
                double selectedOptimalTemp = Double.parseDouble(optimalTempEdit.getText().toString());
                double selectedOptimalMoisture = Double.parseDouble(optimalMoistureEdit.getText().toString());
                double selectedOptimalLight = Double.parseDouble(optimalLightEdit.getText().toString());
                receivedIntent.putExtra(Plant.OPTIMAL_LIGHT_KEY, selectedOptimalLight);
                receivedIntent.putExtra(Plant.OPTIMAL_MOISTURE_KEY, selectedOptimalMoisture);
                receivedIntent.putExtra(Plant.OPTIMAL_TEMP_KEY, selectedOptimalTemp);

                receivedIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(receivedIntent);
                finish();
            }
        });


    }

}
