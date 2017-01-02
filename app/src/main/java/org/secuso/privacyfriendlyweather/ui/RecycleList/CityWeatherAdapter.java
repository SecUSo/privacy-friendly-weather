package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;

import java.util.ArrayList;
import java.util.List;

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> {
    private static final String TAG = "Forecast_Adapter";

    private int[] dataSetTypes;
    List<Forecast> forecastList;
    List<Forecast> courseDayList;

    Context context;

    private CurrentWeatherData currentWeatherDataList;

    public static final int OVERVIEW = 0;
    public static final int DETAILS = 1;
    public static final int WEEK = 2;
    public static final int DAY = 3;
    public static final int SUN = 4;

    public CityWeatherAdapter(CurrentWeatherData currentWeatherDataList, int[] dataSetTypes, Context context){
        this.currentWeatherDataList = currentWeatherDataList;
        this.dataSetTypes = dataSetTypes;
        this.context = context;
        forecastList = new ArrayList<Forecast>();
        courseDayList = new ArrayList<Forecast>();
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
        RecyclerView recyclerView;
        public WeekViewHolder(View v) {
            super(v);
            recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_week);
            recyclerView.setHasFixedSize(true);
        }
    }

    public class DayViewHolder extends ViewHolder {
        RecyclerView recyclerView;
        public DayViewHolder(View v) {
            super(v);
            recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_course_day);
            recyclerView.setHasFixedSize(true);
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
            holder.temperature.setText(Float.toString(currentWeatherDataList.getTemperatureCurrent()));
        } else if (viewHolder.getItemViewType() == DETAILS) {
            DetailViewHolder holder = (DetailViewHolder) viewHolder;
            holder.humidity.setText(Float.toString(currentWeatherDataList.getHumidity()));
            holder.pressure.setText(Float.toString(currentWeatherDataList.getPressure()));
            holder.windspeed.setText(Float.toString(currentWeatherDataList.getWindSpeed()));
        } else if (viewHolder.getItemViewType() == WEEK) {
            WeekViewHolder holder = (WeekViewHolder) viewHolder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            WeekWeatherAdapter adapter = new WeekWeatherAdapter(forecastList);
            holder.recyclerView.setAdapter(adapter);
        } else if (viewHolder.getItemViewType() == DAY) {
            DayViewHolder holder = (DayViewHolder) viewHolder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            CourseOfDayAdapter adapter = new CourseOfDayAdapter(courseDayList);
            holder.recyclerView.setAdapter(adapter);

        } else {
            SunViewHolder holder = (SunViewHolder) viewHolder;
            holder.sunrise.setText(Float.toString(currentWeatherDataList.getTimeSunrise()));
            holder.sunset.setText(Float.toString(currentWeatherDataList.getTimeSunset()));
        }
    }

    @Override
    public int getItemCount () {
        return 5;
    }

    @Override
    public int getItemViewType ( int position){
        return  dataSetTypes[position];
    }
}