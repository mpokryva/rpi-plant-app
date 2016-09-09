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
public class SetNameSpeciesActivity extends Activity {
    private EditText plantNameEdit;
    private EditText plantSpeciesEdit;
    private Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name_species);
        plantNameEdit = (EditText) findViewById(R.id.plant_name_edit);
        plantSpeciesEdit = (EditText) findViewById(R.id.plant_species_edit);
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivedIntent = getIntent();
                receivedIntent.setClass(SetNameSpeciesActivity.this, SetOptimalStatsActivity.class);
                //Intent intent = new Intent(SetNameSpeciesActivity.this, SetOptimalStatsActivity.class);
                // pass on info
                receivedIntent.putExtra(Plant.PLANT_NAME_KEY,plantNameEdit.getText().toString());
                receivedIntent.putExtra(Plant.PLANT_SPECIES_KEY, plantSpeciesEdit.getText().toString());

                receivedIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(receivedIntent);
                finish();

            }
        });


    }

}
