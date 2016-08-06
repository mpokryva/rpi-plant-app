package com.android.miki.rpiplantsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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



public class PlantStatsActivity extends FragmentActivity implements AddPlantDialog.AddPlantDialogListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private PubNub mPubNub;
    private String[] mPlantTitles;
    private static final String PUBLISH_KEY = "pub-c-442f45b2-dfc6-4df6-97ae-fc0e9efd909a";
    private static final String SUBSCRIBE_KEY ="sub-c-6e0344ae-3bd7-11e6-85a4-0619f8945a4f";
    private static final String CHANNEL = "py-light";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_stats);
        Log.d(TAG, "inflated layout");
        Log.d(TAG, "testLog");
        initPubNub();
        Log.d(TAG, "initiated PubNub");



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
                int limit = viewPager.getOffscreenPageLimit();

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

    }

    /**
     * Handle the plant that is received.
     * @param newPlant
     */


    @Override
    public void onDialogPositiveClick(Plant newPlant) {
        createPlantMenuItem(newPlant);
    }

    private void createPlantMenuItem(Plant plant){
        String plantName = plant.getPlantName();
        MenuItem plantMenuItem = plantsMenu.add(plantName);
        plantMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });
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
                //Bundle data = new Bundle();
                //data.putInt(lightKey, lightMessage);
                if(viewPager.getCurrentItem()==1)
                    adapter.getItem(1);

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
                        Bundle moistureBundle = new Bundle();
                        moistureBundle.putInt(moistureKey, moistureMessage);
                        moistureTab.setArguments(moistureBundle);
                        return moistureTab;

                case 1:
                    LightFragment lightTab = new LightFragment();
                   // if (lightTab.isVisible()) {
                        Bundle lightBundle = new Bundle();
                        lightBundle.putInt(lightKey, lightMessage);
                        lightTab.setArguments(lightBundle);
                    //}
                        return lightTab;
                case 2:

                    TemperatureFragment tempTab = new TemperatureFragment();
                        Bundle tempBundle = new Bundle();
                        tempBundle.putInt(tempKey, tempMessage);
                        tempTab.setArguments(tempBundle);
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



}


