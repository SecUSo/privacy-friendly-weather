package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;
import org.secuso.privacyfriendlyweather.widget.WeatherWidget;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetFiveDayForecast;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetThreeDayForecast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmForecastOneCallAPIRequest implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_forecast";

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
    public ProcessOwmForecastOneCallAPIRequest(Context context) {
        this.context = context;
        this.dbHelper = AppDatabase.getInstance(context);
    }

    /**
     * Converts the response to JSON and updates the database. Note that for this method no
     * UI-related operations are performed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        IDataExtractor extractor = new OwmDataExtractor();
        try {
            JSONObject json = new JSONObject(response);
            float lat = (float) json.getDouble("lat");
            float lon = (float) json.getDouble("lon");
            //           Log.d("URL JSON",Float.toString(lat));
            //           Log.d("URL JSON",Float.toString(lon));
            int cityId = 0;
            //get CityID from lat/lon
            //Maybe a risk of rounding problems. Alternative: search closest citytowatch
            List<CityToWatch> citiesToWatch = dbHelper.cityToWatchDao().getAll();
            for (int i = 0; i < citiesToWatch.size(); i++) {
                CityToWatch city = citiesToWatch.get(i);
                //if lat/lon of json response very close to lat/lon in citytowatch
                if ((Math.abs(city.getLatitude() - lat) < 0.01) && (Math.abs(city.getLongitude() - lon) < 0.01)) {
                    cityId = city.getCityId();
                    //                   Log.d("URL CITYID", Integer.toString(cityId));
                    break;
                }
            }

            String rain60min = "no data";
            if (json.has("minutely")) {
                rain60min = "";
                JSONArray listrain = json.getJSONArray("minutely");
                for (int i = 0; i < listrain.length() / 5; i++) {   //evaluate in 5min intervals
                    String currentItem0 = listrain.get(i * 5).toString();
                    String currentItem1 = listrain.get(i * 5 + 1).toString();
                    String currentItem2 = listrain.get(i * 5 + 2).toString();
                    String currentItem3 = listrain.get(i * 5 + 3).toString();
                    String currentItem4 = listrain.get(i * 5 + 4).toString();
                    rain60min += extractor.extractRain60min(currentItem0, currentItem1, currentItem2, currentItem3, currentItem4);
                }
            }

            CurrentWeatherData weatherData = extractor.extractCurrentWeatherDataOneCall(json.getString("current"));

            if (weatherData == null) {
                final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
            } else {
                weatherData.setCity_id(cityId);
                weatherData.setRain60min(rain60min);
                weatherData.setTimeZoneSeconds(json.getInt("timezone_offset"));
                CurrentWeatherData current = dbHelper.currentWeatherDao().getCurrentWeatherByCityId(cityId);
                if (current != null && current.getCity_id() == cityId) {
                    dbHelper.currentWeatherDao().updateCurrentWeather(weatherData);
                } else {
                    dbHelper.currentWeatherDao().addCurrentWeather(weatherData);
                }

                ViewUpdater.updateCurrentWeatherData(weatherData);
            }


            JSONArray listdaily = json.getJSONArray("daily");

            dbHelper.weekForecastDao().deleteWeekForecastsByCityId(cityId);
            List<WeekForecast> weekforecasts = new ArrayList<>();

            for (int i = 0; i < listdaily.length(); i++) {
                String currentItem = listdaily.get(i).toString();
                WeekForecast forecast = extractor.extractWeekForecast(currentItem);
                // Data were not well-formed, abort
                if (forecast == null) {
                    final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                    Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                    return;
                }
                // Could retrieve all data, so proceed
                else {
                    forecast.setCity_id(cityId);
                    // add it to the database
                    dbHelper.weekForecastDao().addWeekForecast(forecast);
                    weekforecasts.add(forecast);
                }
            }

            ViewUpdater.updateWeekForecasts(weekforecasts);
            possiblyUpdateWidgets(cityId, weekforecasts, weatherData);


            //Use hourly data only if forecastChoice 2 (1h) is active
            SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            int choice = Integer.parseInt(prefManager.getString("forecastChoice", "1"));
            if (choice == 2) {
                JSONArray listhourly = json.getJSONArray("hourly");

                dbHelper.forecastDao().deleteForecastsByCityId(cityId);
                List<Forecast> hourlyforecasts = new ArrayList<>();

                for (int i = 0; i < listhourly.length(); i++) {
                    String currentItem = listhourly.get(i).toString();
                    Forecast forecast = extractor.extractHourlyForecast(currentItem);
                    // Data were not well-formed, abort
                    if (forecast == null) {
                        final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                        Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Could retrieve all data, so proceed
                    else {
                        forecast.setCity_id(cityId);
                        // add it to the database
                        dbHelper.forecastDao().addForecast(forecast);
                        hourlyforecasts.add(forecast);
                    }
                }

                ViewUpdater.updateForecasts(hourlyforecasts);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Toast.makeText(context, context.getResources().getString(R.string.error_fetch_forecast), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void possiblyUpdateWidgets(int cityID, List<WeekForecast> weeklyForecasts, CurrentWeatherData currentWeather) {
        //search for 1 Day widgets with same city ID
        int[] ids1day = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidget.class));
        SharedPreferences prefs1 = context.getSharedPreferences(WeatherWidget.PREFS_NAME, 0);
        for (int widgetID : ids1day) {
            //check if city ID is same
            if (cityID == prefs1.getInt(WeatherWidget.PREF_PREFIX_KEY + widgetID, -1)) {
                //perform update for the widget
                Log.d("debugtag", "found 1 day widget to update with data: " + cityID + " with widgetID " + widgetID);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
                updateWidget(widgetID, cityID, views, 1, weeklyForecasts, currentWeather);
            }
        }

        int[] ids3day = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetThreeDayForecast.class));
        SharedPreferences prefs3 = context.getSharedPreferences(WeatherWidgetThreeDayForecast.PREFS_NAME, 0);
        for (int widgetID : ids3day) {
            //check if city ID is same
            if (cityID == prefs3.getInt(WeatherWidget.PREF_PREFIX_KEY + widgetID, -1)) {
                //perform update for the widget
                Log.d("debugtag", "found 3 day widget to update with data: " + cityID + " with widgetID " + widgetID);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_3day_widget);
                updateWidget(widgetID, cityID, views, 3, weeklyForecasts, currentWeather);
            }
        }

        int[] ids5day = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetFiveDayForecast.class));
        SharedPreferences prefs5 = context.getSharedPreferences(WeatherWidgetFiveDayForecast.PREFS_NAME, 0);
        for (int widgetID : ids5day) {
            //check if city ID is same
            if (cityID == prefs5.getInt(WeatherWidget.PREF_PREFIX_KEY + widgetID, -1)) {
                //perform update for the widget
                Log.d("debugtag", "found 5 day widget to update with data: " + cityID + " with widgetID " + widgetID);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_5day_widget);
                updateWidget(widgetID, cityID, views, 5, weeklyForecasts, currentWeather);
            }
        }

    }


    private void updateWidget(int widgetId, int cityId, RemoteViews views, int widgetType, List<WeekForecast> weekForecasts, CurrentWeatherData weatherData) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        City city = dbHelper.cityDao().getCityById(cityId);
        Log.d("debugtag", "updating widget: " + cityId + " with widgetID " + widgetId);

        if (widgetType == 1) {
            WeatherWidget.updateView(context, appWidgetManager, views, widgetId, city, weatherData);

        } else {
            float[][] data = shapeWeekForecastForWidgets(weekForecasts);

            if (widgetType == 3) {
                WeatherWidgetThreeDayForecast.updateView(context, appWidgetManager, views, widgetId, data, city);
            } else {
                WeatherWidgetFiveDayForecast.updateView(context, appWidgetManager, views, widgetId, data, city);
            }

        }

        appWidgetManager.updateAppWidget(widgetId, views);

    }

    // function for week forecast list
    public float[][] shapeWeekForecastForWidgets(List<WeekForecast> forecasts) {

        if (forecasts.isEmpty()) {
            Log.d("devtag", "######## forecastlist empty");
            return new float[][]{new float[]{0}};
        }

        int cityId = forecasts.get(0).getCity_id();

        AppDatabase dbHelper = AppDatabase.getInstance(context.getApplicationContext());
        int zonemilliseconds = dbHelper.currentWeatherDao().getCurrentWeatherByCityId(cityId).getTimeZoneSeconds() * 1000;

        //temp max 0, temp min 1, humidity 2, pressure 3, uv_index 4, wind 5, wind direction 6, precipitation 7, time 8, weather ID 9, number of FCs for day 10
        float[] today = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> todayIDs = new LinkedList<>();
        float[] tomorrow = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> tomorrowIDs = new LinkedList<>();
        float[] in2days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in2daysIDs = new LinkedList<>();
        float[] in3days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in3daysIDs = new LinkedList<>();
        float[] in4days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in4daysIDs = new LinkedList<>();
        float[] in5days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in5daysIDs = new LinkedList<>();
        float[] in6days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in6daysIDs = new LinkedList<>();
        float[] in7days = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LinkedList<Integer> in7daysIDs = new LinkedList<>();
        float[] empty = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  //last field is not displayed otherwise
        LinkedList<Integer> emptyIDs = new LinkedList<>();


        today[0] = forecasts.get(0).getMaxTemperature();
        today[1] = forecasts.get(0).getMinTemperature();
        today[2] = forecasts.get(0).getHumidity();
        today[3] = forecasts.get(0).getPressure();
        today[7] = forecasts.get(0).getPrecipitation();
        today[5] = forecasts.get(0).getWind_speed();
        today[6] = forecasts.get(0).getWind_direction();
        today[4] = forecasts.get(0).getUv_index();
        today[8] = forecasts.get(0).getForecastTime() + zonemilliseconds;
        today[9] = forecasts.get(0).getWeatherID();
        today[10] = 1;

        tomorrow[0] = forecasts.get(1).getMaxTemperature();
        tomorrow[1] = forecasts.get(1).getMinTemperature();
        tomorrow[2] = forecasts.get(1).getHumidity();
        tomorrow[3] = forecasts.get(1).getPressure();
        tomorrow[7] = forecasts.get(1).getPrecipitation();
        tomorrow[5] = forecasts.get(1).getWind_speed();
        tomorrow[6] = forecasts.get(1).getWind_direction();
        tomorrow[4] = forecasts.get(1).getUv_index();
        tomorrow[8] = forecasts.get(1).getForecastTime() + zonemilliseconds;
        tomorrow[9] = forecasts.get(1).getWeatherID();
        tomorrow[10] = 1;

        in2days[0] = forecasts.get(2).getMaxTemperature();
        in2days[1] = forecasts.get(2).getMinTemperature();
        in2days[2] = forecasts.get(2).getHumidity();
        in2days[3] = forecasts.get(2).getPressure();
        in2days[7] = forecasts.get(2).getPrecipitation();
        in2days[5] = forecasts.get(2).getWind_speed();
        in2days[6] = forecasts.get(2).getWind_direction();
        in2days[4] = forecasts.get(2).getUv_index();
        in2days[8] = forecasts.get(2).getForecastTime() + zonemilliseconds;
        in2days[9] = forecasts.get(2).getWeatherID();
        in2days[10] = 1;

        in3days[0] = forecasts.get(3).getMaxTemperature();
        in3days[1] = forecasts.get(3).getMinTemperature();
        in3days[2] = forecasts.get(3).getHumidity();
        in3days[3] = forecasts.get(3).getPressure();
        in3days[7] = forecasts.get(3).getPrecipitation();
        in3days[5] = forecasts.get(3).getWind_speed();
        in3days[6] = forecasts.get(3).getWind_direction();
        in3days[4] = forecasts.get(3).getUv_index();
        in3days[8] = forecasts.get(3).getForecastTime() + zonemilliseconds;
        in3days[9] = forecasts.get(3).getWeatherID();
        in3days[10] = 1;

        in4days[0] = forecasts.get(4).getMaxTemperature();
        in4days[1] = forecasts.get(4).getMinTemperature();
        in4days[2] = forecasts.get(4).getHumidity();
        in4days[3] = forecasts.get(4).getPressure();
        in4days[7] = forecasts.get(4).getPrecipitation();
        in4days[5] = forecasts.get(4).getWind_speed();
        in4days[6] = forecasts.get(4).getWind_direction();
        in4days[4] = forecasts.get(4).getUv_index();
        in4days[8] = forecasts.get(4).getForecastTime() + zonemilliseconds;
        in4days[9] = forecasts.get(4).getWeatherID();
        in4days[10] = 1;

        in5days[0] = forecasts.get(5).getMaxTemperature();
        in5days[1] = forecasts.get(5).getMinTemperature();
        in5days[2] = forecasts.get(5).getHumidity();
        in5days[3] = forecasts.get(5).getPressure();
        in5days[7] = forecasts.get(5).getPrecipitation();
        in5days[5] = forecasts.get(5).getWind_speed();
        in5days[6] = forecasts.get(5).getWind_direction();
        in5days[4] = forecasts.get(5).getUv_index();
        in5days[8] = forecasts.get(5).getForecastTime() + zonemilliseconds;
        in5days[9] = forecasts.get(5).getWeatherID();
        in5days[10] = 1;

        in6days[0] = forecasts.get(6).getMaxTemperature();
        in6days[1] = forecasts.get(6).getMinTemperature();
        in6days[2] = forecasts.get(6).getHumidity();
        in6days[3] = forecasts.get(6).getPressure();
        in6days[7] = forecasts.get(6).getPrecipitation();
        in6days[5] = forecasts.get(6).getWind_speed();
        in6days[6] = forecasts.get(6).getWind_direction();
        in6days[4] = forecasts.get(6).getUv_index();
        in6days[8] = forecasts.get(6).getForecastTime() + zonemilliseconds;
        in6days[9] = forecasts.get(6).getWeatherID();
        in6days[10] = 1;

        in7days[0] = forecasts.get(7).getMaxTemperature();
        in7days[1] = forecasts.get(7).getMinTemperature();
        in7days[2] = forecasts.get(7).getHumidity();
        in7days[3] = forecasts.get(7).getPressure();
        in7days[7] = forecasts.get(7).getPrecipitation();
        in7days[7] = forecasts.get(7).getWind_speed();
        in7days[6] = forecasts.get(7).getWind_direction();
        in7days[4] = forecasts.get(7).getUv_index();
        in7days[8] = forecasts.get(7).getForecastTime() + zonemilliseconds;
        in7days[9] = forecasts.get(7).getWeatherID();
        in7days[10] = 1;

        return new float[][]{today, tomorrow, in2days, in3days, in4days, in5days, in6days, in7days, empty};
    }

}