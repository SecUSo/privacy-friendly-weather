package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

//**
// * Created by yonjuni on 02.01.17.
// * Adapter for the horizontal listView for course of the day.
// */import java.util.List;

public class CourseOfDayAdapter extends RecyclerView.Adapter<CourseOfDayAdapter.CourseOfDayViewHolder> {

    //TODO Add datatype to list
    private List<Forecast> courseOfDayList;
    private Context context;
    private TextView recyclerViewHeader;
    private RecyclerView recyclerView;

    CourseOfDayAdapter(List<Forecast> courseOfDayList, Context context, TextView recyclerViewHeader, RecyclerView recyclerView) {
        this.context = context;
        this.courseOfDayList = courseOfDayList;
        this.recyclerViewHeader = recyclerViewHeader;
        this.recyclerView = recyclerView;


    }

    @NotNull
    @Override
    public CourseOfDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_course_of_day, parent, false);
        return new CourseOfDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull CourseOfDayViewHolder holder, int position) {
        AppDatabase dbHelper = AppDatabase.getInstance(context);
        CurrentWeatherData currentWeather = dbHelper.currentWeatherDao().getCurrentWeatherByCityId(courseOfDayList.get(position).getCity_id());

        Calendar forecastTime = Calendar.getInstance();
        forecastTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        forecastTime.setTimeInMillis(courseOfDayList.get(position).getLocalForecastTime(context));
        //Log.d("devtag","localForecastTime: "+forecastTime.getTime());

        boolean isDay = true;
        if (currentWeather != null) {
            Calendar sunSetTime = Calendar.getInstance();
            sunSetTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            sunSetTime.setTimeInMillis(currentWeather.getTimeSunset() * 1000 + currentWeather.getTimeZoneSeconds() * 1000);
            sunSetTime.set(Calendar.DAY_OF_YEAR, forecastTime.get(Calendar.DAY_OF_YEAR));


            Calendar sunRiseTime = Calendar.getInstance();
            sunRiseTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            sunRiseTime.setTimeInMillis(currentWeather.getTimeSunrise() * 1000 + currentWeather.getTimeZoneSeconds() * 1000);
            sunRiseTime.set(Calendar.DAY_OF_YEAR, forecastTime.get(Calendar.DAY_OF_YEAR));

            isDay = forecastTime.after(sunRiseTime) && forecastTime.before(sunSetTime);
        }

        int day = forecastTime.get(Calendar.DAY_OF_WEEK);

        day = StringFormatUtils.getDay(day);


        //TODO: Remove this part and only use updateRecyclerViewHeader to show day in header? Or keep both and show day also in first entry per day?
        // In first entry per weekday show weekday instead of time
        if (courseOfDayList.size() > 1) {  //if there are at least 2 entries check time difference between first 2 entries
            if (courseOfDayList.get(1).getForecastTime() - courseOfDayList.get(0).getForecastTime() == 3600000) { // 1h difference = 2day/1h forecast
                if (forecastTime.get(Calendar.HOUR_OF_DAY) >= 0 && forecastTime.get(Calendar.HOUR_OF_DAY) < 1) { //show weekday for entry in first hour
                    // In first entry per weekday show weekday instead of time
                    holder.time.setText(day);
                } else {
                    //Time has to be the local time in the city!
                    holder.time.setText(StringFormatUtils.formatTimeWithoutZone(courseOfDayList.get(position).getLocalForecastTime(context)));
                }
            } else if (courseOfDayList.get(1).getForecastTime() - courseOfDayList.get(0).getForecastTime() == 10800000) { // 3h difference = 5day/3h forecast
                if (forecastTime.get(Calendar.HOUR_OF_DAY) >= 0 && forecastTime.get(Calendar.HOUR_OF_DAY) < 3) { //show weekday for entry in first 3 hours
                    // In first entry per weekday show weekday instead of time
                    holder.time.setText(day);
                } else {
                    //Time has to be the local time in the city!
                    holder.time.setText(StringFormatUtils.formatTimeWithoutZone(courseOfDayList.get(position).getLocalForecastTime(context)));
                }
            }
        } else {  // if there is just 1 entry left show time
            holder.time.setText(StringFormatUtils.formatTimeWithoutZone(courseOfDayList.get(position).getLocalForecastTime(context)));
        }

        updateRecyclerViewHeader();  //update header according to date in first visible item on the left
        AppPreferencesManager prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(this.context));

        setIcon(courseOfDayList.get(position).getWeatherID(), holder.weather, isDay);
        holder.humidity.setText(StringFormatUtils.formatInt(courseOfDayList.get(position).getHumidity(), "%rh"));
        holder.temperature.setText(StringFormatUtils.formatTemperature(context, courseOfDayList.get(position).getTemperature()));
        holder.wind_speed.setText(prefManager.convertToCurrentSpeedUnit(courseOfDayList.get(position).getWindSpeed()));
        holder.wind_direction.setText(StringFormatUtils.formatWindDir(context, courseOfDayList.get(position).getWindDirection()));

        if (prefManager.is3hourForecastSet()) {
            holder.rain_probability.setVisibility(View.GONE);
        } else {
            holder.rain_probability.setText(StringFormatUtils.formatInt(courseOfDayList.get(position).getRainProbability(), "%\uD83D\uDCA7"));
            holder.rain_probability.setVisibility(View.VISIBLE);
        }

        if (courseOfDayList.get(position).getRainValue() == 0)
            holder.precipitation.setText("-");
        else
            holder.precipitation.setText(StringFormatUtils.formatDecimal(courseOfDayList.get(position).getRainValue(), "mm"));
    }

    //update header according to date in first visible item on the left of recyclerview
    private void updateRecyclerViewHeader() {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        LinearLayoutManager llm = (LinearLayoutManager) manager;
        int visiblePosition = llm.findFirstVisibleItemPosition();
        if (visiblePosition > -1) {
            Calendar HeaderTime = Calendar.getInstance();
            HeaderTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            HeaderTime.setTimeInMillis(courseOfDayList.get(visiblePosition).getLocalForecastTime(context));
            int headerday = HeaderTime.get(Calendar.DAY_OF_WEEK);
            headerday = StringFormatUtils.getDay(headerday);
            recyclerViewHeader.setText(context.getResources().getString(R.string.card_day_heading) + " " + context.getResources().getString(headerday));
        }
    }


    @Override
    public int getItemCount() {
        return courseOfDayList.size();
    }

    class CourseOfDayViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView weather;
        TextView temperature;
        TextView humidity;
        TextView precipitation;
        TextView rain_probability;
        TextView wind_speed;
        TextView wind_direction;

        CourseOfDayViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.course_of_day_time);
            weather = itemView.findViewById(R.id.course_of_day_weather);
            temperature = itemView.findViewById(R.id.course_of_day_temperature);
            humidity = itemView.findViewById(R.id.course_of_day_humidity);
            precipitation = itemView.findViewById(R.id.course_of_day_precipitation);
            rain_probability = itemView.findViewById(R.id.course_of_day_rain_probability);
            wind_speed = itemView.findViewById(R.id.course_of_day_wind_speed);
            wind_direction = itemView.findViewById(R.id.course_of_day_wind_direction);

        }
    }

    public void setIcon(int value, ImageView imageView, boolean isDay) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value, isDay));
        imageView.setBackgroundResource(R.drawable.backgroundcircle);
    }
}

