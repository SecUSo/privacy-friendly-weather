package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.orm.City;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.ui.ListView.ForecastAdapter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ForecastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Get the parameters and the corresponding database entries
        int cityId = getIntent().getIntExtra("cityId", -1);
        Calendar day = (Calendar) getIntent().getSerializableExtra("day");

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Forecast> forecasts = new ArrayList<>();


        String cityName = "";
        if (cityId != -1) {
            // Retrieve the passed city ID and then the database records; also display the name of the
            // city
            try {
                City city = dbHelper.getCityByID(cityId);
                if (city != null) {
                    cityName = city.getCityName();
                }

                forecasts = dbHelper.getForecastForCityByDay(cityId, day.getTime());
            } catch (SQLException e) {
                e.printStackTrace();
                // TODO: Define the error case
            }
        }

        DateFormat dateFormatter = new SimpleDateFormat("dd.MM");
        String heading = String.format(
                "%s %s",
                cityName,
                dateFormatter.format(day.getTime())
        );
        TextView tvHeading = (TextView) findViewById(R.id.activity_forecast_heading);
        tvHeading.setText(heading);

        initializeListView(forecasts);
    }

    /**
     * @param forecasts The records to initially render.
     */
    private void initializeListView(List<Forecast> forecasts) {
        ForecastAdapter forecastAdapter = new ForecastAdapter(
                ForecastActivity.this,
                0,
                forecasts
        );
        ListView listViewForecasts = (ListView) findViewById(R.id.activity_forecast_list_view_forecasts);
        listViewForecasts.setAdapter(forecastAdapter);
        listViewForecasts.setSelection(forecasts.size() >> 1);
    }

}
