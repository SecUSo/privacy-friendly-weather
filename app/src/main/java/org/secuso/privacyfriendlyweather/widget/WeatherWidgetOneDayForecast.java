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
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static androidx.core.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherWidgetOneDayForecast extends AppWidgetProvider {
    public static final String PREFS_NAME = "org.secuso.privacyfriendlyweather.widget.WeatherWidget1Day";

    public void updateAppWidget(Context context, int appWidgetId) {

        int cityID = context.getSharedPreferences(WeatherWidgetOneDayForecast.PREFS_NAME, 0).
                getInt(WeatherWidget.PREF_PREFIX_KEY + appWidgetId, -1);
        Intent intent = new Intent(context, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_SINGLE_ACTION);

        intent.putExtra("cityId", cityID);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        enqueueWork(context, UpdateDataService.class, 0, intent);

    }

    public static void updateView(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId, float[][] data, City city, long timestamp) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#");
        DecimalFormat decimal1Format = new DecimalFormat("0.0");


        //forecastList = DayForecastFilter.filter(forecastList, 5);
        if (data.length < 5) return;

        String temperature1 = String.format(
                "%s\u200a%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[0][1])),
                prefManager.getWeatherUnit()
        );
        String temperature2 = String.format(
                "%s\u200a%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[1][1])),
                prefManager.getWeatherUnit()
        );
        String temperature3 = String.format(
                "%s\u200a%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[2][1])),
                prefManager.getWeatherUnit()
        );
        String temperature4 = String.format(
                "%s\u200a%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[3][1])),
                prefManager.getWeatherUnit()
        );
        String temperature5 = String.format(
                "%s\u200a%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(data[4][1])),
                prefManager.getWeatherUnit()
        );

        String extra1 = "";
        String extra2 = "";
        String extra3 = "";
        String extra4 = "";
        String extra5 = "";
        //select extra information to display from settings
        int extraInfo = prefManager.get1dayWidgetInfo();
        if (extraInfo==1){
            extra1 = String.format("%smm",decimal1Format.format(data[0][4]));
            extra2 = String.format("%smm", decimal1Format.format(data[1][4]));
            extra3 = String.format("%smm", decimal1Format.format(data[2][4]));
            extra4 = String.format("%smm", decimal1Format.format(data[3][4]));
            extra5 = String.format("%smm", decimal1Format.format(data[4][4]));
        } else if (extraInfo==2) {
            //wind max & min
            extra1 = prefManager.convertToCurrentSpeedUnit(data[0][3]);
            extra2 = prefManager.convertToCurrentSpeedUnit(data[1][3]);
            extra3 = prefManager.convertToCurrentSpeedUnit(data[2][3]);
            extra4 = prefManager.convertToCurrentSpeedUnit(data[3][3]);
            extra5 = prefManager.convertToCurrentSpeedUnit(data[4][3]);
        } else {
            extra1 = String.format("%s%%rh", (int) data[0][2]);
            extra2 = String.format("%s%%rh", (int) data[1][2]);
            extra3 = String.format("%s%%rh", (int) data[2][2]);
            extra4 = String.format("%s%%rh", (int) data[3][2]);
            extra5 = String.format("%s%%rh", (int) data[4][2]);
        }


        views.setTextViewText(R.id.widget_city_name, city.getCityName()+" "+ StringFormatUtils.formatTimeWithoutZone(timestamp));
        Log.d("widgetOne timestamp",timestamp+"");

        views.setTextViewText(R.id.widget_city_weather_1day_temp1, temperature1);
        views.setTextViewText(R.id.widget_city_weather_1day_temp2, temperature2);
        views.setTextViewText(R.id.widget_city_weather_1day_temp3, temperature3);
        views.setTextViewText(R.id.widget_city_weather_1day_temp4, temperature4);
        views.setTextViewText(R.id.widget_city_weather_1day_temp5, temperature5);
        views.setTextViewText(R.id.widget_city_weather_1day_hum1, extra1);
        views.setTextViewText(R.id.widget_city_weather_1day_hum2, extra2);
        views.setTextViewText(R.id.widget_city_weather_1day_hum3, extra3);
        views.setTextViewText(R.id.widget_city_weather_1day_hum4, extra4);
        views.setTextViewText(R.id.widget_city_weather_1day_hum5, extra5);

        views.setImageViewResource(R.id.widget_city_weather_1day_image1, UiResourceProvider.getIconResourceForWeatherCategory((int) data[0][5], true));
        views.setImageViewResource(R.id.widget_city_weather_1day_image2, UiResourceProvider.getIconResourceForWeatherCategory((int) data[1][5], true));
        views.setImageViewResource(R.id.widget_city_weather_1day_image3, UiResourceProvider.getIconResourceForWeatherCategory((int) data[2][5], true));
        views.setImageViewResource(R.id.widget_city_weather_1day_image4, UiResourceProvider.getIconResourceForWeatherCategory((int) data[3][5], true));
        views.setImageViewResource(R.id.widget_city_weather_1day_image5, UiResourceProvider.getIconResourceForWeatherCategory((int) data[4][5], true));

        views.setInt(R.id.widget1day_layout, "setBackgroundResource", prefManager.getShowWidgetBackground() ? R.drawable.rounded_corner_dark : 0);

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
            WeatherWidgetOneDayForecastConfigureActivity.deleteTitlePref(context, appWidgetId);
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
        Intent intent = new Intent(context, WeatherWidgetOneDayForecast.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids;
        if (widgetId == null) {
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetOneDayForecast.class));
        } else {
            ids = new int[]{widgetId};
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

