package org.secuso.privacyfriendlyweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.ForecastCityActivity;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;
import org.secuso.privacyfriendlyweather.ui.util.DayForecastFilter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherWidgetThreeDayForecast extends AppWidgetProvider {
    private static final String PREFS_NAME = "org.secuso.privacyfriendlyweather.widget.WeatherWidget3Day";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        //CharSequence widgetText = WeatherWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_3day_widget);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        final Integer cityId = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, -1);
        if (cityId == -1) {
            Toast.makeText(context, "cityId is null?", Toast.LENGTH_LONG);
            return;
        }


        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                PFASQLiteHelper database = PFASQLiteHelper.getInstance(context);

                City city = database.getCityById(cityId);
                List<Forecast> forecastList = database.getForecastsByCityId(cityId);
                List<Forecast> weekForecastList = new ArrayList<>();
                Date now = new Date();

                for(Forecast fc : forecastList) {
                    if(fc.getForecastTime().after(now)) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(fc.getForecastTime());
                        if (c.get(Calendar.HOUR_OF_DAY) == 12) {
                            weekForecastList.add(fc);
                        }
                    }
                }

                updateView(context, appWidgetManager, views, appWidgetId, weekForecastList, city);

                database.close();

                return null;
            }
        }.doInBackground(cityId);
    }

    private static void updateView(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId, List<Forecast> forecastList, City city) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        //forecastList = DayForecastFilter.filter(forecastList, 3);
        if(forecastList.size() < 3) return;

        String day1 = dayFormat.format(forecastList.get(0).getForecastTime());
        String day2 = dayFormat.format(forecastList.get(1).getForecastTime());
        String day3 = dayFormat.format(forecastList.get(2).getForecastTime());

        String temperature1 = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(0).getTemperature())),
                prefManager.getWeatherUnit()
        );
        String temperature2 = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(1).getTemperature())),
                prefManager.getWeatherUnit()
        );
        String temperature3 = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(2).getTemperature())),
                prefManager.getWeatherUnit()
        );

        String hum1 = String.format("%s %%", forecastList.get(0).getHumidity());
        String hum2 = String.format("%s %%", forecastList.get(1).getHumidity());
        String hum3 = String.format("%s %%", forecastList.get(2).getHumidity());

        views.setTextViewText(R.id.widget_city_name, city.getCityName());

        views.setTextViewText(R.id.widget_city_weather_3day_day1, day1);
        views.setTextViewText(R.id.widget_city_weather_3day_day2, day2);
        views.setTextViewText(R.id.widget_city_weather_3day_day3, day3);
        views.setTextViewText(R.id.widget_city_weather_3day_temp1, temperature1);
        views.setTextViewText(R.id.widget_city_weather_3day_temp2, temperature2);
        views.setTextViewText(R.id.widget_city_weather_3day_temp3, temperature3);
        views.setTextViewText(R.id.widget_city_weather_3day_hum1, hum1);
        views.setTextViewText(R.id.widget_city_weather_3day_hum2, hum2);
        views.setTextViewText(R.id.widget_city_weather_3day_hum3, hum3);

        views.setImageViewResource(R.id.widget_city_weather_3day_image1, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(0).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_3day_image2, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(1).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_3day_image3, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(2).getWeatherID()));

        Intent intent = new Intent(context, ForecastCityActivity.class);
        intent.putExtra("cityId", forecastList.get(0).getCity_id());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_city_weather_image_view, pendingIntent);
        
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WeatherWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void forceWidgetUpdate(Context context){
        forceWidgetUpdate(null, context);
    }

    public static void forceWidgetUpdate(Integer widgetId, Context context){
        Intent intent = new Intent(context, WeatherWidgetThreeDayForecast.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids;
        if(widgetId == null) {
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetThreeDayForecast.class));
        }else{
            ids = new int[]{widgetId};
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

