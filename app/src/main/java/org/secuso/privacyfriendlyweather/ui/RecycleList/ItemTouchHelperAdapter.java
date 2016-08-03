package org.secuso.privacyfriendlyweather.ui.RecycleList;

/**
 * This interface defines the functionality that can be bound to touch events.
 * For the most part it has been taken from
 * https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.hmhbe8sku
 * as of 2016-08-03
 */
public interface ItemTouchHelperAdapter {

    /**
     * This method removes an item from an adapter at the specified positin.
     *
     * @param position The position of the item to remove.
     */
    void onItemDismiss(int position);

}
