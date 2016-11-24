package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is the database model for the cities to watch. 'Cities to watch' means the locations
 * for which a user would like to see the weather for. This includes those locations that will be
 * deleted after app close (non-persistent locations).
 */
@DatabaseTable(tableName = "cities_to_watch")
public class CityToWatch {

    /**
     * Column names
     */
    static final String CITY_ID = "city_id";
    static final String COLUMN_STORE_PERSISTENT = "persistent";
    static final String COLUMN_RANK = "rank";

    /**
     * Database fields.
     */
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = CITY_ID, canBeNull = false, foreign = true, foreignAutoRefresh = true, columnDefinition = "integer references cities(id) on delete cascade")
    private City city;

    @DatabaseField(columnName = COLUMN_STORE_PERSISTENT)
    private boolean storePersistent;

    @DatabaseField(columnName = COLUMN_RANK)
    private long rank;

    /**
     * Constructor.
     */
    public CityToWatch() {
        rank = System.currentTimeMillis();
    }

    /**
     * Constructor.
     *
     * @param city            The ID of the record in the cities table that this record
     *                        corresponds to.
     * @param storePersistent Set this value to true if the record is to be stored permanently,
     *                        i.e. not deleted after closing the app.
     */
    public CityToWatch(City city, boolean storePersistent) {
        this.city = city;
        this.storePersistent = storePersistent;
        rank = System.currentTimeMillis();
    }

    /**
     * @return Returns the ID of a record.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the ID (primary key value) of the City that this record corresponds to.
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city The ID of the record in the cities table that this record corresponds to.
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return Returns true if the record is stored permanently, i. e. not deleted after closing
     * the app.
     */
    public boolean isStorePersistent() {
        return storePersistent;
    }

    /**
     * @param storePersistent Set this value to true if the record is to be stored permanently,
     *                        i.e. not deleted after closing the app.
     */
    public void setStorePersistent(boolean storePersistent) {
        this.storePersistent = storePersistent;
    }

    /**
     * @return Returns the rank of this object, i.e. the position when ordering items.
     */
    public long getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set. This value is used to sort objects e.g. for lists.
     */
    public void setRank(long rank) {
        this.rank = rank;
    }
}
