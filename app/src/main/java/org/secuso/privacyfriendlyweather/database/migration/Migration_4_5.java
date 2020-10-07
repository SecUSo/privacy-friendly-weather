package org.secuso.privacyfriendlyweather.database.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.secuso.privacyfriendlyweather.database.AppDatabase;

/**
 * Migration from version 4 to 5. This is the migration from regular SQLite to Room.
 * That is why a lot of changes had to be done here. Some tables had wrong entry types and the city list was updated.
 * Because this was the case for every table, each one is rebuilt completely and repopulated with the old data.
 * This migration needs a {@link Context} because it needs to populate the city table from a file.
 *
 * @see org.secuso.privacyfriendlyweather.database.migration.ContextAwareMigration
 *
 * @author Christopher Beckmann
 *
 */
public class Migration_4_5 extends ContextAwareMigration {

    public Migration_4_5() {
        super(4, 5);
    }

    @Override
    public void migrate(@NonNull Context context, @NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("DROP TABLE CITIES;");
        database.execSQL("CREATE TABLE IF NOT EXISTS CITIES (" +
                "cities_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "city_name TEXT NOT NULL," +
                "country_code TEXT NOT NULL," +
                "longitude REAL NOT NULL," +
                "latitude REAL NOT NULL);");

        AppDatabase.fillCityDatabase(context, database);
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_CITIES_city_name_cities_id` ON CITIES (`city_name`, `cities_id`)");

        // City to watch
        database.execSQL("CREATE TABLE IF NOT EXISTS new_CITIES_TO_WATCH " +
                "(`cities_to_watch_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`city_id` INTEGER NOT NULL, " +
                "`rank` INTEGER NOT NULL, " +
                "`cities_id` INTEGER, " +
                "`city_name` TEXT, " +
                "`country_code` TEXT, " +
                "`longitude` REAL, " +
                "`latitude` REAL, " +
                "FOREIGN KEY(`city_id`) REFERENCES `CITIES`(`cities_id`) ON UPDATE NO ACTION ON DELETE CASCADE );");
        database.execSQL("INSERT INTO new_CITIES_TO_WATCH (cities_to_watch_id, city_id, rank, cities_id, city_name, country_code, longitude, latitude) " +
                "SELECT cities_to_watch_id, city_id, rank, cities_id, city_name, country_code, longitude, latitude " +
                "FROM CITIES_TO_WATCH INNER JOIN CITIES ON CITIES_TO_WATCH.city_id = CITIES.cities_id");
        database.execSQL("DROP TABLE CITIES_TO_WATCH");
        database.execSQL("ALTER TABLE new_CITIES_TO_WATCH RENAME TO CITIES_TO_WATCH");

        // current weather data
        database.execSQL("CREATE TABLE IF NOT EXISTS new_CURRENT_WEATHER " +
                "(`current_weather_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`city_id` INTEGER NOT NULL, " +
                "`time_of_measurement` INTEGER NOT NULL, " +
                "`weather_id` INTEGER NOT NULL, " +
                "`temperature_current` REAL NOT NULL, " +
                "`temperature_min` REAL NOT NULL, " +
                "`temperature_max` REAL NOT NULL, " +
                "`humidity` REAL NOT NULL, " +
                "`pressure` REAL NOT NULL, " +
                "`wind_speed` REAL NOT NULL, " +
                "`wind_direction` REAL NOT NULL, " +
                "`cloudiness` REAL NOT NULL, " +
                "`time_sunrise` INTEGER NOT NULL, " +
                "`time_sunset` INTEGER NOT NULL, " +
                "`timezone_seconds` INTEGER NOT NULL, " +
                "FOREIGN KEY(`city_id`) REFERENCES `CITIES`(`cities_id`) ON UPDATE NO ACTION ON DELETE CASCADE );");
        database.execSQL("INSERT INTO new_CURRENT_WEATHER (current_weather_id, city_id, time_of_measurement, weather_id, temperature_current, temperature_min, temperature_max, humidity, pressure, wind_speed, wind_direction, cloudiness, time_sunrise, time_sunset, timezone_seconds) " +
                "SELECT current_weather_id, city_id, time_of_measurement, weather_id, temperature_current, temperature_min, temperature_max, humidity, pressure, wind_speed, wind_direction, cloudiness, time_sunrise, time_sunset, timezone_seconds FROM CURRENT_WEATHER");
        database.execSQL("DROP TABLE CURRENT_WEATHER");
        database.execSQL("ALTER TABLE new_CURRENT_WEATHER RENAME TO CURRENT_WEATHER");

        // forecasts
        database.execSQL("CREATE TABLE IF NOT EXISTS new_FORECASTS " +
                "(`forecast_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`city_id` INTEGER NOT NULL, " +
                "`time_of_measurement` INTEGER NOT NULL, " +
                "`forecast_for` INTEGER NOT NULL, " +
                "`weather_id` INTEGER NOT NULL, " +
                "`temperature_current` REAL NOT NULL, " +
                "`humidity` REAL NOT NULL, " +
                "`pressure` REAL NOT NULL, " +
                "`precipitation` REAL NOT NULL, " +
                "`wind_speed` REAL NOT NULL, " +
                "`wind_direction` REAL NOT NULL, " +
                "`cities_id` INTEGER, " +
                "`city_name` TEXT, " +
                "`country_code` TEXT, " +
                "`longitude` REAL, " +
                "`latitude` REAL, " +
                "FOREIGN KEY(`city_id`) REFERENCES `CITIES`(`cities_id`) ON UPDATE NO ACTION ON DELETE CASCADE );");
        database.execSQL("INSERT INTO new_FORECASTS (forecast_id, city_id, time_of_measurement, forecast_for, weather_id, temperature_current, humidity, pressure, precipitation, wind_speed, wind_direction, cities_id, city_name, country_code, longitude, latitude)" +
                "SELECT forecast_id, city_id, time_of_measurement, forecast_for, weather_id, temperature_current, humidity, pressure, precipitation, wind_speed, wind_direction, cities_id, city_name, country_code, longitude, latitude " +
                "FROM FORECASTS INNER JOIN CITIES ON FORECASTS.city_id = CITIES.cities_id");
        database.execSQL("DROP TABLE FORECASTS");
        database.execSQL("ALTER TABLE new_FORECASTS RENAME TO FORECASTS");
    }
}
