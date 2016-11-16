package com.android.miki.rpiplantsapp;

/**
 * Stores a settings configuration. Different fields represent different settings.
 */

public class SettingsConfig {

    private Double refreshRate;
    private boolean isFahrenheit;

    public SettingsConfig(){

    }

    public SettingsConfig(Double refreshRate, boolean isFahrenheit){
        this.refreshRate = refreshRate;
        this.isFahrenheit = isFahrenheit;
    }

    public Double getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(Double refreshRate) {
        this.refreshRate = refreshRate;
    }


    public boolean getIsFahrenheit(){
        return isFahrenheit;
    }

    public void setIsFahrenheit(boolean isFahrenheit){
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
