package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.BuildConfig;

import java.util.prefs.PreferenceChangeListener;

/**
 * This singleton class contains connection data for the OpenWeatherMap API.
 */
public class OwmApiData {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    private static String API_KEY = BuildConfig.DEFAULT_API_KEY;

    public static void setAPI_KEY(String key) {
        API_KEY = key;
    }
    public static void resetAPI_KEY() {
        API_KEY = BuildConfig.DEFAULT_API_KEY;
    }

    private OwmApiData() {}


    public static String getAPI_KEY() {
        return API_KEY;
    }



}
