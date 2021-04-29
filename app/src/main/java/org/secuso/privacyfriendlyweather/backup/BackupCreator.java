package org.secuso.privacyfriendlyweather.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.JsonWriter;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil;
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil;
import org.secuso.privacyfriendlybackup.api.pfa.IBackupCreator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import kotlin.Pair;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.secuso.privacyfriendlyweather.database.AppDatabase.DB_NAME;

public class BackupCreator implements IBackupCreator {
    @Override
    public void writeBackup(@NotNull Context context, @NotNull OutputStream outputStream) {
// lock application, so no changes can be made as long as this backup is created
        // depending on the size of the application - this could take a bit

        Log.d("PFA BackupCreator", "createBackup() started");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
        JsonWriter writer = new JsonWriter(outputStreamWriter);
        writer.setIndent("");

        try {
            writer.beginObject();
            SQLiteDatabase dataBase = SQLiteDatabase.openDatabase(context.getDatabasePath(DB_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY);

            //write database info
            Log.d("PFA BackupCreator", "Writing database");
            writer.name("database");
            writer.beginObject();
            writer.name("version").value(dataBase.getVersion());
            writer.name("content");
            //custom table writing since only one table is important
            writer.beginArray();
            List<Pair<String, String>> tableInfo = DatabaseUtil.getTables(dataBase);
            for (Pair<String, String> table : tableInfo) {
                Log.d("PFA Backup weather", "table: " + table.getFirst());
                if (!table.getFirst().contentEquals("CITIES_TO_WATCH")) {
                    continue;
                }
                Log.d("PFA Backup weather", table.toString());

                writer.beginObject();
                writer.name("tableName").value(table.getFirst());
                writer.name("createSql").value(table.getSecond());
                writer.name("values");
                DatabaseUtil.writeTable(writer, dataBase, table.getFirst());
                writer.endObject();
            }
            writer.endArray();

            //rest of write table boilerplate
            writer.endObject();
            dataBase.close();

            Log.d("PFA BackupCreator", "Writing preferences");
            writer.name("preferences");
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            PreferenceUtil.writePreferences(writer, pref);

            writer.endObject();

            writer.close();
        } catch (Exception e) {
            Log.e("PFA BackupCreator", "Error occurred", e);
            e.printStackTrace();
        }

        Log.d("PFA BackupCreator", "Backup created successfully");

    }
}
