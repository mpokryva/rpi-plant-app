package com.android.miki.rpiplantsapp;

/**
 * Created by Miki on 10/5/2016.
 */
public interface TempUnit {
    double convertUnit(double value);
    String getTempUnit();

    class Celsius implements TempUnit {
        private static final String CELSIUS_UNIT = "°C";
        /**
         * Converts value to Celsius.
         * @param value Value to be converted.
         * @return Converted value.
         */
        public double convertUnit(double value){
            double convertedValue = ((value-32)*5)/9;
            double roundedvalue = Math.round(convertedValue*100.0)/100.0;
            return roundedvalue;
        }

        public String getTempUnit(){
            return CELSIUS_UNIT;
        }
    }

    class Fahrenheit implements TempUnit {
        private static final String FAHRENHEIT_UNIT = "°F";
        /**
         * Converts value to Fahrenheit.
         * @param value Value to be converted.
         * @return Converted value.
         */
        public double convertUnit(double value){
            double convertedValue = ((9*value)/5)+32;
            double roundedvalue = Math.round(convertedValue*100)/100;
            return roundedvalue;
        }

        public String getTempUnit(){
            return FAHRENHEIT_UNIT;
        }
    }


}
