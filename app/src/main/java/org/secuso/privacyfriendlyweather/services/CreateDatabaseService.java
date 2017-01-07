package org.secuso.privacyfriendlyweather.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;

import java.sql.SQLException;

/**
 * This class is a background service that creates the database.
 */
public class CreateDatabaseService extends IntentService {

    /**
     * Constructor.
     */
    public CreateDatabaseService() {
        super("create-database-service");
    }

    /**
     * @see IntentService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * @see IntentService#onHandleIntent(Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //DatabaseHelper dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        //try {
            // To be honest, I did not know how to start the 'Create database' mechanism other than
            // doing some query
            //dbHelper.getCityToWatchDao().queryForAll();
            // When the database is setup, send a message to tell the receiver
            //ResultReceiver rec = intent.getParcelableExtra("receiver");
            //rec.send(Activity.RESULT_OK, new Bundle());
        //} catch (SQLException e) {
        //    e.printStackTrace();
        //}
    }

}
