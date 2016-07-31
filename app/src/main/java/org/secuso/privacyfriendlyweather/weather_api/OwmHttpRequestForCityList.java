package org.secuso.privacyfriendlyweather.weather_api;

import android.content.Context;
import android.text.TextUtils;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for all stored cities.
 */
public class OwmHttpRequestForCityList implements IHttpRequestForCityList {

    private Context context;

    public OwmHttpRequestForCityList(Context context) {
        this.context = context;
    }

    /**
     * Joins the city IDs of the given cities by separating is using commas.
     *
     * @param cities A list of cities to build the groupID for.
     * @return Returns a comma-separated list of city IDs.
     */
    private String joinCityIDs(List<CityToWatch> cities) {
        List<Integer> cityIDs = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            cityIDs.add(cities.get(i).getCity().getCityId());
        }
        return TextUtils.join(",", cityIDs);
    }

    /**
     * @see IHttpRequestForCityList#perform(List)
     */
    @Override
    public void perform(List<CityToWatch> cities) {
        // Build the groupID
        String groupID = joinCityIDs(cities);
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = String.format(
                "%sgroup?id=%s&units=metric&appid=%s",
                OwmApiData.BASE_URL,
                groupID,
                OwmApiData.API_KEY
        );
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmUpdateCityListRequest(context));
    }

}
