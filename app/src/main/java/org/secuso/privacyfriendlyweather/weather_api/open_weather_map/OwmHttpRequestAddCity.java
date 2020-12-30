package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;

import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for a given city and then process the
 * response.
 */
public class OwmHttpRequestAddCity extends OwmHttpRequest implements IHttpRequestForCityList {

    private Context context;

    /**
     * @param context The application context.
     */
    public OwmHttpRequestAddCity(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequestForCityList#perform(List)
     */
    @Override
    public void perform(List<CityToWatch> cities) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = getUrlForQueryingSingleCity(context, cities.get(0).getCityId(), true);
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmAddCityRequest(context));
    }

}
