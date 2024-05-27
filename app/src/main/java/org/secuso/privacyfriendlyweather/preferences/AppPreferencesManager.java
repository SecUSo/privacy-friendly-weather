package org.secuso.privacyfriendlyweather.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import kotlin.random.Random;

/**
 * This class provides access and methods for relevant preferences.
 */
public class AppPreferencesManager {

    /**
     * Constants
     */
    public static final String PREFERENCES_NAME = "org.secuso.privacyfriendlyweather.preferences";
    public static final String OLD_PREF_NAME = "weather-Preference";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String ASKED_FOR_OWM_KEY = "AskedForOWMKey";

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


    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setAskedForOwmKey(boolean asked) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ASKED_FOR_OWM_KEY, asked);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean askedForOWMKey() {
        return preferences.getBoolean(ASKED_FOR_OWM_KEY, false);
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

    public boolean is3hourForecastSet() {
        return 2 == Integer.parseInt(preferences.getString("forecastChoice", "1"));
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

    public void setThemeChoice(int i) {
        int choice;
        if (i == 0) {
            choice = Integer.parseInt(preferences.getString("themeChoice", "1"));
        } else {
            choice = i;
        }

        switch (choice) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 3:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    /**
     * @return Returns speed unit abbreviations
     */
    public String getSpeedUnit() {
        int prefValue = Integer.parseInt(preferences.getString("speedUnit", "6"));
        if (prefValue == 1) {
            return "km/h";
        } else if (prefValue == 2) {
            return "m/s";
        } else if (prefValue == 3) {
            return "mph";
        } else if (prefValue == 4) {
            return "ft/s";
        } else if (prefValue == 5) {
            return "kn";
        } else {
            return "Bft";
        }
    }

    public String convertToCurrentSpeedUnit(float speedInMetersPerSecond) {
        int prefValue = Integer.parseInt(preferences.getString("speedUnit", "6"));
        if (prefValue == 1) {
            return StringFormatUtils.formatDecimal(speedInMetersPerSecond * 3.6f, "km/h");
        } else if (prefValue == 2) {
            return StringFormatUtils.formatDecimal(speedInMetersPerSecond, "m/s");
        } else if (prefValue == 3) {
            return StringFormatUtils.formatDecimal(speedInMetersPerSecond * 2.23694f, "mph");
        } else if (prefValue == 4) {
            return StringFormatUtils.formatDecimal(speedInMetersPerSecond * 3.28084f, "ft/s");
        } else if (prefValue == 5) {
            return StringFormatUtils.formatDecimal(speedInMetersPerSecond * 1.94384f, "kn");
        } else {
            return StringFormatUtils.formatWindToBeaufort(speedInMetersPerSecond);
        }
    }

    public int get1dayWidgetInfo() {
        return Integer.parseInt(preferences.getString("widgetChoice4", "1"));
    }

    public int get5dayWidgetInfo() {
        return Integer.parseInt(preferences.getString("widgetChoice1", "1"));
    }

    public int get3dayWidgetInfo1() {
        return Integer.parseInt(preferences.getString("widgetChoice2", "1"));
    }

    public int get3dayWidgetInfo2() {
        return Integer.parseInt(preferences.getString("widgetChoice3", "2"));
    }

    public boolean usingPersonalKey(Context context) {
        String prefValue = preferences.getString("API_key_value", context.getString(R.string.settings_API_key_default));
        return !prefValue.equals(context.getString(R.string.settings_API_key_default));
    }

    public String getOWMApiKey(Context context) {
        return preferences.getString("API_key_value", context.getString(R.string.settings_API_key_default));
    }
}
