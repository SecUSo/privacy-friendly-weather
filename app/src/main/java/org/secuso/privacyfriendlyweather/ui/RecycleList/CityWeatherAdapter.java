package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.preference.PreferenceManager;
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
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> {
    private static final String TAG = "Forecast_Adapter";

    private int[] dataSetTypes;
    private List<Forecast> forecastList;
    private List<Forecast> courseDayList;

    private Context context;

    private CurrentWeatherData currentWeatherDataList;

    public static final int OVERVIEW = 0;
    public static final int DETAILS = 1;
    public static final int WEEK = 2;
    public static final int DAY = 3;
    public static final int SUN = 4;
    public static final int ERROR = 5;

    public CityWeatherAdapter(CurrentWeatherData currentWeatherDataList, int[] dataSetTypes, Context context) {
        this.currentWeatherDataList = currentWeatherDataList;
        this.dataSetTypes = dataSetTypes;
        this.context = context;

        PFASQLiteHelper database = PFASQLiteHelper.getInstance(context.getApplicationContext());

        List<Forecast> forecasts = database.getForecastsByCityId(currentWeatherDataList.getCity_id());

        updateForecastData(forecasts);
    }

    public void updateForecastData(List<Forecast> forecasts) {
        forecastList = new ArrayList<Forecast>();
        courseDayList = new ArrayList<Forecast>();

        // TODO: filter them accordingly and calculate what should be displayed .. (like average all the 3h forecasts for the week list)
        Date now = new Date();
        long currentTime = currentWeatherDataList.getTimestamp();

        for(Forecast f : forecasts) {
            Date time = f.getForecastTime();

            // only add Forecasts that are in the future
            if(time.after(now)) {

                // course of day list should show entries until the same time the next day is reached
                // since we force our forecasts to be in the future and they are ordered.. we can assume
                // the next entry to be to the full 3h mark after this time ..
                // if we now add a total of 8 entries if should sum up to 24 hours
                if(courseDayList.size() < 8) {
                    courseDayList.add(f);
                }

                Calendar c = new GregorianCalendar();
                c.setTime(f.getForecastTime());
                if(c.get(Calendar.HOUR_OF_DAY) == 12) {
                    forecastList.add(f);
                }
            }
        }

        notifyDataSetChanged();
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

    public class ErrorViewHolder extends ViewHolder {
        public ErrorViewHolder(View v) {
            super(v);
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

        } else if (viewType == SUN){

            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_sun, viewGroup, false);
            return new SunViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_error, viewGroup, false);
            return new ErrorViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == OVERVIEW) {
            OverViewHolder holder = (OverViewHolder) viewHolder;
            setImage(currentWeatherDataList.getWeatherID(), holder.weather);

            AppPreferencesManager prefManager =
                    new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
            DecimalFormat decimalFormat = new DecimalFormat("#.0");
            // Format the values to display
            String heading = String.format(
                    "%s%s",
                    decimalFormat.format(prefManager.convertTemperatureFromCelsius(currentWeatherDataList.getTemperatureCurrent())),
                    prefManager.getWeatherUnit()
            );

            holder.temperature.setText(heading);

        } else if (viewHolder.getItemViewType() == DETAILS) {

            DetailViewHolder holder = (DetailViewHolder) viewHolder;
            holder.humidity.setText(String.format("%s %%", currentWeatherDataList.getHumidity()));
            holder.pressure.setText(String.format("%s hPa", Math.round(currentWeatherDataList.getPressure())));
            holder.windspeed.setText(String.format("%s m/s", currentWeatherDataList.getWindSpeed()));

        } else if (viewHolder.getItemViewType() == WEEK) {

            WeekViewHolder holder = (WeekViewHolder) viewHolder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            WeekWeatherAdapter adapter = new WeekWeatherAdapter(forecastList, context);
            holder.recyclerView.setAdapter(adapter);

        } else if (viewHolder.getItemViewType() == DAY) {

            DayViewHolder holder = (DayViewHolder) viewHolder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            CourseOfDayAdapter adapter = new CourseOfDayAdapter(courseDayList, context);
            holder.recyclerView.setAdapter(adapter);

        } else if (viewHolder.getItemViewType() == SUN) {
            SunViewHolder holder = (SunViewHolder) viewHolder;

            //TODO Is this local time? No it's UTC change to it local time...
            GregorianCalendar calendar = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setCalendar(calendar);

            calendar.setTimeInMillis(currentWeatherDataList.getTimeSunrise()*1000);
            holder.sunrise.setText(dateFormat.format(calendar.getTime()));

            calendar.setTimeInMillis(currentWeatherDataList.getTimeSunset()*1000);
            holder.sunset.setText(dateFormat.format(calendar.getTime()));
        }
        //No update for error needed
    }

    public void setImage(int value, ImageView imageView) {
        imageView.setImageResource(UiResourceProvider.getImageResourceForWeatherCategory(value));

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSetTypes[position];
    }
}