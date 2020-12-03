package org.secuso.privacyfriendlyweather.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlyweather.database.AppDatabase.DB_NAME;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private static final String TAG = "AppTest";
    private static final Context appContext =  InstrumentationRegistry.getInstrumentation().getTargetContext();
    private static final Context testContext =  InstrumentationRegistry.getInstrumentation().getContext();

    @Test
    public void useAppContext() {
        Assert.assertEquals(BuildConfig.APPLICATION_ID, appContext.getPackageName());
    }

    @Before
    public void prepareDatabase() {
        appContext.getDatabasePath(DB_NAME).delete();
        appContext.getDatabasePath("PF_WEATHER_DB_4.db").delete();
    }

    @Rule
    public MigrationTestHelper testHelper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory()
    );

    private AppDatabase getMigratedRoomDatabase() {
        AppDatabase database = AppDatabase.getInstance(appContext);
        // close the database and release any stream resources when the test finishes
        testHelper.closeWhenFinished(database);
        return database;
    }

    /*
    @Test
    public void testMigration4_5() throws IOException {
        PFASQLiteHelper dbHandler = new PFASQLiteHelper(testContext, "PF_WEATHER_DB_4.db", appContext.getApplicationInfo().dataDir + "/databases", null, 4);

        // init database
        dbHandler.getAllCitiesToWatch();
        dbHandler.close();

        // migration
        testHelper.runMigrationsAndValidate("PF_WEATHER_DB_4.db", 6, true, AppDatabase.getMigrations(appContext));
    }
     */

    @Test
    public void testMigration4_6() throws IOException {
        PFASQLiteHelper dbHandler = new PFASQLiteHelper(testContext, "PF_WEATHER_DB_4.db", appContext.getApplicationInfo().dataDir + "/databases", null, 4);

        // init database
        dbHandler.getAllCitiesToWatch();
        dbHandler.close();

        // migration
        testHelper.runMigrationsAndValidate("PF_WEATHER_DB_4.db", 6, true, AppDatabase.getMigrations(appContext));

        AppDatabase appDatabase = Room.databaseBuilder(appContext, AppDatabase.class, "PF_WEATHER_DB_4.db").build();
        // close the database and release any stream resources when the test finishes
        testHelper.closeWhenFinished(appDatabase);

        assertNotNull(appDatabase.cityToWatchDao().getAll());
        assertNotNull(appDatabase.forecastDao().getAll());
        assertNotNull(appDatabase.currentWeatherDao().getAll());
        assertEquals(209579, appDatabase.cityDao().count()); // make sure all the cities are written correctly
    }

    @Test
    public void testRecommendationsTest() {
        AppDatabase appDatabase = getMigratedRoomDatabase();

        // are all the cities in the database?
        assertEquals(209579, appDatabase.cityDao().count());

        // get recommendations
        List<City> possibleCities = appDatabase.cityDao().getCitiesWhereNameLike("Frankfurt", 10);
        assertEquals(5, possibleCities.size());

        List<String> possibleCityNames = Arrays.asList("Frankfurt am Main", "Frankfurt (Oder)", "Frankfurt Main Flughafen", "Frankfurter Vorstadt");

        for (City c : possibleCities) {
            assertEquals("DE", c.getCountryCode());
            assertTrue(possibleCityNames.contains(c.getCityName()));
        }
    }

    @Test
    public void currentWeatherInsertDeleteTest() {
        int cityID = 833;

        CurrentWeatherData cwd = new CurrentWeatherData();
        cwd.setCity_id(cityID);

        AppDatabase appDatabase = getMigratedRoomDatabase();

        appDatabase.currentWeatherDao().addCurrentWeather(cwd);

        assertNotNull(appDatabase.currentWeatherDao().getCurrentWeatherByCityId(cityID));

        appDatabase.currentWeatherDao().deleteCurrentWeatherByCityId(cityID);

        assertNull(appDatabase.currentWeatherDao().getCurrentWeatherByCityId(cityID));
    }

}