package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * This class is the database model for the forecasts table.
 */
@DatabaseTable(tableName = "forecasts")
public class Forecast {

    /**
     * Column names
     */
    public static final String CITY_ID = "city_id";
    public static final String COLUMN_TIME_MEASUREMENT = "time_of_measurement";
    public static final String COLUMN_FORECAST_FOR = "forecast_for";
    public static final String COLUMN_WEATHER_ID = "weather_id";
    public static final String COLUMN_TEMPERATURE_CURRENT = "temperature_current";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_WIND_DIRECTION = "wind_direction";
    public static final String COLUMN_PAST_RAIN_VOLUME = "past_rain_volume";

    /**
     * Other constants
     */
    /*
    This value is used to indicate that there is no value for the column COLUMN_PAST_RAIN_VOLUME
     */
    public static final float NO_RAIN_VALUE = -1;

    /**
     * Database fields / member variables
     */
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnDefinition = "integer references cities(id) on delete cascade")
    private City city;

    @DatabaseField(columnName = COLUMN_TIME_MEASUREMENT)
    private long timestamp;

    @DatabaseField(columnName = COLUMN_FORECAST_FOR, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date forecastFor;

    @DatabaseField(columnName = COLUMN_WEATHER_ID)
    private int weatherID;

    @DatabaseField(columnName = COLUMN_TEMPERATURE_CURRENT)
    private float temperature;
    @DatabaseField(columnName = COLUMN_HUMIDITY)
    private float humidity;
    @DatabaseField(columnName = COLUMN_PRESSURE)
    private float pressure;

    @DatabaseField(columnName = COLUMN_WIND_SPEED)
    private float windSpeed;
    @DatabaseField(columnName = COLUMN_WIND_DIRECTION)
    private float windDirection;

    @DatabaseField(columnName = COLUMN_PAST_RAIN_VOLUME)
    private float pastRainVolumeInMM;

    /**
     * @return Returns the ID of the record (which uniquely identifies the record).
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the date and time for the forecast.
     */
    public Date getForecastTime() {
        return forecastFor;
    }

    /**
     * @param forecastFor The point of time for the forecast.
     */
    public void setForecastTime(Date forecastFor) {
        this.forecastFor = forecastFor;
    }

    /**
     * @return Returns the point of time when the data was inserted into the database in Unix, UTC.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The point of time to set when the data was inserted into the database in
     *                  Unix, UTC.
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
     * @return Returns the current temperature in Celsius.
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * @param temperature The current temperature to set in Celsius.
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
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
     * @return Returns the raining volume of the past 3 hours in millimeters.
     */
    public float getPastRainVolume() {
        return pastRainVolumeInMM;
    }

    /**
     * @param pastRainVolume The raining volume of the past 3 hours in millimeters.
     */
    public void setPastRainVolume(float pastRainVolume) {
        pastRainVolumeInMM = pastRainVolume;
    }

}
