package org.secuso.privacyfriendlyweather.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;

import java.util.List;

/**
 * The adapter class for the overview list of cities and their current weather.
 */
public class CityOverviewListAdapter extends ArrayAdapter<CityOverviewListItem> {

    /**
     * Member variables.
     */
    private Context context;
    private List<CityOverviewListItem> listItems;

    /**
     * @see ArrayAdapter#ArrayAdapter(Context, int, int)
     */
    public CityOverviewListAdapter(Context context, int resource, List<CityOverviewListItem> items) {
        super(context, resource, items);
        this.context = context;
        this.listItems = items;
    }

    /**
     * @see ArrayAdapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.city_overview_list_item, null);
        }

        CityOverviewListItem item = getItem(position);

        // Set the text as well as the image
        TextView text = (TextView) convertView.findViewById(R.id.city_overview_list_item_text);
        ImageView image = (ImageView) convertView.findViewById(R.id.city_overview_list_item_img);
        text.setText(item.getText());
        image.setImageResource(item.getImageId());

        return convertView;
    }

}
