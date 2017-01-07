package org.secuso.privacyfriendlyweather.weather_api;

import org.secuso.privacyfriendlyweather.database.CityToWatch;

import java.util.List;

/**
 * This generic interface is for making an HTTP request to some weather API, process the data and
 * finally trigger some mechanism to update the UI.
 */
public interface IHttpRequestForCityList {

    /**
     * @param cities   A list of CityToWatch objects to get the latest weather data for. These data
     *                 are then displayed on the front page of the app.
     */
    void perform(List<CityToWatch> cities);

}
