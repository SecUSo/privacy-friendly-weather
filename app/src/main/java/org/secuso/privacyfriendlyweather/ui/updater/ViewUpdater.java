package org.secuso.privacyfriendlyweather.ui.updater;

import org.secuso.privacyfriendlyweather.database.data.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.database.data.Forecast;
import org.secuso.privacyfriendlyweather.database.data.WeekForecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */

public class ViewUpdater {
    private static List<IUpdateableCityUI> subscribers = new ArrayList<>();

    public static void addSubscriber(IUpdateableCityUI sub) {
        if (!subscribers.contains(sub)) {
            subscribers.add(sub);
        }
    }

    public static void removeSubscriber(IUpdateableCityUI sub) {
        subscribers.remove(sub);
    }

    public static void updateCurrentWeatherData(CurrentWeatherData data) {
        ArrayList<IUpdateableCityUI> subcopy = new ArrayList<>(subscribers);
        for (IUpdateableCityUI sub : subcopy) {
            sub.processNewWeatherData(data);
        }
    }

    public static void updateWeekForecasts(List<WeekForecast> forecasts) {
        for (IUpdateableCityUI sub : subscribers) {
            sub.updateWeekForecasts(forecasts);
        }
    }

    public static void updateForecasts(List<Forecast> forecasts) {
        for (IUpdateableCityUI sub : subscribers) {
            sub.updateForecasts(forecasts);
        }
    }

    public static void abortUpdate() {
        for (IUpdateableCityUI sub : subscribers) {
            sub.abortUpdate();
        }
    }
}
