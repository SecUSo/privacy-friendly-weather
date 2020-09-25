package org.secuso.privacyfriendlyweather.ui.RecycleList;

import android.content.Context;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        DecimalFormat decimalFormat = new DecimalFormat("#.0");


        setIcon((int) dayValues[9], holder.weather);
        holder.humidity.setText(String.format("%s | %s%%", StringFormatUtils.formatInt(dayValues[2]), StringFormatUtils.formatInt(dayValues[3])));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.setTimeInMillis((long) dayValues[8]);
        int day = c.get(Calendar.DAY_OF_WEEK);

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
        holder.day.setText(day);
        holder.temperature.setText(String.format("%s | %s%s", decimalFormat.format(prefManager.convertTemperatureFromCelsius(dayValues[0])),
                decimalFormat.format(prefManager.convertTemperatureFromCelsius(dayValues[1])), prefManager.getWeatherUnit()));
    }

    @Override
    public int getItemCount() {
        return forecastData.length - 1;
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

    public void setIcon(int value, ImageView imageView) {
        imageView.setImageResource(UiResourceProvider.getIconResourceForWeatherCategory(value, true));
    }

}
