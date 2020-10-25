package org.secuso.privacyfriendlyweather.database.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class represents the database model for current weather data of cities.
 */
@Entity(tableName = "CURRENT_WEATHER", foreignKeys = {
        @ForeignKey(entity = City.class,
                parentColumns = {"cities_id"},
                childColumns = {"city_id"},
                onDelete = ForeignKey.CASCADE)})
public class CurrentWeatherData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "current_weather_id")
    private int id;
    @ColumnInfo(name = "city_id")
    private int city_id;
    @ColumnInfo(name = "time_of_measurement")
    private long timestamp;
    @ColumnInfo(name = "weather_id")
    private int weatherID;
    @ColumnInfo(name = "temperature_current")
    private float temperatureCurrent;
    @ColumnInfo(name = "temperature_min")
    private float temperatureMin;//TODO: Remove, not available in one call api
    @ColumnInfo(name = "temperature_max")
    private float temperatureMax;//TODO: Remove, not available in one call api
    @ColumnInfo(name = "humidity")
    private float humidity;
    @ColumnInfo(name = "pressure")
    private float pressure;
    @ColumnInfo(name = "wind_speed")
    private float windSpeed;
    @ColumnInfo(name = "wind_direction")
    private float windDirection;
    @ColumnInfo(name = "cloudiness")
    private float cloudiness;
    @ColumnInfo(name = "time_sunrise")
    private long timeSunrise;
    @ColumnInfo(name = "time_sunset")
    private long timeSunset;
    @ColumnInfo(name = "timezone_seconds")
    private int timeZoneSeconds;
    @ColumnInfo(name = "rain60min")
    private String rain60min;

    @Ignore
    private String city_name;

    public CurrentWeatherData() {
        this.city_id = Integer.MIN_VALUE;
    }

    @Ignore
    public CurrentWeatherData(int id, int city_id, long timestamp, int weatherID, float temperatureCurrent, float temperatureMin, float temperatureMax, float humidity, float pressure, float windSpeed, float windDirection, float cloudiness, long timeSunrise, long timeSunset, int timeZoneSeconds) {
        this.id = id;
        this.city_id = city_id;
        this.timestamp = timestamp;
        this.weatherID = weatherID;
        this.temperatureCurrent = temperatureCurrent;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.cloudiness = cloudiness;
        this.timeSunrise = timeSunrise;
        this.timeSunset = timeSunset;
        this.timeZoneSeconds = timeZoneSeconds;
        this.rain60min = rain60min;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(int weatherID) {
        this.weatherID = weatherID;
    }

    public float getTemperatureCurrent() {
        return temperatureCurrent;
    }

    public void setTemperatureCurrent(float temperatureCurrent) {
        this.temperatureCurrent = temperatureCurrent;
    }

    public float getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public float getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }

    public float getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(float cloudiness) {
        this.cloudiness = cloudiness;
    }

    public long getTimeSunrise() {
        return timeSunrise;
    }

    public void setTimeSunrise(long timeSunrise) {
        this.timeSunrise = timeSunrise;
    }

    public long getTimeSunset() {
        return timeSunset;
    }

    public void setTimeSunset(long timeSunset) {
        this.timeSunset = timeSunset;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getTimeZoneSeconds() {
        return timeZoneSeconds;
    }

    public void setTimeZoneSeconds(int timeZoneSeconds) {
        this.timeZoneSeconds = timeZoneSeconds;
    }

    public String getRain60min() {
        return rain60min;
    }

    public void setRain60min(String rain60min) {
        this.rain60min = rain60min;
    }
}
