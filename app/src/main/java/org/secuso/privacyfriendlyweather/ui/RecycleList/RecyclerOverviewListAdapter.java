package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;

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
    private Context context;
    private List<CityOverviewListItem> listItems;

    /**
     * Constructor.
     */
    public RecyclerOverviewListAdapter(Context context, List<CityOverviewListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    /**
     * Shows a Snackbar to undo the deletion of a CityOverviewListItem.
     *
     * @param cityToRestore        The CityToWatch record that was deleted but shall be restored now.
     * @param weatherDataToRestore The CurrentWeatherData record that was deleted but shall be
     *                             restored now.
     * @return Returns the Snackbar to show.
     */
    private Snackbar getUndoSnackbar(final CityToWatch cityToRestore, final CurrentWeatherData weatherDataToRestore) {
        final String MSG = context.getResources().getString(R.string.activity_main_snackbar_undo_info);
        final String BTN_TEXT = context.getResources().getString(R.string.activity_main_snackbar_undo_button);
        return Snackbar
                .make(
                        ((Activity) (context)).findViewById(R.id.main_content),
                        MSG,
                        Snackbar.LENGTH_LONG
                )
                .setAction(BTN_TEXT, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        try {
//                            // TODO Re-Insert
//
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                            // TODO: Handle the error case
//                        }
                    }
                });
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
//        try {
//            // Retrieve the records; they are re-inserted in case of undo
////            final int CWD_ID = listItems.get(position).getCurrentWeatherDataID();
////            final CurrentWeatherData weatherDataToDelete = dbHelper.getCurrentWeatherDataByID(CWD_ID);
////            final CityToWatch cityToWatchToDelete = dbHelper.getCityToWatchByCityId(weatherDataToDelete.getCity().getId());
////            // Remove the corresponding database entries
////            dbHelper.deleteCityToWatchRecordByCityID(weatherDataToDelete.getCity().getId());
////            dbHelper.deleteCurrentWeatherRecordByID(CWD_ID);
//
//            // Remove the item from the (visual) list
//            listItems.remove(position);
//            notifyItemRemoved(position);
//
//            // Show the implemented undo snackbar
////            getUndoSnackbar(cityToWatchToDelete, weatherDataToDelete).show();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * @see ItemTouchHelperAdapter#onItemMove(int, int)
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // For updating the database records
//        int fromCurrentWeatherDataID = listItems.get(fromPosition).getCurrentWeatherDataID();
//        int toCurrentWeatherDataID = listItems.get(toPosition).getCurrentWeatherDataID();
//        CityToWatch fromCityToWatch = null;
//        CityToWatch toCityToWatch = null;
//        try {
//            CurrentWeatherData fromCurrentWeatherData = dbHelper.getCurrentWeatherDataByID(fromCurrentWeatherDataID);
//            CurrentWeatherData toCurrentWeatherData = dbHelper.getCurrentWeatherDataByID(toCurrentWeatherDataID);
//            fromCityToWatch = dbHelper.getCityToWatchByCityId(fromCurrentWeatherData.getCity().getId());
//            toCityToWatch = dbHelper.getCityToWatchByCityId(toCurrentWeatherData.getCity().getId());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                if (fromCityToWatch != null && toCityToWatch != null) {
//                    dbHelper.swapRanksOfCitiesToWatch(fromCityToWatch, toCityToWatch);
//                }
//                Collections.swap(listItems, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                if (fromCityToWatch != null && toCityToWatch != null) {
//                    dbHelper.swapRanksOfCitiesToWatch(toCityToWatch, fromCityToWatch);
//                }
//                Collections.swap(listItems, i, i - 1);
//            }
//        }
//        notifyItemMoved(fromPosition, toPosition);
    }

}