package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.Help.StringFormatUtils;
import org.secuso.privacyfriendlyweather.ui.UiResourceProvider;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by yonjuni on 02.01.17.
 */

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.WeekForecastViewHolder> {

    private Context context;
    private float[][] forecastData;

    WeekWeatherAdapter(float[][] forecastData, Context context) {
        this.context = context;
        this.forecastData = forecastData;
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_week_forecast, parent, false);
        return new WeekForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        float[] dayValues = forecastData[position];
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        DecimalFormat decimalFormat = new DecimalFormat("0.0");


        setIcon((int) dayValues[9], holder.weather);
        holder.humidity.setText(StringFormatUtils.formatInt(dayValues[2], "%rh"));
        holder.precipitation.setText(StringFormatUtils.formatDecimal(dayValues[4], "mm"));
        holder.rain_probability.setText(StringFormatUtils.formatInt(dayValues[11], "%\uD83D\uDCA7"));
        holder.uv_index.setText(String.format("UV %s", StringFormatUtils.formatInt(Math.round(dayValues[7]))));
        holder.wind_speed.setText(prefManager.convertToCurrentSpeedUnit(dayValues[5]));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.setTimeInMillis((long) dayValues[8]);
        int day = c.get(Calendar.DAY_OF_WEEK);

        holder.day.setText(StringFormatUtils.getDay(day));
        holder.temperature_max.setText(String.format("%s\u200a%s", decimalFormat.format(prefManager.convertTemperatureFromCelsius(dayValues[0])), prefManager.getWeatherUnit()));
        holder.temperature_min.setText(String.format("%s\u200a%s", decimalFormat.format(prefManager.convertTemperatureFromCelsius(dayValues[1])), prefManager.getWeatherUnit()));
    }

    @Override
    public int getItemCount() {
        return forecastData.length - 1;
    }

    class WeekForecastViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        ImageView weather;
        TextView temperature_max;
        TextView temperature_min;
        TextView humidity;
        TextView wind_speed;
        TextView precipitation;
        TextView rain_probability;
        TextView uv_index;

        WeekForecastViewHolder(View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.week_forecast_day);
            weather = itemView.findViewById(R.id.week_forecast_weather);
            temperature_max = itemView.findViewById(R.id.week_forecast_temperature_max);
            temperature_max.setTextColor(Color.rgb(179, 0, 0));
            temperature_min = itemView.findViewById(R.id.week_forecast_temperature_min);
            temperature_min.setTextColor(Color.rgb(0, 0, 200));
            humidity = itemView.findViewById(R.id.week_forecast_humidity);
            wind_speed = itemView.findViewById(R.id.week_forecast_wind_speed);
            precipitation = itemView.findViewById(R.id.week_forecast_precipitation);
            rain_probability = itemView.findViewById(R.id.week_forecast_rain_probability);
            uv_index = itemView.findViewById(R.id.week_forecast_uv_index);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setIcon(int value, ImageView imageView) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value, true));
        imageView.setBackgroundResource(R.drawable.backgroundcircle);
    }

}
