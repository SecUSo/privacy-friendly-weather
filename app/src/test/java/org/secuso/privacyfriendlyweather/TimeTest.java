package org.secuso.privacyfriendlyweather;

import org.junit.Test;
import org.secuso.privacyfriendlyweather.util.TimeUtil;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TimeTest {

    @Test
    public void testTimeStartOfDay() {
        long currentTime = System.currentTimeMillis();
        long startOfDay = TimeUtil.getStartOfDayCustomCurrentTime(3 * 3600, currentTime);

        String time = (new SimpleDateFormat("HH:mm", Locale.getDefault())).format(new Date(currentTime));
        String time2 = TimeUtil.formatTimeSimple(0, currentTime);
        assertTrue(false); // TODO: test incorrect

        //assertEquals(startOfDay, );
    }

    @Test
    public void testTimePrinter() {
        long currentTime = System.currentTimeMillis();
        String formattedString = TimeUtil.formatTimeSimple(0, currentTime); // TODO: use valid value
        assertEquals("10:00", formattedString); // TODO: Test incorrect
    }


}