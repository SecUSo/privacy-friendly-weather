package org.secuso.privacyfriendlyweather.radius_search;

/**
 * Instances of this class represent locations that are to be used for evaluating the result of
 * radius searches.
 */
public class RadiusSearchItem {

    /**
     * Member variables
     */
    private String cityName;
    private int weatherCategory;
    private float temperature;

    /**
     * Constructor.
     *
     * @param cityName        The name of the city / location.
     * @param temperature     The current temperature of the location.
     * @param weatherCategory The current weather category of the location.s
     */
    public RadiusSearchItem(String cityName, float temperature, int weatherCategory) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.weatherCategory = weatherCategory;
    }

    /**
     * @return Returns the name of the city.
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * @param cityName The name of the city to set,
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return Returns the current temperature of the city.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature The current temperature of the city to set.
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * @return Returns the current weather category (numerical representation of 'rain', 'snow' etc;
     * see IApiToDatabaseConversion#WeatherCategories for details) of the city.
     */
    public int getWeatherCategory() {
        return weatherCategory;
    }

    /**
     * @param weatherCategory The numerical weather category of the city (see
     *                        IApiToDatabaseConversion#WeatherCategories for details).
     */
    public void setWeatherCategory(int weatherCategory) {
        this.weatherCategory = weatherCategory;
    }

}
