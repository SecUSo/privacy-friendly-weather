package org.secuso.privacyfriendlyweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.files.FileReader;

import java.io.IOException;
import java.io.InputStream;

import org.secuso.privacyfriendlyweather.database.City;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Karola Marky, Christopher Beckmann
 * @version 1.0
 * @since 25.01.2018
 * created 02.01.2017
 */
public class PFASQLiteHelper extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 1;
    private Context context;

    private List<City> allCities = new ArrayList<>();

    private static PFASQLiteHelper instance = null;

    public static final String DATABASE_NAME = "PF_WEATHER_DB.db";

    //Names of tables in the database
    private static final String TABLE_CITIES_TO_WATCH = "CITIES_TO_WATCH";
    private static final String TABLE_CITIES = "CITIES";
    private static final String TABLE_FORECAST = "FORECASTS";
    private static final String TABLE_CURRENT_WEATHER = "CURRENT_WEATHER";

    //Names of indices  in TABLE_CITY
    private static final String TABLE_CITIES_INDEX = "city_name_index";

    //Names of columns in TABLE_CITY
    private static final String CITIES_ID = "cities_id";
    private static final String CITIES_NAME = "city_name";
    private static final String CITIES_COUNTRY_CODE = "country_code";
    private static final String CITIES_POSTAL_CODE = "postal_code";

    //Names of columns in TABLE_CITIES_TO_WATCH
    private static final String CITIES_TO_WATCH_ID = "cities_to_watch_id";
    private static final String CITIES_TO_WATCH_CITY_ID = "city_id";
    private static final String CITIES_TO_WATCH_COLUMN_RANK = "rank";

    //Names of columns in TABLE_FORECAST
    private static final String FORECAST_ID = "forecast_id";
    private static final String FORECAST_CITY_ID = "city_id";
    private static final String FORECAST_COLUMN_TIME_MEASUREMENT = "time_of_measurement";
    private static final String FORECAST_COLUMN_FORECAST_FOR = "forecast_for";
    private static final String FORECAST_COLUMN_WEATHER_ID = "weather_id";
    private static final String FORECAST_COLUMN_TEMPERATURE_CURRENT = "temperature_current";
    private static final String FORECAST_COLUMN_HUMIDITY = "humidity";
    private static final String FORECAST_COLUMN_PRESSURE = "pressure";

    //Names of columns in TABLE_CURRENT_WEATHER
    private static final String CURRENT_WEATHER_ID = "current_weather_id";
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
     * Create Table statements for all tables
     */
    private static final String CREATE_CURRENT_WEATHER = "CREATE TABLE " + TABLE_CURRENT_WEATHER +
            "(" +
            CURRENT_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CURRENT_WEATHER_CITY_ID + " INTEGER," +
            COLUMN_TIME_MEASUREMENT + " LONG NOT NULL," +
            COLUMN_WEATHER_ID + " INTEGER," +
            COLUMN_TEMPERATURE_CURRENT + " REAL," +
            COLUMN_TEMPERATURE_MIN + " REAL," +
            COLUMN_TEMPERATURE_MAX + " REAL," +
            COLUMN_HUMIDITY + " REAL," +
            COLUMN_PRESSURE + " REAL," +
            COLUMN_WIND_SPEED + " REAL," +
            COLUMN_WIND_DIRECTION + " REAL," +
            COLUMN_CLOUDINESS + " REAL," +
            COLUMN_TIME_SUNRISE + "  VARCHAR(50) NOT NULL," +
            COLUMN_TIME_SUNSET + "  VARCHAR(50) NOT NULL," +
            " FOREIGN KEY (" + CURRENT_WEATHER_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + CITIES_ID + "));";

    private static final String CREATE_TABLE_CITIES = "CREATE TABLE " + TABLE_CITIES +
            "(" +
            CITIES_ID + " INTEGER PRIMARY KEY," +
            CITIES_NAME + " VARCHAR(100) NOT NULL," +
            CITIES_COUNTRY_CODE + " VARCHAR(10) NOT NULL," +
            CITIES_POSTAL_CODE + " VARCHAR(10) NOT NULL ); ";

    private static final String CREATE_TABLE_CITIES_INDEX = "CREATE INDEX " + TABLE_CITIES_INDEX +
            " ON " + TABLE_CITIES + " ("  + CITIES_NAME +  ");";

    private static final String CREATE_TABLE_FORECASTS = "CREATE TABLE " + TABLE_FORECAST +
            "(" +
            FORECAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FORECAST_CITY_ID + " INTEGER," +
            FORECAST_COLUMN_TIME_MEASUREMENT + " LONG NOT NULL," +
            FORECAST_COLUMN_FORECAST_FOR + " VARCHAR(200) NOT NULL," +
            FORECAST_COLUMN_WEATHER_ID + " INTEGER," +
            FORECAST_COLUMN_TEMPERATURE_CURRENT + " REAL," +
            FORECAST_COLUMN_HUMIDITY + " REAL," +
            FORECAST_COLUMN_PRESSURE + " REAL," +
            " FOREIGN KEY (" + FORECAST_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + CITIES_ID + "));";

    private static final String CREATE_TABLE_CITIES_TO_WATCH = "CREATE TABLE " + TABLE_CITIES_TO_WATCH +
            "(" +
            CITIES_TO_WATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CITIES_TO_WATCH_CITY_ID + " INTEGER," +
            CITIES_TO_WATCH_COLUMN_RANK + " INTEGER," +
            " FOREIGN KEY (" + CITIES_TO_WATCH_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + CITIES_ID + "));";

    public static PFASQLiteHelper getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new PFASQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    private PFASQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_FORECASTS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CURRENT_WEATHER);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CITIES_TO_WATCH);

        // create new tables
        onCreate(db);
    }

    /**
     * Fill TABLE_CITIES_TO_WATCH with all the Cities
     */
    private synchronized void fillCityDatabase(SQLiteDatabase db) {
        long startInsertTime = System.currentTimeMillis();

        InputStream inputStream = context.getResources().openRawResource(R.raw.city_list);
        try {
            FileReader fileReader = new FileReader();
            final List<City> cities = fileReader.readCitiesFromFile(inputStream);
            addCities(db, cities);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endInsertTime = System.currentTimeMillis();
        Log.d("debug_info", "Time for insert:" + String.valueOf(endInsertTime - startInsertTime));
    }

    private synchronized void addCities(SQLiteDatabase database, final List<City> cities) {
        if (cities.size() > 0) {

            //############################################
            // construct everything into one statement
//            StringBuilder sb = new StringBuilder();
//            sb.append("INSERT INTO ").append(TABLE_CITIES).append(" VALUES ");
//
//            for (int i = 0; i < cities.size(); i++) {
//                sb.append("(")
//                        .append(cities.get(i).getCityId()).append(", ")
//                        .append(cities.get(i).getCityName()).append(", ")
//                        .append(cities.get(i).getCountryCode()).append(", ")
//                        .append(cities.get(i).getPostalCode()).append(")");
//                if(i < cities.size() - 1) {
//                    sb.append(", ");
//                }
//            }
//            String sql = sb.toString();
//            database.rawQuery(sql, new String[]{});
            //############################################
            for (City c : cities) {
                ContentValues values = new ContentValues();
                values.put(CITIES_ID, c.getCityId());
                values.put(CITIES_NAME, c.getCityName());
                values.put(CITIES_COUNTRY_CODE, c.getCountryCode());
                values.put(CITIES_POSTAL_CODE, c.getPostalCode());
                database.insert(TABLE_CITIES, null, values);
            }
        }
    }

    public synchronized City getCityById(Integer id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] args = {id.toString()};

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_ID +
                        ", " + CITIES_NAME +
                        ", " + CITIES_COUNTRY_CODE +
                        ", " + CITIES_POSTAL_CODE +
                        " FROM " + TABLE_CITIES +
                        " WHERE " + CITIES_ID + " = ?", args);

        City city = new City();

        if (cursor != null && cursor.moveToFirst()) {

            city.setCityId(Integer.parseInt(cursor.getString(0)));
            city.setCityName(cursor.getString(1));
            city.setCountryCode(cursor.getString(2));
            city.setPostalCode(cursor.getString(3));

            cursor.close();
        }

        return city;
    }

    public synchronized List<City> getCitiesWhereNameLike(String cityNameLetters, int dropdownListLimit) {
        List<City> cities = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT " + CITIES_ID +
                ", " + CITIES_NAME +
                ", " + CITIES_COUNTRY_CODE +
                ", " + CITIES_POSTAL_CODE +
                " FROM " + TABLE_CITIES +
                " WHERE " + CITIES_NAME +
                " LIKE ?" +
                " ORDER BY " + CITIES_NAME +
                " LIMIT " + dropdownListLimit;

        String[] args = {String.format("%s%%", cityNameLetters)};
        Cursor cursor = database.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(Integer.parseInt(cursor.getString(0)));
                city.setCityName(cursor.getString(1));
                city.setCountryCode(cursor.getString(2));
                city.setPostalCode(cursor.getString(3));
                cities.add(city);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return cities;

    }


    /**
     * Methods for TABLE_CITIES_TO_WATCH
     */
    public synchronized void addCityToWatch(CityToWatch city) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITIES_TO_WATCH_CITY_ID, city.getCityId());
        values.put(CITIES_TO_WATCH_COLUMN_RANK, city.getRank());

        database.insert(TABLE_CITIES_TO_WATCH, null, values);
        database.close();
    }

    public synchronized CityToWatch getCityToWatch(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        String[] arguments = {String.valueOf(id)};

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_TO_WATCH_ID +
                        ", " + CITIES_TO_WATCH_CITY_ID +
                        ", " + CITIES_NAME +
                        ", " + CITIES_COUNTRY_CODE +
                        ", " + CITIES_POSTAL_CODE +
                        ", " + CITIES_TO_WATCH_COLUMN_RANK +
                        " FROM " + TABLE_CITIES_TO_WATCH + " INNER JOIN " + TABLE_CITIES +
                        " ON " + TABLE_CITIES_TO_WATCH + "." + CITIES_TO_WATCH_CITY_ID + " = " + TABLE_CITIES + "." + CITIES_ID +
                        " WHERE " + CITIES_TO_WATCH_CITY_ID + " = ?", arguments);

        CityToWatch cityToWatch = new CityToWatch();

        if (cursor != null && cursor.moveToFirst()) {
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

    public synchronized boolean isCityWatched(int cityId) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT " + CITIES_TO_WATCH_CITY_ID +
                " FROM " + TABLE_CITIES_TO_WATCH +
                " WHERE " + CITIES_TO_WATCH_CITY_ID + " = ?";

        String[] params = {String.valueOf(cityId)};
        Cursor cursor = database.rawQuery(query, params);

        boolean result = false;

        if (cursor.moveToFirst()) {
            result = !cursor.isNull(0);
        }

        return result;
    }

    public synchronized List<CityToWatch> getAllCitiesToWatch() {
        List<CityToWatch> cityToWatchList = new ArrayList<CityToWatch>();

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_TO_WATCH_ID +
                        ", " + CITIES_TO_WATCH_CITY_ID +
                        ", " + CITIES_NAME +
                        ", " + CITIES_COUNTRY_CODE +
                        ", " + CITIES_POSTAL_CODE +
                        ", " + CITIES_TO_WATCH_COLUMN_RANK +
                        " FROM " + TABLE_CITIES_TO_WATCH + " INNER JOIN " + TABLE_CITIES +
                        " ON " + TABLE_CITIES_TO_WATCH + "." + CITIES_TO_WATCH_CITY_ID + " = " + TABLE_CITIES + "." + CITIES_ID
                , new String[]{});

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

    public synchronized int updateCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITIES_TO_WATCH_CITY_ID, cityToWatch.getCityId());
        values.put(CITIES_TO_WATCH_COLUMN_RANK, cityToWatch.getRank());

        return database.update(TABLE_CITIES_TO_WATCH, values, CITIES_TO_WATCH_ID + " = ?",
                new String[]{String.valueOf(cityToWatch.getId())});
    }

    public void deleteCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CITIES_TO_WATCH, CITIES_TO_WATCH_ID + " = ?",
                new String[]{Integer.toString(cityToWatch.getId())});
        database.close();
    }


    /**
     * Methods for TABLE_FORECAST
     */
    public synchronized void addForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime().getTime());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());

        database.insert(TABLE_FORECAST, null, values);
        database.close();
    }

    public synchronized void deleteForecastsByCityId(int cityId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FORECAST, FORECAST_CITY_ID + " = ?",
                new String[]{Integer.toString(cityId)});
        database.close();
    }


    public synchronized List<Forecast> getForecastForCityByDay(int cityId, Date day) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + FORECAST_ID + ", " +
                                FORECAST_CITY_ID + ", " +
                                FORECAST_COLUMN_TIME_MEASUREMENT + ", " +
                                FORECAST_COLUMN_FORECAST_FOR + ", " +
                                FORECAST_COLUMN_WEATHER_ID + ", " +
                                FORECAST_COLUMN_TEMPERATURE_CURRENT + ", " +
                                FORECAST_COLUMN_HUMIDITY + ", " +
                                FORECAST_COLUMN_PRESSURE + ", " +
                                CITIES_NAME +
                                " FROM " + TABLE_FORECAST +
                                " INNER JOIN " + TABLE_CITIES + " ON " + CITIES_ID + " = " + FORECAST_CITY_ID +
                                " WHERE " + FORECAST_CITY_ID + " = ? AND " + FORECAST_COLUMN_FORECAST_FOR + " = ?",
                new String[]{String.valueOf(cityId)});

        List<Forecast> list = new ArrayList<>();
        Forecast forecast = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                forecast = new Forecast();
                forecast.setId(Integer.parseInt(cursor.getString(0)));
                forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
                forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
                forecast.setForecastTime(new Date(Long.parseLong(cursor.getString(3))));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));
                forecast.setCity_name(cursor.getString(8));
                list.add(forecast);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    public synchronized List<Forecast> getForecastsByCityId(int cityId) {
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
                , FORECAST_CITY_ID + "=?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);

        List<Forecast> list = new ArrayList<>();
        Forecast forecast = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                forecast = new Forecast();
                forecast.setId(Integer.parseInt(cursor.getString(0)));
                forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
                forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
                forecast.setForecastTime(new Date(Long.parseLong(cursor.getString(3))));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));
                list.add(forecast);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    public synchronized Forecast getForecast(int id) {
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
                , FORECAST_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Forecast forecast = new Forecast();

        if (cursor != null && cursor.moveToFirst()) {
            forecast.setId(Integer.parseInt(cursor.getString(0)));
            forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
            forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
            forecast.setForecastTime(new Date(Long.parseLong(cursor.getString(3))));
            forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
            forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
            forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
            forecast.setPressure(Float.parseFloat(cursor.getString(7)));

            cursor.close();
        }

        return forecast;

    }

    public synchronized List<Forecast> getAllForecasts() {
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
                forecast.setForecastTime(new Date(Long.parseLong(cursor.getString(3))));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));

                forecastList.add(forecast);
            } while (cursor.moveToNext());
        }

        return forecastList;
    }

    public synchronized int updateForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime().getTime());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());

        return database.update(TABLE_FORECAST, values, FORECAST_ID + " = ?",
                new String[]{String.valueOf(forecast.getId())});
    }

    public synchronized void deleteForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FORECAST, FORECAST_ID + " = ?",
                new String[]{Integer.toString(forecast.getId())});
        database.close();
    }

    /**
     * Methods for TABLE_CURRENT_WEATHER
     */
    public synchronized void addCurrentWeather(CurrentWeatherData currentWeather) {
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

    public synchronized CurrentWeatherData getCurrentWeather(int id) {
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
                CURRENT_WEATHER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        CurrentWeatherData currentWeather = new CurrentWeatherData();

        if (cursor != null && cursor.moveToFirst()) {
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

    public synchronized CurrentWeatherData getCurrentWeatherByCityId(int cityId) {
        SQLiteDatabase database = this.getReadableDatabase();

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
                CURRENT_WEATHER_CITY_ID + " = ?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);

        CurrentWeatherData currentWeather = new CurrentWeatherData();

        if (cursor != null && cursor.moveToFirst()) {
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

    public synchronized List<CurrentWeatherData> getAllCurrentWeathers() {
        List<CurrentWeatherData> currentWeatherList = new ArrayList<CurrentWeatherData>();

        String selectQuery = "SELECT * FROM " + TABLE_CURRENT_WEATHER;

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

    public synchronized int updateCurrentWeather(CurrentWeatherData currentWeather) {
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

        return database.update(TABLE_CURRENT_WEATHER, values, CURRENT_WEATHER_CITY_ID + " = ?",
                new String[]{String.valueOf(currentWeather.getCity_id())});
    }

    public synchronized void deleteCurrentWeather(CurrentWeatherData currentWeather) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CURRENT_WEATHER, CURRENT_WEATHER_ID + " = ?",
                new String[]{Integer.toString(currentWeather.getId())});
        database.close();
    }

    public synchronized void deleteCurrentWeatherByCityId(int cityId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CURRENT_WEATHER, CURRENT_WEATHER_CITY_ID + " = ?",
                new String[]{Integer.toString(cityId)});
        database.close();
    }
}
