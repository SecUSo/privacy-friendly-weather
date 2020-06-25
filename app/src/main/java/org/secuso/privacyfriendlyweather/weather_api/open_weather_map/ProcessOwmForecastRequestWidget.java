package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;
import org.secuso.privacyfriendlyweather.widget.WeatherWidget;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetFiveDayForecast;
import org.secuso.privacyfriendlyweather.widget.WeatherWidgetThreeDayForecast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmForecastRequestWidget implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_forecast";

    /**
     * Member variables
     */
    private Context context;
    private PFASQLiteHelper dbHelper;
    private int cityId;
    private int widgetId;
    private int widgetType;
    private RemoteViews views;


    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    ProcessOwmForecastRequestWidget(Context context, int cityId, int widgetId, int widgetType, RemoteViews views) {
        this.context = context;
        this.dbHelper = PFASQLiteHelper.getInstance(context);
        this.cityId = cityId;
        this.widgetId = widgetId;
        this.widgetType = widgetType;
        this.views = views;
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
            JSONArray list = json.getJSONArray("list");
            int cityId = json.getJSONObject("city").getInt("id");

            dbHelper.deleteForecastsByCityId(cityId);

            List<Forecast> forecasts = new ArrayList<>();
            // Continue with inserting new records
            for (int i = 0; i < list.length(); i++) {
                String currentItem = list.get(i).toString();
                Forecast forecast = extractor.extractForecast(currentItem);
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
                    dbHelper.addForecast(forecast);
                    forecasts.add(forecast);
                }
            }


            ViewUpdater.updateForecasts(forecasts);
            updateWidget();

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

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        City city = dbHelper.getCityById(cityId);
        List<Forecast> weekForecastList = new ArrayList<>();
        List<Forecast> forecastList = dbHelper.getForecastsByCityId(cityId);

        if (widgetType > 1) {
            Date now = new Date();

            for (Forecast fc : forecastList) {
                if (fc.getForecastTime().after(now)) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(fc.getLocalForecastTime(context));

                    //TODO replace with max & min values
                    if (c.get(Calendar.HOUR_OF_DAY) < 14 && c.get(Calendar.HOUR_OF_DAY) > 10) {
                        weekForecastList.add(fc);
                    }
                }
            }

        }


        if (widgetType == 1) {
            CurrentWeatherData weatherData = dbHelper.getCurrentWeatherByCityId(city.getCityId());
            WeatherWidget.updateView(context, appWidgetManager, views, widgetId, city, weatherData);

        } else if (widgetType == 3) {
            long start = System.nanoTime();
            float[][] data = compressWeatherData(forecastList);
            long end = System.nanoTime();
            Log.d("devtag", (end - start) / 1000000.0 + "ms");
            WeatherWidgetThreeDayForecast.updateView(context, appWidgetManager, views, widgetId, weekForecastList, data, city);

        } else {
            WeatherWidgetFiveDayForecast.updateView(context, appWidgetManager, views, widgetId, weekForecastList, compressWeatherData(forecastList), city);
        }

        dbHelper.close();

        appWidgetManager.updateAppWidget(widgetId, views);

    }

    private float[][] compressWeatherData(List<Forecast> forecastList) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.ZONE_OFFSET, dbHelper.getCurrentWeatherByCityId(cityId).getTimeZoneSeconds() * 1000);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        //temp max, temp min, humidity max, humidity min, wind max, wind min, time, weather ID
        float[] today = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        LinkedList<Integer> todayIDs = new LinkedList<>();
        float[] tomorrow = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        float[] in2days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        float[] in3days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        float[] in4days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        float[] in5days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0};
        //iterate over FCs from today and after
        for (Forecast fc : forecastList) {
            if (fc.getForecastTime().after(cal.getTime())) {
                //inside current day
                if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 86400000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > today[0]) today[0] = fc.getTemperature();
                    if (fc.getTemperature() < today[1]) today[1] = fc.getTemperature();

                    if (fc.getHumidity() > today[2]) today[2] = fc.getHumidity();
                    if (fc.getHumidity() < today[3]) today[3] = fc.getHumidity();

                    //TODO implement wind in forecast
                    //if(fc.getWind()>today[4]) today[4]=fc.getWind();
                    //if(fc.getWind()<today[5]) today[5]=fc.getWind();

                    //count weather id occurences -> use most common
                    todayIDs.add(fc.getWeatherID());

                    //set weekday time
                    if (today[6] > 0) {
                        today[6] = (today[6] + fc.getTimestamp()) / 2;
                    } else today[6] = fc.getTimestamp();


                    //inside next day...
                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 172800000))) {
                    if (fc.getTemperature() > tomorrow[0]) tomorrow[0] = fc.getTemperature();
                    if (fc.getTemperature() < tomorrow[1]) tomorrow[1] = fc.getTemperature();

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 259200000))) {
                    if (fc.getTemperature() > in2days[0]) in2days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in2days[1]) in2days[1] = fc.getTemperature();

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 345600000))) {
                    if (fc.getTemperature() > in3days[0]) in3days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in3days[1]) in3days[1] = fc.getTemperature();

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 432000000))) {
                    if (fc.getTemperature() > in4days[0]) in4days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in4days[1]) in4days[1] = fc.getTemperature();

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 518400000))) {
                    if (fc.getTemperature() > in5days[0]) in5days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in5days[1]) in5days[1] = fc.getTemperature();
                }
            }
        }
        today[7] = mostPrevalentWeather(todayIDs);

        return new float[][]{today, tomorrow, in2days, in3days, in4days, in5days};
    }

    private int mostPrevalentWeather(LinkedList<Integer> IDs) {
        int[] counts = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        //count 1 up for every ID in its category
        for (int id : IDs) {
            switch (id) {
                case 10:
                    counts[0] += 1;
                case 20:
                    counts[1] += 1;
                case 30:
                    counts[2] += 1;
                case 40:
                    counts[3] += 1;
                case 50:
                    counts[4] += 1;
                case 60:
                    counts[5] += 1;
                case 70:
                    counts[6] += 1;
                case 80:
                    counts[7] += 1;
                case 90:
                    counts[8] += 1;
            }
        }
        int max = 0;
        int index = 0;
        //search for max count and select max as index
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > max) {
                index = i;
                max = counts[i];
            }
        }
        //weather ID is between 10 and 90
        return (index + 1) * 10;
    }

}
