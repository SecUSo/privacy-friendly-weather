package org.secuso.privacyfriendlyweather.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil;
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer;
import org.secuso.privacyfriendlyweather.database.AppDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.secuso.privacyfriendlyweather.database.AppDatabase.DB_NAME;

public class BackupRestorer implements IBackupRestorer {
    @Override
    public boolean restoreBackup(@NotNull Context context, @NotNull InputStream restoreData) {
        try {
            InputStreamReader isReader = new InputStreamReader(restoreData);
            JsonReader reader = new JsonReader(isReader);

            reader.beginObject();

            while (reader.hasNext()) {
                String type = reader.nextName();

                switch (type) {
                    case "database":
                        readDatabase(reader, context);
                        break;
                    case "preferences":
                        readPreferences(reader, context);
                        break;
                    default:
                        throw new RuntimeException("Can not parse type " + type);
                }
            }

            reader.endObject();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void readPreferences(@NonNull JsonReader reader, @NonNull Context context) throws IOException {
        reader.beginObject();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "IsFirstTimeLaunch":
                case "AskedForOWMKey":
                case "":
                    pref.edit().putBoolean(name, reader.nextBoolean()).apply();
                    break;
                case "widgetChoice1":
                case "widgetChoice2":
                case "widgetChoice3":
                case "widgetChoice4":
                case "distanceUnit":
                case "precipitationAmountUnit":
                case "temperatureUnit":
                case "API_key_value":
                    pref.edit().putString(name, reader.nextString()).apply();
                    break;
                case "last_used_key":
                case "shared_calls_used":
                    pref.edit().putInt(name, reader.nextInt()).apply();
                    break;
                case "shared_calls_count_start":
                    pref.edit().putLong(name, reader.nextLong()).apply();
                    break;
                default:
                    throw new RuntimeException("Unknown preference " + name);
            }
        }

        reader.endObject();
    }

    private void readDatabase(JsonReader reader, Context context) throws IOException {
        reader.beginObject();

        String n1 = reader.nextName();
        if (!n1.equals("version")) {
            throw new RuntimeException("Unknown value " + n1);
        }
        int version = reader.nextInt();

        String n2 = reader.nextName();
        if (!n2.equals("content")) {
            throw new RuntimeException("Unknown value " + n2);
        }

        AppDatabase database = AppDatabase.getInstance(context);
        database.cityDao().getCityById(0);
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(DB_NAME), null);
        db.beginTransaction();
        db.setVersion(version);

        //custom readDatabaseContent
        reader.beginArray();
        while (reader.hasNext()) {
            //custom readTable
            reader.beginObject();

            // tableName
            reader.nextName();
            String tableName = reader.nextString();

            // createSql
            reader.nextName();
            String createSql = reader.nextString();
            db.execSQL("drop table if exists " + tableName);
            db.execSQL(createSql);

            // values
            reader.nextName();
            DatabaseUtil.readValues(reader, db, tableName);

            reader.endObject();
            //end of readTable part

        }
        reader.endArray();
        //end of readdatabasecontent part

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        reader.endObject();
    }
}
