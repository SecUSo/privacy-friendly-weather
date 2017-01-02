package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

/**
 * This static class contains connection data for the OpenWeatherMap API.
 */
public class OwmApiData {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static String API_KEY = "3fb239a5397459ff57d21c5cc1ca1536";
    public static String DEFAULT_API_KEY = "3fb239a5397459ff57d21c5cc1ca1536";

    /**
     * Make this class static.
     */
    private OwmApiData() {
    }

    //TODO Handling of set/reseting API Keys in the Settings
    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public void resetAPI_KEY() {
        this.API_KEY = DEFAULT_API_KEY;
    }

}
