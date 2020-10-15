package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for all stored cities.
 */
public class OwmHttpRequestForUpdatingCityList extends OwmHttpRequest implements IHttpRequestForCityList {

    private Context context;

    /**
     * @param context
     */
    public OwmHttpRequestForUpdatingCityList(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequestForCityList#perform(List)
     */
    @Override
    public void perform(List<CityToWatch> cities) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        String API_KEY = prefManager.getOWMApiKey(context);
        if (API_KEY.equals(BuildConfig.DEFAULT_API_KEY)) {        //user without own API_KEY -> use group call to reduece number of API calls
            final String URL = getUrlForQueryingGroupIDs(context, joinCityIDs(cities));
            httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmUpdateCityListRequest(context));
        } else {                                                  //user with own API_KEY -> allow download via single calls, resulting in more calls
            for (int i = 0; i < cities.size(); i++) {
                final String URL = getUrlForQueryingSingleCity(context, cities.get(i).getCityId(), true);
                httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmUpdateSingleCityRequest(context));
            }
        }
    }

}
