package org.secuso.privacyfriendlyweather.database.data;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class is the database model for the cities to watch. 'Cities to watch' means the locations
 * for which a user would like to see the weather for. This includes those locations that will be
 * deleted after app close (non-persistent locations).
 */
@Entity(tableName = "CITIES_TO_WATCH", foreignKeys = {
        @ForeignKey(entity = City.class,
                parentColumns = {"cities_id"},
                childColumns = {"city_id"},
                onDelete = ForeignKey.CASCADE)})
public class CityToWatch {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "cities_to_watch_id") private int id = 0;
    @ColumnInfo(name = "city_id") private int cityId;
    @ColumnInfo(name = "rank") private int rank;
    @Embedded private City city;

    public CityToWatch() {
    }

    @Ignore public CityToWatch(int rank, String countryCode, int id, int cityId, String cityName) {
        this.rank = rank;
        this.id = id;
        this.cityId = cityId;
        this.city = new City();
        this.city.setCityName(cityName);
        this.city.setCityId(cityId);
        this.city.setCountryCode(countryCode);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return city.getCityName();
    }

    public void setCityName(String cityName) {
        this.city.setCityName(cityName);
    }

    public String getCountryCode() {
        return city.getCountryCode();
    }

    public void setCountryCode(String countryCode) {
        this.city.setCountryCode(countryCode);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}