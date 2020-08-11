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

        if (widgetType == 1) {
            CurrentWeatherData weatherData = dbHelper.getCurrentWeatherByCityId(city.getCityId());
            WeatherWidget.updateView(context, appWidgetManager, views, widgetId, city, weatherData);

        } else if (widgetType == 3) {
            long start = System.nanoTime();
            float[][] data = compressWeatherData(forecastList);
            long end = System.nanoTime();
            Log.d("devtag", (end - start) / 1000000.0 + "ms");
            WeatherWidgetThreeDayForecast.updateView(context, appWidgetManager, views, widgetId, data, city);

        } else {
            long start = System.nanoTime();
            float[][] data = compressWeatherData(forecastList);
            long end = System.nanoTime();
            Log.d("devtag", (end - start) / 1000000.0 + "ms");
            WeatherWidgetFiveDayForecast.updateView(context, appWidgetManager, views, widgetId, data, city);
        }

        dbHelper.close();

        appWidgetManager.updateAppWidget(widgetId, views);

    }

    private float[][] compressWeatherData(List<Forecast> forecastList) {
        Calendar cal = new GregorianCalendar();
        int zonemilliseconds = dbHelper.getCurrentWeatherByCityId(cityId).getTimeZoneSeconds() * 1000;
        Log.d("devtag", "zonehours " + zonemilliseconds / 3600000.0);
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.DST_OFFSET, 0);
        cal.set(Calendar.ZONE_OFFSET, zonemilliseconds);

        long startOfDay = cal.getTimeInMillis();
        Log.d("devtag", "calendar " + cal.getTimeInMillis() + cal.getTime());

        if (System.currentTimeMillis() < startOfDay) cal.add(Calendar.HOUR_OF_DAY, -24);
        if (System.currentTimeMillis() > startOfDay + 24 * 3600 * 1000)
            cal.add(Calendar.HOUR_OF_DAY, 24);
        Log.d("devtag", "calendar " + cal.getTimeInMillis() + cal.getTime());

        //temp max, temp min, humidity max, humidity min, wind max, wind min, wind direction, rain total, time, weather ID, number of FCs for day
        float[] today = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> todayIDs = new LinkedList<>();
        float[] tomorrow = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> tomorrowIDs = new LinkedList<>();
        float[] in2days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> in2daysIDs = new LinkedList<>();
        float[] in3days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> in3daysIDs = new LinkedList<>();
        float[] in4days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> in4daysIDs = new LinkedList<>();
        float[] in5days = {Float.MIN_VALUE, Float.MAX_VALUE, 0, 100, 0, Float.MAX_VALUE, 0, 0, 0, 0, 0};
        LinkedList<Integer> in5daysIDs = new LinkedList<>();
        
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

                    if (fc.getWindSpeed() > today[4]) today[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < today[5]) today[5] = fc.getWindSpeed();


                    today[6] += fc.getWindDirection();
                    today[7] += fc.getRainValue();
                    today[8] += fc.getTimestamp();
                    Log.d("devtag", "today" + fc.getTimestamp());
                    //count number of FCs
                    today[10] += 1;

                    //count weather id occurrences -> use most common
                    todayIDs.add(fc.getWeatherID());

                    //inside next day...
                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 172800000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > tomorrow[0]) tomorrow[0] = fc.getTemperature();
                    if (fc.getTemperature() < tomorrow[1]) tomorrow[1] = fc.getTemperature();

                    if (fc.getHumidity() > tomorrow[2]) tomorrow[2] = fc.getHumidity();
                    if (fc.getHumidity() < tomorrow[3]) tomorrow[3] = fc.getHumidity();

                    if (fc.getWindSpeed() > tomorrow[4]) tomorrow[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < tomorrow[5]) tomorrow[5] = fc.getWindSpeed();
                    Log.d("devtag", "tomorrow" + fc.getTimestamp());


                    tomorrow[6] += fc.getWindDirection();
                    tomorrow[7] += fc.getRainValue();
                    tomorrow[8] += fc.getTimestamp();
                    //count number of FCs
                    ++tomorrow[10];

                    //count weather id occurrences -> use most common
                    tomorrowIDs.add(fc.getWeatherID());

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 259200000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > in2days[0]) in2days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in2days[1]) in2days[1] = fc.getTemperature();

                    if (fc.getHumidity() > in2days[2]) in2days[2] = fc.getHumidity();
                    if (fc.getHumidity() < in2days[3]) in2days[3] = fc.getHumidity();

                    if (fc.getWindSpeed() > in2days[4]) in2days[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < in2days[5]) in2days[5] = fc.getWindSpeed();

                    Log.d("devtag", "2days" + fc.getTimestamp());

                    in2days[6] += fc.getWindDirection();
                    in2days[7] += fc.getRainValue();
                    in2days[8] += fc.getTimestamp();
                    //count number of FCs
                    ++in2days[10];

                    //count weather id occurrences -> use most common
                    in2daysIDs.add(fc.getWeatherID());

                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 345600000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > in3days[0]) in3days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in3days[1]) in3days[1] = fc.getTemperature();

                    if (fc.getHumidity() > in3days[2]) in3days[2] = fc.getHumidity();
                    if (fc.getHumidity() < in3days[3]) in3days[3] = fc.getHumidity();

                    if (fc.getWindSpeed() > in3days[4]) in3days[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < in3days[5]) in3days[5] = fc.getWindSpeed();


                    in3days[6] += fc.getWindDirection();
                    in3days[7] += fc.getRainValue();
                    in3days[8] += fc.getTimestamp();
                    //count number of FCs
                    ++in3days[10];

                    //count weather id occurrences -> use most common
                    in3daysIDs.add(fc.getWeatherID());
                    
                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 432000000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > in4days[0]) in4days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in4days[1]) in4days[1] = fc.getTemperature();

                    if (fc.getHumidity() > in4days[2]) in4days[2] = fc.getHumidity();
                    if (fc.getHumidity() < in4days[3]) in4days[3] = fc.getHumidity();

                    if (fc.getWindSpeed() > in4days[4]) in4days[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < in4days[5]) in4days[5] = fc.getWindSpeed();


                    in4days[6] += fc.getWindDirection();
                    in4days[7] += fc.getRainValue();
                    in4days[8] += fc.getTimestamp();
                    //count number of FCs
                    ++in4days[10];

                    //count weather id occurrences -> use most common
                    in4daysIDs.add(fc.getWeatherID());
                    
                } else if (fc.getForecastTime().before(new Date(cal.getTimeInMillis() + 518400000))) {
                    //is temp higher lower than current max/min?
                    if (fc.getTemperature() > in5days[0]) in5days[0] = fc.getTemperature();
                    if (fc.getTemperature() < in5days[1]) in5days[1] = fc.getTemperature();

                    if (fc.getHumidity() > in5days[2]) in5days[2] = fc.getHumidity();
                    if (fc.getHumidity() < in5days[3]) in5days[3] = fc.getHumidity();

                    if (fc.getWindSpeed() > in5days[4]) in5days[4] = fc.getWindSpeed();
                    if (fc.getWindSpeed() < in5days[5]) in5days[5] = fc.getWindSpeed();


                    in5days[6] += fc.getWindDirection();
                    in5days[7] += fc.getRainValue();
                    in5days[8] += fc.getTimestamp();
                    //count number of FCs
                    ++in5days[10];

                    //count weather id occurrences -> use most common
                    in5daysIDs.add(fc.getWeatherID());
                    
                }
            }
        }
        //select most common weather ID from the day
        today[9] = mostPrevalentWeather(todayIDs);
        tomorrow[9] = mostPrevalentWeather(tomorrowIDs);
        in2days[9] = mostPrevalentWeather(in2daysIDs);
        in3days[9] = mostPrevalentWeather(in3daysIDs);
        in4days[9] = mostPrevalentWeather(in4daysIDs);
        in5days[9] = mostPrevalentWeather(in5daysIDs);

        //normalize wind direction and time for number of FCs used for that day
        today[6] /= today[10];
        today[8] = today[8] * 1000 / today[10] + zonemilliseconds;
        tomorrow[6] /= tomorrow[10];
        tomorrow[8] = tomorrow[8] * 1000 / tomorrow[10] + zonemilliseconds;
        in2days[6] /= in2days[10];
        in2days[8] = in2days[8] * 1000 / in2days[10] + zonemilliseconds;
        in3days[6] /= in3days[10];
        in3days[8] = in3days[8] * 1000 / in3days[10] + zonemilliseconds;
        in4days[6] /= in4days[10];
        in4days[8] = in4days[8] * 1000 / in4days[10] + zonemilliseconds;
        in5days[6] /= in5days[10];
        in5days[8] = in5days[8] * 1000 / in5days[10] + zonemilliseconds;
        Log.d("devtag", "total :" + forecastList.size() + "times: " + today[10] + " " + today[8] + " " + tomorrow[10] + " " + tomorrow[8] + " " + in2days[10] + " " + in2days[8] + " " + in3days[10] + " " + in3days[8] + " " + in4days[10] + " " + in4days[8] + " " + in5days[10] + " " + in5days[8]);

        return new float[][]{today, tomorrow, in2days, in3days, in4days, in5days};
    }

    //return most common weather ID from linked list
    private int mostPrevalentWeather(LinkedList<Integer> IDs) {
        int[] counts = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        //count 1 up for every ID in its category
        for (int id : IDs) {
            switch (id) {
                case 10:
                    counts[0] += 1;
                    break;
                case 20:
                    counts[1] += 1;
                    break;
                case 30:
                    counts[2] += 1;
                    break;
                case 40:
                    counts[3] += 1;
                    break;
                case 50:
                    counts[4] += 1;
                    break;
                case 60:
                    counts[5] += 1;
                    break;
                case 70:
                    counts[6] += 1;
                    break;
                case 80:
                    counts[7] += 1;
                    break;
                case 90:
                    counts[8] += 1;
                    break;
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
