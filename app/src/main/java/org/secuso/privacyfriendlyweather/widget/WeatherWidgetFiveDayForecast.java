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
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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

    public static void updateView(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId, float[][] data, City city) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        dayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //forecastList = DayForecastFilter.filter(forecastList, 5);
        if (data.length < 5) return;

        String day1 = dayFormat.format((long) data[0][8]);
        String day2 = dayFormat.format((long) data[1][8]);
        String day3 = dayFormat.format((long) data[2][8]);
        String day4 = dayFormat.format((long) data[3][8]);
        String day5 = dayFormat.format((long) data[4][8]);

        String temperature1 = String.format(
                "%s | %s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[0][0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[0][1])),
                prefManager.getWeatherUnit()
        );
        String temperature2 = String.format(
                "%s | %s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[1][0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[1][1])),
                prefManager.getWeatherUnit()
        );
        String temperature3 = String.format(
                "%s | %s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[2][0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[2][1])),
                prefManager.getWeatherUnit()
        );
        String temperature4 = String.format(
                "%s | %s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[3][0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[3][1])),
                prefManager.getWeatherUnit()
        );
        String temperature5 = String.format(
                "%s | %s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[4][0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[4][1])),
                prefManager.getWeatherUnit()
        );

        String extra1 = "";
        String extra2 = "";
        String extra3 = "";
        String extra4 = "";
        String extra5 = "";
        //select extra information to display from settings
        int extraInfo = prefManager.get5dayWidgetInfo();
        if (extraInfo==1){
            extra1 = String.format("%s ml", (int) data[0][7]);
            extra2 = String.format("%s ml", (int) data[1][7]);
            extra3 = String.format("%s ml", (int) data[2][7]);
            extra4 = String.format("%s ml", (int) data[3][7]);
            extra5 = String.format("%s ml", (int) data[4][7]);
        } else if (extraInfo==2){
            //wind max & min
            extra1 = String.format("%s | %sm/s", (int) data[0][4], (int) data[0][5]);
            extra2 = String.format("%s | %sm/s", (int) data[1][4], (int) data[1][5]);
            extra3 = String.format("%s | %sm/s", (int) data[2][4], (int) data[2][5]);
            extra4 = String.format("%s | %sm/s", (int) data[3][4], (int) data[3][5]);
            extra5 = String.format("%s | %sm/s", (int) data[4][4], (int) data[4][5]);
        } else {
            extra1 = String.format("%s | %s%%", (int) data[0][2], (int) data[0][3]);
            extra2 = String.format("%s | %s%%", (int) data[1][2], (int) data[1][3]);
            extra3 = String.format("%s | %s%%", (int) data[2][2], (int) data[2][3]);
            extra4 = String.format("%s | %s%%", (int) data[3][2], (int) data[3][3]);
            extra5 = String.format("%s | %s%%", (int) data[4][2], (int) data[4][3]);
        }


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
        views.setTextViewText(R.id.widget_city_weather_5day_hum1, extra1);
        views.setTextViewText(R.id.widget_city_weather_5day_hum2, extra2);
        views.setTextViewText(R.id.widget_city_weather_5day_hum3, extra3);
        views.setTextViewText(R.id.widget_city_weather_5day_hum4, extra4);
        views.setTextViewText(R.id.widget_city_weather_5day_hum5, extra5);

        views.setImageViewResource(R.id.widget_city_weather_5day_image1, UiResourceProvider.getIconResourceForWeatherCategory((int) data[0][9], true));
        views.setImageViewResource(R.id.widget_city_weather_5day_image2, UiResourceProvider.getIconResourceForWeatherCategory((int) data[1][9], true));
        views.setImageViewResource(R.id.widget_city_weather_5day_image3, UiResourceProvider.getIconResourceForWeatherCategory((int) data[2][9], true));
        views.setImageViewResource(R.id.widget_city_weather_5day_image4, UiResourceProvider.getIconResourceForWeatherCategory((int) data[3][9], true));
        views.setImageViewResource(R.id.widget_city_weather_5day_image5, UiResourceProvider.getIconResourceForWeatherCategory((int) data[4][9], true));

        Intent intent = new Intent(context, ForecastCityActivity.class);
        intent.putExtra("cityId", city.getCityId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);

        views.setOnClickPendingIntent(R.id.widget5day_layout, pendingIntent);
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
        Intent intent = new Intent(context, WeatherWidgetFiveDayForecast.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids;
        if (widgetId == null) {
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetFiveDayForecast.class));
        } else {
            ids = new int[]{widgetId};
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

