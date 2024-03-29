package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;
import org.secuso.privacyfriendlyweather.widget.WeatherWidget;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetFiveDayForecast;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetThreeDayForecast;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmUpdateCityListRequest implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_update_list";

    /**
     * Member variables
     */
    private Context context;
    private AppDatabase dbHelper;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    public ProcessOwmUpdateCityListRequest(Context context) {
        this.context = context;
        this.dbHelper = AppDatabase.getInstance(context);
    }

    /**
     * Converts the response to JSON and updates the database so that the latest weather data are
     * displayed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        IDataExtractor extractor = new OwmDataExtractor();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray list = json.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                String currentItem = list.get(i).toString();
                CurrentWeatherData weatherData = extractor.extractCurrentWeatherData(currentItem);
                int cityId = extractor.extractCityID(currentItem);
                // Data were not well-formed, abort
                if (weatherData == null || cityId == Integer.MIN_VALUE) {
                    final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                    Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                    return;
                }
                // Could retrieve all data, so proceed
                else {
                    weatherData.setCity_id(cityId);

                    dbHelper.currentWeatherDao().deleteCurrentWeatherByCityId(cityId);

                    dbHelper.currentWeatherDao().addCurrentWeather(weatherData);

                    ViewUpdater.updateCurrentWeatherData(weatherData);
                }
            }

            //Update Widgets
            AppWidgetManager awm = AppWidgetManager.getInstance(context);

            int[] ids1 = awm.getAppWidgetIds(new ComponentName(context, WeatherWidget.class));
            int[] ids3 = awm.getAppWidgetIds(new ComponentName(context, WeatherWidgetThreeDayForecast.class));
            int[] ids5 = awm.getAppWidgetIds(new ComponentName(context, WeatherWidgetFiveDayForecast.class));

            Intent intent1 = new Intent(context, WeatherWidget.class);
            Intent intent3 = new Intent(context, WeatherWidgetThreeDayForecast.class);
            Intent intent5 = new Intent(context, WeatherWidgetFiveDayForecast.class);

            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent3.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent5.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids1);
            intent3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids3);
            intent5.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids5);

            context.sendBroadcast(intent1);
            context.sendBroadcast(intent3);
            context.sendBroadcast(intent5);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO: Error Handling
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(final VolleyError error) {
        Handler h = new Handler(this.context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getResources().getString(R.string.error_fetch_cityList), Toast.LENGTH_LONG).show();
            }
        });
    }

}
