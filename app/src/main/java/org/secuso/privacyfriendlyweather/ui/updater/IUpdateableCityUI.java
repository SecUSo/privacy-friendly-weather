package org.secuso.privacyfriendlyweather.ui.updater;

import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;

import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */
public interface IUpdateableCityUI {
    void updateCurrentWeather(CurrentWeatherData data);
    void updateForecasts(List<Forecast> forecasts);
}
