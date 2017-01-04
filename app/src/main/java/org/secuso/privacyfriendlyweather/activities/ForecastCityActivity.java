package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

public class ForecastCityActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private CityWeatherAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PFASQLiteHelper database;
    private int cityID;
    private CurrentWeatherData currentWeatherDataList;

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, DAY, WEEK, SUN}; //TODO Make dynamic from Settings


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivity);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //TODO Set a better default than New York City
        //Location opened from list, not default,
        if (getIntent().hasExtra("cityId")) {
            cityID = getIntent().getIntExtra("cityId", 5128581);
            //currentWeatherDataList = database.getCurrentWeather(cityID);
        } else {
            //TODO Get dataset from DB based on cityID
            currentWeatherDataList = new CurrentWeatherData(1, 1, 12345678910L, 30, 42, 40, 44, 85, 1000, 85, 1, 1, System.currentTimeMillis(), System.currentTimeMillis());
        }
        mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, getBaseContext());
        mRecyclerView.setAdapter(mAdapter);

        //TODO Change to city name from DB, need a method to get the city name from the ID
        //currentWeatherDataList.getCity_id();
        setTitle("Darmstadt");

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

}

