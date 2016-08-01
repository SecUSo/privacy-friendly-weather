package org.secuso.privacyfriendlyweather.ui;

import org.secuso.privacyfriendlyweather.orm.CityToWatch;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;

import java.sql.SQLException;
import java.util.List;

/**
 * This class provides various methods for updating data which are to be displayed in the UI,
 * e.g. with the latest weather data or forecasts..
 */
public class DataUpdater {

    /**
     * Constants.
     */
    private final String DEBUG_TAG = "ui_updated_debug";

    /**
     * Member variables
     */
    private DatabaseHelper dbHelper;

    /**
     * Constructor.
     *
     * @param dbHelper A DatabaseHelper instance in order to perform database queries.
     */
    public DataUpdater(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * @param apiToUse An implementation of IHttpRequestForCityList which performs the HTTP request
     *                 to the weather API.
     */
    public void updateCurrentWeatherData(IHttpRequestForCityList apiToUse) {
        try {
            // Clear the CurrentWeatherData table
            int count = dbHelper.clearCurrentWeatherDataTable();
            // TODO: Get only those cities where the last CurrentWeatherData is older than one hour
            // Get all the added cities and build the groupID for the HTTP request
            List<CityToWatch> cityToWatches = dbHelper.getCityToWatchDao().queryForAll();
            apiToUse.perform(cityToWatches);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
