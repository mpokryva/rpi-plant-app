package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Miki on 9/2/2016.
 */
public class NoPlantsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_added_plants);
        Button addPlantButton = (Button) findViewById(R.id.add_plant_plus_button);

        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivedIntent = getIntent();
                receivedIntent.setClass(NoPlantsActivity.this, SetNameSpeciesActivity.class);
                receivedIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(receivedIntent);
                finish();
            }
        });
    }




}
