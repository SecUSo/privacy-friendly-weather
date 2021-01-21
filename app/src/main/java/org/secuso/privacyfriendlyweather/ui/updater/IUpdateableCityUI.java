package org.secuso.privacyfriendlyweather.ui.updater;

import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;

import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */
public interface IUpdateableCityUI {
    void processNewWeatherData(CurrentWeatherData data);

    void updateForecasts(List<Forecast> forecasts);

    void updateWeekForecasts(List<WeekForecast> forecasts);

    void abortUpdate();
}
