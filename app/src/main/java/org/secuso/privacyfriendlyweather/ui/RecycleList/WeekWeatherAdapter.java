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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by yonjuni on 02.01.17.
 */

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.WeekForecastViewHolder> {

    private Context context;
    private List<Forecast> forecastList;

    WeekWeatherAdapter(List<Forecast> forecastList, Context context) {
        this.context = context;
        this.forecastList = forecastList;
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_week_forecast, parent, false);
        return new WeekForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        Forecast f = forecastList.get(position);

        setIcon(f.getWeatherID(), holder.weather);
        holder.humidity.setText(StringFormatUtils.formatInt(f.getHumidity(), "%"));

        Calendar c = new GregorianCalendar();
        c.setTime(f.getLocalForecastTime(context));
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
        holder.day.setText(day);
        holder.temperature.setText(StringFormatUtils.formatTemperature(context, f.getTemperature()));
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    class WeekForecastViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        ImageView weather;
        TextView temperature;
        TextView humidity;

        WeekForecastViewHolder(View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.week_forecast_day);
            weather = itemView.findViewById(R.id.week_forecast_weather);
            temperature = itemView.findViewById(R.id.week_forecast_temperature);
            humidity = itemView.findViewById(R.id.week_forecast_humidity);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setIcon(int value, ImageView imageView) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value));
    }

}
