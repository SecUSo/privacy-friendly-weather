package org.secuso.privacyfriendlyweather.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.secuso.privacyfriendlyweather.database.data.CityToWatch;

import java.util.List;

/**
 * @author Christopher Beckmann
 */
@Dao
public interface CityToWatchDao {
    @Query("SELECT * FROM CITIES_TO_WATCH")
    List<CityToWatch> getAll();

    @Query("SELECT * FROM CITIES_TO_WATCH WHERE cities_to_watch_id IS :id")
    CityToWatch getCityToWatchById(int id);

    @Insert
    long addCityToWatch(CityToWatch cityToWatch);

    @Update
    void updateCityToWatch(CityToWatch cityToWatch);

    @Delete
    void deleteCityToWatch(CityToWatch cityToWatch);

    @Query("SELECT max(rank) FROM CITIES_TO_WATCH")
    int getMaxRank();

    @Query("SELECT count(*) FROM CITIES_TO_WATCH")
    int count();

    @Query("SELECT EXISTS(SELECT * FROM CITIES_TO_WATCH WHERE city_id IS :cityId)")
    boolean isCityWatched(int cityId);
}
