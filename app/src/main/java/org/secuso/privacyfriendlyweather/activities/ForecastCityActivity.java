package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;

import java.util.List;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

public class ForecastCityActivity extends BaseActivity implements IUpdateableCityUI {

    private RecyclerView mRecyclerView;
    private CityWeatherAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PFASQLiteHelper database;
    private int cityID;
    private CurrentWeatherData currentWeatherDataList;
    PrefManager prefManager;

    public static final String SKIP_UPDATE_INTERVAL= "skipUpdateInterval";

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, DAY, WEEK, SUN}; //TODO Make dynamic from Settings


    @Override
    protected void onPause() {
        super.onPause();

        ViewUpdater.removeSubsriber(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViewUpdater.addSubsriber(this);

        refreshData(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivity);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        prefManager = new PrefManager(this);
        database = PFASQLiteHelper.getInstance(this);

        if (prefManager.isFirstTimeLaunch()) {
            handleFirstStart();
        }

        //Location opened from list, not default,
        if (getIntent().hasExtra("cityId")) {
            cityID = getIntent().getIntExtra("cityId", -1);
        } else {
            cityID = prefManager.getDefaultLocation();
            //currentWeatherDataList = new CurrentWeatherData(1, 1, 12345678910L, 30, 42, 40, 44, 85, 1000, 85, 1, 1, System.currentTimeMillis(), System.currentTimeMillis());
        }

        loadContentFromDatabase();
    }

    private void loadContentFromDatabase() {
        currentWeatherDataList = database.getCurrentWeatherByCityId(cityID);

        if (currentWeatherDataList.getCity_id() == 0) {
            currentWeatherDataList.setCity_id(cityID);
        }

        mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, getBaseContext());

        mRecyclerView.setAdapter(mAdapter);

        //TODO Change to city name from DB, need a method to get the city name from the ID
        //currentWeatherDataList.getCity_id();
        CityToWatch cityToWatch = database.getCityToWatch(cityID);
        setTitle(cityToWatch.getCityName());
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

    public void handleFirstStart(){
        prefManager.setFirstTimeLaunch(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_forecast_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            refreshData(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData(Boolean asap) {
        Intent intent = new Intent(this, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_ALL_ACTION);
        if (asap) {
            intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        }
        startService(intent);
    }

    @Override
    public void updateCurrentWeather(CurrentWeatherData data) {
        if(data == null || data.getCity_id() != cityID) {
            return;
        }

        cityID = data.getCity_id();
        mAdapter = new CityWeatherAdapter(data, mDataSetTypes, getBaseContext());
        mRecyclerView.setAdapter(mAdapter);

        CityToWatch cityToWatch = database.getCityToWatch(cityID);
        setTitle(cityToWatch.getCityName());
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        if(forecasts == null || forecasts.size() == 0 || forecasts.get(0).getCity_id() != cityID) {
            return;
        }

        mAdapter.updateForecastData(forecasts);
    }
}

