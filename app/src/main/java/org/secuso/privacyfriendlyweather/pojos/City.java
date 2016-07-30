package org.secuso.privacyfriendlyweather.pojos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This plain old Java class is the database model for cities.
 */
@DatabaseTable(tableName = "cities")
public class City {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "city_id")
    private int cityId;

    @DatabaseField(columnName = "city_name")
    private String cityName;

    @DatabaseField(columnName = "latitude")
    private double lat;

    @DatabaseField(columnName = "longitude")
    private double lon;

    @DatabaseField(columnName = "country_code")
    private String countryCode;

    /**
     * Constructor.
     */
    public City() {
    }

    /**
     * Constructor.
     *
     * @param cityId      The ID of the city.
     * @param cityName    The name of the city.
     * @param countryCode The code of the country that the city belongs to.
     * @param lat         Latitude.
     * @param lon         Longitude.
     */
    public City(int cityId, String cityName, String countryCode, double lat, double lon) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return Returns the ID of the city.
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @param cityId The ID of the city.
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    /**
     * @return Returns the name of the city.
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * @param cityName The name of the city to set.
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return Returns the latitude.
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat The latitude of the city to set.
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return Returns the longitude.
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon The longitude of the city to set.
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * @return Returns the country code that the city belongs to.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode The country code that the city belongs to.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return Returns "[cityName], [countryCode]".
     */
    @Override
    public String toString() {
        return String.format("%s, %s", cityName, countryCode);
    }
}
