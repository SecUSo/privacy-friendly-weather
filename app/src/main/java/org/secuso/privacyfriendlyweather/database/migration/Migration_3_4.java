package org.secuso.privacyfriendlyweather.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 3 to 4. It replaces previously equal values of rank with the insert order,
 * because the rank is used to sort cities from this point on.
 *
 * @author Noah Schlegel
 * @see ContextAwareMigration
 */
public class Migration_3_4 extends Migration {

    public Migration_3_4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("UPDATE CITIES_TO_WATCH SET rank=ROWID;");
    }
}
