package org.secuso.privacyfriendlyweather.weather_api;

import android.widget.RemoteViews;

/**
 * This generic interface is for making an HTTP request to some weather API, process the data and
 * finally trigger some mechanism to update the UI.
 */
public interface IHttpRequestForForecastWidget {

    /**
     * @param cityId The (OWM) ID of the city to get the data for.
     */
    void perform(int cityId, int widgetId, int widgetType, RemoteViews views);

}
