package org.secuso.privacyfriendlyweather.database;

/**
 * This class is the database model for the cities to watch. 'Cities to watch' means the locations
 * for which a user would like to see the weather for. This includes those locations that will be
 * deleted after app close (non-persistent locations).
 */
public class CityToWatch {

    private int id;
    private int cityId;
    private String cityName;
    private String countryCode;
    private String postalCode;
    private int rank;

    public CityToWatch() {
    }

    public CityToWatch(int rank, String postalCode, String countryCode, int id, int cityId, String cityName) {
        this.rank = rank;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
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
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}