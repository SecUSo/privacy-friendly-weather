package org.secuso.privacyfriendlyweather.weather_api;

/**
 * This interface defines the template for making HTTP request.
 */
public interface IHttpRequest {

    enum HttpType {
        POST,
        GET,
        PUT,
        DELETE
    }

    ;

    /**
     * @param URL    The target of the HTTP request.
     * @param method Which method to use for the HTTP request.
     * @return Returns the response of the HTTP request.
     */
    String make(final String URL, HttpType method);

}
