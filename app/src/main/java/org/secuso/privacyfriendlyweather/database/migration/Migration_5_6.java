package org.secuso.privacyfriendlyweather.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 5 to 6.
 *
 * @author Noah Schlegel
 * @see ContextAwareMigration
 */
public class Migration_5_6 extends Migration {

    public Migration_5_6() {
        super(5, 6);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("ALTER TABLE CURRENT_WEATHER ADD COLUMN rain60min TEXT;");
        database.execSQL("CREATE TABLE WEEKFORECASTS(" +
                "forecast_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "weather_id INTEGER NOT NULL," +
                "city_id INTEGER NOT NULL, " +
                "cities_id INTEGER, " +
                "city_name TEXT, " +
                "country_code TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "time_of_measurement INTEGER NOT NULL, " +
                "forecastTime INTEGER NOT NULL, " +
                "temperature_current REAL NOT NULL, " +
                "temperature_min REAL NOT NULL, " +
                "temperature_max REAL NOT NULL, " +
                "humidity REAL NOT NULL, " +
                "pressure REAL NOT NULL, " +
                "precipitation REAL NOT NULL, " +
                "wind_speed REAL NOT NULL, " +
                "wind_direction REAL NOT NULL, " +
                "uv_index REAL NOT NULL, " +
                "FOREIGN KEY(`city_id`) REFERENCES `CITIES`(`cities_id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                ");"
        );
    }
}
