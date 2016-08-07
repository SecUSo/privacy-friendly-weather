package org.secuso.privacyfriendlyweather.services;

import android.app.IntentService;
import android.content.Intent;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.OwmHttpRequestForForecast;

/**
 * This class provides the functionality to fetch forecast data for a given city as a background
 * task.
 */
public class FetchForecastDataService extends IntentService {

    /**
     * Constructor.
     */
    public FetchForecastDataService() {
        super("fetch-forecast-data-service");
    }

    /**
     * @see IntentService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    /**
     * @see IntentService#onHandleIntent(Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        IHttpRequestForForecast forecastRequest = new OwmHttpRequestForForecast(getApplicationContext(), dbHelper);
        forecastRequest.perform(intent.getIntExtra("cityId", -1));
    }

}
