package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This plain old Java class is the database model for cities.
 */
@DatabaseTable(tableName = "cities")
public class City {

    /**
     * Constants
     */
    public static final String COLUMN_CITY_ID = "city_id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_COUNTRY_CODE = "country_code";

    /**
     * Member variables
     */
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = COLUMN_CITY_ID)
    private int cityId;

    @DatabaseField(columnName = COLUMN_CITY_NAME)
    private String cityName;

    @DatabaseField(columnName = COLUMN_COUNTRY_CODE)
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
     */
    public City(int cityId, String cityName, String countryCode) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryCode = countryCode;
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
        return String.format("%s (%s)", cityName, countryCode);
    }

}
