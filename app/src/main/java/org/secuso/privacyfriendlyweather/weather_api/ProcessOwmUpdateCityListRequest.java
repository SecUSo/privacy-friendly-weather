package org.secuso.privacyfriendlyweather.weather_api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;

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

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    public ProcessOwmUpdateCityListRequest(Context context) {
        this.context = context;
    }

    /**
     * Converts the response to JSON and updates the database so that the latest weather data are
     * displayed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        try {
            JSONObject json = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            final String ERROR_MESSAGE = context.getResources().getString(R.string.convert_to_json_error);
            Toast.makeText(context, ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(VolleyError error) {

    }

}
