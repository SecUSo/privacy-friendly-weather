package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import org.secuso.privacyfriendlyweather.ui.viewPager.WeatherPagerAdapter;

import java.util.List;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

public class ForecastCityActivity extends BaseActivity implements IUpdateableCityUI {
    private WeatherPagerAdapter pagerAdapter;

    @Override
    protected void onPause() {
        super.onPause();

        ViewUpdater.removeSubsriber(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViewUpdater.addSubsriber(this);

        pagerAdapter.refreshData(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new WeatherPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }



    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
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
            pagerAdapter.refreshData(true);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void updateCurrentWeather(CurrentWeatherData data) {

    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {

    }
}

