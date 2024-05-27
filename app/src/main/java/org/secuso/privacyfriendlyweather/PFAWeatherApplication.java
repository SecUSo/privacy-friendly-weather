package org.secuso.privacyfriendlyweather;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;

import org.secuso.privacyfriendlybackup.api.pfa.BackupManager;
import org.secuso.privacyfriendlyweather.backup.BackupCreator;
import org.secuso.privacyfriendlyweather.backup.BackupRestorer;


public class PFAWeatherApplication extends MultiDexApplication implements Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();
        BackupManager.setBackupCreator(new BackupCreator());
        BackupManager.setBackupRestorer(new BackupRestorer());
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build();
    }
}