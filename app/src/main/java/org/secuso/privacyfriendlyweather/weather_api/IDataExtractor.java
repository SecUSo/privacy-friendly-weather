package org.secuso.privacyfriendlyweather.weather_api;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;

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
     * @param data The data that contains the information to retrieve the ID of the city.
     * @return Returns the ID of the city or Integer#MIN_VALUE in case the data is not well-formed
     * and the information could not be extracted.
     */
    int extractCityID(String data);

}
