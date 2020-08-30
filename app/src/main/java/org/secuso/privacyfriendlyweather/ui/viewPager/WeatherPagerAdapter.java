package org.secuso.privacyfriendlyweather.ui.viewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.WeatherCityFragment;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.support.v4.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.ERROR;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

/**
 * Created by thomagglaser on 07.08.2017.
 */

public class WeatherPagerAdapter extends FragmentStatePagerAdapter implements IUpdateableCityUI {

    private Context mContext;

    private PFASQLiteHelper database;
    PrefManager prefManager;
    long lastUpdateTime;

    private List<CityToWatch> cities;
    private List<CurrentWeatherData> currentWeathers;

    private static int[] mDataSetTypes = {OVERVIEW, DETAILS, DAY, WEEK, SUN}; //TODO Make dynamic from Settings
    private static int[] errorDataSetTypes = {ERROR};

    public WeatherPagerAdapter(Context context, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.mContext = context;
        this.prefManager = new PrefManager(context);
        this.database = PFASQLiteHelper.getInstance(context);
        this.currentWeathers = database.getAllCurrentWeathers();
        this.cities = database.getAllCitiesToWatch();
        try {
            cities = database.getAllCitiesToWatch();
            Collections.sort(cities, new Comparator<CityToWatch>() {
                @Override
                public int compare(CityToWatch o1, CityToWatch o2) {
                    return o1.getRank() - o2.getRank();
                }

            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WeatherCityFragment getItem(int position) {
        Bundle args = new Bundle();
        args.putInt("city_id", cities.get(position).getCityId());
        args.putIntArray("dataSetTypes", mDataSetTypes);

        return (WeatherCityFragment) Fragment.instantiate(mContext, WeatherCityFragment.class.getName(), args);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        GregorianCalendar calendar = new GregorianCalendar();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//        dateFormat.setCalendar(calendar);
//        calendar.setTimeInMillis(lastUpdateTime*1000);
        if (cities.size() == 0) {
            return mContext.getString(R.string.app_name);
        }
        return cities.get(position).getCityName(); // + " (" + dateFormat.format(calendar.getTime()) + ")";
    }

    public CharSequence getPageTitleForActionBar(int position) {

        int zoneseconds = 0;
        //fallback to last time the weather data was updated
        long time = lastUpdateTime;
        int currentCityId = cities.get(position).getCityId();
        //search for current city
        //TODO could time get taken from an old weatherData or is it removed on Update?
        for (CurrentWeatherData weatherData : currentWeathers) {
            if (weatherData.getCity_id() == currentCityId) {
                //set time to last update time for the city and zoneseconds to UTC difference (in seconds)
                time = weatherData.getTimestamp();
                zoneseconds += weatherData.getTimeZoneSeconds();
                break;
            }
        }
        //for formatting into time respective to UTC/GMT
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date updateTime = new Date((time + zoneseconds) * 1000L);
        return String.format("%s (%s)", getPageTitle(position), dateFormat.format(updateTime));
    }

    public void refreshData(Boolean asap) {
        Intent intent = new Intent(mContext, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_ALL_ACTION);
        intent.putExtra(SKIP_UPDATE_INTERVAL, asap);
        enqueueWork(mContext, UpdateDataService.class, 0, intent);
    }

    private CurrentWeatherData findWeatherFromID(List<CurrentWeatherData> currentWeathers, int ID) {
        for (CurrentWeatherData weather : currentWeathers) {
            if (weather.getCity_id() == ID) return weather;
        }
        return null;
    }

    @Override
    public void processNewWeatherData(CurrentWeatherData data) {
        lastUpdateTime = data.getTimestamp();
        int id = data.getCity_id();
        CurrentWeatherData old = findWeatherFromID(currentWeathers, id);
        if (old != null) currentWeathers.remove(old);
        currentWeathers.add(data);
        notifyDataSetChanged();
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        //empty because Fragments are subscribers themselves
    }

    public int getCityIDForPos(int pos) {
            CityToWatch city = cities.get(pos);
                 return city.getCityId();
    }

    public int getPosForCityID(int cityID) {
        for (int i = 0; i < cities.size(); i++) {
            CityToWatch city = cities.get(i);
            if (city.getCityId() == cityID) {
                return i;
            }
        }
        return 0;
    }
}