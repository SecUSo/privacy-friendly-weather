package org.secuso.privacyfriendlyweather.ui.viewPager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.ERROR;

/**
 * Created by thomagglaser on 07.08.2017.
 */

public class WeatherPagerAdapter extends PagerAdapter implements IUpdateableCityUI {

    private Context mContext;

    private PFASQLiteHelper database;
    PrefManager prefManager;
    long lastUpdateTime;

    private List<CityToWatch> cities;

    public static final String SKIP_UPDATE_INTERVAL= "skipUpdateInterval";

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, DAY, WEEK, SUN}; //TODO Make dynamic from Settings

    private int errorDataSetTypes[] = {ERROR};

    private List<RecyclerView> mRecyclerViews = new ArrayList<>();
    private List<CityWeatherAdapter> mAdapters = new ArrayList<>();

    public WeatherPagerAdapter(Context context) {
        this.mContext = context;

        database = PFASQLiteHelper.getInstance(context);

        cities = database.getAllCitiesToWatch();

        prefManager = new PrefManager(context);

        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        float density = mContext.getResources().getDisplayMetrics().density;
        float width = widthPixels / density;

        int columns = width > 500 ? 2 : 1;

        RecyclerView mRecyclerView1 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity1);
        mRecyclerView1.setLayoutManager(new GridLayoutManager(mRecyclerView1.getContext(), columns));

        RecyclerView mRecyclerView2 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity2);
        mRecyclerView2.setLayoutManager(new GridLayoutManager(mRecyclerView2.getContext(), columns));

        RecyclerView mRecyclerView3 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity3);
        mRecyclerView3.setLayoutManager(new GridLayoutManager(mRecyclerView3.getContext(), columns));

        RecyclerView mRecyclerView4 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity4);
        mRecyclerView4.setLayoutManager(new GridLayoutManager(mRecyclerView4.getContext(), columns));

        RecyclerView mRecyclerView5 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity5);
        mRecyclerView5.setLayoutManager(new GridLayoutManager(mRecyclerView5.getContext(), columns));

        RecyclerView mRecyclerView6 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity6);
        mRecyclerView6.setLayoutManager(new GridLayoutManager(mRecyclerView6.getContext(), columns));

        RecyclerView mRecyclerView7 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity7);
        mRecyclerView7.setLayoutManager(new GridLayoutManager(mRecyclerView7.getContext(), columns));

        RecyclerView mRecyclerView8 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity8);
        mRecyclerView8.setLayoutManager(new GridLayoutManager(mRecyclerView8.getContext(), columns));

        RecyclerView mRecyclerView9 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity9);
        mRecyclerView9.setLayoutManager(new GridLayoutManager(mRecyclerView9.getContext(), columns));

        RecyclerView mRecyclerView10 = (RecyclerView) ((ForecastCityActivity) context).findViewById(R.id.recyclerViewActivity10);
        mRecyclerView10.setLayoutManager(new GridLayoutManager(mRecyclerView10.getContext(), columns));

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
        CurrentWeatherData currentWeatherDataList = loadContentFromDatabase(position);

        CityWeatherAdapter mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, ((ForecastCityActivity)mContext).getBaseContext());

        RecyclerView mRecyclerView = mRecyclerViews.get(position);
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

        CurrentWeatherData currentWeatherDataList = loadContentFromDatabase(position);

        CityWeatherAdapter mAdapter = new CityWeatherAdapter(currentWeatherDataList, mDataSetTypes, ((ForecastCityActivity)mContext).getBaseContext());

        RecyclerView mRecyclerView = mRecyclerViews.get(position);
        mRecyclerView.setAdapter(mAdapter);

        super.setPrimaryItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setCalendar(calendar);
        calendar.setTimeInMillis(lastUpdateTime*1000);

        return cities.get(position).getCityName() + " (" + dateFormat.format(calendar.getTime()) + ")";
    }

    private CurrentWeatherData loadContentFromDatabase(int position) {
        final int cityID = cities.get(position).getCityId();

        CurrentWeatherData currentWeatherData = database.getCurrentWeatherByCityId(cityID);

        if (currentWeatherData.getCity_id() == 0) {
            currentWeatherData.setCity_id(cityID);
        }

        lastUpdateTime = currentWeatherData.getTimestamp();
        ((ForecastCityActivity)mContext).setTitle(getPageTitle(position));

        return currentWeatherData;
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

        lastUpdateTime = data.getTimestamp();
        ((ForecastCityActivity)mContext).setTitle(getPageTitle(position));
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

    public int getPosForCityID(int cityID) {
        for (int i = 0; i < cities.size(); i++) {
            CityToWatch city = cities.get(i);
            if (city.getCityId() == cityID) {
                return i;
            }
        }

        return 0;
    };
}
