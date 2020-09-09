package org.secuso.privacyfriendlyweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlyweather.database.PFASQLiteHelper.DATABASE_NAME;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private static final String TAG = "AppTest";
    private static final Context appContext =  InstrumentationRegistry.getInstrumentation().getTargetContext();
    private static final Context testContext =  InstrumentationRegistry.getInstrumentation().getContext();

    private PFASQLiteHelper database;

    @Test
    public void useAppContext() {
        Assert.assertEquals(BuildConfig.APPLICATION_ID, appContext.getPackageName());
    }

    @Before
    public void setupDatabase() {
        database = PFASQLiteHelper.getInstance(appContext);
        clearDatabase();
    }

    @After
    public void clearDatabase() {
        List<CityToWatch> ctw = database.getAllCitiesToWatch();
        for(CityToWatch c : ctw) {
            database.deleteCityToWatch(c);
        }

        List<CurrentWeatherData> cw = database.getAllCurrentWeathers();
        for(CurrentWeatherData c : cw) {
            database.deleteCurrentWeather(c);
        }

        List<Forecast> fl = database.getAllForecasts();
        for(Forecast f : fl) {
            database.deleteForecast(f);
        }
    }

    @Test
    public void addCityToWatchTest() {
        // no cities in list be4
        assertEquals(0, database.getAllCitiesToWatch().size());

        // add city
        CityToWatch c = new CityToWatch(0, "DE", 0, 2938913, "Darmstadt");
        database.addCityToWatch(c);

        // now one city
        List<CityToWatch> result = database.getAllCitiesToWatch();
        assertEquals(1, result.size());
        assertEquals(2938913, result.get(0).getCityId());
    }

    @Test
    public void getRecommendationsTest() {
        List<City> possibleCities = database.getCitiesWhereNameLike("Frankfurt", 10);
        assertEquals(5 , possibleCities.size());

        List<String> possibleCityNames = Arrays.asList("Frankfurt am Main", "Frankfurt (Oder)", "Frankfurt Main Flughafen", "Frankfurter Vorstadt");

        for(City c : possibleCities) {
            assertEquals("DE", c.getCountryCode());
            assertTrue(possibleCityNames.contains(c.getCityName()));
        }
    }

    @Test
    public void migrationTest4_5() {
        // TODO: PF_WEATHER_DB original database with schema 1 in test assets to be able to test migrations
        SQLiteDatabase db = new PFASQLiteHelper(testContext, "PF_WEATHER_DB_4.db", null, 4).getWritableDatabase();
        assertNotNull(db);
    }

}