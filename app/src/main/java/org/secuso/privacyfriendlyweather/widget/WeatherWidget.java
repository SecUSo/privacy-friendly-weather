package org.secuso.privacyfriendlyweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.ForecastCityActivity;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static androidx.core.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherWidget extends AppWidgetProvider {
    public static final String PREFS_NAME = "org.secuso.privacyfriendlyweather.widget.WeatherWidget";
    public static final String PREF_PREFIX_KEY = "appwidget_";

    public void updateAppWidget(Context context, final int appWidgetId) {

        int cityID = context.getSharedPreferences(WeatherWidget.PREFS_NAME, 0).
                getInt(WeatherWidget.PREF_PREFIX_KEY + appWidgetId, -1);
        Intent intent = new Intent(context, UpdateDataService.class);
        Log.d("debugtag", "1day widget calls single update: " + cityID + " with widgetID " + appWidgetId);

        intent.setAction(UpdateDataService.UPDATE_SINGLE_ACTION);
        intent.putExtra("cityId", cityID);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        enqueueWork(context, UpdateDataService.class, 0, intent);

    }

    public static void updateView(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId, City city, CurrentWeatherData weatherData) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String temperature = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(weatherData.getTemperatureCurrent())),
                prefManager.getWeatherUnit()
        );
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //correct for timezone differences
        int zoneseconds = weatherData.getTimeZoneSeconds();

        Date riseTime = new Date((weatherData.getTimeSunrise() + zoneseconds) * 1000L);
        String sunRise = timeFormat.format(riseTime);
        Date setTime = new Date((weatherData.getTimeSunset() + zoneseconds) * 1000L);
        String sunSet = timeFormat.format(setTime);

        String windSpeed = prefManager.convertToCurrentSpeedUnit(weatherData.getWindSpeed());

        views.setTextViewText(R.id.widget_city_weather_temperature, temperature);
        views.setTextViewText(R.id.widget_city_weather_humidity, String.format("%s %%", (int) weatherData.getHumidity()));
        views.setTextViewText(R.id.widget_city_name, city.getCityName());
        views.setTextViewText(R.id.widget_city_weather_rise, sunRise);
        views.setTextViewText(R.id.widget_city_weather_set, sunSet);
        views.setTextViewText(R.id.widget_city_weather_wind, windSpeed);


        boolean isDay = weatherData.getTimestamp()  > weatherData.getTimeSunrise() && weatherData.getTimestamp() < weatherData.getTimeSunset();

        views.setImageViewResource(R.id.widget_city_weather_image_view, UiResourceProvider.getIconResourceForWeatherCategory(weatherData.getWeatherID(), isDay));

        Intent intent = new Intent(context, ForecastCityActivity.class);
        intent.putExtra("cityId", city.getCityId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.widget1day_layout, pendingIntent);

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

    public static void forceWidgetUpdate(Context context) {
        forceWidgetUpdate(null, context);
    }

    public static void forceWidgetUpdate(Integer widgetId, Context context) {
        Intent intent = new Intent(context, WeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids;
        if (widgetId == null) {
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidget.class));
        } else {
            ids = new int[]{widgetId};
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

