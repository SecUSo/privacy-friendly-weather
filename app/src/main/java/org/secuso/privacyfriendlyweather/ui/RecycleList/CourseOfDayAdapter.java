package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.Forecast;

import java.util.List;

//**
// * Created by yonjuni on 02.01.17.
// * Adapter for the horizontal listView for course of the day.
// */import java.util.List;

public class CourseOfDayAdapter extends RecyclerView.Adapter<CourseOfDayAdapter.CourseOfDayViewHolder> {

    //TODO Add datatype to list
    private List<Forecast> courseOfDayList;

    public CourseOfDayAdapter(List<Forecast> courseOfDayList) {
        this.courseOfDayList = courseOfDayList;
        Forecast forecast = new Forecast(1, 1, 12345678910L, null, 15, 43, 86, 1001);
        for (int i=0; i<8; i++){
            courseOfDayList.add(forecast);
        }
    }

    @Override
    public CourseOfDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_course_of_day, parent, false);
        return new CourseOfDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseOfDayViewHolder holder, int position) {

        //TODO set the texts an choose ImageView
        //Time has to be the local time in the city!
//        holder.time.setText();
        holder.temperature.setText(Float.toString(courseOfDayList.get(position).getTemperature()));
        holder.humidity.setText(Float.toString(courseOfDayList.get(position).getHumidity()));

    }

    @Override
    public int getItemCount() {
        return courseOfDayList.size();
    }

    public class CourseOfDayViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView weather;
        TextView temperature;
        TextView humidity;

        public CourseOfDayViewHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.course_of_day_time);
            weather = (ImageView) itemView.findViewById(R.id.course_of_day_weather);
            temperature = (TextView) itemView.findViewById(R.id.course_of_day_temperature);
            humidity = (TextView) itemView.findViewById(R.id.course_of_day_humidity);

        }
    }
}

