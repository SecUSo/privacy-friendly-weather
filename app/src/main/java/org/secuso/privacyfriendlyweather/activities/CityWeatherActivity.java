package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.FetchForecastDataService;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;
import org.secuso.privacyfriendlyweather.ui.UiUtils;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.ValueDeriver;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This is the activity for the current weather data of a selected city.
 */
public class CityWeatherActivity extends AppCompatActivity {

    /**
     * Constant
     */
    private final String DEBUG_TAG = "debug_city_weather_act";
    private final int NUMBER_OF_DAYS_IN_LIST = 6;

    /**
     * Member variables and visual components
     */
    private static CurrentWeatherData currentWeatherData = null;
    private ImageView iv;
    private TextView[] tvForecast;
    private TextView tvHeading;
    private TextView tvCategory;
    private TextView tvHumidity;
    private TextView tvPressure;
    private TextView tvWindSpeed;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvOpenDetailsActivity;
    private int tagCurrentTextViewClicked;

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        if (currentWeatherData == null || getIntent().hasExtra("weatherData")) {
            currentWeatherData = getIntent().getExtras().getParcelable("weatherData");
        }

        initializeComponents();

        setWeatherData(currentWeatherData);

        tvOpenDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open details for today
                if (tagCurrentTextViewClicked == 0) {
                    Intent intent = new Intent(getApplicationContext(), CityWeatherDetailsActivity.class);
                    intent.putExtra("cityId", currentWeatherData.getCity().getId());
                    startActivity(intent);
                }
                // Open a forecast
                else {
                    // Determine the day to display
                    Calendar day = Calendar.getInstance();
                    day.add(Calendar.DAY_OF_MONTH, tagCurrentTextViewClicked);

                    // Open the activity
                    Intent forecastActivity = new Intent(CityWeatherActivity.this, ForecastActivity.class);
                    forecastActivity.putExtra("cityId", currentWeatherData.getCity().getId());
                    forecastActivity.putExtra("day", day);
                    startActivity(forecastActivity);
                }
            }
        });

        // Start a background task to retrieve and store the weather forecast data
        Intent forecastIntent = new Intent(this, FetchForecastDataService.class);
        forecastIntent.putExtra("cityId", currentWeatherData.getCity().getCityId());
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
        tvForecast = new TextView[NUMBER_OF_DAYS_IN_LIST];
        for (int i = 0; i < NUMBER_OF_DAYS_IN_LIST; i++) {
            // Labels start with 1
            String componentId = ACTIVITY_CITY_WEATHER_DAY + String.valueOf(i + 1);
            int id = getResources().getIdentifier(componentId, "id", getApplicationContext().getPackageName());

            // Instantiate the initialize the text views; also, highlight the current day
            tvForecast[i] = (TextView) findViewById(id);
            try {
                tvForecast[i].setText(Html.fromHtml(getDayAbbreviation(day.get(Calendar.DAY_OF_WEEK))));
            } catch (IllegalAccessException e) {
                tvForecast[i].setText("??");
            }
            if (i == 0) {
                tagCurrentTextViewClicked = 0;
                tvForecast[i].setTypeface(null, Typeface.BOLD);
            }
            tvForecast[i].setTag(i);
            // Update this view with new weather data for this city and the clicked day
            tvForecast[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    // The data for today are already available
                    if (tag == 0) {
                        setWeatherData(currentWeatherData);
                    }
                    // If a day in the future was clicked, gather the data and display them
                    // afterwards
                    else {
                        Calendar day = Calendar.getInstance();
                        day.add(Calendar.DAY_OF_MONTH, tag);
                        try {
                            CurrentWeatherData weatherDataToDisplay = getWeatherDataToDisplay(day);
                            if (weatherDataToDisplay == null) {
                                Toast.makeText(getApplicationContext(), R.string.info_no_data_for_day, Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                setWeatherData(weatherDataToDisplay);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    // Highlight the current day
                    tvForecast[tagCurrentTextViewClicked].setTypeface(null);
                    tvForecast[tag].setTypeface(null, Typeface.BOLD);
                    tagCurrentTextViewClicked = tag;
                }
            });

            day.add(Calendar.DAY_OF_MONTH, 1);
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
        tvOpenDetailsActivity = (TextView) findViewById(R.id.activity_city_weather_tv_open_daily_overview);
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
        String sunrise = "-";
        if (weatherData.getTimeSunrise() < Integer.MAX_VALUE) {
            calendar.setTimeInMillis(weatherData.getTimeSunrise() * 1000);
            sunrise = dateFormat.format(calendar.getTime());
        }
        String sunset = "-";
        if (weatherData.getTimeSunset() < Integer.MAX_VALUE) {
            calendar.setTimeInMillis(weatherData.getTimeSunset() * 1000);
            sunset = dateFormat.format(calendar.getTime());
        }

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

        ViewGroup container = (GridLayout) findViewById(R.id.activity_city_weather_content_layout);
        UiUtils.makeTextViewEntirelyVisible(container, tvCategory);
    }

    /**
     * Gathers weather data for a day in the future. The data are determined as follows: All weather
     * data are used from the 3pm forecast except the temperature. The temperature will be set to
     * the highest value of the selected day.
     * NOTE, cloudiness, time sunset and time sunrise are not available and are set to
     * Integer.MAX_VALUE.
     *
     * @param day The day to retrieve the weather data for.
     * @return Returns an instance of CurrentWeatherData with the values of the specified day. If
     * there are no data for the requested day, null will be returned.
     */
    private CurrentWeatherData getWeatherDataToDisplay(Calendar day) throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Forecast> forecastData = dbHelper.getForecastForCityByDay(currentWeatherData.getCity().getId(), day.getTime());

        // Try to use the 3pm data (fallback is the last record for the day)
        final int INDEX_3_PM = 5;
        final int INDEX = (forecastData.size() > INDEX_3_PM) ? INDEX_3_PM : (forecastData.size() - 1);
        if (INDEX == -1) {
            return null;
        } else {
            Forecast forecastToUse = forecastData.get(INDEX);

            // Set the new data
            CurrentWeatherData newWeatherData = new CurrentWeatherData();
            newWeatherData.setCity(forecastToUse.getCity());
            newWeatherData.setTimestamp(forecastToUse.getForecastTime().getTime());
            newWeatherData.setWeatherID(forecastToUse.getWeatherID());
            newWeatherData.setTemperatureCurrent(forecastToUse.getTemperature());
            newWeatherData.setTemperatureMin(forecastToUse.getTemperature());
            newWeatherData.setTemperatureMax(forecastToUse.getTemperature());
            newWeatherData.setHumidity(forecastToUse.getHumidity());
            newWeatherData.setPressure(forecastToUse.getPressure());
            newWeatherData.setWindSpeed(forecastToUse.getWindSpeed());
            newWeatherData.setWindDirection(forecastToUse.getWindDirection());
            newWeatherData.setCloudiness(Integer.MAX_VALUE);
            newWeatherData.setTimeSunrise(Integer.MAX_VALUE);
            newWeatherData.setTimeSunset(Integer.MAX_VALUE);
            // Get the maximum and minumum temperature of the day
            for (Forecast forecast : forecastData) {
                if (forecast.getTemperature() > newWeatherData.getTemperatureCurrent()) {
                    newWeatherData.setTemperatureCurrent(forecast.getTemperature());
                    newWeatherData.setTemperatureMax(forecast.getTemperature());
                }
                if (forecast.getTemperature() < newWeatherData.getTemperatureMin()) {
                    newWeatherData.setTemperatureMin(forecast.getTemperature());
                }
            }

            return newWeatherData;
        }
    }

    /**
     * @param day The day o the week to get the abbreviation for. Sunday has value 1 and Saturday
     *            value 7.
     * @return Returns the abbreviation of the given day of the week in the language that the device
     * uses (with fallback to English).
     * @throws IllegalAccessException If an invalid day value will be passed (< 1 or > 7) this
     *                                exception will be thrown.
     */
    private String getDayAbbreviation(int day) throws IllegalAccessException {
        if (day < 1 || day > 7) {
            throw new IllegalAccessException("Day ");
        }

        int resId = 0;
        switch (day) {
            case 1:
                resId = R.string.abbreviation_sunday;
                break;
            case 2:
                resId = R.string.abbreviation_monday;
                break;
            case 3:
                resId = R.string.abbreviation_tuesday;
                break;
            case 4:
                resId = R.string.abbreviation_wednesday;
                break;
            case 5:
                resId = R.string.abbreviation_thursday;
                break;
            case 6:
                resId = R.string.abbreviation_friday;
                break;
            case 7:
                resId = R.string.abbreviation_saturday;
                break;
        }
        return getResources().getString(resId);
    }

}
