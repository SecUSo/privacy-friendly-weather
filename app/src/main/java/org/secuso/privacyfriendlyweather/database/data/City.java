package org.secuso.privacyfriendlyweather.database.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Locale;

/**
 * Created by yonjuni on 04.01.17.
 * data object for city
 * <p>
 * Structure taken from the old orm package from previous versions of this app.
 */
@Entity(tableName = "CITIES", indices = {@Index(value = {"city_name", "cities_id"})})
public class City {

    private static final String UNKNOWN_POSTAL_CODE_VALUE = "-";

    @PrimaryKey @ColumnInfo(name = "cities_id") private int cityId;
    @ColumnInfo(name = "city_name") @NonNull private String cityName = "";
    @ColumnInfo(name = "country_code") @NonNull private String countryCode = "";
    @ColumnInfo(name = "longitude") private float longitude;
    @ColumnInfo(name = "latitude") private float latitude;

    public City() { }

    @Ignore public City(int cityId, @NonNull String cityName, @NonNull String countryCode, float lon, float lat) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.longitude = lon;
        this.latitude = lat;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public @NonNull String getCityName() {
        return cityName;
    }

    public void setCityName(@NonNull String cityName) {
        this.cityName = cityName;
    }

    public @NonNull String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@NonNull String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public @NonNull String toString() {
        return String.format(Locale.ENGLISH, "%s, %s (%f - %f)", cityName, countryCode, longitude, latitude);
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float lon) {
        this.longitude = lon;
    }
}
