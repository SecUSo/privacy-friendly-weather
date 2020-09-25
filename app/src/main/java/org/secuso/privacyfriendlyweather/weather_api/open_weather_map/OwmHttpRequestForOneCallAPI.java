package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.util.Log;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForOneCallAPI;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for all stored cities.
 */
public class OwmHttpRequestForOneCallAPI extends OwmHttpRequest implements IHttpRequestForOneCallAPI {

    /**
     * Member variables.
     */
    private Context context;

    /**
     * @param context The context to use.
     */
    public OwmHttpRequestForOneCallAPI(Context context) {
        this.context = context;
    }



    @Override
    public void perform(float lat, float lon) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = getUrlForQueryingOneCallAPI(context, lat, lon);
 //       Log.d("OneCallURL",URL);
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmForecastOneCallAPIRequest(context));
    }
}
