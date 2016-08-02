package org.secuso.privacyfriendlyweather.weather_api;

import android.content.Context;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for all stored cities.
 */
public class OwmHttpRequestForCityToList extends OwmHttpRequest implements IHttpRequestForCityList {

    private Context context;
    private DatabaseHelper dbHelper;

    /**
     * @param context
     * @param dbHelper
     */
    public OwmHttpRequestForCityToList(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    /**
     * @see IHttpRequestForCityList#perform(List)
     */
    @Override
    public void perform(List<CityToWatch> cities) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = getUrlForQueryingSingleCity(cities.get(0).getCity().getCityId());
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmAddCityToListRequest(context, dbHelper));
    }

}
