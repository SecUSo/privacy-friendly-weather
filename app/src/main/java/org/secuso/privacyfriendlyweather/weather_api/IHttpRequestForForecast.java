package org.secuso.privacyfriendlyweather.weather_api;

import org.secuso.privacyfriendlyweather.orm.City;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;

import java.util.List;

/**
 * This generic interface is for making an HTTP request to some weather API, process the data and
 * finally trigger some mechanism to update the UI.
 */
public interface IHttpRequestForForecast {

    /**
     * @param city A City object to get the weather forecast for.
     */
    void perform(City city);

}
