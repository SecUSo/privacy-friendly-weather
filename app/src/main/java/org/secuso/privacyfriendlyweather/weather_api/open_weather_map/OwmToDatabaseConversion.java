package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;

/**
 * This class implements the IApiToDatabaseConversion interface for the OpenWeatherMap API.
 */
public class OwmToDatabaseConversion extends IApiToDatabaseConversion {

    /**
     * @see IApiToDatabaseConversion#convertWeatherCategory(String)
     * https://openweathermap.org/weather-conditions
     */
    @Override
    public int convertWeatherCategory(String category) {
        int value = Integer.parseInt(category);
        if (value >= 200 && value <= 299) {
            return WeatherCategories.THUNDERSTORM.getNumVal();
        } else if (value >= 300 && value <= 399) {
            return WeatherCategories.DRIZZLE_RAIN.getNumVal();
        } else if (value == 500) {
            return WeatherCategories.LIGHT_RAIN.getNumVal();
        } else if (value == 501) {
            return WeatherCategories.MODERATE_RAIN.getNumVal();
        } else if (value >= 502 && value <= 599) {
            return WeatherCategories.RAIN.getNumVal();
        } else if (value >= 600 && value <= 699) {
            return WeatherCategories.SNOW.getNumVal();
        } else if (value >= 700 && value <= 799) {
            return WeatherCategories.MIST.getNumVal();
        } else if (value == 800) {
            return WeatherCategories.CLEAR_SKY.getNumVal();
        } else if (value == 801) {
            return WeatherCategories.CLOUDS.getNumVal();
        } else if (value == 802) {
            return WeatherCategories.SCATTERED_CLOUDS.getNumVal();
        } else if (value == 803) {
            return WeatherCategories.BROKEN_CLOUDS.getNumVal();
        }
        // Fallback: Clouds
        return WeatherCategories.OVERCAST_CLOUDS.getNumVal();
    }

}
