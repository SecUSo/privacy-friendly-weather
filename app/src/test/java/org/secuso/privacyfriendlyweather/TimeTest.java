package org.secuso.privacyfriendlyweather;

import org.junit.Test;
import org.secuso.privacyfriendlyweather.util.TimeUtil;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TimeTest {

    @Test
    public void testTimeStartOfDay() {
        long currentTime = 1599659833857L;
        long startOfDay = TimeUtil.getStartOfDayCustomCurrentTime(3 * 3600, currentTime);

        assertEquals(1599609603057L ,startOfDay);
    }

    @Test
    public void testTimePrinter() {
        long currentTimeInMillis = 1599659833857L;
        long currentTimeInSeconds = currentTimeInMillis / 1000;
        String formattedString = TimeUtil.formatTimeSimple(2 * 3600, currentTimeInSeconds);
        assertEquals("15:57", formattedString);
    }

    @Test
    public void testTimePrinter2() {
        long currentTimeInMillis = 1599659833857L;
        long currentTimeInSeconds = currentTimeInMillis / 1000;
        String formattedString = TimeUtil.formatTimeSimple(3 * 3600, currentTimeInSeconds);
        assertEquals("16:57", formattedString);
    }

    @Test
    public void testTimePrinterZero() {
        long currentTimeInMillis = 0;
        long currentTimeInSeconds = currentTimeInMillis / 1000;
        String formattedString = TimeUtil.formatTimeSimple(0, currentTimeInSeconds);
        assertEquals("00:00", formattedString);
    }

    @Test
    public void testTimePrinterNegativeTimezone() {
        long currentTimeInMillis = 1599659833857L;
        long currentTimeInSeconds = currentTimeInMillis / 1000;
        String formattedString = TimeUtil.formatTimeSimple(-1 * 3600, currentTimeInSeconds);
        assertEquals("12:57", formattedString);
    }

    @Test
    public void testTimePrinter3() {
        long currentTimeInMillis = 1599659833857L;
        long currentTimeInSeconds = currentTimeInMillis / 1000;
        String formattedString = TimeUtil.formatTimeSimple(4 * 3600, currentTimeInSeconds);
        assertEquals("17:57", formattedString);
    }


}