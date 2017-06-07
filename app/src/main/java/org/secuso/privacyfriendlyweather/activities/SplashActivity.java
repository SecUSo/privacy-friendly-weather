package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.firststart.TutorialActivity;

/**
 * Created by yonjuni on 24.10.16.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PFASQLiteHelper pfasqLiteHelper = PFASQLiteHelper.getInstance(this);
        pfasqLiteHelper.getCityById(0);

        Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }

}
