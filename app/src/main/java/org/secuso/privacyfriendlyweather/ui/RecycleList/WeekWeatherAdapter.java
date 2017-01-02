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

/**
 * Created by yonjuni on 02.01.17.
 */

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.WeekForecastViewHolder> {

    List<Forecast> forecastList;

    public WeekWeatherAdapter(List<Forecast> forecastList) {
        this.forecastList = forecastList;
        //TODO Update to DB data
        Forecast forecast = new Forecast(1, 1, 12345678910L, null, 15, 43, 86, 1001);
        for (int i=0; i<5; i++){
            forecastList.add(forecast);
        }
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_week_forecast, parent, false);
        return new WeekForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        // TODO holder.weather.setBackground(); setday...
        holder.temperature.setText(Float.toString(forecastList.get(position).getTemperature()));
        holder.humidity.setText(Float.toString(forecastList.get(position).getHumidity()));
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class WeekForecastViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        ImageView weather;
        TextView temperature;
        TextView humidity;

        public WeekForecastViewHolder(View itemView) {
            super(itemView);

            day = (TextView) itemView.findViewById(R.id.week_forecast_day);
            weather = (ImageView) itemView.findViewById(R.id.week_forecast_weather);
            temperature = (TextView) itemView.findViewById(R.id.week_forecast_temperature);
            humidity = (TextView) itemView.findViewById(R.id.week_forecast_humidity);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
