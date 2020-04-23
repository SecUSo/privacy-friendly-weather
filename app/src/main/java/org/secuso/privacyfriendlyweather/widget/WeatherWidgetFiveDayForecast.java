package org.secuso.privacyfriendlyweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.ForecastCityActivity;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.support.v4.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherWidgetFiveDayForecast extends AppWidgetProvider {
    public static final String PREFS_NAME = "org.secuso.privacyfriendlyweather.widget.WeatherWidget5Day";

    public void updateAppWidget(Context context, int appWidgetId) {

        Intent intent = new Intent(context, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_WIDGET_ACTION);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("widget_type", 5);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        enqueueWork(context, UpdateDataService.class, 0, intent);

    }

    public static void updateView(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId, List<Forecast> forecastList, City city) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

        //forecastList = DayForecastFilter.filter(forecastList, 5);
        if(forecastList.size() < 5) return;

        String day1 = dayFormat.format(forecastList.get(0).getForecastTime());
        String day2 = dayFormat.format(forecastList.get(1).getForecastTime());
        String day3 = dayFormat.format(forecastList.get(2).getForecastTime());
        String day4 = dayFormat.format(forecastList.get(3).getForecastTime());
        String day5 = dayFormat.format(forecastList.get(4).getForecastTime());

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
        String temperature4 = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(3).getTemperature())),
                prefManager.getWeatherUnit()
        );
        String temperature5 = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(4).getTemperature())),
                prefManager.getWeatherUnit()
        );

        String hum1 = String.format("%s %%", forecastList.get(0).getHumidity());
        String hum2 = String.format("%s %%", forecastList.get(1).getHumidity());
        String hum3 = String.format("%s %%", forecastList.get(2).getHumidity());
        String hum4 = String.format("%s %%", forecastList.get(3).getHumidity());
        String hum5 = String.format("%s %%", forecastList.get(4).getHumidity());

        views.setTextViewText(R.id.widget_city_name, city.getCityName());

        views.setTextViewText(R.id.widget_city_weather_5day_day1, day1);
        views.setTextViewText(R.id.widget_city_weather_5day_day2, day2);
        views.setTextViewText(R.id.widget_city_weather_5day_day3, day3);
        views.setTextViewText(R.id.widget_city_weather_5day_day4, day4);
        views.setTextViewText(R.id.widget_city_weather_5day_day5, day5);
        views.setTextViewText(R.id.widget_city_weather_5day_temp1, temperature1);
        views.setTextViewText(R.id.widget_city_weather_5day_temp2, temperature2);
        views.setTextViewText(R.id.widget_city_weather_5day_temp3, temperature3);
        views.setTextViewText(R.id.widget_city_weather_5day_temp4, temperature4);
        views.setTextViewText(R.id.widget_city_weather_5day_temp5, temperature5);
        views.setTextViewText(R.id.widget_city_weather_5day_hum1, hum1);
        views.setTextViewText(R.id.widget_city_weather_5day_hum2, hum2);
        views.setTextViewText(R.id.widget_city_weather_5day_hum3, hum3);
        views.setTextViewText(R.id.widget_city_weather_5day_hum4, hum4);
        views.setTextViewText(R.id.widget_city_weather_5day_hum5, hum5);

        views.setImageViewResource(R.id.widget_city_weather_5day_image1, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(0).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_5day_image2, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(1).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_5day_image3, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(2).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_5day_image4, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(3).getWeatherID()));
        views.setImageViewResource(R.id.widget_city_weather_5day_image5, UiResourceProvider.getIconResourceForWeatherCategory(forecastList.get(4).getWeatherID()));

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
            updateAppWidget(context, appWidgetId);
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
        Intent intent = new Intent(context, WeatherWidgetFiveDayForecast.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids;
        if(widgetId == null) {
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetFiveDayForecast.class));
        }else{
            ids = new int[]{widgetId};
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

