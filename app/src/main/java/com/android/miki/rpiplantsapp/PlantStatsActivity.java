package com.android.miki.rpiplantsapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.security.auth.callback.Callback;


public class PlantStatsActivity extends AppCompatActivity implements DialogListener {

    private DrawerLayout mDrawerLayout;
    private String publishKey = "pub-c-442f45b2-dfc6-4df6-97ae-fc0e9efd909a";
    private String subscribeKey = "sub-c-6e0344ae-3bd7-11e6-85a4-0619f8945a4f";
    private String channel = "py-light";
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
    private HashMap<String, MenuItem> mPlantNameToItemMap;
    private HashMap<String, Plant> mPlantNameToPlantMap;
    private TempUnit tempUnit;
    private static final String MOISTURE_UNIT = "%";
    private static final String LIGHT_UNIT = "lux";
    private boolean isFahrenheit;
    private MenuItem selectedPlantItem;
    private ActionBarDrawerToggle mDrawerToggle;
    private final int TEMP_CHANGE_REQUEST = 1;
    private ArrayList<Plant> mPlantsMenuOrder;
    public static final int ADD_PLANT_REQUEST = 2;
    static String SETTINGS_INTENT_KEY = "settingIntentKey";
    public static final String PLANTS_MENU_INDEX_KEY = "plantMenuIndex";
    private static final String PLANT_NAME_KEY = "plantNameKey";
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSettingsRef = mRootRef.child("settings");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.deleteDatabase("userplants.db");
        mDBHandler = new DBHandler(PlantStatsActivity.this, null, null, 1);
        mPlantNameToItemMap = new HashMap<>();
        mPlantsMenuOrder = new ArrayList<>();
        mPlantNameToPlantMap = new HashMap<>();

        setContentView(R.layout.activity_plant_stats);
        Toolbar actionBar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(actionBar);



        setTempUnit(new TempUnit.Fahrenheit());

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

        NavigationView mDrawerList = (NavigationView) findViewById(R.id.main_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, actionBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Menu navMenu = mDrawerList.getMenu();
        plantsMenu = navMenu.addSubMenu("Plants");
        //this.deleteDatabase("userplants.db");
        mDBHandler = new DBHandler(PlantStatsActivity.this, null, null, 1);
        SQLiteDatabase db = mDBHandler.getWritableDatabase(); ///////////*************delete
        //db.delete("plants", null, null); //////////////*******delete
        mPlants = mDBHandler.makePlants();

        // Delete the stuff below
        /**
         Plant newPlant = new Plant("TestName", "TestSpecies");
         newPlant.getLightFrag().getStat().setOptimalLevel(1);
         newPlant.getMoistureFrag().getStat().setOptimalLevel(2);
         newPlant.getTempFrag().getStat().setOptimalLevel(3);
         newPlant.setLightGPIO(4);
         newPlant.setMoistureGPIO(5);
         newPlant.setTempGPIO(6);
         **/


        for (int i = 0; i < mPlants.size(); i++) {
            createPlantMenuItem(mPlants.get(i), false, 0);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(PLANT_NAME_KEY) != null) {
                String currentItemPlantName = savedInstanceState.getString(PLANT_NAME_KEY);
                MenuItem lastSelectedItem = mPlantNameToItemMap.get(currentItemPlantName);
                setSelectedPlantItem(lastSelectedItem);
                android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                actionbar.setTitle(currentItemPlantName);
                selectedPlant = mPlantNameToPlantMap.get(currentItemPlantName);
                //loadPlantItemTabs(mPlantNameToPlantMap.get(currentItemPlantName));
            }
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
                startSettingsActivity();
            }
        });

        pushNotification("TEST");


    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (mDBHandler.isEmpty()) {
            startNoPlantActivity();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (selectedPlant != null) {
            outState.putString(PLANT_NAME_KEY, selectedPlant.getPlantName());
        }
        super.onSaveInstanceState(outState);
    }


    /**
     * Handle the plant that is received from dialog.
     *
     * @param dialogTag Tag of dialog
     * @param newPlant
     */
    @Override
    public void onDialogPositiveClick(String dialogTag, int menuItemIndex, Plant newPlant) {
        if (dialogTag == NameSpeciesDialog.NAME_SPECIES_DIALOG_TAG) {
            plantsMenu.removeItem(menuItemIndex);
            mPlantsMenuOrder.remove(menuItemIndex);
            createPlantMenuItem(newPlant, true, menuItemIndex);
            adapter.refreshCurrentFrags(false);
        }
        if (dialogTag == OptimalStatsDialog.OPTIMAL_STATS_DIALOG_TAG) {
            plantsMenu.removeItem(menuItemIndex);
            mPlantsMenuOrder.remove(menuItemIndex);
            createPlantMenuItem(newPlant, true, menuItemIndex);
            adapter.refreshCurrentFrags(false);
        }
        if (dialogTag == SetGPIODialog.SET_GPIO_DIALOG_TAG) {
            plantsMenu.removeItem(menuItemIndex);
            mPlantsMenuOrder.remove(menuItemIndex);
            createPlantMenuItem(newPlant, true, menuItemIndex);
            adapter.refreshCurrentFrags(false);
        }
    }

    private void createPlantMenuItem(final Plant plant, boolean specifyPosition, int position) {
        final String plantName = plant.getPlantName();
        final MenuItem plantMenuItem;
        if (specifyPosition) {
            plantMenuItem = plantsMenu.add(Menu.NONE, position, position, plantName);
        } else {
            plantMenuItem = plantsMenu.add(Menu.NONE, mPlantsMenuOrder.size(), mPlantsMenuOrder.size(), plantName);
        }
        ImageButton pottedPlantIcon = new ImageButton(this);
        pottedPlantIcon.setImageResource(R.drawable.potted_plant);
        pottedPlantIcon.setBackgroundColor(Color.TRANSPARENT);
        plantMenuItem.setActionView(pottedPlantIcon);
        plantMenuItem.setCheckable(true);
        plantMenuItem.getActionView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Make dialog with 3 options. (NameSpecies, OptimalStats, GPIO)
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlantStatsActivity.this);
                alertDialog.setTitle("Change plant attributes");
                final String choices[] = {"Name/species", "Optimal stats", "GPIO pins", "Delete plant"};
                alertDialog.setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int choice) {
                        switch (choice) {
                            // Name/species
                            case 0:
                                NameSpeciesDialog nameSpeciesDialog = NameSpeciesDialog.newInstance(plantMenuItem.getOrder());
                                nameSpeciesDialog.setPlant(plant);
                                nameSpeciesDialog.show(getSupportFragmentManager(), NameSpeciesDialog.NAME_SPECIES_DIALOG_TAG);
                                break;
                            case 1:
                                OptimalStatsDialog optimalStatsDialog = OptimalStatsDialog.newInstance(plantMenuItem.getOrder());
                                optimalStatsDialog.setPlant(plant);
                                optimalStatsDialog.show(getSupportFragmentManager(), OptimalStatsDialog.OPTIMAL_STATS_DIALOG_TAG);
                                break;
                            case 2:
                                SetGPIODialog setGPIODialog = SetGPIODialog.newInstance(plantMenuItem.getOrder());
                                setGPIODialog.setPlant(plant);
                                setGPIODialog.show(getSupportFragmentManager(), SetGPIODialog.SET_GPIO_DIALOG_TAG);
                                break;
                            case 3:
                                int currentItemId = plantMenuItem.getItemId();
                                plantMenuItem.setVisible(false);
                                mPlantsMenuOrder.remove(currentItemId);
                                mPlantNameToItemMap.remove(plantName);
                                mPlantNameToPlantMap.remove(plantName);
                                mDBHandler.deletePlant(plantName);
                                break;

                            default:
                                break;

                        }
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        plantMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            //Menu item is clicked. Highlights it, un-highlights other item. Loads tabs. Updates actionbar text.
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Loads tabs
                // Should delete this toast
                Toast.makeText(PlantStatsActivity.this, String.valueOf(item.getOrder()), Toast.LENGTH_SHORT).show();
                setSelectedPlantItem(item);
                android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                actionbar.setTitle(plantName);
                loadPlantItemTabs(plant);

                return false;
            }
        });

        mDBHandler.addPlant(plant);
        mPlantsMenuOrder.add(mPlantsMenuOrder.size(), plant);
        mPlantNameToItemMap.put(plantName, plantMenuItem);
        mPlantNameToPlantMap.put(plantName, plant);
    }

    /**
     * Highlights selected plant MenuItem and un-highlights previous one.
     *
     * @param item
     */
    public void setSelectedPlantItem(MenuItem item) {
        if (selectedPlantItem != null) {
            selectedPlantItem.setChecked(false);
        }
        selectedPlantItem = item;
        selectedPlantItem.setChecked(true);
    }

    private void loadPlantItemTabs(Plant plant) {
        selectedPlant = plant;
        double optimalMoisture = selectedPlant.getMoistureFrag().getStat().getOptimalLevel();
        double optimalLight = selectedPlant.getLightFrag().getStat().getOptimalLevel();
        double optimalTemp = selectedPlant.getTempFrag().getStat().getOptimalLevel();
        adapter.updateCurrentFragsOptimal(optimalMoisture, optimalLight, optimalTemp);
        // selectedPlant = null;
    }


    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.TEMP_UNIT_INTENT_KEY, isFahrenheit);
        startActivityForResult(intent, TEMP_CHANGE_REQUEST);
    }

    private void startNoPlantActivity() {
        Intent intent = new Intent(this, NoPlantsActivity.class);
        startActivityForResult(intent, ADD_PLANT_REQUEST);
    }




    public String getTempUnit() {
        return tempUnit.getTempUnit();
    }

    public String getLightUnit() {
        return LIGHT_UNIT;
    }

    public String getMoistureUnit() {
        return MOISTURE_UNIT;
    }
    private void setTempUnit(TempUnit newTempUnit) {
        tempUnit = newTempUnit;
        if (tempUnit instanceof TempUnit.Fahrenheit) {
            isFahrenheit = true;
        } else {
            isFahrenheit = false;
        }
    }

    /**
     * Convenience method for setting tempUnit.
     *
     * @param isFahrenheit If true, tempUnit is Fahrenheit. If false, it is Celsius.
     */
    private void setTempUnit(boolean isFahrenheit) {
        if (isFahrenheit) {
            tempUnit = new TempUnit.Fahrenheit();
        } else {
            tempUnit = new TempUnit.Celsius();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TEMP_CHANGE_REQUEST) {
            // Making sure request was successful
            if (resultCode == RESULT_OK) {
                Bundle receivedData = data.getExtras();
                boolean isFahrenheitUpdated = receivedData.getBoolean(SettingsActivity.TEMP_UNIT_INTENT_KEY);
                int refreshRate = receivedData.getInt(SettingsActivity.REFRESH_RATE_INTENT_KEY);

                boolean convert;
                if (isFahrenheit != isFahrenheitUpdated) {
                    convert = true;
                } else {
                    convert = false;
                }
                this.isFahrenheit = isFahrenheitUpdated;
                    this.publishKey = publishKey;
                    this.subscribeKey = subscribeKey;

                    JSONObject messageToPi = new JSONObject();
                    try {
                        messageToPi.put("publishKey", this.publishKey);
                        messageToPi.put("subscribeKey", this.subscribeKey);
                        messageToPi.put("channel", this.channel);
                        messageToPi.put("refreshRate", refreshRate);
                    } catch (JSONException e) {
                        Log.d(TAG, "Value in put() method is probably null");
                        e.printStackTrace();
                    }




                //}
                this.channel = channel;
                setTempUnit(isFahrenheit);
                adapter.refreshCurrentFrags(convert);


            }
        }

        if (requestCode == ADD_PLANT_REQUEST) {
            if (resultCode == RESULT_OK) {
                makePlantFromIntent(data);
            }
        }
    }

    private void makePlantFromIntent(Intent data) {
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

        createPlantMenuItem(plant, false, 0);
    }



    private void sendValueToFragment(double value, String key) {
        if (key.equals(lightKey)) {
            Intent intent = new Intent(LightFragment.getIntentKeyWord());
            intent.putExtra(key, value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else if (key.equals(moistureKey)) {
            Intent intent = new Intent(MoistureFragment.getIntentKeyWord());
            intent.putExtra(key, value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else if (key.equals(tempKey)) {
            Intent intent = new Intent(TemperatureFragment.getIntentKeyWord());
            intent.putExtra(key, value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }





    /**
     * Makes a push notification. NOT CURRENTLY WORKING.
     */
    public void pushNotification(String notificationText) {
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification = new Notification();
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentText(notificationText);
        builder.setSmallIcon(R.drawable.potted_plant);
        Notification notification = builder.build();
        notificationManager.notify(0, notification);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
        private MoistureFragment currentMoistureFragment;
        private LightFragment currentLightFragment;
        private TemperatureFragment currentTempFragment;
        boolean notifyChangesNeverCalled = true;
        boolean getItemNeverCalled = true;

        public ViewPageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            mFragmentManager = fm;
            this.mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (currentMoistureFragment == null) {
                        MoistureFragment moistureTab = new MoistureFragment();
                        currentMoistureFragment = moistureTab;
                        return currentMoistureFragment;

                    }
                case 1:
                    if (currentLightFragment == null) {
                        LightFragment lightTab = new LightFragment();
                        currentLightFragment = lightTab;
                        return currentLightFragment;
                    }
                case 2:
                    if (currentTempFragment == null) {
                        TemperatureFragment tempTab = new TemperatureFragment();
                        currentTempFragment = tempTab;
                        getItemNeverCalled = false;
                        return currentTempFragment;
                    }
                default:
                    return null;
            }
        }

        /**
         * Method added for dealing with orientation changes.
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                case 0:
                    MoistureFragment moistureTab = (MoistureFragment) super.instantiateItem(container, position);
                    currentMoistureFragment = moistureTab;
                    return currentMoistureFragment;

                case 1:
                    LightFragment lightTab = (LightFragment) super.instantiateItem(container, position);
                    currentLightFragment = lightTab;
                    return currentLightFragment;
                case 2:
                    TemperatureFragment tempTab = (TemperatureFragment) super.instantiateItem(container, position);
                    currentTempFragment = tempTab;
                    return currentTempFragment;
                default:
                    return null;
            }
            //areFragmentsRestored = true;
        }


        @Override
        public int getCount() {
            return mNumOfTabs;
        }


        /**
         * Updates only optimal stats of current fragments in viewPager. Called when adding new plant.
         *
         * @param moistureValue
         * @param lightValue
         * @param tempValue
         */
        public void updateCurrentFragsOptimal(double moistureValue, double lightValue, double tempValue) {
            currentMoistureFragment.setOptimalStatText(String.valueOf(moistureValue));
            currentLightFragment.setOptimalStatText(String.valueOf(lightValue));
            currentTempFragment.setOptimalStatText(String.valueOf(tempValue));
        }

        /**
         * Refreshes current fragments, and converts units if necessary.
         *
         * @param tempConvert If true, method converts temp unit. Otherwise, it does not.
         */
        public void refreshCurrentFrags(boolean tempConvert) {
            if (tempConvert) {
                convert();
            }
            currentMoistureFragment.refresh();
            currentLightFragment.refresh();
            currentTempFragment.refresh();
        }

        /**
         * Converts fragments from C to F or vice versa.
         */
        public void convert() {
            PlantStat tempStat = currentTempFragment.getStat();
            tempStat.setCurrentLevel(tempUnit.convertUnit(tempStat.getCurrentLevel()));
            tempStat.setOptimalLevel(tempUnit.convertUnit(tempStat.getOptimalLevel()));
        }
    }
}





































