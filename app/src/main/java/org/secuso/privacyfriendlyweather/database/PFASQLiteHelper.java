package org.secuso.privacyfriendlyweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.files.FileReader;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

/**
 * @author Karola Marky, Christopher Beckmann
 * @version 1.0
 * @since 25.01.2018
 * created 02.01.2017
 */
@Deprecated
public class PFASQLiteHelper extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 5;
    private Context context;

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
    private static final String CITIES_LONGITUDE = "longitude";
    private static final String CITIES_LATITUDE = "latitude";


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
    private static final String FORECAST_COLUMN_PRECIPITATION = "precipitation";
    private static final String FORECAST_COLUMN_WIND_SPEED = "wind_speed";
    private static final String FORECAST_COLUMN_WIND_DIRECTION = "wind_direction";

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
    private static final String COLUMN_TIMEZONE_SECONDS = "timezone_seconds";

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
            COLUMN_TIMEZONE_SECONDS + " INTEGER," +
            " FOREIGN KEY (" + CURRENT_WEATHER_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + CITIES_ID + "));";

    private static final String CREATE_TABLE_CITIES = "CREATE TABLE " + TABLE_CITIES +
            "(" +
            CITIES_ID + " INTEGER PRIMARY KEY," +
            CITIES_NAME + " VARCHAR(100) NOT NULL," +
            CITIES_COUNTRY_CODE + " VARCHAR(10) NOT NULL," +
            CITIES_LONGITUDE + " REAL NOT NULL," +
            CITIES_LATITUDE + " REAL NOT NULL ); ";

    private static final String CREATE_TABLE_CITIES_INDEX = "CREATE INDEX " + TABLE_CITIES_INDEX +
            " ON " + TABLE_CITIES + " (" + CITIES_NAME + ");";

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
            FORECAST_COLUMN_PRECIPITATION + " REAL," +
            FORECAST_COLUMN_WIND_SPEED + " REAL," +
            FORECAST_COLUMN_WIND_DIRECTION + " REAL," +
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

    @VisibleForTesting
    public PFASQLiteHelper(Context context, String name, String directory, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, directory, factory, version);
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         // on upgrade drop older tables
         db.execSQL(String.format("DROP TABLE IF EXISTS %s", CREATE_TABLE_CITIES));
         db.execSQL(String.format("DROP TABLE IF EXISTS %s", CREATE_TABLE_FORECASTS));
         db.execSQL(String.format("DROP TABLE IF EXISTS %s", CREATE_CURRENT_WEATHER));
         db.execSQL(String.format("DROP TABLE IF EXISTS %s", CREATE_TABLE_CITIES_TO_WATCH));

         // create new tables
         onCreate(db);
         **/
        super.onUpgrade(db, oldVersion, newVersion);

        if (oldVersion < 5) fillCityDatabase(db);

        Intent intent = new Intent(context, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_ALL_ACTION);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        enqueueWork(context, UpdateDataService.class, 0, intent);
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
        Log.d("debug_info", "Time for insert:" + (endInsertTime - startInsertTime));
        db.execSQL(CREATE_TABLE_CITIES_INDEX);
    }

    private synchronized void addCities(SQLiteDatabase database, final List<City> cities) {
        if (cities.size() > 0) {
            database.beginTransaction();
            boolean success = true;
            for (City c : cities) {
                ContentValues values = new ContentValues();
                values.put(CITIES_ID, c.getCityId());
                values.put(CITIES_NAME, c.getCityName());
                values.put(CITIES_COUNTRY_CODE, c.getCountryCode());
                values.put(CITIES_LONGITUDE, c.getLongitude());
                values.put(CITIES_LATITUDE, c.getLatitude());
                long id = database.insert(TABLE_CITIES, null, values);
                success &= id != -1;
            }
            if (success) {
                database.setTransactionSuccessful();
            }
            database.endTransaction();
        }
    }

    public synchronized City getCityById(Integer id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] args = {id.toString()};

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_ID +
                        ", " + CITIES_NAME +
                        ", " + CITIES_COUNTRY_CODE +
                        ", " + CITIES_LONGITUDE +
                        ", " + CITIES_LATITUDE +
                        " FROM " + TABLE_CITIES +
                        " WHERE " + CITIES_ID + " = ?", args);

        City city = new City();

        if (cursor != null && cursor.moveToFirst()) {

            city.setCityId(Integer.parseInt(cursor.getString(0)));
            city.setCityName(cursor.getString(1));
            city.setCountryCode(cursor.getString(2));
            city.setLongitude(cursor.getFloat(3));
            city.setLatitude(cursor.getFloat(4));

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
                ", " + CITIES_LONGITUDE +
                ", " + CITIES_LATITUDE +
                " FROM " + TABLE_CITIES +
                " WHERE " + CITIES_NAME +
                " LIKE ?" +
                " ORDER BY " + CITIES_NAME +
                " LIMIT " + dropdownListLimit;

        Log.d("devtag", "searchphrase: " + String.format("%s", cityNameLetters));
        String[] args = {String.format("%s%%", cityNameLetters)};
        Cursor cursor = database.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(Integer.parseInt(cursor.getString(0)));
                city.setCityName(cursor.getString(1));
                city.setCountryCode(cursor.getString(2));
                city.setLongitude(cursor.getFloat(3));
                city.setLatitude(cursor.getFloat(4));
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
            cityToWatch.setRank(Integer.parseInt(cursor.getString(4)));

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
        cursor.close();

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
                        ", " + CITIES_TO_WATCH_COLUMN_RANK +
                        " FROM " + TABLE_CITIES_TO_WATCH + " INNER JOIN " + TABLE_CITIES +
                        " ON " + TABLE_CITIES_TO_WATCH + "." + CITIES_TO_WATCH_CITY_ID + " = " + TABLE_CITIES + "." + CITIES_ID
                , new String[]{});

        CityToWatch cityToWatch;

        if (cursor.moveToFirst()) {
            do {
                cityToWatch = new CityToWatch();
                cityToWatch.setId(Integer.parseInt(cursor.getString(0)));
                cityToWatch.setCityId(Integer.parseInt(cursor.getString(1)));
                cityToWatch.setCityName(cursor.getString(2));
                cityToWatch.setCountryCode(cursor.getString(3));
                cityToWatch.setRank(Integer.parseInt(cursor.getString(4)));

                cityToWatchList.add(cityToWatch);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cityToWatchList;
    }

    public synchronized int updateCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITIES_TO_WATCH_CITY_ID, cityToWatch.getCityId());
        values.put(CITIES_TO_WATCH_COLUMN_RANK, cityToWatch.getRank());
        values.put(CITIES_TO_WATCH_ID, cityToWatch.getId());

        return database.update(TABLE_CITIES_TO_WATCH, values, CITIES_TO_WATCH_ID + " = ?",
                new String[]{String.valueOf(cityToWatch.getId())});
    }

    public void deleteCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CITIES_TO_WATCH, CITIES_TO_WATCH_ID + " = ?",
                new String[]{Integer.toString(cityToWatch.getId())});
        database.close();
    }

    public int getWatchedCitiesCount() {
        SQLiteDatabase database = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(database, TABLE_CITIES_TO_WATCH);
        database.close();
        return (int) count;
    }

    public int getMaxRank() {
        List<CityToWatch> cities = getAllCitiesToWatch();
        int maxRank = 0;
        for (CityToWatch ctw : cities) {
            if (ctw.getRank() > maxRank) maxRank = ctw.getRank();
        }
        return maxRank;
    }


    /**
     * Methods for TABLE_FORECAST
     */
    public synchronized void addForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());
        values.put(FORECAST_COLUMN_PRECIPITATION, forecast.getRainValue());
        values.put(FORECAST_COLUMN_WIND_SPEED, forecast.getWindSpeed());
        values.put(FORECAST_COLUMN_WIND_DIRECTION, forecast.getWindDirection());
        database.insert(TABLE_FORECAST, null, values);
        database.close();
    }

    public synchronized void deleteForecastsByCityId(int cityId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FORECAST, FORECAST_CITY_ID + " = ?",
                new String[]{Integer.toString(cityId)});
        database.close();
    }

    public synchronized void deleteOldForecastsByCityId(int cityId, long timestamp) {
        SQLiteDatabase database = this.getWritableDatabase();
        long cutofftime = timestamp - 24 * 60 * 60 * 1000L;
        database.delete(TABLE_FORECAST, FORECAST_CITY_ID + " = ? AND " + FORECAST_COLUMN_FORECAST_FOR + " <= ?",
                new String[]{Integer.toString(cityId), Long.toString(cutofftime)});
        database.close();
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
                        FORECAST_COLUMN_PRESSURE,
                        FORECAST_COLUMN_PRECIPITATION,
                        FORECAST_COLUMN_WIND_SPEED,
                        FORECAST_COLUMN_WIND_DIRECTION}
                , FORECAST_CITY_ID + "=?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);

        List<Forecast> list = new ArrayList<>();
        Forecast forecast;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                forecast = new Forecast();
                forecast.setId(Integer.parseInt(cursor.getString(0)));
                forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
                forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
                forecast.setForecastTime(Long.parseLong(cursor.getString(3)));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));
                forecast.setRainValue(Float.parseFloat(cursor.getString(8)));
                forecast.setWindSpeed(Float.parseFloat(cursor.getString(9)));
                forecast.setWindDirection(Float.parseFloat(cursor.getString(10)));
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
                        FORECAST_COLUMN_PRESSURE,
                        FORECAST_COLUMN_PRECIPITATION,
                        FORECAST_COLUMN_WIND_SPEED,
                        FORECAST_COLUMN_WIND_DIRECTION}
                , FORECAST_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Forecast forecast = new Forecast();

        if (cursor != null && cursor.moveToFirst()) {
            forecast.setId(Integer.parseInt(cursor.getString(0)));
            forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
            forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
            forecast.setForecastTime(Long.parseLong(cursor.getString(3)));
            forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
            forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
            forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
            forecast.setPressure(Float.parseFloat(cursor.getString(7)));
            forecast.setRainValue(Float.parseFloat(cursor.getString(8)));
            forecast.setWindSpeed(Float.parseFloat(cursor.getString(9)));
            forecast.setWindDirection(Float.parseFloat(cursor.getString(10)));
            cursor.close();
        }

        return forecast;

    }

    public synchronized List<Forecast> getAllForecasts() {
        List<Forecast> forecastList = new ArrayList<Forecast>();

        String selectQuery = "SELECT  * FROM " + TABLE_FORECAST;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        Forecast forecast;

        if (cursor.moveToFirst()) {
            do {
                forecast = new Forecast();
                forecast.setId(Integer.parseInt(cursor.getString(0)));
                forecast.setCity_id(Integer.parseInt(cursor.getString(1)));
                forecast.setTimestamp(Long.parseLong(cursor.getString(2)));
                forecast.setForecastTime(Long.parseLong(cursor.getString(3)));
                forecast.setWeatherID(Integer.parseInt(cursor.getString(4)));
                forecast.setTemperature(Float.parseFloat(cursor.getString(5)));
                forecast.setHumidity(Float.parseFloat(cursor.getString(6)));
                forecast.setPressure(Float.parseFloat(cursor.getString(7)));
                forecast.setRainValue(Float.parseFloat(cursor.getString(8)));
                forecast.setWindSpeed(Float.parseFloat(cursor.getString(9)));
                forecast.setWindDirection(Float.parseFloat(cursor.getString(10)));
                forecastList.add(forecast);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return forecastList;
    }

    public synchronized int updateForecast(Forecast forecast) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FORECAST_CITY_ID, forecast.getCity_id());
        values.put(FORECAST_COLUMN_TIME_MEASUREMENT, forecast.getTimestamp());
        values.put(FORECAST_COLUMN_FORECAST_FOR, forecast.getForecastTime());
        values.put(FORECAST_COLUMN_WEATHER_ID, forecast.getWeatherID());
        values.put(FORECAST_COLUMN_TEMPERATURE_CURRENT, forecast.getTemperature());
        values.put(FORECAST_COLUMN_HUMIDITY, forecast.getHumidity());
        values.put(FORECAST_COLUMN_PRESSURE, forecast.getPressure());
        values.put(FORECAST_COLUMN_PRECIPITATION, forecast.getRainValue());
        values.put(FORECAST_COLUMN_WIND_SPEED, forecast.getWindSpeed());
        values.put(FORECAST_COLUMN_WIND_DIRECTION, forecast.getWindDirection());

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
        values.put(COLUMN_TIMEZONE_SECONDS, currentWeather.getTimeZoneSeconds());


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
                        COLUMN_TIME_SUNSET,
                        COLUMN_TIMEZONE_SECONDS},
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
            currentWeather.setTimeZoneSeconds(Integer.parseInt(cursor.getString(14)));

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
                        COLUMN_TIME_SUNSET,
                        COLUMN_TIMEZONE_SECONDS},
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
            currentWeather.setTimeZoneSeconds(Integer.parseInt(cursor.getString(14)));

            cursor.close();
        }

        return currentWeather;
    }

    public synchronized List<CurrentWeatherData> getAllCurrentWeathers() {
        List<CurrentWeatherData> currentWeatherList = new ArrayList<CurrentWeatherData>();

        String selectQuery = "SELECT * FROM " + TABLE_CURRENT_WEATHER;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        CurrentWeatherData currentWeather;

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
                currentWeather.setTimeZoneSeconds(Integer.parseInt(cursor.getString(14)));

                currentWeatherList.add(currentWeather);
            } while (cursor.moveToNext());
        }

        cursor.close();
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
        values.put(COLUMN_TIMEZONE_SECONDS, currentWeather.getTimeZoneSeconds());

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
