package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.Forecast;
import org.secuso.privacyfriendlyweather.radius_search.RadiusSearchItem;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a concrete implementation for extracting weather data that was retrieved by
 * OpenWeatherMap.
 */
public class OwmDataExtractor implements IDataExtractor {

    /**
     * @see IDataExtractor#wasCityFound(String)
     */
    @Override
    public boolean wasCityFound(String data) {
        try {
            JSONObject json = new JSONObject(data);
            return json.has("cod") && (json.getInt("cod") == 200);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param data The data that contains the information to instantiate a CurrentWeatherData
     *             object.
     *             If data for a single city were requested, the response string can be
     *             passed as an argument.
     *             If data for multiple cities were requested, make sure to pass only one item
     *             of the response list at a time!
     * @return Returns an instance of CurrentWeatherData of the information could be extracted
     * successfully or null in case there was some error while parsing the response (which is not
     * too good because that means that the response of OpenWeatherMap was not well-formed).
     */
    @Override
    public CurrentWeatherData extractCurrentWeatherData(String data) {
        try {
            JSONObject jsonData = new JSONObject(data);
            CurrentWeatherData weatherData = new CurrentWeatherData();

            weatherData.setTimestamp(jsonData.getLong("dt"));

            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            weatherData.setWeatherID(conversion.convertWeatherCategory(jsonWeather.getString("id")));

            JSONObject jsonMain = jsonData.getJSONObject("main");
            weatherData.setTemperatureCurrent((float) jsonMain.getDouble("temp"));
            weatherData.setTemperatureMin((float) jsonMain.getDouble("temp_min"));
            weatherData.setTemperatureMax((float) jsonMain.getDouble("temp_max"));
            weatherData.setHumidity((float) jsonMain.getDouble("humidity"));
            weatherData.setPressure((float) jsonMain.getDouble("pressure"));

            JSONObject jsonWind = jsonData.getJSONObject("wind");
            weatherData.setWindSpeed((float) jsonWind.getDouble("speed"));
            weatherData.setWindDirection((float) jsonWind.getDouble("deg"));

            JSONObject jsonClouds = jsonData.getJSONObject("clouds");
            weatherData.setCloudiness((float) jsonClouds.getDouble("all"));

            if (jsonData.has("sys")) {
                JSONObject jsonSunRiseSet = jsonData.getJSONObject("sys");
                weatherData.setTimeSunrise(jsonSunRiseSet.getLong("sunrise"));
                weatherData.setTimeSunset(jsonSunRiseSet.getLong("sunset"));
            }

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @see IDataExtractor#extractRadiusSearchItemData(String)
     */
    @Override
    public RadiusSearchItem extractRadiusSearchItemData(String data) {
        try {
            JSONObject jsonData = new JSONObject(data);
            JSONObject jsonMain = jsonData.getJSONObject("main");
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();

            return new RadiusSearchItem(
                    jsonData.getString("name"),
                    (float) jsonMain.getDouble("temp"),
                    conversion.convertWeatherCategory(jsonWeather.getString("id"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @see IDataExtractor#extractForecast(String)
     */
    @Override
    public Forecast extractForecast(String data) {
        try {
            Forecast forecast = new Forecast();
            JSONObject jsonData = new JSONObject(data);

            forecast.setTimestamp(System.currentTimeMillis() / 1000);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date forecastTime = formatter.parse(jsonData.getString("dt_txt"));
            forecast.setForecastTime(forecastTime);

            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            forecast.setWeatherID(conversion.convertWeatherCategory(jsonWeather.getString("id")));

            JSONObject jsonMain = jsonData.getJSONObject("main");
            forecast.setTemperature((float) jsonMain.getDouble("temp"));
            forecast.setHumidity((float) jsonMain.getDouble("humidity"));
            forecast.setPressure((float) jsonMain.getDouble("pressure"));

            JSONObject jsonWind = jsonData.getJSONObject("wind");
            forecast.setWindSpeed((float) jsonWind.getDouble("speed"));
            forecast.setWindDirection((float) jsonWind.getDouble("deg"));

            // In case there was no rain in the past 3 hours, there is no "rain" field
            if (jsonData.isNull("rain")) {
                forecast.setPastRainVolume(Forecast.NO_RAIN_VALUE);
            } else {
                JSONObject jsonRain = jsonData.getJSONObject("rain");
                if (jsonRain.isNull("3h")) {
                    forecast.setPastRainVolume(Forecast.NO_RAIN_VALUE);
                } else {
                    forecast.setPastRainVolume((float) jsonRain.getDouble("3h"));
                }
            }

            return forecast;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param data The data that contains the information to retrieve the ID of the city.
     *             If data for a single city were requested, the response string can be
     *             passed as an argument.
     *             If data for multiple cities were requested, make sure to pass only one item
     *             of the response list at a time!
     * @return Returns the ID of the city or Integer#MIN_VALUE in case the data is not well-formed
     * and the information could not be extracted.
     */
    @Override
    public int extractCityID(String data) {
        try {
            JSONObject json = new JSONObject(data);
            return json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Integer.MIN_VALUE;
    }

    /**
     * @see IDataExtractor#extractLatitudeLongitude(String)
     */
    @Override
    public double[] extractLatitudeLongitude(String data) {

        try {
            JSONObject json = new JSONObject(data);
            JSONObject coordinationObject = json.getJSONObject("coord");
            return new double[]{
                    coordinationObject.getDouble("lat"),
                    coordinationObject.getDouble("lon")
            };
        } catch (JSONException e) {
            e.printStackTrace();
            return new double[0];
        }
    }


}
