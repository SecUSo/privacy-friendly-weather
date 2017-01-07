package org.secuso.privacyfriendlyweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForUpdatingCityList;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This class provides the functionality to fetch forecast data for a given city as a background
 * task.
 */
public class UpdateDataService extends IntentService {

    public static final String UPDATE_CURRENT_WEATHER_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_CURRENT_WEATHER_ACTION";
    public static final String UPDATE_FORECAST_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_FORECAST_ACTION";
    public static final String UPDATE_ALL_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_ALL_ACTION";

    public static final String CITY_ID = "cityId";
    public static final String SKIP_UPDATE_INTERVAL= "skipUpdateInterval";

    private PFASQLiteHelper dbHelper;
    private SharedPreferences prefManager;

    /**
     * Constructor.
     */
    public UpdateDataService() {
        super("fetch-forecast-data-service");
    }

    /**
     * @see IntentService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = PFASQLiteHelper.getInstance(getApplicationContext());
        prefManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
    
    /**
     * @see IntentService#onHandleIntent(Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null) {
            if      (UPDATE_ALL_ACTION.equals(intent.getAction()))              handleUpdateAll(intent);
            else if (UPDATE_CURRENT_WEATHER_ACTION.equals(intent.getAction()))  handleUpdateCurrentWeatherAction(intent);
            else if (UPDATE_FORECAST_ACTION.equals(intent.getAction()))         handleUpdateForecastAction(intent);
        }
    }

    /**
     * Be careful, with using this. It can cause many calls to the API, because it wants to update everything if the update interval allows it.
     * @param intent
     */
    private void handleUpdateAll(Intent intent) {
        handleUpdateCurrentWeatherAction(intent);
        List<CityToWatch> cities = dbHelper.getAllCitiesToWatch();
        for(CityToWatch c : cities) {
            handleUpdateForecastAction(intent, c.getCityId());
        }
    }

    private void handleUpdateForecastAction(Intent intent, int cityId) {
        boolean skipUpdateInterval = intent.getBooleanExtra(SKIP_UPDATE_INTERVAL, false);

        long timestamp = 0;
        long systemTime = System.currentTimeMillis() / 1000;
        long updateInterval = 30*60;

        if (!skipUpdateInterval) {
            // check timestamp of the current forecasts
            List<Forecast> forecasts = dbHelper.getForecastsByCityId(cityId);
            if (forecasts.size() > 0) {
                timestamp = forecasts.get(0).getTimestamp();
            }

            // TODO: insert this into the settings page to make it changeable
            updateInterval = prefManager.getLong("pref_updateInterval", 30*60);
        }

        // only Update if a certain time has passed
        if (skipUpdateInterval || timestamp + updateInterval - systemTime <= 0) {
            IHttpRequestForForecast forecastRequest = new OwmHttpRequestForForecast(getApplicationContext());
            forecastRequest.perform(cityId);
        }
    }

    private void handleUpdateForecastAction(Intent intent) {
        int cityId = intent.getIntExtra(CITY_ID, -1);
        handleUpdateForecastAction(intent, cityId);
    }

    private void handleUpdateCurrentWeatherAction(Intent intent) {
        boolean skipUpdateInterval = intent.getBooleanExtra(SKIP_UPDATE_INTERVAL, false);

        long timestamp = 0;
        long systemTime = System.currentTimeMillis() / 1000;
        long updateInterval = 30*60;

        if (!skipUpdateInterval) {
            // check timestamp of the current weather
            List<CurrentWeatherData> weather = dbHelper.getAllCurrentWeathers();
            if (weather.size() > 0) {
                timestamp = weather.get(0).getTimestamp();
            }

            updateInterval = prefManager.getLong("pref_updateInterval", 30*60);
        }

        if (skipUpdateInterval || timestamp + updateInterval - systemTime <= 0) {
            IHttpRequestForCityList currentWeatherRequest = new OwmHttpRequestForUpdatingCityList(getApplicationContext());
            List<CityToWatch> cityToWatches = dbHelper.getAllCitiesToWatch();
            currentWeatherRequest.perform(cityToWatches);
        }
    }
}
