package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmUpdateCityListRequest implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_update_list";

    /**
     * Member variables
     */
    private Context context;
    //private DatabaseHelper dbHelper;
    private PFASQLiteHelper dbHelper;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    public ProcessOwmUpdateCityListRequest(Context context) {
        this.context = context;
        this.dbHelper = PFASQLiteHelper.getInstance(context);
    }

    /**
     * Converts the response to JSON and updates the database so that the latest weather data are
     * displayed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        IDataExtractor extractor = new OwmDataExtractor();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray list = json.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                String currentItem = list.get(i).toString();
                CurrentWeatherData weatherData = extractor.extractCurrentWeatherData(currentItem);
                int cityId = extractor.extractCityID(currentItem);
                // Data were not well-formed, abort
                if (weatherData == null || cityId == Integer.MIN_VALUE) {
                    final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                    Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                    return;
                }
                // Could retrieve all data, so proceed
                else {
                    weatherData.setCity_id(cityId);

                    CurrentWeatherData current = dbHelper.getCurrentWeatherByCityId(cityId);
                    if(current != null && current.getCity_id() == cityId) {
                        dbHelper.updateCurrentWeather(weatherData);
                    } else {
                        dbHelper.addCurrentWeather(weatherData);
                    }

                    //update UI
                    ViewUpdater.updateCurrentWeatherData(weatherData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO: Error Handling
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(final VolleyError error) {
        Handler h = new Handler(this.context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getResources().getString(R.string.error_fetch_cityList), Toast.LENGTH_LONG).show();
            }
        });
    }

}
