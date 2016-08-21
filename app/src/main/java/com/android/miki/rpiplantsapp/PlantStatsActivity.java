package com.android.miki.rpiplantsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
/**
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.pubnub.api.Callback;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

**/


import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;




import java.lang.reflect.Member;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;

import javax.security.auth.callback.Callback;

import retrofit2.http.DELETE;


public class PlantStatsActivity extends FragmentActivity implements DialogListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private PubNub mPubNub;
    private String[] mPlantTitles;
    private String publishKey = "pub-c-442f45b2-dfc6-4df6-97ae-fc0e9efd909a";
    private String subscribeKey ="sub-c-6e0344ae-3bd7-11e6-85a4-0619f8945a4f";
    private String channel = "py-light";
    private long lastUpdate = System.currentTimeMillis();
    private TabLayout tabLayout;
    private String TAG = "PlantsStatsActivity";
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    public Menu plantsMenu;
    private DBHandler mDBHandler;
    private ArrayList<Plant> mPlants;
    public static String lightKey = "lightValue";
    public static String moistureKey = "moistureValue";
    public static String tempKey = "tempValue";
    private double lightMessage;
    private double moistureMessage;
    private double tempMessage;
    private Plant selectedPlant;
    private String celsius = "°C";
    private String fahrenheit = "°F";
    private String tempUnit;
    private boolean isFahrenheit;
    private final int TEMP_CHANGE_REQUEST = 1;
    public static final int ADD_PLANT_REQUEST = 2;
    static String SETTINGS_INTENT_KEY = "settingIntentKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_stats);
        Log.d(TAG, "inflated layout");
        Log.d(TAG, "testLog");
        initPubNub();
        Log.d(TAG, "initiated PubNub");

        setTempUnit(fahrenheit);

        //FragmentManager fm = getSupportFragmentManager();
        //LightFragment lightFragment = (LightFragment)fm.findFragmentById()

        //* Original code* TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Moisture"));
        tabLayout.addTab(tabLayout.newTab().setText("Light"));
        tabLayout.addTab(tabLayout.newTab().setText("Temperature"));

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setOffscreenPageLimit(3);

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        NavigationView mDrawerList =(NavigationView) findViewById(R.id.main_navigation);

            Menu navMenu = mDrawerList.getMenu();
        plantsMenu = navMenu.addSubMenu("Plants");
        mDBHandler = new DBHandler(PlantStatsActivity.this, null, null, 1);
        SQLiteDatabase db = mDBHandler.getWritableDatabase(); ///////////*************delete
        db.delete("plants", null, null); //////////////*******delete
        mPlants = mDBHandler.makePlants();

        for(int i=0; i<mPlants.size(); i++){
            createPlantMenuItem(mPlants.get(i));
        }



        final Button addPlantButton = (Button) findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantStatsActivity.this, SetNameSpeciesActivity.class);
                startActivityForResult(intent, ADD_PLANT_REQUEST);



            }
        });

        final Button settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity(v);
            }
        });



    }

    /**
     * Handle the plant that is received.
     * @param dialogTag Tag of dialog
     * @param newPlant
     */


    @Override
    public void onDialogPositiveClick(String dialogTag, Plant newPlant) {
        if (dialogTag == NameSpeciesDialog.NAME_SPECIES_DIALOG_TAG){
            adapter.refreshCurrentFrags(false);
        }
        //if (dialogTag == )
        //createPlantMenuItem(newPlant);
    }

    private void createPlantMenuItem(final Plant plant){
        String plantName = plant.getPlantName();
        MenuItem plantMenuItem = plantsMenu.add(plantName);
        View view = new View(this);
        plantMenuItem.setActionView(R.drawable.potted_plant);
        plantMenuItem.getActionView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Make dialog with 3 options. (NameSpecies, OptimalStats, GPIO)
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlantStatsActivity.this);
                alertDialog.setTitle("Change plant attributes");
                final String choices[] = {"Name/species", "Optimal stats", "GPIO pins"};
                alertDialog.setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int choice){
                        switch (choice){
                            // Name/species
                            case 0:
                                NameSpeciesDialog nameSpeciesDialog = new NameSpeciesDialog();
                                nameSpeciesDialog.setPlant(plant);
                                nameSpeciesDialog.show(getSupportFragmentManager(), NameSpeciesDialog.NAME_SPECIES_DIALOG_TAG);
                        }
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        /*
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Make dialog with 3 options. (NameSpecies, OptimalStats, GPIO)
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlantStatsActivity.this);
                alertDialog.setTitle("Change plant attributes");
                final String choices[] = {"Name/species", "Optimal stats", "GPIO pins"};
                alertDialog.setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int choice){
                        switch (choice){
                            // Name/species
                            case 0:
                                NameSpeciesDialog nameSpeciesDialog = new NameSpeciesDialog();
                                nameSpeciesDialog.setPlant(plant);
                                nameSpeciesDialog.show(getSupportFragmentManager(), NameSpeciesDialog.NAME_SPECIES_DIALOG_TAG);
                        }
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        */
        plantMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Loads tabs
                selectedPlant = plant;
                double optimalMoisture = selectedPlant.getMoistureFrag().getStat().getOptimalLevel();
                double optimalLight = selectedPlant.getLightFrag().getStat().getOptimalLevel();
                double optimalTemp = selectedPlant.getTempFrag().getStat().getOptimalLevel();
                adapter.updateCurrentFragsOptimal(optimalMoisture, optimalLight, optimalTemp);
               // selectedPlant = null;
                return false;
            }
        });

            mDBHandler.addPlant(plant);




    }

    private void sendValueToFragments(double value){
        Intent intent = new Intent(LightFragment.getIntentKeyWord());
        intent.putExtra(lightKey, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void startSettingsActivity(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.TEMP_UNIT_INTENT_KEY, isFahrenheit);
        intent.putExtra(SettingsActivity.PUBLISH_INTENT_KEY, publishKey);
        intent.putExtra(SettingsActivity.SUBSCRIBE_INTENT_KEY, subscribeKey);
        intent.putExtra(SettingsActivity.CHANNEL_INTENT_KEY, channel);
        startActivityForResult(intent, TEMP_CHANGE_REQUEST);
    }

    public String getTempUnit(){
        return tempUnit;
    }


    /**
     * Should probably delete this method
     * @param newTempUnit
     */
    private void setTempUnit(String newTempUnit){
        if (newTempUnit.equals(fahrenheit) || newTempUnit.equals(celsius)){
            if (newTempUnit.equals(fahrenheit)){
                isFahrenheit = true;
            }
            else {
                isFahrenheit = false;
            }
            tempUnit = newTempUnit;
        }
    }

    /**
     * Set the temperature unit, and does conversions, if necessary.
     * @param isFahrenheit if true, then unit is Fahrenheit.

     */
    private void setTempUnit(boolean isFahrenheit){
       if (isFahrenheit){
           tempUnit = fahrenheit;
       }
        else{
           tempUnit = celsius;
       }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == TEMP_CHANGE_REQUEST){
            // Making sure request was successful
            if (resultCode == RESULT_OK){
                Bundle receivedData = data.getExtras();
                String publishKey = receivedData.getString(SettingsActivity.PUBLISH_INTENT_KEY);
                String subscribeKey = receivedData.getString(SettingsActivity.SUBSCRIBE_INTENT_KEY);
                boolean isFahrenheitUpdated = receivedData.getBoolean(SettingsActivity.TEMP_UNIT_INTENT_KEY);

                boolean convert;
                if(isFahrenheit != isFahrenheitUpdated){
                    convert = true;
                }
                else {
                    convert = false;
                }

                this.publishKey = publishKey;
                this.subscribeKey = subscribeKey;
                this.isFahrenheit = isFahrenheitUpdated;
                setTempUnit(isFahrenheit);
                adapter.refreshCurrentFrags(convert);



            }
        }

        if (requestCode == ADD_PLANT_REQUEST){
            if (resultCode == RESULT_OK){
                Bundle receivedData = data.getExtras();
                String plantName = receivedData.getString(Plant.PLANT_NAME_KEY);
                String plantSpecies = receivedData.getString(Plant.PLANT_SPECIES_KEY);
                double optimalLight = receivedData.getDouble(Plant.OPTIMAL_LIGHT_KEY);
                double optimalMoisture = receivedData.getDouble(Plant.OPTIMAL_MOISTURE_KEY);
                double optimalTemp = receivedData.getDouble(Plant.OPTIMAL_TEMP_KEY);
                double lightGPIO = receivedData.getDouble(Plant.GPIO_LIGHT_KEY);
                double moistureGPIO = receivedData.getDouble(Plant.GPIO_MOISTURE_KEY);
                double tempGPIO = receivedData.getDouble(Plant.GPIO_TEMP_KEY);

                Plant plant = new Plant(plantName, plantSpecies);
                plant.getLightFrag().getStat().setOptimalLevel(optimalLight);
                plant.getMoistureFrag().getStat().setOptimalLevel(optimalMoisture);
                plant.getTempFrag().getStat().setOptimalLevel(optimalTemp);
                plant.setLightGPIO(lightGPIO);
                plant.setMoistureGPIO(moistureGPIO);
                plant.setTempGPIO(tempGPIO);

                createPlantMenuItem(plant);
            }
        }
    }

    /**
     * Converts to Celsius.
     * @param value Value to convert.
     * @return The converted value
     */
    private double convertToCelsius(double value){
        double convertedValue = ((value-32)*5)/9;
        double roundedvalue = Math.round(convertedValue*100.0)/100.0;

        return roundedvalue;
    }

    /**
     * Converts to Fahrenheit.
     * @param value Value to convert.
     * @return The converted value.
     */
    private double convertToFahrenheit(double value){
        double convertedValue = ((9*value)/5)+32;
        double roundedvalue = Math.round(convertedValue*100)/100;

        return roundedvalue;
    }










    public void initPubNub(){
        PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setPublishKey(publishKey);
        pnConfiguration.setSubscribeKey(subscribeKey);
        pnConfiguration.setUuid("AndroidPiLight");
        mPubNub = new PubNub(pnConfiguration);
        mPubNub.subscribe().channels(Arrays.asList(channel)).execute();
        Log.d(TAG, "subscribed");

        mPubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // handle any status
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                JsonNode lightNode = message.getMessage().findValue(lightKey); // "lightValue" is JSON key.
                lightMessage = lightNode.asDouble();
                Log.d(TAG, "Got message as double");
                sendValueToFragments(lightMessage);
                //Bundle data = new Bundle();
                //data.putDouble(lightKey, lightMessage);


            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // handle incoming presence data
            }
        });
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



   // @Override
   // public boolean onOptionsItemSelected(MenuItem menuItem){
        // Do stuff
    //}







     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     // Handle action bar item clicks here. The action bar will
     // automatically handle clicks on the Home/Up button, so long
     // as you specify a parent activity in AndroidManifest.xml.
     int id = item.getItemId();

     //noinspection SimplifiableIfStatement
     if (id == R.id.action_settings) {
     return true;
     }

     return super.onOptionsItemSelected(item);
     }


    /**
     * Can probably be moved to its own class. Also, bundle code in each case are
     * probably unnecessary.
     */

    public class ViewPageAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        FragmentManager mFragmentManager;
        MoistureFragment currentMoistureFragment;
        LightFragment currentLightFragment;
        TemperatureFragment currentTempFragment;
        boolean notifyChangesNeverCalled = true;
        boolean getItemNeverCalled = true;

        public ViewPageAdapter(FragmentManager fm, int numOfTabs){
            super(fm);
            mFragmentManager = fm;
            this.mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position){
            switch (position){
                case 0:
                    if(currentMoistureFragment == null){
                        MoistureFragment moistureTab = new MoistureFragment();
                        currentMoistureFragment = moistureTab;
                        return currentMoistureFragment;

                    }
                case 1:
                    if(currentLightFragment == null){
                        LightFragment lightTab = new LightFragment();
                        currentLightFragment = lightTab;
                        return currentLightFragment;
                    }
                case 2:
                    if(currentTempFragment == null){
                        TemperatureFragment tempTab = new TemperatureFragment();
                        currentTempFragment = tempTab;;
                        getItemNeverCalled = false;
                        return currentTempFragment;
                    }
                default:
                    return null;
            }
        }



        @Override
        public int getCount(){
            return mNumOfTabs;
        }


        /**
         * Updates only optimal stats of current fragments in viewPager. Called when adding new plant.
         * @param moistureValue
         * @param lightValue
         * @param tempValue
         */
        public void updateCurrentFragsOptimal(double moistureValue, double lightValue, double tempValue){
            currentMoistureFragment.setOptimalStatText(String.valueOf(moistureValue));
            currentLightFragment.setOptimalStatText(String.valueOf(lightValue));
            currentTempFragment.setOptimalStatText(String.valueOf(tempValue));
        }

        /**
         *  Refreshes current fragments, and converts units if necessary.
         * @param convert
         */
        public void refreshCurrentFrags(boolean convert){
            if (convert){
                // Convert from C to F
                if (isFahrenheit) {
                    PlantStat moistureStat = currentMoistureFragment.getStat();
                    PlantStat lightStat = currentLightFragment.getStat();
                    PlantStat tempStat = currentTempFragment.getStat();
                    moistureStat.setCurrentLevel(convertToFahrenheit(moistureStat.getCurrentLevel()));
                    moistureStat.setOptimalLevel(convertToFahrenheit(moistureStat.getOptimalLevel()));
                    currentLightFragment.getStat().setCurrentLevel(convertToFahrenheit(lightStat.getCurrentLevel()));
                    currentLightFragment.getStat().setOptimalLevel(convertToFahrenheit(lightStat.getOptimalLevel()));
                    currentTempFragment.getStat().setCurrentLevel(convertToFahrenheit(tempStat.getCurrentLevel()));
                    currentTempFragment.getStat().setOptimalLevel(convertToFahrenheit(tempStat.getOptimalLevel()));
                }
                // Convert from F to C
                if (!isFahrenheit) {
                    PlantStat moistureStat = currentMoistureFragment.getStat();
                    PlantStat lightStat = currentLightFragment.getStat();
                    PlantStat tempStat = currentTempFragment.getStat();
                    moistureStat.setCurrentLevel(convertToCelsius(moistureStat.getCurrentLevel()));
                    moistureStat.setOptimalLevel(convertToCelsius(moistureStat.getOptimalLevel()));
                    currentLightFragment.getStat().setCurrentLevel(convertToCelsius(lightStat.getCurrentLevel()));
                    currentLightFragment.getStat().setOptimalLevel(convertToCelsius(lightStat.getOptimalLevel()));
                    currentTempFragment.getStat().setCurrentLevel(convertToCelsius(tempStat.getCurrentLevel()));
                    currentTempFragment.getStat().setOptimalLevel(convertToCelsius(tempStat.getOptimalLevel()));
                }
            }
            currentMoistureFragment.refresh();
            currentLightFragment.refresh();
            currentTempFragment.refresh();
        }



    }



}





































