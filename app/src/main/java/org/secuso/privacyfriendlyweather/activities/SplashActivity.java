package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.secuso.privacyfriendlyweather.firststart.TutorialActivity;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;

/**
 * Created by yonjuni on 24.10.16.
 */

public class SplashActivity extends AppCompatActivity {
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PFASQLiteHelper pfasqLiteHelper = PFASQLiteHelper.getInstance(this);  // TODO: what is this for?
        //pfasqLiteHelper.getCityById(0);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()){  //First time got to TutorialActivity
            Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        } else { //otherwise directly start ForecastCityActivity
            Intent mainIntent = new Intent(SplashActivity.this, ForecastCityActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        }

        SplashActivity.this.finish();
    }

}
