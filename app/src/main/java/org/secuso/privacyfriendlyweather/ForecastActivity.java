package org.secuso.privacyfriendlyweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.ui.ListView.ForecastAdapter;

import java.sql.SQLException;
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
        try {
            forecasts = dbHelper.getForecastForCityByDay(cityId, day.getTime());
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Define the error case
        }
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
        ListView listViewForecasts = (ListView) findViewById(R.id.list_view_weather_forecasts);
        listViewForecasts.setAdapter(forecastAdapter);
    }

}
