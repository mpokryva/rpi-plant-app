package com.android.miki.rpiplantsapp;

/**
 * Stores a settings configuration. Different fields represent different settings.
 */

public class SettingsConfig {

    private Double refreshRate;
    private Boolean isFahrenheit;
    //private String tempUnit;
   // private static Double REFRESH_RATE_DEFAULT = 1.0;
    //private static String TEMP_UNIT_DEFAULT = "Fahrenheit";


    public SettingsConfig(){

    }

    /**
    public SettingsConfig(Double refreshRate, String tempUnit){
        if (TempUnit.contains(tempUnit)){
            //this.tempUnit = tempUnit;
        }
        this.refreshRate = refreshRate;
    }
     **/

    public SettingsConfig(Double refreshRate, Boolean isFahrenheit){
        this.refreshRate = refreshRate;
        this.isFahrenheit = isFahrenheit;
    }

    public Double getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(Double refreshRate) {
        this.refreshRate = refreshRate;
    }

    /**
    public String getTempUnit() {
        return tempUnit;
    }

    public void setTempUnit(String tempUnit) {
        this.tempUnit = tempUnit;
    }
     **/

    public Boolean isFahrenheit(){
        return isFahrenheit;
    }

    public void setIsFahrenheit(Boolean isFahrenheit){
        this.isFahrenheit = isFahrenheit;
    }



    /**
    private enum TempUnit {

        CELSIUS("Celsius"), FAHRENHEIT("Fahrenheit");
        private String tempUnit;

        TempUnit(String tempUnit){
            this.tempUnit = tempUnit;
        }

        String getUnit(){
            return tempUnit;
        }
        static boolean contains(String unitToCompare){
            for(TempUnit unit : TempUnit.values()){
                if (unit.getUnit().equals(unitToCompare))
                        return true;
            }
            return false;
        }

    }
     **/


}
