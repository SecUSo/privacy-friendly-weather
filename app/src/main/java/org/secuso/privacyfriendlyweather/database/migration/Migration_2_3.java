package org.secuso.privacyfriendlyweather.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 2 to 3. It adds 3 columns to the forecasts table.
 *
 * @author Noah Schlegel
 * @see ContextAwareMigration
 */
public class Migration_2_3 extends Migration {

    public Migration_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("ALTER TABLE FORECASTS ADD COLUMN precipitation REAL DEFAULT 0;");
        database.execSQL("ALTER TABLE FORECASTS ADD COLUMN wind_speed REAL DEFAULT 0;");
        database.execSQL("ALTER TABLE FORECASTS ADD COLUMN wind_direction REAL DEFAULT 0;");

    }
}
