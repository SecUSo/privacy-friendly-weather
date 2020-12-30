package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;

/**
 * Created by yonjuni on 24.10.16.
 */

public class SplashActivity extends AppCompatActivity {
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase database = AppDatabase.getInstance(this);
        database.cityDao().getCityById(0);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {  //First time go to TutorialActivity
            //TODO make DB call async

            Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        } else if (!prefManager.askedForOWMKey()) {
            Intent keyIntent = new Intent(this, CreateKeyActivity.class);
            keyIntent.putExtra("429", false);
            this.startActivity(keyIntent);
        } else { //otherwise directly start ForecastCityActivity
            Intent mainIntent = new Intent(SplashActivity.this, ForecastCityActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        }

        SplashActivity.this.finish();
    }

}
