package org.secuso.privacyfriendlyweather.orm;

import java.util.Comparator;

/**
 * This class provides the logic for comparing CurrentWeatherData by their rank.
 */
public class CurrentWeatherComparator implements Comparator<CurrentWeatherData> {

    /**
     * Member variables
     */
    private DatabaseHelper dbHelper;

    /**
     * Constructor.
     *
     * @param dbHelper Database helper that is necessary to query further information for
     *                 comparison.
     */
    public CurrentWeatherComparator(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * @see Comparator#compare(Object, Object)
     */
    @Override
    public int compare(CurrentWeatherData lhs, CurrentWeatherData rhs) {
        // Get the CityToWatch for the weather data
        int lhsCityId = lhs.getCity().getId();
        int rhsCityId = rhs.getCity().getId();
        CityToWatch lhsCityToWatch = dbHelper.getCityToWatchByCityId(lhsCityId);
        CityToWatch rhsCityToWatch = dbHelper.getCityToWatchByCityId(rhsCityId);
        return (int) (lhsCityToWatch.getRank() - rhsCityToWatch.getRank());
    }

}

