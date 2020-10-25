package org.secuso.privacyfriendlyweather.database.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 5 to 6.
 *
 * @author Noah Schlegel
 * @see ContextAwareMigration
 */
public class Migration_5_6 extends ContextAwareMigration {

    public Migration_5_6() {
        super(5, 6);
    }

    @Override
    public void migrate(@NonNull Context context, @NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("ALTER TABLE CURRENT_WEATHER ADD COLUMN rain60min TEXT;");
        database.execSQL("CREATE TABLE WEEKFORECASTS(" +
                "forecast_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "city_id INTEGER, " +
                "time_of_measurement INTEGER NOT NULL, " +
                "forecastTime TEXT NOT NULL, " +
                "weather_id INTEGER NOT NULL, " +
                "temperature_current REAL, " +
                "temperature_min REAL, " +
                "temperature_max REAL, " +
                "humidity REAL, " +
                "pressure REAL, " +
                "precipitation REAL, " +
                "wind_speed REAL, " +
                "wind_direction REAL, " +
                "uv_index REAL, " +
                "FOREIGN KEY (city_id) REFERENCES CITIES(cities_id)" +
                ");"
        );
    }
}
