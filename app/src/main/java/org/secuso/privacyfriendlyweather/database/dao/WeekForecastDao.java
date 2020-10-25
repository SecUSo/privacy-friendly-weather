package org.secuso.privacyfriendlyweather.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.secuso.privacyfriendlyweather.database.data.WeekForecast;

import java.util.List;

/**
 * @author Noah Schlegel
 */
@Dao
public interface WeekForecastDao {
    @Query("SELECT * FROM WEEKFORECASTS")
    List<WeekForecast> getAll();

    @Query("DELETE FROM WEEKFORECASTS")
    void deleteAll();

    @Update
    void updateWeekForecast(WeekForecast forecast);

    @Insert
    void addWeekForecast(WeekForecast forecast);

    @Delete
    void deleteWeekForecast(WeekForecast forecast);

    @Query("DELETE FROM WEEKFORECASTS WHERE city_id = :cityId")
    void deleteWeekForecastsByCityId(int cityId);

    // TODO: long cutofftime = timestamp - 24 * 60 * 60 * 1000L;
    @Query("DELETE FROM WEEKFORECASTS WHERE city_id = :cityId AND forecastTime <= :timestamp")
    void deleteOldWeekForecastsByCityId(int cityId, long timestamp);

    @Query("SELECT * FROM WEEKFORECASTS WHERE city_id = :cityId")
    List<WeekForecast> getWeekForecastsByCityId(int cityId);


}
