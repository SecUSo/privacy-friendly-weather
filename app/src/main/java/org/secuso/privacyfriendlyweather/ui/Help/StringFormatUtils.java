package org.secuso.privacyfriendlyweather.ui.Help;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;

import java.text.DecimalFormat;
import java.util.Date;

public final class StringFormatUtils {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private static DecimalFormat intFormat = new DecimalFormat("0");

    public static String formatDecimal(float decimal) {
        return decimalFormat.format(decimal);
    }

    public static String formatInt(float decimal) {
        return intFormat.format(decimal);
    }

    public static String formatInt(float decimal, String appendix) {
        return String.format("%s%s", formatInt(decimal), appendix);
    }

    public static String formatDecimal(float decimal, String appendix) {
        return String.format("%s%s",formatDecimal(decimal), appendix);
    }

    public static String formatTemperature(Context context, float temperature) {
        AppPreferencesManager prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        return formatDecimal(prefManager.convertTemperatureFromCelsius(temperature), prefManager.getWeatherUnit());
    }

    public static String formatTime(Context context, long timeInMillis) {
        return DateUtils.formatDateTime(context, timeInMillis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String formatTime(Context context, Date date) {
        return formatTime(context, date.getTime());
    }


}
