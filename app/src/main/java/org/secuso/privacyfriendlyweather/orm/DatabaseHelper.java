package org.secuso.privacyfriendlyweather.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.files.FileReader;
import org.secuso.privacyfriendlyweather.pojos.City;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * Database information
     */
    private static final String DATABASE_NAME = "privacyfriendlyweather";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    /**
     * The data access object used to interact with the SQLite database to do C.R.U.D operations.
     */
    private Dao<City, Integer> cityDao;

    /**
     * @see OrmLiteSqliteOpenHelper
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (cityDao == null) {
            try {
                cityDao = getDao(City.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillCitiesTable() {
        Log.d("debug_info", "Fill DB");
        InputStream inputStream = context.getResources().openRawResource(R.raw.city_list);
        FileReader fileReader = new FileReader();
        try {
            List<City> cities = fileReader.readCitiesFromFile(inputStream);
            inputStream.close();
            for (City city : cities) {
                cityDao.create(city);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see OrmLiteSqliteOpenHelper#onCreate(SQLiteDatabase, ConnectionSource)
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, City.class);
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
            onCreate(database, connectionSource);
            fillCitiesTable();
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
            queryBuilder.where().like("city_name", String.format("%s%%", cityFirstLetters));
            queryBuilder.orderBy("city_name", true);
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
     * @return Returns an instance of the DAO.
     */
    public Dao<City, Integer> getCityDao() {
        return cityDao;
    }

}
