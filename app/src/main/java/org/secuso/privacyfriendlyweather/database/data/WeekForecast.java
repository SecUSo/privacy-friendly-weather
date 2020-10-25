package org.secuso.privacyfriendlyweather.database.data;

import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.secuso.privacyfriendlyweather.database.AppDatabase;

/**
 * This class is the database model for the Weekforecasts table.
 */
@Entity(tableName = "WEEKFORECASTS", foreignKeys = {
        @ForeignKey(entity = City.class,
                parentColumns = {"cities_id"},
                childColumns = {"city_id"},
                onDelete = ForeignKey.CASCADE)})
public class WeekForecast {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "forecast_id")
    private int id;
    @ColumnInfo(name = "city_id")
    private int city_id;
    @ColumnInfo(name = "time_of_measurement")
    private long timestamp;
    @ColumnInfo(name = "forecastTime")
    private long forecastTime;
    @ColumnInfo(name = "weather_id")
    private int weatherID;
    @ColumnInfo(name = "temperature_current")
    private float temperature;
    @ColumnInfo(name = "temperature_min")
    private float temperature_min;
    @ColumnInfo(name = "temperature_max")
    private float temperature_max;
    @ColumnInfo(name = "humidity")
    private float humidity;
    @ColumnInfo(name = "pressure")
    private float pressure;
    @ColumnInfo(name = "precipitation")
    private float precipitation;
    @ColumnInfo(name = "wind_speed")
    private float wind_speed;
    @ColumnInfo(name = "wind_direction")
    private float wind_direction;
    @ColumnInfo(name = "uv_index")
    private float uv_index;
    @Embedded
    private City city;

    public WeekForecast() {
    }

    @Ignore
    public WeekForecast(int id, int city_id, long timestamp, long forecastTime, int weatherID, float temperature, float temperature_min, float temperature_max, float humidity, float pressure, float precipitation, float wind_speed, float wind_direction, float uv_index) {
        this.id = id;
        this.city_id = city_id;
        this.timestamp = timestamp;
        this.forecastTime = forecastTime;
        this.weatherID = weatherID;
        this.temperature = temperature;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.humidity = humidity;
        this.pressure = pressure;
        this.precipitation = precipitation;
        this.wind_speed = wind_speed;
        this.wind_direction = wind_direction;
        this.uv_index = uv_index;
    }


    /**
     * @return Returns the ID of the record (which uniquely identifies the record).
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the date and time for the forecast.
     */
    public long getForecastTime() {
        return forecastTime;
    }

    /**
     * @return Returns the local time for the forecast in UTC epoch
     */
    public long getLocalForecastTime(Context context) {
        AppDatabase dbhelper = AppDatabase.getInstance(context);
        int timezoneseconds = dbhelper.currentWeatherDao().getCurrentWeatherByCityId(city_id).getTimeZoneSeconds();
        return forecastTime + timezoneseconds * 1000L;
    }

    /**
     * @param forecastFor The point of time for the forecast.
     */
    public void setForecastTime(long forecastFor) {
        this.forecastTime = forecastFor;
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

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
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
     * @return Returns the min temperature in Celsius.
     */
    public float getMinTemperature() {
        return temperature_min;
    }

    /**
     * @param temperature_min The min temperature to set in Celsius.
     */
    public void setMinTemperature(float temperature_min) {
        this.temperature_min = temperature_min;
    }

    /**
     * @return Returns the max temperature in Celsius.
     */
    public float getMaxTemperature() {
        return temperature_max;
    }

    /**
     * @param temperature_max The max temperature to set in Celsius.
     */
    public void setMaxTemperature(float temperature_max) {
        this.temperature_max = temperature_max;
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

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(float precipitation) {
        this.precipitation = precipitation;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public float getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(float wind_direction) {
        this.wind_direction = wind_direction;
    }

    public float getUv_index() {
        return uv_index;
    }

    public void setUv_index(float uv_index) {
        this.uv_index = uv_index;
    }

    public float getTemperature_min() {
        return temperature_min;
    }

    public void setTemperature_min(float temperature_min) {
        this.temperature_min = temperature_min;
    }

    public float getTemperature_max() {
        return temperature_max;
    }

    public void setTemperature_max(float temperature_max) {
        this.temperature_max = temperature_max;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }


}
