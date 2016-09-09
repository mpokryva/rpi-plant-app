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
public class SetGPIOActivity extends Activity {

    private EditText lightGPIOEdit;
    private EditText moistureGPIOEdit;
    private EditText tempGPIOEdit;
    private DBHandler mDBHandler;
    private Button finishButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gpio);
        lightGPIOEdit = (EditText) findViewById(R.id.gpio_light_edit);
        moistureGPIOEdit = (EditText) findViewById(R.id.gpio_moisture_edit);
        tempGPIOEdit = (EditText) findViewById(R.id.gpio_temp_edit);
        finishButton = (Button) findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(SetOptimalStatsActivity.this, SetGPIOActivity.class);
                // pass on info
                Intent receivedIntent = getIntent();
                receivedIntent.setClass(SetGPIOActivity.this, PlantStatsActivity.class);
                double lightGPIO = Double.parseDouble(lightGPIOEdit.getText().toString());
                double moistureGPIO = Double.parseDouble(moistureGPIOEdit.getText().toString());
                double tempGPIO = Double.parseDouble(tempGPIOEdit.getText().toString());
                receivedIntent.putExtra(Plant.GPIO_LIGHT_KEY, lightGPIO);
                receivedIntent.putExtra(Plant.GPIO_MOISTURE_KEY, moistureGPIO);
                receivedIntent.putExtra(Plant.GPIO_TEMP_KEY, tempGPIO);


                setResult(Activity.RESULT_OK, receivedIntent);
                finish();
            }
        });
    }


}
