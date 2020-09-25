package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.WeekForecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.updater.ViewUpdater;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class processes the HTTP requests that are made to the OpenWeatherMap API requesting the
 * current weather for all stored cities.
 */
public class ProcessOwmForecastOneCallAPIRequest implements IProcessHttpRequest {

    /**
     * Constants
     */
    private final String DEBUG_TAG = "process_forecast";

    /**
     * Member variables
     */
    private Context context;
    private PFASQLiteHelper dbHelper;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    public ProcessOwmForecastOneCallAPIRequest(Context context) {
        this.context = context;
        this.dbHelper = PFASQLiteHelper.getInstance(context);
    }

    /**
     * Converts the response to JSON and updates the database. Note that for this method no
     * UI-related operations are performed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response) {
        IDataExtractor extractor = new OwmDataExtractor();
        try {
            JSONObject json = new JSONObject(response);
            float lat = (float)json.getDouble("lat");
            float lon = (float)json.getDouble("lon");
 //           Log.d("URL JSON",Float.toString(lat));
 //           Log.d("URL JSON",Float.toString(lon));
            int cityId=0;
            //get CityID from lat/lon
            //Maybe a risk of rounding problems. Alternative: search closest citytowatch
            List<CityToWatch> citiesToWatch = dbHelper.getAllCitiesToWatch();
            for (int i = 0; i < citiesToWatch.size(); i++) {
                CityToWatch city = citiesToWatch.get(i);
                //if lat/lon of json response very close to lat/lon in citytowatch
                if ((Math.abs(city.getLatitude() - lat)<0.01) && (Math.abs(city.getLongitude() - lon)<0.01)) {
                    cityId=city.getCityId();
 //                   Log.d("URL CITYID", Integer.toString(cityId));
                    break;
                }
            }
            
            JSONArray list = json.getJSONArray("daily");
            //delete forecasts older than 24 hours
            dbHelper.deleteWeekForecastsByCityId(cityId);
            List<WeekForecast> forecasts = new ArrayList<>();
            // Continue with inserting new records
            for (int i = 0; i < list.length(); i++) {
                String currentItem = list.get(i).toString();
                WeekForecast forecast = extractor.extractWeekForecast(currentItem);
                // Data were not well-formed, abort
                if (forecast == null) {
                    final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                    Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                    return;
                }
                // Could retrieve all data, so proceed
                else {
                    forecast.setCity_id(cityId);
                    // add it to the database
                    dbHelper.addWeekForecast(forecast);
                    forecasts.add(forecast);
                }
            }

            ViewUpdater.updateWeekForecasts(forecasts);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Toast.makeText(context, context.getResources().getString(R.string.error_fetch_forecast), Toast.LENGTH_LONG).show();
            }
        });
    }

}
