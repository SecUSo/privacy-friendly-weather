package org.secuso.privacyfriendlyweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.secuso.privacyfriendlyweather.orm.City;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonjuni on 02.01.17.
 */

public class PFASQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "PF_WEATHER_DB";

    //Names of tables in the database
    private static final String TABLE_CITIES = "CITIES";
    private static final String TABLE_FORECAST = "FORECASTS";
    private static final String TABLE_CURRENT_WEATHER = "CURRENT_WEATHER";

    //Names of columns in TABLE_CITIES
    private static final String CITIES_ID = "id";
    private static final String CITY_ID = "city_id";
    private static final String CITY_NAME = "city_name";
    private static final String COUNTRY_CODE = "country_code";
    private static final String POSTAL_CODE = "postal_code";
    private static final String CITIES_COLUMN_RANK = "rank";

    //Names of columns in TABLE_FORECAST
    private static final String FORECAST_ID = "id";
    private static final String FORECAST_CITY_ID = "city_id";
    private static final String FORECAST_COLUMN_TIME_MEASUREMENT = "time_of_measurement";
    private static final String FORECAST_COLUMN_FORECAST_FOR = "forecast_for";
    private static final String FORECAST_COLUMN_WEATHER_ID = "weather_id";
    private static final String FORECAST_COLUMN_TEMPERATURE_CURRENT = "temperature_current";
    private static final String FORECAST_COLUMN_HUMIDITY = "humidity";
    private static final String FORECAST_COLUMN_PRESSURE = "pressure";

    //Names of columns in TABLE_CURRENT_WEATHER
    private static final String CURRENT_WEATHER_ID = "id";
    private static final String CURRENT_WEATHER_CITY_ID = "city_id";
    private static final String COLUMN_TIME_MEASUREMENT = "time_of_measurement";
    private static final String COLUMN_WEATHER_ID = "weather_id";
    private static final String COLUMN_TEMPERATURE_CURRENT = "temperature_current";
    private static final String COLUMN_TEMPERATURE_MIN = "temperature_min";
    private static final String COLUMN_TEMPERATURE_MAX = "temperature_max";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_PRESSURE = "pressure";
    private static final String COLUMN_WIND_SPEED = "wind_speed";
    private static final String COLUMN_WIND_DIRECTION = "wind_direction";
    private static final String COLUMN_CLOUDINESS = "cloudiness";
    private static final String COLUMN_TIME_SUNRISE = "time_sunrise";
    private static final String COLUMN_TIME_SUNSET = "time_sunset";

    /**
     *   Create Table statements for all tables
     */
    private static final String CREATE_CURRENT_WEATHER = "CREATE TABLE " + TABLE_CURRENT_WEATHER +
            "(" +
            CURRENT_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CURRENT_WEATHER_CITY_ID + " INTEGER," +
            COLUMN_TIME_MEASUREMENT + " TEXT NOT NULL," +
            COLUMN_WEATHER_ID + " INTEGER," +
            COLUMN_TEMPERATURE_CURRENT + " REAL," +
            COLUMN_TEMPERATURE_MIN + " REAL," +
            COLUMN_TEMPERATURE_MAX + " REAL," +
            COLUMN_HUMIDITY + " REAL," +
            COLUMN_PRESSURE + " REAL," +
            COLUMN_WIND_SPEED + " REAL," +
            COLUMN_WIND_DIRECTION + " REAL," +
            COLUMN_CLOUDINESS + " REAL," +
            COLUMN_TIME_SUNRISE + "  TEXT NOT NULL," +
            COLUMN_TIME_SUNSET + "  TEXT NOT NULL," +
            " FOREIGN KEY ("+CURRENT_WEATHER_CITY_ID+") REFERENCES "+TABLE_CITIES+"("+CITIES_ID+"));";

    private static final String CREATE_TABLE_CITIES = "CREATE TABLE " + TABLE_FORECAST +
            "(" +
            FORECAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FORECAST_CITY_ID + " INTEGER," +
            FORECAST_COLUMN_TIME_MEASUREMENT + " TEXT NOT NULL," +
            FORECAST_COLUMN_FORECAST_FOR + " TEXT NOT NULL," +
            FORECAST_COLUMN_WEATHER_ID + " INTEGER," +
            FORECAST_COLUMN_TEMPERATURE_CURRENT + " REAL," +
            FORECAST_COLUMN_HUMIDITY + " REAL," +
            FORECAST_COLUMN_PRESSURE + " REAL," +
            " FOREIGN KEY ("+FORECAST_CITY_ID+") REFERENCES "+TABLE_CITIES+"("+CITIES_ID+"));";

    private static final String CREATE_TABLE_FORECASTS = "CREATE TABLE " + TABLE_CITIES +
            "(" +
            CITIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CITY_ID + " INTEGER," +
            CITY_NAME + " TEXT NOT NULL," +
            COUNTRY_CODE + " TEXT NOT NULL," +
            POSTAL_CODE + " TEXT NOT NULL," +
            CITIES_COLUMN_RANK + " INTEGER);";

    public PFASQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_CITIES);
        db.execSQL(CREATE_TABLE_FORECASTS);
        db.execSQL(CREATE_CURRENT_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_FORECASTS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CURRENT_WEATHER);

        // create new tables
        onCreate(db);
    }

    /**
     * Methods for TABLE_CITIES
     */
    public void addCityToWatch(CityToWatch city) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITY_ID, city.getCityId());
        values.put(CITY_NAME, city.getCityName());
        values.put(COUNTRY_CODE, city.getCountryCode());
        values.put(POSTAL_CODE, city.getPostalCode());
        values.put(CITIES_COLUMN_RANK, city.getRank());

        database.insert(TABLE_CITIES, null, values);
        database.close();
    }

    public CityToWatch getCityToWatch(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_CITIES, new String[]{CITIES_ID,
                        CITY_ID, CITY_NAME, COUNTRY_CODE, POSTAL_CODE, CITIES_COLUMN_RANK}, CITIES_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        CityToWatch cityToWatch = new CityToWatch();

        if( cursor != null && cursor.moveToFirst() ){
            cityToWatch.setId(Integer.parseInt(cursor.getString(0)));
            cityToWatch.setCityId(Integer.parseInt(cursor.getString(1)));
            cityToWatch.setCityName(cursor.getString(2));
            cityToWatch.setCountryCode(cursor.getString(3));
            cityToWatch.setPostalCode(cursor.getString(4));
            cityToWatch.setRank(Integer.parseInt(cursor.getString(5)));

            cursor.close();
        }

        return cityToWatch;

    }

    public List<CityToWatch> getAllCitiesToWatch() {
        List<CityToWatch> cityToWatchList = new ArrayList<CityToWatch>();

        String selectQuery = "SELECT  * FROM " + TABLE_CITIES;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        CityToWatch cityToWatch = null;

        if (cursor.moveToFirst()) {
            do {
                cityToWatch = new CityToWatch();
                cityToWatch.setId(Integer.parseInt(cursor.getString(0)));
                cityToWatch.setCityId(Integer.parseInt(cursor.getString(1)));
                cityToWatch.setCityName(cursor.getString(2));
                cityToWatch.setCountryCode(cursor.getString(3));
                cityToWatch.setPostalCode(cursor.getString(4));
                cityToWatch.setRank(Integer.parseInt(cursor.getString(5)));

                cityToWatchList.add(cityToWatch);
            } while (cursor.moveToNext());
        }

        return cityToWatchList;
    }

    public int updateCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITY_ID, cityToWatch.getCityId());
        values.put(CITY_NAME, cityToWatch.getCityName());
        values.put(COUNTRY_CODE, cityToWatch.getCountryCode());
        values.put(POSTAL_CODE, cityToWatch.getPostalCode());
        values.put(CITIES_COLUMN_RANK, cityToWatch.getRank());

        return database.update(TABLE_CITIES, values, CITIES_ID + " = ?",
                new String[] { String.valueOf(cityToWatch.getId()) });
    }

    public void deleteCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CITIES, CITIES_ID + " = ?",
                new String[] { Integer.toString(cityToWatch.getId()) });
        database.close();
    }


    /**
     * Methods for TABLE_FORECAST
     */
    public void addForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime().toString());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());

        database.insert(TABLE_FORECAST, null, values);
        database.close();
    }

    public Forecast getForecast(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_FORECAST,
                new String[]{FORECAST_ID,
                        FORECAST_CITY_ID,
                        FORECAST_COLUMN_TIME_MEASUREMENT,
                        FORECAST_COLUMN_FORECAST_FOR,
                        FORECAST_COLUMN_WEATHER_ID,
                        FORECAST_COLUMN_TEMPERATURE_CURRENT,
                        FORECAST_COLUMN_HUMIDITY,
                        FORECAST_COLUMN_PRESSURE}
                , CITIES_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Forecast forecast = new Forecast();

        if( cursor != null && cursor.moveToFirst() ){
            forecast.setId(Integer.parseInt(cursor.getString(0)));
            forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
            forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
            forecast.setForecastTime(Date.valueOf(cursor.getString(3)));
            forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
            forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
            forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
            forecast.setPressure(Float.parseFloat(cursor.getString(7)));

            cursor.close();
        }

        return forecast;

    }

    public List<Forecast> getAllForecasts() {
        List<Forecast> forecastList = new ArrayList<Forecast>();

        String selectQuery = "SELECT  * FROM " + TABLE_FORECAST;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        Forecast forecast = null;

        if (cursor.moveToFirst()) {
            do {
                forecast = new Forecast();
                forecast.setId(Integer.parseInt(cursor.getString(0)));
                forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
                forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
                forecast.setForecastTime(Date.valueOf(cursor.getString(3)));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));

                forecastList.add(forecast);
            } while (cursor.moveToNext());
        }

        return forecastList;
    }

    public int updateForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime().toString());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());


        return database.update(TABLE_FORECAST, values, FORECAST_ID + " = ?",
                new String[] { String.valueOf(forecast.getId()) });
    }

    public void deleteForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FORECAST, FORECAST_ID + " = ?",
                new String[] { Integer.toString(forecast.getId()) });
        database.close();
    }

    /**
     * Methods for TABLE_CURRENT_WEATHER
     */
    public void addCurrentWeather(CurrentWeatherData currentWeather) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CURRENT_WEATHER_CITY_ID, currentWeather.getCity_id());
        values.put(COLUMN_TIME_MEASUREMENT, currentWeather.getTimestamp());
        values.put(COLUMN_WEATHER_ID, currentWeather.getWeatherID());
        values.put(COLUMN_TEMPERATURE_CURRENT, currentWeather.getTemperatureCurrent());
        values.put(COLUMN_TEMPERATURE_MIN, currentWeather.getTemperatureMin());
        values.put(COLUMN_TEMPERATURE_MAX, currentWeather.getTemperatureMax());
        values.put(COLUMN_HUMIDITY, currentWeather.getHumidity());
        values.put(COLUMN_PRESSURE, currentWeather.getPressure());
        values.put(COLUMN_WIND_SPEED, currentWeather.getWindSpeed());
        values.put(COLUMN_WIND_DIRECTION, currentWeather.getWindDirection());
        values.put(COLUMN_CLOUDINESS, currentWeather.getCloudiness());
        values.put(COLUMN_TIME_SUNRISE, currentWeather.getTimeSunrise());
        values.put(COLUMN_TIME_SUNSET, currentWeather.getTimeSunset());

        database.insert(TABLE_CURRENT_WEATHER, null, values);
        database.close();
    }

    public CurrentWeatherData getCurrentWeather(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_CURRENT_WEATHER,
                new String[]{CURRENT_WEATHER_ID,
                        CURRENT_WEATHER_CITY_ID,
                        COLUMN_TIME_MEASUREMENT,
                        COLUMN_WEATHER_ID,
                        COLUMN_TEMPERATURE_CURRENT,
                        COLUMN_TEMPERATURE_MIN,
                        COLUMN_TEMPERATURE_MAX,
                        COLUMN_HUMIDITY,
                        COLUMN_PRESSURE,
                        COLUMN_WIND_SPEED,
                        COLUMN_WIND_DIRECTION,
                        COLUMN_CLOUDINESS,
                        COLUMN_TIME_SUNRISE,
                        COLUMN_TIME_SUNSET},
                CURRENT_WEATHER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        CurrentWeatherData currentWeather = new CurrentWeatherData();

        if( cursor != null && cursor.moveToFirst() ){
            currentWeather.setId(Integer.parseInt(cursor.getString(0)));
            currentWeather.setCity_id(Integer.parseInt(cursor.getString(1)));
            currentWeather.setTimestamp(Long.parseLong(cursor.getString(2)));
            currentWeather.setWeatherID(Integer.parseInt(cursor.getString(3)));
            currentWeather.setTemperatureCurrent(Float.parseFloat(cursor.getString(4)));
            currentWeather.setTemperatureMin(Float.parseFloat(cursor.getString(5)));
            currentWeather.setTemperatureMax(Float.parseFloat(cursor.getString(6)));
            currentWeather.setHumidity(Float.parseFloat(cursor.getString(7)));
            currentWeather.setPressure(Float.parseFloat(cursor.getString(8)));
            currentWeather.setWindSpeed(Float.parseFloat(cursor.getString(9)));
            currentWeather.setWindDirection(Float.parseFloat(cursor.getString(10)));
            currentWeather.setCloudiness(Float.parseFloat(cursor.getString(11)));
            currentWeather.setTimeSunrise(Long.parseLong(cursor.getString(12)));
            currentWeather.setTimeSunset(Long.parseLong(cursor.getString(13)));

            cursor.close();
        }

        return currentWeather;
    }

    public List<CurrentWeatherData> getAllCurrentWeathers() {
        List<CurrentWeatherData> currentWeatherList = new ArrayList<CurrentWeatherData>();

        String selectQuery = "SELECT  * FROM " + TABLE_CURRENT_WEATHER;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        CurrentWeatherData currentWeather = null;

        if (cursor.moveToFirst()) {
            do {
                currentWeather = new CurrentWeatherData();
                currentWeather.setId(Integer.parseInt(cursor.getString(0)));
                currentWeather.setCity_id(Integer.parseInt(cursor.getString(1)));
                currentWeather.setTimestamp(Long.parseLong(cursor.getString(2)));
                currentWeather.setWeatherID(Integer.parseInt(cursor.getString(3)));
                currentWeather.setTemperatureCurrent(Float.parseFloat(cursor.getString(4)));
                currentWeather.setTemperatureMin(Float.parseFloat(cursor.getString(5)));
                currentWeather.setTemperatureMax(Float.parseFloat(cursor.getString(6)));
                currentWeather.setHumidity(Float.parseFloat(cursor.getString(7)));
                currentWeather.setPressure(Float.parseFloat(cursor.getString(8)));
                currentWeather.setWindSpeed(Float.parseFloat(cursor.getString(9)));
                currentWeather.setWindDirection(Float.parseFloat(cursor.getString(10)));
                currentWeather.setCloudiness(Float.parseFloat(cursor.getString(11)));
                currentWeather.setTimeSunrise(Long.parseLong(cursor.getString(12)));
                currentWeather.setTimeSunset(Long.parseLong(cursor.getString(13)));

                currentWeatherList.add(currentWeather);
            } while (cursor.moveToNext());
        }

        return currentWeatherList;
    }

    public int updateCurrentWeather(CurrentWeatherData currentWeather) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CURRENT_WEATHER_CITY_ID, currentWeather.getCity_id());
        values.put(COLUMN_TIME_MEASUREMENT, currentWeather.getTimestamp());
        values.put(COLUMN_WEATHER_ID, currentWeather.getWeatherID());
        values.put(COLUMN_TEMPERATURE_CURRENT, currentWeather.getTemperatureCurrent());
        values.put(COLUMN_TEMPERATURE_MIN, currentWeather.getTemperatureMin());
        values.put(COLUMN_TEMPERATURE_MAX, currentWeather.getTemperatureMax());
        values.put(COLUMN_HUMIDITY, currentWeather.getHumidity());
        values.put(COLUMN_PRESSURE, currentWeather.getPressure());
        values.put(COLUMN_WIND_SPEED, currentWeather.getWindSpeed());
        values.put(COLUMN_WIND_DIRECTION, currentWeather.getWindDirection());
        values.put(COLUMN_CLOUDINESS, currentWeather.getCloudiness());
        values.put(COLUMN_TIME_SUNRISE, currentWeather.getTimeSunrise());
        values.put(COLUMN_TIME_SUNSET, currentWeather.getTimeSunset());

        return database.update(TABLE_CURRENT_WEATHER, values, CURRENT_WEATHER_ID + " = ?",
                new String[] { String.valueOf(currentWeather.getId()) });
    }

    public void deleteCurrentWeather(CurrentWeatherData currentWeather) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CURRENT_WEATHER, CURRENT_WEATHER_ID + " = ?",
                new String[] { Integer.toString(currentWeather.getId()) });
        database.close();
    }

}
