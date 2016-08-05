package org.secuso.privacyfriendlyweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * This is the activity for the current weather data of a selected city.
 */
public class CityWeatherActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "debug_city_weather_act";

    /**
     * Member variables and visual components
     */
    private ImageView iv;
    private TextView tvHeading;
    private TextView tvCategory;
    private TextView tvHumidity;
    private TextView tvPressure;
    private TextView tvWindSpeed;
    private TextView tvSunrise;
    private TextView tvSunset;
    private FloatingActionButton fabOpenDetailsActivity;

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        initializeComponents();
        setWeatherData((CurrentWeatherData) getIntent().getExtras().getParcelable("weatherData"));

        fabOpenDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CityWeatherDetailsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_bottom_to_top, R.anim.animation_bottom_to_top);
            }
        });
    }

    /**
     * Initializes the visual components
     */
    private void initializeComponents() {
        iv = (ImageView) findViewById(R.id.activity_city_weather_image_view);
        tvHeading = (TextView) findViewById(R.id.activity_city_weather_tv_heading);
        tvCategory = (TextView) findViewById(R.id.activity_city_weather_tv_category_value);
        tvHumidity = (TextView) findViewById(R.id.activity_city_weather_tv_humidity_value);
        tvPressure = (TextView) findViewById(R.id.activity_city_weather_tv_pressure_value);
        tvWindSpeed = (TextView) findViewById(R.id.activity_city_weather_tv_wind_speed_value);
        tvSunrise = (TextView) findViewById(R.id.activity_city_weather_tv_sunrise_value);
        tvSunset = (TextView) findViewById(R.id.activity_city_weather_tv_sunset_value);
        fabOpenDetailsActivity = (FloatingActionButton) findViewById(R.id.activity_city_weather_fab_open_new_activity);
    }

    /**
     * @param weatherData The weather data to display.
     */
    private void setWeatherData(CurrentWeatherData weatherData) {
        // Format the values to display
        String heading = String.format(
                "%s, %s%s",
                weatherData.getCity().getCityName(),
                Math.round(weatherData.getTemperatureCurrent()),
                "°C"
        );
        String humidity = String.format("%s %%", weatherData.getHumidity());
        String pressure = String.format("%s hPa", Math.round(weatherData.getPressure()));
        String windSpeed = String.format("%s m/s", weatherData.getWindSpeed());

        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setCalendar(calendar);
        calendar.setTimeInMillis(weatherData.getTimeSunrise() * 1000);
        String sunrise = dateFormat.format(calendar.getTime());
        calendar.setTimeInMillis(weatherData.getTimeSunset() * 1000);
        String sunset = dateFormat.format(calendar.getTime());

        // Fill with content
        iv.setImageResource(UiResourceProvider.getImageResourceForWeatherCategory(weatherData.getWeatherID()));
        tvHeading.setText(heading);
        // TODO: Need to translate this value
        tvCategory.setText(weatherData.getWeatherCategory());
        tvHumidity.setText(humidity);
        tvPressure.setText(pressure);
        tvWindSpeed.setText(windSpeed);
        tvSunrise.setText(sunrise);
        tvSunset.setText(sunset);
    }

}
