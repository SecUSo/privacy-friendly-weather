package org.secuso.privacyfriendlyweather.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;

import java.util.List;


public class RainViewerActivity extends AppCompatActivity {

    private WebView webView;
    private float latitude;
    private float longitude;
    private static String API_KEY;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain_viewer);
        AppPreferencesManager prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        API_KEY = prefManager.getOWMApiKey(getApplicationContext());
        latitude = getIntent().getFloatExtra("latitude",-1);
        longitude = getIntent().getFloatExtra("longitude",-1);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/rainviewer.html?appid=" + API_KEY + "&lat=" + latitude + "&lon=" + longitude);
    }
}
