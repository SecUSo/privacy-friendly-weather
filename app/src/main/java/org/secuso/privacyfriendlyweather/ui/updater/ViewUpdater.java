package org.secuso.privacyfriendlyweather.ui.updater;

import org.secuso.privacyfriendlyweather.database.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.Forecast;
import org.secuso.privacyfriendlyweather.widget.WeatherWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */

public class ViewUpdater {
    private static List<IUpdateableCityUI> subscribers = new ArrayList<>();

    public static void addSubsriber(IUpdateableCityUI sub) {
        if(!subscribers.contains(sub)) {
            subscribers.add(sub);
        }
    }

    public static void removeSubsriber(IUpdateableCityUI sub) {
        if(subscribers.contains(sub)) {
            subscribers.remove(sub);
        }
    }

    public static void updateCurrentWeatherData(CurrentWeatherData data) {
        for(IUpdateableCityUI sub : subscribers) {
            sub.updateCurrentWeather(data);
        }
    }

    public static void updateForecasts(List<Forecast> forecasts) {
        for(IUpdateableCityUI sub : subscribers) {
            sub.updateForecasts(forecasts);
        }
    }
}
