package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.widget.RemoteViews;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecast;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForForecastWidget;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for all stored cities.
 */
public class OwmHttpRequestForWidgetUpdate extends OwmHttpRequest implements IHttpRequestForForecastWidget {

    /**
     * Member variables.
     */
    private Context context;

    /**
     * @param context The context to use.
     */
    public OwmHttpRequestForWidgetUpdate(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequestForForecast#perform(int)
     */
    @Override
    public void perform(int cityId, int widgetId, int widgetType, RemoteViews views) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = getUrlForQueryingForecast(cityId);
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmForecastRequestWidget(context, cityId, widgetId, widgetType, views));
    }
}
