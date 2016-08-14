package com.android.miki.rpiplantsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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


import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;




import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;

import javax.security.auth.callback.Callback;

import retrofit2.http.DELETE;


public class PlantStatsActivity extends FragmentActivity implements AddPlantDialog.AddPlantDialogListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private PubNub mPubNub;
    private String[] mPlantTitles;
    private static String PUBLISH_KEY = "pub-c-442f45b2-dfc6-4df6-97ae-fc0e9efd909a";
    private static String SUBSCRIBE_KEY ="sub-c-6e0344ae-3bd7-11e6-85a4-0619f8945a4f";
    private static String CHANNEL = "py-light";
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
    private int lightMessage;
    private int moistureMessage;
    private int tempMessage;
    private Plant selectedPlant;
    private String celsius = "°C";
    private String fahrenheit = "°F";
    private String tempUnit;
    private boolean isFahrenheit;
    static final int TEMP_CHANGE_REQUEST = 1;
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
        mDBHandler = new DBHandler(PlantStatsActivity.this, null, null, 1); ////Uncomment this
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
                AddPlantDialog addPlantDialog = new AddPlantDialog();
                addPlantDialog.show(getSupportFragmentManager(), "AddPlantDialog");
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
     * @param newPlant
     */


    @Override
    public void onDialogPositiveClick(Plant newPlant) {
        createPlantMenuItem(newPlant);
    }

    private void createPlantMenuItem(final Plant plant){
        String plantName = plant.getPlantName();
        MenuItem plantMenuItem = plantsMenu.add(plantName);
        plantMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Loads tabs
                selectedPlant = plant;
                int optimalMoisture = selectedPlant.getMoistureFrag().getStat().getOptimalLevel();
                int optimalLight = selectedPlant.getLightFrag().getStat().getOptimalLevel();
                int optimalTemp = selectedPlant.getTempFrag().getStat().getOptimalLevel();
                adapter.updateCurrentFrags(optimalMoisture, optimalLight, optimalTemp);
               // selectedPlant = null;
                return false;
            }
        });
    }

    private void sendValueToFragments(int value){
        Intent intent = new Intent(LightFragment.getIntentKeyWord());
        intent.putExtra(lightKey, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void startSettingsActivity(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SETTINGS_INTENT_KEY, isFahrenheit);
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
            // Making sure requrset was successful
            if (resultCode == RESULT_OK){
                Bundle receivedData = data.getExtras();
                String publishKey = receivedData.getString(SettingsActivity.PUBLISH_INTENT_KEY);
                String subKey = receivedData.getString(SettingsActivity.SUBSCRIBE_INTENT_KEY);
                boolean isFahrenheit = receivedData.getBoolean(SettingsActivity.TEMP_UNIT_INTENT_KEY);

                PUBLISH_KEY = publishKey;
                SUBSCRIBE_KEY = subKey;
                this.isFahrenheit = isFahrenheit;



            }
        }
    }










    public void initPubNub(){
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(PUBLISH_KEY);
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setUuid("AndroidPiLight");
        mPubNub = new PubNub(pnConfiguration);
        mPubNub.subscribe().channels(Arrays.asList(CHANNEL)).execute();
        Log.d(TAG, "subscribed");

        mPubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // handle any status
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                JsonNode lightNode = message.getMessage().findValue(lightKey); // "lightValue" is JSON key.
                lightMessage = lightNode.asInt();
                Log.d(TAG, "Got message as int");
                sendValueToFragments(lightMessage);
                //Bundle data = new Bundle();
                //data.putInt(lightKey, lightMessage);


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
                    else {
                       // mFragmentManager.beginTransaction().remove(currentMoistureFragment).commit();
                        int optimalMoisture = selectedPlant.getMoistureFrag().getStat().getOptimalLevel();
                        currentMoistureFragment = (MoistureFragment) MoistureFragment.newInstance(moistureKey, optimalMoisture);
                            //notifyDataSetChanged();

                        return currentMoistureFragment;
                    }
                case 1:
                    if(currentLightFragment == null){
                        LightFragment lightTab = new LightFragment();
                        currentLightFragment = lightTab;
                        return currentLightFragment;
                    }
                    else {
                        //mFragmentManager.beginTransaction().remove(currentLightFragment).commit();
                        //currentLightFragment = selectedPlant.getLightFrag();
                        int optimalLight = selectedPlant.getLightFrag().getStat().getOptimalLevel();
                        currentLightFragment = (LightFragment) LightFragment.newInstance(moistureKey, optimalLight);
                       // notifyDataSetChanged();
                        return currentLightFragment;
                    }
                case 2:
                    if(currentTempFragment == null){
                        TemperatureFragment tempTab = new TemperatureFragment();
                        currentTempFragment = tempTab;;
                        getItemNeverCalled = false;
                        return currentTempFragment;
                    }
                    else {
                        //mFragmentManager.beginTransaction().remove(currentTempFragment).commit();
                       // currentTempFragment = selectedPlant.getTempFrag();
                        int optimalTemp = selectedPlant.getTempFrag().getStat().getOptimalLevel();
                        currentTempFragment =  (TemperatureFragment) TemperatureFragment.newInstance(moistureKey, optimalTemp);
                        //notifyDataSetChanged();
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


        public void updateCurrentFrags(int moistureValue, int lightValue, int tempValue){
            currentMoistureFragment.setOptimalStatText(String.valueOf(moistureValue));
            currentLightFragment.setOptimalStatText(String.valueOf(lightValue));
            currentTempFragment.setOptimalStatText(String.valueOf(tempValue));
        }



    }



}





































