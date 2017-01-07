package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yonjuni on 02.01.17.
 */

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.WeekForecastViewHolder> {

    Context context;

    List<Forecast> forecastList;

    public WeekWeatherAdapter(List<Forecast> forecastList, Context context) {
        this.context = context;
        this.forecastList = forecastList;
        //TODO Update to DB data
        if(forecastList.size() == 0) {
            Forecast forecast = new Forecast(1, 1, System.currentTimeMillis(), null, 20, 10, 90, 1001);
            Forecast forecast2 = new Forecast(1, 1, System.currentTimeMillis(), null, 10, 26, 86, 1001);
            Forecast forecast3 = new Forecast(1, 1, System.currentTimeMillis(), null, 30, 3, 90, 1001);
            Forecast forecast4 = new Forecast(1, 1, System.currentTimeMillis(), null, 40, 33, 86, 1001);
            Forecast forecast5 = new Forecast(1, 1, System.currentTimeMillis(), null, 50, 43, 90, 1001);
            forecastList.add(forecast);
            forecastList.add(forecast2);
            forecastList.add(forecast3);
            forecastList.add(forecast4);
            forecastList.add(forecast5);
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
        setIcon(forecastList.get(position).getWeatherID(), holder.weather);
        holder.humidity.setText(String.format("%s %%", forecastList.get(position).getHumidity()));

        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        // Format the values to display
        String heading = String.format(
                "%s%s",
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(forecastList.get(position).getTemperature())),
                prefManager.getWeatherUnit()
        );

        holder.temperature.setText(heading);
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
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value));


    }

}
