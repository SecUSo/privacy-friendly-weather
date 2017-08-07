package org.secuso.privacyfriendlyweather.ui.viewPager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.ForecastCityActivity;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;

import java.util.ArrayList;
import java.util.List;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

/**
 * Created by thomagglaser on 07.08.2017.
 */

public class WeatherPagerAdapter extends PagerAdapter implements IUpdateableCityUI {

    private Context mContext;

    private PFASQLiteHelper database;
    PrefManager prefManager;

    private List<CityToWatch> cities;

    public static final String SKIP_UPDATE_INTERVAL= "skipUpdateInterval";

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, DAY, WEEK, SUN}; //TODO Make dynamic from Settings


    private List<RecyclerView> mRecyclerViews = new ArrayList<>();
    private List<CityWeatherAdapter> mAdapters = new ArrayList<>();

    public WeatherPagerAdapter(Context context) {
        this.mContext = context;

        database = PFASQLiteHelper.getInstance(context);

        cities = database.getAllCitiesToWatch();
        Log.i("TGL", "got " + cities.size() + " cities");

        prefManager = new PrefManager(context);

        RecyclerView mRecyclerView1 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity1);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
        mRecyclerView1.setLayoutManager(mLayoutManager1);

        RecyclerView mRecyclerView2 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity2);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(context);
        mRecyclerView2.setLayoutManager(mLayoutManager2);

        RecyclerView mRecyclerView3 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity3);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(context);
        mRecyclerView3.setLayoutManager(mLayoutManager3);

        RecyclerView mRecyclerView4 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity4);
        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(context);
        mRecyclerView4.setLayoutManager(mLayoutManager4);

        RecyclerView mRecyclerView5 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity5);
        RecyclerView.LayoutManager mLayoutManager5 = new LinearLayoutManager(context);
        mRecyclerView5.setLayoutManager(mLayoutManager5);

        RecyclerView mRecyclerView6 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity6);
        RecyclerView.LayoutManager mLayoutManager6 = new LinearLayoutManager(context);
        mRecyclerView6.setLayoutManager(mLayoutManager6);

        RecyclerView mRecyclerView7 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity7);
        RecyclerView.LayoutManager mLayoutManager7 = new LinearLayoutManager(context);
        mRecyclerView7.setLayoutManager(mLayoutManager7);

        RecyclerView mRecyclerView8 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity8);
        RecyclerView.LayoutManager mLayoutManager8 = new LinearLayoutManager(context);
        mRecyclerView8.setLayoutManager(mLayoutManager8);

        RecyclerView mRecyclerView9 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity9);
        RecyclerView.LayoutManager mLayoutManager9 = new LinearLayoutManager(context);
        mRecyclerView9.setLayoutManager(mLayoutManager9);

        RecyclerView mRecyclerView10 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity10);
        RecyclerView.LayoutManager mLayoutManager10 = new LinearLayoutManager(context);
        mRecyclerView10.setLayoutManager(mLayoutManager10);

        mRecyclerViews.add(mRecyclerView1);
        mRecyclerViews.add(mRecyclerView2);
        mRecyclerViews.add(mRecyclerView3);
        mRecyclerViews.add(mRecyclerView4);
        mRecyclerViews.add(mRecyclerView5);
        mRecyclerViews.add(mRecyclerView6);
        mRecyclerViews.add(mRecyclerView7);
        mRecyclerViews.add(mRecyclerView8);
        mRecyclerViews.add(mRecyclerView9);
        mRecyclerViews.add(mRecyclerView10);

        if (prefManager.isFirstTimeLaunch()) {
            handleFirstStart();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        Log.i("TGL", "instantiate Item at pos: " + position);

        CurrentWeatherData currentWeatherDataList = loadContentFromDatabase(position);

        CityWeatherAdapter mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, ((ForecastCityActivity)mContext).getBaseContext());

        RecyclerView mRecyclerView = mRecyclerViews.get(position);
        Log.i("TGL", "got view: " + mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mAdapters.add(mAdapter);

        return mRecyclerView;
    };

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        //Log.i("TGL", "destroyItem: " + position);
        //collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Log.i("TGL", "changeItem to: " + position);

        CurrentWeatherData currentWeatherDataList = loadContentFromDatabase(position);

        Log.i("TGL", "got Weather for: " + currentWeatherDataList.getCity_id() + ": " + currentWeatherDataList.getTemperatureCurrent());

        CityWeatherAdapter mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, ((ForecastCityActivity)mContext).getBaseContext());

        RecyclerView mRecyclerView = mRecyclerViews.get(position);
        mRecyclerView.setAdapter(mAdapter);

        super.setPrimaryItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("TGL", "getPageTitle for: " + position);
        return cities.get(position).getCityName();
    }

    private CurrentWeatherData loadContentFromDatabase(int position) {
        final int cityID = cities.get(position).getCityId();

        CurrentWeatherData currentWeatherDataList = database.getCurrentWeatherByCityId(cityID);

        if (currentWeatherDataList.getCity_id() == 0) {
            currentWeatherDataList.setCity_id(cityID);
        }

        new AsyncTask<Integer, Void, CityToWatch>() {
            @Override
            protected CityToWatch doInBackground(Integer... params) {
                CityToWatch cityToWatch = database.getCityToWatch(cityID);
                ((ForecastCityActivity)mContext).setTitle(cityToWatch.getCityName());

                return cityToWatch;
            }
        }.doInBackground(cityID);

        return currentWeatherDataList;
    }

    public void handleFirstStart(){
        prefManager.setFirstTimeLaunch(false);
    }

    public void refreshData(Boolean asap) {
        Intent intent = new Intent(mContext, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_ALL_ACTION);
        if (asap) {
            intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        }
        mContext.startService(intent);
    }

    @Override
    public void updateCurrentWeather(CurrentWeatherData data) {
        final int cityID = data.getCity_id();

        int position = getPosForCityID(cityID);

        CityWeatherAdapter mAdapter = new CityWeatherAdapter(data, mDataSetTypes, ((ForecastCityActivity)mContext).getBaseContext());
        mRecyclerViews.get(position).setAdapter(mAdapter);

        mAdapters.remove(position);
        mAdapters.add(position, mAdapter);

        new AsyncTask<Integer, Void, CityToWatch>() {
            @Override
            protected CityToWatch doInBackground(Integer... params) {
                CityToWatch cityToWatch = database.getCityToWatch(cityID);
                ((ForecastCityActivity)mContext).setTitle(cityToWatch.getCityName());

                return cityToWatch;
            }
        }.doInBackground(cityID);
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        if(forecasts == null || forecasts.size() == 0) {
            return;
        }

        int cityID = forecasts.get(0).getCity_id();

        int position = getPosForCityID(cityID);

        mAdapters.get(position).updateForecastData(forecasts);
    }

    private int getPosForCityID(int cityID) {
        for (int i = 0; i < cities.size(); i++) {
            CityToWatch city = cities.get(i);
            if (city.getCityId() == cityID) {
                return i;
            }
        }

        return 0;
    };
}
