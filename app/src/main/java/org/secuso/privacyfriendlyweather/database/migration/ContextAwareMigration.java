package org.secuso.privacyfriendlyweather.database.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * A context aware migration for the room database.
 *
 * @author Christopher Beckmann
 */
public abstract class ContextAwareMigration extends Migration {

    private Context mContext;
    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     *
     * @param startVersion The start version of the database.
     * @param endVersion   The end version of the database after this migration is applied.
     */
    public ContextAwareMigration(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    public void injectContext(Context context) {
        mContext = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        if(mContext == null) {
            throw new IllegalStateException("ContextAwareMigration("+startVersion+","+endVersion+") - " +
                    "The context was not yet injected, but migrate was called. " +
                    "Make sure to inject the context before using this migration.");
        }
        migrate(mContext, database);
    }

    public abstract void migrate(final Context context, @NonNull SupportSQLiteDatabase database);
}
