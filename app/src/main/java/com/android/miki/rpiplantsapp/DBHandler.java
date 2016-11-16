package com.android.miki.rpiplantsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBar;

import com.android.miki.rpiplantsapp.Plant;

import java.util.ArrayList;

/**
 * Created by Miki on 7/25/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userplants.db";
    public static final String TABLE_PLANTS = "plants";
    public static final String  COLUMN_ID = "id";
    public static final String COLUMN_PLANT_NAME = "plant_name";
    public static final String COLUMN_PLANT_SPECIES = "plant_species";
    public static final String COLUMN_CURRENT_LIGHT = "current_light" ;
    public static final String COLUMN_CURRENT_MOISTURE = "current_moisture";
    public static final String COLUMN_CURRENT_TEMP = "current_temp";
    public static final String COLUMN_OPTIMAL_LIGHT = "optimal_light";
    public static final String COLUMN_OPTIMAL_MOISTURE = "optimal_moisture";
    public static final String COLUMN_OPTIMAL_TEMP = "optimal_temp";
    public static final String COLUMN_LAST_LIGHT = "last_light";
    public static final String COLUMN_LAST_MOISTURE = "last_moisture";
    public static final String COLUMN_LAST_TEMP = "last_temp";
    public static final String COLUMN_GPIO_MOISTURE = "gpio_moisture";
    public static final String COLUMN_GPIO_LIGHT = "gpio_light";
    public static final String COLUMN_GPIO_TEMP = "gpio_temp";

    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_TEMP_UNIT = "temp_unit";
    public static final String COLUMN_PUB_KEY = "pub_key";
    public static final String COLUMN_SUB_KEY = "sub_key";
    public static final String COLUMN_CHANNEL = "channel";

    /**
     * Put this column into a different table. Along with channel, and pub & sub keys.
    public static final String COLUMN_TEMP_UNIT = "temp_unit";
     **/
    public static final String[] COLUMNS = {COLUMN_PLANT_NAME, COLUMN_PLANT_SPECIES, COLUMN_CURRENT_LIGHT, COLUMN_CURRENT_MOISTURE,
            COLUMN_CURRENT_TEMP, COLUMN_OPTIMAL_LIGHT, COLUMN_OPTIMAL_MOISTURE, COLUMN_OPTIMAL_TEMP};
    public static final String[] COLUMNS_CURRENT_STATS = {COLUMN_CURRENT_LIGHT, COLUMN_CURRENT_MOISTURE, COLUMN_CURRENT_TEMP};
    public static final String[] COLUMNS_OPTIMAL_STATS = {COLUMN_OPTIMAL_LIGHT, COLUMN_OPTIMAL_MOISTURE, COLUMN_OPTIMAL_TEMP};
    public static final String[] COLUMNS_ALL_STATS = {COLUMN_CURRENT_LIGHT, COLUMN_CURRENT_MOISTURE, COLUMN_CURRENT_TEMP,
            COLUMN_OPTIMAL_LIGHT, COLUMN_OPTIMAL_MOISTURE, COLUMN_OPTIMAL_TEMP};
    public static final String[] COLUMNS_LIGHT = {COLUMN_CURRENT_LIGHT, COLUMN_OPTIMAL_LIGHT};
    public static final String[] COLUMNS_MOISTURE = {COLUMN_CURRENT_MOISTURE, COLUMN_OPTIMAL_MOISTURE};
    public static final String[] COLUMNS_TEMP = {COLUMN_CURRENT_TEMP, COLUMN_OPTIMAL_TEMP};
    public static final String[] COLUMNS_ALL_BUT_CURRENT = {COLUMN_PLANT_NAME, COLUMN_PLANT_SPECIES,
            COLUMN_OPTIMAL_LIGHT, COLUMN_OPTIMAL_MOISTURE, COLUMN_OPTIMAL_TEMP, COLUMN_GPIO_LIGHT, COLUMN_GPIO_MOISTURE, COLUMN_GPIO_TEMP
    };
//


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PLANTS + "(" +
                COLUMN_PLANT_NAME  + " TEXT PRIMARY KEY, " +
                COLUMN_PLANT_SPECIES   + " TEXT, " +
                COLUMN_CURRENT_LIGHT  + " REAL, " +
                COLUMN_CURRENT_MOISTURE  + " REAL, " +
                COLUMN_CURRENT_TEMP  + " REAL, " +
                COLUMN_OPTIMAL_LIGHT  + " REAL, " +
                COLUMN_OPTIMAL_MOISTURE + " REAL, " +
                COLUMN_OPTIMAL_TEMP + " REAL, " +
                COLUMN_GPIO_LIGHT + " REAL, " +
                COLUMN_GPIO_MOISTURE + " REAL, " +
                COLUMN_GPIO_TEMP + " REAL " +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_PLANTS);
        onCreate(db);
    }

    public void setGPIO(double lightGPIO, double moistureGPIO, double tempGPIO){
        ContentValues values = new ContentValues();
        values.put(COLUMN_GPIO_MOISTURE, moistureGPIO);
        values.put(COLUMN_GPIO_LIGHT, lightGPIO);
        values.put(COLUMN_GPIO_TEMP, tempGPIO);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SETTINGS, null, values);
        db.close();
    }

    // Add a new row to the database
    public void addPlant(Plant plant){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_NAME, plant.getPlantName());
        values.put(COLUMN_PLANT_SPECIES, plant.getPlantSpecies());
        double optimalLight = plant.getLightFrag().getStat().getOptimalLevel();
        double optimalMoisture = plant.getMoistureFrag().getStat().getOptimalLevel();
        double optimalTemp = plant.getTempFrag().getStat().getOptimalLevel();
        double lightGPIO = plant.getLightGPIO();
        double moistureGPIO = plant.getMoistureGPIO();
        double tempGPIO = plant.getTempGPIO();
        values.put(COLUMN_OPTIMAL_LIGHT, optimalLight);
        values.put(COLUMN_OPTIMAL_MOISTURE, optimalMoisture);
        values.put(COLUMN_OPTIMAL_TEMP, optimalTemp);
        values.put(COLUMN_GPIO_LIGHT, lightGPIO);
        values.put(COLUMN_GPIO_MOISTURE, moistureGPIO);
        values.put(COLUMN_GPIO_TEMP, tempGPIO);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLANTS, null, values);
        db.close();
    }

    // Delete a plant from the database
    public void deletePlant(String plantName){
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("DELETE FROM " + TABLE_PLANTS +" WHERE " + COLUMN_PLANT_NAME + "=\""
        //+ plantName + "\";");
        String whereClause = COLUMN_PLANT_NAME + "=?";
        String[] whereArgs = {plantName};
        db.delete(TABLE_PLANTS, whereClause, whereArgs);
        db.close();
    }

    public Plant getPlant(String plantName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        Plant resultPlant = null;
        c = db.rawQuery("SELECT * FROM " + TABLE_PLANTS + "WHERE " + COLUMN_PLANT_NAME + "=?", new String[]{plantName});
        if (c.getCount() > 0){
            c.moveToFirst();
            String plantSpecies = c.getString(c.getColumnIndex(COLUMN_PLANT_SPECIES));
            Plant plant = new Plant(plantName, plantSpecies);
            double lightGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_LIGHT));
            double moistureGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_MOISTURE));
            double tempGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_TEMP));
            PlantStat lightStat = plant.getLightFrag().getStat();
            double optimalLight = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_LIGHT));
            lightStat.setOptimalLevel(optimalLight);

            PlantStat moistureStat = plant.getMoistureFrag().getStat();
            double optimalMoisture = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_MOISTURE));
            moistureStat.setOptimalLevel(optimalMoisture);

            PlantStat tempStat = plant.getTempFrag().getStat();
            double optimalTemp = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_TEMP));
            moistureStat.setOptimalLevel(optimalTemp);
        }
        return resultPlant;
    }

    // Print out the database as a String
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANTS + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();


        /**
         * This method runs infinitely. Fix it.
         */
        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_PLANT_NAME)) != null){
                dbString += c.getString(c.getColumnIndex("plant_name"));
                /**
                 * Add toString of other columns
                 */
                dbString += "\n";
            }
        }
        db.close();
        return dbString;
    }

    public boolean isEmpty(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PLANTS, COLUMNS_ALL_BUT_CURRENT, null, null, null, null, null, null);
        return (c.getCount() == 0);
    }

    public ArrayList<String> getPlantNames(){
        ArrayList<String> plantNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLANTS,new String[]{COLUMN_PLANT_NAME},null, null, null, null, null, null);
        cursor.moveToFirst();
        int i =0;
        while(!cursor.isAfterLast()){
            plantNames.add(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return  plantNames;

    }

    public void deleteItem(){

    }

    /**
     * Retrieves a plant by its name from the db,
     * and constructs a Plant object for it, including its
     * LMT levels.
     * @param
     * @return The Plant object that is constructed.
     */
    public ArrayList<Plant> makePlants(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PLANTS, COLUMNS_ALL_BUT_CURRENT, null, null, null, null, null, null);
        c.moveToFirst();
        ArrayList<Plant> plantsList = new ArrayList<>();

        if(c.getCount() > 0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                String plantName = c.getString(c.getColumnIndex(COLUMN_PLANT_NAME));
                String plantSpecies = c.getString(c.getColumnIndex(COLUMN_PLANT_SPECIES));
                Plant plant = new Plant(plantName, plantSpecies);
                double lightGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_LIGHT));
                double moistureGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_MOISTURE));
                double tempGPIO = c.getDouble(c.getColumnIndex(COLUMN_GPIO_TEMP));
                plant.setLightGPIO(lightGPIO);
                plant.setMoistureGPIO(moistureGPIO);
                plant.setTempGPIO(tempGPIO);

                PlantStat lightStat = plant.getLightFrag().getStat();
                //double currentLight = c.getDouble(c.getColumnIndex(COLUMN_CURRENT_LIGHT));
                double optimalLight = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_LIGHT));
                //lightStat.setCurrentLevel(currentLight);
                lightStat.setOptimalLevel(optimalLight);

                PlantStat moistureStat = plant.getMoistureFrag().getStat();
               // double currentMoisture = c.getDouble(c.getColumnIndex(COLUMN_CURRENT_MOISTURE));
                double optimalMoisture = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_MOISTURE));
                //moistureStat.setCurrentLevel(currentMoisture);
                moistureStat.setOptimalLevel(optimalMoisture);

                PlantStat tempStat = plant.getTempFrag().getStat();
               // double currentTemp = c.getDouble(c.getColumnIndex(COLUMN_CURRENT_TEMP));
                double optimalTemp = c.getDouble(c.getColumnIndex(COLUMN_OPTIMAL_TEMP));
                // tempStat.setCurrentLevel(currentTemp);
                moistureStat.setOptimalLevel(optimalTemp);

                plantsList.add(plant);

                c.moveToNext();
            }
        }
        c.close();
        db.close();
        return plantsList;
    }



    /**
    public PlantStat makePlantStat(double currentLevel, double optimalLevel){
        PlantStat stat = new PlantStat(currentLevel, optimalLevel, ""); // Last parameter should not really be ""
    }
     **/



}
