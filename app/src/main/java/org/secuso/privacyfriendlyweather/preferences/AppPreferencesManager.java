package org.secuso.privacyfriendlyweather.preferences;

import android.content.SharedPreferences;

/**
 * This class provides access and methods for relevant preferences.
 */
public class AppPreferencesManager {

    /**
     * Constants
     */
    public static final String PREFERENCES_NAME = "org.secuso.privacyfriendlyweather.preferences";
    private static final String PREFERENCES_IS_FIRST_START = "is_first_app_start";

    /**
     * Member variables
     */
    SharedPreferences preferences;

    /**
     * Constructor.
     *
     * @param preferences Source for the preferences to use.
     */
    public AppPreferencesManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    private void setFirstAppStartToFalse() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_IS_FIRST_START, false);
        editor.apply();
    }

    /**
     * @return Returns true if the app is started for the very first time else false.
     */
    public boolean isFirstAppStart() {
        boolean isFirstStart = preferences.getBoolean(PREFERENCES_IS_FIRST_START, true);
        if (isFirstStart) {
            setFirstAppStartToFalse();
        }
        return isFirstStart;
    }

    /**
     * This method converts a given temperature value into the unit that was set in the preferences.
     *
     * @param temperature The temperature to convert into the unit that is set in the preferences.
     *                    Make sure to pass a value in celsius.
     * @return Returns the converted temperature.
     */
    public float convertTemperatureFromCelsius(float temperature) {
        // 1 = Celsius (fallback), 2 = Fahrenheit
        int prefValue = Integer.parseInt(preferences.getString("temperatureUnit", "1"));
        if (prefValue == 1) {
            return temperature;
        } else {
            return (((temperature * 9) / 5) + 32);
        }
    }

    /**
     * This method converts a given distance value into the unit that was set in the preferences.
     *
     * @param kilometers The kilometers to convert into the unit that is set in the preferences.
     *                   Make sure to pass a value in kilometers.
     * @return Returns the converted distance.
     */
    public float convertDistanceFromKilometers(float kilometers) {
        // 1 = kilometers, 2 = miles
        int prefValue = Integer.parseInt(preferences.getString("distanceUnit", "1"));
        if (prefValue == 1) {
            return kilometers;
        } else {
            return convertKmInMiles(kilometers);
        }
    }

    /**
     * @return Returns true if kilometers was set as distance unit in the preferences else false.
     */
    public boolean isDistanceUnitKilometers() {
        int prefValue = Integer.parseInt(preferences.getString("distanceUnit", "0"));
        return (prefValue == 1);
    }

    /**
     * @return Returns true if miles was set as distance unit in the preferences else false.
     */
    public boolean isDistanceUnitMiles() {
        int prefValue = Integer.parseInt(preferences.getString("distanceUnit", "0"));
        return (prefValue == 2);
    }

    /**
     * Converts a kilometer value in miles.
     *
     * @param km The value to convert to miles.
     * @return Returns the converted value.
     */
    public float convertKmInMiles(float km) {
        // TODO: Is this the right class for the function???
        return (float) (km / 1.609344);
    }

    /**
     * Converts a miles value in kilometers.
     *
     * @param miles The value to convert to kilometers.
     * @return Returns the converted value.
     */
    public float convertMilesInKm(float miles) {
        // TODO: Is this the right class for the function???
        return (float) (miles * 1.609344);
    }

    /**
     * @return Returns "°C" in case Celsius is set and "°F" if Fahrenheit was selected.
     */
    public String getWeatherUnit() {
        int prefValue = Integer.parseInt(preferences.getString("temperatureUnit", "1"));
        if (prefValue == 1) {
            return "°C";
        } else {
            return "°F";
        }
    }

    /**
     * @return Returns "km" in case kilometer is set and "mi" if miles was selected.
     */
    public String getDistanceUnit() {
        int prefValue = Integer.parseInt(preferences.getString("distanceUnit", "1"));
        if (prefValue == 1) {
            return "km";
        } else {
            return "mi";
        }
    }

}
