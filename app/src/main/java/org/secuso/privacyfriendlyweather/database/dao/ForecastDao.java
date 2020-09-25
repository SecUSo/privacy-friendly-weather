package org.secuso.privacyfriendlyweather.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.secuso.privacyfriendlyweather.database.data.Forecast;

import java.util.List;

@Dao
public interface ForecastDao {
    @Query("SELECT * FROM FORECASTS")
    List<Forecast> getAll();

    @Delete
    void deleteAll();

    @Update
    void updateForecast(Forecast forecast);

    @Insert
    void addForecast(Forecast forecast);

    @Delete
    void deleteForecast(Forecast forecast);

    @Query("DELETE FROM FORECASTS WHERE city_id = :cityId")
    void deleteForecastsByCityId(int cityId);

    // TODO: long cutofftime = timestamp - 24 * 60 * 60 * 1000L;
    @Query("DELETE FROM FORECASTS WHERE city_id = :cityId AND forecast_for <= :timestamp")
    void deleteOldForecastsByCityId(int cityId, long timestamp);

    @Query("SELECT * FROM FORECASTS WHERE city_id = :cityId")
    List<Forecast> getForecastsByCityId(int cityId);


}
