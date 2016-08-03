package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents the database model for current weather data of cities.
 */
@DatabaseTable(tableName = "current_weather_data")
public class CurrentWeatherData {

    /**
     * Constants
     */
    public static final String COLUMN_TIME_MEASUREMENT = "time_of_measurement";
    public static final String COLUMN_WEATHER_ID = "weather_id";
    public static final String COLUMN_WEATHER_CATEGORY = "weather_category";
    public static final String COLUMN_WEATHER_DESCRIPTION = "weather_description";
    public static final String COLUMN_TEMPERATURE_CURRENT = "temperature_current";
    public static final String COLUMN_TEMPERATURE_MIN = "temperature_min";
    public static final String COLUMN_TEMPERATURE_MAX = "temperature_max";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_WIND_DIRECTION = "wind_direction";
    public static final String COLUMN_CLOUDINESS = "cloudiness";
    public static final String COLUMN_TIME_SUNRISE = "time_sunrise";
    public static final String COLUMN_TIME_SUNSET = "time_sunset";

    /**
     * Database fields / member variables
     */
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnDefinition = "integer references cities(id) on delete cascade")
    private City city;

    @DatabaseField(columnName = COLUMN_TIME_MEASUREMENT)
    private long timestamp;

    @DatabaseField(columnName = COLUMN_WEATHER_ID)
    private int weatherID;
    @DatabaseField(columnName = COLUMN_WEATHER_CATEGORY)
    private String weatherCategory;
    @DatabaseField(columnName = COLUMN_WEATHER_DESCRIPTION)
    private String weatherDescription;

    @DatabaseField(columnName = COLUMN_TEMPERATURE_CURRENT)
    private float temperatureCurrent;
    @DatabaseField(columnName = COLUMN_TEMPERATURE_MIN)
    private float temperatureMin;
    @DatabaseField(columnName = COLUMN_TEMPERATURE_MAX)
    private float temperatureMax;
    @DatabaseField(columnName = COLUMN_HUMIDITY)
    private float humidity;
    @DatabaseField(columnName = COLUMN_PRESSURE)
    private float pressure;

    @DatabaseField(columnName = COLUMN_WIND_SPEED)
    private float windSpeed;
    @DatabaseField(columnName = COLUMN_WIND_DIRECTION)
    private float windDirection;

    @DatabaseField(columnName = COLUMN_CLOUDINESS)
    private float cloudiness;

    @DatabaseField(columnName = COLUMN_TIME_SUNRISE)
    private long timeSunrise;
    @DatabaseField(columnName = COLUMN_TIME_SUNSET)
    private long timeSunset;

    /**
     * Getters and setters
     */

    /**
     * @return Returns the ID of the record (Fwhich uniquely identifies the record).
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the point of time when the data was measures in Unix, UTC.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The point of time when the data was measured in Unix, UTC.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Returns the city that the weather data belong to.
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city The city that the weather data belong to.
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return Returns the weather condition ID.
     */
    public int getWeatherID() {
        return weatherID;
    }

    /**
     * @param weatherID The weather condition ID to set.
     */
    public void setWeatherID(int weatherID) {
        this.weatherID = weatherID;
    }

    /**
     * @return Returns a short weather condition description like "Clear", "Rainy" etc.
     */
    public String getWeatherCategory() {
        return weatherCategory;
    }

    /**
     * @param weatherCategory A short description of the weather condition like "Clear", "Rainy" etc.
     */
    public void setWeatherCategory(String weatherCategory) {
        this.weatherCategory = weatherCategory;
    }

    /**
     * @return Returns a description of the current weather.
     */
    public String getWeatherDescription() {
        return weatherDescription;
    }

    /**
     * @param weatherDescription A description of the current weather.
     */
    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    /**
     * @return Returns the current temperature in Celsius.
     */
    public float getTemperatureCurrent() {
        return temperatureCurrent;
    }

    /**
     * @param temperatureCurrent The current temperature to set in Celsius.
     */
    public void setTemperatureCurrent(float temperatureCurrent) {
        this.temperatureCurrent = temperatureCurrent;
    }

    /**
     * @return Returns the minimum temperature in Celsius at the moment. "This is deviation from
     * current temp that is possible for large cities and megalopolises geographically expanded".
     * (see http://openweathermap.org/current)
     */
    public float getTemperatureMin() {
        return temperatureMin;
    }

    /**
     * @param temperatureMin The minimum temperature in Celsius at the moment.
     */
    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    /**
     * @return Returns the maximum temperature in Celsius at the moment. "This is deviation from
     * current temp that is possible for large cities and megalopolises geographically expanded".
     * (see http://openweathermap.org/current)
     */
    public float getTemperatureMax() {
        return temperatureMax;
    }

    /**
     * @param temperatureMax The maximum temperature in Celsius at the moment.
     */
    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    /**
     * @return Returns the humidity value in percent.
     */
    public float getHumidity() {
        return humidity;
    }

    /**
     * @param humidity The humidity value in percent to set.
     */
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    /**
     * @return Returns the air pressure value in hectopascal (hPa).
     */
    public float getPressure() {
        return pressure;
    }

    /**
     * @param pressure The air pressure value in hectopascal (hPa) to set.
     */
    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    /**
     * @return Returns the wind speed in meters per second.
     */
    public float getWindSpeed() {
        return windSpeed;
    }

    /**
     * @param windSpeed The wind speed in meters per second to set.
     */
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * @return Returns the wind direction in degrees (meteorological).
     */
    public float getWindDirection() {
        return windDirection;
    }

    /**
     * @param windDirection The wind direction to set (degrees [meteorological]).
     */
    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * @return Returns the cloudiness value.
     */
    public float getCloudiness() {
        return cloudiness;
    }

    /**
     * @param cloudiness The cloudiness value to set.
     */
    public void setCloudiness(float cloudiness) {
        this.cloudiness = cloudiness;
    }

    /**
     * @return Returns the time of the sunrise (Unix, UTC)
     */
    public long getTimeSunrise() {
        return timeSunrise;
    }

    /**
     * @param timeSunrise The time of the sunrise to set (Unix, UTC).
     */
    public void setTimeSunrise(long timeSunrise) {
        this.timeSunrise = timeSunrise;
    }

    /**
     * @return Returns the time of the sunset (Unix, UTC).
     */
    public long getTimeSunset() {
        return timeSunset;
    }

    /**
     * @param timeSunset The time of the sunset to set (Unix, UTC).
     */
    public void setTimeSunset(long timeSunset) {
        this.timeSunset = timeSunset;
    }

}
