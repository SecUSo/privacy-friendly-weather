package org.secuso.privacyfriendlyweather.weather_api;

/**
 * This interface class defines a set of methods that guarantee that even the use of multiple APIs
 * result in the same data bases.
 */
public abstract class IApiToDatabaseConversion {

    /**
     * This enum provides a list of all available weather categories and assigns them a numerical
     * value.
     */
    public enum WeatherCategories {
        CLEAR_SKY(10),
        CLOUDS(20),
        SCATTERED_CLOUDS(30),
        BROKEN_CLOUDS(40),
        SHOWER_RAIN(50),
        RAIN(60),
        THUNDERSTORM(70),
        SNOW(80),
        MIST(90);

        private int numVal;

        WeatherCategories(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }

    }

    /**
     * Different APIs will use different representation for weather conditions / categories.
     * Internally, they will stored uniformly. Implementation of this weather provide this
     * functionality.Category(jsonWeather.getString("main"));
     * forecast.setWeather
     *
     * @param category The category to convert into the internal representation.
     * @return Returns 10 for clear sky, 20 for (few) clouds, 30 for scattered cloud, 40 for broken
     * clouds, 50 for shower rain, 60 for rain, 70 for thunderstorm, 80 for snow, 90 for mist.
     */
    public abstract int convertWeatherCategory(String category);

    /**
     * @param value The value to get the enum label for.
     * @return Returns the label that belongs to the given value. Fallback value is CLOUDS.
     */
    public static WeatherCategories getLabelForValue(int value) {
        switch (value) {
            case 10:
                return WeatherCategories.CLOUDS.CLEAR_SKY;
            case 20:
                return WeatherCategories.CLOUDS.CLOUDS;
            case 30:
                return WeatherCategories.CLOUDS.SCATTERED_CLOUDS;
            case 40:
                return WeatherCategories.CLOUDS.BROKEN_CLOUDS;
            case 50:
                return WeatherCategories.CLOUDS.SHOWER_RAIN;
            case 60:
                return WeatherCategories.CLOUDS.RAIN;
            case 70:
                return WeatherCategories.CLOUDS.THUNDERSTORM;
            case 80:
                return WeatherCategories.CLOUDS.SNOW;
            case 90:
                return WeatherCategories.CLOUDS.MIST;
            default:
                return WeatherCategories.CLOUDS;
        }
    }

}
