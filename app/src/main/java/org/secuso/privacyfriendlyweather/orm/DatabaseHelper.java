package org.secuso.privacyfriendlyweather.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.files.FileReader;
import org.secuso.privacyfriendlyweather.pojos.City;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ptrck on 28/07/16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * Database information
     */
    private static final String DATABASE_NAME = "privacyfriendlyweather";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    /**
     * The data access object used to interact with the Sqlite database to do C.R.U.D operations.
     */
    private Dao<City, Integer> cityDao;

    /**
     * @see OrmLiteSqliteOpenHelper
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
     * @return Returns an instance of the DAO.
     */
    public Dao<City, Integer> getCityDao() {
        if (cityDao == null) {
            try {
                cityDao = getDao(City.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cityDao;
    }

}
