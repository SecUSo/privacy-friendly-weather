package org.secuso.privacyfriendlyweather.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;

import java.util.List;

public class WeatherCityFragment extends Fragment implements IUpdateableCityUI {

    private int mCityId = -1;
    private int[] mDataSetTypes = new int[]{};

    private CityWeatherAdapter mAdapter;

    private RecyclerView recyclerView;


    public void setAdapter(CityWeatherAdapter adapter) {
        mAdapter = adapter;

        if(recyclerView != null) {
            recyclerView.setAdapter(mAdapter);
        }
    }

    public void loadData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                CurrentWeatherData currentWeatherData = PFASQLiteHelper.getInstance(WeatherCityFragment.this.getContext()).getCurrentWeatherByCityId(mCityId);

                if (currentWeatherData.getCity_id() == 0) {
                    currentWeatherData.setCity_id(mCityId);
                }

                mAdapter = new CityWeatherAdapter(currentWeatherData, mDataSetTypes, WeatherCityFragment.this.getContext());

                ((Activity)WeatherCityFragment.this.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(mAdapter);
                    }
                });

                return null;
            }
        }.doInBackground();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ViewUpdater.addSubsriber(this);
    }

    @Override
    public void onDetach() {
        ViewUpdater.removeSubsriber(this);

        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_forecast_city_overview, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.weatherForecastRecyclerView);
        recyclerView.setLayoutManager(getLayoutManager(getContext()));

        Bundle args = getArguments();
        mCityId = args.getInt("city_id");
        mDataSetTypes = args.getIntArray("dataSetTypes");

        loadData();

        return v;
    }

    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        float width = widthPixels / density;

        if (width > 500) {
            return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            return new LinearLayoutManager(context);
        }
    }

    @Override
    public void updateCurrentWeather(CurrentWeatherData data) {
        if(data.getCity_id() == mCityId) {
            setAdapter(new CityWeatherAdapter(data, mDataSetTypes, getContext()));
        }
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        if(forecasts != null && forecasts.size() > 0 && forecasts.get(0).getCity_id() == mCityId) {
            if (mAdapter != null) {
                mAdapter.updateForecastData(forecasts);
            }
        }
    }
}
