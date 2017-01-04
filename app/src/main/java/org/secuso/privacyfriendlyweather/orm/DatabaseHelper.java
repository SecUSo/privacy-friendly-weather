package org.secuso.privacyfriendlyweather.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.files.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * Database information
     */
    //TODO Change name that it fits to PFA-DB structure
    private static final String DATABASE_NAME = "privacyfriendlyweather";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    /**
     * The data access objects used to interact with the SQLite database to do C.R.U.D operations.
     */
    private Dao<City, Integer> cityDao;
    private Dao<CityToWatch, Integer> cityToWatchDao;
    private Dao<CurrentWeatherData, Integer> currentWeatherDataDao;
    private Dao<Forecast, Integer> forecastDao;

    /**
     * @see OrmLiteSqliteOpenHelper
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (cityDao == null || cityToWatchDao == null || currentWeatherDataDao == null || forecastDao == null) {
            try {
                cityDao = getDao(City.class);
                cityToWatchDao = getDao(CityToWatch.class);
                currentWeatherDataDao = getDao(CurrentWeatherData.class);
                forecastDao = getDao(Forecast.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method fills the cities table.
     */
    private void fillCitiesTable() {
//        Log.d("debug_info", "Filling DB");
//        long startInsertTime = System.currentTimeMillis();
//        InputStream inputStream = context.getResources().openRawResource(R.raw.city_list);
//        FileReader fileReader = new FileReader();
//        try {
//            final List<City> cities = fileReader.readCitiesFromFile(inputStream);
//            inputStream.close();
//            // TODO: This method is rather slow for 75k inserts, maybe there is a faster one?
//            /*
//            - Using transactions as suggested here
//              http://stackoverflow.com/questions/18884587/thousands-of-ormlite-raw-inserts-taking-several-minutes-on-android
//              makes no performance difference
//            - Neither do batch tasks as here
//              http://stackoverflow.com/questions/18884587/thousands-of-ormlite-raw-inserts-taking-several-minutes-on-android
//            - Generating a query, loading it and then using updateRaw makes the app crash due to
//              some problem in updateRaw
//              http://stackoverflow.com/questions/5054974/android-ormlite-pre-populate-database
//             */
//            cityDao.create(cities);
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//        long endInsertTime = System.currentTimeMillis();
//        Log.d("debug_info", "Time for insert:" + String.valueOf(endInsertTime - startInsertTime));
    }

    /**
     * @see OrmLiteSqliteOpenHelper#onCreate(SQLiteDatabase, ConnectionSource)
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, City.class);
            TableUtils.createTable(connectionSource, CityToWatch.class);
            TableUtils.createTable(connectionSource, CurrentWeatherData.class);
            TableUtils.createTable(connectionSource, Forecast.class);
            fillCitiesTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see OrmLiteSqliteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, City.class, false);
            TableUtils.dropTable(connectionSource, CityToWatch.class, false);
            TableUtils.dropTable(connectionSource, CurrentWeatherData.class, false);
            TableUtils.dropTable(connectionSource, Forecast.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cityFirstLetters Find only those cities where the first letters of the city name match
     *                         this parameter.
     * @param limit            Limits the number of returned cities.
     * @return Returns a list of cities where each city starts with the provided letters.
     */
    public List<City> getCitiesWhereNameLike(String cityFirstLetters, long limit) {
        List<City> cities = new ArrayList<>();
        QueryBuilder<City, Integer> queryBuilder = cityDao.queryBuilder();

        try {
            queryBuilder.where().like(City.COLUMN_CITY_NAME, String.format("%s%%", cityFirstLetters));
            queryBuilder.orderBy(City.COLUMN_CITY_NAME, true);
            queryBuilder.limit(limit);

            PreparedQuery<City> preparedQuery = queryBuilder.prepare();

            for (City city : cityDao.query(preparedQuery)) {
                cities.add(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cities;
    }

    /**
     * @return Returns all the records in the cities_to_watch table.
     * @throws SQLException Might be thrown if there is some error while retrieving records.
     */
    public List<CityToWatch> getAllCitiesToWatch(boolean onlyNonPermanent) throws SQLException {
        QueryBuilder<CityToWatch, Integer> queryBuilder = cityToWatchDao.queryBuilder();
        queryBuilder.orderBy(CityToWatch.COLUMN_RANK, true);
        if (onlyNonPermanent) {
            queryBuilder.where().eq(CityToWatch.COLUMN_STORE_PERSISTENT, true);
        }
        PreparedQuery<CityToWatch> preparedQuery = queryBuilder.prepare();
        return cityToWatchDao.query(preparedQuery);
    }

    public void swapRanksOfCitiesToWatch(CityToWatch city1, CityToWatch city2) {
        // Get the ranks
        long rank1 = city1.getRank();
        long rank2 = city2.getRank();

        // Set the ranks
        city1.setRank(rank2);
        city2.setRank(rank1);

        // Update records
        try {
            int updated1 = cityToWatchDao.update(city1);
            int updated2 = cityToWatchDao.update(city2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a given city is already in the CityToWatch table.
     *
     * @param city The city to check if already present.
     * @return Returns true if the given city is already watched else false.
     */
    public boolean isCityAlreadyWatched(City city) {
        QueryBuilder<CityToWatch, Integer> queryBuilder = cityToWatchDao.queryBuilder();
        try {
            queryBuilder.where().eq(CityToWatch.CITY_ID, city.getId());
            PreparedQuery<CityToWatch> preparedQuery = queryBuilder.prepare();
            List<CityToWatch> cities = cityToWatchDao.query(preparedQuery);
            return (cities.size() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            // In case of error return false => worse case: city will be present several times
            return false;
        }
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param id The ID (value in the column id).
     * @return Returns the city that matches the ID or null in case non was found or an
     * SQLException occurred.
     */
    public City getCityByID(int id) {
        QueryBuilder<City, Integer> queryBuilder = cityDao.queryBuilder();
        try {
            queryBuilder.where().eq("id", id);
            PreparedQuery<City> prepare = queryBuilder.prepare();
            List<City> cities = cityDao.query(prepare);
            return cities.size() == 0 ? null : cities.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a city by its cityId.
     *
     * @param cityId The cityId (value in the column city_id).
     * @return Returns the city that matches the city ID or null in case non was found or an
     * SQLException occurred.
     */
    public City getCityByCityID(int cityId) {
        QueryBuilder<City, Integer> queryBuilder = cityDao.queryBuilder();
        try {
            queryBuilder.where().eq(City.COLUMN_CITY_ID, cityId);
            PreparedQuery<City> prepare = queryBuilder.prepare();
            List<City> cities = cityDao.query(prepare);
            return cities.size() == 0 ? null : cities.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the CityToWatch record for a given cityId.
     *
     * @param cityId The ID of the city to retrieve the record for.
     * @return Returns an instance of CityToWatch if a record with the given ID was present else
     * null (null also in case of SQLException).
     */
    public CityToWatch getCityToWatchByCityId(int cityId) {
        QueryBuilder<CityToWatch, Integer> queryBuilder = cityToWatchDao.queryBuilder();
        try {
            queryBuilder.where().eq(CityToWatch.CITY_ID, cityId);
            PreparedQuery<CityToWatch> query = queryBuilder.prepare();
            List<CityToWatch> result = cityToWatchDao.query(query);
            return (result.size() == 0) ? null : result.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a CurrentWeatherData record by ID.
     *
     * @param ID The ID to get the record for.
     * @return Either returns an instance of CurrentWeatherData if the record was found or null
     * instead.
     * @throws SQLException This exception is thrown if some error during querying the database
     *                      occurs.
     */
    public CurrentWeatherData getCurrentWeatherDataByID(final int ID) throws SQLException {
        QueryBuilder<CurrentWeatherData, Integer> queryBuilder = currentWeatherDataDao.queryBuilder();
        queryBuilder.where().eq("id", ID);
        PreparedQuery<CurrentWeatherData> prepare = queryBuilder.prepare();
        List<CurrentWeatherData> result = currentWeatherDataDao.query(prepare);
        return (result.size() == 0) ? null : result.get(0);
    }

    /**
     * @param sortByRank If this value is set to true, the records will be sorted by the rank of the
     *                   corresponding CityToWatch rank.
     * @return Returns all current weather information. In case of an error, an empty list is
     * returned.
     */
    public List<CurrentWeatherData> getCurrentWeatherData(boolean sortByRank) {
        try {
            List<CurrentWeatherData> weatherDataList = currentWeatherDataDao.queryForAll();
            if (sortByRank) {
                Collections.sort(weatherDataList, new CurrentWeatherComparator(this));
            }
            return weatherDataList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @param cityId The ID of the city to get the data for.
     * @param day    The day to get the data for.
     * @return Returns a list of Forecast objects that match the given city and day.
     * @throws SQLException In case an error occurs while fetching the records.
     */
    public List<Forecast> getForecastForCityByDay(int cityId, Date day) throws SQLException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        QueryBuilder<Forecast, Integer> queryBuilder = forecastDao.queryBuilder();
        queryBuilder.where()
                .eq(Forecast.CITY_ID, cityId)
                .and().raw(String.format("`%s` LIKE '%s%%'", Forecast.COLUMN_FORECAST_FOR, dateFormatter.format(day)));
        PreparedQuery<Forecast> preparedQuery = queryBuilder.prepare();
        return forecastDao.query(preparedQuery);
    }

    /**
     * Deletes all entries in the cities_to_watch table whose field persistent is set to false.
     *
     * @return Returns the number of deleted entries.
     * @throws SQLException This exception is thrown if something goes wrong while deleting
     *                      records.
     */
    public int deleteNonPersistentCitiesToWatch() throws SQLException, NullPointerException {
        DeleteBuilder<CityToWatch, Integer> deletionQuery = cityToWatchDao.deleteBuilder();
        deletionQuery.where().eq(CityToWatch.COLUMN_STORE_PERSISTENT, false);
        return deletionQuery.delete();
    }

    /**
     * Clears the table that corresponds to CurrentWeatherData.
     *
     * @return Returns the number of deleted rows.
     * @throws SQLException This exception might be thrown while cleating the table.
     */
    public int clearCurrentWeatherDataTable() throws SQLException {
        DeleteBuilder<CurrentWeatherData, Integer> deletionQuery = currentWeatherDataDao.deleteBuilder();
        return deletionQuery.delete();
    }


    /**
     * Deletes an entry from the CurrentWeatherData table.
     *
     * @param ID The ID that identifies the record to delete.
     * @return Returns the number of deleted row which can be either 0 (record could not be found)
     * or 1 (record was deleted),
     * @throws SQLException This exception is thrown if the deletion query could not be executed.
     */
    public int deleteCurrentWeatherRecordByID(final int ID) throws SQLException {
        DeleteBuilder<CurrentWeatherData, Integer> deleteQuery = currentWeatherDataDao.deleteBuilder();
        deleteQuery.where().eq("ID", ID);
        return deleteQuery.delete();
    }

    /**
     * Deletes an entry from the CityToWatch table.
     *
     * @param CITY_TO_WATCH_ID The ID of the city that identifies the record to delete.
     * @return Returns the number of deleted row which can be either 0 (record could not be found)
     * or 1 (record was deleted),
     * @throws SQLException This exception is thrown if the deletion query could not be executed.
     */
    public int deleteCityToWatchRecordByCityID(final int CITY_TO_WATCH_ID) throws SQLException {
        DeleteBuilder<CityToWatch, Integer> deleteQuery = cityToWatchDao.deleteBuilder();
        deleteQuery.where().eq(CityToWatch.CITY_ID, CITY_TO_WATCH_ID);
        return deleteQuery.delete();
    }

    /**
     * Deletes entries from the Forecast table.
     *
     * @param CITY_ID The ID of the city that identifies the records to delete.
     * @return Returns the number of deleted rows.
     * @throws SQLException This exception is thrown if the deletion query could not be executed.
     */
    public int deleteForecastRecordsByCityID(final int CITY_ID) throws SQLException {
        City city = getCityByCityID(CITY_ID);
        if (city != null) {
            DeleteBuilder<Forecast, Integer> deleteQuery = forecastDao.deleteBuilder();
            deleteQuery.where().eq(Forecast.CITY_ID, city.getId());
            return deleteQuery.delete();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns an instance of the DAO.
     */
    public Dao<City, Integer> getCityDao() {
        return cityDao;
    }

    /**
     * @return Returns an instance of the DAO.
     */
    public Dao<CityToWatch, Integer> getCityToWatchDao() {
        return cityToWatchDao;
    }

    /**
     * @return Returns an instance of the DAO.
     */
    public Dao<CurrentWeatherData, Integer> getCurrentWeatherDataDao() {
        return currentWeatherDataDao;
    }

    /**
     * @return Returns an instance of the DAO.
     */
    public Dao<Forecast, Integer> getForecastDao() {
        return forecastDao;
    }

}
