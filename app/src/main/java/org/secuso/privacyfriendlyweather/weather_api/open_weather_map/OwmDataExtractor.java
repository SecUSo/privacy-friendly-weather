package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.WeekForecast;
import org.secuso.privacyfriendlyweather.radius_search.RadiusSearchItem;
import org.secuso.privacyfriendlyweather.weather_api.IApiToDatabaseConversion;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;

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

            if(jsonData.has("cod")) {     //update for single city, field cod only available there
                if (jsonData.has("sys")) {
                    JSONObject jsonSunRiseSet = jsonData.getJSONObject("sys");
                    weatherData.setTimeSunrise(jsonSunRiseSet.getLong("sunrise"));
                    weatherData.setTimeSunset(jsonSunRiseSet.getLong("sunset"));
                }
                    weatherData.setTimeZoneSeconds(jsonData.getInt("timezone"));  //timezone is not part of sys for single city
              } else {                          //update for group of cities
                if (jsonData.has("sys")) {
                   JSONObject jsonSunRiseSet = jsonData.getJSONObject("sys");
                   weatherData.setTimeSunrise(jsonSunRiseSet.getLong("sunrise"));
                   weatherData.setTimeSunset(jsonSunRiseSet.getLong("sunset"));
                   weatherData.setTimeZoneSeconds(jsonSunRiseSet.getInt("timezone"));  //timezone part of sys
               }
            }

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
    public CurrentWeatherData extractCurrentWeatherDataOneCall(String data) {
        try {
            JSONObject jsonData = new JSONObject(data);
            CurrentWeatherData weatherData = new CurrentWeatherData();

            /*
            private int id;
            -private int city_id;
            -private long timestamp;
            -private int weatherID;
            -private float temperatureCurrent;
            -private float temperatureMin;  //not available
            -private float temperatureMax;  //not available
            -private float humidity;
            -private float pressure;
            -private float windSpeed;
            -private float windDirection;
            -private float cloudiness;
            -private long timeSunrise;
            -private long timeSunset;
             */
            weatherData.setTimestamp(jsonData.getLong("dt"));
            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            weatherData.setWeatherID(conversion.convertWeatherCategory(jsonWeather.getString("id")));
            weatherData.setTemperatureCurrent((float) jsonData.getDouble("temp"));
            weatherData.setTemperatureMin((float) jsonData.getDouble("temp")); //data not available
            weatherData.setTemperatureMax((float) jsonData.getDouble("temp")); //data not available
            weatherData.setHumidity((float) jsonData.getDouble("humidity"));
            weatherData.setPressure((float) jsonData.getDouble("pressure"));
            weatherData.setWindSpeed((float) jsonData.getDouble("wind_speed"));
            weatherData.setWindDirection((float) jsonData.getDouble("wind_deg"));
            weatherData.setCloudiness((float) jsonData.getDouble("clouds"));
            weatherData.setTimeSunrise(jsonData.getLong("sunrise"));
            weatherData.setTimeSunset(jsonData.getLong("sunset"));
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


            forecast.setForecastTime(jsonData.getLong("dt") * 1000L);

            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            forecast.setWeatherID(conversion.convertWeatherCategory(jsonWeather.getString("id")));

            JSONObject jsonMain = jsonData.getJSONObject("main");
            forecast.setTemperature((float) jsonMain.getDouble("temp"));
            forecast.setHumidity((float) jsonMain.getDouble("humidity"));
            forecast.setPressure((float) jsonMain.getDouble("pressure"));

            //TODO: Save wind for the 3h forecast?
            JSONObject jsonWind = jsonData.getJSONObject("wind");
            forecast.setWindSpeed((float) jsonWind.getDouble("speed"));
            forecast.setWindDirection((float) jsonWind.getDouble("deg"));

            //TODO: leave this out?
            // In case there was no rain in the past 3 hours, there is no "rain" field
            if (jsonData.isNull("rain")) {
                forecast.setRainVolume(Forecast.NO_RAIN_VALUE);
            } else {
                JSONObject jsonRain = jsonData.getJSONObject("rain");
                if (jsonRain.isNull("3h")) {
                    forecast.setRainVolume(Forecast.NO_RAIN_VALUE);
                } else {
                    forecast.setRainVolume((float) jsonRain.getDouble("3h"));
                }
            }
            //add snow precipitation to rain
            if (!jsonData.isNull("snow")) {
                JSONObject jsonSnow = jsonData.getJSONObject("snow");
                if (!jsonSnow.isNull("3h")) {
                    forecast.setRainVolume(forecast.getRainValue() + (float) jsonSnow.getDouble("3h"));
                }
            }


            return forecast;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @see IDataExtractor#extractWeekForecast(String)
     */
    @Override
    public WeekForecast extractWeekForecast(String data) {
        try {

            WeekForecast forecast = new WeekForecast();
            JSONObject jsonData = new JSONObject(data);

            forecast.setTimestamp(System.currentTimeMillis() / 1000);
            forecast.setForecastTime(jsonData.getLong("dt") * 1000L);

            IApiToDatabaseConversion conversion = new OwmToDatabaseConversion();
            JSONArray jsonWeatherArray = jsonData.getJSONArray("weather");
            JSONObject jsonWeather = new JSONObject(jsonWeatherArray.get(0).toString());
            forecast.setWeatherID(conversion.convertWeatherCategory(jsonWeather.getString("id")));

            JSONObject jsonTemp = jsonData.getJSONObject("temp");
            if (jsonTemp.has("day")) forecast.setTemperature((float) jsonTemp.getDouble("day"));
            if (jsonTemp.has("max")) forecast.setMaxTemperature((float) jsonTemp.getDouble("max"));
            if (jsonTemp.has("min")) forecast.setMinTemperature((float) jsonTemp.getDouble("min"));
            if (jsonData.has("humidity")) forecast.setHumidity((float) jsonData.getDouble("humidity"));
            if (jsonData.has("pressure")) forecast.setPressure((float) jsonData.getDouble("pressure"));
            if (jsonData.has("wind_speed")) forecast.setWind_speed((float) jsonData.getDouble("wind_speed"));
            if (jsonData.has("wind_deg")) forecast.setWind_direction((float) jsonData.getDouble("wind_deg"));
            if (jsonData.has("uvi")) forecast.setUv_index((float) jsonData.getDouble("uvi"));

            if (jsonData.isNull("rain")) {
                forecast.setPrecipitation(Forecast.NO_RAIN_VALUE);
            } else {
                    forecast.setPrecipitation((float) jsonData.getDouble("rain"));
                }
                        //add snow precipitation to rain
            if (!jsonData.isNull("snow")) {
                    forecast.setPrecipitation(forecast.getPrecipitation() + (float) jsonData.getDouble("snow"));
                }

            return forecast;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @see IDataExtractor#extractRain60min(String, String, String, String, String)
     */
    @Override
    public String extractRain60min(String data0,String data1, String data2, String data3, String data4) {
        try {

            String rain = "";
            JSONObject jsonData0 = new JSONObject(data0);
            JSONObject jsonData1 = new JSONObject(data1);
            JSONObject jsonData2 = new JSONObject(data2);
            JSONObject jsonData3 = new JSONObject(data3);
            JSONObject jsonData4 = new JSONObject(data4);
            double rain5min=jsonData0.getDouble("precipitation")+jsonData1.getDouble("precipitation")+jsonData2.getDouble("precipitation")+jsonData3.getDouble("precipitation")+jsonData4.getDouble("precipitation");
            if (rain5min==0){
                rain ="\u25a1";
            } else if (rain5min<12.5){  //light rain equals <2.5mm/h (12.5 = 5 x 2.5)
                rain ="\u25a6";
            } else{
                rain ="\u25a0";
            }

            return rain;
        } catch (JSONException e) {
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
