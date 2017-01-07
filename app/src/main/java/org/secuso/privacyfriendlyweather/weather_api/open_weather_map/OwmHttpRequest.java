package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.text.TextUtils;

import org.secuso.privacyfriendlyweather.database.CityToWatch;

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
            cityIDs.add(cities.get(i).getCityId());
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
                OwmApiData.getAPI_KEY()
        );
    }

    /**
     * Builds the URL for the OpenWeatherMap API that can be used to query the weather for a single
     * city.
     *
     * @param cityId    The ID of the city to get the data for.
     * @param useMetric If set to true, temperature values will be in Celsius.
     * @return Returns the URL that can be used to query the weather for the given city.
     */
    protected String getUrlForQueryingSingleCity(int cityId, boolean useMetric) {
        return String.format(
                "%sweather?id=%s%s&appid=%s",
                OwmApiData.BASE_URL,
                cityId,
                (useMetric) ? "&units=metric" : "",
                OwmApiData.getAPI_KEY()
        );
    }

    /**
     * Builds the URL for the OpenWeatherMap API that can be used to query the weather forecast
     * for a single city.
     *
     * @param cityId The ID of the city to get the forecast data for.
     * @return Returns the URL that can be used to query weather forecasts for the given city using
     * OpenWeatherMap.
     */
    protected String getUrlForQueryingForecast(int cityId) {
        return String.format(
                "%sforecast?id=%s&units=metric&appid=%s",
                OwmApiData.BASE_URL,
                cityId,
                OwmApiData.getAPI_KEY()
        );
    }

    /**
     * Builds the URL for the OpenWeatherMap API that can be used to query the weather for several
     * cities within a given rectangle.
     *
     * @param boundingBox An array consisting of four value: The first value is the left longitude,
     *                    second bottom latitude, third right longitude, fourth top latitude.
     * @param mapZoom     The map zoom which determines the granularity of the OpenWeatherMap response.
     *                    Lower values will return less cities.
     * @return Returns the URL for the OpenWeatherMap API to retrieve the weather data of cities
     * within the specified bounding box.
     */
    public String getUrlForQueryingRadiusSearch(double[] boundingBox, int mapZoom) {
        return String.format(
                "%sbox/city?bbox=%s,%s,%s,%s,%s&cluster=yes&appid=%s",
                OwmApiData.BASE_URL,
                boundingBox[0],
                boundingBox[1],
                boundingBox[2],
                boundingBox[3],
                mapZoom,
                OwmApiData.getAPI_KEY()
        );
    }

}
