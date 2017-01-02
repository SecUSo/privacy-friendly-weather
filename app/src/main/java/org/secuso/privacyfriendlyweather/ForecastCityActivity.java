package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.WEEK;

public class ForecastCityActivity extends BaseActivity {

    private static CurrentWeatherData currentWeatherData = null;

    private RecyclerView mRecyclerView;
    private CityWeatherAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private CurrentWeatherData currentWeatherDataList;

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, WEEK, DAY, SUN}; //TODO Make dynamic from Settings


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivity);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //TODO Get dataset from DB
        currentWeatherDataList = new CurrentWeatherData(1, 1, 12345678910L, 1, 42, 40, 44, 85, 1000, 85, 1, 1, 12345678910L, 12345678910L);
        mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, getBaseContext());
        mRecyclerView.setAdapter(mAdapter);

        //TODO Change to city name from DB
        setTitle("Darmstadt");

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

}

