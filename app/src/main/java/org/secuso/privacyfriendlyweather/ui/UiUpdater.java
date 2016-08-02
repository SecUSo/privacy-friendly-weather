package org.secuso.privacyfriendlyweather.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton class provides the functionality to update different UI components. This is useful when
 * database records were inserted or deleted.
 */
public class UiUpdater {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "ui_updated_debug_tag";

    /**
     * Member variables
     */
    private DatabaseHelper dbHelper;
    // Make it static because only one list shall exist during runtime
    private static List<CityOverviewListItem> overviewListItems = null;

    /**
     * @param context  The context in which the UI updater is to be used.
     * @param dbHelper A DatabaseHelper instance which is used to retrieve data from the database.
     */
    public UiUpdater(Context context, DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        // Initialize the overview list and corresponding the visual component only once
        if (overviewListItems == null) {
            overviewListItems = new ArrayList<>();
            ListView listView = (ListView) ((Activity) context).findViewById(R.id.listViewCities);
            CityOverviewListAdapter listAdapter = new CityOverviewListAdapter(context, R.layout.city_overview_list_item, overviewListItems);
            listView.setAdapter(listAdapter);
        }
    }

    /**
     * @param categoryNumber The category number. See IApiToDatabaseConversion#WeatherCategories
     *                       for details.
     * @return Returns the image resource that belongs to the given category number.
     */
    private int getImageResourceForWeatherCategory(int categoryNumber) {
        switch (categoryNumber) {
            case 10:
                return R.drawable.weather_icon_sunny;
            case 20:
                return R.drawable.weather_icon_sunny_with_clouds;
            case 30:
                return R.drawable.weather_icon_cloudy_scattered;
            case 40:
                return R.drawable.weather_icon_cloudy_broken;
            case 50:
                return R.drawable.weather_icon_shower_rain;
            case 60:
                return R.drawable.weather_icon_rain;
            case 70:
                return R.drawable.weather_icon_thunderstorm;
            case 80:
                return R.drawable.weather_icon_snow;
            case 90:
                return R.drawable.weather_icon_foggy;
            default:
                return R.drawable.weather_icon_sunny_with_clouds;
        }
    }

    /**
     * This private method serves the purpose of encapsulating the logic of how a list item looks.
     *
     * @param data The data that will be used to generate the new list item.
     * @return Returns the list item that can be added to the overview.
     */
    private CityOverviewListItem createNewCityOverviewListItem(CurrentWeatherData data) {
        String text = String.format("%s, %s°C", data.getCity().getCityName(), Math.round(data.getTemperatureCurrent()));
        int img = getImageResourceForWeatherCategory(data.getWeatherID());
        return new CityOverviewListItem(text, img);
    }

    /**
     * @param weatherData The current weather data that will be used to generate a new item.
     */
    public void addItemToOverview(CurrentWeatherData weatherData) {
        overviewListItems.add(createNewCityOverviewListItem(weatherData));
    }

    /**
     * Retrieves the latest data from the database and updates the list in the main activity.
     */
    public void updateCityList() {
        List<CurrentWeatherData> currentWeatherData = dbHelper.getCurrentWeatherData();
        for (CurrentWeatherData data : currentWeatherData) {
            overviewListItems.add(createNewCityOverviewListItem(data));
        }
    }

}
