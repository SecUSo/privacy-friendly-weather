package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.radius_search.RadiusSearchItem;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.ValueDeriver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RadiusSearchResultActivity extends AppCompatActivity {

    /**
     * Visual components
     */
    private ListView listViewResult;

    /**
     * Member variables
     */
    List<String> itemsToDisplay;
    ArrayAdapter<String> itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius_search_result);

        // Retrieve the data to display
        Bundle bundle = getIntent().getExtras();
        ArrayList<RadiusSearchItem> resultList = bundle.getParcelableArrayList("resultList");
        itemsToDisplay = getItemsToDisplay(resultList);

        initialize();
    }

    /**
     * Initialized the components of this activity.
     */
    private void initialize() {
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsToDisplay);
        listViewResult = (ListView) findViewById(R.id.activity_radius_search_result_list_view);
        listViewResult.setAdapter(itemsAdapter);
    }

    private List<String> getItemsToDisplay(List<RadiusSearchItem> resultList) {
        List<String> itemsToDisplay = new ArrayList<>();
        IApiToDatabaseConversion.WeatherCategories category;
        ValueDeriver deriver = new ValueDeriver(getApplicationContext());

        DecimalFormat decimalFormatter = new DecimalFormat(".#");
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(this));

        for (int i = 0; i < resultList.size(); i++) {
            category = IApiToDatabaseConversion.getLabelForValue(resultList.get(i).getWeatherCategory());
            itemsToDisplay.add(String.format(
                    "%s. %s, %s %s %s",
                    i + 1,
                    resultList.get(i).getCityName(),
                    deriver.getWeatherDescriptionByCategory(category),
                    decimalFormatter.format(prefManager.convertTemperatureFromCelsius((float) resultList.get(i).getTemperature())),
                    prefManager.getWeatherUnit()
            ));
        }
        return itemsToDisplay;
    }

}
