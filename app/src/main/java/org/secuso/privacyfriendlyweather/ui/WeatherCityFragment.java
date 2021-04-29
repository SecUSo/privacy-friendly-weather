package org.secuso.privacyfriendlyweather.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;

import java.util.List;

public class WeatherCityFragment extends Fragment implements IUpdateableCityUI {

    private int mCityId = -1;
    private int[] mDataSetTypes = new int[]{};

    private CityWeatherAdapter mAdapter;

    private RecyclerView recyclerView;

    public static WeatherCityFragment newInstance(CurrentWeatherData data, int[] mDataSetTypes) {
        WeatherCityFragment WCfragment = new WeatherCityFragment();
        Bundle args = new Bundle();
        args.putInt("city_id", data.getCity_id());
        args.putIntArray("dataSetTypes", mDataSetTypes);
        args.putLong("timestamp", data.getTimestamp());
        args.putInt("weatherID", data.getWeatherID());
        args.putFloat("temperatureCurrent", data.getTemperatureCurrent());
        args.putFloat("humidity", data.getHumidity());
        args.putFloat("pressure", data.getPressure());
        args.putFloat("windSpeed", data.getWindSpeed());
        args.putFloat("windDirection", data.getWindDirection());
        args.putFloat("cloudiness", data.getCloudiness());
        args.putLong("timeSunrise", data.getTimeSunrise());
        args.putLong("timeSunset", data.getTimeSunset());
        args.putInt("timeZoneSeconds", data.getTimeZoneSeconds());
        args.putString("rain60min", data.getRain60min());

        WCfragment.setArguments(args);
        return WCfragment;
    }

    public void setAdapter(CityWeatherAdapter adapter) {
        mAdapter = adapter;

        if (recyclerView != null) {
            recyclerView.setAdapter(mAdapter);
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(getLayoutManager(getContext()));  //fixes problems with StaggeredGrid: After refreshing data only empty space shown below tab
        }
    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        ViewUpdater.addSubscriber(this);
    }

    @Override
    public void onDetach() {
        ViewUpdater.removeSubscriber(this);

        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_forecast_city_overview, container, false);

        recyclerView = v.findViewById(R.id.weatherForecastRecyclerView);
        recyclerView.setLayoutManager(getLayoutManager(getContext()));

        Bundle args = getArguments();
        mCityId = args.getInt("city_id");
        mDataSetTypes = args.getIntArray("dataSetTypes");

        CurrentWeatherData cwd = new CurrentWeatherData();
        cwd.setCity_id(mCityId);
        cwd.setCloudiness(args.getFloat("cloudiness"));
        cwd.setHumidity(args.getFloat("humidity"));
        cwd.setTimestamp(args.getLong("timestamp"));
        cwd.setTemperatureCurrent(args.getFloat("temperatureCurrent"));
        cwd.setWeatherID(args.getInt("weatherID"));
        cwd.setPressure(args.getFloat("pressure"));
        cwd.setRain60min(args.getString("rain60min"));
        cwd.setTimeZoneSeconds(args.getInt("timeZoneSeconds"));
        cwd.setTimeSunrise(args.getLong("timeSunrise"));
        cwd.setTimeSunset(args.getLong("timeSunset"));
        cwd.setWindDirection(args.getFloat("windDirection"));
        cwd.setWindSpeed(args.getFloat("windSpeed"));

        mAdapter = new CityWeatherAdapter(cwd, mDataSetTypes, getContext());

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter(mAdapter);
            }
        });
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
    public void processNewWeatherData(CurrentWeatherData data) {
        if (data.getCity_id() == mCityId) {
            //why not on UI thread as in OnCreateView?
            setAdapter(new CityWeatherAdapter(data, mDataSetTypes, getContext()));
        }
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        if (forecasts != null && forecasts.size() > 0 && forecasts.get(0).getCity_id() == mCityId) {
            if (mAdapter != null) {
                mAdapter.updateForecastData(forecasts);
            }
        }
        //TODO Update Titlebar Text
    }

    @Override
    public void updateWeekForecasts(List<WeekForecast> forecasts) {
        if (forecasts != null && forecasts.size() > 0 && forecasts.get(0).getCity_id() == mCityId) {
            if (mAdapter != null) {
                mAdapter.updateWeekForecastData(forecasts);
            }
        }
        //TODO Update Titlebar Text
    }

    @Override
    public void abortUpdate() {
        //empty because doesn't need to change something if update is aborted
    }
}
