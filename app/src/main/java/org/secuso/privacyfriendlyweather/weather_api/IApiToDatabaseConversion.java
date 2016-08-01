package org.secuso.privacyfriendlyweather.weather_api;

/**
 * This interface defines a set of methods that guarantee that even the use of multiple APIs
 * result in the same data bases.
 */
public interface IApiToDatabaseConversion {

    /**
     * This enum provides a list of all available weather categories and assigns them a numerical
     * value.
     */
    enum WeatherCategories {
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
     * functionality.
     *
     * @param category The category to convert into the internal representation.
     * @return Returns 10 for clear sky, 20 for (few) clouds, 30 for scattered cloud, 40 for broken
     * clouds, 50 for shower rain, 60 for rain, 70 for thunderstorm, 80 for snow, 90 for mist.
     */
    int convertWeatherCategory(String category);

}
