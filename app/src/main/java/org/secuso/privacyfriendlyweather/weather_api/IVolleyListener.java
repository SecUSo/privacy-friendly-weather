package org.secuso.privacyfriendlyweather.weather_api;

/**
 * This interface is for separating the HTTP request and the handling of the response. This is
 * necessary because the HTTP request are not done inline.
 *
 * @param <T> The type of the response.
 */
public interface IVolleyListener<T> {

    /**
     * The implementation of this method is to be used to handle the response.
     *
     * @param object The return value of the request.
     */
    public void getResult(T object);

}
