package org.secuso.privacyfriendlyweather.ui.viewPager;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.WeatherCityFragment;
import org.secuso.privacyfriendlyweather.ui.updater.IUpdateableCityUI;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static androidx.core.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.CHART;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.ERROR;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.CityWeatherAdapter.WEEK;

/**
 * Created by thomagglaser on 07.08.2017.
 */

public class WeatherPagerAdapter extends FragmentStatePagerAdapter implements IUpdateableCityUI {

    private Context mContext;

    private AppDatabase database;
    AppPreferencesManager prefManager;
    long lastUpdateTime;

    private List<CityToWatch> cities;
    private List<CurrentWeatherData> currentWeathers;

    private static int[] mDataSetTypes = {OVERVIEW, DETAILS, DAY, WEEK, CHART}; //TODO Make dynamic from Settings
    private static int[] errorDataSetTypes = {ERROR};

    public WeatherPagerAdapter(Context context, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.mContext = context;
        this.prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        this.database = AppDatabase.getInstance(context);
        this.currentWeathers = database.currentWeatherDao().getAll();
        this.cities = database.cityToWatchDao().getAll();
        try {
            cities = database.cityToWatchDao().getAll();
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
        CurrentWeatherData cityWeather = getDataForID(cities.get(position).getCityId());
        if (cityWeather == null) {
            cityWeather = new CurrentWeatherData();
            cityWeather.setCity_id(cities.get(position).getCityId());
            cityWeather.setTimestamp(System.currentTimeMillis() / 1000);
            cityWeather.setWeatherID(0);
            cityWeather.setTemperatureCurrent(0);
            cityWeather.setHumidity(0);
            cityWeather.setPressure(0);
            cityWeather.setWindSpeed(0);
            cityWeather.setWindDirection(0);
            cityWeather.setCloudiness(0);
            cityWeather.setTimeSunrise(System.currentTimeMillis() / 1000);
            cityWeather.setTimeSunset(System.currentTimeMillis() / 1000);
            cityWeather.setTimeZoneSeconds(0);
            cityWeather.setRain60min("000000000000");
        }
        return WeatherCityFragment.newInstance(cityWeather, mDataSetTypes);
    }

    private CurrentWeatherData getDataForID(int cityID) {
        for (CurrentWeatherData data : currentWeathers) {
            if (data.getCity_id() == cityID) return data;
        }
        return null;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {             //TODO: Remove, no longer needed. City is shown on TAB, time is now shown in card details,  as there is more space
//        GregorianCalendar calendar = new GregorianCalendar();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//        dateFormat.setCalendar(calendar);
//        calendar.setTimeInMillis(lastUpdateTime*1000);
        if (cities.size() == 0) {
            return mContext.getString(R.string.app_name);
        }
        return cities.get(position).getCityName(); // + " (" + dateFormat.format(calendar.getTime()) + ")";
    }

    //TODO: Remove, no longer needed. City is shown on TAB, time is now shown in card details,  as there is more space
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

    public void refreshSingleData(Boolean asap, int cityId) {
        Intent intent = new Intent(mContext, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_SINGLE_ACTION);
        intent.putExtra(SKIP_UPDATE_INTERVAL, asap);
        intent.putExtra("cityId", cityId);
        enqueueWork(mContext, UpdateDataService.class, 0, intent);
    }

    @Override
    public void processNewWeatherData(CurrentWeatherData data) {
        //TODO lastupdatetime might be used for more cities than it reflects
        // (update could be for any city, still other cities dont get updated)
        lastUpdateTime = System.currentTimeMillis() / 1000;
        int id = data.getCity_id();
        CurrentWeatherData old = getDataForID(id);
        if (old != null) currentWeathers.remove(old);
        currentWeathers.add(data);
        notifyDataSetChanged();
    }

    @Override
    public void updateForecasts(List<Forecast> forecasts) {
        //empty because Fragments are subscribers themselves
    }

    @Override
    public void updateWeekForecasts(List<WeekForecast> forecasts) {
        //empty because Fragments are subscribers themselves
    }

    @Override
    public void abortUpdate() {
        //empty because doesn't need to change something if update is aborted
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

    public boolean hasCityInside(int cityID) {
        for (int i = 0; i < cities.size(); i++) {
            CityToWatch city = cities.get(i);
            if (city.getCityId() == cityID) {
                return true;
            }
        }
        return false;
    }

    public float getLatForPos(int pos) {
        CityToWatch city = cities.get(pos);
        return city.getLatitude();
    }

    public float getLonForPos(int pos) {
        CityToWatch city = cities.get(pos);
        return city.getLongitude();
    }

    public void addCityFromDB(int cityID) {
        CityToWatch newCity = database.cityToWatchDao().getCityToWatchById(cityID);
        if (newCity != null) {
            cities.add(cities.size(), newCity);
            notifyDataSetChanged();
        }
    }


}