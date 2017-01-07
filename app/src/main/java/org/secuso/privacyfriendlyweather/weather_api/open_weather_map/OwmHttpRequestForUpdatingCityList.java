package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
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
        final String URL = getUrlForQueryingGroupIDs(joinCityIDs(cities));
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmUpdateCityListRequest(context));
    }

}
