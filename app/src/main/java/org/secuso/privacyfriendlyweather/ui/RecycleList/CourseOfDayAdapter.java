package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
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

    CourseOfDayAdapter(List<Forecast> courseOfDayList, Context context) {
        this.context = context;
        this.courseOfDayList = courseOfDayList;

        /*if(courseOfDayList.size() == 0) {
            Forecast forecast = new Forecast(1, 1, System.currentTimeMillis(), null, 20, 10, 90, 1001);
            Forecast forecast2 = new Forecast(1, 1, System.currentTimeMillis(), null, 10, 26, 86, 1001);
            Forecast forecast3 = new Forecast(1, 1, System.currentTimeMillis(), null, 30, 3, 90, 1001);
            Forecast forecast4 = new Forecast(1, 1, System.currentTimeMillis(), null, 40, 33, 86, 1001);
            Forecast forecast5 = new Forecast(1, 1, System.currentTimeMillis(), null, 50, 43, 90, 1001);
            Forecast forecast6 = new Forecast(1, 1, System.currentTimeMillis(), null, 60, 1, 86, 1001);
            Forecast forecast7 = new Forecast(1, 1, System.currentTimeMillis(), null, 70, -6, 90, 1001);
            Forecast forecast8 = new Forecast(1, 1, System.currentTimeMillis(), null, 80, -10, 86, 1001);
            courseOfDayList.add(forecast);
            courseOfDayList.add(forecast2);
            courseOfDayList.add(forecast3);
            courseOfDayList.add(forecast4);
            courseOfDayList.add(forecast5);
            courseOfDayList.add(forecast6);
            courseOfDayList.add(forecast7);
            courseOfDayList.add(forecast8);
        }*/

    }

    @Override
    public CourseOfDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_course_of_day, parent, false);
        return new CourseOfDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseOfDayViewHolder holder, int position) {
        AppDatabase dbHelper = AppDatabase.getInstance(context);
        CurrentWeatherData currentWeather = dbHelper.currentWeatherDao().getCurrentWeatherByCityId(courseOfDayList.get(position).getCity_id());

        // Show day icons between 4am and 8pm.
        // Would be better to use actual sunset and sunrise time, but did not know how to do this, These are not available in forecast database.
        Calendar forecastTime = Calendar.getInstance();
        forecastTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        forecastTime.setTimeInMillis(courseOfDayList.get(position).getLocalForecastTime(context));
        //Log.d("devtag","localForecastTime: "+forecastTime.getTime());

        Calendar sunSetTime = Calendar.getInstance();
        sunSetTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        sunSetTime.setTimeInMillis(currentWeather.getTimeSunset() * 1000 + currentWeather.getTimeZoneSeconds() * 1000);
        sunSetTime.set(Calendar.DAY_OF_YEAR, forecastTime.get(Calendar.DAY_OF_YEAR));


        Calendar sunRiseTime = Calendar.getInstance();
        sunRiseTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        sunRiseTime.setTimeInMillis(currentWeather.getTimeSunrise() * 1000 + currentWeather.getTimeZoneSeconds() * 1000);
        sunRiseTime.set(Calendar.DAY_OF_YEAR, forecastTime.get(Calendar.DAY_OF_YEAR));

        //Log.d("devtag", position + " " + forecastTime.getTime());

        boolean isDay = forecastTime.after(sunRiseTime) && forecastTime.before(sunSetTime);
        //Log.d("devtag", sunRiseTime.getTime() + " " + sunSetTime.getTime() + " " + sunRiseTime.get(Calendar.HOUR_OF_DAY) + " " + forecastTime.get(Calendar.HOUR_OF_DAY) + " " + sunSetTime.get(Calendar.HOUR_OF_DAY) + " " + isDay);

        //boolean isDay = c.get(Calendar.HOUR_OF_DAY) >= 4 && c.get(Calendar.HOUR_OF_DAY) <= 20;

        int day = forecastTime.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                day = R.string.abbreviation_monday;
                break;
            case Calendar.TUESDAY:
                day = R.string.abbreviation_tuesday;
                break;
            case Calendar.WEDNESDAY:
                day = R.string.abbreviation_wednesday;
                break;
            case Calendar.THURSDAY:
                day = R.string.abbreviation_thursday;
                break;
            case Calendar.FRIDAY:
                day = R.string.abbreviation_friday;
                break;
            case Calendar.SATURDAY:
                day = R.string.abbreviation_saturday;
                break;
            case Calendar.SUNDAY:
                day = R.string.abbreviation_sunday;
                break;
            default:
                day = R.string.abbreviation_monday;
        }

        if (forecastTime.get(Calendar.HOUR_OF_DAY) > 0 && forecastTime.get(Calendar.HOUR_OF_DAY) <= 3) {
            // In first entry per weekday show weekday instead of time
            holder.time.setText(day);
        } else {
            //Time has to be the local time in the city!
            holder.time.setText(StringFormatUtils.formatTimeWithoutZone(courseOfDayList.get(position).getLocalForecastTime(context)));
        }

        setIcon(courseOfDayList.get(position).getWeatherID(), holder.weather, isDay);
        holder.humidity.setText(StringFormatUtils.formatInt(courseOfDayList.get(position).getHumidity(), "%rh"));
        holder.temperature.setText(StringFormatUtils.formatTemperature(context, courseOfDayList.get(position).getTemperature()));
        holder.wind_speed.setText(StringFormatUtils.formatWindSpeed(context, courseOfDayList.get(position).getWindSpeed()));
        holder.wind_direction.setText(StringFormatUtils.formatWindDir(context, courseOfDayList.get(position).getWindDirection()));

        if (courseOfDayList.get(position).getRainValue() == 0)
            holder.precipitation.setText("-");
        else
            holder.precipitation.setText(StringFormatUtils.formatDecimal(courseOfDayList.get(position).getRainValue(), "mm"));
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
        TextView wind_speed;
        TextView wind_direction;

        CourseOfDayViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.course_of_day_time);
            weather = itemView.findViewById(R.id.course_of_day_weather);
            temperature = itemView.findViewById(R.id.course_of_day_temperature);
            humidity = itemView.findViewById(R.id.course_of_day_humidity);
            precipitation = itemView.findViewById(R.id.course_of_day_precipitation);
            wind_speed = itemView.findViewById(R.id.course_of_day_wind_speed);
            wind_direction = itemView.findViewById(R.id.course_of_day_wind_direction);

        }
    }

    public void setIcon(int value, ImageView imageView, boolean isDay) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value, isDay));
    }
}

