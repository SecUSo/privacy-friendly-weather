package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the adapter for the RecyclerList that is to be used for the overview of added locations.
 * For the most part, it has been taken from
 * https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.hmhbe8sku
 * as of 2016-08-03
 */
public class RecyclerOverviewListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {

    /**
     * Member variables
     */
    private DatabaseHelper dbHelper;
    private static List<CityOverviewListItem> listItems;

    /**
     * Constructor.
     */
    public RecyclerOverviewListAdapter(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        // As the list is static, initialize it only once
        if (listItems == null) {
            listItems = new ArrayList<>();
        }
    }

    /**
     * @return Returns the items of the list.
     */
    public static List<CityOverviewListItem> getListItems() {
        return listItems;
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     * Returns the template for a list item.
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_overview_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     * Sets the content of items.
     */
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.getTvInformation().setText(listItems.get(position).getText());
        holder.getIvIcon().setImageResource(listItems.get(position).getImageId());
    }

    /**
     * @see RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    /**
     * @see ItemTouchHelperAdapter#onItemDismiss(int)
     * Removes an item from the list.
     */
    @Override
    public void onItemDismiss(int position) {
        try {
            // Remove the corresponding database entry from the CityToWatch and CurrentWeatherData
            final int CWD_ID = listItems.get(position).getCurrentWeatherDataID();
            CurrentWeatherData weatherData = dbHelper.getCurrentWeatherDataByID(CWD_ID);
            dbHelper.deleteCurrentWeatherRecordByID(CWD_ID);
            dbHelper.deleteCityToWatchRecordByCityID(weatherData.getCity().getId());

            // Remove the item from the (visual) list
            listItems.remove(position);
            notifyItemRemoved(position);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}