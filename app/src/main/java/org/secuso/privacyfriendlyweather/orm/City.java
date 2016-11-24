package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * This plain old Java class is the database model for cities.
 */
@DatabaseTable(tableName = "cities")
public class City implements Serializable {

    /**
     * Constants
     */
    private static final String COLUMN_CITY_ID = "city_id";
    private static final String COLUMN_CITY_RANK = "city_rank";
    private static final String COLUMN_CITY_NAME = "city_name";
    private static final String COLUMN_COUNTRY_CODE = "country_code";
    private static final String COLUMN_POSTAL_CODE = "postal_code";
    private static final String UNKNOWN_POSTAL_CODE_VALUE = "-";

    /**
     * Member variables
     */
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = COLUMN_CITY_ID)
    private int cityId;

    @DatabaseField(columnName = COLUMN_CITY_RANK)
    private int cityRank;

    @DatabaseField(columnName = COLUMN_CITY_NAME)
    private String cityName;

    @DatabaseField(columnName = COLUMN_COUNTRY_CODE)
    private String countryCode;

    @DatabaseField(columnName = COLUMN_POSTAL_CODE)
    private String postalCode;

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
     * @param cityRank    Position of the city in the list.
     * @param countryCode The code of the country that the city belongs to.
     * @param postalCode  The postal code of the city. If it is unknown, pass
     *                    UNKNOWN_POSTAL_CODE_VALUE.
     */
    public City(int cityId, int cityRank, String cityName, String countryCode, String postalCode) {
        this.cityId = cityId;
        this.cityRank = cityRank;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
    }

    /**
     * @return Returns the unique ID of the record.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the ID of the city.
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @return Returns the ID of the city.
     */
    public int getCityRank() {
        return cityRank;
    }

    /**
     * @param cityId The ID of the city.
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    /**
     * @param cityRank The rank of the city in the list.
     */
    public void setCityRank(int cityRank) {
        this.cityRank = cityRank;
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
     * @return Returns the postal code that is associated with the record. If there is none,
     * UNKNOWN_POSTAL_CODE_VALUE will be returned.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode The postal code of the city / location. If it is unknown, pass
     *                   UNKNOWN_POSTAL_CODE_VALUE.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return Returns "[cityName], ([countryCode])" if no postal code is set, otherwise
     * "[cityName], [postalCode] ([countryCode])"
     */
    @Override
    public String toString() {
        if (postalCode.equals(UNKNOWN_POSTAL_CODE_VALUE)) {
            return String.format("%s (%s)", cityName, countryCode);
        } else {
            return String.format("%s, %s (%s)", cityName, postalCode, countryCode);
        }
    }

}
