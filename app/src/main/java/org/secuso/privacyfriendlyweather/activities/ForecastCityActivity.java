package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.ui.viewPager.WeatherPagerAdapter;

import java.util.List;

public class ForecastCityActivity extends BaseActivity implements IUpdateableCityUI {
    private WeatherPagerAdapter pagerAdapter;

    private MenuItem refreshActionButton;
    private MenuItem rainviewerButton;

    private int cityId = -1;
    private ViewPager viewPager;
    private TextView noCityText;

    @Override
    protected void onPause() {
        super.onPause();

        ViewUpdater.removeSubscriber(this);
        ViewUpdater.removeSubscriber(pagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViewUpdater.addSubscriber(this);
        ViewUpdater.addSubscriber(pagerAdapter);

        if (pagerAdapter.getCount() > 0) {  //only if at least one city is watched
            cityId = getIntent().getIntExtra("cityId", -1);
            // if Intent contains no cityId go to previous position
            if (cityId == -1) {
                cityId = pagerAdapter.getCityIDForPos(viewPager.getCurrentItem());
                //if city id was found in intent but not yet inside pageradapter (such as when added from widget)
            } else if (!pagerAdapter.hasCityInside(cityId)) {
                //manually add city
                pagerAdapter.addCityFromDB(cityId);
            }
            //TODO possible slowdown when opening Activity
            pagerAdapter.refreshSingleData(false, cityId);  //only update current tab at start
        }
        viewPager.setCurrentItem(pagerAdapter.getPosForCityID(cityId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        //cityId = getIntent().getIntExtra("cityId", -1); //done in onResume

        initResources();
        //viewPager.setAdapter(pagerAdapter);  //not needed, done below
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
             /*   if (getSupportActionBar() != null) {  //no longer needed, TODO Remove
                    getSupportActionBar().setTitle(getApplicationContext().getString(R.string.app_name));
                }*/
                pagerAdapter.refreshSingleData(false, pagerAdapter.getCityIDForPos(position));
                viewPager.setNextFocusRightId(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        // viewPager.setCurrentItem(pagerAdapter.getPosForCityID(cityId));  //not needed, done in onResume
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);


        AppDatabase db = AppDatabase.getInstance(this);
        if (db.cityToWatchDao().getAll().isEmpty()) {
            // no cities selected.. don't show the viewPager - rather show a text that tells the user that no city was selected
            viewPager.setVisibility(View.GONE);
            noCityText.setVisibility(View.VISIBLE);

        } else {
            noCityText.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            viewPager.setAdapter(pagerAdapter);
            //viewPager.setCurrentItem(pagerAdapter.getPosForCityID(cityId)); //done in onResume
        }
    }

    public void updatePageTitle() {
        if (getSupportActionBar() != null && pagerAdapter.getCount() > 0) {
            getSupportActionBar().setTitle(pagerAdapter.getPageTitleForActionBar(viewPager.getCurrentItem()));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void initResources() {
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new WeatherPagerAdapter(this, getSupportFragmentManager());
        noCityText = findViewById(R.id.noCitySelectedText);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_forecast_city, menu);

        final Menu m = menu;

        refreshActionButton = menu.findItem(R.id.menu_refresh);
        refreshActionButton.setActionView(R.layout.menu_refresh_action_view);
        refreshActionButton.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.performIdentifierAction(refreshActionButton.getItemId(), 0);
            }
        });
        rainviewerButton = menu.findItem(R.id.menu_rainviewer);
        rainviewerButton.setActionView(R.layout.menu_rainviewer_view);
        rainviewerButton.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.performIdentifierAction(rainviewerButton.getItemId(), 0);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AppDatabase db = AppDatabase.getInstance(this);
        switch (id) {
            case R.id.menu_rainviewer:
                if (!db.cityToWatchDao().getAll().isEmpty()) {  //only if at least one city is watched, otherwise crash
                    Intent intent = new Intent(this, RainViewerActivity.class);
                    intent.putExtra("latitude", pagerAdapter.getLatForPos((viewPager.getCurrentItem())));
                    intent.putExtra("longitude", pagerAdapter.getLonForPos((viewPager.getCurrentItem())));
                    startActivity(intent);
                    break;
                }
            case R.id.menu_refresh:
                if (!db.cityToWatchDao().getAll().isEmpty()) {  //only if at least one city is watched, otherwise crash
                    pagerAdapter.refreshSingleData(true, pagerAdapter.getCityIDForPos(viewPager.getCurrentItem()));

                    RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(500);
                    rotate.setRepeatCount(Animation.INFINITE);
                    rotate.setInterpolator(new LinearInterpolator());
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            refreshActionButton.getActionView().setActivated(false);
                            refreshActionButton.getActionView().setEnabled(false);
                            refreshActionButton.getActionView().setClickable(false);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            refreshActionButton.getActionView().setActivated(true);
                            refreshActionButton.getActionView().setEnabled(true);
                            refreshActionButton.getActionView().setClickable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    refreshActionButton.getActionView().startAnimation(rotate);
                    break;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //       updatePageTitle();  // TODO REMOVE, not needed anymore, Time shown in details and City is shown on TAB
    }

    @Override
    public void processNewWeatherData(CurrentWeatherData data) {
        if (refreshActionButton != null && refreshActionButton.getActionView() != null) {
            refreshActionButton.getActionView().clearAnimation();
        }
        //       updatePageTitle();  // TODO REMOVE, not needed anymore, Time shown in details and City is shown on TAB
    }

    @Override
    public void updateWeekForecasts(List<WeekForecast> forecasts) {
        if (refreshActionButton != null && refreshActionButton.getActionView() != null) {
            refreshActionButton.getActionView().clearAnimation();
        }
    }

    @Override
    public void abortUpdate() {
        if (refreshActionButton != null && refreshActionButton.getActionView() != null) {
            refreshActionButton.getActionView().clearAnimation();
        }
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        if (refreshActionButton != null && refreshActionButton.getActionView() != null) {
            refreshActionButton.getActionView().clearAnimation();
        }
    }
}

