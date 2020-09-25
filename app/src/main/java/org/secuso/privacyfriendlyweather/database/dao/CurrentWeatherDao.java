package org.secuso.privacyfriendlyweather.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;

import java.util.List;

/**
 * @author Christopher Beckmann
 */
@Dao
public interface CurrentWeatherDao {

    @Query("SELECT * FROM CURRENT_WEATHER")
    List<CurrentWeatherData> getAll();

    @Update
    void updateCurrentWeather(CurrentWeatherData currentWeatherData);

    @Insert
    void addCurrentWeather(CurrentWeatherData currentWeatherData);

    @Query("SELECT * FROM CURRENT_WEATHER WHERE city_id = :cityId")
    CurrentWeatherData getCurrentWeatherByCityId(int cityId);

    @Query("DELETE FROM CURRENT_WEATHER WHERE city_id = :cityId")
    void deleteCurrentWeatherByCityId(int cityId);

}
