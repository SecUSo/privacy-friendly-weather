package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.orm.Forecast;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private static final String TAG = "Forecast_Adapter";

    private String[] mDataSet;
    private int[] mDataSetTypes;

    //TODO Update Array to list
    private List<CurrentWeatherData> currentWeatherDataList;

    public static final int OVERVIEW = 0;
    public static final int DETAILS = 1;
    public static final int WEEK = 2;
    public static final int DAY = 3;
    public static final int SUN = 4;

    public ForecastAdapter(List<CurrentWeatherData> currentWeatherDataList, int[] dataSetTypes){
        this.currentWeatherDataList = currentWeatherDataList;
        mDataSetTypes = dataSetTypes;
    }


    public ForecastAdapter(String[] dataSet, int[] dataSetTypes) {
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class OverViewHolder extends ViewHolder {
        TextView temperature;
        ImageView weather;

        public OverViewHolder(View v) {
            super(v);
            this.temperature = (TextView) v.findViewById(R.id.activity_city_weather_temperature);
            this.weather = (ImageView) v.findViewById(R.id.activity_city_weather_image_view);
        }
    }

    public class DetailViewHolder extends ViewHolder {
        TextView humidity;
        TextView pressure;
        TextView windspeed;

        public DetailViewHolder(View v) {
            super(v);
            this.humidity = (TextView) v.findViewById(R.id.activity_city_weather_tv_humidity_value);
            this.pressure = (TextView) v.findViewById(R.id.activity_city_weather_tv_pressure_value);
            this.windspeed = (TextView) v.findViewById(R.id.activity_city_weather_tv_wind_speed_value);
        }
    }

    public class WeekViewHolder extends ViewHolder {
        //TODO Add data to the weekview
        public WeekViewHolder(View v) {
            super(v);
        }
    }

    public class DayViewHolder extends ViewHolder {
        //TODO Add data to the dayview
        public DayViewHolder(View v) {
            super(v);
        }
    }

    public class SunViewHolder extends ViewHolder {
        TextView sunrise;
        TextView sunset;

        public SunViewHolder(View v) {
            super(v);
            this.sunrise = (TextView) v.findViewById(R.id.activity_city_weather_tv_sunrise_value);
            this.sunset = (TextView) v.findViewById(R.id.activity_city_weather_tv_sunset_value);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == OVERVIEW) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_overview, viewGroup, false);

            return new OverViewHolder(v);
        } else if (viewType == DETAILS) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_details, viewGroup, false);
            return new DetailViewHolder(v);
        } else if (viewType == WEEK) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_week, viewGroup, false);
            return new WeekViewHolder(v);
        } else if (viewType == DAY) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_day, viewGroup, false);
            return new DayViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_sun, viewGroup, false);
            return new SunViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == OVERVIEW) {
            OverViewHolder holder = (OverViewHolder) viewHolder;
            //TODO Set imageview
            holder.temperature.setText(mDataSet[position]);
        } else if (viewHolder.getItemViewType() == DETAILS) {
            DetailViewHolder holder = (DetailViewHolder) viewHolder;
            //TODO Set data
        } else if (viewHolder.getItemViewType() == WEEK) {
            WeekViewHolder holder = (WeekViewHolder) viewHolder;
            //TODO Set data
        } else if (viewHolder.getItemViewType() == DAY) {
            DayViewHolder holder = (DayViewHolder) viewHolder;
            //TODO Set data
        } else {
            SunViewHolder holder = (SunViewHolder) viewHolder;
            //TODO Set data
        }
    }

        @Override
        public int getItemCount () {
            return mDataSet.length;
        }

        @Override
        public int getItemViewType ( int position){
            return mDataSetTypes[position];
        }
    }