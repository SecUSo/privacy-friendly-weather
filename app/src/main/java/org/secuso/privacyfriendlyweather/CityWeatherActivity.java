package org.secuso.privacyfriendlyweather;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.FetchForecastDataService;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.ValueDeriver;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is the activity for the current weather data of a selected city.
 */
public class CityWeatherActivity extends AppCompatActivity {

    /**
     * Constant
     */
    private final String DEBUG_TAG = "debug_city_weather_act";
    private final int NUMBER_OF_FORECASTS = 5;

    /**
     * Member variables and visual components
     */
    private static CurrentWeatherData weatherDataToDisplay = null;
    private ImageView iv;
    private TextView[] tvForecast;
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

        if (weatherDataToDisplay == null || getIntent().hasExtra("weatherData")) {
            weatherDataToDisplay = getIntent().getExtras().getParcelable("weatherData");
        }

        setWeatherData(weatherDataToDisplay);

        fabOpenDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CityWeatherDetailsActivity.class);
                intent.putExtra("cityId", weatherDataToDisplay.getCity().getId());
                startActivity(intent);
            }
        });

        // Start a background task to retrieve and store the weather forecast data
        Intent forecastIntent = new Intent(this, FetchForecastDataService.class);
        forecastIntent.putExtra("cityId", weatherDataToDisplay.getCity().getCityId());
        startService(forecastIntent);

    }

    /**
     * Initializes the visual components
     */
    private void initializeComponents() {
        // Initialize the text views for the forecasts
        final String ACTIVITY_CITY_WEATHER_DAY = "activity_city_weather_day";
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM");
        Calendar day = Calendar.getInstance();
        tvForecast = new TextView[NUMBER_OF_FORECASTS];
        for (int i = 0; i < NUMBER_OF_FORECASTS; i++) {
            // First day: tomorrow
            day.add(Calendar.DAY_OF_MONTH, 1);
            // Labels start with 1
            String componentId = ACTIVITY_CITY_WEATHER_DAY + String.valueOf(i + 1);
            int id = getResources().getIdentifier(componentId, "id", getApplicationContext().getPackageName());
            tvForecast[i] = (TextView) findViewById(id);
            tvForecast[i].setText(dateFormatter.format(day.getTime()));
            tvForecast[i].setTag(i + 1);
            tvForecast[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the day for the forecast
                    Calendar day = Calendar.getInstance();
                    TextView thisTextView = (TextView) v;
                    day.add(Calendar.DAY_OF_MONTH, (Integer) thisTextView.getTag());
                    openForecastActivity(weatherDataToDisplay.getCity().getId(), day);
                }
            });
        }

        // All other components
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
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        // Format the values to display
        String heading = String.format(
                "%s %s%s",
                weatherData.getCity().getCityName(),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(weatherData.getTemperatureCurrent())),
                prefManager.getWeatherUnit()
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
        IApiToDatabaseConversion.WeatherCategories category = IApiToDatabaseConversion.getLabelForValue(weatherData.getWeatherID());
        ValueDeriver valueDeriver = new ValueDeriver(getApplicationContext());
        iv.setImageResource(UiResourceProvider.getImageResourceForWeatherCategory(weatherData.getWeatherID()));
        tvHeading.setText(heading);
        tvCategory.setText(valueDeriver.getWeatherDescriptionByCategory(category));
        tvHumidity.setText(humidity);
        tvPressure.setText(pressure);
        tvWindSpeed.setText(windSpeed);
        tvSunrise.setText(sunrise);
        tvSunset.setText(sunset);
    }

    /**
     * Takes a city and a day, opens the view for the forecast and displays the data for the given
     * city and day.
     *
     * @param cityId The city to retrieve the forecast data for.
     * @param day    The day to retrieve the forecast data for.
     */
    private void openForecastActivity(int cityId, Calendar day) {
        Intent forecastActivity = new Intent(CityWeatherActivity.this, ForecastActivity.class);
        forecastActivity.putExtra("cityId", cityId);
        forecastActivity.putExtra("day", day);
        startActivity(forecastActivity);
    }

}
