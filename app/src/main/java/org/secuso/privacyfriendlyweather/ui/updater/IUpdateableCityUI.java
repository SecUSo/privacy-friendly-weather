package org.secuso.privacyfriendlyweather.ui.updater;

import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.database.WeekForecast;

import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */
public interface IUpdateableCityUI {
    void processNewWeatherData(CurrentWeatherData data);

    void updateForecasts(List<Forecast> forecasts);

    void updateWeekForecasts(List<WeekForecast> forecasts);
}
