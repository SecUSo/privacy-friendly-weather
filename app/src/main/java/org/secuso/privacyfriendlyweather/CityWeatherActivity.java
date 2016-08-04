package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private final String DEBUG_TAG = "debug_city_weather_act";

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        // Retrieve the data that was passed on to this activity
        CurrentWeatherData weatherData = getIntent().getExtras().getParcelable("weatherData");

        // Get and set the corresponding fields
        ImageView iv = (ImageView) findViewById(R.id.activity_city_weather_image_view);
        TextView tvHeading = (TextView) findViewById(R.id.activity_city_weather_tv_heading);
        TextView tvCategory = (TextView) findViewById(R.id.activity_city_weather_tv_category_value);
        TextView tvHumidity = (TextView) findViewById(R.id.activity_city_weather_tv_humidity_value);
        TextView tvPressure = (TextView) findViewById(R.id.activity_city_weather_tv_pressure_value);
        TextView tvWindSpeed = (TextView) findViewById(R.id.activity_city_weather_tv_wind_speed_value);
        TextView tvSunrise = (TextView) findViewById(R.id.activity_city_weather_tv_sunrise_value);
        TextView tvSunset = (TextView) findViewById(R.id.activity_city_weather_tv_sunset_value);

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
