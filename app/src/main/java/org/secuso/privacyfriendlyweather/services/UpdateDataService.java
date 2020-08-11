package org.secuso.privacyfriendlyweather.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecastWidget;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForUpdatingCityList;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForWidgetUpdate;
import org.secuso.privacyfriendlyweather.widget.WeatherWidget;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetFiveDayForecast;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetThreeDayForecast;

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
    public static final String UPDATE_WIDGET_ACTION = "org.secuso.privacyfriendlyweather.services.UpdateDataService.UPDATE_WIDGET_ACTION";

    public static final String CITY_ID = "cityId";
    public static final String SKIP_UPDATE_INTERVAL= "skipUpdateInterval";

    private PFASQLiteHelper dbHelper;
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
        dbHelper = PFASQLiteHelper.getInstance(getApplicationContext());
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

        if(intent != null) {
            if      (UPDATE_ALL_ACTION.equals(intent.getAction()))              handleUpdateAll(intent);
            else if (UPDATE_CURRENT_WEATHER_ACTION.equals(intent.getAction()))  handleUpdateCurrentWeatherAction(intent);
            else if (UPDATE_FORECAST_ACTION.equals(intent.getAction()))         handleUpdateForecastAction(intent);
            else if (UPDATE_WIDGET_ACTION.equals(intent.getAction())) handleWidgetUpdate(intent);
        }
    }

    private void handleWidgetUpdate(Intent intent) {

        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        int widgetType = intent.getIntExtra("widget_type", 0);
        Log.d("devtag", "widgetUpdate: type " + widgetType + " id: " + widgetId);

        if (widgetId > -1 && widgetType > 0) {

            //initialize depending on widget type
            RemoteViews views;
            SharedPreferences prefs;
            // Construct the RemoteViews object
            if (widgetType == 1) {
                views = new RemoteViews(getBaseContext().getPackageName(), R.layout.weather_widget);
                prefs = getBaseContext().getSharedPreferences(WeatherWidget.PREFS_NAME, 0);
            } else if (widgetType == 3) {
                views = new RemoteViews(getBaseContext().getPackageName(), R.layout.weather_3day_widget);
                prefs = getBaseContext().getSharedPreferences(WeatherWidgetThreeDayForecast.PREFS_NAME, 0);
            } else {
                views = new RemoteViews(getBaseContext().getPackageName(), R.layout.weather_5day_widget);
                prefs = getBaseContext().getSharedPreferences(WeatherWidgetFiveDayForecast.PREFS_NAME, 0);
            }

            int cityId = prefs.getInt(WeatherWidget.PREF_PREFIX_KEY + widgetId, -1);
            if (cityId == -1) {
                Log.d("debug", "cityId is null?");
                return;
            }

            //Widget update code
            IHttpRequestForForecastWidget forecastRequestWidget = new OwmHttpRequestForWidgetUpdate(getApplicationContext());
            forecastRequestWidget.perform(cityId, widgetId, widgetType, views);

        }
    }

    /**
     * Be careful, with using this. It can cause many calls to the API, because it wants to update everything if the update interval allows it.
     * @param intent contains necessary parameters for the service work
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

        // TODO: 07.0

        long timestamp = 0;
        long systemTime = System.currentTimeMillis() / 1000;
        long updateInterval = 2*60*60;

        if (!skipUpdateInterval) {
            // check timestamp of the current forecasts
            List<Forecast> forecasts = dbHelper.getForecastsByCityId(cityId);
            if (forecasts.size() > 0) {
                timestamp = forecasts.get(0).getTimestamp();
            }


            updateInterval = Long.parseLong(prefManager.getString("pref_updateInterval", "2")) * 60 * 60;
        }

        // only Update if a certain time has passed
        if (skipUpdateInterval || timestamp + updateInterval - systemTime <= 0) {
            IHttpRequestForForecast forecastRequest = new OwmHttpRequestForForecast(getApplicationContext());
            forecastRequest.perform(cityId);
        }
    }

    private boolean isOnline() {
        try {
            InetAddress inetAddress = InetAddress.getByName("api.openweathermap.org");
            return inetAddress.isReachable(2000);
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    private void handleUpdateForecastAction(Intent intent) {
        int cityId = intent.getIntExtra(CITY_ID, -1);
        handleUpdateForecastAction(intent, cityId);
    }

    private void handleUpdateCurrentWeatherAction(Intent intent) {
        boolean skipUpdateInterval = intent.getBooleanExtra(SKIP_UPDATE_INTERVAL, false);

        long systemTime = System.currentTimeMillis() / 1000;
        boolean shouldUpdate = false;

        if (!skipUpdateInterval) {
            long updateInterval = Long.parseLong(prefManager.getString("pref_updateInterval", "2")) * 60 * 60;

            List<CityToWatch> citiesToWatch = dbHelper.getAllCitiesToWatch();
            // check timestamp of the current weather .. if one of them is out of date.. update them all at once
            List<CurrentWeatherData> weather = dbHelper.getAllCurrentWeathers();

            for(CityToWatch city : citiesToWatch) {
                int cityId = city.getCityId();
                boolean foundId = false;
                for (CurrentWeatherData w : weather) {
                    if(w.getCity_id() == cityId) {

                        foundId = true;

                        long timestamp = w.getTimestamp();
                        if (timestamp + updateInterval - systemTime <= 0) {
                            shouldUpdate = true;
                            break;
                        }
                    }
                }
                if(shouldUpdate || !foundId) {
                    shouldUpdate = true;
                    break;
                }
            }
        }

        if (skipUpdateInterval || shouldUpdate) {
            IHttpRequestForCityList currentWeatherRequest = new OwmHttpRequestForUpdatingCityList(getApplicationContext());
            List<CityToWatch> cityToWatches = dbHelper.getAllCitiesToWatch();
            currentWeatherRequest.perform(cityToWatches);
        }
    }
}
