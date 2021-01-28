package org.secuso.privacyfriendlyweather.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.JobIntentService;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForOneCallAPI;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForOneCallAPI;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * This class provides the functionality to fetch forecast data for a given city as a background
 * task.
 */
public class UpdateDataService extends JobIntentService {

    public static final String UPDATE_CURRENT_WEATHER_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_CURRENT_WEATHER_ACTION";
    public static final String UPDATE_FORECAST_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_FORECAST_ACTION";
    public static final String UPDATE_ALL_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_ALL_ACTION";
    public static final String UPDATE_SINGLE_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_SINGLE_ACTION";

    public static final String CITY_ID = "cityId";
    public static final String SKIP_UPDATE_INTERVAL = "skipUpdateInterval";

    private AppDatabase dbHelper;
    private SharedPreferences prefManager;

    /**
     * Constructor.
     */
    public UpdateDataService() {
        super();
    }

    /**
     * @see IntentService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = AppDatabase.getInstance(getApplicationContext());
        prefManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    /**
     *
     */
    @Override
    protected void onHandleWork(Intent intent) {
        if (!isOnline()) {
            Handler h = new Handler(getApplicationContext().getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        if (intent != null) {
            if (UPDATE_ALL_ACTION.equals(intent.getAction())) handleUpdateAll(intent);
            else if (UPDATE_FORECAST_ACTION.equals(intent.getAction()))
                handleUpdateForecastAction(intent);
            else if (UPDATE_SINGLE_ACTION.equals(intent.getAction())) handleUpdateSingle(intent);
        }
    }


    /**
     * Be careful, with using this. It can cause many calls to the API, because it wants to update everything if the update interval allows it.
     *
     * @param intent contains necessary parameters for the service work
     */
    private void handleUpdateAll(Intent intent) {
        //  handleUpdateCurrentWeatherAction(intent); //TODO: remove, now done via one call api
        List<CityToWatch> cities = dbHelper.cityToWatchDao().getAll();
        for (CityToWatch c : cities) {
            handleUpdateForecastAction(intent, c.getCityId(), c.getLatitude(), c.getLongitude());
        }
    }

    private void handleUpdateSingle(Intent intent) {
        int cityId = intent.getIntExtra("cityId", -1);
        City city = dbHelper.cityDao().getCityById(cityId);
        if (cityId == -1 || city == null) {
            Log.d("city null", cityId + " " + city);
            return;
        }
        handleUpdateForecastAction(intent, cityId, city.getLatitude(), city.getLongitude());
    }

    private void handleUpdateForecastAction(Intent intent, int cityId, float lat, float lon) {
        boolean skipUpdateInterval = intent.getBooleanExtra(SKIP_UPDATE_INTERVAL, false);

        // TODO: 07.0

        long timestamp = 0;
        long systemTime = System.currentTimeMillis() / 1000;
        long updateInterval = 2 * 60 * 60;

        if (!skipUpdateInterval) {
            // check timestamp of the current forecasts
            List<Forecast> forecasts = dbHelper.forecastDao().getForecastsByCityId(cityId);
            if (forecasts.size() > 0) {
                timestamp = forecasts.get(0).getTimestamp();
            }


            updateInterval = Long.parseLong(prefManager.getString("pref_updateInterval", "2")) * 60 * 60;
        }

        // only Update if a certain time has passed
        if (skipUpdateInterval || timestamp + updateInterval - systemTime <= 0) {
            //if forecastChoice = 1 (3h) perform both else only one call API
            int choice = Integer.parseInt(prefManager.getString("forecastChoice", "1"));
            if (choice == 2) {
                IHttpRequestForForecast forecastRequest = new OwmHttpRequestForForecast(getApplicationContext());
                forecastRequest.perform(cityId);
            }
            if (oneCallAllowed(getApplicationContext())) {
                IHttpRequestForOneCallAPI forecastOneCallRequest = new OwmHttpRequestForOneCallAPI(getApplicationContext());
                forecastOneCallRequest.perform(lat, lon);
            }
            //TODO add alternative retrieval with CurrentWeatherData API

        }
    }

    private boolean oneCallAllowed(Context context) {
        AppPreferencesManager appPreferences = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        if (appPreferences.usingPersonalKey(context)) {
            Log.d("oneCallAllowed", "personalKey");
            return true;
        }
        //if shared key is used, execute limiting code
        SharedPreferences.Editor editor = prefManager.edit();
        int currentCalls = prefManager.getInt("shared_calls_used", 0);
        if (currentCalls == 0) {
            editor.putLong("shared_calls_count_start", System.currentTimeMillis());
            Log.d("oneCallAllowed", "no calls yet");

        }
        if (currentCalls < 10) {
            editor.putInt("shared_calls_used", currentCalls + 1);
            editor.commit();
            Log.d("oneCallAllowed", "under 10 calls" + currentCalls);
            return true;
            // if calls reached but day since first call elapsed
        } else if (prefManager.getLong("shared_calls_count_start", 0) + 86400000 < System.currentTimeMillis()) {
            editor.putInt("shared_calls_used", 1);
            editor.putLong("shared_calls_count_start", System.currentTimeMillis());
            editor.commit();
            Log.d("oneCallAllowed", "day passed");
            return true;
            // if 10 calls in the last 24 hours used
        } else {
            Handler h = new Handler(getApplicationContext().getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.one_call_limit), Toast.LENGTH_SHORT).show();
                }
            });
            ViewUpdater.abortUpdate();
            Log.d("oneCallAllowed", "too many calls");

            return false;
        }
    }


    private boolean isOnline() {
        try {
            InetAddress inetAddress = InetAddress.getByName("api.openweathermap.org");
            return !inetAddress.equals("");
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    private void handleUpdateForecastAction(Intent intent) {
        int cityId = intent.getIntExtra(CITY_ID, -1);
        float lat = 0;
        float lon = 0;
        //get lat lon for cityID
        List<CityToWatch> citiesToWatch = dbHelper.cityToWatchDao().getAll();
        for (int i = 0; i < citiesToWatch.size(); i++) {
            CityToWatch city = citiesToWatch.get(i);
            if (city.getCityId() == cityId) {
                lat = city.getLatitude();
                lon = city.getLongitude();
                break;
            }
        }
        handleUpdateForecastAction(intent, cityId, lat, lon);
    }
}
