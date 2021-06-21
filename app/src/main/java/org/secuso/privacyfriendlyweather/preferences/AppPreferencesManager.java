package org.secuso.privacyfriendlyweather.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;

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

    public boolean getShowWidgetBackground() {
        return preferences.getBoolean("showWidgetBackground", true);
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
        String noKeyString = context.getString(R.string.settings_API_key_default);
        String prefValue = preferences.getString("API_key_value", noKeyString);
        if (!prefValue.equals(noKeyString)) {
            return prefValue;
        } else {
            int keyIndex = preferences.getInt("last_used_key", 1);
            SharedPreferences.Editor editor = preferences.edit();
            switch (keyIndex) {
                case 1:
                    editor.putInt("last_used_key", 2);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY2;
                case 2:
                    editor.putInt("last_used_key", 3);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY3;
                case 3:
                    editor.putInt("last_used_key", 4);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY4;
                case 4:
                    editor.putInt("last_used_key", 5);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY5;
                case 5:
                    editor.putInt("last_used_key", 6);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY6;
                case 6:
                    editor.putInt("last_used_key", 7);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY7;
                case 7:
                    editor.putInt("last_used_key", 8);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY8;
                case 8:
                    editor.putInt("last_used_key", 9);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY9;
                case 9:
                    editor.putInt("last_used_key", 10);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY10;
                case 10:
                    editor.putInt("last_used_key", 11);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY11;
                case 11:
                    editor.putInt("last_used_key", 12);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY12;
                case 12:
                    editor.putInt("last_used_key", 13);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY13;
                case 13:
                    editor.putInt("last_used_key", 14);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY14;
                case 14:
                    editor.putInt("last_used_key", 15);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY15;
                case 15:
                    editor.putInt("last_used_key", 16);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY16;
                case 16:
                    editor.putInt("last_used_key", 17);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY17;
                case 17:
                    editor.putInt("last_used_key", 18);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY18;
                case 18:
                    editor.putInt("last_used_key", 19);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY19;
                case 19:
                    editor.putInt("last_used_key", 20);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY20;
                default:
                    editor.putInt("last_used_key", 1);
                    editor.commit();
                    return BuildConfig.DEFAULT_API_KEY1;
            }
        }

    }


}
