package org.secuso.privacyfriendlyweather.ui;

import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
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
    private PFASQLiteHelper dbHelper;

    /**
     * Constructor.
     *
     * @param dbHelper A DatabaseHelper instance in order to perform database queries.
     */
    public DataUpdater(PFASQLiteHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * @param apiToUse An implementation of IHttpRequestForCityList which performs the HTTP request
     *                 to the weather API.
     */
    public void updateCurrentWeatherData(IHttpRequestForCityList apiToUse) {
        // Get all the added cities and build the groupID for the HTTP request
        List<CityToWatch> cityToWatches = dbHelper.getAllCitiesToWatch();
        apiToUse.perform(cityToWatches);
    }

}
