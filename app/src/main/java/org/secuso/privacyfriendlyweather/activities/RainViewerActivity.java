package org.secuso.privacyfriendlyweather.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.secuso.privacyfriendlyweather.R;


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
        latitude = getIntent().getFloatExtra("latitude", -1);
        longitude = getIntent().getFloatExtra("longitude", -1);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/rainviewer.html?lat=" + latitude + "&lon=" + longitude);
    }
}
