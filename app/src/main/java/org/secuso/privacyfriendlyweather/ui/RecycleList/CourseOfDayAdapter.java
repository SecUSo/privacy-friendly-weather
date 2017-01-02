package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;

/**
 * Created by yonjuni on 02.01.17.
 * Adapter for the horizontal listView for course of the day.
 */

public class CourseOfDayAdapter extends RecyclerView.Adapter<CourseOfDayAdapter.CourseOfDayViewHolder> {

    //TODO Add datatype to list
    //private List<Forecast> courseOfDayList;

//    public CourseOfDayAdapter(List<Forecast> courseOfDayList) {
//        this.courseOfDayList = courseOfDayList;
//    }

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
//        holder.temperature.setText();
//        holder.humidity.setText();

    }

    @Override
    public int getItemCount() {
        //return courseOfDayList.size();
        return 0;
    }

    public class CourseOfDayViewHolder extends RecyclerView.ViewHolder{
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
