package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.WEEK;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DAY;

public class ForecastCityActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //TODO Change to DB Values
    private String[] mDataset = {"29°C", "Seahawks 24 - 27 Bengals",
            "Flash missing, vanishes in crisis", "Half Life 3 announced"};
    private int mDataSetTypes[] = {OVERVIEW, DETAILS, WEEK, DAY, SUN}; //view types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivity);
        mLayoutManager = new LinearLayoutManager(ForecastCityActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Adapter is created in the last step
        mAdapter = new ForecastAdapter(mDataset, mDataSetTypes);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

}

