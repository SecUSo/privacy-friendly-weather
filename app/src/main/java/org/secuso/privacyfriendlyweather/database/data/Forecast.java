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
 * This class is the database model for the forecasts table.
 */
@Entity(tableName = "FORECASTS", foreignKeys = {
        @ForeignKey(entity = City.class,
                parentColumns = {"cities_id"},
                childColumns = {"city_id"},
                onDelete = ForeignKey.CASCADE)})
public class Forecast {

    public static final float NO_RAIN_VALUE = 0;

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "forecast_id") private int id;
    @ColumnInfo(name = "city_id") private int city_id;
    @ColumnInfo(name = "time_of_measurement") private long timestamp;
    @ColumnInfo(name = "forecast_for") private long forecastTime;
    @ColumnInfo(name = "weather_id") private int weatherID;
    @ColumnInfo(name = "temperature_current") private float temperature;
    @ColumnInfo(name = "humidity") private float humidity;
    @ColumnInfo(name = "pressure") private float pressure;
    @ColumnInfo(name = "wind_speed") private float windSpeed;
    @ColumnInfo(name = "wind_direction") private float windDirection;
    @ColumnInfo(name = "precipitation") private float rainValue;
    @Embedded private City city;

    public Forecast() { }

    @Ignore public Forecast(int id, int city_id, long timestamp, long forecastFor, int weatherID, float temperature, float humidity,
                    float pressure, float windSpeed, float windDirection, float rainValue) {
        this.id = id;
        this.city_id = city_id;
        this.timestamp = timestamp;
        this.forecastTime = forecastFor;
        this.weatherID = weatherID;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.rainValue = rainValue;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float speed) {
        windSpeed = speed;
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

    public String getCity_name() {
        return city.getCityName();
    }

    public void setCity_name(String city_name) {
        this.city.setCityName(city_name);
    }

    public float getRainValue() {
        return rainValue;
    }

    public void setRainValue(float RainValue) {
        rainValue = RainValue;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
