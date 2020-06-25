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

        //Time has to be the local time in the city!
        holder.time.setText(StringFormatUtils.formatTime(context, courseOfDayList.get(position).getLocalForecastTime(context)));
        setIcon(courseOfDayList.get(position).getWeatherID(), holder.weather);
        holder.humidity.setText(StringFormatUtils.formatInt(courseOfDayList.get(position).getHumidity(), "%"));
        holder.temperature.setText(StringFormatUtils.formatTemperature(context, courseOfDayList.get(position).getTemperature()));

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

        CourseOfDayViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.course_of_day_time);
            weather = itemView.findViewById(R.id.course_of_day_weather);
            temperature = itemView.findViewById(R.id.course_of_day_temperature);
            humidity = itemView.findViewById(R.id.course_of_day_humidity);

        }
    }

    private void setIcon(int value, ImageView imageView) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value));

    }
}

