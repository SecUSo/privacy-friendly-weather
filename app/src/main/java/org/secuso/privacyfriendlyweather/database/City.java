package org.secuso.privacyfriendlyweather.database;

/**
 * Created by yonjuni on 04.01.17.
 * data object for city
 *
 * Structure taken from the old orm package from previous versions of this app.
 */

public class City {

    private static final String UNKNOWN_POSTAL_CODE_VALUE = "-";

    private int cityId;
    private String cityName;
    private String countryCode;
    private String postalCode;

    public City() {
    }

    public City(int cityId, String cityName, String countryCode, String postalCode) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
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

        if (postalCode == null) {
            return UNKNOWN_POSTAL_CODE_VALUE;
        } else {
             return postalCode;
        }

    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        if (postalCode.equals(UNKNOWN_POSTAL_CODE_VALUE)) {
            return String.format("%s (%s)", cityName, countryCode);
        } else {
            return String.format("%s, %s (%s)", cityName, postalCode, countryCode);
        }
    }

}
