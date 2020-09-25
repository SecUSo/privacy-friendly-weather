package org.secuso.privacyfriendlyweather.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.secuso.privacyfriendlyweather.database.data.City;

import java.util.List;

/**
 * @author Christopher Beckmann
 */
@Dao
public interface CityDao {
    @Insert
    void insertAll(List<City> cities);

    @Insert
    void insert(City city);

    @Delete
    void delete(City city);

    @Query("SELECT * FROM CITIES WHERE city_name LIKE '%' || :cityNameLetters || '%' ORDER BY city_name LIMIT :dropdownListLimit")
    List<City> getCitiesWhereNameLike(String cityNameLetters, int dropdownListLimit);

    @Query("SELECT * FROM CITIES WHERE cities_id = :id")
    City getCityById(int id);

    @Query("SELECT count(*) FROM CITIES")
    int count();
}
