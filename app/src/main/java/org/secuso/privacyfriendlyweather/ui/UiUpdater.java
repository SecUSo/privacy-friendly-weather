package org.secuso.privacyfriendlyweather.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.secuso.privacyfriendlyweather.CityWeatherActivity;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.ui.RecycleList.CityOverviewListItem;
import org.secuso.privacyfriendlyweather.ui.RecycleList.RecyclerItemClickListener;
import org.secuso.privacyfriendlyweather.ui.RecycleList.RecyclerOverviewListAdapter;
import org.secuso.privacyfriendlyweather.ui.RecycleList.SimpleItemTouchHelperCallback;

import java.sql.SQLException;
import java.util.List;

/**
 * This class provides the functionality to update different UI components. This is useful when
 * database records were inserted or deleted.
 */
public class UiUpdater {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "ui_updated_debug_tag";

    /**
     * Member variables (makes sense to have them only once => static)
     */
    private RecyclerView recyclerView;
    private RecyclerOverviewListAdapter adapter;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;
    private DatabaseHelper dbHelper;

    /**
     * @param CONTEXT  The context in which the UI updater is to be used.
     * @param dbHelper A DatabaseHelper instance which is used to retrieve data from the database.
     */
    public UiUpdater(final Context CONTEXT, final DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        // Initialize the overview list and corresponding the visual component
        recyclerView = (RecyclerView) ((Activity) CONTEXT).findViewById(R.id.list_view_cities);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CONTEXT));

        adapter = new RecyclerOverviewListAdapter(dbHelper);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(CONTEXT, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Get the corresponding weather data and pass it on to the started activity
                        try {
                            int weatherDataId = RecyclerOverviewListAdapter.getListItems().get(position).getCurrentWeatherDataID();
                            CurrentWeatherData currentWeatherData = dbHelper.getCurrentWeatherDataByID(weatherDataId);
                            Intent intent = new Intent(CONTEXT, CityWeatherActivity.class);
                            intent.putExtra("weatherData", currentWeatherData);
                            CONTEXT.startActivity(intent);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // TODO: Handle error case
                        }

                    }
                })
        );

        callback = new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
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
     * @param weatherData The current weather data that will be used to generate a new item.
     */
    public void addItemToOverview(CurrentWeatherData weatherData) {
        String text = String.format("%s, %s°C", weatherData.getCity().getCityName(), Math.round(weatherData.getTemperatureCurrent()));
        int img = getImageResourceForWeatherCategory(weatherData.getWeatherID());
        RecyclerOverviewListAdapter.getListItems().add(new CityOverviewListItem(weatherData.getId(), text, img));
    }

    /**
     * Retrieves the latest data from the database and updates the list in the main activity.
     */
    public void updateCityList() {
        // Clear the list
        // See TODO in DataUpdater (when this TODO is implemented the entire list cannot just be cleared)
        RecyclerOverviewListAdapter.getListItems().clear();
        // Add the new items
        List<CurrentWeatherData> currentWeatherData = dbHelper.getCurrentWeatherData();
        for (CurrentWeatherData data : currentWeatherData) {
            String text = String.format("%s, %s°C", data.getCity().getCityName(), Math.round(data.getTemperatureCurrent()));
            int img = getImageResourceForWeatherCategory(data.getWeatherID());
            RecyclerOverviewListAdapter.getListItems().add(new CityOverviewListItem(data.getId(), text, img));
        }
    }

}
