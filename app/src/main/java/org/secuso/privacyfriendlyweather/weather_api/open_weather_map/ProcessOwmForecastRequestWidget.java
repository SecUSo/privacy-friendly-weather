package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Handler;
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

        if (widgetType > 1) {
            List<Forecast> forecastList = dbHelper.getForecastsByCityId(cityId);
            Date now = new Date();

            for (Forecast fc : forecastList) {
                if (fc.getForecastTime().after(now)) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(fc.getForecastTime());
                    if (c.get(Calendar.HOUR_OF_DAY) == 12) {
                        weekForecastList.add(fc);
                    }
                }
            }

        }


        if (widgetType == 1) {
            CurrentWeatherData weatherData = dbHelper.getCurrentWeatherByCityId(city.getCityId());
            WeatherWidget.updateView(context, appWidgetManager, views, widgetId, city, weatherData);

        } else if (widgetType == 3) {
            WeatherWidgetThreeDayForecast.updateView(context, appWidgetManager, views, widgetId, weekForecastList, city);

        } else {
            WeatherWidgetFiveDayForecast.updateView(context, appWidgetManager, views, widgetId, weekForecastList, city);
        }

        dbHelper.close();

        appWidgetManager.updateAppWidget(widgetId, views);

    }

}
