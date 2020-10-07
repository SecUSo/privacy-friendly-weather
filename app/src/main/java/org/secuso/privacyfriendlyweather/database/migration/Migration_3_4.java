package org.secuso.privacyfriendlyweather.database.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration from version 3 to 4. It replaces previously equal values of rank with the insert order,
 * because the rank is used to sort cities from this point on.
 *
 * @author Noah Schlegel
 * @see ContextAwareMigration
 */
public class Migration_3_4 extends ContextAwareMigration {

    public Migration_3_4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull Context context, @NonNull SupportSQLiteDatabase database) {
        // Cities
        database.execSQL("UPDATE CITIES_TO_WATCH SET rank=ROWID;");
    }
}
