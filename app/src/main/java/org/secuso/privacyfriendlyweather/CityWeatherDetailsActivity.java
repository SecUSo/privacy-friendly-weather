package org.secuso.privacyfriendlyweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.orm.City;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.ui.ListView.ForecastAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the activity for displaying weather data of today for a city.
 */
public class CityWeatherDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather_details);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        List<Forecast> forecasts = new ArrayList<>();

        // Retrieve the passed city ID and then the database records; also display the name of the
        // city
        String heading = "";
        int cityId = getIntent().getIntExtra("cityId", -1);
        if (cityId != -1) {
            try {
                City city = dbHelper.getCityByID(cityId);
                if (city != null) {
                    heading = city.getCityName();
                }

                forecasts = dbHelper.getForecastForCityByDay(cityId, new Date());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        TextView tvHeading = (TextView) findViewById(R.id.activity_weather_details_heading);
        tvHeading.setText(heading);
        initializeListView(forecasts);
    }

    /**
     * Note, even though data of the current day are displayed the ForecastAdapter is used
     * nonetheless as the database record belong to the Forecast table!
     *
     * @param forecasts The records to initially render.
     */
    private void initializeListView(List<Forecast> forecasts) {
        ForecastAdapter forecastAdapter = new ForecastAdapter(
                CityWeatherDetailsActivity.this,
                0,
                forecasts
        );
        ListView listViewForecasts = (ListView) findViewById(R.id.list_view_weather_details);
        listViewForecasts.setAdapter(forecastAdapter);
    }

}
