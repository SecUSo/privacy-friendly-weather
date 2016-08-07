package org.secuso.privacyfriendlyweather.weather_api;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.Forecast;

import java.util.List;

/**
 * This interface defines the frame of the functionality to extractCurrentWeatherData weather information from which
 * is returned by some API.
 */
public interface IDataExtractor {

    /**
     * @param data The data that contains the information to instantiate a CurrentWeatherData
     *             object. In the easiest case this is the (HTTP) response of the API.
     * @return Returns the extracted information as a CurrentWeatherData instance.
     */
    CurrentWeatherData extractCurrentWeatherData(String data);

    /**
     * @param data The data that contains the information to instantiate a Forecast object, i. e. a
     *             list item of the JSON response.
     * @return Returns the extracted weather forecast information. In case some error occurs, null
     * will be returned.
     */
    Forecast extractForecast(String data);

    /**
     * @param data The data that contains the information to retrieve the ID of the city.
     * @return Returns the ID of the city or Integer#MIN_VALUE in case the data is not well-formed
     * and the information could not be extracted.
     */
    int extractCityID(String data);

}
