package org.secuso.privacyfriendlyweather.weather_api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class implements the IHttpRequest interface. It provides HTTP requests by using Volley.
 * See: https://developer.android.com/training/volley/simple.html
 */
public class VolleyHttpRequest implements IHttpRequest {

    private Context context;
    private IVolleyListener<String> listener;

    /**
     * Constructor.
     *
     * @param context Volley needs a context "for creating the cache dir".
     * @see Volley#newRequestQueue(Context)
     */
    public VolleyHttpRequest(Context context, IVolleyListener<String> listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * @see IHttpRequest#make(String, HttpType)
     */
    @Override
    public String make(String URL, HttpType method) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Define the request method
        int requestMethod;
        switch (method) {
            case POST:
                requestMethod = Request.Method.POST;
                break;
            case GET:
                requestMethod = Request.Method.GET;
                break;
            case PUT:
                requestMethod = Request.Method.PUT;
                break;
            case DELETE:
                requestMethod = Request.Method.DELETE;
                break;
            default:
                requestMethod = Request.Method.GET;
        }

        // Execute the request
        StringRequest stringRequest = new StringRequest(requestMethod, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString() != null)
                            listener.getResult(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.getResult("");
                    }
                });

        queue.add(stringRequest);
        return null;
    }

}
