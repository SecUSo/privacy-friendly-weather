package org.secuso.privacyfriendlyweather.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 6 to 7. It adds the rain_probability columns to the (week)forecasts tables.
 *
 * @author Noah Schlegel
 */
public class Migration_6_7 extends Migration {

    public Migration_6_7() {
        super(6, 7);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("ALTER TABLE FORECASTS ADD COLUMN rain_probability REAL NOT NULL DEFAULT 0;");
        database.execSQL("ALTER TABLE WEEKFORECASTS ADD COLUMN rain_probability REAL NOT NULL DEFAULT 0;");
    }
}
