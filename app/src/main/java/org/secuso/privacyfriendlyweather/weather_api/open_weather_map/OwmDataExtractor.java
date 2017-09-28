package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
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

            /*
            private int id;
            -private int city_id;
            -private long timestamp;
            -private int weatherID;
            -private float temperatureCurrent;
            -private float temperatureMin;
            -private float temperatureMax;
            -private float humidity;
            -private float pressure;
            -private float windSpeed;
            -private float windDirection;
            -private float cloudiness;
            -private long timeSunrise;
            -private long timeSunset;
             */

            /*
            coord
                coord.lon City geo location, longitude
                coord.lat City geo location, latitude
            weather (more info Weather condition codes)
                weather.id Weather condition id
                weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
                weather.description Weather condition within the group
                weather.icon Weather icon id
            base Internal parameter
            main
                main.temp Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                main.pressure Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
                main.humidity Humidity, %
                main.temp_min Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                main.temp_max Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                main.sea_level Atmospheric pressure on the sea level, hPa
                main.grnd_level Atmospheric pressure on the ground level, hPa
            wind
                wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
                wind.deg Wind direction, degrees (meteorological)
            clouds
                clouds.all Cloudiness, %
            rain
                rain.3h Rain volume for the last 3 hours
            snow
                snow.3h Snow volume for the last 3 hours
            dt Time of data calculation, unix, UTC
            sys
                sys.type Internal parameter
                sys.id Internal parameter
                sys.message Internal parameter
                sys.country Country code (GB, JP etc.)
                sys.sunrise Sunrise time, unix, UTC
                sys.sunset Sunset time, unix, UTC
            id City ID
            name City name
            cod Internal parameter
             */

            weatherData.setTimestamp(jsonData.getLong("dt"));

            weatherData.setCity_id(jsonData.getInt("id"));

            // Maybe save coordinates as well?
            //JSONObject jsonCoords = jsonData.getJSONObject("coord");
            //weatherData.setLongitude((float) jsonMain.getDouble("lon"));
            //weatherData.setLatitude((float) jsonMain.getDouble("lat"));
            // Maybe the city name?
            //weatherData.setCityName(jsonMain.getString("name"));

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
            if (jsonWind.has("deg")) {
                weatherData.setWindDirection((float) jsonWind.getDouble("deg"));
            }

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
            /*
            code Internal parameter
            message Internal parameter
            city
                city.id City ID
                city.name City name
                city.coord
                    city.coord.lat City geo location, latitude
                    city.coord.lon City geo location, longitude
                city.country Country code (GB, JP etc.)
            cnt Number of lines returned by this API call
            list
                list.dt Time of data forecasted, unix, UTC
                list.main
                    list.main.temp Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                    list.main.temp_min Minimum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                    list.main.temp_max Maximum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                    list.main.pressure Atmospheric pressure on the sea level by default, hPa
                    list.main.sea_level Atmospheric pressure on the sea level, hPa
                    list.main.grnd_level Atmospheric pressure on the ground level, hPa
                    list.main.humidity Humidity, %
                    list.main.temp_kf Internal parameter
                list.weather (more info Weather condition codes)
                    list.weather.id Weather condition id
                    list.weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
                    list.weather.description Weather condition within the group
                    list.weather.icon Weather icon id
                list.clouds
                    list.clouds.all Cloudiness, %
                list.wind
                    list.wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
                    list.wind.deg Wind direction, degrees (meteorological)
                list.rain
                    list.rain.3h Rain volume for last 3 hours, mm
                list.snow
                    list.snow.3h Snow volume for last 3 hours
                list.dt_txt Data/time of caluclation, UTC
             */
            Forecast forecast = new Forecast();
            JSONObject jsonData = new JSONObject(data);

            forecast.setTimestamp(System.currentTimeMillis() / 1000);
            //forecast.setTimestamp(jsonData.getLong("dt"));

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

            //TODO: Save wind for the 3h forecast?
            //JSONObject jsonWind = jsonData.getJSONObject("wind");
            //forecast.setWindSpeed((float) jsonWind.getDouble("speed"));
            //forecast.setWindDirection((float) jsonWind.getDouble("deg"));

            //TODO: leave this out?
            // In case there was no rain in the past 3 hours, there is no "rain" field
            /*if (jsonData.isNull("rain")) {
                forecast.setPastRainVolume(Forecast.NO_RAIN_VALUE);
            } else {
                JSONObject jsonRain = jsonData.getJSONObject("rain");
                if (jsonRain.isNull("3h")) {
                    forecast.setPastRainVolume(Forecast.NO_RAIN_VALUE);
                } else {
                    forecast.setPastRainVolume((float) jsonRain.getDouble("3h"));
                }
            }*/

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
