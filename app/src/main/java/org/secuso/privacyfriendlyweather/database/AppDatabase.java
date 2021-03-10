package org.secuso.privacyfriendlyweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.dao.CityDao;
import org.secuso.privacyfriendlyweather.database.dao.CityToWatchDao;
import org.secuso.privacyfriendlyweather.database.dao.CurrentWeatherDao;
import org.secuso.privacyfriendlyweather.database.dao.ForecastDao;
import org.secuso.privacyfriendlyweather.database.dao.WeekForecastDao;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;
import org.secuso.privacyfriendlyweather.database.migration.ContextAwareMigration;
import org.secuso.privacyfriendlyweather.database.migration.Migration_1_2;
import org.secuso.privacyfriendlyweather.database.migration.Migration_2_3;
import org.secuso.privacyfriendlyweather.database.migration.Migration_3_4;
import org.secuso.privacyfriendlyweather.database.migration.Migration_4_5;
import org.secuso.privacyfriendlyweather.database.migration.Migration_5_6;
import org.secuso.privacyfriendlyweather.files.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Christopher Beckmann
 */
@Database(entities = {City.class, CityToWatch.class, CurrentWeatherData.class, Forecast.class, WeekForecast.class}, version = AppDatabase.VERSION)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "PF_WEATHER_DB.db";
    static final int VERSION = 6;
    static final String TAG = AppDatabase.class.getSimpleName();

    // DAOs
    public abstract CityDao cityDao();
    public abstract CityToWatchDao cityToWatchDao();
    public abstract CurrentWeatherDao currentWeatherDao();

    public abstract ForecastDao forecastDao();

    public abstract WeekForecastDao weekForecastDao();

    // INSTANCE
    private static final Object databaseLock = new Object();
    private static volatile AppDatabase INSTANCE;

    /**
     * Get list of all migrations. If they are ContextAwareMigrations, then inject the Context.
     * @param context to be injected into the ContextAwareMigrations
     * @return an array of Migrations, this can not be empty
     */
    public static Migration[] getMigrations(Context context) {
        Migration[] MIGRATIONS = new Migration[]{
                new Migration_1_2(),
                new Migration_2_3(),
                new Migration_3_4(),
                new Migration_4_5(),
                new Migration_5_6()
                // Add new migrations here
        };

        for(Migration m : MIGRATIONS) {
            if(m instanceof ContextAwareMigration) {
                ((ContextAwareMigration) m).injectContext(context);
            }
        }
        return MIGRATIONS;
    }

    public static AppDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (databaseLock) {
                if(INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(final Context context) {
        Migration[] MIGRATIONS = getMigrations(context);

        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .addMigrations(MIGRATIONS)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        // disable WAL because of the upcoming big transaction
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.disableWriteAheadLogging();
                        db.beginTransaction();

                        int cityCount = 0;
                        Cursor c = db.query("SELECT count(*) FROM CITIES");
                        if(c != null) {
                            if(c.moveToFirst()) {
                                cityCount = c.getInt(c.getColumnIndex("count(*)"));
                            }
                            c.close();
                        }

                        Log.d(TAG, "City count: " + cityCount);
                        if(cityCount == 0) {
                            fillCityDatabase(context, db);
                        }

                        // TODO: DEBUG ONLY - REMOVE WHEN DONE
                        c = db.query("SELECT count(*) FROM CITIES");
                        if(c != null) {
                            if(c.moveToFirst()) {
                                cityCount = c.getInt(c.getColumnIndex("count(*)"));
                            }
                            c.close();
                        }
                        Log.d(TAG, "City count: " + cityCount);
                    }
                })
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void fillCityDatabase(final Context context, SupportSQLiteDatabase database) {
        long startInsertTime = System.currentTimeMillis();

        InputStream inputStream = context.getResources().openRawResource(R.raw.city_list);
        try {
            FileReader fileReader = new FileReader();
            final List<City> cities = fileReader.readCitiesFromFile(inputStream);

            if (cities.size() > 0) {
                for (City c : cities) {
                    ContentValues values = new ContentValues();
                    values.put("cities_id", c.getCityId());
                    values.put("city_name", c.getCityName());
                    values.put("country_code", c.getCountryCode());
                    values.put("longitude", c.getLongitude());
                    values.put("latitude", c.getLatitude());
                    database.insert("CITIES", SQLiteDatabase.CONFLICT_REPLACE, values);
                }
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endInsertTime = System.currentTimeMillis();
        Log.d("debug_info", "Time for insert:" + (endInsertTime - startInsertTime));
    }
}
