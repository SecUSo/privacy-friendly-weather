package org.secuso.privacyfriendlyweather.ui.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.City;

import java.util.ArrayList;
import java.util.Collection;

public class CitySelectAdapter extends ArrayAdapter<City> {

    private ArrayList<City> cities;
    private int resourceID;

    public CitySelectAdapter(@NonNull Context context, int resource, ArrayList<City> cities) {
        super(context, resource);
        this.cities =cities;
        this.resourceID = resource;
    }
/*
    @Nullable
    @Override
    public City getItem(int position) {
        return cities.get(position);
    }

 */

    @Override
    public Filter getFilter(){
        return ownFilter;
    }

    private Filter ownFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
           /* if (charSequence != null){
                String content= charSequence.toString();
                if (content.contains("(")){
                    int split = content.indexOf("(");
                    name = content.substring(0,split).replaceAll("\\s+$", "");
                    country = content.substring(split+1, Math.min(split+3,content.length()));
                } else {
                    name = content.replaceAll("\\s+$", "");
                    country="*";
                }
                ArrayList<City> suggest = new ArrayList<City>();
                for(City city : cities){
                    if(city.getCityName().contains(charSequence.subString(0,charSequence)))
                }
                }
          */
           FilterResults results = new FilterResults();
           results.values=cities;
           results.count=cities.size();
           return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(filterResults != null && filterResults.count >0){

                //addAll((ArrayList<City>)filterResults.values);
            }
            notifyDataSetChanged();


        }
    };

    @Override
    public void add(@Nullable City object) {
        Log.d("cityfind","add, length: "+cities.size());

        super.add(object);
        cities.add((City)object);
        Log.d("cityfind","after add, length: "+cities.size());


    }

    @Override
    public void addAll(@NonNull Collection<? extends City> collection) {
        Log.d("cityfind","addall, length: "+cities.size());
        super.addAll(collection);
        cities.addAll((ArrayList<City>)collection);
        Log.d("cityfind","after addall, length: "+cities.size());

    }

    @Override
    public void clear() {
        Log.d("cityfind","cleared, length: "+cities.size());
        super.clear();
        cities.clear();
    }

}
