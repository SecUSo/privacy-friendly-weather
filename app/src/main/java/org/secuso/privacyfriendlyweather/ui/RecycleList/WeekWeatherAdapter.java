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
        Forecast forecast = new Forecast(1, 1, 12345678910L, null, 20, 10, 90, 1001);
        Forecast forecast2 = new Forecast(1, 1, 12345678910L, null, 10, 26, 86, 1001);
        Forecast forecast3 = new Forecast(1, 1, 12345678910L, null, 30, 3, 90, 1001);
        Forecast forecast4 = new Forecast(1, 1, 12345678910L, null, 40, 33, 86, 1001);
        Forecast forecast5 = new Forecast(1, 1, 12345678910L, null, 50, 43, 90, 1001);
        forecastList.add(forecast);
        forecastList.add(forecast2);
        forecastList.add(forecast3);
        forecastList.add(forecast4);
        forecastList.add(forecast5);
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_week_forecast, parent, false);
        return new WeekForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        // TODO holder.weather.setBackground(); setday...
        setIcon(forecastList.get(position).getWeatherID(), holder.weather);
        holder.temperature.setText(Float.toString(forecastList.get(position).getTemperature()));
        holder.humidity.setText(String.format("%s %%", forecastList.get(position).getHumidity()));
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

    public void setIcon(int value, ImageView imageView) {
        switch (value) {
            case 10:
                imageView.setImageResource(R.mipmap.weather_icon_sunny);
                break;
            case 20:
                imageView.setImageResource(R.mipmap.weather_icon_sunny_with_clouds);
                break;
            case 30:
                imageView.setImageResource(R.mipmap.weather_icon_cloudy_scattered);
                break;
            case 40:
                imageView.setImageResource(R.mipmap.weather_icon_cloudy_broken);
                break;
            case 50:
                imageView.setImageResource(R.mipmap.weather_icon_foggy);
                break;
            case 60:
                imageView.setImageResource(R.mipmap.weather_icon_rain);
                break;
            case 70:
                imageView.setImageResource(R.mipmap.weather_icon_rain);
                break;
            case 80:
                imageView.setImageResource(R.mipmap.weather_icon_snow);
                break;
            case 90:
                imageView.setImageResource(R.mipmap.weather_icon_thunderstorm);
                break;
            default:
                imageView.setImageResource(R.mipmap.weather_icon_sunny_with_clouds);
                break;
        }
    }

}
