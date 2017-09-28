package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;

/**
 * This class holds instances of items that are to be displayed in the list.
 * The idea of this class has been taken from
 * https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.hmhbe8sku
 * as of 2016-08-03. Parts of the code were copied from that source.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    /**
     * Member variables
     */
    private TextView tvInformation;
    private ImageView isDefault;

    /**
     * Constructor.
     *
     * @param itemView The view that contains the fields that are to be set for each list item.
     */
    public ItemViewHolder(View itemView) {
        super(itemView);
        tvInformation = (TextView) itemView.findViewById(R.id.city_overview_list_item_text);
        isDefault = (ImageView) itemView.findViewById(R.id.locationDefault);
    }

    /**
     * @return Returns the TextView of the item.
     */
    public TextView getTvInformation() {
        return tvInformation;
    }

    /**
     * @return Returns the TextView of the item.
     */
    public ImageView getIsDefault() {
        return isDefault;
    }


}