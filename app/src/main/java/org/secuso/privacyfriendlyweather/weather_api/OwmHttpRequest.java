package org.secuso.privacyfriendlyweather.weather_api;

import android.text.TextUtils;

import org.secuso.privacyfriendlyweather.orm.CityToWatch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OwmHttpRequest {

    /**
     * Joins the city IDs of the given cities by separating is using commas.
     *
     * @param cities A list of cities to build the groupID for.
     * @return Returns a comma-separated list of city IDs.
     */
    protected String joinCityIDs(List<CityToWatch> cities) {
        List<Integer> cityIDs = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            cityIDs.add(cities.get(i).getCity().getCityId());
        }
        return TextUtils.join(",", cityIDs);
    }

    /**
     * Builds the URL for the OpenWeatherMap API that can be used to query the weather for multiple
     * cities.
     *
     * @param groupID A list of comma-separated city IDs.
     * @return Returns the URL that can be used to query the weather for the given cities.
     */
    protected String getUrlForQueryingGroupIDs(String groupID) {
        return String.format(
                "%sgroup?id=%s&units=metric&appid=%s",
                OwmApiData.BASE_URL,
                groupID,
                OwmApiData.API_KEY
        );
    }

    /**
     * Builds the URL for the OpenWeatherMap API that can be used to query the weather for a single
     * citi.
     *
     * @param cityId The ID of the city to get the data for.
     * @return Returns the URL that can be used to query the weather for the given citiy.
     */
    protected String getUrlForQueryingSingleCity(int cityId) {
        return String.format(
                "%sweather?id=%s&units=metric&appid=%s",
                OwmApiData.BASE_URL,
                cityId,
                OwmApiData.API_KEY
        );
    }

}
