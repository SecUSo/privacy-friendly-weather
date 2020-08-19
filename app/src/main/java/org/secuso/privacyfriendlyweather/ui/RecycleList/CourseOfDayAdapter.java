package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
        boolean isDay;

        // Show day icons between 4am and 8pm.
        // Would be better to use actual sunset and sunrise time, but did not know how to do this, These are not available in forecast database.
        Calendar c = new GregorianCalendar();
        c.setTime(courseOfDayList.get(position).getLocalForecastTime(context));
        if (c.get(Calendar.HOUR_OF_DAY) >= 4  && c.get(Calendar.HOUR_OF_DAY) <= 20  ) {
            isDay = true;
        } else {isDay = false;}

        int day = c.get(Calendar.DAY_OF_WEEK);

        switch(day) {
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

        if (c.get(Calendar.HOUR_OF_DAY) > 0  && c.get(Calendar.HOUR_OF_DAY) <= 3  ) {
            // In first entry per weekday show weekday instead of time
            holder.time.setText(day);
        } else {
            //Time has to be the local time in the city!
            holder.time.setText(StringFormatUtils.formatTime(context, courseOfDayList.get(position).getLocalForecastTime(context)));
        }


        //Time has to be the local time in the city!

        setIcon(courseOfDayList.get(position).getWeatherID(), holder.weather, isDay);
        holder.humidity.setText(StringFormatUtils.formatInt(courseOfDayList.get(position).getHumidity(), "%"));
        holder.temperature.setText(StringFormatUtils.formatTemperature(context, courseOfDayList.get(position).getTemperature()));

        if(courseOfDayList.get(position).getRainValue()==0)
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

        CourseOfDayViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.course_of_day_time);
            weather = itemView.findViewById(R.id.course_of_day_weather);
            temperature = itemView.findViewById(R.id.course_of_day_temperature);
            humidity = itemView.findViewById(R.id.course_of_day_humidity);
            precipitation = itemView.findViewById(R.id.course_of_day_precipitation);

        }
    }

    public void setIcon(int value, ImageView imageView, boolean isDay) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value, isDay));
    }
}

