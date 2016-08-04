package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;

/**
 * This is the activity for the current weather data of a selected city.
 */
public class CityWeatherActivity extends AppCompatActivity {

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        // Retrieve the data that was passed on to this activity
        CurrentWeatherData weatherData = getIntent().getExtras().getParcelable("weatherData");
    }

}
