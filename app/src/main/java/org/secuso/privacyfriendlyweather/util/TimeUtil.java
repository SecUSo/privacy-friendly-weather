package org.secuso.privacyfriendlyweather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static long getStartOfDay(int timezoneInSeconds) {
        return getStartOfDayCustomCurrentTime(timezoneInSeconds, System.currentTimeMillis());
    }

    public static long getStartOfDayCustomCurrentTime(int timezoneInSeconds, long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.DST_OFFSET, 0);
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.ZONE_OFFSET, timezoneInSeconds);

        long startOfDay = cal.getTimeInMillis();
        //Log.d("devtag", "calendar " + cal.getTimeInMillis() + cal.getTime());

        if (currentTime < startOfDay) {
            cal.add(Calendar.HOUR_OF_DAY, -24);
        }
        if (currentTime > startOfDay + 24 * 3600 * 1000) {
            cal.add(Calendar.HOUR_OF_DAY, 24);
        }
        return cal.getTimeInMillis();
    }

    public static String formatTimeSimple(int timeZoneSeconds, long timeInSeconds) {
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateTime = new Date((timeInSeconds + timeZoneSeconds) * 1000L);
        return timeFormat.format(dateTime);
    }
}
